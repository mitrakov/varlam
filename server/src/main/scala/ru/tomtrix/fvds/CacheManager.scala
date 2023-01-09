package ru.tomtrix.fvds

import org.springframework.cache.annotation._
import ru.tomtrix.fvds.Starter._
import ru.tomtrix.fvds.db.Operation
import ru.tomtrix.fvds.CaseClasses.SingleOperation

/**
 * Manager to cache operations
 * <br>Injected by '''Spring Framework'''
 */
class CacheManager {
  /**
   * Fetches an operation by ID from cache or (if it is not present) from DB
   * @param id operation ID
   * @return SingleOperation wrapper
   */
  @Cacheable(Array("operation"))
  def getSingleOperation(id: Long): Option[SingleOperation] = {
    logger info s"Trying to find operation $id"
    dao.findById(id, classOf[Operation]) { op =>
      SingleOperation(op.getOperationId, formatter format op.getTime, op.getItem.getName,
        op.getItem.getCategory.getName, op.getSumma, op.getCurrency,
        Option(op.getPerson).map(_.getName), Option(op.getCurrencyRate), Option(op.getComment)
      )
    }
  }

  /**
   * Removes an operation with specified ID from cache (use it when some of SingleOperation fields are changed)
   * @param id operation ID
   */
  @CacheEvict(Array("operation"))
  def removeFromCache(id: Long): Unit = {}
}
