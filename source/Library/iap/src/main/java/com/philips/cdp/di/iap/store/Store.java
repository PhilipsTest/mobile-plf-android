/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.store;

import android.content.Context;

import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.RequestListener;
import com.philips.cdp.di.iap.utils.IAPLog;

public class Store {

    //Public since required by StoreConfiguration initialization
    public static final String HTTPS = "https://";
    public static final String WEB_ROOT = "pilcommercewebservices";
    public static final String V2 = "v2";
    public static final String SEPERATOR = "/";

    private static final String USER = "users";
    private static final String METAINFO = "metainfo";
    private static final String REGIONS = "regions";
    private static final String LANG = "?fields=FULL&lang=en";
    private static final String LANG_ONLY = "&lang=en";
    private static final String LANGUAGE_EN = "?language=en";

    //Oauth
    private static final String SUFFIX_OAUTH =
            "oauth/token?janrain=%s&grant_type=janrain&client_id=mobile_android&client_secret=secret";

    //Requests
    private static final String SUFFIX_CURRENT_CART = "/carts/current";
    private static final String SUFFIX_GET_CART = "/carts?fields=FULL";
    private static final String SUFFIX_CART_CREATE = "/carts";
    private static final String SUFFIX_CART_ENTRIES = "/entries";
    private static final String SUFFIX_PRODUCT_MODIFY = "/entries/%s";

    private static final String SUFFIX_PAYMENT_DETAILS = "/paymentdetails";
    private static final String SUFFIX_SET_PAYMENT_DETAILS = "/paymentdetails";
    private static final String SUFFIX_SET_PAYMENT_URL = "/orders/%s/pay";

    private static final String SUFFIX_ADDRESSES_FULL = "/addresses" + LANG;
    private static final String SUFFIX_ADDRESSES_ALTER = "/addresses/%s";

    private static final String SUFFIX_DELIVERY_MODE = "/deliverymode";
    private static final String SUFFIX_DELIVERY_ADDRESS = "/addresses/delivery";

    private static final String SUFFIX_PLACE_ORDER = "/orders";
    private static final String SUFFIX_PPRODUCT_CATALOG = "products/search?query=::category:Tuscany_Campaign" + LANG_ONLY;
    private static final String SUFFIX_REFRESH_OAUTH = "/oauth/token";

    private static final String PREFIX_RETAILERS = "www.philips.com/api/wtb/v1/";
    private static final String RETAILERS_ALTER = "online-retailers?product=%s";

    private StoreConfiguration mStoreConfig;
    public IAPUser mIAPUser;

    private String mModifyProductUrl;
    private String mPaymentDetailsUrl;
    private String mAddressDetailsUrl;
    private String mRegionsUrl;
    private String mAddressAlterUrl;
    private String mDeliveryModeUrl;
    private String mDeliveryAddressUrl;
    private String mSetPaymentDetails;
    private String mSetPaymentUrl;
    private String mPlaceOrderUrl;
    private String mCreateCartUrl;
    private String mAddToCartUrl;
    protected String mBaseURl;
    protected String mBaseURlForProductCatalog;
    private String mCurrentCartUrl;
    private String mGetRetailersUrl;
    private String mOauthUrl;
    private String mOauthRefreshUrl;
    private String mGetCartUrl;
    private String mGetProductCatalogUrl;
    private String mCountry;
    private boolean mUserLoggedout;
    private String mRetailersAlter;

    private boolean mStoreInitialized;
    private String mLanguage;

    public Store(Context context) {
        mIAPUser = initIAPUser(context);
        mStoreConfig = getStoreConfig(context);
    }

    IAPUser initIAPUser(Context context) {
        mUserLoggedout = false;
        mIAPUser = new IAPUser(context, this);
        IAPLog.i(IAPLog.LOG, "initIAPUser = " + mIAPUser.getJanRainID());
        return mIAPUser;
    }

