package com

import com.Cases.{AddToCsvResponse, DataListOfRegion, SalesById, TotalSales}
import com.github.tototoshi.csv.CSVWriter
import org.json4s.Formats
import org.json4s.jackson.Serialization

import java.io.FileNotFoundException
import scala.io.BufferedSource
import scala.collection.mutable.ArrayBuffer


object ReadCsv {

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
      implicit val formats: Formats = org.json4s.DefaultFormats.withLong
        .withDouble.withStrictOptionParsing
      val resJson = Serialization.write(TotalSales(region, sum, count))
      bufferedSource.close()
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

          implicit val formats: Formats = org.json4s.DefaultFormats.withLong
            .withDouble.withStrictOptionParsing
          resJson = Serialization.write(SalesById(sales, index, region, id))
        }
      }
      bufferedSource.close()
      resJson
    } catch {
      case _: FileNotFoundException =>
        "404"
    }
  }

  def dataListByRegion(region: String): String = {
    try {
      val bufferedSource: BufferedSource = io.Source
        .fromFile("src/testData.csv")

      var resultJson = ""
      val resultArray = ArrayBuffer[String]()

      for (line <- bufferedSource.getLines()) {
        if (line.contains(region.capitalize)) {
          val cols = line.split(",").map(_.trim)
          val resultString = s"{sales:${cols(0)}" +
            s", index:${cols(1)}" +
            s", region:${cols(2)}" +
            s", id:${cols(3)}}"
          resultArray += resultString
          implicit val formats: Formats = org.json4s.DefaultFormats.withLong.
            withDouble.withStrictOptionParsing
          resultJson = Serialization.write(DataListOfRegion(resultArray))
        }
      }
      bufferedSource.close()
      resultJson
    } catch {
      case _: FileNotFoundException =>
        "404"
    }
  }

  def dataListById(id: String): String = {
    try {
      val bufferedSource: BufferedSource = io.Source
        .fromFile("src/testData.csv")

      var resultJson = ""
      val resultArray = ArrayBuffer[String]()

      for (line <- bufferedSource.getLines.drop(1)) {
        val cols = line.split(",").map(_.trim)
        if (cols(3).startsWith(id)) {
          val resultString = s"{sales:${cols(0)}" +
            s", index:${cols(1)}" +
            s", region:${cols(2)}" +
            s", id:${cols(3)}}"
          resultArray += resultString
          implicit val formats: Formats = org.json4s.DefaultFormats.withLong.
            withDouble.withStrictOptionParsing
          resultJson = Serialization.write(DataListOfRegion(resultArray))
        }
      }
      bufferedSource.close()
      resultJson
    } catch {
      case _: FileNotFoundException =>
        "404"
    }
  }

  def dataListBySales(sales: String): String = {
    try {
      val bufferedSource: BufferedSource = io.Source
        .fromFile("src/testData.csv")

      var resultJson = ""
      val resultArray = ArrayBuffer[String]()

      for (line <- bufferedSource.getLines.drop(1)) {
        val cols = line.split(",").map(_.trim)
        if (cols(0).toInt > sales.toInt) {
          val resultString = s"{sales:${cols(0)}" +
            s", index:${cols(1)}" +
            s", region:${cols(2)}" +
            s", id:${cols(3)}}"
          resultArray += resultString
          implicit val formats: Formats = org.json4s.DefaultFormats.withLong.
            withDouble.withStrictOptionParsing
          resultJson = Serialization.write(DataListOfRegion(resultArray))
        }
      }
      bufferedSource.close()
      resultJson
    } catch {
      case _: FileNotFoundException =>
        "404"
      case _: NumberFormatException =>
        "405"
    }
  }

  def writeDataToCsv(sales: Int, index: Int, region: String, id: Int): String = {

    val bufferedSource: BufferedSource = io.Source
      .fromFile("src/testData.csv")
    val idArr = ArrayBuffer[Int]()
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",").map(_.trim)
      idArr += cols(3).toInt
    }

    var resultJson = ""
    if (!idArr.contains(id)) {
      val writer = CSVWriter
        .open("src/testData.csv"
          , append = true)

      writer.writeRow(List(sales, index, region, id))
      writer.close
      bufferedSource.close()
      implicit val formats: Formats = org.json4s.DefaultFormats.withLong.
        withDouble.withStrictOptionParsing
      resultJson = Serialization.write(AddToCsvResponse(success = true, "data added"))
      resultJson
    } else {
      implicit val formats: Formats = org.json4s.DefaultFormats.withLong.
        withDouble.withStrictOptionParsing
      resultJson = Serialization.write(AddToCsvResponse(success = false, "id value is already exist"))
      resultJson
    }
  }

}
