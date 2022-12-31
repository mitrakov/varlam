package ru.tomtrix.fvds.servlets.item

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Changes an item
 */
@WebServlet(urlPatterns = Array("/item/change"))
class VarlamChangeItem extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      itemName <- json get "name" map {_.toString.str}
      newName <- json get "newName" map {_.toString.str}
      newCategoryName <- json get "newCategoryName" map {_.toString.str}
      user <- getUser(req.getHeader("username").str)
      (item, oldCategoryName) <- getItemFunc(user, itemName) {i => i -> i.getCategory.getName}
      newCategory <- getCategory(user, newCategoryName)
    } yield {
      item setName newName
      item setCategory newCategory
      dao merge item
      getOperations(user) foreach {op => operationCache removeFromCache op.getOperationId}
      (itemName -> oldCategoryName) -> (newName -> newCategoryName)
    }
    result match {
      case Some(((x, y), (u, v))) => Result(resp, 200, 0, Map("msg" -> s"Item $x ($y) changed to $u ($v)")).write()
      case None => Result(resp, 400, 21).write()
    }
  }
}
