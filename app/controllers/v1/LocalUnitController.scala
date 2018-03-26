package controllers.v1

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repository.LocalUnitRepository

import scala.concurrent.ExecutionContext.Implicits.global

class LocalUnitController @Inject() (repository: LocalUnitRepository) extends Controller {
  def retrieveLocalUnit(ern: String, lurn: String) = Action.async { _ =>
    repository.retrieveLatest(ern, lurn).map { optLocalUnit =>
      optLocalUnit.fold(Ok("""{"msg": "Hello World!"}""").as(JSON)) { localUnit =>
        Ok(Json.toJson(localUnit))
      }
    }
  }
}
