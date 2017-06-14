package com.philips.platform.ccdemouapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
//import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.philips.cdp.digitalcare.CcDependencies;
import com.philips.cdp.digitalcare.CcInterface;
import com.philips.cdp.digitalcare.CcLaunchInput;
import com.philips.cdp.digitalcare.CcSettings;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.listeners.CcListener;
import com.philips.cdp.prxclient.PrxConstants.Catalog;
import com.philips.cdp.prxclient.PrxConstants.Sector;
import com.philips.cdp.productselection.productselectiontype.HardcodedProductList;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.ccdemouapp.adapter.Listener;
import com.philips.platform.ccdemouapp.adapter.SampleAdapter;
import com.philips.platform.ccdemouapp.adapter.SimpleItemTouchHelperCallback;
import com.philips.platform.ccdemouapp.util.ThemeUtil;
import com.philips.platform.ccdemouapp.view.CustomDialog;
import com.philips.platform.ccdemouapplibrary.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CCDemoUAppActivity extends FragmentActivity implements View.OnClickListener,
        CcListener {

    private static final String TAG = CCDemoUAppActivity.class.getSimpleName();
    public static ArrayList<String> mList = null;
    private static boolean mActivityButtonSelected = true;
    private static boolean mFragmentButtonSelected = true;
    private Button mLaunchDigitalCare = null;
    private Button mLaunchAsFragment = null;
    private Button mChangeTheme = null;
    private ImageButton mAddButton = null;
    private RecyclerView mRecyclerView = null;
    private SampleAdapter adapter = null;


    private Spinner mCountry_spinner;
    private String mCountry[], mcountryCode[];
    private CcSettings ccSettings;
    private CcLaunchInput ccLaunchInput;
    private ThemeUtil mThemeUtil;

    private AppInfraInterface appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_care);


        mLaunchDigitalCare = (Button) findViewById(R.id.launchDigitalCare);
        mLaunchAsFragment = (Button) findViewById(R.id.launchAsFragment);
        mChangeTheme = (Button) findViewById(R.id.change_theme);
        mAddButton = (ImageButton) findViewById(R.id.addimageButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAddButton.setOnClickListener(this);

        // set listener
        mLaunchDigitalCare.setOnClickListener(this);
        mLaunchAsFragment.setOnClickListener(this);
        mChangeTheme.setOnClickListener(this);
        mThemeUtil = new ThemeUtil(getApplicationContext().getSharedPreferences(
                this.getString(R.string.app_name), Context.MODE_PRIVATE));

        // setting country spinner
        mCountry_spinner = (Spinner) findViewById(R.id.spinner2);
        mCountry = getResources().getStringArray(R.array.country);
        mcountryCode = getResources().getStringArray(R.array.country_code);
        ArrayAdapter<String> mCountry_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mCountry);
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
        appInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                tv.setText("Country from Service Discovery : " +s);
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {

            }
        });


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
       // DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
         //       layoutManager.getOrientation());
        //mRecyclerView.addItemDecoration(mDividerItemDecoration);
        return adapter;
    }

    private void launchDialog() {
        CustomDialog dialog = new CustomDialog(this, mList, new Listener() {
            @Override
            public void updateList(ArrayList<String> productList) {
                mList = productList;
                setAdapter(mList);
                Log.d(TAG, " Products Size = " + mList.size());
            }
        });
        dialog.show();
    }

    private void restoreCountryOption() {
        if(appInfraInterface == null) {
            appInfraInterface = CCDemoUAppuAppInterface.mAppInfraInterface;
        }
        appInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
            @Override
            public void onSuccess(String s, SOURCE source) {
                for (int i=0; i < mcountryCode.length; i++) {
                    if(s.equalsIgnoreCase(mcountryCode[i])) {
                        mCountry_spinner.setSelection(i);
                    }
                }
            }

            @Override
            public void onError(ERRORVALUES errorvalues, String s) {
            }
        });
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

        appInfraInterface.getServiceDiscovery().setHomeCountry(mcountryCode[mCountry_spinner.getSelectedItemPosition()]);
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


            String[] ctnList = new String[mList.size()];
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

            final com.philips.platform.uappframework.launcher.ActivityLauncher activityLauncher =
                    new com.philips.platform.uappframework.launcher.ActivityLauncher
                            (com.philips.platform.uappframework.
                                    launcher.ActivityLauncher.
                                    ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED,
                                    mThemeUtil.getCurrentTheme());

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
            appInfraInterface.getServiceDiscovery().getHomeCountry(new ServiceDiscoveryInterface.OnGetHomeCountryListener() {
                @Override
                public void onSuccess(String s, SOURCE source) {
                    if (s.equals("CN")) {
                        ccLaunchInput.setLiveChatUrl("http://ph-china.livecom.cn/webapp/index.html?app_openid=ph_6idvd4fj&token=PhilipsTest");
                    } else {
                        ccLaunchInput.setLiveChatUrl(null);
                    }
                    ccInterface.launch(activityLauncher, ccLaunchInput);
                }

                @Override
                public void onError(ERRORVALUES errorvalues, String s) {
                    ccInterface.launch(activityLauncher, ccLaunchInput);
                }
            });

        } else if (i1 == R.id.launchAsFragment) {
            mActivityButtonSelected = false;
            mFragmentButtonSelected = true;

            mLaunchDigitalCare.setVisibility(View.INVISIBLE);


            startActivity(new Intent(getApplicationContext(), CCDemoUAppFragmentActivity.class));

        } else if (i1 == R.id.change_theme) {
            Resources.Theme theme = super.getTheme();
            theme.applyStyle(mThemeUtil.getNextTheme(), true);
            relaunchActivity();

        }
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if(mThemeUtil ==null){
            mThemeUtil = new ThemeUtil(getApplicationContext().getSharedPreferences(
                    this.getString(R.string.app_name), Context.MODE_PRIVATE));
        }
        theme.applyStyle(mThemeUtil.getCurrentTheme(), true);
        return theme;
    }

    private void relaunchActivity() {
        Intent intent;
        int RESULT_CODE_THEME_UPDATED = 1;
        setResult(RESULT_CODE_THEME_UPDATED);
        intent = new Intent(this, CCDemoUAppActivity.class);
        startActivity(intent);
        finish();
    }

   /* private void setDigitalCareLocale(String language, String country) {

        DigitalCareConfigManager.getInstance().setLocale(language, country);


    }*/


}

