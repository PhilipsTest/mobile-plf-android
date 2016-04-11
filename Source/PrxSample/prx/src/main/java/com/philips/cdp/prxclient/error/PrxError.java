package com.philips.cdp.prxclient.error;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 *
 * Created by kiran.kumar.r_1@philips.com on 05-Nov-15.
 */
public enum PrxError {

    VOLLEY_TIME_OUT(504, "Time out Exception"),
    UNKNOWN_EXCEPTION(-1, "Unknown exception"),
    NO_INTERNET_CONNECTION(9, "No internet connection"),
    NOT_FOUND(404, "The requested file was not found"),

    AUTHENTICATION_FAILURE(401, "Authentication failure when performing a Request"),
    NETWORK_ERROR(511, "Network error when performing a Volley request"),
    PARSE_ERROR(1, "Indicates that the server's response could not be parsed"),
    SERVER_ERROR(2, "Indicates that the error responded with an error response.");


    private final int id;
    private final String description;

    PrxError(final int id, final String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
