package ru.tomtrix.fvds.servlets.person

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.util.parsing.json.JSONArray
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.db.Person
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Enumerates persons
 */
@WebServlet(urlPatterns = Array("/person/list"))
class VarlamListPerson extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val result = getUser(req.getHeader("username").str) map { user =>
      dao.findBySQL("SELECT * FROM person WHERE user_id = :x", classOf[Person], Map("x" -> user.getUserId)) {_.getName}
    } getOrElse Nil
    Result(resp, 200, 0, Map("msg" -> JSONArray(result))).write()
  }
}
