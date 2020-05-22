package com.philips.platform.ecs.microService.model.filter

class ProductFilter {

    var sortType: ECSSortType?=null

    var stockLevel: ECSStockLevel?=null

    var modifiedSince: String?=null
}


enum class ECSStockLevel(var value: String) {
     InStock("IN_STOCK"),

     OutOfStock("OUT_OF_STOCK"),

     LowStock("LOW_STOCK");

    override fun toString(): String {
        return value
    }

}

enum class ECSSortType(var value: String) {

     topRated("topRated"),

     priceAscending("price"),

     priceDescending("-price"),

     discountPercentageAscending("discountPercentage"),

     discountPercentageDescending("-discountPercentage");


    override fun toString(): String {
        return value
    }
}


