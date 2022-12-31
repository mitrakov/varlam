package ru.tomtrix.fvds.servlets.category

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.util.parsing.json._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.db.Category
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Enumerates categories and flatten them to a linear array
 */
@WebServlet(urlPatterns = Array("/category/list/linear"))
class VarlamListCategoryPlain extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {

    // recursive traversal of categories tree
    def f(category: Category): List[String] =
      category.getName :: category.getCategories.toArray(Array.empty[Category]).toList.flatMap(f)

    //MySQL: val sql = "SELECT * FROM category WHERE user_id = :x AND ISNULL(parent_id)"
    val sql = "SELECT * FROM category WHERE user_id = :x AND parent_id ISNULL"
    val result = getUser(req.getHeader("username").str) map { user =>
      dao.findBySQL(sql, classOf[Category], Map("x" -> user.getUserId)) {f}.flatten
    } getOrElse Nil
    Result(resp, 200, 0, Map("msg" -> JSONArray(result))).write()
  }
}
