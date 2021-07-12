package ru.tomtrix.fvds

import java.security._
import javax.crypto._
import javax.crypto.spec.SecretKeySpec
import ru.tomtrix.fvds.Utils._
import java.util.Base64

sealed trait Encrypt {
  private val cipher = Cipher.getInstance("DESede")
  protected var keyOpt: Option[SecretKey]

  def encrypt(array: Array[Byte]): Option[Array[Byte]] = keyOpt map { key =>
    cipher.init(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(array)
  }

  def encrypt(s: String): Option[Array[Byte]] = encrypt(s.getBytes("UTF-8"))

  def encrypt(obj: Object): Option[Array[Byte]] = serialize(obj) flatMap encrypt

  def decryptArray(array: Array[Byte]): Option[Array[Byte]] = keyOpt map { key =>
    cipher.init(Cipher.DECRYPT_MODE, key)
    cipher.doFinal(array)
  }

  def decryptString(array: Array[Byte]): Option[String] = decryptArray(array) map {new String(_, "UTF-8")}

  def decryptObj[T](array: Array[Byte]) = decryptArray(array) flatMap {array => deserialize[T](array)}
}

object Encrypt {
  implicit class PublicKeyToStr(key: PublicKey) {
    def toBase64Str = Base64.getEncoder.encodeToString(key.getEncoded)
  }
}

/**
 * Created by Tommy on 4/9/14
 */
class EncryptConsumer extends Encrypt {
  override protected var keyOpt: Option[SecretKey] = None
  private val keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
  
  def getPublicKey = keyPair.getPublic

  def setSymmetricKey(encryptedKey: Array[Byte]) {
    val asymmetric = Cipher.getInstance("RSA")
    asymmetric.init(Cipher.PRIVATE_KEY, keyPair.getPrivate)
    val keyBytes = asymmetric.doFinal(encryptedKey)
    keyOpt = Some(new SecretKeySpec(keyBytes, "DESede"))
  }
}

class EncryptOwner extends Encrypt {
  override protected var keyOpt: Option[SecretKey] = Some(KeyGenerator.getInstance("DESede").generateKey())
  
  def getEncryptedSymmetricKey(publicKey: PublicKey) = {
    val asymmetric = Cipher.getInstance("RSA")
    asymmetric.init(Cipher.PUBLIC_KEY, publicKey)
    asymmetric.doFinal(keyOpt.get.getEncoded)
  }
}
