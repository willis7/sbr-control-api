package repository.hbase

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject
import play.api.http.HeaderNames.ACCEPT
import play.api.http.MimeTypes.JSON
import play.api.http.Status.{ NOT_FOUND, OK, UNAUTHORIZED }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{ JsValue, Reads }
import play.api.libs.ws.WSAuthScheme.BASIC
import play.api.libs.ws.{ WSClient, WSRequest, WSResponse }
import repository.RestRepository
import repository.RestRepository.{ ErrorMessage, Row }
import utils.TrySupport

import scala.concurrent.duration._
import scala.concurrent.{ Future, TimeoutException }
import scala.util.Try

case class HBaseRestRepositoryConfig(protocolWithHostname: String, port: String,
  namespace: String, username: String, password: String, timeout: Long)

class HBaseRestRepository @Inject() (
    config: HBaseRestRepositoryConfig,
    wsClient: WSClient,
    responseReaderMaker: HBaseResponseReaderMaker
) extends RestRepository with LazyLogging {

  /*
   * Always return a successful Future, materialising a failure in the result value (on the left).
   * This simplifies client interaction.
   */
  override def findRow(table: String, rowKey: String, columnGroup: String): Future[Either[ErrorMessage, Option[Row]]] = {
    val withRowReader = responseReaderMaker.forColumnGroup(columnGroup)
    val url = HBase.rowKeyUrl(config.protocolWithHostname, config.port, config.namespace, table, rowKey, columnGroup)
    logger.info(s"Requesting [$url] from HBase REST.")
    requestFor(url).get().map {
      (fromResponseToErrorOrJson _).andThen(convertToErrorOrRows(withRowReader)).andThen(verifyAtMostOneRow)
    }.recover(withTranslationOfFailureToError)
  }

  private def requestFor(url: String): WSRequest =
    wsClient.
      url(url).
      withHeaders(ACCEPT -> JSON).
      withAuth(config.username, config.password, scheme = BASIC).
      withRequestTimeout(config.timeout.milliseconds)

  /*
   * Note that official environments running Cloudera will receive an OK result containing an "empty row" on Not Found.
   * Developers using HBase directly in a local environment will more than likely receive a 404.
   */
  private def fromResponseToErrorOrJson(response: WSResponse): Either[ErrorMessage, Option[JsValue]] = {
    logger.info(s"HBase response has status ${describeStatus(response)}")
    response.status match {
      case OK => bodyAsJson(response)
      case NOT_FOUND => Right(None)
      case UNAUTHORIZED => Left(describeStatus(response) + " - check HBase REST configuration")
      case _ => Left(describeStatus(response))
    }
  }

  private def bodyAsJson(response: WSResponse): Either[ErrorMessage, Option[JsValue]] =
    TrySupport.fold(Try(response.json))(
      err => Left(s"Unable to create JsValue from HBase response [${err.getMessage}]"),
      json => Right(json)
    ).right.map(Some(_))

  private def describeStatus(response: WSResponse): String =
    s"${response.statusText} (${response.status})"

  private def convertToErrorOrRows(withReader: Reads[Seq[Row]])(errorOrJson: Either[ErrorMessage, Option[JsValue]]): Either[ErrorMessage, Seq[Row]] =
    errorOrJson.right.flatMap { optJson =>
      logger.debug(s"HBase REST response JSON is [$optJson]")
      optJson.fold[Either[ErrorMessage, Seq[Row]]](Right(Seq.empty)) { json =>
        parseJson(withReader)(json)
      }
    }

  private def parseJson(readsRows: Reads[Seq[Row]])(json: JsValue): Either[ErrorMessage, Seq[Row]] = {
    val eitherErrorOrRows = readsRows.reads(json).asEither
    logger.debug(s"HBase REST parsed response is [$eitherErrorOrRows]")
    eitherErrorOrRows.left.map(errors => s"Unable to parse HBase REST json response [$errors].")
  }

  private def verifyAtMostOneRow(errorOrRows: Either[ErrorMessage, Seq[Row]]): Either[ErrorMessage, Option[Row]] =
    errorOrRows.right.flatMap { rows =>
      if (rows.size > 1) {
        logger.warn(s"At most one result was expected for query but found [$rows].")
        Left(s"At most one result was expected but found [${rows.size}]")
      } else Right(rows.headOption)
    }

  private def withTranslationOfFailureToError = new PartialFunction[Throwable, Either[ErrorMessage, Option[Row]]] {
    override def isDefinedAt(cause: Throwable): Boolean = true

    override def apply(cause: Throwable): Either[ErrorMessage, Option[Row]] = {
      logger.info(s"Translating HBase request failure [$cause].")
      cause match {
        case t: TimeoutException => Left(s"Timeout.  ${t.getMessage}")
        case t: Throwable => Left(t.getMessage)
      }
    }
  }
}