// src/main/scala/com/hospital/UserRoutes.scala
package com.hospital

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._
import org.mongodb.scala.Document
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object UserRoutes {
  import MongoCollections._

  def docFromUser(u: User): Document =
    Document(
      "id" -> u.id,
      "username" -> u.username,
      "email" -> u.email,
      "password" -> u.password, 
      "role" -> u.role
    )

  val route = pathPrefix("users") {
    concat(
      path("signup") {
        post {
          entity(as[User]) { user =>
            userCollection.insertOne(docFromUser(user)).toFuture()
            complete(StatusCodes.Created, s"User ${user.username} registered.")
          }
        }
      },
      path("login") {
        post {
          entity(as[LoginRequest]) { login =>
            val foundUser = userCollection.find(equal("username", login.username)).first().toFuture()
            onSuccess(foundUser) {
              case doc if doc != null =>
                val storedPassword = doc.getString("password")
                if (storedPassword == login.password) {
                  complete(StatusCodes.OK, s"Welcome, ${login.username}")
                } else {
                  complete(StatusCodes.Unauthorized, "Invalid credentials.")
                }
              case _ =>
                complete(StatusCodes.NotFound, "User not found.")
            }
          }
        }
      },
      pathEndOrSingleSlash {
        get {
          complete {
            userCollection.find().toFuture().map(_.map(_.toJson()))
          }
        }
      }
    )
  }

  final case class LoginRequest(username: String, password: String)
}
