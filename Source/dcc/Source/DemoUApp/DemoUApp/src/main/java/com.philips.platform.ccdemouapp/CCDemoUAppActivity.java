package com.philips.platform.ccdemouapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.digitalcare.CcDependencies;
import com.philips.cdp.digitalcare.CcInterface;
import com.philips.cdp.digitalcare.CcLaunchInput;
import com.philips.cdp.digitalcare.CcSettings;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.listeners.CcListener;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.productselection.productselectiontype.HardcodedProductList;
import com.philips.cdp.prxclient.PrxConstants.Catalog;
import com.philips.cdp.prxclient.PrxConstants.Sector;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.ccdemouapp.adapter.Listener;
import com.philips.platform.ccdemouapp.adapter.SampleAdapter;
import com.philips.platform.ccdemouapp.adapter.SimpleItemTouchHelperCallback;
import com.philips.platform.ccdemouapp.util.ThemeHelper;
import com.philips.platform.ccdemouapp.view.CustomDialog;
import com.philips.platform.ccdemouapplibrary.R;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class CCDemoUAppActivity extends FragmentActivity implements View.OnClickListener,
        CcListener {

    private static final String TAG = CCDemoUAppActivity.class.getSimpleName();
    public static ArrayList<String> mList = null;
    private static boolean mActivityButtonSelected = true;
    private static boolean mFragmentButtonSelected = true;
    private Button mLaunchDigitalCare = null;
    private Button mLaunchAsFragment = null;
    private Label mChangeTheme = null;
    private Button mAddButton = null;
    private RecyclerView mRecyclerView = null;
    private SampleAdapter adapter = null;


    private Spinner mCountry_spinner;
    private String mCountry[], mcountryCode[];
    private CcSettings ccSettings;
    private CcLaunchInput ccLaunchInput;
    private ThemeHelper themeHelper;

    private AppInfraInterface appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_care);


        mLaunchDigitalCare = (Button) findViewById(R.id.launchDigitalCare);
        mLaunchAsFragment = (Button) findViewById(R.id.launchAsFragment);
        mChangeTheme = (Label) findViewById(R.id.textViewChangeTheme);
        mAddButton = (Button) findViewById(R.id.addimageButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAddButton.setOnClickListener(this);

        // set listener
        mLaunchDigitalCare.setOnClickListener(this);
        mLaunchAsFragment.setOnClickListener(this);
        // setting country spinner
        mCountry_spinner = (Spinner) findViewById(R.id.spinner2);
        mCountry = getResources().getStringArray(R.array.dccuapp_country);
        mcountryCode = getResources().getStringArray(R.array.dccuapp_country_code);
        ArrayAdapter<String> mCountry_adapter = new ArrayAdapter<String>(this,
                R.layout.textview, mCountry);
        mCountry_spinner.setAdapter(mCountry_adapter);
        restoreCountryOption();

        // Ctn List Code Snippet

        if (mList == null)
            mList = new ArrayList<String>();
        if (mList.size() == 0)
            addCtnData();

        if (adapter == null)
            adapter = new SampleAdapter(mList);
        adapter = setAdapter(mList);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
    }


    private void addCtnData() {

        List<String> mCtnList = Arrays.asList(getResources().getStringArray(R.array.productselection_ctnlist));
        for (int i = 0; i < mCtnList.size(); i++) {
            mList.add(mCtnList.get(i));
        }
    }

    @Override
    protected void onDestroy() {
        DigitalCareConfigManager.getInstance().unRegisterCcListener(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView tv = (TextView) findViewById(R.id.textViewCurrentCountry);
        if(appInfraInterface == null) {
            appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;
        }
        else {
            appInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
                @Override
                public void onSuccess(String s, SOURCE source) {
                    tv.setText("Country from Service Discovery : " + s);
                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {

                }
            });
        }

        mCountry_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initializeDigitalCareLibrary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (mActivityButtonSelected) {
            mLaunchDigitalCare.setVisibility(View.VISIBLE);
        } else {
            mLaunchDigitalCare.setVisibility(View.INVISIBLE);
        }

        if (mFragmentButtonSelected) {
            mLaunchAsFragment.setVisibility(View.VISIBLE);
        } else {
            mLaunchAsFragment.setVisibility(View.INVISIBLE);
        }
    }

    @NonNull
    private SampleAdapter setAdapter(ArrayList<String> mList) {
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(mRecyclerView.getContext()));
        return adapter;
    }

    private void launchDialog() {
        CustomDialog dialog = new CustomDialog(this, mList, new Listener() {
            @Override
            public void updateList(ArrayList<String> productList) {
                mList = productList;
                setAdapter(mList);
                DigiCareLogger.d(TAG, " Products Size = " + mList.size());
            }
        });
        dialog.show();
    }

    private void restoreCountryOption() {
        if(appInfraInterface == null) {
            appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;
        }
        else {
            appInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
                @Override
                public void onSuccess(String s, SOURCE source) {
                    for (int i = 0; i < mcountryCode.length; i++) {
                        if (s.equalsIgnoreCase(mcountryCode[i])) {
                            mCountry_spinner.setSelection(i);
                        }
                    }
                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {
                }
            });
        }
    }

    private void initializeDigitalCareLibrary() {
/*

        if(!(mCountry_spinner.getSelectedItemId() == 0)){
            mAppInfraInterface.getServiceDiscovery().setHomeCountry(mcountryCode[mCountry_spinner.getSelectedItemPosition()]);
        }
*/
        /*if(appInfraInterface == null) {
            appInfraInterface = new AppInfra.Builder().build(getApplicationContext());
        }*/
        if(appInfraInterface == null) {
            appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;
        }
        else {
            appInfraInterface.getServiceDiscovery().setHomeCountry(mcountryCode[mCountry_spinner.getSelectedItemPosition()]);
        }
    }

    @Override
    public boolean onMainMenuItemClicked(String mainMenuItem) {
        if (mainMenuItem.equals(getStringKey(R.string.sign_into_my_philips))) {
            Intent intent = new Intent(CCDemoUAppActivity.this,
                    DummyScreen.class);
            startActivity(intent);
            return true;
        }
        /*if (mainMenuItem.equals(getStringKey(R.string.consumercare_view_product_details))) {
            Intent intent = new Intent(LaunchDigitalCare.this,
                    DummyScreen.class);
            startActivity(intent);
            return true;
        }*/
        return false;
    }


    private String getStringKey(int resId) {
        return getResources().getResourceEntryName(resId);
    }

    @Override
    public boolean onProductMenuItemClicked(String productMenu) {
        return false;
    }

    @Override
    public boolean onSocialProviderItemClicked(String socialProviderItem) {
        return false;
    }

    @Override
    public void onClick(View view) {
    /*
      Setting AppID is very much required from App side, in order to TAG the page. Here in below code
      we are putting dummy value. Please provide proper APP_ID from you App.
      Also if tagging is not enabled , consumer care is not tagging any events*/

        //   DigitalCareConfigManager.getInstance().setAppTaggingInputs(true, "App_ID_101", "AppName", "CurrentPageName");

        /*
         * Take values from GUI editText.
         */


        int i1 = view.getId();
        if (i1 == R.id.addimageButton) {
            launchDialog();

        } else if (i1 == R.id.launchDigitalCare) {
            mActivityButtonSelected = true;
            mFragmentButtonSelected = false;

            mLaunchAsFragment.setVisibility(View.INVISIBLE);


            final String[] ctnList = new String[mList.size()];
            for (int i = 0; i < mList.size(); i++)
                ctnList[i] = mList.get(i);
            //  if (ctnList.length != 0) {
            HardcodedProductList productsSelection = new HardcodedProductList(ctnList);
            productsSelection.setCatalog(Catalog.CARE);
            productsSelection.setSector(Sector.B2C);
               /*  ActivityLauncher uiLauncher = new ActivityLauncher(com.philips.cdp.productselection.launchertype.ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
                        R.style.Theme_Philips_BrightBlue_Gradient_WhiteBackground);
                uiLauncher.setAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
                DigitalCareConfigManager.getInstance().invokeDigitalCare(uiLauncher, productsSelection);*/

                final ActivityLauncher activityLauncher =
                        new ActivityLauncher(this,ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, getDlsThemeConfiguration(),
                                        themeHelper.getThemeResourceId(), null);

            activityLauncher.setCustomAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom);

//                mAppInfraInterface = new AppInfra.Builder().build(getApplicationContext());

            final CcInterface ccInterface = new CcInterface();
            if (ccSettings == null) ccSettings = new CcSettings(this);
            if (ccLaunchInput == null) ccLaunchInput = new CcLaunchInput();
            ccLaunchInput.setProductModelSelectionType(productsSelection);

            //CcDependencies ccDependencies = new CcDependencies(AppInfraSingleton.getInstance());
            CcDependencies ccDependencies = new CcDependencies(appInfraInterface);
            ccInterface.init(ccDependencies, ccSettings);
            //ccInterface.launch(activityLauncher, ccLaunchInput);
            if(appInfraInterface == null) {
                appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;
            }
            else {
                appInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
                    @Override
                    public void onSuccess(String s, SOURCE source) {
                        if (!(ctnList.length == 0))
                            ccInterface.launch(activityLauncher, ccLaunchInput);
                        else
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_ctn), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ERRORVALUES errorvalues, String s) {

                        if (!(ctnList.length == 0))
                            ccInterface.launch(activityLauncher, ccLaunchInput);
                        else
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_ctn), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (i1 == R.id.launchAsFragment) {
            mActivityButtonSelected = false;
            mFragmentButtonSelected = true;
            mLaunchDigitalCare.setVisibility(View.INVISIBLE);

            if(mList.size() != 0) {
                startActivity(new Intent(getApplicationContext(), CCDemoUAppFragmentActivity.class));
            }
            else{
                Toast.makeText(this, getResources().getString(R.string.no_ctn), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void relaunchActivity() {
        Intent intent;
        int RESULT_CODE_THEME_UPDATED = 1;
        setResult(RESULT_CODE_THEME_UPDATED);
        intent = new Intent(this, CCDemoUAppActivity.class);
        startActivity(intent);
        finish();
    }

    protected  void initTheme(){
        UIDHelper.injectCalligraphyFonts();
        ThemeConfiguration config = getDlsThemeConfiguration(); //themeHelper.getThemeConfig();
        config.add(AccentRange.ORANGE);
        themeHelper = new ThemeHelper(this);
        setTheme(themeHelper.getThemeResourceId());
        UIDHelper.init(config);
        //FontIconTypefaceHolder.init(getAssets(),"fonts/iconfont.ttf");
    }

    protected void changeTheme(){
        themeHelper.changeTheme();
    }

    protected ThemeConfiguration getDlsThemeConfiguration() {
        return new ThemeConfiguration(this, ColorRange.GROUP_BLUE, NavigationColor.BRIGHT, ContentColor.VERY_DARK, AccentRange.ORANGE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}

