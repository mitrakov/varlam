package ru.tomtrix.fvds.servlets.item

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.db.Operation
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Removes an item
 */
@WebServlet(urlPatterns = Array("/item/delete"))
class VarlamDeleteItem extends VarlamServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val result = for {
      itemName <- req.getParameter("name").asOption
      user <- getUser(req.getHeader("username").str)
      item <- getItem(user, itemName)
    } yield {
      val m = Map("x" -> item.getItemId)
      dao.findBySQL("SELECT * FROM operation WHERE item_id = :x", classOf[Operation], m) {o => o}.size
    }
    result match {
      case Some(0) => Result(resp, 200, 0, Map("msg" -> "it's safe to remove an item")).write()
      case Some(x) => Result(resp, 200, 24, Map("operations" -> x)).write()
      case None => Result(resp, 400, 23).write()
    }
  }

   override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
     val json = req.getReader.readLine.asJsonObject
     val result = for {
       itemName <- json get "name" map {_.toString.str}
       user <- getUser(req.getHeader("username").str)
       item <- getItem(user, itemName)
     } yield {
       dao remove item
       getOperations(user) foreach {op => operationCache removeFromCache op.getOperationId}
       itemName
     }
     result match {
       case Some(x) => Result(resp, 200, 0, Map("msg" -> s"Item $x deleted")).write()
       case None => Result(resp, 400, 22).write()
     }
   }
 }
