package com.philips.cdp.digitalcare.listeners;

/**
 * This is the "ConsumerCare" Common interface used across the application to serve the
 * Social Provider Click Listener , Supper Screen Button Click Listener & the ProductMenu Listener.
 * <p/>
 */
public interface CcListener {


    /**
     * @param mainMenuItem
     * @return Click Event of the Main menu Button in  the Support Screen
     */
    boolean onMainMenuItemClicked(String mainMenuItem);


    /**
     *
     * @param productMenu
     * @return Click Event of the product menu Button in  the product information Screen
     */
    boolean onProductMenuItemClicked(String productMenu);


    /**
     *
     * @param socialProviderItem
     * @return Click Event of the social provider menu Button in  the contact us Screen
     */
    boolean onSocialProviderItemClicked(String socialProviderItem);
}
