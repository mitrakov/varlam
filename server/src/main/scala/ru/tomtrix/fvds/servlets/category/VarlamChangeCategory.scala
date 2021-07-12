package ru.tomtrix.fvds.servlets.category

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Changes a category
 */
@WebServlet(urlPatterns = Array("/category/change"))
class VarlamChangeCategory extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      categoryName <- json get "name" map {_.toString.str}
      newCategoryName <- json get "newName" map {_.toString.str}
      newParentName <- json get "newParentName" map {_.toString.str}
      user <- getUser(req.getHeader("username").str)
      (category, parentNameOpt) <- getCategoryFunc(user, categoryName) {c => c -> safe(c.getCategory.getName, _ => {})}
    } yield {
      val parentName = parentNameOpt getOrElse "NULL"
      category setName newCategoryName
      category.setCategory(getCategory(user, newParentName) getOrElse null)
      dao merge category
      getOperations(user) foreach {op => operationCache removeFromCache op.getOperationId}
      (categoryName -> parentName) -> (newCategoryName -> newParentName)
    }
    result match {
      case Some(((x, y), (u, v))) =>
        Result(resp, 200, 0, Map("msg" -> s"Category $x (parent=$y) changed to $u (parent=$v)")).write()
      case None =>
        Result(resp, 400, 11).write()
    }
  }
}