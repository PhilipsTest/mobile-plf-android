package com.philips.cdp.di.iap.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.di.iap.Fragments.ProductCatalogFragment;
import com.philips.cdp.di.iap.Fragments.ShoppingCartFragment;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.IAPLog;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.tagging.Tagging;
import com.philips.cdp.uikit.UiKitActivity;
import com.philips.cdp.uikit.drawable.VectorDrawable;

import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class IAPActivity extends UiKitActivity implements IAPFragmentListener {
    private final int DEFAULT_THEME = R.style.Theme_Philips_DarkBlue_WhiteBackground;
    private TextView mTitleTextView;
    private ImageView mBackButton;
    private FrameLayout frameLayout;
    private TextView mCartCount;
    private ImageView mCartIcon;
    private FrameLayout mCartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iap_activity);
        addActionBar();
        Boolean isShoppingCartViewSelected = getIntent().getBooleanExtra(IAPConstant.IAP_IS_SHOPPING_CART_VIEW_SELECTED, true);
        if (isShoppingCartViewSelected)
            addShoppingFragment();
        else
            addProductCatalog();
    }

    private void initTheme() {
        int themeIndex = getIntent().getIntExtra(IAPConstant.IAP_KEY_ACTIVITY_THEME, DEFAULT_THEME);
        //Handle invalid index
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME;
        }
        setTheme(themeIndex);
    }

    private void addShoppingFragment() {
        //Track first page of InAppPurchase
        IAPAnalytics.trackLaunchPage(IAPAnalyticsConstant.SHOPPING_CART_PAGE_NAME);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_mainFragmentContainer, new ShoppingCartFragment());
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void addProductCatalog() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_mainFragmentContainer, new ProductCatalogFragment(),ProductCatalogFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
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
        Drawable mShoppingCartIcon = VectorDrawable.create(this, R.drawable.iap_shopping_cart);
        View mCustomView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.iap_action_bar, null); // layout which contains your button.

        mTitleTextView = (TextView) mCustomView.findViewById(R.id.text);
        mCartIcon = (ImageView) mCustomView.findViewById(R.id.cart_icon);
        mCartIcon.setImageDrawable(mShoppingCartIcon);
        mBackButton = (ImageView) mCustomView.findViewById(R.id.arrow);
        mBackButton.setImageDrawable(VectorDrawable.create(this, R.drawable.uikit_up_arrow));
        mCartCount = (TextView) mCustomView.findViewById(R.id.item_count);
        frameLayout = (FrameLayout) mCustomView.findViewById(R.id.UpButton);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
        mCartContainer = (FrameLayout) mCustomView.findViewById(R.id.cart_container);
        mCartContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                addShoppingFragment();
            }
        });

        mActionBar.setCustomView(mCustomView, params);
        mActionBar.setDisplayShowCustomEnabled(true);

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public void onBackPressed() {
        IAPLog.i(IAPLog.LOG, "OnBackpressed Called");
        Utility.hideKeypad(this);
        Tagging.trackAction(IAPAnalyticsConstant.SEND_DATA,
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

    @Override
    public void setHeaderTitle(final int pResourceId) {
        mTitleTextView.setText(pResourceId);
    }

    @Override
    public void updateCount(final int count) {
        if (count == 0) {
            mCartCount.setVisibility(View.GONE);
        } else {
            mCartCount.setVisibility(View.VISIBLE);
            mCartCount.setText(String.valueOf(count));
        }
    }

    @Override
    public void setCartIconVisibility(final int visibility) {
        mCartContainer.setVisibility(visibility);
        mCartIcon.setVisibility(visibility);
        mCartIcon.setVisibility(visibility);
    }

    @Override
    public void setBackButtonVisibility(final int isVisible) {
        if (isVisible == View.GONE) {
            frameLayout.setEnabled(false);
            frameLayout.setClickable(false);
        } else if (isVisible == View.VISIBLE) {
            frameLayout.setEnabled(true);
            frameLayout.setClickable(true);
        }
        mBackButton.setVisibility(isVisible);
    }

    @Override
    public void setHeaderTitle(final String title) {
        mTitleTextView.setText(title);
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
        Tagging.pauseCollectingLifecycleData();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Tagging.collectLifecycleData();
        super.onResume();
    }
}
