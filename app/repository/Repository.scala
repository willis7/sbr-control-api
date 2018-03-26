package repository

import scala.concurrent.Future

trait Repository {
  def find(table: String, rowKey: String, columnFamily: String): Future[Map[String, String]] = ???
}
