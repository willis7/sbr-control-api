package uk.gov.ons.sbr.models.localunit

import java.time.Month.MARCH
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import org.scalatest.{ FreeSpec, Matchers }
import play.api.libs.json._
import uk.gov.ons.sbr.models.Envelope

class EnvelopeSpec extends FreeSpec with Matchers {

  /*
   * Note that this fixture defines a custom Writes for LocalUnit.
   * This is to prevent a change to the real format from rippling through multiple specs.
   */
  private trait Fixture {
    implicit val localUnitWrites = new Writes[LocalUnit] {
      override def writes(lu: LocalUnit): JsValue =
        JsObject(Map(
          "lurn" -> JsString(lu.lurn),
          "postcode" -> JsString(lu.address.postcode)
        ))
    }

    implicit val envelopeWrites = Envelope.makeWrites[LocalUnit]("localUnit")

    def expectedJsonStrOf(envelope: Envelope[LocalUnit]): String =
      s"""
         |{
         |  "period": "${envelope.period.format(DateTimeFormatter.ofPattern("uuuuMM"))}",
         |  "localUnit": {
         |    "lurn": "${envelope.a.lurn}",
         |    "postcode": "${envelope.a.address.postcode}"
         |  }
         |}""".stripMargin
  }

  "An Envelope" - {
    "can be represented in JSON containing a LocalUnit" in new Fixture {
      val aLocalUnit = LocalUnit(lurn = "900000011", luref = "luref-value", name = "COMPANY X",
        tradingStyle = "tradingStyle-value", sic07 = "sic07-value", employees = 42,
        enterprise = EnterpriseLink(ern = "1000000012", entref = "entref-value"),
        address = Address(line1 = "line1-value", line2 = "line2-value", line3 = "line3-value", line4 = "line4-value",
          line5 = "line5-value", postcode = "postcode-value"))
      val envelope = Envelope(YearMonth.of(2018, MARCH), aLocalUnit)

      Json.toJson(envelope) shouldBe Json.parse(expectedJsonStrOf(envelope))
    }
  }
}
