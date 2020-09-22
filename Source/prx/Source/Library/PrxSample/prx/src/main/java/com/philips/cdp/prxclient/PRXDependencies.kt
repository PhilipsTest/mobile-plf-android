package com.philips.cdp.prxclient

import android.content.Context
import com.philips.platform.appinfra.AppInfraInterface
import com.philips.platform.appinfra.logging.LoggingInterface

/**
 * PRX Dependencies Class.
 * @since 1.0.0
 */
class PRXDependencies
/**
 * PRXDependencies constructor.
 * @param context Context
 * @param appInfra App Infra Interface
 * @param parentTLA Parent Three Letter Acronym
 * @since 1.0.0
 */(val context: Context, mAppInfraInterface: AppInfraInterface?, mParentTLA: String?) {
    var mAppInfraLogging: LoggingInterface? = null

    /**
     * Getter for AppInfra Interface.
     * @return Returns the App Infra interface
     * @since 1.0.0
     */
    val appInfra: AppInfraInterface? = mAppInfraInterface

    /**
     * Getter for parent TLA.
     * @return Returns the parent TLA
     * @since  2.2.0
     */
    val parentTLA: String?= mParentTLA

}