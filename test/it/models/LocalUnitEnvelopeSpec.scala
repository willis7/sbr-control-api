package it.models

import java.time.Month.FEBRUARY
import java.time.YearMonth
import java.time.format.DateTimeFormatter.ofPattern

import org.scalatest.{FreeSpec, Matchers}
import play.api.libs.json.Json
import uk.gov.ons.sbr.models.Envelope
import uk.gov.ons.sbr.models.localunit.{Address, EnterpriseLink, LocalUnit}

class LocalUnitEnvelopeSpec extends FreeSpec with Matchers {

  private trait Fixture {
    def expectedJsonStrOf(envelope: Envelope[LocalUnit]): String = {
      s"""{
         |  "period":"${envelope.period.format(ofPattern("uuuuMM"))}",
         |  "localUnit": {
         |    "lurn":"${envelope.a.lurn}",
         |    "luref":"${envelope.a.luref}",
         |    "name":"${envelope.a.name}",
         |    "tradingStyle":"${envelope.a.tradingStyle}",
         |    "sic07":"${envelope.a.sic07}",
         |    "employees":${envelope.a.employees},
         |    "enterprise": {
         |      "ern":"${envelope.a.enterprise.ern}",
         |      "entref":"${envelope.a.enterprise.entref}"
         |    },
         |    "address": {
         |      "line1":"${envelope.a.address.line1}",
         |      "line2":"${envelope.a.address.line2}",
         |      "line3":"${envelope.a.address.line3}",
         |      "line4":"${envelope.a.address.line4}",
         |      "line5":"${envelope.a.address.line5}",
         |      "postcode":"${envelope.a.address.postcode}"
         |    }
         |  }
         |}""".stripMargin
    }
  }

  "A LocalUnit Envelope" - {
    "can be represented as JSON" in new Fixture {
      val aLocalUnit = LocalUnit(lurn = "900000011", luref = "luref-value", name = "COMPANY X",
        tradingStyle = "tradingStyle-value", sic07 = "sic07-value", employees = 42,
        enterprise = EnterpriseLink(ern = "1000000012", entref = "entref-value"),
        address = Address(line1 = "line1-value", line2 = "line2-value", line3 = "line3-value",
          line4 = "line4-value", line5 = "line5-value", postcode = "postcode-value"))
      val envelope = Envelope(period = YearMonth.of(2018, FEBRUARY), aLocalUnit)

      Json.toJson(envelope)(Envelope.makeWrites("localUnit")) shouldBe Json.parse(expectedJsonStrOf(envelope))
    }
  }
}
