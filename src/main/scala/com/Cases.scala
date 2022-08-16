package com

object Cases {

  case class TotalSales(region: String, sales: Int, points: Int)

  case class SalesById(sales: String, index: String, region: String, id: String)
  case class SalesByIdError(id: String, message: String)

}
