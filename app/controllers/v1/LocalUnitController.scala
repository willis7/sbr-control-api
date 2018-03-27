package controllers.v1

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repository.LocalUnitRepository
import uk.gov.ons.sbr.models.Envelope

import scala.concurrent.ExecutionContext.Implicits.global

class LocalUnitController @Inject() (repository: LocalUnitRepository) extends Controller {
  def retrieveLocalUnit(ern: String, lurn: String) = Action.async { _ =>
    repository.retrieveLatest(ern, lurn).map { optEnvelope =>
      optEnvelope.fold(NotImplemented("")) { envelope =>
        Ok(Json.toJson(envelope)(Envelope.makeWrites("localUnit")))
      }
    }
  }
}
