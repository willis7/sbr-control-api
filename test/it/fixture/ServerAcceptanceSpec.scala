package fixture

import org.scalatest.Outcome
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Port
import play.api.libs.ws.WSClient
import play.api.test.{ DefaultAwaitTimeout, FutureAwaits, WsTestClient }

class ServerAcceptanceSpec extends AcceptanceSpec with GuiceOneServerPerSuite with DefaultAwaitTimeout with FutureAwaits {
  override type FixtureParam = WSClient

  override protected def withFixture(test: OneArgTest): Outcome = {
    WsTestClient.withClient { wsClient =>
      withFixture(test.toNoArgTest(wsClient))
    }(new Port(port))
  }
}
