package ru.tomtrix.fvds.servlets.sign

import java.util.Calendar
import javax.servlet.annotation.WebServlet
import javax.servlet.http._
import scala.concurrent.ExecutionContext.Implicits.global
import ru.tomtrix.fvds.db._
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._
import ru.tomtrix.fvds.AuthManager._
import ru.tomtrix.fvds.CaseClasses.Result
import ru.tomtrix.fvds.Utils.randomString
import ru.tomtrix.fvds.servlets.VarlamServlet

/**
 * Generates new login/password pair for a temp user (+ generates temp data)
 */
@WebServlet(urlPatterns = Array("/try"), asyncSupported = true)
class VarlamTryNow extends VarlamServlet {
  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = async(req, resp) {
    val json = req.getReader.readLine.asJsonObject
    val username = "$" + randomString
    val password = 100000 + rand.nextInt()
    val (lang, currency) = json get "lang" match {
      case Some("en") => EN -> "USD"
      case _ => RU -> "RUB"
    }
    savePassword(username, password.toString) map {
      case true =>
        generateData(username, lang, currency)
        Result(resp, 200, 0, Map("username" -> username, "password" -> password, "token" -> register(username)))
      case false => Result(resp, 400, 7)
    }
  }

  /**
   * Generates test data
   * @param username login
   * @param lang language (RU/EN)
   */
  private def generateData(username: String, lang: Lang, currency: String) {
    getUser(username) foreach { user =>

      // categories
      val auto = new Category(user, if (lang eq RU) "Автомобиль" else "Car")
      dao.persist(auto)
      val hs = new Category(user, if (lang eq RU) "Услуги ЖКХ" else "Housing Services")
      dao.persist(hs)
      val food = new Category(user, if (lang eq RU) "Продукты" else "Food")
      dao.persist(food)
      val dairy = new Category(user, if (lang eq RU) "Молочные" else "Dairy")
      dairy.setCategory(food)
      dao.persist(dairy)
      val meat = new Category(user, if (lang eq RU) "Мясные" else "Meat")
      meat.setCategory(food)
      dao.persist(meat)

      // items
      val milk = new Item(dairy, if (lang eq RU) "Молоко" else "Milk")
      dao.persist(milk)
      val cream = new Item(dairy, if (lang eq RU) "Сметана" else "Sour Cream")
      dao.persist(cream)
      val beef = new Item(meat, if (lang eq RU) "Говядина" else "Beef")
      dao.persist(beef)
      val pork = new Item(meat, if (lang eq RU) "Свинина" else "Pork")
      dao.persist(pork)
      val parking = new Item(auto, if (lang eq RU) "Стоянка" else "Parking Place")
      dao.persist(parking)
      val petrol = new Item(auto, if (lang eq RU) "Топливо" else "Petrol")
      dao.persist(petrol)
      val gas = new Item(hs, if (lang eq RU) "Газ" else "Gas Supply")
      dao.persist(gas)
      val electricity = new Item(hs, if (lang eq RU) "Электроэнергия" else "Electricity Supply")
      dao.persist(electricity)
      val water = new Item(hs, if (lang eq RU) "Водоснабжение" else "Water Supply")
      dao.persist(water)

      // operations
      val calendar = Calendar.getInstance()
      calendar.set(Calendar.DAY_OF_MONTH, 1)
      dao.persist(new Operation(gas, calendar.getTime, 55, currency))
      dao.persist(new Operation(electricity, calendar.getTime, 315, currency))
      dao.persist(new Operation(water, calendar.getTime, 445, currency))
      dao.persist(new Operation(parking, calendar.getTime, 350, currency))
      dao.persist(new Operation(petrol, calendar.getTime, 500, currency))
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      dao.persist(new Operation(pork, calendar.getTime, 205, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 2)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 3)
      dao.persist(new Operation(beef, calendar.getTime, 250, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 4)
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      dao.persist(new Operation(pork, calendar.getTime, 275, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 5)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 6)
      dao.persist(new Operation(petrol, calendar.getTime, 600, currency))
      dao.persist(new Operation(beef, calendar.getTime, 215, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 7)
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 8)
      dao.persist(new Operation(parking, calendar.getTime, 350, currency))
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 9)
      dao.persist(new Operation(pork, calendar.getTime, 300, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 10)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      dao.persist(new Operation(beef, calendar.getTime, 280, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 11)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      dao.persist(new Operation(pork, calendar.getTime, 290, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 12)
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 13)
      dao.persist(new Operation(petrol, calendar.getTime, 500, currency))
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      dao.persist(new Operation(beef, calendar.getTime, 265, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 14)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      dao.persist(new Operation(pork, calendar.getTime, 285, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 15)
      dao.persist(new Operation(parking, calendar.getTime, 350, currency))
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 16)
      dao.persist(new Operation(beef, calendar.getTime, 270, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 17)
      dao.persist(new Operation(petrol, calendar.getTime, 600, currency))
      dao.persist(new Operation(pork, calendar.getTime, 220, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 18)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 19)
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 20)
      dao.persist(new Operation(beef, calendar.getTime, 300, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 21)
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      dao.persist(new Operation(pork, calendar.getTime, 260, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 22)
      dao.persist(new Operation(parking, calendar.getTime, 350, currency))
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 23)
      dao.persist(new Operation(petrol, calendar.getTime, 500, currency))
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 24)
      dao.persist(new Operation(beef, calendar.getTime, 295, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 25)
      dao.persist(new Operation(milk,calendar.getTime, 25, currency))
      dao.persist(new Operation(pork,calendar.getTime, 210, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 26)
      dao.persist(new Operation(cream, calendar.getTime, 40, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 27)
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      dao.persist(new Operation(pork, calendar.getTime, 230, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 28)
      dao.persist(new Operation(petrol, calendar.getTime, 600, currency))
      dao.persist(new Operation(milk, calendar.getTime, 25, currency))
      dao.persist(new Operation(beef, calendar.getTime, 285, currency))
      calendar.set(Calendar.DAY_OF_MONTH, 29)
      dao.persist(new Operation(parking, calendar.getTime, 350, currency))
    }
  }

  trait Lang
  object RU extends Lang
  object EN extends Lang
}
