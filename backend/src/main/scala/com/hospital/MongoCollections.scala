// src/main/scala/com/hospital/MongoCollections.scala
package com.hospital

import org.mongodb.scala._

object MongoCollections {
  val mongoClient: MongoClient = MongoClient("mongodb://127.0.0.1:27017")
  val database: MongoDatabase = mongoClient.getDatabase("hospital_db_new")

  val doctorCollection: MongoCollection[Document] = database.getCollection("doctors")
  val appointmentCollection: MongoCollection[Document] = database.getCollection("appointments")
  val staffCollection: MongoCollection[Document] = database.getCollection("staff")
  val pharmacyCollection: MongoCollection[Document] = database.getCollection("pharmacy")
}
