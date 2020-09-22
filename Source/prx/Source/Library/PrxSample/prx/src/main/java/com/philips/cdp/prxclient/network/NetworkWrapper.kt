package com.philips.cdp.prxclient.network

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.HttpHeaderParser.*
import com.philips.cdp.prxclient.PRXDependencies
import com.philips.cdp.prxclient.PrxConstants
import com.philips.cdp.prxclient.error.PrxError
import com.philips.cdp.prxclient.error.PrxError.PrxErrorType
import com.philips.cdp.prxclient.request.PrxRequest
import com.philips.cdp.prxclient.response.ResponseListener
import com.philips.platform.appinfra.logging.LoggingInterface
import com.philips.platform.appinfra.rest.request.GsonCustomRequest
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface.OnErrorListener.ERRORVALUES
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * A class which performs HTTP get, maintains request queue, handles caching etc.
 * It is responsible for interacting with any third party libraries that is used for performing network operations.
 *
 * @since 1.0.0
 */
class NetworkWrapper(private val mPrxDependencies: PRXDependencies?) {
    private val mPrxLogging: LoggingInterface?

    /**
     * Execute custom JSON request.
     *
     * @param prxRequest PRX Request
     * @param listener   Response listener
     * @since 1.0.0
     */
    fun executeCustomJsonRequest(prxRequest: PrxRequest, listener: ResponseListener?) {
        if (listener == null) {
            mPrxLogging!!.log(LoggingInterface.LogLevel.ERROR, PrxConstants.PRX_NETWORK_WRAPPER, "ResponseListener is null")
        } else {
            val responseListener = getVolleyResponseListener(prxRequest, listener)
            val errorListener = getVolleyErrorListener(listener)
            if (mPrxDependencies?.appInfra != null) {
                prxRequest.getRequestUrlFromAppInfra(mPrxDependencies.appInfra, object : PrxRequest.OnUrlReceived {
                    override fun onSuccess(url: String?) {
                        if (url != null) {
                            excuteRequest(url, prxRequest, responseListener, errorListener, listener)
                        } else {
                            listener.onResponseError(PrxError(PrxErrorType.INJECT_APPINFRA.description, PrxErrorType.INJECT_APPINFRA.id))
                        }
                    }

                    override fun onError(errorvalues: ERRORVALUES, s: String) {
                        listener.onResponseError(PrxError(PrxErrorType.UNKNOWN_EXCEPTION.description, PrxErrorType.UNKNOWN_EXCEPTION.id))
                    }
                })
            } else {
                listener.onResponseError(PrxError(PrxErrorType.INJECT_APPINFRA.description, PrxErrorType.INJECT_APPINFRA.id))
            }
        }
    }

    private fun excuteRequest(url: String, prxRequest: PrxRequest, responseListener: Response.Listener<JSONObject?>, errorListener: Response.ErrorListener, listener: ResponseListener) {
        var request: GsonCustomRequest<JSONObject?>? = null
        try {
            request = object : GsonCustomRequest<JSONObject?>(prxRequest.requestType,
                    url, null, responseListener, errorListener,
                    prxRequest.headers, prxRequest.params, null, prxRequest.body) {
                override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject?>? {
                    return try {
                        var headers = Charset.forName(parseCharset(response.headers))
                        val jsonString = String(response.data,headers)
                        var result: JSONObject? = null
                        if (jsonString.length > 0) result = JSONObject(jsonString)
                        Response.success(result,
                                parseCacheHeaders(response))
                    } catch (je: Exception) {
                        Response.error(ParseError(je))
                    }
                }
            }
            request.setRetryPolicy(DefaultRetryPolicy(
                    prxRequest.requestTimeOut,
                    prxRequest.maxRetries,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
            request.setShouldCache(true)
        } catch (e: Exception) {
            listener.onResponseError(PrxError(PrxErrorType.UNKNOWN_EXCEPTION.description, PrxErrorType.UNKNOWN_EXCEPTION.id))
        }
        if (request != null) {
            if (mPrxDependencies?.appInfra?.restClient != null) {
                try {
                    mPrxLogging?.log(LoggingInterface.LogLevel.DEBUG, PrxConstants.PRX_NETWORK_WRAPPER, " Request url - " + request.url
                            + " request headers - " + request.headers + " request type - " + request.method)
               } catch (authFailureError: AuthFailureError) {
                    authFailureError.printStackTrace()
                }
                mPrxDependencies.appInfra.restClient.requestQueue.add(request)
            } else {
                mPrxLogging!!.log(LoggingInterface.LogLevel.ERROR, PrxConstants.PRX_NETWORK_WRAPPER, "Couldn't initialise REST Client")
            }
        }
    }

    private fun getVolleyErrorListener(listener: ResponseListener): Response.ErrorListener {
        return Response.ErrorListener { error ->
            if (error != null) {
                val networkResponse = error.networkResponse
                try {
                    if (error is NoConnectionError) {
                        listener.onResponseError(PrxError(PrxErrorType.NO_INTERNET_CONNECTION.description, PrxErrorType.NO_INTERNET_CONNECTION.id))
                    } else if (error is TimeoutError) {
                        listener.onResponseError(PrxError(PrxErrorType.TIME_OUT.description, PrxErrorType.TIME_OUT.id))
                    } else if (error is AuthFailureError) {
                        listener.onResponseError(PrxError(PrxErrorType.AUTHENTICATION_FAILURE.description, PrxErrorType.AUTHENTICATION_FAILURE.id))
                    } else if (error is NetworkError) {
                        listener.onResponseError(PrxError(PrxErrorType.NETWORK_ERROR.description, PrxErrorType.NETWORK_ERROR.id))
                    } else if (error is ParseError) {
                        listener.onResponseError(PrxError(PrxErrorType.PARSE_ERROR.description, PrxErrorType.PARSE_ERROR.id))
                    } else if (networkResponse != null) {
                        listener.onResponseError(PrxError(networkResponse.toString(), networkResponse.statusCode))
                    } else listener.onResponseError(PrxError(PrxErrorType.UNKNOWN_EXCEPTION.description, PrxErrorType.UNKNOWN_EXCEPTION.id))
                } catch (e: Exception) {
                    listener.onResponseError(PrxError(PrxErrorType.UNKNOWN_EXCEPTION.description, PrxErrorType.UNKNOWN_EXCEPTION.id))
                }
            }
        }
    }

    private fun getVolleyResponseListener(prxRequest: PrxRequest,
                                          listener: ResponseListener): Response.Listener<JSONObject?> {
        return Response.Listener { response ->
            val responseData = prxRequest.getResponseData(response)
            if (responseData != null) {
                mPrxLogging!!.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_NETWORK_WRAPPER, "Successfully get Response")
                if (response != null) mPrxLogging.log(LoggingInterface.LogLevel.INFO, PrxConstants.PRX_NETWORK_WRAPPER, " Prx response is - $response")
                listener.onResponseSuccess(responseData)
            } else {
                listener.onResponseError(PrxError("Null Response", 0))
            }
        }
    }

    /**
     * NetworkWrapper constructor.
     *
     * @param prxDependencies PRX dependencies
     * @since 1.0.0
     */
    init {
        mPrxLogging = mPrxDependencies!!.mAppInfraLogging
    }
}