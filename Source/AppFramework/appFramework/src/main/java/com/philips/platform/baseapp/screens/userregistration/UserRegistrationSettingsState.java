/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.userregistration;

import android.util.Log;
import android.widget.Toast;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

public class UserRegistrationSettingsState extends UserRegistrationState {

    private BaseState baseState;
    private String SETTINGS_LOGOUT = "logout";
    /**
     * AppFlowState constructor
     *
     */
    public UserRegistrationSettingsState() {
        super(AppStates.SETTINGS_REGISTRATION);
    }


    @Override
    public void onUserLogoutSuccess() {
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        try {
            baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), SETTINGS_LOGOUT);
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            Log.d(getClass() + "", e.getMessage());
            Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
        }
        if (baseState != null)
            baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
    }

}
