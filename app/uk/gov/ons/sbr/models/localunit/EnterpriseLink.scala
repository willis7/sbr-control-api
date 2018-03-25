package uk.gov.ons.sbr.models.localunit

import play.api.libs.json.Json

case class EnterpriseLink(ern: String, entref: String)

object EnterpriseLink {
  implicit val writes = Json.writes[EnterpriseLink]
}