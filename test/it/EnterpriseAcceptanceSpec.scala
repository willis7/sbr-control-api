import fixture.{ ServerAcceptanceSpec, WithWireMockHBase }
import org.scalatest.OptionValues
import play.api.http.Status.OK
import play.mvc.Http.MimeTypes.JSON

class EnterpriseAcceptanceSpec extends ServerAcceptanceSpec with WithWireMockHBase with OptionValues {

  private val EnterpriseId = "12345"
  private val EnterpriseHBaseResponseBody =
    s"""|{"Row": [
        |  {"key": "${hbaseEncode("54321~201803")}",
        |   "Cell": [
        |      {"column": "${hbaseEncode("Column1")}",
        |       "timestamp": 1520333985745,
        |       "$$": "${hbaseEncode("Value1")}"
        |      },
        |      {"column": "${hbaseEncode("Column2")}",
        |       "timestamp": 1520333985745,
        |       "$$": "${hbaseEncode("Value2")}"
        |      }]
        |  }
        |]}""".stripMargin

  feature("lookup an enterprise") {
    scenario("when the enterprise exists") { wsClient =>
      Given(s"an enterprise exists in HBase with id $EnterpriseId")
      stubHbaseFor(anEnterpriseRequest(withId = EnterpriseId).willReturn(
        anOkResponse().withBody(EnterpriseHBaseResponseBody)
      ))

      When(s"the enterprise with id $EnterpriseId is requested")
      val response = await(wsClient.url(s"/v1/enterprises/$EnterpriseId").get())

      Then(s"the details of enterprise $EnterpriseId are returned")
      response.status shouldBe OK
      response.header("Content-Type").value shouldBe JSON
      /*
       * TODO assert on response body
       * {"id":"12345","period":"201803","vars":{"Column1":"Value1","Column2":"Value2"},"unitType":"ENT","childrenJson":[]}
       */
      println(s"enterprise response: $response.body")
    }
  }
}
