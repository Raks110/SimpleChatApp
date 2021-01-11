package com.skyblueos.actors

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, entity}
import akka.http.scaladsl.server.{Directives, Route}
import com.skyblueos.actors.database.DatabaseUtils
import com.skyblueos.actors.models.{Chat, Communicate, CommunicateJsonSupport, User, UserActor, UserJsonSupport}
import com.softwaremill.session.SessionDirectives.{invalidateSession, requiredSession, setSession}
import com.softwaremill.session.SessionOptions.{refreshable, usingCookies}
import com.softwaremill.session.{InMemoryRefreshTokenStorage, SessionConfig, SessionManager}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Routes extends App with UserJsonSupport with CommunicateJsonSupport {

  //Server Pre-Requisites
  val host = "localhost"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem("Chat")
  implicit val executor: ExecutionContext = system.dispatcher

  //Session Managers
  val sessionConfig = SessionConfig.default(
    "c05ll3lesrinf39t7mc5h6un6r0c69lgfno69dsak3vabeqamouq4328cuaekros401ajdpkh60rrtpd8ro24rbuqmgtnd1ebag6ljnb65i8a55d482ok7o0nch0bfbe")
  implicit val sessionManager: SessionManager[User] = new SessionManager[User](sessionConfig)
  implicit val refreshTokenStorage: InMemoryRefreshTokenStorage[User] = new InMemoryRefreshTokenStorage[User] {
    def log(msg: String) = println(msg)
  }
  def session(user: User) = setSession(refreshable, usingCookies, user)
  val sessionVerified = requiredSession(refreshable, usingCookies)
  val sessionInvalidate = invalidateSession(refreshable, usingCookies)

  //Available Routes
  def route: Route = {
    Directives.concat(
      Directives.post {
        Directives.concat(
          Directives.path("register") {
            entity(as[User]) { user =>
              complete(DatabaseUtils.saveUser(user))
            }
          },
          Directives.path("chat") {
            entity(as[Communicate]) { message =>
              sessionVerified { session => ctx =>
                if(DatabaseUtils.doesAccountExist(message.receiver)) {
                  system.actorOf(Props[UserActor], message.receiver) ! Chat(session.email, message.receiver, message.message)
                  ctx.complete("The Message has been transmitted.")
                }
                else{
                  ctx.complete("The receiver does not seem to be registered with us.")
                }
              }
            }
          },
          Directives.path("login") {
            entity(as[User]) { user =>
              if(DatabaseUtils.isSuccessfulLogin(user.email, user.password)) {
                session(user) { ctx =>
                  ctx.complete("Session set up. You are all set to chat! Head over to: http://localhost:9000/chat")
                }
              }
              else
                complete("Account is not registered. Head over to http://localhost:9000/register")
            }
          }
        )
      },
      Directives.get{
        Directives.path("logout") {
          sessionVerified { session =>
            sessionInvalidate { ctx =>
              println(s"Logging out ${session.email}")
              ctx.complete("You have been logged out successfully. Thank you!")
            }
          }
        }
      }
    )
  }

  //Server Binding
  val binder = Http().newServerAt(host, port).bind(route)
  binder.onComplete {
    case Success(boundServer) => println(s"Listening to ${boundServer.localAddress}")
    case Failure(error) => println(s"Error: ${error.getMessage}")
  }
}
