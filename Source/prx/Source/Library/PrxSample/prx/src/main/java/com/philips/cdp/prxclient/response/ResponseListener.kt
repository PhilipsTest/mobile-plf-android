package com.philips.cdp.prxclient.response

import com.philips.cdp.prxclient.error.PrxError

/**
 * The Listener interface used by the vertical applications for getting the product,locale,category specific data from the Philips IT system.
 * It is an interface which has two methods. On successful response we return onResponseSuccess (ResponseData data) and on any error we return onResponseError (Error error)
 * @since 1.0.0
 */
interface ResponseListener {
    /**
     * Gets returned on success.
     * @param responseData The Response data
     * @since 1.0.0
     */
    fun onResponseSuccess(responseData: ResponseData?)

    /**
     * Gets returned on error.
     * @param prxError PRX error
     * @since 1.0.0
     */
    fun onResponseError(prxError: PrxError?)
}