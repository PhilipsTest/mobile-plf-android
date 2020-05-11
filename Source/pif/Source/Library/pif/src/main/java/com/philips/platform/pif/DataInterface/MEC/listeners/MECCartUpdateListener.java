package com.philips.platform.pif.DataInterface.MEC.listeners;

/**
 * The interface Mec cart update listener.
 */
public interface MECCartUpdateListener  {

    /**
     * This API is called whenever there is change in number of products in cart. e.g. Addition of product, deletion of product, change product quantity etc
     *
     * @param count the shopping cart product updated count
     * @since 2002.0
     */
    void onUpdateCartCount( int count);

    /**
     * This API is called to decide if shopping cart icon should be displayed or not
     *
     * @param shouldShow the should show shopping cart icon if the param is true else not
     * @since 2002.0
     */
    void shouldShowCart(Boolean shouldShow );



}
