import fixture.{ ServerAcceptanceSpec, WithWireMockHBase }
import org.scalatest.OptionValues
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.mvc.Http.MimeTypes.JSON
import uk.gov.ons.sbr.models.localunit.{ Address, EnterpriseLink, LocalUnit }

class LocalUnitAcceptanceSpec extends ServerAcceptanceSpec with WithWireMockHBase with OptionValues {
  private val ERN = "1000000012"
  private val LURN = "900000011"
  private val EarliestPeriod = "201802"
  private val LatestPeriod = "201803"

  // TODO move all this as we develop an implementation ...
  implicit val addressReads = Json.reads[Address]
  implicit val enterpriseLinkReads = Json.reads[EnterpriseLink]
  implicit val localUnitReads = Json.reads[LocalUnit]
  // end TO MOVE

  private val LocalUnitMultiplePeriodsHBaseResponseBody =
    s"""{"Row": ${
      List(
        aRowWith(
          key = s"${ERN.reverse}~$EarliestPeriod~$LURN",
          columns = aColumnWith(name = "lurn", value = LURN),
          aColumnWith(name = "luref", value = "some-luref"),
          aColumnWith(name = "ern", value = ERN),
          aColumnWith(name = "entref", value = "some-entref"),
          aColumnWith(name = "name", value = s"name-$EarliestPeriod"),
          aColumnWith(name = "tradingstyle", value = s"tradingstyle-$EarliestPeriod"),
          aColumnWith(name = "address1", value = s"address1-$EarliestPeriod"),
          aColumnWith(name = "address2", value = s"address2-$EarliestPeriod"),
          aColumnWith(name = "address3", value = s"address3-$EarliestPeriod"),
          aColumnWith(name = "address4", value = s"address4-$EarliestPeriod"),
          aColumnWith(name = "address5", value = s"address5-$EarliestPeriod"),
          aColumnWith(name = "postcode", value = s"postcode-$EarliestPeriod"),
          aColumnWith(name = "sic07", value = s"sic07-$EarliestPeriod"),
          aColumnWith(name = "employees", value = "99")
        ),
        aRowWith(
          key = s"${ERN.reverse}~$LatestPeriod~$LURN",
          columns = aColumnWith(name = "lurn", value = LURN),
          aColumnWith(name = "luref", value = "some-luref"),
          aColumnWith(name = "ern", value = ERN),
          aColumnWith(name = "entref", value = "some-entref"),
          aColumnWith(name = "name", value = s"name-$LatestPeriod"),
          aColumnWith(name = "tradingstyle", value = s"tradingstyle-$LatestPeriod"),
          aColumnWith(name = "address1", value = s"address1-$LatestPeriod"),
          aColumnWith(name = "address2", value = s"address2-$LatestPeriod"),
          aColumnWith(name = "address3", value = s"address3-$LatestPeriod"),
          aColumnWith(name = "address4", value = s"address4-$LatestPeriod"),
          aColumnWith(name = "address5", value = s"address5-$LatestPeriod"),
          aColumnWith(name = "postcode", value = s"postcode-$LatestPeriod"),
          aColumnWith(name = "sic07", value = s"sic07-$LatestPeriod"),
          aColumnWith(name = "employees", value = "100")
        )
      ).mkString("[", ",", "]")
    }
    }"""

  feature("retrieve the latest local unit") {
    // TODO revert to "ignore" when incomplete
    scenario("for an Enterprise reference (ERN) and Local Unit reference (LURN) when the local unit exists over many periods") { wsClient =>
      Given(s"a local unit exists for multiple periods in HBase with a Local Unit reference of $LURN and Enterprise reference of $ERN")
      stubHbaseFor(aLocalUnitRequest(withErn = ERN, withLurn = LURN).willReturn(
        anOkResponse().withBody(LocalUnitMultiplePeriodsHBaseResponseBody)
      ))

      When(s"the latest unit with Local Unit reference $LURN and Enterprise reference $ERN is requested")
      val response = await(wsClient.url(s"/v1/enterprises/$ERN/localunits/$LURN?max=1").get())

      Then(s"the details of the local unit with Local Unit reference $LURN for the latest period are returned")
      response.status shouldBe OK
      response.header("Content-Type").value shouldBe JSON
      response.json.as[LocalUnit] shouldBe
        LocalUnit(lurn = LURN, luref = "some-luref", name = s"name-$LatestPeriod",
          tradingStyle = s"tradingstyle-$LatestPeriod", sic07 = s"sic07-$LatestPeriod", employees = 100,
          enterprise = EnterpriseLink(ern = ERN, entref = "some-entref"),
          address = Address(line1 = s"address1-$LatestPeriod", line2 = s"address2-$LatestPeriod",
            line3 = s"address3-$LatestPeriod", line4 = s"address4-$LatestPeriod", line5 = s"address5-$LatestPeriod",
            postcode = s"postcode-$LatestPeriod"))
    }
  }
}
