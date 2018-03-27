package repository

import scala.concurrent.Future

case class Row(rowKey: String, variables: Map[String, String])

trait RegexQuery {
  def find(table: String, query: String): Future[Seq[Row]]
}
