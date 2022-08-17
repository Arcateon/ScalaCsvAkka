package com

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object AkkaHttp extends App {

  implicit val system: ActorSystem = ActorSystem("Scala-http")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val log: LoggingAdapter = Logging(system, "main")

  val port = 8888

  val bindingFuture =
    Http().bindAndHandle(Routes.route, "localhost", port)

  log.info(s"Server started at the port $port")

}
