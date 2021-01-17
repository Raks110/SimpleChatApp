package com.skyblueos.actors.models

import com.skyblueos.actors.Routes.{StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat

final case class LoginRequest(email: String, password: String)
trait LoginRequestJsonSupport {
  implicit val loginFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
}