package ru.tomtrix.fvds

/**
 * Localization
 */
object L18n {
  def getDescription(code: Int): Map[String, String] = code match {
      // OK
    case 0 => Map.empty[String, String]
      // errors with Exception message
    case 1 => Map.empty[String, String]
      // server and user errors
    case 2 => Map("msg" -> "Server error")
    case 3 => Map("msg" -> "Future error")
    case 4 => Map("msg" -> "Unauthorized")
    case 5 => Map("msg" -> "Access denied")
    case 6 => Map("msg" -> "User not found")
    case 7 => Map("msg" -> "Cannot create a temp user")
      // category errors
    case 10 => Map("msg" -> "Fail to save category. Maybe 'name' or 'parent' is empty? Even though parent is NULL specify the empty field anyway")
    case 11 => Map("msg" -> "Fail to change a category. Maybe 'name', 'newName' or 'newParentName' is empty? Even though parent is NULL specify the empty field anyway")
    case 12 => Map("msg" -> "Fail to delete a category. Maybe 'name' is empty?")
    case 13 => Map("msg" -> "Fail to find references to a category. Maybe 'name' is empty?")
    case 14 => Map("msg" -> "Removing will cause some dependent categories to be also removed")
    case 15 => Map("msg" -> "Removing will cause some dependent items to be also removed")
    case 16 => Map("msg" -> "Removing will cause some dependent categories & items to be also removed")
      // item errors
    case 20 => Map("msg" -> "Fail to save an item. Maybe 'name' or 'category' is empty?")
    case 21 => Map("msg" -> "Fail to change an item. Maybe 'name', 'newName' or 'newCategoryName' is empty?")
    case 22 => Map("msg" -> "Fail to delete an item. Maybe 'name' is empty?")
    case 23 => Map("msg" -> "Fail to find references to an item. Maybe 'name' is empty?")
    case 24 => Map("msg" -> "Removing will cause some dependent operations to be also removed")
      // operation errors
    case 30 => Map("msg" -> "Fail to save an operation. Maybe 'itemName', 'personName' or 'summa' is empty? Even though 'personName' is NULL specify empty field anyway")
    case 31 => Map("msg" -> "Fail to change an operation. Maybe 'id', 'newItemName', 'newPersonName' or 'newSumma' is empty? Even though new person is NULL specify empty field anyway")
    case 32 => Map("msg" -> "Fail to delete an operation. Maybe 'id' is empty?")
    case 33 => Map("msg" -> "Operation not found. Maybe 'id' is empty?")
      // person errors
    case 40 => Map("msg" -> "Fail to save a person. Maybe 'name' is empty?")
    case 41 => Map("msg" -> "Fail to change a person. Maybe 'name' or 'newName' is empty?")
    case 42 => Map("msg" -> "Fail to delete a person. Maybe 'name' is empty?")
    case 43 => Map("msg" -> "Fail to find references to a person. Maybe 'name' is empty?")
    case 44 => Map("msg" -> "Removing will cause some dependent operations to be also removed")
      // chart errors
    case 50 => Map("msg" -> "Fail to parse parameters. Specify 'month' and 'year'")
    case 51 => Map("msg" -> "Fail to parse parameters. Specify 'step', 'from' and 'categories'")
      // query errors
    case 52 => Map("msg" -> "Fail to parse parameters. Required parameters: 'from' and 'to'. Optional parameters: 'function' (SUM, MIN, MAX, AVG). Required headers: 'username'")
      //
    case _ => Map.empty[String, String]
  }
}
