package uk.gov.ons.sbr.models.localunit

import org.scalatest.{ FreeSpec, Matchers }
import play.api.libs.json.{ JsString, JsValue, Json, Writes }

class LocalUnitSpec extends FreeSpec with Matchers {

  /*
   * Note that this fixture defines custom Writes for EnterpriseLink and Address.
   * This is to prevent a change to one of these formats from rippling through multiple specs.
   */
  private trait Fixture {
    implicit val enterpriseLinkWrites = new Writes[EnterpriseLink] {
      override def writes(el: EnterpriseLink): JsValue =
        JsString(s"${el.ern}_${el.entref}")
    }

    implicit val addressWrites = new Writes[Address] {
      override def writes(a: Address): JsValue =
        JsString(s"${a.line1}_${a.line2}_${a.line3}_${a.line4}_${a.line5}_${a.postcode}")
    }

    def expectedJsonStrOf(localUnit: LocalUnit): String = {
      import localUnit._
      import localUnit.enterprise._
      import localUnit.address._
      s"""
         |{
         | "lurn":"$lurn",
         | "luref":"$luref",
         | "name":"$name",
         | "tradingStyle":"$tradingStyle",
         | "sic07":"$sic07",
         | "employees":$employees,
         | "enterprise": "${ern}_$entref",
         | "address": "${line1}_${line2}_${line3}_${line4}_${line5}_$postcode"
         |}""".stripMargin
    }
  }

  "A LocalUnit" - {
    "can be represented as JSON" in new Fixture {
      val aLocalUnit = LocalUnit(lurn = "900000011", luref = "luref-value", name = "COMPANY X",
        tradingStyle = "tradingStyle-value", sic07 = "sic07-value", employees = 42,
        enterprise = EnterpriseLink(ern = "1000000012", entref = "entref-value"),
        address = Address(line1 = "line1-value", line2 = "line2-value", line3 = "line3-value",
          line4 = "line4-value", line5 = "line5-value", postcode = "postcode-value"))

      Json.toJson(aLocalUnit)(LocalUnit.makeWrites) shouldBe Json.parse(expectedJsonStrOf(aLocalUnit))
    }
  }
}
