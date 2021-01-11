package com.skyblueos.actors.database

import akka.actor.Props
import com.skyblueos.actors.Routes
import com.skyblueos.actors.models.{Chat, User, UserActor}
import org.mongodb.scala.Completed
import org.mongodb.scala.model.Filters.equal

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

object DatabaseUtils {

  def saveUser(user: User): String = {
    val emailRegex = "^[a-zA-Z0-9+-._]+@[a-zA-Z0-9.-]+$"
    if(user.email.matches(emailRegex)){
      val ifUserExists: Boolean = checkIfExists(user.email)
      if(ifUserExists)
      {
        "A User with the same E-Mail already exists."
      }
      else
      {
        if(Routes.system == null){
          "Endpoints are inactive."
        }
        else {
          val future = DatabaseConfig.collection.insertOne(user).toFuture()
          Await.result(future, 60.seconds)
          Routes.system.actorOf(Props[UserActor], user.email)
          "Registration Successful. Please login at: http://localhost:9000/login"
        }
      }
    }
    else {
      "E-Mail Validation Failed"
    }
  }

  def checkIfExists(email: String): Boolean = {
    val data = Await.result(getUsers,10.seconds)
    data.foreach(user => if(user.email.equalsIgnoreCase(email)) return true)
    false
  }

  def getUsers: Future[Seq[User]] = {
    DatabaseConfig.collection.find().toFuture()
  }

  def getUsers(email: String): Future[Seq[User]] = {
    DatabaseConfig.collection.find(equal("email",email)).toFuture()
  }

  def isSuccessfulLogin(email: String, password: String): Boolean = {

    val dbFuture = DatabaseConfig.collection.find(equal("email",email)).toFuture()
    val user = Await.result(dbFuture, 10.seconds).head
    user.password.equals(password)
  }

  def saveChat(chat: Chat): Future[Completed] = {
    DatabaseConfig.collectionForChat.insertOne(chat).toFuture()
  }

  def doesAccountExist(email: String): Boolean = {

    val dbFuture = DatabaseConfig.collection.find(equal("email",email)).toFuture()
    val users = Await.result(dbFuture, 10.seconds)
    users.nonEmpty
  }
}