    public void setNewUser(Context context) {
        initIAPUser(context);
        generateStoreUrls();
    }

    StoreConfiguration getStoreConfig(final Context context) {
        return new StoreConfiguration(context, this);
    }

    public void setLangAndCountry(String language, String countryCode) {
        checkAndUpdateStoreChange(language, countryCode);
        mLanguage = language;
        mCountry = countryCode;
    }

    public void initStoreConfig(String language, String countryCode, RequestListener listener) {
        mLanguage = language;
        mCountry = countryCode;
        mStoreConfig.initConfig(language, countryCode, listener);
    }

    void generateStoreUrls() {
        createBaseUrl();
        createBaseUrlForProductCatalog();
        createBaseUrlForRetailers();
        createOauthUrl();
        generateGenericUrls();
    }

    private void createBaseUrlForProductCatalog() {
        StringBuilder builder = new StringBuilder(HTTPS);
        builder.append(mStoreConfig.getHostPort()).append(SEPERATOR);
        builder.append(WEB_ROOT).append(SEPERATOR);
        builder.append(V2).append(SEPERATOR);
        builder.append(mStoreConfig.getSite()).append(SEPERATOR);

        mBaseURlForProductCatalog = builder.toString();
    }

    private void createBaseUrl() {
        setStoreInitialized(true);
        StringBuilder builder = new StringBuilder(HTTPS);
        builder.append(mStoreConfig.getHostPort()).append(SEPERATOR);
        builder.append(WEB_ROOT).append(SEPERATOR);
        builder.append(V2).append(SEPERATOR);
        builder.append(mStoreConfig.getSite()).append(SEPERATOR);
        builder.append(USER).append(SEPERATOR).append(mIAPUser.getJanRainEmail());

        mBaseURl = builder.toString();
    }

    private void createBaseUrlForRetailers() {
        StringBuilder builder = new StringBuilder(HTTPS);
        builder.append(PREFIX_RETAILERS).append(SEPERATOR);
        builder.append(NetworkConstants.PRX_SECTOR_CODE).append(SEPERATOR);
        builder.append(getLocale()).append(SEPERATOR);
        mGetRetailersUrl = builder.toString();
    }

    private void createOauthUrl() {
        StringBuilder builder = new StringBuilder(HTTPS);
        builder.append(mStoreConfig.getHostPort()).append(SEPERATOR);
        builder.append(WEB_ROOT).append(SEPERATOR);
        builder.append(SUFFIX_OAUTH);

        mOauthUrl = String.format(builder.toString(), mIAPUser.getJanRainID());
    }

    void checkAndUpdateStoreChange(String language, String countryCode) {
        if (language == null || countryCode == null || mLanguage == null || mCountry == null
                || !mCountry.equals(countryCode) || !mLanguage.equals(language)) {
            setStoreInitialized(false);
        }
    }

    void setStoreInitialized(boolean changed) {
        mStoreInitialized = changed;
    }

    private String createRegionsUrl() {
        StringBuilder builder = new StringBuilder(HTTPS);
        builder.append(mStoreConfig.getHostPort()).append(SEPERATOR);
        builder.append(WEB_ROOT).append(SEPERATOR);
        builder.append(V2).append(SEPERATOR);
        builder.append(METAINFO).append(SEPERATOR);
        builder.append(REGIONS).append(SEPERATOR);
        builder.append(getCountry()).append(LANGUAGE_EN);//Check whether to pass "UK" / "GB"
        return builder.toString();
    }

