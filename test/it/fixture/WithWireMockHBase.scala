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
    get(urlEqualTo(s"/sbr_control_db:enterprise/${withId.reverse}~*/d")).
      withHeader("Accept", matching(JSON))

  def anOkResponse(): ResponseDefinitionBuilder =
    aResponse().withStatus(OK)

  def hbaseEncode(value: String): String =
    Base64.getEncoder.encodeToString(value.getBytes(UTF_8.name()))

  val stubHbaseFor: MappingBuilder => Unit =
    WireMock.stubFor
}
