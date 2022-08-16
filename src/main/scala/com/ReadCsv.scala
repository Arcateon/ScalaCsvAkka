package com

import com.Cases.{SalesById, SalesByIdError, TotalSales}
import com.github.tototoshi.csv.CSVWriter
import org.json4s.Formats
import org.json4s.jackson.Serialization

import java.io.FileNotFoundException
import scala.io.{BufferedSource, Source}
import scala.collection.mutable.ArrayBuffer


object ReadCsv extends App {

  def salesByRegion(region: String): String = {
    try {
      var sum = 0
      var count = 0
      val bufferedSource: BufferedSource = io.Source
        .fromFile("src/testData.csv")
      for (line <- bufferedSource.getLines()) {
        if (line.contains(region.capitalize)) {
          val cols = line.split(",").map(_.trim)
          val res = cols(0).toInt
          sum += res
          count += 1
        }
      }
      implicit val formats: Formats = org.json4s.DefaultFormats.withLong.withDouble.withStrictOptionParsing
      val resJson = Serialization.write(TotalSales(region, sum, count))
      resJson
    } catch {
      case _: FileNotFoundException =>
      "404"
    }
  }

  def dataById(id: String): String = {
    try {
      val bufferedSource: BufferedSource = io.Source
        .fromFile("src/testData.csv")
      var resJson = ""
      for (line <- bufferedSource.getLines()) {
        val cols = line.split(",").map(_.trim)
        if (cols(3) == id) {
          val sales = cols(0)
          val index = cols(1)
          val region = cols(2)
          val id = cols(3)

          implicit val formats: Formats = org.json4s.DefaultFormats.withLong.withDouble.withStrictOptionParsing
          resJson = Serialization.write(SalesById(sales, index, region, id))
        }
      }
      resJson
    } catch {
      case _: FileNotFoundException =>
        "404"
    }
  }

}
