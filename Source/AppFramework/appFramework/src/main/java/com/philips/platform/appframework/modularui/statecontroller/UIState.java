package com.philips.platform.appframework.modularui.statecontroller;

import com.philips.platform.appframework.modularui.util.UIConstants;

/**
 * Created by 310240027 on 6/16/2016.
 */
public class UIState extends UIStateBase {

    UIBaseNavigation navigator;
    @UIConstants.UIStateDef
    int stateID;

    @Override
    public UIBaseNavigation getNavigator() {
        return navigator;
    }

    @Override
    public void setNavigator(UIBaseNavigation navigator) {
        this.navigator = navigator;
    }

    @Override
    public int getStateID() {
        return stateID;
    }

    @Override
    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

}
