package ru.tomtrix.fvds.servlets.chart

import java.io.File
import java.util.{Locale, Date}
import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import org.jfree.chart._
import org.jfree.data.general.DefaultPieDataset
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.ExtNumber._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Creates new Pie-Chart and saves it on disk
 */
@WebServlet(urlPatterns = Array("/chart/pie"))
class VarlamPieChart extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val url = for {
      user <- getUser(req.getHeader("username").str)
      month <- json.get("month") map {_.asInstanceOf[Double].toInt}
      year <- json.get("year") map {_.asInstanceOf[Double].toInt}
    } yield {
      /*MySQL:
      val sql = "SELECT category.name, sum(summa) AS s FROM operation JOIN item USING(item_id) JOIN category USING" +
        "(category_id) WHERE operation.user_id = :x AND month(time) = :y AND year(time) = :z " +
        "GROUP BY category.name ORDER BY s DESC"*/
      val sql = "SELECT category.name, sum(summa) AS s FROM operation JOIN item USING(item_id) JOIN category USING" +
        "(category_id) WHERE operation.user_id = :x AND EXTRACT(MONTH FROM time) = :y AND EXTRACT(YEAR FROM time) = :z " +
        "GROUP BY category.name ORDER BY s DESC"
      val data = dao.findBySQL(sql, Map("x" -> user.getUserId, "y" -> month, "z" -> year)) { q =>
        //MySQL: q(0).asInstanceOf[String] -> q(1).asInstanceOf[java.math.BigDecimal].toBigInteger.intValue
        q(0).asInstanceOf[String] -> q(1).asInstanceOf[java.math.BigInteger].intValue
      }
      val total = data.map{_._2}.sum
      val dataset = new DefaultPieDataset()
      data foreach { d =>
        val % = 100d * d._2 / total
        if (% >= 1)
          dataset.setValue(s"${d._1} (${% rounded 1}%)", %)
      }
      val chart = ChartFactory.createPieChart("Соотношение расходов", dataset, true, true, Locale.JAPAN)
      val host = req.getHeader("Host")
      val today = formatter.format(new Date())
      val fname = randomString + ".png"
      new File(s"/usr/local/tomcat/webapps/guap/charts/$today").mkdirs
      ChartUtilities.saveChartAsPNG(new File(s"/usr/local/tomcat/webapps/guap/charts/$today", fname), chart, 640, 480)
      s"http://$host/guap/charts/$today/$fname"
    }
    url match {
      case Some(s) => Result(resp, 200, 0, Map("url" -> s)).write()
      case None => Result(resp, 400, 50).write()
    }
  }
}
