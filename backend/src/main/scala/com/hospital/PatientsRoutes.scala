// src/main/scala/com/hospital/PatientRoutes.scala
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

object PatientRoutes {
  import MongoCollections._

  def docFromPatient(p: Patient): Document =
    Document("id" -> p.id, "name" -> p.name, "age" -> p.age, "gender" -> p.gender)

  val route = pathPrefix("patients") {
    concat(
      post {
        entity(as[Patient]) { patient =>
          patientCollection.insertOne(docFromPatient(patient)).toFuture()
          complete(StatusCodes.Created, s"Patient ${patient.name} added.")
        }
      },
      path(Segment) { id =>
        concat(
          get {
            complete {
              patientCollection.find(equal("id", id)).first().toFuture().map(_.toJson())
            }
          },
          put {
            entity(as[Patient]) { updated =>
              patientCollection.updateOne(equal("id", id), combine(
                set("name", updated.name),
                set("age", updated.age),
                set("gender", updated.gender)
              )).toFuture()
              complete(StatusCodes.OK, s"Patient $id updated.")
            }
          },
          delete {
            patientCollection.deleteOne(equal("id", id)).toFuture()
            complete(StatusCodes.OK, s"Patient $id deleted.")
          }
        )
      },
      pathEndOrSingleSlash {
        get {
          complete {
            patientCollection.find().toFuture().map(_.map(_.toJson()))
          }
        }
      }
    )
  }
}
