/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.di.iap.Fragments.BaseAnimationSupportFragment;
import com.philips.cdp.di.iap.Fragments.ProductCatalogFragment;
import com.philips.cdp.di.iap.Fragments.ProductDetailFragment;
import com.philips.cdp.di.iap.Fragments.PurchaseHistoryFragment;
import com.philips.cdp.di.iap.Fragments.ShoppingCartFragment;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.uikit.UiKitActivity;

import java.util.List;
import java.util.Locale;

public class IAPActivity extends UiKitActivity {
    private final int DEFAULT_THEME = R.style.Theme_Philips_DarkBlue_WhiteBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iap_activity);
        initTheme();
        addActionBar();

        if (savedInstanceState == null) {
            setLocale();
            int landingScreen = getIntent().getIntExtra(IAPConstant.IAP_IS_SHOPPING_CART_VIEW_SELECTED, -1);

            if (landingScreen == IAPConstant.IAPLandingViews.IAP_PRODUCT_CATALOG_VIEW) {
                addFragment(ProductCatalogFragment.createInstance(new Bundle(),
                        BaseAnimationSupportFragment.AnimationType.NONE), ProductCatalogFragment.TAG);
            } else if (landingScreen == IAPConstant.IAPLandingViews.IAP_SHOPPING_CART_VIEW) {
                addFragment(ShoppingCartFragment.createInstance(new Bundle(),
                        BaseAnimationSupportFragment.AnimationType.NONE), ShoppingCartFragment.TAG);
            } else if (landingScreen == IAPConstant.IAPLandingViews.IAP_PURCHASE_HISTORY_VIEW) {
                addFragment(PurchaseHistoryFragment.createInstance(new Bundle(),
                        BaseAnimationSupportFragment.AnimationType.NONE), PurchaseHistoryFragment.TAG);
            } else if (landingScreen == IAPConstant.IAPLandingViews.IAP_PRODUCT_DETAIL_VIEW) {
                if (getIntent().hasExtra(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER,
                            getIntent().getStringExtra(IAPConstant.IAP_PRODUCT_CATALOG_NUMBER));
                    addFragment(ProductDetailFragment.createInstance(bundle,
                            BaseAnimationSupportFragment.AnimationType.NONE), ProductDetailFragment.TAG);
                }

            }
        }

    }

    private void initTheme() {
        int themeIndex = getIntent().getIntExtra(IAPConstant.IAP_KEY_ACTIVITY_THEME, DEFAULT_THEME);
        //Handle invalid index
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME;
        }
        setTheme(themeIndex);
    }

    private void setLocale() {
        PILLocaleManager localeManager = new PILLocaleManager(getApplicationContext());
        String localeAsString = localeManager.getInputLocale();
        if (localeAsString != null) {
            String[] localeArray = localeAsString.split("_");
            Locale locale = new Locale(localeArray[0], localeArray[1]);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config,
                    getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onDestroy() {
        Utility.dismissProgressDialog();
        NetworkUtility.getInstance().dismissErrorDialog();
        super.onDestroy();
    }

    public void addFragment(BaseAnimationSupportFragment newFragment,
                            String newFragmentTag) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_mainFragmentContainer, newFragment, newFragmentTag);
        transaction.addToBackStack(newFragmentTag);
        transaction.commitAllowingStateLoss();

        IAPLog.d(IAPLog.LOG, "Add fragment " + newFragment.getClass().getSimpleName() + "   ("
                + newFragmentTag + ")");
    }


    private void addActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        IAPLog.d(IAPLog.BASE_FRAGMENT_ACTIVITY, "IAPActivity == onCreate");
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        View mCustomView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.iap_action_bar, null); // layout which contains your button.
        ViewGroup mUPButtonLayout = (ViewGroup) mCustomView.findViewById(R.id.iap_header_back_button);
        mUPButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public void onBackPressed() {
        IAPLog.i(IAPLog.LOG, "OnBackpressed Called");
        Utility.hideKeypad(this);
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.SPECIAL_EVENTS, IAPAnalyticsConstant.BACK_BUTTON_PRESS);
        boolean dispatchBackHandled = dispatchBackToFragments();
        if (!dispatchBackHandled)
            super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CartModelContainer.getInstance().resetApplicationFields();
    }

    public boolean dispatchBackToFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        IAPLog.i(IAPLog.LOG, "OnBackpressed dispatchBackToFragments Called = " + fragments);
        boolean isBackHandled = false;
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && (fragment instanceof IAPBackButtonListener)) {

                isBackHandled = ((IAPBackButtonListener) fragment).onBackPressed();
                IAPLog.i(IAPLog.LOG, "OnBackpressed dispatchBackToFragments Called");
            }
        }
        return isBackHandled;
    }

    @Override
    protected void onPause() {
        IAPAnalytics.pauseCollectingLifecycleData();
        super.onPause();
    }

    @Override
    protected void onResume() {
        IAPAnalytics.collectLifecycleData();
        super.onResume();
    }

}
