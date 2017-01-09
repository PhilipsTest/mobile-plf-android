/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.prxrequest;

import android.net.Uri;
import android.util.Log;

import com.philips.cdp.prodreg.logging.ProdRegLogger;
import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.request.RequestType;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

public class ProductMetadataRequest extends PrxRequest {
    private static final String TAG = ProductMetadataRequest.class.getSimpleName();
    private String mCtn = null;
    private String mServerInfo = "https://acc.philips.com/prx/registration/";

    public ProductMetadataRequest(String ctn) {
        this.mCtn = ctn;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new ProductMetadataResponse().parseJsonResponseData(jsonObject);
    }

    @Override
    public String getServerInfo() {
        AppInfraInterface appInfra = RegistrationHelper.getInstance().getAppInfraInstance();
        final ServiceDiscoveryInterface serviceDiscoveryInterface = appInfra.getServiceDiscovery();

        serviceDiscoveryInterface.getServiceUrlWithCountryPreference("prodreg.productmetadatarequest"
                , new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                        Log.d(TAG, " Response Error : " + s);

                    }

                    @Override
                    public void onSuccess(URL url) {
                        mServerInfo = url.toString();
                    }
                });
//        String mConfiguration = getRegistrationEnvironment();
//        if (mConfiguration.equalsIgnoreCase("Development")) {
//            mServerInfo = "https://10.128.41.113.philips.com/prx/registration/";
//        } else if (mConfiguration.equalsIgnoreCase("Testing")) {
//            mServerInfo = "https://tst.philips.com/prx/registration/";
//        } else if (mConfiguration.equalsIgnoreCase("Evaluation")) {
//            mServerInfo = "https://acc.philips.com/prx/registration/";
//        } else if (mConfiguration.equalsIgnoreCase("Staging")) {
//            mServerInfo = "https://dev.philips.com/prx/registration/";
//        } else if (mConfiguration.equalsIgnoreCase("Production")) {
//            mServerInfo = "https://www.philips.com/prx/registration/";
//        }
        return mServerInfo;
    }

    protected String getRegistrationEnvironment() {
        return RegistrationConfiguration.getInstance().getRegistrationEnvironment();
    }

    @Override
    public String getRequestUrl() {
        Uri builtUri = Uri.parse(getServerInfo())
                .buildUpon()
                .appendPath(getSector().name())
                .appendPath(getLocaleMatchResult())
                .appendPath(getCatalog().name())
                .appendPath("products")
                .appendPath(mCtn + ".metadata")
                .build();
        String url = builtUri.toString();
        try {
            url = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            ProdRegLogger.e(TAG, e.getMessage());
        }
        ProdRegLogger.d(getClass() + "URl :", builtUri.toString());
        return url;
    }

    @Override
    public int getRequestType() {
        return RequestType.GET.getValue();
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }

    public void setCtn(final String mCtn) {
        this.mCtn = mCtn;
    }

    @Override
    public int getRequestTimeOut() {
        return 30000;
    }
}
