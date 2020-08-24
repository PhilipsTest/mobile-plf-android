package com.philips.platform.ecs.microService.model.filter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProductFilter(var sortType: ECSSortType?, var stockLevelList: List<ECSStockLevel>) : Parcelable

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


