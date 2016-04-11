/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.store;

import android.content.Context;

import com.philips.cdp.di.iap.session.RequestListener;

public class StoreConfiguration {
    private static final String SUFFIX_INAPPCONFIG = "inAppConfig";

    private Store mStore;
    private String hostport;
    private String site;
    private VerticalAppConfig mVerticalAppConfig;
    private WebStoreConfig mWebStoreConfig;

    public StoreConfiguration(Context context, Store store) {
        mStore = store;
        mVerticalAppConfig = getVerticalAppConfig(context);
        mWebStoreConfig = getWebStoreConfig(context);
    }

    void initConfig(String countryCode, RequestListener listener) {
        mWebStoreConfig.initConfig(countryCode, listener);
    }

    public String getHostPort() {
        return mVerticalAppConfig.getHostPort();
    }

    public String getSite() {
        return mWebStoreConfig.getSiteID();
    }

    public String getPropositionID() {
        return mVerticalAppConfig.getPropositionID();
    }

    VerticalAppConfig getVerticalAppConfig(final Context context) {
        return new VerticalAppConfig(context);
    }

    WebStoreConfig getWebStoreConfig(final Context context) {
        return new WebStoreConfig(context, this);
    }

    public String getRawConfigUrl() {
        StringBuilder builder = new StringBuilder(Store.HTTPS);
        builder.append(getHostPort()).append(Store.SEPERATOR);
        builder.append(Store.WEB_ROOT).append(Store.SEPERATOR);
        builder.append(Store.V2).append(Store.SEPERATOR);
        builder.append(SUFFIX_INAPPCONFIG).append(Store.SEPERATOR);
        builder.append(mWebStoreConfig.getLocale()).append(Store.SEPERATOR);
        builder.append(getPropositionID());

        return builder.toString();
    }

    public void createStoreUrls() {
        mStore.generateStoreUrls();
    }
}