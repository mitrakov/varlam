package ru.tomtrix.fvds

import java.io._
import java.net.URL
import java.util.UUID

import scala.collection.mutable.ListBuffer
import scala.compat.Platform
import scala.util.Random

/**
 * Utils
 */
object Utils {
  val rand = new Random(Platform.currentTime)

  /**
   * Wraps the code so that all the exceptions/errors will be caught.<br>
   * @param f your code
   * @param finallyFunc code that must be run in a finally clause
   * @tparam T type parameter
   * @return Option[T]
   */
  def safe[T](f: => T, catchFunc: Throwable => Unit = {System.err.println(_)}, finallyFunc: => Unit = {}): Option[T] = {
    try {
      Some(f)
    }
    catch {
      case e: Throwable => catchFunc(e); None
    }
    finally {
      finallyFunc
    }
  }

  def safeCatch[T](f: => T) (catchFunc: Throwable => Unit): Option[T] = safe(f, catchFunc)

  def getHTML(url: String): Option[String] = {
    assert(url contains "://", """Specify the protocol (i.e. "http://")""")
    safe {
      val reader = new BufferedReader(new InputStreamReader(new URL(url).openStream))
      var html = new StringBuilder()
      var loop = true
      while (loop)
        reader.readLine() match {
          case null => loop = false
          case x => html ++= Platform.EOL ++= x
        }
      reader.close()
      html.toString()
    }
  }

  /**
   * Serializes an object into a byte array
   * @param obj serializable object
   * @return byte array of a serialized object
   */
  def serialize(obj: Object): Option[Array[Byte]] = {
    safe {
      val baos = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(baos)
      oos writeObject obj
      val result = baos toByteArray()
      oos close()
      result
    }
  }

  def serialize(obj: Object, filename: String): Boolean = {
    serialize(obj) match {
      case Some(array) => safe {
          val fos = new FileOutputStream(filename)
          fos write array
          fos close()
          true
        } getOrElse false
      case None => false
    }
  }

  /**
   * Deserializes an object from a byte array
   * @param buf byte array that keeps an object
   * @return deserialized object
   */
  def deserialize[T](buf: Array[Byte]): Option[T] = {
    safe {
      val ois = new ObjectInputStream(new ByteArrayInputStream(buf))
      val result = ois.readObject().asInstanceOf[T]
      ois close()
      result
    }
  }

  def deserialize[T](filename: String, bufSize: Int = 65536): Option[T] = {
    safe {
      val buf = new Array[Byte](bufSize)
      val fis = new FileInputStream(filename)
      fis read buf
      fis close()
      buf
    } flatMap deserialize[T]
  }

  /**
   * Подробнее о методе см. <a href="http://stackoverflow.com/questions/1226555/case-class-to-map-in-scala">тута</a>
   * @param cc Case Class
   * @return
   */
  def caseclassToMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f setAccessible true
      if (!f.getName.startsWith("$")) a + (f.getName -> f.get(cc))
      a
    }

  def caseclassToList(cc: AnyRef): List[(String, Any)] =
    (ListBuffer[(String, Any)]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f setAccessible true
      if (!f.getName.startsWith("$")) a += (f.getName -> f.get(cc))
      a
    }.toList

  /**
   * @return random string containing 36 characters
   */
  def randomString = UUID.randomUUID().toString.replaceAll("-", "")
}
