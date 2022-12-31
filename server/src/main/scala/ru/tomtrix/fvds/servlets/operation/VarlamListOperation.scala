package ru.tomtrix.fvds.servlets.operation

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.util.parsing.json._
import ru.tomtrix.fvds.db._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Enumerate operations
 * <ul>
 * <li> if an '''item''' parameter specified, it returns all the operations connected to that item
 * <li> if a '''category''' parameter specified, it return all the operations connected to that category,
 *      all its children and items
 * <li> otherwise it returns all the operations
 * </ul>
 */
@WebServlet(urlPatterns = Array("/operation/list/*"))
class VarlamListOperation extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    getUser(req.getHeader("username").str) match {
      case None => Result(resp, 404, 6).write()
      case Some(usr) =>
        // filter the result by the items/categories
        val itemOpt = req.getParameter("item").asOption flatMap {name => getItem(usr, name)} map {_.getItemId}
        val categoryOpt = req.getParameter("category").asOption flatMap { name =>
          getCategoryFunc(usr, name) {c => getAllItems(c) map {_.getItemId}}
        }
        val itemIDs = itemOpt.toList ++ categoryOpt.toList.flatten
        // build SQL and parameters
        val sql = req.getPathInfo.str match {
          case "/all" => "SELECT * FROM operation JOIN item USING (item_id) JOIN category USING (category_id) WHERE user_id = :x ORDER BY time DESC"
          case x => logger.info(s"x = $x")
            val mkstring = if (itemIDs.isEmpty) "-1" else itemIDs.mkString(",") // safe for SQL-injections, because itemIDs is a List[Long]
            s"SELECT * FROM operation JOIN item USING (item_id) JOIN category USING (category_id) WHERE user_id = :x AND item_id IN ($mkstring) ORDER BY time DESC"
        }
        // fetch the result
        val result = dao.findBySQL(sql, classOf[Operation], Map("x" -> usr.getUserId)) {_.getOperationId}
        Result(resp, 200, 0, Map("msg" -> JSONArray(result))).write()
    }
  }

  private def getAllItems(category: Category): List[Item] = {
    def f(cats: List[Category]): List[Item] = {
      cats flatMap {cat =>
        cat.getItems.toArray(Array.empty[Item]).toList ++
        f(cat.getCategories.toArray(Array.empty[Category]).toList)
      }
    }

    f(List(category))
  }
}
