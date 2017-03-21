/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.screens.homescreen;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.flowmanager.AppStates;
import com.philips.platform.screens.base.FragmentView;
import com.philips.platform.screens.base.UIBasePresenter;
import com.philips.platform.uappdemolibrary.R;
import com.philips.platform.uappframework.launcher.FragmentLauncher;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * This class id used for loading various fragments that are supported by home activity ,
 * based on user selection this class loads the next state of the application.
 */
public class HamburgerActivityPresenter extends UIBasePresenter {

    private FragmentView fragmentView;
    private FragmentLauncher fragmentLauncher;
    private BaseState baseState;
    private final int SAMPLE = 2;

    public HamburgerActivityPresenter(final FragmentView fragmentView) {
        super(fragmentView);
        this.fragmentView = fragmentView;
        setState(AppStates.HAMBURGER_HOME);
    }

    /**
     * This methods handles all click events done on hamburger menu
     * Any changes for hamburger menu options should be made here
     */
    @Override
    public void onEvent(int componentID) {
        String eventState = getEventState(componentID);
        try {
            BaseFlowManager targetFlowManager = fragmentView.getTargetFlowManager();
            baseState = targetFlowManager.getNextState(targetFlowManager.getState(AppStates.HAMBURGER_HOME), eventState);
            if (null != baseState) {
                baseState.setUiStateData(setStateData(baseState.getStateID()));
                fragmentLauncher = getFragmentLauncher();
                baseState.navigate(fragmentLauncher);
            }
        }  catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            Log.d(getClass() + "", e.getMessage());
            Toast.makeText(fragmentView.getFragmentActivity(), fragmentView.getFragmentActivity().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    protected ArrayList<String> getCtnList() {
        return new ArrayList<>(Arrays.asList(fragmentView.getFragmentActivity().getResources().getStringArray(R.array.iap_productselection_ctnlist)));
    }

    protected FragmentLauncher getFragmentLauncher() {
        fragmentLauncher = new FragmentLauncher(fragmentView.getFragmentActivity(), fragmentView.getContainerId(), fragmentView.getActionBarListener());
        return fragmentLauncher;
    }

    // TODO: Deepthi, is this expected? deviation from ios i think. - (Rakesh -As disscussed. Needed to convert int ID for views into Strings )
    public String getEventState(int componentID) {

        switch (componentID) {
            case MENU_OPTION_HOME:
                return HOME_FRAGMENT;
            case MENU_OPTION_ABOUT:
                return HOME_ABOUT;
            case SAMPLE:
                return "sample";
            // Commented as part of Plan A removal.
           /* case Constants.UI_SHOPPING_CART_BUTTON_CLICK:
                return SHOPPING_CART;*/
            default:
                return HOME_FRAGMENT;
        }
    }
}
