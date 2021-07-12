package ru.tomtrix.fvds.servlets

import javax.servlet.http._
import scala.util._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.AuthManager._
import ru.tomtrix.fvds.CaseClasses.Result

/**
 * Superclass for all servlets. Provides additional functions like
 * [[ru.tomtrix.fvds.servlets.VarlamServlet#async async]] and
 * [[ru.tomtrix.fvds.servlets.VarlamServlet#authenticated authenticated]]
 * <br>Ensure that a servlet '''MUST''' extend this trait
 */
trait VarlamServlet extends HttpServlet {

  /**
   * Performs the operation only if a user is registered by [[ru.tomtrix.fvds.AuthManager#register register]] method
   * @param req HttpServletRequest
   * @param resp HttpServletResponse
   * @param f code
   */
  def authenticated(req: HttpServletRequest, resp: HttpServletResponse)(f: => Unit): Option[Unit] = safe {
    if (isRegistered(req.getHeader("username").str, req.getHeader("token").str)) f
    else Result(resp, 401, 4).write()
  }

  /**
   * Performs an asynchronous operation. Ensure that if you use asynchronous code inside a servlet handler then you
   * '''MUST''' wrap it into '''async''' and annotate with [[javax.servlet.annotation.WebServlet WebServlet]]
   * annotation with a value __asyncSupported = true__. Otherwise asynchronous code won't work correctly
   * @param req HttpServletRequest
   * @param resp HttpServletResponse
   * @param f code
   */
  def async(req: HttpServletRequest, resp: HttpServletResponse)(f: => Future[Result]): Option[Unit] = safe {
    if (req.isAsyncSupported) {
      req.startAsync()
      val context = req.getAsyncContext
      context setTimeout 3000L
      f onComplete {
        case Success(result) =>
          result.write()
          context.complete()
        case Failure(e) =>
          logger error e.getMessage
          Result(resp, 500, 1, Map("msg" -> e.getMessage)).write()
          context.complete()
      }
    }
    else logger error "Async is not supported"
  }

  /**
   * Destroys the servlet and closes Entity Manager Factory
   */
  override def destroy(): Unit = {
    if (emf.isOpen) {
      emf.close()
      logger info "Shutting down application"
    }
    super.destroy()
  }
}
