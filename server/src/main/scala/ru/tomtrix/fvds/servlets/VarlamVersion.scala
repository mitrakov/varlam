package ru.tomtrix.fvds.servlets

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.CaseClasses.Result

/**
 * Simple servlet that shows the current build version
 */
@WebServlet(urlPatterns = Array("/version"))
class VarlamVersion extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = safe {
    Result(resp, 200, 0, Map("msg" -> "Varlam Application 23.1.9.1")).write()
  }
}
