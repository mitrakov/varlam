package ru.tomtrix.fvds.servlets.person

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.db.Person
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Creates a new person
 */
@WebServlet(urlPatterns = Array("/person/new"))
class VarlamNewPerson extends VarlamServlet {
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      name <- json get "name" flatMap {_.toString.asOption}
      user <- getUser(req.getHeader("username").str)
    } yield {dao persist new Person(user, name); name}
    result match {
      case Some(x) => Result(resp, 200, 0, Map("msg" -> s"Person $x saved")).write()
      case None => Result(resp, 400, 40).write()
    }
  }
}