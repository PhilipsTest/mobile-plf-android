/*
 * Created by Darshan Pandya.
 * @itznotabug
 * Copyright (c) 2018.
 */

package com.philips.cdp.registration.ads.helper

import android.content.Context
import android.util.Log
import androidx.annotation.RestrictTo
import com.philips.cdp.registration.ads.model.App
import kotlinx.coroutines.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class JsonPullerTask2()   {


    fun launchRequest(urlSubString: String, view: Context , listener: JsonPullerListener) {

        runBlocking {
            val handler = coroutineExceptionHandler()
            GlobalScope.launch(handler) {
                val retrofitClientInstance: RestHandler = RestHandler()
                val service = retrofitClientInstance.create()
                val repositories = withContext(Dispatchers.Default) {
                    service.getTestProductsList()
                }

                withContext(Dispatchers.Default) { listener.onPostExecute(repositories.apps) }
            }
        }

    }

    private fun coroutineExceptionHandler() = CoroutineExceptionHandler { _, exception ->
        Log.d("TAG", "coroutineExceptionHandler:exception ${exception}")
        if (null != exception.message) {
        }
    }

    private fun coroutineSuccessHandler(response: List<App>?, listener: JsonPullerListener) {
        Log.d("TAG", "coroutineSuccessHandler:success$response")
        Log.d("TAG", "coroutineSuccessHandler:success${response!!.size}")


        listener.onPostExecute(response)

    }

    interface JsonPullerListener {
        fun onPostExecute(result: List<App>?)
    }
}
