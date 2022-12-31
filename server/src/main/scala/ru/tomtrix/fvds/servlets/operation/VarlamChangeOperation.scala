package ru.tomtrix.fvds.servlets.operation

import java.util.Date
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
      newItemName <- json get "itemName" map {_.toString.str}
      newPersonName <- json get "personName" map {_.toString.str}
      newSumma <- json get "summa" map {_.asInstanceOf[Double].toInt}
      user <- getUser(req.getHeader("username").str)
      operation <- getOperation(id)
      (itemName, personName, summa) <- getOperationFunc(id) { t =>
        (t.getItem.getName, safe {t.getPerson.getName} getOrElse "NULL", t.getSumma)
      }
      newItem <- getItem(user, newItemName)
    } yield {
      val date = json get "date" map {s => formatter parse s.toString} getOrElse new Date()
      operation.setPerson(getPerson(user, newPersonName).orNull)
      operation setItem newItem
      operation setSumma newSumma
      operation setTime date
      dao merge operation
      operationCache removeFromCache id
      (id, itemName, personName, summa, newItemName, newPersonName, newSumma)
    }
    result match {
      case Some((a, b, c, d, e, f, g)) => Result(resp, 200, 0,
        Map("msg" -> s"Operation $a changed from ($b, $c, $d) to ($e, $f, $g)")).write()
      case None => Result(resp, 400, 31).write()
    }
  }
}
