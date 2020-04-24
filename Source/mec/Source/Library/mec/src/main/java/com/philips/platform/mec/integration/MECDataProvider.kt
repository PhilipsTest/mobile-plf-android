/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration


import android.content.Context
import com.philips.platform.mec.R
import com.philips.platform.mec.analytics.MECAnalytics
import com.philips.platform.mec.integration.serviceDiscovery.MECManager
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog
import com.philips.platform.pif.DataInterface.MEC.MECDataInterface
import com.philips.platform.pif.DataInterface.MEC.MECException
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECHybrisAvailabilityListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object MECDataProvider : MECDataInterface {
    private val TAG: String = MECDataProvider::class.java.simpleName

    internal var context: Context? = null

    override fun addCartUpdateListener(mecCartUpdateListener: MECCartUpdateListener?) {
        MECDataHolder.INSTANCE.mecCartUpdateListener = mecCartUpdateListener
    }

    override fun removeCartUpdateListener(mecCartUpdateListener: MECCartUpdateListener?) {
        //  TODO("not implemented")
    }

    @Throws(MECException::class)
    override fun fetchCartCount(mECFetchCartListener: MECFetchCartListener) {
        MECDataHolder.INSTANCE.initECSSDK()
        //TODO Make error checking at a common place : Pabitra
        if (MECDataHolder.INSTANCE.isInternetActive()) {
            if (MECDataHolder.INSTANCE.isUserLoggedIn()) {
                if (MECDataHolder.INSTANCE.hybrisEnabled) {
                    GlobalScope.launch {
                        val mecManager = MECManager()
                        mecManager.getProductCartCountWorker(mECFetchCartListener)
                    }
                } else {
                    MECLog.d(TAG, "Hybris is disabled")
                    throw MECException(context?.getString(R.string.mec_no_philips_shop), MECException.USER_NOT_LOGGED_IN)
                }
            } else {
                MECLog.d(TAG, "User is not logged in")
                throw MECException(context?.getString(R.string.mec_cart_login_error_message), MECException.USER_NOT_LOGGED_IN)
            }
        } else {
            MECLog.d(TAG, "No Network or Internet")
            MECAnalytics.trackInformationError(MECAnalytics.getDefaultString(context!!, R.string.mec_no_internet))
            throw MECException(context?.getString(R.string.mec_no_internet), MECException.NO_INTERNET)
        }
    }

    @Throws(MECException::class)
    override fun isHybrisAvailable(mECHybrisAvailabilityListener: MECHybrisAvailabilityListener) {
        MECDataHolder.INSTANCE.initECSSDK()
        //TODO Make error checking at a common place : Pabitra
        if (MECDataHolder.INSTANCE.isInternetActive()) {
            if (MECDataHolder.INSTANCE.hybrisEnabled) {
                GlobalScope.launch {
                    val mecManager = MECManager()
                    mecManager.ishybrisavailableWorker(mECHybrisAvailabilityListener)
                }
            } else {
                MECLog.d(TAG, "Hybris is disabled")
                throw MECException(context?.getString(R.string.mec_no_philips_shop), MECException.USER_NOT_LOGGED_IN)
            }
        } else {
            MECLog.d(TAG, "Internet not available")
            MECAnalytics.trackInformationError(MECAnalytics.getDefaultString(context!!, R.string.mec_no_internet))
            throw MECException(MECDataHolder.INSTANCE.appinfra.appInfraContext.getString(R.string.mec_no_internet), MECException.NO_INTERNET)
        }
    }

    
}