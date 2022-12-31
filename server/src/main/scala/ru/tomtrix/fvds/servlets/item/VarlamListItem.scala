package ru.tomtrix.fvds.servlets.item

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.util.parsing.json.JSONArray
import ru.tomtrix.fvds.db.Item
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Enumerates items
 */
@WebServlet(urlPatterns = Array("/item/list"))
class VarlamListItem extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val result = (for {
      user <- getUser(req.getHeader("username"))
      category <- getCategory(user, req.getParameter("category").str)
    } yield {
      val sql = "SELECT * FROM item JOIN category USING (category_id) WHERE user_id = :x AND category_id = :y"
      dao.findBySQL(sql, classOf[Item], Map("x" -> user.getUserId, "y" -> category.getCategoryId)) {_.getName}
    }) getOrElse Nil
    Result(resp, 200, 0, Map("msg" -> JSONArray(result))).write()
  }
}
