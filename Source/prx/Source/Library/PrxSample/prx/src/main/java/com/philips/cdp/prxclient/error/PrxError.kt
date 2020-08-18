package com.philips.cdp.prxclient.error

/**
 * Prx Error Handle Class
 *
 */
class PrxError(val description: String?, val statusCode: Int) {

    enum class PrxErrorType(val id: Int, val description: String) {
        TIME_OUT(504, "Time out Exception"),
        UNKNOWN_EXCEPTION(-1, "Unknown exception"),
        NO_INTERNET_CONNECTION(9, "No internet connection"),
        NOT_FOUND(404, "The requested file was not found"),
        AUTHENTICATION_FAILURE(401, "Authentication failure when performing a Request"),
        NETWORK_ERROR(511, "Network error when performing a request"),
        PARSE_ERROR(1, "Indicates that the server's response could not be parsed"),
        INJECT_APPINFRA(3, "You must inject AppInfra into PRX"),
        SERVER_ERROR(2, "Indicates that the error responded with an error response.");

    }

}