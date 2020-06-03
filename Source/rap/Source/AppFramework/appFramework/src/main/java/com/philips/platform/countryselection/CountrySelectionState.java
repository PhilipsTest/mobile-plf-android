package com.philips.platform.countryselection;

import android.content.Context;
import android.os.Bundle;

import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.stateimpl.DemoBaseState;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.screens.splash.SplashFragment;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

public class CountrySelectionState extends DemoBaseState {

    public CountrySelectionState() {
        super(AppStates.COUNTRY_SELECTION);
    }

    @Override
    public void navigate(UiLauncher uiLauncher) {
        RALog.d("CountrySelectionState"," navigate called ");
        final FragmentLauncher fragmentLauncher = (FragmentLauncher) uiLauncher;
        CountrySelectionFragment countrySelectionFragment = new CountrySelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("eventId","launchRegistrationComponent");
        countrySelectionFragment.setArguments(bundle);
        ((AbstractAppFrameworkBaseActivity) fragmentLauncher.getFragmentActivity()).
                addFragment(countrySelectionFragment, CountrySelectionFragment.TAG);
    }

    @Override
    public void init(Context context) {


    }

    @Override
    public void updateDataModel() {

    }
}
