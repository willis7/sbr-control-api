package repository.hbase

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, Matchers, OptionValues}
import repository.Repository
import uk.gov.ons.sbr.models.localunit.{Address, EnterpriseLink, LocalUnit}

import scala.concurrent.Future

class HBaseRestLocalUnitRepositorySpec extends FreeSpec with Matchers with MockFactory with OptionValues with ScalaFutures {

  private trait Fixture {
    val repository = mock[Repository]
    val localUnitRepository = new HBaseRestLocalUnitRepository(repository)
  }

  "A LocalUnit Repository" - {
    "foo" in new Fixture {
      val record = Map("lurn" -> "", "luref" -> "", "ern" -> "", "entref" -> "", "name" -> "", "tradingstyle" -> "",
        "address1" -> "", "address2" -> "", "address3" -> "", "address4" -> "", "address5" -> "", "postcode" -> "",
        "sic07" -> "", "employees" -> "")

      (repository.find _).expects("local_unit", "", "d").returning(Future.successful(record))

      whenReady(localUnitRepository.retrieveLatest(ern = "", lurn = "")) { result =>
        result.value shouldBe LocalUnit(lurn = "", luref = "", name = "", tradingStyle = "", sic07 = "", employees = 42,
          enterprise = EnterpriseLink(ern = "", entref = ""),
          address = Address(line1 = "", line2 = "", line3 = "", line4 = "", line5 = "", postcode = ""))
      }
    }
  }
}
