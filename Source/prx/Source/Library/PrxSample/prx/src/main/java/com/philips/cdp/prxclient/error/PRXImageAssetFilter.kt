/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */
package com.philips.cdp.prxclient.error

import com.philips.cdp.prxclient.datamodels.assets.Asset

class PRXImageAssetFilter {
    fun getValidPRXAssets(assets: List<Asset>): List<Asset> {
        val validAssets: MutableList<Asset> = ArrayList()
        for (asset in assets) {
            val assetType = getAssetType(asset)
            if (assetType != -1) {
                validAssets.add(asset)
            }
        }
        return validAssets
    }

    private fun getAssetType(asset: Asset): Int {
        return when (asset.type) {
            "RTP" -> RTP
            "APP" -> APP
            "DPP" -> DPP
            "MI1" -> MI1
            "PID" -> PID
            else -> -1
        }
    }

    companion object {
        private const val RTP = 1
        private const val APP = 2
        private const val DPP = 3
        private const val MI1 = 4
        private const val PID = 5
    }
}