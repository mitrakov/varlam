package ru.tomtrix.fvds.servlets.operation

import java.math.BigDecimal
import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Changes an operation
 */
@WebServlet(urlPatterns = Array("/operation/change"))
class VarlamChangeOperation extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      id <- json get "id" map {_.asInstanceOf[Double].toLong}
      operation <- getOperation(id)
      user <- getUser(req.getHeader("username").str)
      (itemName, personName, summa) <- getOperationFunc(id) { t =>
        (t.getItem.getName, safe {t.getPerson.getName} getOrElse "NULL", t.getSumma)
      }
    } yield {
      val newDate = json get "date" map {s => formatter parse s.toString}
      val newItem = json get "itemName" map {_.toString.str} flatMap {i => getItem(user, i)}
      val newPerson = json get "personName" map {_.toString.str} flatMap {p => getPerson(user, p)}
      val newSumma = json get "summa" flatMap {d => safe {new BigDecimal(d.asInstanceOf[Double])}}
      val newCurrency = json get "currency" map {_.toString.str}
      val newCurrencyRate = json get "currencyRate" flatMap {d => safe {new BigDecimal(d.asInstanceOf[Double])}}

      newItem foreach operation.setItem
      newPerson foreach operation.setPerson
      newDate foreach operation.setTime
      newSumma foreach operation.setSumma
      newCurrency foreach operation.setCurrency
      newCurrencyRate foreach operation.setCurrencyRate

      dao merge operation
      operationCache removeFromCache id
      (id, itemName, personName, summa, newItem, newPerson, newSumma)
    }
    result match {
      case Some((a, b, c, d, e, f, g)) => Result(resp, 200, 0,
        Map("msg" -> s"Operation $a changed from ($b, $c, $d) to ($e, $f, $g)")).write()
      case None => Result(resp, 400, 31).write()
    }
  }
}
