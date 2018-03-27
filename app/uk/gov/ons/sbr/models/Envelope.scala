package uk.gov.ons.sbr.models

import java.time.YearMonth
import java.time.format.DateTimeFormatter

import play.api.libs.json._

case class Envelope[A](period: YearMonth, a: A)

object Envelope {
  private implicit object YearMonthWrites extends Writes[YearMonth] {
    override def writes(ym: YearMonth): JsValue =
      JsString(ym.format(DateTimeFormatter.ofPattern("uuuuMM")))
  }

  def makeWrites[A: Writes](label: String): Writes[Envelope[A]] =
    new Writes[Envelope[A]] {
      override def writes(envelope: Envelope[A]): JsValue = {
        val writesA = implicitly[Writes[A]]
        JsObject(Map(
          "period" -> Json.toJson(envelope.period),
          label -> writesA.writes(envelope.a)
        ))
      }
    }
}