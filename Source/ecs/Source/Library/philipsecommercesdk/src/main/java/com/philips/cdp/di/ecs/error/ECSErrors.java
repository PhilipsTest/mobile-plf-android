package com.philips.cdp.di.ecs.error;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.philips.cdp.di.ecs.R;
import com.philips.cdp.di.ecs.util.ECSConfig;
import com.philips.platform.appinfra.logging.LoggingInterface;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.philips.cdp.di.ecs.util.ECSErrorReason.ECS_AUTH_FAILURE_ERROR;
import static com.philips.cdp.di.ecs.util.ECSErrorReason.ECS_CONNECTION_TIMEOUT;
import static com.philips.cdp.di.ecs.util.ECSErrorReason.ECS_PARSE_ERROR;
import static com.philips.cdp.di.ecs.util.ECSErrorReason.ECS_SERVER_NOT_FOUND;

public class ECSErrors {


 /*   private static final String LOGGING_TAG = "DETAIL_ERROR";

    public static Exception getVolleyException(VolleyError volleyError) {
        String errorType = null;
        int errorCode = 0;
        if (volleyError instanceof NetworkError || volleyError instanceof NoConnectionError) {
            errorType = getLocalizedErrorMessage(ECSErrorConstant.ECSVolleyError.ECS_CANNOT_CONNECT_INTERNET.getResourceID());

        } else if (volleyError instanceof AuthFailureError) {
            errorType=ECS_AUTH_FAILURE_ERROR;
            try {
                JSONObject jsonError = new JSONObject(logDetailErrorMessage(volleyError));
                String type = jsonError.getString("type");
                if (null != type && !type.isEmpty()) {
                    errorType = type;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (volleyError instanceof ServerError) {
            errorType = ECS_SERVER_NOT_FOUND;
        } else if (volleyError instanceof ParseError) {
            errorType = ECS_PARSE_ERROR;
        } else if (volleyError instanceof TimeoutError) {
            errorType = ECS_CONNECTION_TIMEOUT;
        }
        Exception exception =null;
        if(null!=errorType) {
            exception = new Exception(errorType);
        }else{
            exception = new Exception(volleyError.getLocalizedMessage());
        }
        return exception;
    }

    public static String logDetailErrorMessage(VolleyError volleyError) {
        String message = null;
        try {
            if(null!=volleyError.networkResponse && null!=volleyError.networkResponse.data) {
                String responseBody = new String(volleyError.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                JSONArray errors = data.getJSONArray("errors");
                JSONObject jsonMessage = errors.getJSONObject(0);

                message = jsonMessage.toString();
                //jsonMessage.getString("message");
            }else if (null!=volleyError.getMessage() ){
                message=volleyError.getMessage();
            }

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        } catch (Exception e) {

        }
        ECSConfig.INSTANCE.getAppInfra().getLogging().log(LoggingInterface.LogLevel.DEBUG,LOGGING_TAG,message);
        return message;
    }

    public static void showECSToast(Context context, String message) {
        try {
            Toast.makeText(context, message,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }




   public static String getLocalizedErrorMessage(int stringId){
        String localizedError=ECSConfig.INSTANCE.getAppInfra().getAppInfraContext().getString(R.string.something_went_wrong);
        try{
            localizedError =   ECSConfig.INSTANCE.getAppInfra().getAppInfraContext().getResources().getString(stringId);
        } catch(Exception e){
            Log.e("RES_NOT_FOUND", e.getMessage());
        }
        return localizedError;
   }*/
}
