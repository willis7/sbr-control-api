package repository.hbase

import java.time.Month.MARCH
import java.time.YearMonth

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Second, Span}
import org.scalatest.{FreeSpec, Matchers, OptionValues}
import repository.{RegexQuery, Row}
import uk.gov.ons.sbr.models.Envelope
import uk.gov.ons.sbr.models.localunit.{Address, EnterpriseLink, LocalUnit}

import scala.concurrent.Future

class HBaseRestLocalUnitRepositorySpec extends FreeSpec with Matchers with MockFactory with OptionValues with ScalaFutures {

  private implicit val defaultPatience = PatienceConfig(timeout = Span(1, Second), interval = Span(5, Millis))

  private trait Fixture {
    val Ern = "1000000012"
    val Lurn = "900000011"

    implicit val ec = scala.concurrent.ExecutionContext.global
    val regexQuery = mock[RegexQuery]
    val localUnitRepository = new HBaseRestLocalUnitRepository(regexQuery)

    def columnsFor(ern: String, lurn: String, id: String): Map[String, String] =
      Map("lurn" -> lurn, "luref" -> s"luref-$id", "ern" -> ern, "entref" -> s"entref-$id",
        "name" -> s"name-$id", "tradingstyle" -> s"tradingstyle-$id", "address1" -> s"address1-$id",
        "address2" -> s"address2-$id", "address3" -> s"address3-$id", "address4" -> s"address4-$id",
        "address5" -> s"address5-$id", "postcode" -> s"postcode-$id", "sic07" -> s"sic07-$id",
        "employees" -> "42")
  }

  "A LocalUnit Repository" - {
    "can perform a wildcard period search to retrieve then latest local unit with the target Ern and Lurn" in new Fixture {
      val row201802 = Row(rowKey = s"${Ern.reverse}~201802~$Lurn", columnsFor(Ern, Lurn, "201802"))
      val row201803 = Row(rowKey = s"${Ern.reverse}~201803~$Lurn", columnsFor(Ern, Lurn, "201803"))
      (regexQuery.find _).expects("local_unit", s"${Ern.reverse}~[0-9]{6}~$Lurn").returning(Future.successful(
        Seq(row201802, row201803)))
      val latestPeriod = "201803"

      whenReady(localUnitRepository.retrieveLatest(Ern, Lurn)) { result =>
        result.value shouldBe Envelope(period = YearMonth.of(2018, MARCH),
          LocalUnit(lurn = Lurn, luref = s"luref-$latestPeriod", name = s"name-$latestPeriod",
            tradingStyle = s"tradingstyle-$latestPeriod", sic07 = s"sic07-$latestPeriod", employees = 42,
            enterprise = EnterpriseLink(ern = Ern, entref = s"entref-$latestPeriod"),
            address = Address(line1 = s"address1-$latestPeriod", line2 = s"address2-$latestPeriod",
              line3 = s"address3-$latestPeriod", line4 = s"address4-$latestPeriod", line5 = s"address5-$latestPeriod",
              postcode = s"postcode-$latestPeriod"))
        )
      }
    }
  }
}
