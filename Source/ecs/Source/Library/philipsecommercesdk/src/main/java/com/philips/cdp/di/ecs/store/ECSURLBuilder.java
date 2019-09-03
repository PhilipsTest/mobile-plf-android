/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.ecs.store;


import com.philips.cdp.di.ecs.util.ECSConfig;


public class ECSURLBuilder implements URLProvider {

    private static final String SUFFIX_CONFIGURATION = "inAppConfig";

    public static final String WEBROOT = "pilcommercewebservices";
    public static final String V2 = "v2";
    public static final String SEPERATOR = "/";
    private static final String USER = "users";
    private static final String FIELDS_FULL_LANG = "?fields=FULL&lang=";
    private static final String SUFFIX_LANG_QUESTION = "?lang=";

    //Region API
    private static final String METAINFO = "metainfo";
    private static final String REGIONS = "regions";

    //Oauth
    private static final String SUFFIX_OAUTH ="oauth/token";


    private static final String SUFFIX_REFRESH_OAUTH = "oauth/token";

    //Requests
    private static final String SUFFIX_CARTS = "carts";
    private static final String SUFFIX_CURRENT = "current";
    private static final String SUFFIX_ENTRIES = "entries";

    /*ToDO : using lang=en instead of locale as backend not support*/
    private String SUFFIX_PRODUCT_CATALOG = "products/search?query=::category:Tuscany_Campaign&lang=";

    private static final String SUFFIX_PRODUCTS = "products";
    private static final String SUFFIX_CURRENT_PAGE = "&currentPage=";
    private static final String SUFFIX_STRING_PARAM = "%s";

    private static final String SUFFIX_ADDRESSES = "addresses";
    private static final String SUFFIX_DELIVERY_ADDRESS = "delivery";

    private static final String SUFFIX_DELIVERY_MODE = "deliverymode";
    private static final String SUFFIX_DELIVERY_MODES = "deliverymodes";

    private static final String SUFFIX_PAYMENT_DETAILS = "/paymentdetails";
    private static final String SUFFIX_ORDERS = "orders";
    private static final String SUFFIX_PAY = "/pay";
    private static final String SUFFIX_CONTACT_PHONE_URL = "%s" + ".querytype.(fallback)";

    private static final String SUFFIX_VOUCHERS = "vouchers";

    private String mOauthRefreshUrl;


    private String mGetProductCatalogUrl;
    private String mSearchProductUrl;
    private String mUpdateProductUrl;

    private String mGetCurrentCartUrl;
    private String mDeleteCartUrl;

    private String mGetPhoneContactUrl;








    //OAuth
    @Override
    public String getOauthUrl(String janRainID) {
        StringBuilder builder = new StringBuilder(ECSConfig.INSTANCE.getBaseURL());
        builder.append(WEBROOT).append(SEPERATOR).append(SUFFIX_OAUTH)
        .append("?janrain=").append(janRainID)
        .append("&grant_type=janrain&client_id=mobile_android&client_secret=secret");
        return  builder.toString();
    }

    @Override
    public String getOauthRefreshUrl() {
        return mOauthRefreshUrl;
    }

    @Override
    public void refreshLoginSession() {

    }

    //Product
    @Override
    public String getProductCatalogUrl(int currentPage, int pageSize) {

        StringBuilder builder = new StringBuilder(ECSConfig.INSTANCE.getBaseURL());
        builder.append(WEBROOT).append(SEPERATOR).append(V2).append(SEPERATOR);
        builder.append(ECSConfig.INSTANCE.getSiteId()).append(SEPERATOR);
        String baseURlForProductCatalog = builder.toString();

        String SUFFIX_PRODUCT_CATALOG = "products/search?query=::category:"+ECSConfig.INSTANCE.getRootCategory()+"&lang=";
        mGetProductCatalogUrl = baseURlForProductCatalog.concat(SUFFIX_PRODUCT_CATALOG).concat(ECSConfig.INSTANCE.getLocale()).concat("&currentPage=%s&pageSize=%s");

        if (mGetProductCatalogUrl != null)
            return String.format(mGetProductCatalogUrl, currentPage, pageSize);
        return null;
    }


    // this is one of theIAP  entry point where directly product detail is launched
    @Override
    public String getProduct(String ctnNumber) {
        String formattedCtnNumber = ctnNumber.replace('/', '_');
        StringBuilder builder = new StringBuilder(ECSConfig.INSTANCE.getBaseURL());
        builder.append(WEBROOT).append(SEPERATOR).append(V2).append(SEPERATOR);
        builder.append(ECSConfig.INSTANCE.getSiteId()).append(SEPERATOR).append(SUFFIX_PRODUCTS)
                .append(SEPERATOR).append(formattedCtnNumber).append(SUFFIX_LANG_QUESTION).append(ECSConfig.INSTANCE.getLocale());

        return builder.toString();
    }

