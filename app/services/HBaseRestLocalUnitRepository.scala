package services
import uk.gov.ons.sbr.models.localunit.LocalUnit

import scala.concurrent.Future

class HBaseRestLocalUnitRepository extends LocalUnitRepository {
  override def retrieveLatest(ern: String, lurn: String): Future[Option[LocalUnit]] =
    Future.successful(None)
}
