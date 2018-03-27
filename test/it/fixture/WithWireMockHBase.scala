package fixture

import java.nio.charset.StandardCharsets.UTF_8
import java.util.{ Base64, UUID }

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.{ MappingBuilder, ResponseDefinitionBuilder, WireMock }
import org.scalatest.Suite
import play.api.http.Status.{ CREATED, OK }
import play.mvc.Http.MimeTypes.{ JSON, XML }

trait WithWireMockHBase extends WithWireMock { this: Suite =>
  override val wireMockPort = 8075
  private val LocalUnitTable = "sbr_control_db:local_unit"

  private val RegexRequestBodyTemplate =
    """|<Scanner batch="10">
       |  <filter>
       |  {
       |    "type": "RowFilter",
       |    "op": "EQUAL",
       |    "comparator": {
       |      "type": "RegexStringComparator",
       |      "value": "<query-value>"
       |    }
       |  }
       |  </filter>
       |</Scanner>""".stripMargin

  private def aRegexRequestBodyForQuery(regexQuery: String): String =
    RegexRequestBodyTemplate.replace("<query-value>", regexQuery)

  def anEnterpriseRequest(withId: String): MappingBuilder =
    getHbaseJson(s"/sbr_control_db:enterprise/${withId.reverse}~*/d")

  def aLocalUnitWithWildcardPeriodRequest(withErn: String, withLurn: String): MappingBuilder =
    stubRegexScannerQuery(LocalUnitTable, aRegexRequestBodyForQuery(s"${withErn.reverse}~[0-9]{6}~$withLurn"))

  private def stubRegexScannerQuery(table: String, xmlQueryBody: String): MappingBuilder = {
    val scannerId = UUID.randomUUID().toString
    val scannerUrl = s"http://localhost:$wireMockPort/$table/scanner/$scannerId"
    stubFor(put(urlEqualTo(s"$table/scanner")).
      // TODO implement semantic body matching (note that the xml body contains a json document !)
      // withRequestBody(...).
      withHeader("Content-Type", equalTo(XML)).
      willReturn(aResponse().
        withStatus(CREATED).
        withHeader("Location", scannerUrl)))

    // HBase returns OK rather than a RESTful NO_CONTENT
    stubFor(delete(urlEqualTo(scannerUrl)).willReturn(aResponse().withStatus(OK)))
    getHbaseJson(scannerUrl)
  }

  private def getHbaseJson(url: String): MappingBuilder =
    get(urlEqualTo(url)).withHeader("Accept", equalTo(JSON))

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
