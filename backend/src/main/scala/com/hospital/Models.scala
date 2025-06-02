// src/main/scala/com/hospital/Models.scala
package com.hospital

final case class Doctor(id: String,
 name: String, specialty: String)
final case class Appointment(id: String, patientId: String, doctorId: String, date: String, time: String)
final case class Staff(id: String, name: String, role: String, department: String)
final case class Pharmacy(id: String, patientId: String, doctorId: String, date: String, medication: String, time: String)
final case class User(
  id: String,
  username: String,
  email: String,
  password: String,
  role: String
)

final case class LoginRequest(
  username: String,
  password: String
)

final case class Patient(
  id: String,
  name: String,
  age: Int,
  gender: String,
 
)