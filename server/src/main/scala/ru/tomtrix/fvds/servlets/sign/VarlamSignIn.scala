package ru.tomtrix.fvds.servlets.sign

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.concurrent.ExecutionContext.Implicits.global
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.AuthManager._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Auth servlet
 */
@WebServlet(urlPatterns = Array("/sign/in"), asyncSupported = true)
class VarlamSignIn extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = async(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val username = json.getOrElse("username", "").toString.str
    val password = json.getOrElse("password", "").toString.str
    val isChecked = json.getOrElse("client", "").toString.str match {
      case "web" => checkHash(username, password)
      case "android" => checkHash(username, password)
      case _ => checkPassword(username, password)
    }
    logger debug s"User $username trying to log in"
    isChecked map {
      case true => Result(resp, 200, 0, Map("token" -> register(username)))
      case false => Result(resp, 400, 5)
    }
  }
}
