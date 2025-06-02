error id: file://<WORKSPACE>/backend/src/main/scala/com/hospital/Models.scala:scala/Predef.String#
file://<WORKSPACE>/backend/src/main/scala/com/hospital/Models.scala
empty definition using pc, found symbol in pc: scala/Predef.String#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -String#
	 -scala/Predef.String#
offset: 132
uri: file://<WORKSPACE>/backend/src/main/scala/com/hospital/Models.scala
text:
```scala
// src/main/scala/com/hospital/Models.scala
package com.hospital

final case class Doctor(id: String,
 name: String, specialty: Stri@@ng)
final case class Appointment(id: String, patientId: String, doctorId: String, date: String, time: String)
final case class Staff(id: String, name: String, role: String, department: String)
final case class Pharmacy(id: String, patientId: String, doctorId: String, date: String, medication: String, time: String)

```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Predef.String#