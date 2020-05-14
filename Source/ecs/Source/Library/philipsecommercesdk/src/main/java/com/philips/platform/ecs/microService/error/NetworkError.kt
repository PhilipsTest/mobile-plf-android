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

class NetworkError {


    fun getECSError(volleyError: VolleyError) : ECSError{

       var ecsError = ECSError("",null,null)

        when(volleyError){

            is NoConnectionError -> ""
            is TimeoutError -> ""
            is AuthFailureError -> ""
            is NetworkError -> ""
            is ParseError -> ""
            is ServerError ->""

        }


        return ecsError
    }

}