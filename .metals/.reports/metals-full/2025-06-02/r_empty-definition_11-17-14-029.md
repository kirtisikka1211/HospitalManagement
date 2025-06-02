error id: file://<WORKSPACE>/backend/src/main/scala/com/hospital/Main.scala:`<none>`.
file://<WORKSPACE>/backend/src/main/scala/com/hospital/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -akka/actor/ActorSystem#
	 -akka/http/scaladsl/server/Directives.ActorSystem#
	 -io/circe/generic/auto/ActorSystem#
	 -de/heikoseeberger/akkahttpcirce/FailFastCirceSupport.ActorSystem#
	 -org/mongodb/scala/ActorSystem#
	 -org/mongodb/scala/model/Filters.ActorSystem#
	 -org/mongodb/scala/model/Updates.ActorSystem#
	 -org/mongodb/scala/model/ActorSystem#
	 -ch/megard/akka/http/cors/scaladsl/CorsDirectives.ActorSystem#
	 -akka/http/scaladsl/model/HttpMethods.ActorSystem#
	 -ActorSystem#
	 -scala/Predef.ActorSystem#
offset: 1031
uri: file://<WORKSPACE>/backend/src/main/scala/com/hospital/Main.scala
text:
```scala
package com.hospital

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import akka.http.scaladsl.model.HttpMethods._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorS@@ystem = ActorSystem("hospital-management-system")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val routes: Route = Routes.allRoutes

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)
    println("Hospital Management System online at http://localhost:8080/")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.