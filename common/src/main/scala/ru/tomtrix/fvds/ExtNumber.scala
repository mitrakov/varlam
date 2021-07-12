package ru.tomtrix.fvds

import scala.math._

/**
 * Created by Tom-Trix-NBW on 01.03.14
 */
object ExtNumber {
  implicit class ExtendedInt(n: Int) {
    def inflect(s1: String, s234: String, s567890 : String) = n.toString match {
      case s if s.endsWith("1" ) && !s.endsWith("11") => s1
      case s if s.endsWith("2" ) && !s.endsWith("12") => s234
      case s if s.endsWith("3" ) && !s.endsWith("13") => s234
      case s if s.endsWith("4" ) && !s.endsWith("14") => s234
      case _ => s567890
    }
  }

  implicit class ExtendedDouble(n: Double) {
    def rounded(x: Int) = {
      val w = pow(10, x)
      (n * w).toLong.toDouble / w
    }
  }
}