package com.philips.cdp.di.ecs.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.constants.ModelConstants;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.address.Addresses;

import com.philips.cdp.di.ecs.store.ECSURLBuilder;
import com.philips.cdp.di.ecs.util.ECSConfig;
import com.philips.cdp.di.ecs.util.ECSErrorReason;
import com.philips.cdp.di.ecs.util.ECSErrors;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.philips.cdp.di.ecs.util.ECSErrors.getDetailErrorMessage;
import static com.philips.cdp.di.ecs.util.ECSErrors.getErrorMessage;

public class CreateAddressRequest extends OAuthAppInfraAbstractRequest implements Response.Listener<String> {


    Addresses ecsAddressRequest;
    private  ECSCallback<Addresses,Exception> ecsCallback;

    public CreateAddressRequest(Addresses ecsAddressRequest, ECSCallback<Addresses, Exception> ecsCallback) {
        this.ecsAddressRequest = ecsAddressRequest;
        this.ecsCallback = ecsCallback;
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(String response) {
        Addresses addresses=null;
        Exception exception = new Exception();
                // created address response is not checked
        try {
            addresses = new Gson().fromJson(response, Addresses.class);
        }catch(Exception e){
            exception = e;

        }
        if(null!=addresses) {
            ecsCallback.onResponse(addresses);
        }else{
            ecsCallback.onFailure(new Exception(ECSErrorReason.ECS_UNKNOWN_ERROR), exception.getMessage(),12999);
        }
    }

    @Override
    public int getMethod() {
        return Request.Method.POST;
    }

    @Override
    public String getURL() {
        return new ECSURLBuilder().getAddressesUrl();
    }

    @Override
    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", "Bearer " + ECSConfig.INSTANCE.getAccessToken());
        return header;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(ModelConstants.FIRST_NAME, ecsAddressRequest.getFirstName());
        params.put(ModelConstants.LAST_NAME, ecsAddressRequest.getLastName());
        params.put(ModelConstants.TITLE_CODE, ecsAddressRequest.getTitleCode().toLowerCase(Locale.getDefault()));
        params.put(ModelConstants.COUNTRY_ISOCODE, ecsAddressRequest.getCountry().getIsocode());
        params.put(ModelConstants.LINE_1, ecsAddressRequest.getLine1());
        if(null!=ecsAddressRequest.getLine2()){
            params.put(ModelConstants.LINE_2, ecsAddressRequest.getLine2());
        }
        if(null!=ecsAddressRequest.getHouseNumber()) {
            params.put(ModelConstants.HOUSE_NO, ecsAddressRequest.getHouseNumber());
        }
        params.put(ModelConstants.POSTAL_CODE, ecsAddressRequest.getPostalCode());
        params.put(ModelConstants.TOWN, ecsAddressRequest.getTown());
        params.put(ModelConstants.PHONE_1, ecsAddressRequest.getPhone1());
        params.put(ModelConstants.PHONE_2, ecsAddressRequest.getPhone1());
        if(null!=ecsAddressRequest.getRegion().getIsocodeShort()) {
            params.put(ModelConstants.REGION_ISOCODE, ecsAddressRequest.getRegion().getIsocodeShort());
        }

        return params;
    }

    /**
     * Callback method that an error has been occurred with the provided error code and optional
     * user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        ecsCallback.onFailure(getErrorMessage(error),getDetailErrorMessage(error),12999);

    }

    public Response.Listener<String> getStringSuccessResponseListener(){
        return this;
    }
}
