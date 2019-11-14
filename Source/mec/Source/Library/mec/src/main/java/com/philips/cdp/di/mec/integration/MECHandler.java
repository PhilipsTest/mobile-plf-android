/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.mec.integration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.di.mec.common.MECFragmentLauncher;
import com.philips.cdp.di.mec.common.MECLauncherActivity;
import com.philips.cdp.di.mec.utils.MECConstant;
import com.philips.cdp.di.mec.utils.MECDataHolder;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;

class MECHandler {
    private MECDependencies mMECDependencies;
    private MECSettings mMECSetting;
    private UiLauncher mUiLauncher;
    private MECLaunchInput mLaunchInput;

    MECHandler(MECDependencies pMECDependencies, MECSettings pMecSettings, UiLauncher pUiLauncher, MECLaunchInput pLaunchInput) {
        this.mMECDependencies = pMECDependencies;
        this.mMECSetting = pMecSettings;
        this.mUiLauncher = pUiLauncher;
        this.mLaunchInput = pLaunchInput;

    }


    void launchMEC() {
        AppInfra appInfra= (AppInfra) mMECDependencies.getAppInfra() ;
        AppConfigurationInterface configInterface = appInfra.getConfigInterface();
        AppConfigurationInterface.AppConfigurationError configError = new AppConfigurationInterface.AppConfigurationError();
        String propertyForKey = (String) configInterface.getPropertyForKey("propositionid", "MEC", configError);
        ECSServices ecsServices = new ECSServices(propertyForKey,appInfra);
        MecHolder.INSTANCE.eCSServices=ecsServices; // singleton
        MECDataHolder.INSTANCE.mecBannerEnabler = mLaunchInput.getMecBannerEnabler();
        if (mUiLauncher instanceof ActivityLauncher) {
            launchMECasActivity();
        } else {
            launchMECasFragment();
        }
    }


    protected void launchMECasActivity() {
        Intent intent = new Intent(mMECSetting.getContext(), MECLauncherActivity.class);
        intent.putExtra(MECConstant.INSTANCE.getMEC_LANDING_SCREEN(), mLaunchInput.getMLandingView());
        ActivityLauncher activityLauncher = (ActivityLauncher) mUiLauncher;
        Bundle bundle =  getBundle();
        bundle.putInt(MECConstant.INSTANCE.getMEC_KEY_ACTIVITY_THEME(), activityLauncher.getUiKitTheme());
        intent.putExtras(bundle);
        mMECSetting.getContext().startActivity(intent);

    }

    protected void launchMECasFragment() {
        FragmentLauncher fragmentLauncher = (FragmentLauncher) mUiLauncher;
        Bundle bundle =  getBundle();
        bundle.putInt("fragment_container", fragmentLauncher.getParentContainerResourceID()); // frame_layout for fragment
        loadDecisionFragment(bundle);


    }

     void loadDecisionFragment(Bundle bundle ){
        MECFragmentLauncher mecFragmentLauncher = new MECFragmentLauncher();
         mecFragmentLauncher.setArguments(bundle);

         FragmentLauncher fragmentLauncher = (FragmentLauncher)mUiLauncher;
         MECDataHolder.INSTANCE.setActionBarListener(fragmentLauncher.getActionbarListener(), mLaunchInput.getMecListener());
         String tag = mecFragmentLauncher.getClass().getName();
         FragmentTransaction transaction = fragmentLauncher.getFragmentActivity().getSupportFragmentManager().beginTransaction();
         transaction.replace(fragmentLauncher.getParentContainerResourceID(), mecFragmentLauncher, tag);
         transaction.commitAllowingStateLoss();

    }

    Bundle getBundle (){

        Bundle mBundle = new Bundle();
        if (mLaunchInput.getMMECFlowInput() != null) {
            if (mLaunchInput.getMMECFlowInput().getProductCTN() != null) {
                mBundle.putString(MECConstant.INSTANCE.getMEC_PRODUCT_CTN_NUMBER_FROM_VERTICAL(),
                        mLaunchInput.getMMECFlowInput().getProductCTN());
            }
            if (mLaunchInput.getMMECFlowInput().getProductCTNs() != null) {
                mBundle.putStringArrayList(MECConstant.INSTANCE.getCATEGORISED_PRODUCT_CTNS(),
                        mLaunchInput.getMMECFlowInput().getProductCTNs());
            }
            mBundle.putStringArrayList(MECConstant.INSTANCE.getMEC_IGNORE_RETAILER_LIST(), mLaunchInput.getIgnoreRetailers());
        }
        return mBundle;
    }


}
