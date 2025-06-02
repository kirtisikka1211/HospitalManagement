// src/main/scala/com/hospital/AppointmentRoutes.scala
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


object AppointmentRoutes {
  import MongoCollections._

  def docFromAppointment(a: Appointment): Document =
    Document("id" -> a.id, "patientId" -> a.patientId, "doctorId" -> a.doctorId, "date" -> a.date, "time" -> a.time)

  val route = pathPrefix("appointments") {
    concat(
      post {
        entity(as[Appointment]) { appointment =>
          appointmentCollection.insertOne(docFromAppointment(appointment)).toFuture()
          complete(StatusCodes.Created, s"Appointment ${appointment.id} created.")
        }
      },
      path(Segment) { id =>
        concat(
          get {
            complete(appointmentCollection.find(equal("id", id)).first().toFuture().map(_.toJson()))
          },
          put {
            entity(as[Appointment]) { updated =>
              appointmentCollection.updateOne(equal("id", id), combine(
                set("patientId", updated.patientId),
                set("doctorId", updated.doctorId),
                set("date", updated.date),
                set("time", updated.time)
              )).toFuture()
              complete(StatusCodes.OK, s"Appointment $id updated.")
            }
          },
          delete {
            appointmentCollection.deleteOne(equal("id", id)).toFuture()
            complete(StatusCodes.OK, s"Appointment $id deleted.")
          }
        )
      },
      pathEndOrSingleSlash {
        get {
          complete(appointmentCollection.find().toFuture().map(_.map(_.toJson())))
        }
      }
    )
  }
}
