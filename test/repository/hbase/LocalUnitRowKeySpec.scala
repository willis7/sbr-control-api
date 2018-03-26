package repository.hbase

import org.scalatest.{FreeSpec, Matchers}

class LocalUnitRowKeySpec extends FreeSpec with Matchers {

  private trait Fixture {
    val Ern = "1000000012"
    val ReversedErn = "2100000001"
    val Lurn = "900000011"
  }

  "A RowKey for a LocalUnit" - {
    "can be built containing a period wildcard" in new Fixture {
      LocalUnitRowKey.withPeriodWildcard(Ern, Lurn) shouldBe s"$ReversedErn~*~$Lurn"
    }
  }
}
