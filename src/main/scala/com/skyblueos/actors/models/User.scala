package com.skyblueos.actors.models

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.skyblueos.actors.database.DatabaseUtils
import com.softwaremill.session.{MultiValueSessionSerializer, SessionSerializer, SingleValueSessionSerializer}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.Try

final case class User(email: String, password: String)
class UserActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case User(email, _) => log.info(s"Hello! This message is from ${email}")
    case Chat(sender, receiver, message) => println(s"Message ${message},was sent by ${sender} and received by ${receiver}")
      DatabaseUtils.saveChat(Chat(sender, receiver, message))
  }
}
trait UserJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit def serializer: SessionSerializer[User, String] =
    new MultiValueSessionSerializer(user => Map[String, String](user.email -> user.password),
      map =>
        Try {
          User(map.head._1, map.head._2)
        })
}
