package ru.tomtrix.fvds.servlets.category

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.db._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Removes a category
 */
@WebServlet(urlPatterns = Array("/category/delete"))
class VarlamDeleteCategory extends VarlamServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val result = for {
      categoryName <- req.getParameter("name").asOption
      user <- getUser(req.getHeader("username").str)
      category <- getCategory(user, categoryName)
    } yield {
      val m = Map("x" -> category.getCategoryId)
      dao.findBySQL("SELECT * FROM category WHERE parent_id = :x", classOf[Category], m) {c => c}.size ->
        dao.findBySQL("SELECT * FROM item WHERE category_id = :x", classOf[Item], m) {i => i}.size
    }
    result match {
      case Some((0, 0)) => Result(resp, 200, 0, Map("msg" -> "it's safe to remove a category")).write()
      case Some((x, 0)) => Result(resp, 200, 14, Map("categories" -> x)).write()
      case Some((0, y)) => Result(resp, 200, 15, Map("items" -> y)).write()
      case Some((x, y)) => Result(resp, 200, 16, Map("categories" -> x, "items" -> y)).write()
      case None         => Result(resp, 400, 13).write()
    }
  }

  override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
     val json = req.getReader.readLine.asJsonObject
     val result = for {
       categoryName <- json get "name" map {_.toString.str}
       user <- getUser(req.getHeader("username").str)
       category <- getCategory(user, categoryName)
     } yield {
       dao remove category
       getOperations(user) foreach {op => operationCache removeFromCache op.getOperationId}
       categoryName
     }
     result match {
       case Some(x) => Result(resp, 200, 0, Map("msg" ->  s"Category $x deleted")).write()
       case None => Result(resp, 403, 12).write()
     }
   }
 }
