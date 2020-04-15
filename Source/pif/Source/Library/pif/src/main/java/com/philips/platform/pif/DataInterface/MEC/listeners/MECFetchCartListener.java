package com.philips.platform.pif.DataInterface.MEC.listeners;

/**
 * The interface Mec fetch cart listener.
 */
public interface MECFetchCartListener  {
    /**
     * This API is called to get current shopping cart product count explicitly. e.g. Total number of products added to cart
     *
     * @param count the total number of product added to shopping cart
     *@since 2002.0
     */
    void onGetCartCount(int count);

    /**
     *
     *
     * This API is called on any failure in onGetCartCount call
     * @since 2002.0
     */
    void onFailure(Exception exception);
}
