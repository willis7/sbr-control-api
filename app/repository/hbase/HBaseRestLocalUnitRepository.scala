package repository.hbase

import javax.inject.Inject
import repository.{LocalUnitRepository, RegexQuery, Row}
import uk.gov.ons.sbr.models.Envelope
import uk.gov.ons.sbr.models.localunit.LocalUnit

import scala.concurrent.{ExecutionContext, Future}

class HBaseRestLocalUnitRepository @Inject() (regexQuery: RegexQuery) extends LocalUnitRepository {

  override def retrieveLatest(ern: String, lurn: String)(implicit ec: ExecutionContext): Future[Option[Envelope[LocalUnit]]] =
    regexQuery.find("local_unit", s"${ern.reverse}~[0-9]{6}~$lurn").map { rows =>
      rows.map(toLocalUnitEnvelope).lastOption
    }

  private def toLocalUnitEnvelope(row: Row): Envelope[LocalUnit] = ???
}
