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

package com.philips.platform.ecs.microService.util

import com.google.gson.Gson
import org.json.JSONObject

fun<T> JSONObject.getData(classOfT: Class<T>) : T{
    return Gson().fromJson(this.toString(),classOfT)
}