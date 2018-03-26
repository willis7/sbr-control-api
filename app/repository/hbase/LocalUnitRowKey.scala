package repository.hbase

object LocalUnitRowKey {
  def withPeriodWildcard(ern: String, lurn: String): String =
    s"${ern.reverse}~*~$lurn"
}
