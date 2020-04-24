/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.philips.platform.ecs.error.ECSError
import com.philips.platform.ecs.integration.ECSCallback
import com.philips.platform.ecs.model.oauth.ECSOAuthData
import com.philips.platform.mec.auth.HybrisAuth
import com.philips.platform.mec.utils.MECDataHolder
import com.philips.platform.mec.utils.MECLog

open class CommonViewModel : ViewModel() {
    val mecError = MutableLiveData<MecError>()


    var authFailCallback = { error: Exception?, ecsError: ECSError? -> authFailureCallback(error, ecsError) }

    fun authAndCallAPIagain(retryAPIcall: () -> Unit, authFailureCallback: (Exception, ECSError) -> Unit) {
        val authCallback = object : ECSCallback<ECSOAuthData, Exception> {
            override fun onResponse(result: ECSOAuthData?) {
                retryAPIcall.invoke()
            }

            override fun onFailure(error: Exception, ecsError: ECSError) {
                authFailureCallback.invoke(error, ecsError)
            }
        }
        if (MECDataHolder.INSTANCE.refreshToken != null)
            HybrisAuth.hybrisRefreshAuthentication(authCallback)
        else HybrisAuth.refreshJainrain(authCallback)
    }

    open fun authFailureCallback(error: Exception?, ecsError: ECSError?) {
        MECLog.e("Auth", "refresh auth failed $ecsError");
        val mecError = MecError(error, ecsError, null)
        this.mecError.value = mecError
    }
}