package uk.gov.ons.sbr.models.localunit

import play.api.libs.json.{ Json, Writes }

case class LocalUnit(lurn: String, luref: String, name: String, tradingStyle: String, sic07: String, employees: Int,
  enterprise: EnterpriseLink, address: Address)

object LocalUnit {
  implicit def makeWrites(implicit writesEnterpriseLink: Writes[EnterpriseLink] = EnterpriseLink.writes,
    writesAddress: Writes[Address] = Address.writes): Writes[LocalUnit] =
    Json.writes[LocalUnit]
}