/*
 * Copyright (c) Koninklijke Philips N.V. 2019
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */

package com.philips.platform.pim;


import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.uappframework.uappinput.UappLaunchInput;

import java.util.HashMap;

/**
 * This class is used to provide input parameters and customizations for PIM.
 *
 * @since TODO: Version needs to implement
 */
public class PIMLaunchInput extends UappLaunchInput {

    private static final long serialVersionUID = 1355617433017768480L;
    private UserLoginListener userLoginListener;
    private PIMLaunchFlow pimLaunchFlow;

    private HashMap<PIMParameterToLaunchEnum, Object> parameterToLaunch;

    UserLoginListener getUserLoginListener() {
        return userLoginListener;
    }

    public void setUserLoginListener(UserLoginListener userLoginListener) {
        this.userLoginListener = userLoginListener;
    }

    public HashMap<PIMParameterToLaunchEnum, Object> getParameterToLaunch() {
        return parameterToLaunch;
    }

    public void setParameterToLaunch(HashMap<PIMParameterToLaunchEnum, Object> parameterToLaunch) {
        this.parameterToLaunch = parameterToLaunch;
    }

    public void setPIMLaunchFlow(PIMLaunchFlow pimLaunchFlow) {
        this.pimLaunchFlow = pimLaunchFlow;
    }

    public PIMLaunchFlow getPimLaunchFlow() {
        return pimLaunchFlow;
    }
}
