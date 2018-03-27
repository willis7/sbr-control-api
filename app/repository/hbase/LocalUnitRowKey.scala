package repository.hbase

import java.time.YearMonth
import java.time.format.DateTimeFormatter.ofPattern

import scala.util.Try

object LocalUnitRowKey {
  def unapply(rowKey: String): Option[(String, YearMonth, String)] = {
    val components = rowKey.split('~')
    if (components.length == 3) {
      Try(YearMonth.parse(components(1), ofPattern("uuuuMM"))).toOption.map { yearMonth =>
        (components(0).reverse, yearMonth, components(2))
      }
    } else None
  }
}
