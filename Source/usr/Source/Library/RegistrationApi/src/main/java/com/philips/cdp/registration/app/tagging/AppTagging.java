/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.app.tagging;

import android.app.Activity;

import androidx.annotation.VisibleForTesting;

import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.appinfra.tagging.ErrorCategory;
import com.philips.platform.appinfra.tagging.TaggingError;

import java.util.HashMap;
import java.util.Map;


public class AppTagging {

    private static AppTaggingInterface appTaggingInterface;

    public static void init() {
        appTaggingInterface = RegistrationConfiguration.getInstance().getComponent().getAppTaggingInterface();
        appTaggingInterface = appTaggingInterface.createInstanceForComponent(RegConstants.COMPONENT_TAGS_ID, RegistrationHelper.getRegistrationApiVersion());
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

    public static void trackInformationError(String tag) {
        appTaggingInterface.trackErrorAction(ErrorCategory.INFORMATIONAL_ERROR, getCommonGoalsMap(), new TaggingError(tag));
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
