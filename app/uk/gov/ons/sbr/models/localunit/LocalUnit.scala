package uk.gov.ons.sbr.models.localunit

import play.api.libs.json.Json

case class LocalUnit(lurn: String, luref: String, name: String, tradingStyle: String, sic07: String, employees: Int,
  enterprise: EnterpriseLink, address: Address)

object LocalUnit {
  implicit val writes = Json.writes[LocalUnit]
}