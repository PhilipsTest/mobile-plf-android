package com.philips.platform.ccdemouapp;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.digitalcare.CcDependencies;
import com.philips.cdp.digitalcare.CcInterface;
import com.philips.cdp.digitalcare.CcLaunchInput;
import com.philips.cdp.digitalcare.CcSettings;
import com.philips.cdp.digitalcare.listeners.CcListener;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.productselection.productselectiontype.HardcodedProductList;
import com.philips.cdp.productselection.productselectiontype.ProductModelSelectionType;
import com.philips.cdp.prxclient.PrxConstants.Catalog;
import com.philips.cdp.prxclient.PrxConstants.Sector;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.ccdemouapp.util.ThemeHelper;
import com.philips.platform.ccdemouapplibrary.R;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDLangPackActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Created by 310166779 on 3/21/2017.
 */
public class CCDemoUAppFragmentActivity extends UIDLangPackActivity implements View.OnClickListener,
        CcListener {
    private static final String TAG = CCDemoUAppFragmentActivity.class.getSimpleName();
    private ImageView mActionBarMenuIcon = null;
    private ImageView mActionBarArrow = null;
    private TextView mActionBarTitle = null;
    private FragmentManager mFragmentManager = null;
    private CcSettings ccSettings = null;
    private CcLaunchInput ccLaunchInput = null;
    private  AppInfraInterface mAppInfraInterface;
    private ThemeHelper themeHelper;

    @SuppressWarnings("serial")
    private ActionBarListener actionBarListener = new ActionBarListener() {
        @Override
        public void updateActionBar(@IdRes int i, boolean b) {
            updateActionBar(getResources().getString(i), b);
        }

        @Override
        public void updateActionBar(String title, boolean actionBarLeftArrow) {

            // Toast.makeText(MicroAppFragmentActivity.this, title, Toast.LENGTH_SHORT).show();
            mActionBarTitle.setText(title);
            if (actionBarLeftArrow) {
                enableActionBarLeftArrow();
            } else {
                enableActionBarHome();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initTheme();
        if(savedInstanceState!=null){
            // if app killed by vm.
            savedInstanceState =null;
            finish();
            super.onCreate(savedInstanceState);
            return;
        }
        super.onCreate(savedInstanceState);
        mAppInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;

//        getSupportActionBar().hide();
        //setTheme(R.style.Theme_Philips_DarkBlue_WhiteBackground);
        //setTheme(mThemeUtil.getCurrentTheme());

        DigiCareLogger.i(TAG, " onCreate ++ ");
        setContentView(R.layout.activity_sample);

        if (CCDemoUAppActivity.mList != null) {
            String[] ctnList = new String[CCDemoUAppActivity.mList.size()];
            for (int i = 0; i < CCDemoUAppActivity.mList.size(); i++)
                ctnList[i] = CCDemoUAppActivity.mList.get(i);

            ProductModelSelectionType productsSelection = new HardcodedProductList(ctnList);
            productsSelection.setCatalog(Catalog.CARE);
            productsSelection.setSector(Sector.B2C);


            /*FragmentLauncher fragLauncher = new FragmentLauncher(
                    this, R.id.sampleMainContainer, actionBarClickListener);
            // fragLauncher.setAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            //Testing purpose (Fragments Launch without Animation)
            fragLauncher.setAnimation(0, 0);

            DigitalCareConfigManager.getInstance().invokeDigitalCare(fragLauncher, productsSelection);*/

            final com.philips.platform.uappframework.launcher.FragmentLauncher launcher =
                    new com.philips.platform.uappframework.launcher.FragmentLauncher
                            (this, R.id.sampleMainContainer, actionBarListener);
            launcher.setCustomAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

           /* CcInterface ccInterface = new CcInterface();
            ccInterface.init(this, null);
            ccInterface.launch(launcher, productsSelection, this);*/
            //mAppInfraInterface = new AppInfra.Builder().build(getApplicationContext());

            final CcInterface ccInterface = new CcInterface();
            if (ccSettings == null) ccSettings = new CcSettings(getApplicationContext());
            if (ccLaunchInput == null) ccLaunchInput = new CcLaunchInput();
            ccLaunchInput.setProductModelSelectionType(productsSelection);
            ccLaunchInput.setConsumerCareListener(this);

            CcDependencies ccDependencies = new CcDependencies(mAppInfraInterface);

            ccInterface.init(ccDependencies, ccSettings);
            //ccInterface.launch(launcher, ccLaunchInput);

            if(mAppInfraInterface == null) {
                mAppInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;
            }
            else {
                mAppInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
                    @Override
                    public void onSuccess(String s, SOURCE source) {
                        if (s.equals("CN")) {
                            ccLaunchInput.setLiveChatUrl("http://ph-china.livecom.cn/webapp/index.html?app_openid=ph_6idvd4fj&token=PhilipsTest");
                        } else {
                            ccLaunchInput.setLiveChatUrl(null);
                        }
                        ccInterface.launch(launcher, ccLaunchInput);
                    }

                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {
                        ccInterface.launch(launcher, ccLaunchInput);
                    }
                });
            }
            try {
                initActionBar();
            } catch (ClassCastException e) {
                DigiCareLogger.e(TAG, "SampleActivity Actionbar: " + e.getMessage());
            }
            enableActionBarHome();

            // DigitalCareConfigManager.getInstance();
            mFragmentManager = getSupportFragmentManager();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DigiCareLogger.e(TAG, "CCDemoUAppFragmentActivity.class");
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

    protected void initActionBar() throws ClassCastException {
        mActionBarMenuIcon = (ImageView) findViewById(R.id.sample_home_icon);
        mActionBarArrow = (ImageView) findViewById(R.id.sample_back_to_home_img);
        mActionBarTitle = (TextView) findViewById(R.id.sample_action_bar_title);

        mActionBarMenuIcon.setOnClickListener(this);
        mActionBarArrow.setOnClickListener(this);
    }

    private boolean previousFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            removeFromStack();
        }
        return true;
    }

    private void removeFromStack() {
        mFragmentManager.popBackStack();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        removeCurrentFragment(transaction);
    }

    private void removeCurrentFragment(FragmentTransaction transaction) {


        Fragment currentFrag = mFragmentManager
                .findFragmentById(R.id.mainContainer);

        if (currentFrag != null) {
            transaction.remove(currentFrag);
        }
        transaction.commit();
    }

    private void enableActionBarLeftArrow() {
        mActionBarMenuIcon.setVisibility(View.GONE);
        mActionBarArrow.setVisibility(View.VISIBLE);
        mActionBarArrow.bringToFront();
    }

    private void enableActionBarHome() {
        mActionBarMenuIcon.setVisibility(View.VISIBLE);
        mActionBarMenuIcon.bringToFront();
        mActionBarArrow.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return previousFragment();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        int _id = view.getId();
        if (_id == R.id.sample_home_icon) {
            finish();
        } else if (_id == R.id.sample_back_to_home_img)
            previousFragment();
    }



    protected void initTheme() {
        UIDHelper.injectCalligraphyFonts();
        themeHelper = new ThemeHelper(this);
        ThemeConfiguration config = themeHelper.getThemeConfig();
        config.add(AccentRange.ORANGE);
        setTheme(themeHelper.getThemeResourceId());
        UIDHelper.init(config);
    }

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}

