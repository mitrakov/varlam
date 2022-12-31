package ru.tomtrix.fvds.servlets.category

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.db.Category
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Creates a new category
 */
@WebServlet(urlPatterns = Array("/category/new"))
class VarlamNewCategory extends VarlamServlet {
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      categoryName <- json get "name" flatMap {_.toString.asOption}
      parentName <- json get "parent" map {_.toString.str}
      user <- getUser(req.getHeader("username").str)
    } yield {
      val category = new Category(user, categoryName)
      getCategory(user, parentName) foreach category.setCategory
      dao persist category
      categoryName -> parentName
    }
    result match {
      case Some((x, y)) => Result(resp, 200, 0, Map("msg" -> s"Category $x with parent=$y saved")).write()
      case None => Result(resp, 402, 10).write()
    }
  }
}
