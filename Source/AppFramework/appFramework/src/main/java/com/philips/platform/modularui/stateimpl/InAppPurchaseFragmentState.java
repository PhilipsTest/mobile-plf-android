/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.modularui.stateimpl;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.platform.appframework.AppFrameworkBaseActivity;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.homescreen.HomeActivity;
import com.philips.platform.appinfra.AppInfraSingleton;
import com.philips.platform.modularui.statecontroller.UIState;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;
import java.util.Arrays;

public class InAppPurchaseFragmentState extends UIState{

    Context mContext;
    private FragmentActivity fragmentActivity;
    private int containerID;
    private ArrayList<String> mCtnList = null;
    private TextView mTitleTextView;
    private ImageView mBackImage;
    private ImageView mCartIcon;
    private TextView mCountText;
    private ActionBarListener actionBarListener;

    public InAppPurchaseFragmentState(@UIStateDef int stateID) {
        super(stateID);
    }

    @Override
    public void navigate(Context context) {
        mContext = context;
        if(context instanceof HomeActivity) {
            fragmentActivity = (HomeActivity) context;
            containerID = R.id.frame_container;
            actionBarListener = (HomeActivity)context;
        }
        if (mCtnList == null) {
            mCtnList = new ArrayList<>(Arrays.asList(fragmentActivity.getResources().getStringArray(R.array.productselection_ctnlist)));
        }
        launchIAP();
    }

    private void launchIAP() {
        IAPInterface iapInterface = new IAPInterface();
        IAPSettings iapSettings = new IAPSettings(fragmentActivity);
        IAPDependencies iapDependencies = new IAPDependencies(AppInfraSingleton.getInstance());
        iapSettings.setUseLocalData(false);
        iapInterface.init(iapDependencies, iapSettings);
        IAPFlowInput iapFlowInput = new IAPFlowInput(mCtnList);
        IAPLaunchInput iapLaunchInput = new IAPLaunchInput();
        iapLaunchInput.setIAPFlow(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, iapFlowInput);
        FragmentLauncher fragLauncher = new FragmentLauncher(fragmentActivity, containerID,actionBarListener);
        try {
            iapInterface.launch(fragLauncher, iapLaunchInput);

        } catch (RuntimeException e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void back(final Context context) {
        ((AppFrameworkBaseActivity)context).popBackTillHomeFragment();
    }


}
