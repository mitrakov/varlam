package ru.tomtrix.fvds

import ru.tomtrix.fvds.db._
import ru.tomtrix.fvds.Starter._

/**
 * Created by Tommy on 3/16/14
 */
object Finder extends DAO {
  /**
   * Retrieves a user by name and applies a function to it
   * @param username user name
   * @param f function applied to found user
   * @tparam T result type
   * @return Option[ResultType]
   */
  def getUserFunc[T](username: String)(f: Usr => T): Option[T] = {
    dao.findSingleBySQL("SELECT * FROM usr WHERE username = :x", classOf[Usr], Map("x" -> username)){f}
  }

  /**
   * Retrieves a person by user & name and applies a function to it
   * @param u user
   * @param name person name
   * @param f function applied to found person
   * @tparam T result type
   * @return Option[ResultType]
   */
  def getPersonFunc[T](u: Usr, name: String)(f: Person => T): Option[T] = {
    dao.findSingleBySQL("SELECT * FROM person WHERE user_id = :x AND name = :y", classOf[Person],
      Map("x" -> u.getUserId, "y" -> name)){f}
  }

  /**
   * Retrieves a category by user & name and applies a function to it
   * @param u user
   * @param categoryName category name
   * @param f function applied to found category
   * @tparam T result type
   * @return Option[ResultType]
   */
  def getCategoryFunc[T](u: Usr, categoryName: String)(f: Category => T): Option[T] = {
    dao.findSingleBySQL("SELECT * FROM category WHERE user_id = :x AND name = :y", classOf[Category],
      Map("x" -> u.getUserId, "y" -> categoryName)){f}
  }

  /**
   * Retrieves an item by user & name and applies a function to it
   * @param u user
   * @param itemName item name
   * @param f function applied to found item
   * @tparam T result type
   * @return Option[ResultType]
   */
  def getItemFunc[T](u: Usr, itemName: String)(f: Item => T): Option[T] = {
    dao.findSingleBySQL("SELECT * FROM item WHERE user_id = :x AND name = :y", classOf[Item],
      Map("x" -> u.getUserId, "y" -> itemName)){f}
  }

  /**
   * Retrieves an operation by ID and applies a function to it
   * @param id operation ID
   * @param f function applied to found operation
   * @tparam T result type
   * @return Option[ResultType]
   */
  def getOperationFunc[T](id: Long)(f: Operation => T): Option[T] = {
    dao.findById(id, classOf[Operation]){f}
  }

  /**
   * Retrieves all user's operations and applies a function to each of them
   * @param u user
   * @param f function applied to each of found operations
   * @tparam T result type
   * @return Option[ResultType]
   */
  def getOperationsFunc[T](u: Usr)(f: Operation => T): List[T] = {
    dao.findBySQL("SELECT * FROM operation WHERE user_id = :x", classOf[Operation], Map("x" -> u.getUserId)){f}
  }

  /**
   * @param username user name
   * @return user by name
   */
  def getUser(username: String): Option[Usr] = getUserFunc(username){ u => u}

  /**
   * @param id user ID
   * @return user by ID
   */
  def getUser(id: Long): Option[Usr] = dao.findById(id, classOf[Usr]){ t => t}

  /**
   * @param u user
   * @param name person name
   * @return person by name
   */
  def getPerson(u: Usr, name: String): Option[Person] = getPersonFunc(u, name){ p => p}

  /**
   * @param id person ID
   * @return person by ID
   */
  def getPerson(id: Long): Option[Person] = dao.findById(id, classOf[Person]){ t => t}

  /**
   * @param u user
   * @param categoryName category name
   * @return category by name
   */
  def getCategory(u: Usr, categoryName: String): Option[Category] = getCategoryFunc(u, categoryName){ c => c}

  /**
   * @param id category ID
   * @return category by ID
   */
  def getCategory(id: Long): Option[Category] = dao.findById(id, classOf[Category]){ t => t}

  /**
   * @param u user
   * @param itemName item name
   * @return item by name
   */
  def getItem(u: Usr, itemName: String): Option[Item] = getItemFunc(u, itemName){ i => i}

  /**
   * @param id item ID
   * @return item by name
   */
  def getItem(id: Long): Option[Item] = dao.findById(id, classOf[Item]){ t => t}

  /**
   * @param id operation ID
   * @return operation by ID
   */
  def getOperation(id: Long): Option[Operation] = getOperationFunc(id){ t => t}

  /**
   * @param u user
   * @return all user's operations
   */
  def getOperations(u: Usr): List[Operation] = getOperationsFunc(u) { o => o}
}
