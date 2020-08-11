/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.integration.serviceDiscovery

import android.view.View
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.error.ECSErrorEnum
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECutility
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener
import com.philips.platform.pif.DataInterface.MEC.listeners.MECHybrisAvailabilityListener

/*
* Implementation of MECInterface exposed API
* @since 2002.0
* */

class MECManager {

    // to be called by Proposition to check if Hybris available
    fun ishybrisavailableWorker(mECHybrisAvailabilityListener: MECHybrisAvailabilityListener) {

        MECDataHolder.INSTANCE.eCSServices.microService.configureECS(object : com.philips.platform.ecs.microService.callBack.ECSCallback<com.philips.platform.ecs.microService.model.config.ECSConfig, com.philips.platform.ecs.microService.error.ECSError> {
            override fun onResponse(result: com.philips.platform.ecs.microService.model.config.ECSConfig) {
                mECHybrisAvailabilityListener.isHybrisAvailable(result.isHybris)
            }

            override fun onFailure(ecsError: com.philips.platform.ecs.microService.error.ECSError) {
                mECHybrisAvailabilityListener.isHybrisAvailable(false)
            }
        })

    }

    var fetchCartListener: MECFetchCartListener? = null

    // to be called by Proposition getProductCartCount() API call to show cart count
    fun getProductCartCountWorker(mECFetchCartListener: MECFetchCartListener) {

        fetchCartListener = mECFetchCartListener

        MECDataHolder.INSTANCE.eCSServices.microService.configureECS(object : com.philips.platform.ecs.microService.callBack.ECSCallback<com.philips.platform.ecs.microService.model.config.ECSConfig, com.philips.platform.ecs.microService.error.ECSError> {
            override fun onResponse(result: com.philips.platform.ecs.microService.model.config.ECSConfig) {
                if (result.isHybris && null != result.rootCategory) {

                    getShoppingCartData(object : MECCartUpdateListener {
                        override fun onUpdateCartCount(count: Int) {
                            mECFetchCartListener.onGetCartCount(count)
                        }

                        override fun shouldShowCart(shouldShow: Boolean?) {
                            // do nothing
                        }
                    })
                }else{
                    mECFetchCartListener.onFailure(Exception(ECSErrorEnum.ECSHybrisNotAvailable.localizedErrorString))
                }
            }

            override fun onFailure(ecsError: com.philips.platform.ecs.microService.error.ECSError) {
                mECFetchCartListener.onFailure(Exception(ecsError.errorMessage))
            }
        })

    }

    //to be called by Catalog and Product Detail screen to show cart count
    fun getShoppingCartData(mECCartUpdateListener: MECCartUpdateListener) {
        // handle both from catalog ...detail and proposition
        // Handle user logged in status ...for direct launch to landing view

        if (MECutility.isExistingUser() && com.philips.platform.ecs.util.ECSConfiguration.INSTANCE.accessToken != null) {
            doCartCall(mECCartUpdateListener)
        } else {
            doHybrisAuthCall(mECCartUpdateListener)
        }

    }

    private fun doCartCall(mECCartUpdateListener: MECCartUpdateListener) {
        MECDataHolder.INSTANCE.eCSServices.fetchShoppingCart(object : ECSCallback<com.philips.platform.ecs.model.cart.ECSShoppingCart, Exception> {
            override fun onResponse(carts: com.philips.platform.ecs.model.cart.ECSShoppingCart?) {
                if (carts != null) {
                    val quantity = MECutility.getQuantity(carts)
                    mECCartUpdateListener.onUpdateCartCount(quantity)

                }
            }

            override fun onFailure(error: Exception, ecsError: ECSError) {
                if (MECutility.isAuthError(ecsError)) doHybrisAuthCall(mECCartUpdateListener)
            }
        })
    }

    private fun doHybrisAuthCall(mECCartUpdateListener: MECCartUpdateListener) {
        var authCallBack = object : ECSCallback<ECSOAuthData, Exception> {

            override fun onResponse(result: ECSOAuthData?) {
                getShoppingCartData(mECCartUpdateListener)
            }

            override fun onFailure(error: Exception, ecsError: ECSError) {
                fetchCartListener?.onFailure(error)
            }
        }
        HybrisAuth.hybrisAuthentication(authCallBack)
    }

}