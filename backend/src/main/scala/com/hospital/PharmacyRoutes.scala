// src/main/scala/com/hospital/PharmacyRoutes.scala
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


object PharmacyRoutes {
  import MongoCollections._

  def docFromPharmacy(p: Pharmacy): Document =
    Document("id" -> p.id, "patientId" -> p.patientId, "doctorId" -> p.doctorId, "date" -> p.date, "medication" -> p.medication, "time" -> p.time)

  val route = pathPrefix("pharmacy") {
    concat(
      post {
        entity(as[Pharmacy]) { entry =>
          pharmacyCollection.insertOne(docFromPharmacy(entry)).toFuture()
          complete(StatusCodes.Created, s"Pharmacy entry ${entry.id} created.")
        }
      },
      path(Segment) { id =>
        concat(
          get {
            complete(pharmacyCollection.find(equal("id", id)).first().toFuture().map(_.toJson()))
          },
          put {
            entity(as[Pharmacy]) { updated =>
              pharmacyCollection.updateOne(equal("id", id), combine(
                set("patientId", updated.patientId),
                set("doctorId", updated.doctorId),
                set("date", updated.date),
                set("medication", updated.medication),
                set("time", updated.time)
              )).toFuture()
              complete(StatusCodes.OK, s"Pharmacy entry $id updated.")
            }
          },
          delete {
            pharmacyCollection.deleteOne(equal("id", id)).toFuture()
            complete(StatusCodes.OK, s"Pharmacy entry $id deleted.")
          }
        )
      },
      pathEndOrSingleSlash {
        get {
          complete(pharmacyCollection.find().toFuture().map(_.map(_.toJson())))
        }
      }
    )
  }
}
