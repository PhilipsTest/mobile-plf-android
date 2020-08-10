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
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.ecs.microService.model.error.HybrisError
import com.philips.platform.ecs.microService.util.ECSDataHolder
import com.philips.platform.ecs.microService.util.getData
import com.philips.platform.ecs.microService.util.getJsonError

class ErrorHandler {


    fun getECSError(volleyError: VolleyError?): ECSError {

        var ecsDefaultError = ECSError(ECSErrorType.ECSsomethingWentWrong.getLocalizedErrorString(), ECSErrorType.ECSsomethingWentWrong.errorCode, ECSErrorType.ECSsomethingWentWrong)
        if (volleyError == null) return ecsDefaultError

        val ecsVolleyError = ECSError(ECSErrorType.ECS_volley_error.getLocalizedErrorString(), ECSErrorType.ECS_volley_error.errorCode, ECSErrorType.ECS_volley_error)

        when (volleyError) {

            is NoConnectionError -> ecsDefaultError = ecsVolleyError
            is TimeoutError -> ecsDefaultError = ecsVolleyError
            is AuthFailureError -> {
                handleAuthError()
            }
            is NetworkError -> ecsDefaultError = ecsVolleyError
            is ParseError -> ecsDefaultError = ecsVolleyError
            is ServerError -> {
                handleServerError(volleyError, ecsDefaultError)
            }

        }
        return ecsDefaultError
    }

    private fun handleAuthError() {

    }

    private fun handleServerError(volleyError: ServerError, ecsError: ECSError) {
        val jsonErrorObject = volleyError.getJsonError()
        val pilError = jsonErrorObject?.getData(HybrisError::class.java)
        if (pilError?.errors?.size ?: 0 > 0) {
            setPILECSError(pilError, ecsError)
        }
    }

    private fun setEcsError(ecsError: ECSError, ecsErrorType: ECSErrorType) {
        ecsError.errorMessage = ecsErrorType.getLocalizedErrorString()
        ecsError.errorCode = ecsErrorType.errorCode
        ecsError.errorType = ecsErrorType
    }

    internal fun setPILECSError(pilError: HybrisError?, ecsError: ECSError) {


        val parameter = pilError?.errors?.get(0)?.source?.parameter
        val code = pilError?.errors?.get(0)?.code




        val commaSeparatedParameterString = parameter?.replace("[", "")?.replace("]", "")
        val firstFailureParameterString = commaSeparatedParameterString?.split(",")?.get(0)


        code?.let {

            val localizedStringID = "ECSPIL" + "_" + code + "_" + firstFailureParameterString
            ECSDataHolder.loggingInterface?.log(LoggingInterface.LogLevel.VERBOSE, "setPILECSError", localizedStringID)
            try {
                val ecsErrorType = ECSErrorType.valueOf(localizedStringID)
                setEcsError(ecsError, ecsErrorType)
            } catch (e: Exception) {
                try {
                    val ecsErrorType = ECSErrorType.valueOf("ECSPIL_$code")
                    setEcsError(ecsError, ecsErrorType)

                } catch (e: Exception) {

                }
            }

        }

    }

}