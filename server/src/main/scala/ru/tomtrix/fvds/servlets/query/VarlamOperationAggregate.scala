package ru.tomtrix.fvds.servlets.query

import java.math.BigDecimal
import javax.servlet.annotation.WebServlet
import javax.servlet.http._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Aggregate by 'operation' table
 */
@WebServlet(urlPatterns = Array("/query/operations/aggregate"))
class VarlamOperationAggregate extends VarlamServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val result = for {
      user <- getUser(req.getHeader("username"))
      dateFrom <- Option(req.getParameter("from")) map {formatter.parse}
      dateTo <- Option(req.getParameter("to")) map {formatter.parse}
      func = Option(req.getParameter("function")).map(_.str).filter(checkFunction) getOrElse "SUM"
      sql = s"SELECT $func(summa*currency_rate) FROM operation JOIN item USING(item_id) JOIN category USING(category_id) WHERE user_id = :x AND (time BETWEEN :y AND :z)"
      value <- dao.findScalarBySQL[BigDecimal, BigDecimal](sql, Map("x" -> user.getUserId, "y" -> dateFrom, "z" -> dateTo))(identity)
    } yield value

    result match {
      case Some(d) => Result(resp, 200, 0, Map("msg" -> d)).write()
      case None => Result(resp, 400, 52).write()
    }
  }

  private def checkFunction(fn: String): Boolean = {
    // check function to avoid SQL-Injections
    Set("SUM", "MIN", "MAX", "AVG").contains(fn.str.toUpperCase)
  }
}
