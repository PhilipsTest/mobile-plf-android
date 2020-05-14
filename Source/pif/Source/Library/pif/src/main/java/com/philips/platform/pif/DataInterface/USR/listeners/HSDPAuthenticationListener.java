package com.philips.platform.pif.DataInterface.USR.listeners;

/**
 * Callback for HSDP Authentication
 *
 * @deprecated since 1903
 */
//@Deprecated
public interface HSDPAuthenticationListener {

    /**
     * Callback when HSDP login success happened
     *
     * @since 1804.0
     */
     void onHSDPLoginSuccess();

    /**
     * Callback when HSDP failure success happened
     *
     * @since 1804.0
      */
     void onHSDPLoginFailure(int errorCode, String msg);
}
