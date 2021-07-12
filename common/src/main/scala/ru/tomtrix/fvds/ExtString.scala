package ru.tomtrix.fvds

import java.util.Base64
import java.security._
import java.security.spec.X509EncodedKeySpec
import scala.util.parsing.json.JSON._
import ru.tomtrix.fvds.Utils._
import ru.tomtrix.fvds.ExtOption._

/**
 * Created by Tom-Trix-NBW on 01.03.14
 */
object ExtString {
  implicit class ExtendedString(_s: String) {
    private val s = _s match {
      case null => ""
      case x => x.toString
    }

    def toMD5 = hash("MD5")
    def toSHA1 = hash("SHA-1")
    def toSHA256 = hash("SHA-256")
    def str = s.trim
    def isBlank = str.isEmpty
    def ===(other: String) = str.toLowerCase == other.str.toLowerCase
    def asJsonObject = parseFull(s).cast[Map[String, Any]] getOrElse Map.empty
    def asJsonArray = parseFull(s).cast[List[Any]] getOrElse List.empty
    def asOption = if (!isBlank) Some(str) else None
    def toPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder.decode(str)))
    def toIntOpt = safe {s.toInt}

    private def hash(algorithm: String) = {
      val messageDigest = MessageDigest getInstance algorithm
      messageDigest update s.getBytes
      new String(messageDigest.digest())
    }
  }
}