/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework.flowmanager.parser;

import android.content.Context;
import android.support.annotation.IdRes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.pojo.AppFlow;
import com.philips.platform.appframework.flowmanager.pojo.AppFlowEvent;
import com.philips.platform.appframework.flowmanager.pojo.AppFlowModel;
import com.philips.platform.appframework.flowmanager.pojo.AppFlowState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppFrameworkDataParser {

    /**
     * This method will return the object of AppFlow class or 'null'.
     * It request 'getJsonFromURL' to download the AppFlow json by sending the server URL.
     * it also send the path of prepackaged AppFlow Json file to handle the offline/error scenarios.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     * @return Object to 'AppFlowModel' class or 'null'
     */
    public static AppFlowModel getAppFlow(Context context, @IdRes int jsonPath) {
        String appFlowResponse;
        final JSONHelper jsonHelper = new JSONHelper(context);
        AppFlowModel appFlow;
        try {
            appFlowResponse = jsonHelper.getJsonForAppFlow(jsonPath);
            appFlow = new Gson().fromJson(appFlowResponse, AppFlowModel.class);
        } catch (JsonSyntaxException e) {
            // This code has been added to handle the cases of JSON parsing error/exception
            appFlowResponse = jsonHelper.readJsonFromFile
                    (R.string.com_philips_app_fmwk_app_flow_url, context);
            appFlow = new Gson().fromJson(appFlowResponse, AppFlowModel.class);
        }
        return appFlow;
    }

    /**
     * This method will return a Map of state to array of next states.
     *
     * @param appFlow Object to AppFlow class which defines the app flow.
     * @return Map of state to array of next states.
     */
    public static Map<String, List<AppFlowEvent>> getAppFlowMap(AppFlow appFlow) {
        HashMap<String, List<AppFlowEvent>> appFlowMap = null;
        if (appFlow.getStates() != null) {
            appFlowMap = new HashMap<>();
            for (final AppFlowState states : appFlow.getStates()) {
                appFlowMap.put(states.getState(), states.getEvents());
            }
        }
        return appFlowMap;
    }
}
