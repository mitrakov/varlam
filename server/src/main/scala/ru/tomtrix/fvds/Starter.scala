package ru.tomtrix.fvds

import javax.persistence.EntityManagerFactory
import java.text.SimpleDateFormat
import scala.util.Random
import scala.compat.Platform
import org.apache.log4j.Logger
import org.springframework.context.support.ClassPathXmlApplicationContext
import ru.tomtrix.fvds.Finder._

/**
 * Global application singleton
 */
object Starter {
  /** Spring XML context */
  private val factory = new ClassPathXmlApplicationContext("trix.xml")

  /** Entity Manager Factory<br>'''ATTENTION!''' it must be closed before the application shutdown */
  val emf = factory.getBean("entityManagerFactory").asInstanceOf[EntityManagerFactory]

  /** Data Access Object that provides all JPA capabilities */
  val dao = factory.getBean("dao").asInstanceOf[DAO]

  /** Cache used to store the operations */
  val operationCache = factory.getBean("operationCache").asInstanceOf[CacheManager]

  /** Datetime formatter */
  val formatter = new SimpleDateFormat("dd-MM-yyyy")

  /** Global logger */
  val logger = Logger.getLogger("mainLogger")

  /** Random generator */
  val rand = new Random(Platform.currentTime)

  logger info "Guap Server started..."
}
