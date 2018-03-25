package uk.gov.ons.sbr.models.localunit

import org.scalatest.{ FreeSpec, Matchers }
import play.api.libs.json.Json

class LocalUnitSpec extends FreeSpec with Matchers {

  private trait Fixture {
    def expectedJsonStrOf(localUnit: LocalUnit): String =
      s"""
         |{
         | "lurn":"${localUnit.lurn}",
         | "luref":"${localUnit.luref}",
         | "name":"${localUnit.name}",
         | "tradingStyle":"${localUnit.tradingStyle}",
         | "sic07":"${localUnit.sic07}",
         | "employees":${localUnit.employees},
         | "enterprise": {
         |   "ern":"${localUnit.enterprise.ern}",
         |   "entref":"${localUnit.enterprise.entref}"
         | },
         | "address": {
         |   "line1":"${localUnit.address.line1}",
         |   "line2":"${localUnit.address.line2}",
         |   "line3":"${localUnit.address.line3}",
         |   "line4":"${localUnit.address.line4}",
         |   "line5":"${localUnit.address.line5}",
         |   "postcode":"${localUnit.address.postcode}"
         | }
         |}
       """.stripMargin
  }

  "A LocalUnit" - {
    "can be represented as JSON" in new Fixture {
      val aLocalUnit = LocalUnit(lurn = "900000011", luref = "luref-value", name = "COMPANY X",
        tradingStyle = "tradingStyle-value", sic07 = "sic07-value", employees = 42,
        enterprise = EnterpriseLink(ern = "1000000012", entref = "entref-value"),
        address = Address(line1 = "line1-value", line2 = "line2-value", line3 = "line3-value", line4 = "line4-value",
          line5 = "line5-value", postcode = "postcode-value"))

      Json.toJson(aLocalUnit) shouldBe Json.parse(expectedJsonStrOf(aLocalUnit))
    }
  }
}
