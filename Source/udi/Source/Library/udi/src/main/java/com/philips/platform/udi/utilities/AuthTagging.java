/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.platform.udi.utilities;

import android.app.Activity;
import android.support.annotation.VisibleForTesting;

import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.udi.configration.UdiConfiguration;

import java.util.HashMap;
import java.util.Map;


public class AuthTagging {

    private static AppTaggingInterface appTaggingInterface;

    public static void init(){
        appTaggingInterface = UdiConfiguration.getInstance().getComponent().getAppTaggingInterface();
        appTaggingInterface = appTaggingInterface.createInstanceForComponent(UdiConstants.COMPONENT_TAGS_ID, UdiConstants.getAppAuthApiVersion());
    }

    public static void trackPage(String currPage) {
        final Map<String, String> commonGoalsMap = getCommonGoalsMap();
        appTaggingInterface.trackPageWithInfo(currPage, commonGoalsMap);
    }

    public static void trackFirstPage(String currPage) {
        trackPage(currPage);
    }

    public static void trackAction(String state, String key, String value) {
        final Map<String, String> commonGoalsMap = getCommonGoalsMap();
        commonGoalsMap.put(key, value);
        appTaggingInterface.trackActionWithInfo(state, commonGoalsMap);
    }

    public static void trackMultipleActions(String state, Map<String, String> map) {
        final Map<String, String> commonGoalsMap = getCommonGoalsMap();
        commonGoalsMap.putAll(map);
        appTaggingInterface.trackActionWithInfo(state, map);
    }

    private static Map<String, String> getCommonGoalsMap() {
        return new HashMap<>();
    }

    public static void pauseCollectingLifecycleData() {
        appTaggingInterface.pauseLifecycleInfo();
    }

    public static void collectLifecycleData(Activity activity) {
        appTaggingInterface.collectLifecycleInfo(activity);
    }


    @VisibleForTesting
    public static void setMockAppTaggingInterface(AppTaggingInterface mockAppTaggingInterface) {
        appTaggingInterface = mockAppTaggingInterface;
    }
}
