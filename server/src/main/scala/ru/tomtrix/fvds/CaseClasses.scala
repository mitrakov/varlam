package ru.tomtrix.fvds

import javax.servlet.http.HttpServletResponse
import scala.util.parsing.json.JSONObject
import ru.tomtrix.fvds.L18n._
import ru.tomtrix.fvds.Starter._

/**
 * Singleton containing simple case classes
 */
object CaseClasses {

  /**
   * Class that represents a simple HTTP result.
   * Use [[ru.tomtrix.fvds.CaseClasses.Result#write write]] method to get it done
   * @param resp HttpServletResponse
   * @param status HTTP status (e.g. 200 for OK)
   * @param code error code (0 if there's no error)
   * @param m map for JSON-formatted object
   */
  case class Result(resp: HttpServletResponse, status: Int, code: Int = 0, m: Map[String, Any] = Map.empty) {
    /**
     * Commits the HTTP result, sends response to a client & flushes the buffers
     */
    def write() {
      val json = JSONObject(getDescription(code) ++ m + ("code" -> code))
      logger debug s"""Response "$json" sent to client with status=$status """
      resp.setCharacterEncoding("UTF-8")
      resp.getWriter println json
      resp setStatus status
      resp.flushBuffer()
    }
  }

  /**
   * Simple wrapper on operation's data
   * @param id operation ID
   * @param time date in "dd-MM-yyyy" format
   * @param item item name
   * @param category category name
   * @param summa sum
   * @param person person name
   */
  case class SingleOperation(id: Long, time: String, item: String, category: String, summa: Int, person: Option[String])
}
