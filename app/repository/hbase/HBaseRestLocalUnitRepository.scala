package repository.hbase

import repository.{LocalUnitRepository, Repository}
import uk.gov.ons.sbr.models.localunit.{Address, EnterpriseLink, LocalUnit}

import scala.concurrent.Future

class HBaseRestLocalUnitRepository(repository: Repository) extends LocalUnitRepository {
  override def retrieveLatest(ern: String, lurn: String): Future[Option[LocalUnit]] =
    Future.successful(Some(LocalUnit(lurn = "foo", luref = "bar", name = "baz", tradingStyle = "", sic07 = "",
      employees = 42, enterprise = EnterpriseLink(ern = "oof", entref = "rab"),
      address = Address(line1 = "22 Acacia Avenue", line2 = "", line3 = "", line4 = "", line5 = "",
        postcode = "NP10 8XY"))))
}
