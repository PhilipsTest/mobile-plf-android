package com.philips.platform.ecs.microService.model.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ECSProducts(var commerceProducts : List<ECSProduct> ) : Parcelable {


}