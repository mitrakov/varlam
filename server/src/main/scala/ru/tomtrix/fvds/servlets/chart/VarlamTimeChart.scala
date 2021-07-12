package ru.tomtrix.fvds.servlets.chart

import java.io.File
import java.util.{Calendar, Date}
import javax.servlet.http._
import javax.servlet.annotation.WebServlet
import org.jfree.chart._
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Creates new Time-Chart and saves it on disk
 */
@WebServlet(urlPatterns = Array("/chart/time"))
class VarlamTimeChart extends VarlamServlet {
  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit = authenticated(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val url = for {
      user <- getUser(req.getHeader("username").str)
      // step = {"day", "month"}
      step <- json.get("step") map {_.toString}
      dateFrom <- json.get("from") map {s => formatter.parse(s.toString)}
      categories <- json.get("categories") map {_.asInstanceOf[List[Any]] map {s => getCategory(user, s.toString)}}
    } yield {
      val dateTo = json.get("to") map {s => formatter.parse(s.toString)} getOrElse new Date()
      // obviously categoryLst is safe from SQL-injections!
      val categoryLst = categories.flatten.map{_.getCategoryId}.mkString(",")
      /* MySQL: val sql = s"SELECT time, sum(summa) FROM operation JOIN item USING(item_id) JOIN category " +
        s"USING(category_id) WHERE category_id in ($categoryLst) AND time BETWEEN :x AND :y GROUP BY $step(time)"
        Probably $step(time) is incorrect! it might return only 31 rows!*/
      val sql = s"SELECT date_trunc('$step', time) AS day, sum(summa) FROM operation JOIN item USING(item_id) JOIN " +
        s"category USING(category_id) WHERE category_id in ($categoryLst) AND time BETWEEN :x AND :y GROUP BY day " +
        s"ORDER BY day"
      val data = dao.findBySQL(sql, Map("x" -> dateFrom, "y" -> dateTo)) { q =>
        q(0).asInstanceOf[Date] -> q(1).asInstanceOf[java.math.BigInteger/*MySQL: BigDecimal*/]
      }
      val dataset = new DefaultCategoryDataset()
      val calendar = Calendar.getInstance()
      data foreach {d =>
        calendar.setTime(d._1)
        dataset.addValue(d._2, "Динамика расходов", step match {
          case "day" => calendar.get(Calendar.DATE).toString
          case "month" => getMonth(calendar.get(Calendar.MONTH))
          case _ => d._1
        })
      }
      val chart = ChartFactory.createBarChart("Расходы", "Время", "Сумма", dataset, PlotOrientation.VERTICAL, true,
        true, true)
      val host = req.getHeader("Host")
      val today = formatter.format(new Date())
      val fname = randomString + ".png"
      new File(s"/usr/local/tomcat/webapps/guap/charts/$today").mkdirs
      ChartUtilities.saveChartAsPNG(new File(s"/usr/local/tomcat/webapps/guap/charts/$today", fname), chart, 640, 480)
      req.getRemoteHost
      s"https://$host/guap/charts/$today/$fname"
    }
    url match {
      case Some(s) => Result(resp, 200, 0, Map("url" -> s)).write()
      case None => Result(resp, 400, 51).write()
    }
  }

  def getMonth(n: Int): String = {
    List("Янв","Фев","Мар","Апр","Май","Июн","Июл","Авг","Сен","Окт","Ноя","Дек")(n)
  }
}
