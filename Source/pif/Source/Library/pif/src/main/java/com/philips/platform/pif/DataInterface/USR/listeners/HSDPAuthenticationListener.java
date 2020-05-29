package com.philips.platform.pif.DataInterface.USR.listeners;

/**
 * Callback for HSDP Authentication
 *
 */
// Deprecated  since 1903

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
