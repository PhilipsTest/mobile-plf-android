/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.modularui.stateimpl;

import android.content.Context;

import com.philips.platform.appframework.AppFrameworkBaseActivity;
import com.philips.platform.modularui.cocointerface.UICoCoUserRegImpl;
import com.philips.platform.modularui.factorymanager.CoCoFactory;
import com.philips.platform.modularui.statecontroller.UIState;
import com.philips.platform.modularui.util.UIConstants;

public class UserRegistrationState extends UIState {
    UICoCoUserRegImpl uiCoCoUserReg;
    public UserRegistrationState(@UIStateDef int stateID) {
        super(stateID);
    }

    @Override
    public void navigate(Context context) {
        uiCoCoUserReg = (UICoCoUserRegImpl) CoCoFactory.getInstance().getCoCo(UIConstants.UI_COCO_USER_REGISTRATION);
        uiCoCoUserReg.loadPlugIn(context);
        uiCoCoUserReg.runCoCo(context);
    }

    @Override
    public void back(final Context context) {
        ((AppFrameworkBaseActivity)context).popBack();
    }
}
