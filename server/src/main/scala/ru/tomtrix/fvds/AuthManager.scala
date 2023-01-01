package ru.tomtrix.fvds

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import akka.util.Timeout
import akka.actor.ActorSystem
import com.redis.RedisClient
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.db.Usr
import ru.tomtrix.fvds.Finder._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.ExtString._

/**
 * Singleton handling passwords and tokens
 */
object AuthManager {
  private val K8S_REDIS_HOST = "redis-service"
  private val K8S_REDIS_PORT = 6379

  // Redis dependencies
  implicit private val system: ActorSystem = ActorSystem("redis-client")
  implicit private val executionContext: ExecutionContext = system.dispatcher
  implicit private val timeout: Timeout = Timeout(3.seconds)

  /** Redis Client */
  private val redis = new RedisClient(K8S_REDIS_HOST, K8S_REDIS_PORT, secret = Some("65418886"))

  /** Map {Username -> Token} */
  private val users = new ConcurrentHashMap[String, String]()

  /**
   * Saves new {username; password} pair
   * @param username login
   * @param hash hash of password
   */
  def saveHash(username: String, hash: String): Future[Boolean] = {
    logger debug s"Try to save $username with hash $hash"
    if (username.isBlank || hash.isBlank) Future.failed(new RuntimeException("Specify username & password"))
    else if (getUser(username).isDefined) Future.failed(new RuntimeException("Username is busy"))
    else { // it'd better wrap to a transaction
      dao.persist(new Usr(username))
      Future(redis.set(username, hash))
    }
  }

  /**
   * Checks the password
   * @param username login
   * @param hash hash of password
   * @return '''true''', if the password is correct
   */
  def checkHash(username: String, hash: String): Future[Boolean] = {
    logger debug s"Checking $username with hash $hash"
    if (username.isBlank || hash.isBlank) Future.failed(new RuntimeException("Specify username & password"))
    else Future(redis.get[String](username)) map {
      case Some(s) => s == hash.str
      case None => false
    }
  }

  /**
   * Saves new {username; password} pair
   * <br>'''NOT RECOMMENDED!''' Use [[ru.tomtrix.fvds.AuthManager#saveHash saveHash]] instead
   * @param username login
   * @param password pure password
   */
  def savePassword(username: String, password: String): Future[Boolean] = {
    logger debug s"Try to save $username with password $password"
    if (username.isBlank || password.isBlank) Future.failed(new RuntimeException("Specify username & password"))
    // TODO this toSHA256 differs from Web-Client SHA256. Possibly the solution is to convert to base64
    else saveHash(username, password.toSHA256)
  }

  /**
   * Checks the password
   * <br>'''NOT RECOMMENDED!''' Use [[ru.tomtrix.fvds.AuthManager#checkHash checkHash]] instead
   * @param username login
   * @param password pure password
   * @return '''true''', if the password is correct
   */
  def checkPassword(username: String, password: String): Future[Boolean] = {
    logger debug s"Checking $username with password $password"
    if (username.isBlank || password.isBlank) Future.failed(new RuntimeException("Specify username & password"))
    else checkHash(username, password.toSHA256)
  }

  /**
   * Registers a user for a session + generates new token
   * @param username login
   * @return token
   */
  def register(username: String): String = {
    logger debug s"Registering $username"
    if (username.isBlank) ""
    else {
      val token = randomString
      users.put(username.str, token)
      token
    }
  }

  /**
   * Checks whether a user is registered on current session
   * @param username login
   * @param token token gained by [[ru.tomtrix.fvds.AuthManager#register register]] method
   * @return '''true''', if the token is correct
   */
  def isRegistered(username: String, token: String): Boolean = {
    logger debug s"Checking $username with token $token"
    !username.isBlank && users.get(username.str).str == token.str
  }
}
