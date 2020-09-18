package com.philips.cdp.productselection.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.philips.cdp.productselection.ProductModelSelectionHelper;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.platform.uid.utils.UIDActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


//import com.philips.cdp.ui.catalog.themeutils.ThemeUtils;

/**
 * ProductSelectionBaseActivity is the main container class which can contain Digital Care fragments.
 *
 * @author : Ritesh.jha@philips.com
 *         naveen@philips.com
 * @since : 20 Jan 2016
 */
public abstract class ProductSelectionBaseActivity extends UIDActivity {
    private static String TAG = ProductSelectionBaseActivity.class.getSimpleName();
    private FragmentManager fragmentManager = null;
    private ProductModelSelectionHelper mProductModelSelectionHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ProductModelSelectionHelper.getInstance();
        fragmentManager = getSupportFragmentManager();
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
                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProductModelSelectionHelper.getInstance().getTaggingInterface().collectLifecycleInfo(this);
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

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
