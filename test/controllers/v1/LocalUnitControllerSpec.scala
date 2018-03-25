package controllers.v1

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FreeSpec, Matchers, OptionValues }
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.LocalUnitRepository
import uk.gov.ons.sbr.models.localunit.{ Address, EnterpriseLink, LocalUnit }
import play.api.test.Helpers._
import play.mvc.Http.MimeTypes.JSON

import scala.concurrent.Future

class LocalUnitControllerSpec extends FreeSpec with Matchers with MockFactory with OptionValues {

  private trait Fixture {
    val ALocalUnit = LocalUnit("lurn", "luref", "name", "tradingStyle", "sic07", employees = 3,
      enterprise = EnterpriseLink("ern", "entref"),
      address = Address("line1", "line2", "line3", "line4", "line5", "postcode"))

    val repository = mock[LocalUnitRepository]
    val controller = new LocalUnitController(repository)
  }

  "A request" - {
    "to retrieve a local unit by Enterprise Reference (ERN) and Local Unit reference (LURN)" - {
      "responds with a JSON representation of the local unit when it is found" in new Fixture {
        (repository.retrieveLatest _).expects("ern", "lurn").returning(Future.successful(Some(ALocalUnit)))

        val response = controller.retrieveLocalUnit("ern", "lurn").apply(FakeRequest())

        status(response) shouldBe OK
        contentType(response).value shouldBe JSON
        contentAsJson(response) shouldBe Json.toJson(ALocalUnit)
      }
    }
  }
}
