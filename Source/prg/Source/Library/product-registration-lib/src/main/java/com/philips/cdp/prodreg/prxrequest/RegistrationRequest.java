/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.prodreg.prxrequest;


import com.google.gson.Gson;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.registerproduct.RegistrationResponseNewData;
import com.philips.cdp.prodreg.model_request.Attributes;
import com.philips.cdp.prodreg.model_request.Data;
import com.philips.cdp.prodreg.model_request.Meta;
import com.philips.cdp.prodreg.model_request.RegistrationRequestBody;
import com.philips.cdp.prodreg.model_request.UserProfile;
import com.philips.cdp.prxclient.PrxConstants;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.request.RequestType;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.ServiceDiscoveryService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_ACCEPT_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_APIKEY_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_APIVERSION_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_AUTHORIZATION_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_AUTHORIZATION_VALUE;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_CONTENTTYYPE_KEY;

public class RegistrationRequest extends PrxRequest {

    private static final String TAG = RegistrationRequest.class.getSimpleName();

    private String ctn = null;
    private String accessToken;
    private String productSerialNumber;
    private String purchaseDate;
    private String registrationChannel;
    private String sendEmail;
    private String address1;
    private String address2;
    private String address3;
    private String City;
    private String Zip;
    private String state;
    private String country;
    private String serviceID;
    private static int MAX_REQUEST_TIME_OUT = 30000;
    private boolean receiveMarketingEmail;
    private String shouldSendEmailAfterRegistration = "true";
    private String apiKey;
    private String apiVersion;
    private String contentType;
    private String authorizationProvider;
    private boolean isOidcToken;


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAuthorizationProvider() {
        return authorizationProvider;
    }

    public void setAuthorizationProvider(String authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    public RegistrationRequest(String ctn, String serviceID, PrxConstants.Sector sector, PrxConstants.Catalog catalog, boolean oidcToken) {
        super(ctn, serviceID, sector, catalog);
        this.ctn = ctn;
        this.serviceID = serviceID;
        isOidcToken = oidcToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProductSerialNumber() {
        return productSerialNumber;
    }

    public void setProductSerialNumber(String productSerialNumber) {
        this.productSerialNumber = productSerialNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getRegistrationChannel() {
        return registrationChannel;
    }

    public void setRegistrationChannel(String registrationChannel) {
        this.registrationChannel = registrationChannel;
    }

    public String getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(String sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getZip() {
        return Zip;
    }

    public void setZip(String zip) {
        Zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCtn() {
        return ctn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new RegistrationResponseNewData().parseJsonResponseData(jsonObject);
    }

    public String getShouldSendEmailAfterRegistration() {
        return shouldSendEmailAfterRegistration;
    }

    public void setShouldSendEmailAfterRegistration(final String shouldSendEmailAfterRegistration) {
        this.shouldSendEmailAfterRegistration = shouldSendEmailAfterRegistration;
    }

    @Override
    public int getRequestType() {
        return RequestType.POST.getValue();
    }

    @Override
    public int getRequestTimeOut() {
        return MAX_REQUEST_TIME_OUT;
    }

    @Override
    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
       // headers.put(ProdRegConstants.ACCESS_TOKEN_KEY, getAccessToken());
        headers.put(PROD_REG_APIKEY_KEY, getApiKey());
        headers.put(PROD_REG_APIVERSION_KEY, getApiVersion());
        headers.put(PROD_REG_AUTHORIZATION_KEY, PROD_REG_AUTHORIZATION_VALUE + getAccessToken());
        headers.put(PROD_REG_CONTENTTYYPE_KEY, getContentType());
        headers.put(PROD_REG_ACCEPT_KEY,getContentType());

        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(serviceID);
        PRUiHelper.getInstance().getAppInfraInstance().getServiceDiscovery().
                getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                    @Override
                    public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                        String url = urlMap.get(serviceID).getConfigUrls();
                        getAuthoraisationProvider(url, headers);

                    }

                    @Override
                    public void onError(ERRORVALUES error, String message) {
                        ProdRegLogger.i("Product Registration Request", "error :" + error.toString() + ":  message : " + message);
                    }
                },null);
        return headers;
    }

    private void getAuthoraisationProvider(String url, Map<String, String> headers) {
        ProdRegLogger.i("Product Registration Request"," isOidcToken "+ isOidcToken);

        if(isOidcToken){
            if (url.contains(ProdRegConstants.CHINA_DOMAIN)){
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.OIDC_AUTHORIZATION_PROVIDER_VAL_CN);
            } else {
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.OIDC_AUTHORIZATION_PROVIDER_VAL_EU);
                ProdRegLogger.i("Product Registration Request",url+ " does not contain china domain.");
            }
        }else{
            if (url.contains(ProdRegConstants.CHINA_DOMAIN)){
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.JANRAIN_AUTHORIZATION_PROVIDER_VAL_CN);
            } else {
                headers.put(ProdRegConstants.AUTHORIZATION_PROVIDER_KEY, ProdRegConstants.JANRAIN_AUTHORIZATION_PROVIDER_VAL_EU);
                ProdRegLogger.i("Product Registration Request",url+ " does not contain china domain.");
            }
        }

    }

