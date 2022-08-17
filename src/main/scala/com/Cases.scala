package com

import scala.collection.mutable.ArrayBuffer

object Cases {

  case class TotalSales(region: String, sales: Int, points: Int)

  case class SalesById(sales: String, index: String, region: String, id: String)
  case class SalesByIdError(id: String, message: String)

  case class DataListOfRegion(points: ArrayBuffer[String])
  case class DataListOfRegionError(region: String, message: String)

  case class MoreThanSalesError(sales: String, message: String)

}
