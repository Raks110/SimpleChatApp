name := "AkkaPractice"

version := "0.1"

scalaVersion := "2.13.4"

val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
  "com.softwaremill.akka-http-session" %% "core" % "0.6.1"
)