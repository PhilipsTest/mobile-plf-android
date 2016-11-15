package com.philips.platform.flowmanager.condition;

import android.content.Context;

import com.philips.platform.appframework.AppFrameworkApplication;
import com.philips.platform.modularui.statecontroller.BaseAppCondition;
import com.philips.platform.modularui.statecontroller.BaseUiFlowManager;

public class ConditionAppLaunch implements BaseCondition {
    @Override
    public boolean isSatisfied(final Context context) {
        AppFrameworkApplication appFrameworkApplication = (AppFrameworkApplication) context;
        final BaseUiFlowManager targetFlowManager = appFrameworkApplication.getTargetFlowManager();
        final boolean isUserLoggedIn = targetFlowManager.getCondition(BaseAppCondition.IS_LOGGED_IN).isSatisfied(context);
        final boolean isDonePressed = targetFlowManager.getCondition(BaseAppCondition.IS_DONE_PRESSED).isSatisfied(context);
        return isDonePressed && !isUserLoggedIn;
    }
}
