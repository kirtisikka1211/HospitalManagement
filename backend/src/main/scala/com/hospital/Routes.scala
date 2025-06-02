// src/main/scala/com/hospital/Routes.scala
package com.hospital

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import akka.http.scaladsl.model.HttpMethods._
import com.hospital.DoctorRoutes
import com.hospital.PharmacyRoutes
import com.hospital.AppointmentRoutes
import com.hospital.StaffRoutes


object Routes {
  val corsSettings = CorsSettings.defaultSettings.withAllowedMethods(Seq(GET, POST, PUT, DELETE, OPTIONS))

  val allRoutes: Route = cors(corsSettings) {
    concat(
      DoctorRoutes.route,
      AppointmentRoutes.route,
      PharmacyRoutes.route,
      StaffRoutes.route
    )
  }
}
