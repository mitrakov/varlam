package ru.tomtrix.fvds.servlets.operation

import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import scala.util.parsing.json.JSONObject
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Retrieves the operation by ID
 */
@WebServlet(urlPatterns = Array("/operation/get"))
class VarlamGetOperation extends VarlamServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    req.getParameter("id").asOption map {_.toLong} flatMap operationCache.getSingleOperation match {
      case Some(operation) =>
        val json = JSONObject(Map(
          "id" -> operation.id,
          "time" -> operation.time,
          "item" -> operation.item,
          "category" -> operation.category,
          "summa" -> operation.summa,
          "currency" -> operation.currencyCode
        ) ++ operation.person.map("person" -> _) ++ operation.currencyRate.map("currencyRate" -> _))
        Result(resp, 200, 0, Map("operation" -> json)).write()
      case None => Result(resp, 404, 33).write()
    }
  }
}
