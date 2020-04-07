/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.pif.DataInterface.MEC.listeners;

/**
 * The interface Mec hybris availability listener.
 */
public interface MECHybrisAvailabilityListener {
    /**
     * This API is called  to check if  Hybris is currently available for selected country
     *
     * @param isHybrisAvailable returns true if Hybris is currently available
     * @since 2002.0
     */
    void isHybrisAvailable( Boolean isHybrisAvailable);
}
