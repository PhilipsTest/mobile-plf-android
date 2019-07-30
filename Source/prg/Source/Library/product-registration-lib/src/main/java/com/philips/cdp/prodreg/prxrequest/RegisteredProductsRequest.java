/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.prxrequest;


import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.launcher.PRUiHelper;
import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.registeredproducts.RegisteredResponse;
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

import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_APIKEY_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_APIKEY_VALUE;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_APIVERSION_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_AUTHORIZATION_KEY;
import static com.philips.cdp.prodreg.constants.ProdRegConstants.PROD_REG_AUTHORIZATION_VALUE;

public class RegisteredProductsRequest extends PrxRequest {

    private String accessToken;
    private String mServiceId;
    private boolean isOidcToken;

    public RegisteredProductsRequest(String ctn, String serviceID, PrxConstants.Sector sector, PrxConstants.Catalog catalog, boolean oidcToken) {
        super(ctn, serviceID, sector, catalog);
        mServiceId = serviceID;
        isOidcToken = oidcToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new RegisteredResponse().parseJsonResponseData(jsonObject);
    }


    @Override
    public int getRequestType() {
        return RequestType.GET.getValue();
    }

    @Override
    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
       // headers.put(ProdRegConstants.ACCESS_TOKEN_KEY, getAccessToken());

        headers.put(PROD_REG_APIKEY_KEY,PROD_REG_APIKEY_VALUE);
        headers.put(PROD_REG_APIVERSION_KEY, "1");
        headers.put(PROD_REG_AUTHORIZATION_KEY, PROD_REG_AUTHORIZATION_VALUE + getAccessToken());



        ArrayList<String> serviceIDList = new ArrayList<>();
        serviceIDList.add(mServiceId);
        PRUiHelper.getInstance().getAppInfraInstance().getServiceDiscovery().
                getServicesWithCountryPreference(serviceIDList, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
                    @Override
                    public void onSuccess(Map<String, ServiceDiscoveryService> urlMap) {
                        String url = urlMap.get(mServiceId).getConfigUrls();
                        getAuthoraisationProvider(url, headers);
                    }

                    @Override
                    public void onError(ERRORVALUES error, String message) {
                        ProdRegLogger.i("Registration Request","Registration Request :error :"+error.toString() + ":  message : "+message );
                    }
                },null);
        return headers;
    }

    private void getAuthoraisationProvider(String url, Map<String, String> headers) {

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


    @Override
    public Map<String, String> getParams() {
        return null;
    }

    @Override
    public PrxConstants.Sector getSector() {
        return PrxConstants.Sector.DEFAULT;
    }

    @Override
    public PrxConstants.Catalog getCatalog() {
        return PrxConstants.Catalog.DEFAULT;
    }

    @Override
    public int getRequestTimeOut() {
        return 30000;
    }
}
