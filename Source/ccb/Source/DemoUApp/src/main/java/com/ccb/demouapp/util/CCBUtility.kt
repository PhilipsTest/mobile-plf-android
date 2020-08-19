/*
 * Copyright (c) Koninklijke Philips N.V., 2020
 *
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 *
 */

package com.ccb.demouapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CCBUtility {

    companion object {

         fun isNetworkAvailable(context:Context?): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }
    }
}