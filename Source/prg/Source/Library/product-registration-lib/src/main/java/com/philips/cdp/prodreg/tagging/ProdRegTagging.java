/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.tagging;

import android.app.Activity;

import com.philips.cdp.product_registration_lib.BuildConfig;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;

import java.util.HashMap;
import java.util.Map;

public class ProdRegTagging {

    private static ProdRegTagging prodRegTagging;
    private static AppTaggingInterface aiAppTaggingInterface;

    private ProdRegTagging() {
    }

    public static ProdRegTagging getInstance() {
        if (prodRegTagging == null) {
            prodRegTagging = new ProdRegTagging();
        }
        return prodRegTagging;
    }

    @SuppressWarnings("deprecation")
    public static void init(AppInfra appInfra) {
        aiAppTaggingInterface = appInfra.getTagging().createInstanceForComponent("prg", BuildConfig.VERSION_NAME);
    }

    public AppTaggingInterface getAiAppTaggingInterface() {
        return aiAppTaggingInterface;
    }

    public void trackPage(String pageName) {
        try {
            getAiAppTaggingInterface().trackPageWithInfo(pageName, null);
        } catch (IllegalArgumentException e) {

        }
    }

    public void trackAction(String event, String key, String value) {
        final Map<String, String> commonGoalsMap = new HashMap<>();
        commonGoalsMap.put(key, value);
        getAiAppTaggingInterface().trackActionWithInfo(event, commonGoalsMap);
    }

    public void pauseCollectingLifecycleData() {
        getAiAppTaggingInterface().pauseLifecycleInfo();
    }

    public void collectLifecycleData(Activity activity) {
        getAiAppTaggingInterface().collectLifecycleInfo(activity);
    }

}
