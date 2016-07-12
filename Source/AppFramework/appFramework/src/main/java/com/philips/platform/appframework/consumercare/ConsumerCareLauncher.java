/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/

package com.philips.platform.appframework.consumercare;

import android.support.v4.app.FragmentActivity;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.listeners.MainMenuListener;
import com.philips.cdp.digitalcare.productdetails.ProductMenuListener;
import com.philips.cdp.digitalcare.social.SocialProviderListener;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.productselection.launchertype.FragmentLauncher;
import com.philips.cdp.productselection.listeners.ActionbarUpdateListener;
import com.philips.cdp.productselection.productselectiontype.ProductModelSelectionType;
import com.philips.platform.appframework.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ConsumerCareLauncher is to integrate ConsumerCare horizontal module.
 */
public class ConsumerCareLauncher implements MainMenuListener,
        ProductMenuListener, SocialProviderListener {
    private static String TAG = ConsumerCareLauncher.class.getSimpleName();
    private ArrayList<String> mCtnList = null;
    private FragmentActivity mContext = null;
    private ActionbarUpdateListener mActionBarClickListener = null;

    private void initializeDigitalCareLibrary() {
        PILLocaleManager localeManager = new PILLocaleManager(mContext);
        localeManager.setInputLocale("en", "GB");

        DigitalCareConfigManager.getInstance().setAppTaggingInputs(true, "App_ID_101", "AppName", "CurrentPageName");

        // Initializing DigitalCare Component.
        DigitalCareConfigManager.getInstance().initializeDigitalCareLibrary(
                mContext);

        // Set DigitalCareLibrary Listeners
        DigitalCareConfigManager.getInstance().registerMainMenuListener(this);
        DigitalCareConfigManager.getInstance()
                .registerProductMenuListener(this);
        DigitalCareConfigManager.getInstance().registerSocialProviderListener(this);

        DigiCareLogger.enableLogging();
    }

    public void initCC(FragmentActivity context, ActionbarUpdateListener actionBarClickListener) {
        mContext = context;
        mActionBarClickListener = actionBarClickListener;

        if (mCtnList == null) {
            mCtnList = new ArrayList<String>(Arrays.asList(mContext.getResources().getStringArray(R.array.productselection_ctnlist)));
        }

        if (mCtnList != null) {
            String[] ctnList = new String[mCtnList.size()];
            for (int i = 0; i < mCtnList.size(); i++) {
                ctnList[i] = mCtnList.get(i);
            }

            ProductModelSelectionType productsSelection = new com.philips.cdp.productselection
                    .productselectiontype.HardcodedProductList(ctnList);
            productsSelection.setCatalog(Catalog.CARE);
            productsSelection.setSector(Sector.B2C);

            initializeDigitalCareLibrary();

            FragmentLauncher fragLauncher = new FragmentLauncher(
                    mContext, R.id.frame_container, actionBarClickListener);
            fragLauncher.setAnimation(0, 0);
            DigitalCareConfigManager.getInstance().invokeDigitalCare(fragLauncher, productsSelection);
        }
    }

    @Override
    public boolean onMainMenuItemClicked(String mainMenuItem) {
        return false;
    }

    @Override
    public boolean onProductMenuItemClicked(String productMenu) {
        return false;
    }

    @Override
    public boolean onSocialProviderItemClicked(String socialProviderItem) {
        return false;
    }


    public void releaseConsumerCare() {
        DigitalCareConfigManager.getInstance().unregisterMainMenuListener(this);
        DigitalCareConfigManager.getInstance().unregisterProductMenuListener(this);
        DigitalCareConfigManager.getInstance().unregisterSocialProviderListener(this);
    }
}