    @Override
    public String getUpdateProductUrl(String productID) {
        return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR
                +ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR
                +USER+SEPERATOR
                +SUFFIX_CURRENT+SEPERATOR
                +SUFFIX_CARTS+SEPERATOR
                +SUFFIX_CURRENT+SEPERATOR
                +SUFFIX_ENTRIES+SEPERATOR
                +productID+FIELDS_FULL_LANG
                +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getSearchProductUrl(String ctnNumber) {
        if(mUpdateProductUrl != null && ctnNumber!=null){
            return String.format(mSearchProductUrl, ctnNumber);
        }
        else{
            return null;
        }

    }

    //Carts
    @Override
    public String getCartsUrl() {
        return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR
                +ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR
                +USER+ECSURLBuilder.SEPERATOR
                +SUFFIX_CURRENT+ECSURLBuilder.SEPERATOR
                +SUFFIX_CARTS+ECSURLBuilder.SEPERATOR
                +SUFFIX_CURRENT+SUFFIX_LANG_QUESTION
                +ECSConfig.INSTANCE.getLocale();

    }

    @Override
    public String getCurrentCartUrl() {
        return mGetCurrentCartUrl;
    }

    @Override
    public String getCreateCartUrl() {
        return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR
                +ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR
                +USER+ECSURLBuilder.SEPERATOR
                +SUFFIX_CURRENT+ECSURLBuilder.SEPERATOR
                +SUFFIX_CARTS+SUFFIX_LANG_QUESTION
                +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getDeleteCartUrl() {
        return mDeleteCartUrl;
    }

    @Override
    public String getAddToCartUrl() {
        return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR
                +ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR
                +USER+ECSURLBuilder.SEPERATOR
                +SUFFIX_CURRENT+ECSURLBuilder.SEPERATOR
                +SUFFIX_CARTS+ECSURLBuilder.SEPERATOR
                +SUFFIX_CURRENT+ECSURLBuilder.SEPERATOR
                +SUFFIX_ENTRIES+FIELDS_FULL_LANG
                +ECSConfig.INSTANCE.getLocale();
    }

    //Address
    @Override
    public String getRegionsUrl() {
       return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR+
                 METAINFO+SEPERATOR+
                 REGIONS+SEPERATOR+
                 ECSConfig.INSTANCE.getCountry()+FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getUserUrl() {

        return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR+
                USER+SEPERATOR +
                SUFFIX_CURRENT+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale() ;
    }

    @Override
    public String getAddressesUrl() {
       return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR+
                USER+SEPERATOR +
                SUFFIX_CURRENT+SEPERATOR +
                SUFFIX_ADDRESSES+ FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale() ;
    }

    @Override
    public String getEditAddressUrl(String addressID) {

       return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR+
                USER+SEPERATOR +
                SUFFIX_CURRENT+SEPERATOR +
                SUFFIX_ADDRESSES+SEPERATOR+
                addressID+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale() ;
    }

    @Override
    public String getSetDeliveryAddressUrl() {

      return   ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+ECSURLBuilder.SEPERATOR+
                USER+SEPERATOR +
                SUFFIX_CURRENT+SEPERATOR +
                SUFFIX_CARTS+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR +
                SUFFIX_ADDRESSES+SEPERATOR+
                SUFFIX_DELIVERY_ADDRESS+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale() ;
    }

    //Delivery mode
    @Override
    public String getDeliveryModesUrl() {
        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_CARTS+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_DELIVERY_MODES+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getSetDeliveryModeUrl() {

        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_CARTS+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_DELIVERY_MODE+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale();
    }

    //Payment
    @Override
    public String getPaymentDetailsUrl() {

        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+
                SUFFIX_PAYMENT_DETAILS+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getSetPaymentDetailsUrl() {
        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_CARTS+SEPERATOR+
                SUFFIX_CURRENT+
                SUFFIX_PAYMENT_DETAILS+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getMakePaymentUrl(String orderId) {
        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+
                SEPERATOR+
                SUFFIX_ORDERS+
                SEPERATOR+orderId+
                SUFFIX_PAY;
    }

    @Override
    public String getPlaceOrderUrl() {

        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_ORDERS;

    }

    //Orders
    @Override
    public String getOrderHistoryUrl(String pageNumber) {

        ///pilcommercewebservices/v2/US_Pub/users/current/orders
      return   ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_ORDERS+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale()+ SUFFIX_CURRENT_PAGE+pageNumber;

    }

    @Override
    public String getOrderDetailUrl(String orderId) {

        // mOrderDetailUrl = mBaseURl.concat(SUFFIX_ORDERS).concat(SUFFIX_STRING_PARAM).concat(FIELDS_FULL_LANG) + mStoreConfig.getLocale();
        return   ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_ORDERS+SEPERATOR+
                orderId+
                FIELDS_FULL_LANG +ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getPhoneContactUrl(String category) {
        return String.format(mGetPhoneContactUrl, category);
    }

    @Override
    public String getApplyVoucherUrl() {
       return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
               ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
               USER+SEPERATOR+
               SUFFIX_CURRENT+SEPERATOR+
               SUFFIX_CARTS+SEPERATOR+
               SUFFIX_CURRENT+SEPERATOR+
               SUFFIX_VOUCHERS+SUFFIX_LANG_QUESTION+
               ECSConfig.INSTANCE.getLocale();
    }

    @Override
    public String getDeleteVoucherUrl(String voucherId) {
        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_CARTS+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_VOUCHERS+SEPERATOR+
                voucherId+
                SUFFIX_LANG_QUESTION+ ECSConfig.INSTANCE.getLocale();

    }

    @Override
    public String getAppliedVoucherUrl() {
        return ECSConfig.INSTANCE.getBaseURL()+ WEBROOT + SEPERATOR + V2 + SEPERATOR+
                ECSConfig.INSTANCE.getSiteId()+SEPERATOR+
                USER+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_CARTS+SEPERATOR+
                SUFFIX_CURRENT+SEPERATOR+
                SUFFIX_VOUCHERS+
                SUFFIX_LANG_QUESTION+ ECSConfig.INSTANCE.getLocale();
    }

    public String getRawConfigUrl() {
        return ECSConfig.INSTANCE.getBaseURL() + ECSURLBuilder.WEBROOT + ECSURLBuilder.SEPERATOR + ECSURLBuilder.V2 + ECSURLBuilder.SEPERATOR +
                SUFFIX_CONFIGURATION + ECSURLBuilder.SEPERATOR +
                ECSConfig.INSTANCE.getLocale() + ECSURLBuilder.SEPERATOR +
                ECSConfig.INSTANCE.getPropositionID();
    }

}