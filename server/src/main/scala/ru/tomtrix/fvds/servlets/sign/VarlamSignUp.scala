package ru.tomtrix.fvds.servlets.sign

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.AuthManager._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Registration servlet
 */
@WebServlet(urlPatterns = Array("/sign/up"), asyncSupported = true)
class VarlamSignUp extends VarlamServlet {
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = async(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val (save, uname) = (for {
      username <- json get "username" flatMap {_.toString.asOption}
      password <- json get "password" flatMap {_.toString.asOption}
    } yield {
      logger debug s"User $username is gonna sign up"
      val uname = username.replaceAll("$", "")
      val future = json.getOrElse("client", "").toString.str match {
        case "web"     => saveHash(uname, password)
        case "android" => saveHash(uname, password)
        case _         => savePassword(uname, password)
      }
      future -> uname
    }) getOrElse Future.failed(new Exception("Username or password is empty")) -> ""
    save map {
      case true => Result(resp, 200, 0, Map("token" -> register(uname)))
      case false => Result(resp, 400, 2)
    }
  }
}
