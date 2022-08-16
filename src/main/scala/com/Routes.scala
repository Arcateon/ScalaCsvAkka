package com

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.Cases.SalesByIdError
import org.json4s.Formats
import org.json4s.jackson.Serialization


object Routes {

  implicit val formats: Formats = org.json4s.DefaultFormats.withLong.withDouble.withStrictOptionParsing

  val route: Route = {
    // sample request: /total-sales/?region=московская, /sales/?region=Чувашия
    pathPrefix("total-sales") {
      parameters("region") { (region) =>
        if (ReadCsv.salesByRegion(region) != "404") {
          complete(ReadCsv.salesByRegion(region))
        } else complete("Server error")
      }
    } ~ // sample request: /data-by-id/?id=300
      pathPrefix("data-by-id") {
        parameters("id") { (id) =>
          if (ReadCsv.dataById(id) != "") {
            complete(ReadCsv.dataById(id))
          } else if (ReadCsv.dataById(id).isEmpty) {
            complete(Serialization.write(SalesByIdError(id, message = "id value is not found")))
          } else complete("Server error")
        }
      }
  }
}
