package repository

import uk.gov.ons.sbr.models.localunit.LocalUnit

import scala.concurrent.Future

trait LocalUnitRepository {
  def retrieveLatest(ern: String, lurn: String): Future[Option[LocalUnit]]
}
