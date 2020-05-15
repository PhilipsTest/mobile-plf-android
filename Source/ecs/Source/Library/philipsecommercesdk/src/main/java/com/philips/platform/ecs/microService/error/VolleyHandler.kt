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

package com.philips.platform.ecs.microService.error

import com.android.volley.*
import com.android.volley.NetworkError
import com.philips.platform.ecs.microService.model.error.OCCServerError
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError

class VolleyHandler {


    fun getECSError(volleyError: VolleyError?) : ECSError{

       var ecsError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(),ECSErrorType.ECSsomethingWentWrong.errorCode,ECSErrorType.ECSsomethingWentWrong)
       if(volleyError==null) return ecsError

        val ecsVolleyError = ECSError(ECSErrorType.ECS_volley_error.getLocalizedErrorString(), ECSErrorType.ECS_volley_error.errorCode, ECSErrorType.ECS_volley_error)

        when(volleyError){

            is NoConnectionError -> ecsError = ecsVolleyError
            is TimeoutError -> ecsError = ecsVolleyError
            is AuthFailureError -> {

            }
            is NetworkError -> ecsError = ecsVolleyError
            is ParseError -> ecsError = ecsVolleyError
            is ServerError ->{

                ecsError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(),ECSErrorType.ECSsomethingWentWrong.errorCode,ECSErrorType.ECSsomethingWentWrong)
                val jsonErrorObject = volleyError.getJsonError()
                //For PIL
                val pilError = jsonErrorObject?.getData(HybrisError::class.java)
                if(pilError?.errors?.size ?:0 > 0){
                    setPILECSError(pilError, ecsError)
                }

                //for OCC Hybris config TODO - to be removed
                val occError = jsonErrorObject?.getData(OCCServerError::class.java)
                if(occError?.errors?.size ?:0 > 0){

                    val localizedStringID = occError?.errors?.get(0)?.type
                    localizedStringID?.let {
                        val ecsErrorType = ECSErrorType.valueOf("ECS$localizedStringID")
                        ecsError = ECSError(ecsErrorType.getLocalizedErrorString(), ecsErrorType.errorCode, ecsErrorType)
                    }
                }

            }

        }


        return ecsError
    }

    private fun setPILECSError(pilError: HybrisError?, ecsError: ECSError) {
        val parameter = pilError?.errors?.get(0)?.source?.parameter
        val commaSeparatedParameterString = parameter?.replace("[", "")?.replace("]", "")
        val firstFailureString = commaSeparatedParameterString?.split(",")?.get(0)

        firstFailureString?.let {

            val localizedStringID = "ECS$firstFailureString"
            val ecsErrorType = ECSErrorType.valueOf(localizedStringID)
            ecsError.errorMessage = ecsErrorType.getLocalizedErrorString()
            ecsError.errorcode= ecsErrorType.errorCode
            ecsError.errorType = ecsErrorType
        }

    }

}