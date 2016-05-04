package com.philips.cdp.prodreg.prxrequest;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.philips.cdp.prodreg.model.metadata.ProductMetadataResponse;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.request.RequestType;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProductMetadataRequest extends PrxRequest {

    private String mCtn = null;
    private String mServerInfo="https://acc.philips.com/prx/registration/";

    public ProductMetadataRequest(String ctn) {
        this.mCtn = ctn;
    }

    @Override
    public ResponseData getResponseData(JSONObject jsonObject) {
        return new ProductMetadataResponse().parseJsonResponseData(jsonObject);
    }

    @Override
    public String getServerInfo() {
        String mConfiguration = RegistrationConfiguration.getInstance().getPilConfiguration().getRegistrationEnvironment();
        if (mConfiguration.equalsIgnoreCase("Development")) {
            mServerInfo = "https://10.128.41.113.philips.com/prx/registration/";
        } else if (mConfiguration.equalsIgnoreCase("Testing")) {
            mServerInfo = "https://tst.philips.com/prx/registration/";
        } else if (mConfiguration.equalsIgnoreCase("Evaluation")) {
            mServerInfo = "https://acc.philips.com/prx/registration/";
        } else if (mConfiguration.equalsIgnoreCase("Staging")) {
            mServerInfo = "https://dev.philips.com/prx/registration/";
        }else if (mConfiguration.equalsIgnoreCase("Production")) {
            mServerInfo = "https://www.philips.com/prx/registration/";
        }
        return mServerInfo;
    }

    @Override
    public String getRequestUrl() {
        return generateUrl();
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

    private String generateUrl() {
        Uri builtUri = Uri.parse(getServerInfo())
                .buildUpon()
                .appendPath(getSector().name())
                .appendPath(getLocaleMatchResult())
                .appendPath(getCatalog().name())
                .appendPath("products")
                .appendPath(mCtn + ".metadata")
                .build();
        Log.d(getClass() + "URl :", builtUri.toString());
        return getDecodedUrl(builtUri);
    }

    @NonNull
    private String getDecodedUrl(final Uri builtUri) {
        String url = builtUri.toString();
        try {
            url = java.net.URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(getClass() + "", url);
        return url;
    }

    public void setCtn(final String mCtn) {
        this.mCtn = mCtn;
    }
}
