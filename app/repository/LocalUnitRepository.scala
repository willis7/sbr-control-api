package repository

import uk.gov.ons.sbr.models.Envelope
import uk.gov.ons.sbr.models.localunit.LocalUnit

import scala.concurrent.{ExecutionContext, Future}

trait LocalUnitRepository {
  def retrieveLatest(ern: String, lurn: String)(implicit ec: ExecutionContext): Future[Option[Envelope[LocalUnit]]]
}
