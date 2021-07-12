package ru.tomtrix.fvds.servlets.operation

import java.text._
import java.util.Date
import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
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
      personName <- json get "personName" map {_.toString.str}
      summa <- json get "summa" map {_.asInstanceOf[Double].toInt}
      user <- getUser(req.getHeader("username").str)
      item <- getItem(user, itemName)
    } yield {
      val date = json get "date" map {s => formatter parse s.toString} getOrElse new Date()
      logger.info("Date received: " + DateFormat.getInstance().format(date))
      val operation = new Operation(item, user, null, date, summa)
      getPerson(user, personName) foreach operation.setPerson
      dao persist operation
      itemName -> summa
    }
    result match {
      case Some((x, y)) => Result(resp, 200, 0, Map("msg" -> s"Operation $x ($y rub.) saved")).write()
      case None => Result(resp, 400, 40).write()
    }
  }
}