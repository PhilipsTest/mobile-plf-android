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

package com.philips.platform.ecs.microService.request

import com.android.volley.Request
import com.philips.platform.appinfra.rest.request.JsonObjectRequest
import com.philips.platform.ecs.microService.callBack.ECSCallback
import com.philips.platform.ecs.microService.error.ECSError

abstract class TestAbstractGeneric(ecsErrorCallback: ECSCallback<*, ECSError>) : ECSJsonRequest(ecsErrorCallback){

}