    protected void generateGenericUrls() {
        String mCurrentCartUrl = mBaseURl.concat(SUFFIX_CURRENT_CART);
        mGetCartUrl = mBaseURl.concat(SUFFIX_GET_CART);
        mCreateCartUrl = mBaseURl.concat(SUFFIX_CART_CREATE);
        mAddToCartUrl = mCurrentCartUrl.concat(SUFFIX_CART_ENTRIES);
        mPaymentDetailsUrl = mBaseURl.concat(SUFFIX_PAYMENT_DETAILS);
        mAddressDetailsUrl = mBaseURl.concat(SUFFIX_ADDRESSES_FULL);
        mRegionsUrl = createRegionsUrl();
        mAddressAlterUrl = mBaseURl.concat(SUFFIX_ADDRESSES_ALTER);
        mSetPaymentUrl = mBaseURl.concat(SUFFIX_SET_PAYMENT_URL);
        mPlaceOrderUrl = mBaseURl.concat(SUFFIX_PLACE_ORDER);
        mGetProductCatalogUrl = mBaseURlForProductCatalog.concat(SUFFIX_PPRODUCT_CATALOG);
        mModifyProductUrl = mCurrentCartUrl.concat(SUFFIX_PRODUCT_MODIFY);
        mDeliveryModeUrl = mCurrentCartUrl.concat(SUFFIX_DELIVERY_MODE);
        mDeliveryAddressUrl = mCurrentCartUrl.concat(SUFFIX_DELIVERY_ADDRESS);
        mSetPaymentDetails = mCurrentCartUrl.concat(SUFFIX_SET_PAYMENT_DETAILS);
        mRetailersAlter = mGetRetailersUrl.concat(RETAILERS_ALTER);
        mOauthRefreshUrl = HTTPS.concat(mStoreConfig.getHostPort()).concat(SEPERATOR)
                .concat(WEB_ROOT).concat(SUFFIX_REFRESH_OAUTH);
    }

    public String getCountry() {
        if (mCountry != null) {
            return mCountry;
        }
        return "";
    }

    public String getLocale() {
        return mStoreConfig.getLocale();
    }

    //Package level access
    //Called when janrain token is changed
    void updateJanRainIDBasedUrls() {
        createOauthUrl();
    }

    public String getOauthUrl() {
        return mOauthUrl;
    }

    public String getOauthRefreshUrl() {
        return mOauthRefreshUrl;
    }

    public String getJanRainEmail() {
        return mIAPUser.getJanRainEmail();
    }

    public IAPUser getUser() {
        return mIAPUser;
    }

    //Request Urls
    public String getCurrentCartDetailsUrl() {
        return mGetCartUrl;
    }

    public String getProductCatalogUrl() {
        return mGetProductCatalogUrl;
    }

    public String getCreateCartUrl() {
        return mCreateCartUrl;
    }

    public String getAddToCartUrl() {
        return mAddToCartUrl;
    }

    public String getModifyProductUrl(String productID) {
        return String.format(mModifyProductUrl, productID);
    }

    public String getPaymentDetailsUrl() {
        return mPaymentDetailsUrl;
    }

    public String getAddressDetailsUrl() {
        return mAddressDetailsUrl;
    }

    public String getRegionsUrl() {
        return mRegionsUrl;
    }

    public String getAddressAlterUrl(String addressID) {
        return String.format(mAddressAlterUrl, addressID);
    }

    public String getRetailersAlterUrl(String CTN) {
        return String.format(mRetailersAlter, CTN);
    }

    public String getUpdateDeliveryModeUrl() {
        return mDeliveryModeUrl;
    }

    public String getUpdateDeliveryAddressUrl() {
        return mDeliveryAddressUrl;
    }

    public String getSetPaymentUrl(String id) {
        return String.format(mSetPaymentUrl, id);
    }

    public String getPlaceOrderUrl() {
        return mPlaceOrderUrl;
    }

    public String getSetPaymentDetailsUrl() {
        return mSetPaymentDetails;
    }

    public void refreshLoginSession() {
        mIAPUser.refreshLoginSession();
    }

    public void setUserLogout(boolean userLoggedout) {
        this.mUserLoggedout = userLoggedout;
    }

    public boolean isUserLoggedOut() {
        return mUserLoggedout;
    }

    public boolean isStoreInitialized() {
        return mStoreInitialized;
    }
}