//    @Override
//    public Map<String, String> getParams() {
//        String REGISTRATION_CHANNEL = "registrationChannel";
//        String SEND_EMAIL = "sendEmail";
//        String MARKETING_EMAIL = "receiveMarketingEmail";
//        Map<String, String> params = new HashMap<>();
//        validatePurchaseDate(params, getPurchaseDate());
//        validateSerialNumber(params);
//        params.put(REGISTRATION_CHANNEL, getRegistrationChannel());
//        params.put(SEND_EMAIL, getShouldSendEmailAfterRegistration());
//        params.put(MARKETING_EMAIL, isReceiveMarketingEmail());
//        return params;
//    }

    @Override
    public String getBody() {

        return getBodyItems();
    }

    private String getBodyItems() {


        RegistrationRequestBody registrationRequestBody = new RegistrationRequestBody();
        boolean boo = Boolean.parseBoolean(getShouldSendEmailAfterRegistration());
        Meta meta = new Meta();
        meta.setSendEmail(boo);

        Data data = new Data();
        data.setType("productRegistration");
        UserProfile userProfile = new UserProfile();
        userProfile.setOptIn(Boolean.parseBoolean(isReceiveMarketingEmail()));
        Attributes attributes = new Attributes();
        attributes.setProductId(ctn);
        attributes.setCatalog(getCatalog().toString());
        attributes.setSector(getSector().toString());
        attributes.setSerialNumber(getSerialNumber());
        attributes.setPurchased(purchaseDate(getPurchaseDate()));
        attributes.setMicrositeId(PRUiHelper.getInstance().getAppInfraInstance().getAppIdentity().getMicrositeId());
        attributes.setUserProfile(userProfile);

        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(serviceID);
        PRUiHelper.getInstance().getAppInfraInstance().getServiceDiscovery().
                getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                    @Override
                    public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                        String locale = urlMap.get(serviceID).getLocale();
                        attributes.setLocale(locale);
                    }

                    @Override
                    public void onError(ERRORVALUES error, String message) {
                        ProdRegLogger.i("Product Registration Request", "error :" + error.toString() + ":  message : " + message);
                    }
                },null);

        data.setAttributes(attributes);

        registrationRequestBody.setData(data);
        registrationRequestBody.setMeta(meta);

        String mapp = "";
        Gson gson = new Gson();
        mapp = gson.toJson(registrationRequestBody);
        return mapp;
    }

    public String isReceiveMarketingEmail() {
        return String.valueOf(receiveMarketingEmail);
    }

    public void setReceiveMarketEmail(boolean receiveMarketingEmail) {
        this.receiveMarketingEmail = receiveMarketingEmail;
    }

    private void validateSerialNumber(final Map<String, String> params) {
        String PRODUCT_SERIAL_NUMBER = "productSerialNumber";
        final String productSerialNumber = getProductSerialNumber();
        if (productSerialNumber != null && productSerialNumber.length() > 0)
            params.put(PRODUCT_SERIAL_NUMBER, productSerialNumber);
    }



    private void validatePurchaseDate(final Map<String, String> params, final String purchaseDate) {
        String PURCHASE_DATE = "purchaseDate";
        if (purchaseDate != null && purchaseDate.length() > 0)
            params.put(PURCHASE_DATE, purchaseDate);
    }

    private String getSerialNumber() {
        String PRODUCT_SERIAL_NUMBER = "";
        final String productSerialNumber = getProductSerialNumber();
        if (productSerialNumber != null && productSerialNumber.length() > 0)
            return productSerialNumber;
        return PRODUCT_SERIAL_NUMBER;
    }

    private String purchaseDate(final String purchaseDate) {
        String PURCHASE_DATE = "";
        if (purchaseDate != null && purchaseDate.length() > 0)
            return purchaseDate;
        return PURCHASE_DATE;
    }

}
