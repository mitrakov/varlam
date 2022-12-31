package ru.tomtrix.fvds.servlets.person

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Changes a person
 */
@WebServlet(urlPatterns = Array("/person/change"))
class VarlamChangePerson extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val result = for {
      name <- json get "name" map {_.toString.str}
      newName <- json get "newName" map {_.toString.str}
      user <- getUser(req.getHeader("username").str)
      person <- getPerson(user, name)
    } yield {
      person setName newName
      dao merge person
      getOperations(user) foreach {op => operationCache removeFromCache op.getOperationId}
      name -> newName
    }
    result match {
      case Some((x, y)) => Result(resp, 200, 0, Map("msg" -> s"Person $x renamed to $y")).write()
      case None => Result(resp, 400, 41).write()
    }
  }
}
