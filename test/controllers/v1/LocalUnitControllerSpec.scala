package controllers.v1

import java.time.Month.JANUARY
import java.time.YearMonth

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FreeSpec, Matchers, OptionValues}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.ons.sbr.models.localunit.{Address, EnterpriseLink, LocalUnit}
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes.JSON
import repository.LocalUnitRepository
import uk.gov.ons.sbr.models.Envelope

import scala.concurrent.Future

class LocalUnitControllerSpec extends FreeSpec with Matchers with MockFactory with OptionValues {

  private trait Fixture {
    val LocalUnitEnvelope = Envelope(period = YearMonth.of(2018, JANUARY),
      LocalUnit("lurn", "luref", "name", "tradingStyle", "sic07", employees = 3,
        enterprise = EnterpriseLink("ern", "entref"),
        address = Address("line1", "line2", "line3", "line4", "line5", "postcode")))

    implicit val ec = scala.concurrent.ExecutionContext.global
    val repository = mock[LocalUnitRepository]
    val controller = new LocalUnitController(repository)
  }

  "A request" - {
    "to retrieve a local unit by Enterprise Reference (ERN) and Local Unit reference (LURN)" - {
      "responds with a JSON representation of the local unit when it is found" in new Fixture {
        (repository.retrieveLatest _).expects("ern", "lurn").returning(Future.successful(Some(LocalUnitEnvelope)))

        val response = controller.retrieveLocalUnit("ern", "lurn").apply(FakeRequest())

        status(response) shouldBe OK
        contentType(response).value shouldBe JSON
        contentAsJson(response) shouldBe Json.toJson(LocalUnitEnvelope)(Envelope.makeWrites("localUnit"))
      }
    }
  }
}
