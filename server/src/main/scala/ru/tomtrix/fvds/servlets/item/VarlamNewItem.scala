package ru.tomtrix.fvds.servlets.item

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.db.Item
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Creates a new item
 */
@WebServlet(urlPatterns = Array("/item/new"))
class VarlamNewItem extends VarlamServlet {
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      itemName <- json get "name" flatMap {_.toString.asOption}
      categoryName <- json get "category" map {_.toString.str}
      user <- getUser(req.getHeader("username").str)
      category <- getCategory(user, categoryName)
    } yield {
      dao persist new Item(category, itemName)
      itemName
    }
    result match {
      case Some(x) => Result(resp, 200, 0, Map("msg" -> s"Item $x saved")).write()
      case None => Result(resp, 400, 20).write()
    }
  }
}
