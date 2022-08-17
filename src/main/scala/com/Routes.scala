package com

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.Cases.{DataListOfRegionError, InputData, MoreThanSalesError, SalesByIdError}
import org.json4s.Formats
import org.json4s.jackson.{JsonMethods, Serialization}


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
      } ~ // sample request: /points-by-region/?region=московская
      pathPrefix("points-by-region") {
        parameters("region") { (region) =>
          if (ReadCsv.dataListByRegion(region) != "") {
            complete(ReadCsv.dataListByRegion(region))
          } else if (ReadCsv.dataListByRegion(region).isEmpty) {
            complete(Serialization.write(DataListOfRegionError(region, message = "region value is not found")))
          } else complete("Server error")
        }
      } ~ // sample request: /starts-with/?id=30
      pathPrefix("starts-with") {
        parameters("id") { (id) =>
          if (ReadCsv.dataListById(id) != "") {
            complete(ReadCsv.dataListById(id))
          } else if (ReadCsv.dataListById(id).isEmpty) {
            complete(Serialization.write(SalesByIdError(id, message = "id value is not found")))
          } else complete("Server error")
        }
      } ~ // sample request: /more-than/?sales=6000
      pathPrefix("more-than") {
        parameters("sales") { (sales) =>
          if (ReadCsv.dataListBySales(sales) != "404" && ReadCsv.dataListBySales(sales) != "405") {
            complete(ReadCsv.dataListBySales(sales))
          } else if (ReadCsv.dataListBySales(sales) == "405") {
            complete(Serialization.write(MoreThanSalesError(sales, message = "invalid sales")))
          } else complete("Server error")
        }
      } ~
      path("add-data") {
        post {
          entity(as[String]) {
            body =>
              val requestJson = JsonMethods.parse(body)
              val request = requestJson.extract[InputData]
                complete(ReadCsv.writeDataToCsv(
                  request.sales,
                  request.index,
                  request.region,
                  request.id
                ))
          }
        }
      }
  }
}
