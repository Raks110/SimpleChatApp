package com.skyblueos.actors.models

import akka.actor.Actor
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.skyblueos.actors.database.DatabaseUtils
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class User(email: String, password: String, verificationComplete: Boolean)
class UserActor extends Actor {
  override def receive: Receive = {
    case Chat(sender, receiver, message) => println(s"Message ${message},was sent by ${sender} and received by ${receiver}")
      DatabaseUtils.saveChat(Chat(sender, receiver, message))
  }
}

trait UserJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat3(User)
}
