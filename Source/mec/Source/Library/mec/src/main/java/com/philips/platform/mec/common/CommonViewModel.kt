/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.common

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.philips.cdp.di.ecs.error.ECSError
import com.philips.cdp.di.ecs.integration.ECSCallback
import com.philips.cdp.di.ecs.model.oauth.ECSOAuthData
import com.philips.platform.mec.auth.HybrisAuth

open class CommonViewModel : ViewModel() {
    val mecError = MutableLiveData<MecError>()


    var authFailCallback ={ error: Exception?, ecsError: ECSError? -> authFailureCallback(error, ecsError) }

    fun authAndCallAPIagain(retryAPIcall: () -> Unit, authFailureCallback: (Exception, ECSError) -> Unit){
        val authCallback= object: ECSCallback<ECSOAuthData, Exception> {
            override fun onResponse(result: ECSOAuthData?) {
                retryAPIcall.invoke()
            }
            override fun onFailure(error: Exception, ecsError: ECSError) {
                authFailureCallback.invoke(error,ecsError)
            }
        }
       HybrisAuth.hybrisRefreshAuthentication(authCallback)
    }

    open fun authFailureCallback(error: Exception?, ecsError: ECSError?){
        Log.v("Auth","refresh auth failed "+ecsError);
        val mecError = com.philips.platform.mec.common.MecError(error, ecsError, null)
        this.mecError.value = mecError
    }
}