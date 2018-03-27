package repository.hbase

import repository.{ RegexQuery, Row }

import scala.concurrent.Future

class HBaseRestRegexQuery extends RegexQuery {
  override def find(table: String, query: String): Future[Seq[Row]] = ???
}
