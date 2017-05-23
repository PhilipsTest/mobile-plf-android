package com.philips.cdp.productselection.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.productselection.ProductModelSelectionHelper;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.cdp.productselection.utils.ThemeHelper;
import com.philips.cdp.uikit.UiKitActivity;
import com.philips.cdp.uikit.drawable.VectorDrawable;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.shamanland.fonticon.FontIconTypefaceHolder;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import com.philips.cdp.ui.catalog.themeutils.ThemeUtils;

/**
 * ProductSelectionBaseActivity is the main container class which can contain Digital Care fragments.
 *
 * @author : Ritesh.jha@philips.com
 *         naveen@philips.com
 * @since : 20 Jan 2016
 */
public abstract class ProductSelectionBaseActivity extends UiKitActivity {
    private static String TAG = ProductSelectionBaseActivity.class.getSimpleName();
    private FragmentManager fragmentManager = null;
    private ProductModelSelectionHelper mProductModelSelectionHelper = null;
    protected ThemeHelper themeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ProductModelSelectionHelper.getInstance();
        fragmentManager = getSupportFragmentManager();
        //UIDHelper.setupToolbar(this);
        //initActionBar();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backstackFragment();
               // onBackPressed();
                return true;
            default:
                break;
        }
        return true;
    }

    private void initActionBar() {
        ActionBar mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        View mCustomView = LayoutInflater.from(this).inflate(R.layout.actionbar_productselection, null); // layout which contains your button.

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.text);

        FrameLayout frameLayout = (FrameLayout) mCustomView.findViewById(R.id.productselection_UpButton);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                backstackFragment();
            }
        });

        ImageView arrowImage = (ImageView) mCustomView
                .findViewById(R.id.productselection_arrow);
        arrowImage.setImageDrawable(VectorDrawable.create(this, R.drawable.uikit_up_arrow));
        arrowImage.bringToFront();
        mActionBar.setCustomView(mCustomView, params);
        mActionBar.setDisplayShowCustomEnabled(true);

        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProductModelSelectionHelper.getInstance().getTaggingInterface().collectLifecycleInfo(this);
        // Tagging.collectLifecycleData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProductModelSelectionHelper.getInstance().getTaggingInterface().pauseLifecycleInfo();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return backstackFragment();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mProductModelSelectionHelper != null) {
            mProductModelSelectionHelper = null;
        }
    }

    private boolean backstackFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
//            enableActionBarHome();
            fragmentManager.popBackStack();
            removeCurrentFragment();
        }
        return true;
    }

    private void removeCurrentFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment currentFrag = fragmentManager
                .findFragmentById(R.id.mainContainer);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commit();
    }


    protected void showFragment(Fragment fragment) {
        try {
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();

            fragmentTransaction.replace(R.id.mainContainer, fragment, "tagname");
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            ProductSelectionLogger.e(TAG, "Fragment Transaction exception is handled " + e.getMessage());
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow() != null && getWindow().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    protected boolean isTablet() {
        DisplayMetrics metrics = new DisplayMetrics();
        try {
            if (this.getWindowManager() != null)
                this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        } catch (NullPointerException e) {
            ProductSelectionLogger.e(TAG, "V4 library known issue is catched");
        } finally {
            float yInches = metrics.heightPixels / metrics.ydpi;
            float xInches = metrics.widthPixels / metrics.xdpi;
            double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
            return diagonalInches >= 6.5;
        }
    }

    protected void backtoConsumerCare() {
        finish();
    }

    protected void initTheme() {
        UIDHelper.injectCalligraphyFonts();
        themeHelper = new ThemeHelper(this);
        ThemeConfiguration config = themeHelper.getThemeConfig();
        setTheme(themeHelper.getThemeResourceId());
        UIDHelper.init(config);
        FontIconTypefaceHolder.init(getAssets(), "fonts/puicon.ttf");
    }

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
