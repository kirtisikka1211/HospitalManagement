// src/main/scala/com/hospital/StaffRoutes.scala
package com.hospital

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._
import org.mongodb.scala.Document
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global


object StaffRoutes {
  import MongoCollections._

  def docFromStaff(s: Staff): Document =
    Document("id" -> s.id, "name" -> s.name, "role" -> s.role, "department" -> s.department)

  val route = pathPrefix("staff") {
    concat(
      post {
        entity(as[Staff]) { staff =>
          staffCollection.insertOne(docFromStaff(staff)).toFuture()
          complete(StatusCodes.Created, s"Staff ${staff.name} added.")
        }
      },
      path(Segment) { id =>
        concat(
          get {
            complete(staffCollection.find(equal("id", id)).first().toFuture().map(_.toJson()))
          },
          put {
            entity(as[Staff]) { updated =>
              staffCollection.updateOne(equal("id", id), combine(
                set("name", updated.name),
                set("role", updated.role),
                set("department", updated.department)
              )).toFuture()
              complete(StatusCodes.OK, s"Staff $id updated.")
            }
          },
          delete {
            staffCollection.deleteOne(equal("id", id)).toFuture()
            complete(StatusCodes.OK, s"Staff $id deleted.")
          }
        )
      },
      pathEndOrSingleSlash {
        get {
          complete(staffCollection.find().toFuture().map(_.map(_.toJson())))
        }
      }
    )
  }
}
