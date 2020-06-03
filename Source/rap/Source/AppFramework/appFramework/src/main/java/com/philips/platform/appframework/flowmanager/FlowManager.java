/*
 *  Copyright (c) Koninklijke Philips N.V., 2017
 *  All rights are reserved. Reproduction or dissemination
 *  in whole or in part is prohibited without the prior written
 *  consent of the copyright holder.
 */
package com.philips.platform.appframework.flowmanager;

import com.philips.platform.appframework.flowmanager.base.BaseCondition;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.stateimpl.DemoAILState;
import com.philips.platform.appframework.stateimpl.DemoDCCState;
import com.philips.platform.appframework.stateimpl.DemoECSState;
import com.philips.platform.appframework.stateimpl.DemoECSTestState;
import com.philips.platform.appframework.stateimpl.DemoIAPState;
import com.philips.platform.appframework.stateimpl.DemoMECState;
import com.philips.platform.appframework.stateimpl.DemoPIMState;
import com.philips.platform.appframework.stateimpl.DemoPRGState;
import com.philips.platform.appframework.stateimpl.DemoUSRState;
import com.philips.platform.appframework.stateimpl.HamburgerActivityState;
import com.philips.platform.appframework.stateimpl.TestFragmentState;
import com.philips.platform.baseapp.FlowManagerUtil;
import com.philips.platform.baseapp.condition.ConditionAppLaunch;
import com.philips.platform.baseapp.condition.ConditionCookiesConsent;
import com.philips.platform.baseapp.condition.ConditionIsDonePressed;
import com.philips.platform.baseapp.condition.ConditionIsLoggedIn;
import com.philips.platform.baseapp.screens.cookiesconsent.CookiesConsentState;
import com.philips.platform.countryselection.CountrySelectionState;

import java.util.Map;


public class FlowManager extends BaseFlowManager {

    public void populateStateMap(final Map<String, BaseState> uiStateMap) {
        new FlowManagerUtil().addValuesToMap(uiStateMap);
        uiStateMap.put(AppStates.HAMBURGER_HOME, new HamburgerActivityState());
        uiStateMap.put(AppStates.TEST_MICROAPP, new TestFragmentState());
        uiStateMap.put(AppStates.TESTAPPINFRA, new DemoAILState());
        uiStateMap.put(AppStates.TESTUR, new DemoUSRState());
        uiStateMap.put(AppStates.TESTPR, new DemoPRGState());
        uiStateMap.put(AppStates.TESTIAP, new DemoIAPState());
        uiStateMap.put(AppStates.TESTMEC, new DemoMECState());
        uiStateMap.put(AppStates.TESTECS, new DemoECSState());
        uiStateMap.put(AppStates.TESTECSTEST, new DemoECSTestState());
        uiStateMap.put(AppStates.TESTPIM, new DemoPIMState());
        uiStateMap.put(AppStates.TESTCC, new DemoDCCState());
        uiStateMap.put(AppStates.COOKIES_CONSENT, new CookiesConsentState());
        uiStateMap.put(AppStates.COUNTRY_SELECTION,new CountrySelectionState());

    }

    public void populateConditionMap(final Map<String, BaseCondition> baseConditionMap) {
        baseConditionMap.put(AppConditions.IS_LOGGED_IN, new ConditionIsLoggedIn());
        baseConditionMap.put(AppConditions.IS_DONE_PRESSED, new ConditionIsDonePressed());
        baseConditionMap.put(AppConditions.CONDITION_APP_LAUNCH, new ConditionAppLaunch());
        baseConditionMap.put(AppConditions.LAUNCH_COOKIES_CONSENT, new ConditionCookiesConsent());
    }

}
