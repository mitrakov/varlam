package ru.tomtrix.fvds.servlets.person

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.db.Operation
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Removes a person
 */
@WebServlet(urlPatterns = Array("/person/delete"))
class VarlamDeletePerson extends VarlamServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val result = for {
      personName <- req.getParameter("name").asOption
      user <- getUser(req.getHeader("username").str)
      person <- getPerson(user, personName)
    } yield {
      val m = Map("x" -> person.getPersonId)
      dao.findBySQL("SELECT * FROM operation WHERE person_id = :x", classOf[Operation], m) {identity}.size
    }
    result match {
      case Some(0) => Result(resp, 200, 0, Map("msg" -> "it's safe to remove an item")).write()
      case Some(x) => Result(resp, 200, 44, Map("operations" -> x)).write()
      case None =>    Result(resp, 402, 43).write()
    }
  }

   override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
     val json = req.getReader.readLine.asJsonObject
     val result = for {
       name <- json get "name" map {_.toString.str}
       user <- getUser(req.getHeader("username").str)
       person <- getPerson(user, name)
     } yield {
       dao remove person
       getOperations(user) foreach {op => operationCache removeFromCache op.getOperationId}
       name
     }
     result match {
       case Some(x) => Result(resp, 200, 0, Map("msg" -> s"Person $x deleted")).write()
       case None => Result(resp, 400, 42).write()
     }
   }
 }
