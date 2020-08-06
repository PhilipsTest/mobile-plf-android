package com.philips.cdp.prxclient.response

import org.json.JSONObject

/**
 * An abstract base class for all response model classes. It is a parent class which is used while returning back the response to clients after which clients will typecast to respective model class. All model classes also implements abstract method called parseResponse for parsing the response.
 * @since 1.0.0
 */
abstract class ResponseData {
    /**
     * Parsing the JSON object response.
     * @param response JSON Object response
     * @return ResponseData
     * @since 1.0.0
     */
    abstract fun parseJsonResponseData(response: JSONObject?): ResponseData?
}