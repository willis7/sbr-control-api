package uk.gov.ons.sbr.models.localunit

import play.api.libs.json.Json

case class Address(line1: String, line2: String, line3: String, line4: String, line5: String, postcode: String)

object Address {
  implicit val writes = Json.writes[Address]
}
