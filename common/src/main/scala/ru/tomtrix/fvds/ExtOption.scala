package ru.tomtrix.fvds

/**
 * Created by Tom-Trix-NBW on 02.03.14
 */
object ExtOption {
  implicit class ExtendedOption[T](opt: Option[T]) {
    def cast[V] = opt filter {_.isInstanceOf[V]} map {_.asInstanceOf[V]}
  }
}
