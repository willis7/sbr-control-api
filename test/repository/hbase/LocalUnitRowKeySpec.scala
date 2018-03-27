package repository.hbase

import java.time.Month.MARCH
import java.time.YearMonth

import org.scalatest.{FreeSpec, Matchers}

class LocalUnitRowKeySpec extends FreeSpec with Matchers {
  "A LocalUnit row key" - {
    "can be exploded into its constituent parts" in {
      LocalUnitRowKey.unapply("54321~201803~9876") shouldBe Some("12345", YearMonth.of(2018, MARCH), "9876")
    }
  }
}
