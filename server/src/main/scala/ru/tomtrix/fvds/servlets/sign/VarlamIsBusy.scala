package ru.tomtrix.fvds.servlets.sign

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Checks if the login is busy
 */
@WebServlet(urlPatterns = Array("/is/busy"))
class VarlamIsBusy extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val username = req.getParameter("username").str
    Result(resp, 200, 0, Map("busy" -> getUser(username).isDefined)).write()
  }
}
