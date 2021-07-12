package ru.tomtrix.fvds

/**
 * Created by Tom-Trix-NBW on 02.03.14
 */
object ExtList {
  implicit class ExtendedList[T](lst: List[T]) {
    def cast[V] = lst filter {_.isInstanceOf[V]} map {_.asInstanceOf[V]}
    def get(n: Int) = if (n < 0 || n >= lst.size) None else Some(lst(n))
    def maxOpt[B>:T](implicit cmp: scala.Ordering[B]) = if (lst.isEmpty) None else Some(lst.max(cmp))
    def minOpt[B>:T](implicit cmp: scala.Ordering[B]) = if (lst.isEmpty) None else Some(lst.min(cmp))
  }
}
