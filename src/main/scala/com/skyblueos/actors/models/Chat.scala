package com.skyblueos.actors.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class Chat(sender: String, receiver: String, message: String)
trait ChatJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val chatFormat: RootJsonFormat[Chat] = jsonFormat3(Chat)
}

case class SeqChat(seqChat: Seq[Chat])
trait SeqChatJsonSupport extends ChatJsonSupport {
  implicit val seqChatFormat: RootJsonFormat[SeqChat] = jsonFormat1(SeqChat)
}