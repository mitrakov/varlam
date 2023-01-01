package ru.tomtrix.fvds

import javax.persistence._
import scala.compat.Platform
import org.springframework.transaction.annotation.Transactional
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.Starter._

/**
 * Rewritten version of DAO generated by
 * <a href="http://mojo.codehaus.org/hibernate3-maven-plugin/">hibername3-maven-plugin</a>
 * <br>Injected by '''Spring Framework'''
 */
@Transactional
class DAO {
  @PersistenceContext
  private val entityManager: EntityManager = null

  /**
   * Saves an entity in DB
   * @param transientInstance entity
   */
  def persist(transientInstance: AnyRef): Option[Unit] = safe {
    val startTime = Platform.currentTime
    entityManager.persist(transientInstance)
    logger debug s"persisting done in ${Platform.currentTime - startTime} msec"
  }

  /**
   * Removes an entity from DB
   * @param persistentInstance entity
   */
  def remove(persistentInstance: AnyRef): Option[Unit] = safe {
    val startTime = Platform.currentTime
    entityManager.remove(entityManager.merge(persistentInstance))
    logger debug s"removing done in ${Platform.currentTime - startTime} msec"
  }

  /**
   * Updates an entity in DB
   * @param detachedInstance entity
   * @tparam T entity type
   * @return Option[entity]
   */
  def merge[T](detachedInstance: T): Option[T] = safe {
    val startTime = Platform.currentTime
    val result = entityManager.merge(detachedInstance)
    logger debug s"merging done in ${Platform.currentTime - startTime} msec"
    result
  }

  /**
   * Finds an entity by ID and applies a function to it
   * @param id entity ID
   * @param clazz entity class
   * @param f function applied to found entity
   * @tparam T entity type
   * @tparam V result type
   * @return Option[ResultType]
   */
  def findById[T, V](id: Long, clazz: Class[T])(f: T => V): Option[V] = safe {
    val startTime = Platform.currentTime
    val result = entityManager.find(clazz, id)
    logger debug s"getting instance by id $id done in ${Platform.currentTime - startTime} msec"
    f(result)
  }

  /**
   * Finds entities by SQL and applies a function to each of them
   * @param sql SQL-expression
   * @param clazz entity class
   * @param parameters SQL-parameters
   * @param f function applied to each of found entities
   * @tparam T entity type
   * @tparam V result type
   * @return List[ResultType]
   */
  def findBySQL[T, V](sql: String, clazz: Class[T], parameters: Map[String, Any] = Map.empty)(f: T => V): List[V] = safe {
    val startTime = Platform.currentTime
    val query = entityManager.createNativeQuery(sql, clazz)
    parameters foreach { p => query.setParameter(p._1, p._2)}
    val result = query.getResultList.toArray.map{_.asInstanceOf[T]}.map(f).toList
    logger debug s"getting instances by SQL: $sql done in ${Platform.currentTime - startTime} msec"
    result
  } getOrElse List.empty

  /**
   * Finds rows by native SQL and applies a function to each of them. This method is independent on entities
   * @param sql native SQL-expression
   * @param parameters SQL-parameters
   * @param f function applied to each of found rows
   * @tparam T result type
   * @return List[ResultType]
   */
  def findBySQL[T](sql: String, parameters: Map[String, Any] = Map.empty)(f: Array[Any] => T): List[T] = safe {
    val startTime = Platform.currentTime
    val query = entityManager.createNativeQuery(sql)
    parameters foreach { p => query.setParameter(p._1, p._2)}
    val result = query.getResultList.toArray.map{t => t.asInstanceOf[Array[Any]]}.map(f).toList
    logger debug s"getting instances by native SQL: $sql done in ${Platform.currentTime - startTime} msec"
    result
  } getOrElse List.empty

  /**
   * Finds a single entity by SQL and applies a function to it
   * @param sql SQL-expression
   * @param clazz entity class
   * @param parameters SQL-parameters
   * @param f function applied to found entity
   * @tparam T entity type
   * @tparam V result type
   * @return Option[ResultType]
   */
  def findSingleBySQL[T, V](sql: String, clazz: Class[T], parameters: Map[String, Any] = Map.empty)(f: T => V): Option[V] = safe {
    val startTime = Platform.currentTime
    val query = entityManager.createNativeQuery(sql, clazz)
    parameters foreach { p => query.setParameter(p._1, p._2)}
    val result = query.getSingleResult.asInstanceOf[T]
    logger debug s"getting instance by SQL: $sql done in ${Platform.currentTime - startTime} msec"
    f(result)
  }
}
