package repository.hbase

import javax.inject.Inject
import repository.{LocalUnitRepository, RegexQuery, Row}
import uk.gov.ons.sbr.models.Envelope
import uk.gov.ons.sbr.models.localunit.{Address, EnterpriseLink, LocalUnit}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class HBaseRestLocalUnitRepository @Inject() (regexQuery: RegexQuery) extends LocalUnitRepository {

  override def retrieveLatest(ern: String, lurn: String)(implicit ec: ExecutionContext): Future[Option[Envelope[LocalUnit]]] =
    regexQuery.find("local_unit", s"${ern.reverse}~[0-9]{6}~$lurn").map { rows =>
      rows.map(toLocalUnitEnvelope).lastOption
    }

  private def toLocalUnitEnvelope(row: Row): Envelope[LocalUnit] =
    row.rowKey match {
      case LocalUnitRowKey(_, period, _) => Envelope(period, toLocalUnit(row.variables))
      case _ => throw new AssertionError("Unable to fully populate LocalUnit Envelope")
    }

  private def toLocalUnit(variables: Map[String, String]): LocalUnit = {
    val enterpriseLink = toEnterpriseLink(variables)
    val address = toAddress(variables)
    val optUnitLink = for {
      lurn <- variables.get("lurn")
      luref <- variables.get("luref")
      name <- variables.get("name")
      tradingStyle <- variables.get("tradingstyle")
      sic07 <- variables.get("sic07")
      employees <- variables.get("employees")
      employessInt <- Try(employees.toInt).toOption
    } yield LocalUnit(lurn, luref, name, tradingStyle, sic07, employessInt, enterpriseLink, address)
    optUnitLink.getOrElse(throw new AssertionError("Unable to fully populate LocalUnit"))
  }

  private def toEnterpriseLink(variables: Map[String, String]): EnterpriseLink = {
    val optEnterpriseLink = for {
      ern <- variables.get("ern")
      entref <- variables.get("entref")
    } yield EnterpriseLink(ern, entref)
    optEnterpriseLink.getOrElse(throw new AssertionError("Unable to fully populate EnterpriseLink"))
  }

  private def toAddress(variables: Map[String, String]): Address = {
    val optAddress = for {
      line1 <- variables.get("address1")
      line2 <- variables.get("address2")
      line3 <- variables.get("address3")
      line4 <- variables.get("address4")
      line5 <- variables.get("address5")
      postcode <- variables.get("postcode")
    } yield Address(line1, line2, line3, line4, line5, postcode)
    optAddress.getOrElse(throw new AssertionError("Unable to fully populate Address"))
  }
}
