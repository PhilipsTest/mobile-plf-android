package com.philips.cdp.prxclient.datamodels.assets

import android.os.Parcelable
import com.philips.cdp.prxclient.error.PRXImageAssetFilter
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 02-Nov-15.
 */
@Parcelize
data class Assets(var asset: List<Asset> = ArrayList()) : Parcelable{
    fun getValidPRXImageAssets() : List<Asset>{
        return PRXImageAssetFilter().getValidPRXAssets(asset)
    }

}