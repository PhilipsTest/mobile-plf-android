/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.pif.DataInterface.MEC;

import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener;
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener;
import com.philips.platform.pif.DataInterface.MEC.listeners.MECHybrisAvailabilityListener;

/**
 * The interface Mec data interface.
 */
public interface MECDataInterface {

    /**
     * This API is called to explicitly fetch shopping cart product count .
     *
     * @param mecFetchCartListener the  fetch cart listener for product count callback
     *  @since 2002.0
     */


    void fetchCartCount(MECFetchCartListener mecFetchCartListener)throws MECException;



    /**
     * This API is called to explicitly check if Hybris server is available .
     *
     *  @param mECHybrisAvailabilityListener the Hybris server availability listener for product count callback
     *  @throws MECException : It can through user not logged in or no internet exception
     *  @since 2002.0
     */
    void isHybrisAvailable(  MECHybrisAvailabilityListener mECHybrisAvailabilityListener) throws MECException;



    /**
     * This API is called to add a MECCartUpdateListener which will give shopping cart product count update and should show cart boolean
     *
     *
     *  @param mecCartUpdateListener the listener for cart product count update and should show cart callbacks
     *  @throws MECException : It can through user not logged in or no internet exception
     *  @since 2002.0
     */
    void addCartUpdateListener(MECCartUpdateListener mecCartUpdateListener ) ;



    /**
     *This API is called to remove a previously added MECCartUpdateListener
     *
     * @param mecCartUpdateListener The listener to be removed
     *  @since 2002.0
     */
    void removeCartUpdateListener(MECCartUpdateListener mecCartUpdateListener);

}
