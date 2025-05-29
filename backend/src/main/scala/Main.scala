// import akka.actor.ActorSystem
// import akka.http.scaladsl.Http
// import akka.http.scaladsl.server.Directives._
// import io.circe.generic.auto._
// import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
// import org.mongodb.scala._
// import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

// import scala.concurrent.ExecutionContextExecutor
// import scala.io.StdIn

// final case class HospitalManagement(name: String, email: String, eventName: String)

// object Main {
//   def main(args: Array[String]): Unit = {
//     implicit val system: ActorSystem = ActorSystem("event-registration-system")
//     implicit val executionContext: ExecutionContextExecutor = system.dispatcher

//     val mongoClient: MongoClient = MongoClient("mongodb://127.0.0.1:27017")
//     val database: MongoDatabase = mongoClient.getDatabase("event_db")
//     val collection: MongoCollection[Document] = database.getCollection("registrations")

//     val route =
//       cors() {
//         path("register") {
//           post {
//             entity(as[EventRegistration]) { registration =>
//               println(s"Received registration: $registration")

//               val doc: Document = Document(
//                 "name" -> registration.name,
//                 "email" -> registration.email,
//                 "eventName" -> registration.eventName
//               )

//               collection.insertOne(doc).subscribe(new Observer[Any] {
//                 override def onNext(result: Any): Unit = println("Inserted registration into DB")
//                 override def onError(e: Throwable): Unit = println("Failed to insert: " + e.getMessage)
//                 override def onComplete(): Unit = println("Insert operation completed")
//               })

//               complete("Registration received")
//             }
//           }
//         }
//       }

//     val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

//     println("Server online at http://localhost:8080/")
//     StdIn.readLine()
//     bindingFuture
//       .flatMap(_.unbind())
//       .onComplete(_ => system.terminate())
//   }
// }
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
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import akka.http.scaladsl.model.HttpMethods._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

// === Case Classes ===
final case class Doctor(id: String, name: String, specialty: String)
final case class Appointment(id: String, patientId: String, doctorId: String, date: String, time: String)

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("hospital-management-system")
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val mongoClient: MongoClient = MongoClient("mongodb://127.0.0.1:27017")
    val database: MongoDatabase = mongoClient.getDatabase("hospital_db")
    val doctorCollection: MongoCollection[Document] = database.getCollection("doctors")
    val appointmentCollection: MongoCollection[Document] = database.getCollection("appointments")

    // Helpers
    def docFromDoctor(d: Doctor): Document =
      Document("id" -> d.id, "name" -> d.name, "specialty" -> d.specialty)

    def docFromAppointment(a: Appointment): Document =
      Document("id" -> a.id, "patientId" -> a.patientId, "doctorId" -> a.doctorId, "date" -> a.date, "time" -> a.time)
      val corsSettings = CorsSettings.defaultSettings.withAllowedMethods(Seq(GET, POST, PUT, DELETE, OPTIONS))
    // === HTTP Routes ===
    val route =
      cors(corsSettings) {
        concat(
          // === Doctor CRUD ===
          pathPrefix("doctors") {
            concat(
              post {
                entity(as[Doctor]) { doctor =>
                  doctorCollection.insertOne(docFromDoctor(doctor)).subscribe(_ => ())
                  complete(StatusCodes.Created, s"Doctor ${doctor.name} added.")
                }
              },
              get {
                path(Segment) { id =>
                  complete {
                    doctorCollection.find(equal("id", id)).first().toFuture().map(_.toJson())
                  }
                }
              },
              put {
                path(Segment) { id =>
                  entity(as[Doctor]) { updated =>
                    doctorCollection.updateOne(equal("id", id), combine(
                      set("name", updated.name),
                      set("specialty", updated.specialty)
                    )).subscribe(_ => ())
                    complete(StatusCodes.OK, s"Doctor $id updated.")
                  }
                }
              },
              delete {
                path(Segment) { id =>
                  doctorCollection.deleteOne(equal("id", id)).subscribe(_ => ())
                  complete(StatusCodes.OK, s"Doctor $id deleted.")
                }
              }
            )
          },

          // === Appointment CRUD ===
          pathPrefix("appointments") {
            concat(
              post {
                entity(as[Appointment]) { appt =>
                  appointmentCollection.insertOne(docFromAppointment(appt)).subscribe(_ => ())
                  complete(StatusCodes.Created, s"Appointment ${appt.id} booked.")
                }
              },
              get {
                path(Segment) { id =>
                  complete {
                    appointmentCollection.find(equal("id", id)).first().toFuture().map(_.toJson())
                  }
                }
              },
              put {
                path(Segment) { id =>
                  entity(as[Appointment]) { updated =>
                    appointmentCollection.updateOne(equal("id", id), combine(
                      set("patientId", updated.patientId),
                      set("doctorId", updated.doctorId),
                      set("date", updated.date),
                      set("time", updated.time)
                    )).subscribe(_ => ())
                    complete(StatusCodes.OK, s"Appointment $id updated.")
                  }
                }
              },
              delete {
                path(Segment) { id =>
                  appointmentCollection.deleteOne(equal("id", id)).subscribe(_ => ())
                  complete(StatusCodes.OK, s"Appointment $id cancelled.")
                }
              }
            )
          }
        )
      }

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println("Hospital Management System online at http://localhost:8080/")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
