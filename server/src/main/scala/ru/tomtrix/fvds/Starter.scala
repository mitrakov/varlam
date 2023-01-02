package ru.tomtrix.fvds

import javax.persistence.EntityManagerFactory
import java.text.SimpleDateFormat
import scala.util.Random
import scala.compat.Platform
import org.apache.log4j.Logger
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Starting point. Try: "`curl mitrakoff.com:8080/varlam/version`".
 *
 * 1. Use only Java 8 to build this app (example for SdkMan: "`sdk use java 8.302.08.1-amzn`")
 *
 * 2. After changes, don't forget to update version in [[ru.tomtrix.fvds.servlets.VarlamVersion VarlamVersion]].
 *
 * 3. All DAO are being updated automatically using DB schema on mitrakoff.com (run "mvn compile").
 *
 * 4. To build app, run "`mvn package`", then "`docker build -t mitrakov/guap-docker-compose .`", then "`docker push mitrakov/guap-docker-compose`"
 *
 * <br><br>
 * To debug JS application:
 *
 * - set hostname to "localhost:8080" in 'web/scripts/request.js'
 *
 * - build server app
 *
 * - run "docker-compose up"
 *
 * - import SQL data to DB
 *
 * - import auth data to Redis
 *
 * - open "main.html"
 */
object Starter {
  /** Spring XML context */
  private val factory = new ClassPathXmlApplicationContext("trix.xml")

  /** Entity Manager Factory<br>'''ATTENTION!''' it must be closed before the application shutdown */
  val emf: EntityManagerFactory = factory.getBean("entityManagerFactory").asInstanceOf[EntityManagerFactory]

  /** Data Access Object that provides all JPA capabilities */
  val dao: DAO = factory.getBean("dao").asInstanceOf[DAO]

  /** Cache used to store the operations */
  val operationCache: CacheManager = factory.getBean("operationCache").asInstanceOf[CacheManager]

  /** Datetime formatter */
  val formatter = new SimpleDateFormat("dd-MM-yyyy")

  /** Global logger */
  val logger: Logger = Logger.getLogger("mainLogger")

  /** Random generator */
  val rand = new Random(Platform.currentTime)

  logger info "Guap Server started..."
}
