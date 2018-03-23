package fixture

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.{ MappingBuilder, ResponseDefinitionBuilder, WireMock }
import org.scalatest.Suite
import play.api.http.Status.OK
import play.mvc.Http.MimeTypes.JSON

trait WithWireMockHBase extends WithWireMock { this: Suite =>
  override val wireMockPort = 8075

  def anEnterpriseRequest(withId: String): MappingBuilder =
    getHbaseJson(s"/sbr_control_db:enterprise/${withId.reverse}~*/d")

  def aLocalUnitRequest(withErn: String, withLurn: String): MappingBuilder =
    getHbaseJson(s"/sbr_control_db:local_unit/${withErn.reverse}~*~$withLurn/d")

  private def getHbaseJson(url: String): MappingBuilder =
    get(urlEqualTo(url)).withHeader("Accept", matching(JSON))

  def anOkResponse(): ResponseDefinitionBuilder =
    aResponse().withStatus(OK)

  def hbaseEncode(value: String): String =
    Base64.getEncoder.encodeToString(value.getBytes(UTF_8.name()))

  val stubHbaseFor: MappingBuilder => Unit =
    WireMock.stubFor

  def aRowWith(key: String, columns: String*): String =
    s"""|{"key": "${hbaseEncode(key)}",
        | "Cell": ${columns.mkString("[", ",", "]")}
        |}""".stripMargin

  def aColumnWith(name: String, value: String, timestamp: Long = 1520333985745L): String =
    s"""|{"column": "${hbaseEncode(name)}",
        | "timestamp": $timestamp,
        | "$$": "${hbaseEncode(value)}"
        |}""".stripMargin
}
