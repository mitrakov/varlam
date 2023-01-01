package ru.tomtrix.fvds.servlets.operation

import java.util.Date
import java.math.BigDecimal
import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.db.Operation
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Creates a new operation
 */
@WebServlet(urlPatterns = Array("/operation/new"))
class VarlamNewOperation extends VarlamServlet {
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      itemName <- json get "itemName" map {_.toString.str}
      summa <- json get "summa" flatMap {d => safe {new BigDecimal(d.asInstanceOf[Double])}}
      user <- getUser(req.getHeader("username").str)
      item <- getItem(user, itemName)
    } yield {
      val date = json get "date" map {s => formatter parse s.toString} getOrElse new Date()
      val person = json get "personName" map {_.toString.str} flatMap {p => getPerson(user, p)}
      val currency = json get "currency" map {_.toString.str} getOrElse "RUB"
      val currencyRate = json get "currencyRate" flatMap {d => safe {new BigDecimal(d.asInstanceOf[Double])}}
      val operation = new Operation(item, date, summa, currency)
      person foreach operation.setPerson
      currencyRate foreach operation.setCurrencyRate

      dao persist operation
      (itemName, date, summa, currency, person, currencyRate)
    }
    result match {
      case Some((a, b, c, d, e, f)) => Result(resp, 200, 0, Map("msg" -> s"Operation saved: $a, $b, $c, $d, $e, $f")).write()
      case None => Result(resp, 400, 40).write()
    }
  }
}
