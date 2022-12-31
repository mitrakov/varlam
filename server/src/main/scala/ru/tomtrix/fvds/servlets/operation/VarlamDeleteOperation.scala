package ru.tomtrix.fvds.servlets.operation

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Removes an operation
 */
@WebServlet(urlPatterns = Array("/operation/delete"))
class VarlamDeleteOperation extends VarlamServlet {
   override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
     val json = req.getReader.readLine.asJsonObject
     val result = for {
       id <- json get "id" map {_.asInstanceOf[Double].toLong}
       operation <- getOperation(id)
     } yield {
       dao remove operation
       operationCache removeFromCache id
       id
     }
     result match {
       case Some(x) => Result(resp, 200, 0, Map("msg" -> s"Operation $x deleted")).write()
       case None => Result(resp, 400, 32).write()
     }
   }
 }
