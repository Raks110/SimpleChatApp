package com.skyblueos.actors.users

import com.skyblueos.actors.models.User
import xyz.wiedenhoeft.scalacrypt.{SymmetricKeyArbitrary, toCanBuildKeyOp}
import xyz.wiedenhoeft.scalacrypt.khash.HmacSHA256

object EncryptionManager {

  /**
   *
   * @param user contains the email and password- the password is encrypted using the corresponding email
   * @return encrypted password which is a Seq[Byte] converted to String
   */
  def encrypt(user: User): String = {

    val email: String = user.email
    val password: String = user.password

    val passwordKey: SymmetricKeyArbitrary = email.getBytes.toSeq.toKey[SymmetricKeyArbitrary].get
    val mac: Seq[Byte] = HmacSHA256(passwordKey, password.getBytes).get

    mac.toString
  }

  def encrypt(normalString: String, secretKey: String): String = {

    val key: SymmetricKeyArbitrary = secretKey.getBytes.toSeq.toKey[SymmetricKeyArbitrary].get
    HmacSHA256(key, normalString.getBytes).get.toString
  }

  /**
   *
   * @param user the object fetched from the database- contains email and encrypted password
   * @param enteredPassword the string entered by the user as password while logging in
   * @return true if login was successful, false otherwise
   */
  def verify(user: User, enteredPassword: String): Boolean = {

    try {
      val passwordKey = user.email.getBytes.toSeq.toKey[SymmetricKeyArbitrary].get
      val mac = HmacSHA256(passwordKey, enteredPassword.getBytes).get
      mac.toString().equals(user.password)
    }
    catch{
      case ex: Throwable => false
    }
  }
}
