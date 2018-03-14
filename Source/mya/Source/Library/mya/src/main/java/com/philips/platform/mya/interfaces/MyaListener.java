/*
 * Copyright (c) Koninklijke Philips N.V. 2017
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.mya.interfaces;


import android.app.Activity;

import com.philips.platform.mya.error.MyaError;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import java.io.Serializable;

public interface MyaListener extends Serializable {

    boolean onSettingsMenuItemSelected(FragmentLauncher fragmentLauncher, String itemName);

    boolean onProfileMenuItemSelected(FragmentLauncher fragmentLauncher, String itemName);

    void onError(Activity activity, MyaError myaError);

    void onLogoutClicked(Activity activity, MyaLogoutListener myaLogoutListener);

    interface MyaLogoutListener{
        void onLogoutSuccess();
        void onLogOutFailure();
    }

}
