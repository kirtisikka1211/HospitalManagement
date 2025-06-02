// src/main/scala/com/hospital/DoctorRoutes.scala
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

object DoctorRoutes {
  import MongoCollections._

  def docFromDoctor(d: Doctor): Document =
    Document("id" -> d.id, "name" -> d.name, "specialty" -> d.specialty)

  val route = pathPrefix("doctors") {
    concat(
      post {
        entity(as[Doctor]) { doctor =>
          doctorCollection.insertOne(docFromDoctor(doctor)).toFuture()
          complete(StatusCodes.Created, s"Doctor ${doctor.name} added.")
        }
      },
      path(Segment) { id =>
        concat(
          get {
            complete {
              doctorCollection.find(equal("id", id)).first().toFuture().map(_.toJson())
            }
          },
          put {
            entity(as[Doctor]) { updated =>
              doctorCollection.updateOne(equal("id", id), combine(
                set("name", updated.name),
                set("specialty", updated.specialty)
              )).toFuture()
              complete(StatusCodes.OK, s"Doctor $id updated.")
            }
          },
          delete {
            doctorCollection.deleteOne(equal("id", id)).toFuture()
            complete(StatusCodes.OK, s"Doctor $id deleted.")
          }
        )
      },
      pathEndOrSingleSlash {
        get {
          complete {
            doctorCollection.find().toFuture().map(_.map(_.toJson()))
          }
        }
      }
    )
  }
}
