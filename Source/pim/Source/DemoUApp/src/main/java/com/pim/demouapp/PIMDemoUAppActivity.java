
package com.pim.demouapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ecs.demotestuapp.integration.EcsDemoTestAppSettings;
import com.ecs.demotestuapp.integration.EcsDemoTestUAppDependencies;
import com.ecs.demotestuapp.integration.EcsDemoTestUAppInterface;
import com.ecs.demouapp.integration.EcsLaunchInput;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.cdp.prodreg.util.ProgressAlertDialog;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.mec.integration.MECBannerConfigurator;
import com.philips.platform.mec.integration.MECBazaarVoiceInput;
import com.philips.platform.mec.integration.MECDependencies;
import com.philips.platform.mec.integration.MECFlowConfigurator;
import com.philips.platform.mec.integration.MECInterface;
import com.philips.platform.mec.integration.MECLaunchInput;
import com.philips.platform.mec.integration.MECSettings;
import com.philips.platform.mec.screens.reviews.MECBazaarVoiceEnvironment;
import com.philips.platform.pif.DataInterface.MEC.MECException;
import com.philips.platform.pif.DataInterface.MEC.listeners.MECCartUpdateListener;
import com.philips.platform.pif.DataInterface.MEC.listeners.MECFetchCartListener;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefetchUserDetailsListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pim.UDIRedirectReceiverActivity;
import com.philips.platform.pim.errors.PIMErrorCodes;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.pim.listeners.UserMigrationListener;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.PIMParameterToLaunchEnum;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.Switch;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import utils.PIMNetworkUtility;

public class PIMDemoUAppActivity extends AppCompatActivity implements View.OnClickListener, UserRegistrationUIEventListener, UserLoginListener, IAPListener, MECFetchCartListener, MECCartUpdateListener, MECBannerConfigurator {
    private String TAG = PIMDemoUAppActivity.class.getSimpleName();
    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    //Theme
    public static final String KEY_ACTIVITY_THEME = "KEY_ACTIVITY_THEME";
    public static final String SELECTED_COUNTRY = "SELECTED_COUNTRY";


    private Button btnLaunchAsActivity, btnLaunchAsFragment, btnLogout, btn_ECS, btn_MCS, btnRefreshSession, btnISOIDCToken, btnMigrator, btnGetUserDetail,
            btn_RefetchUserDetails, btn_RegistrationPR, btn_IAP, btnUpdateMarketingOptin;
    private Switch aSwitch, abTestingSwitch, marketingOptedSwitch;
    private UserDataInterface userDataInterface;
    private PIMInterface pimInterface;
    private boolean isUSR;
    private Context mContext;
    private Spinner spinnerCountrySelection;
    private Label spinnerCountryText;
    @NonNull
    private AppInfraInterface appInfraInterface;
    private IAPInterface mIapInterface;
    private IAPSettings mIAPSettings;
    private IAPLaunchInput mIapLaunchInput;
    private ArrayList<String> mCategorizedProductList;
    private SharedPreferences sharedPreferences;
    private HomeCountryUpdateReceiver receiver;
    private ServiceDiscoveryInterface mServiceDiscoveryInterface = null;
    private Boolean isABTestingStatus = false;
    private MECLaunchInput mMecLaunchInput;
    private MECBazaarVoiceInput mecBazaarVoiceInput;
    private PIMDemoUAppApplication uAppApplication;
    private boolean isOptedIn;
    private ProgressAlertDialog progresDialog;
    private EcsDemoTestUAppInterface iapDemoUAppInterface;
    private MECInterface mMecInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo_uapp);

        mContext = this;
        Label appversion = findViewById(R.id.appversion);
        appversion.setText("Version : " + BuildConfig.VERSION_NAME);

        uAppApplication = (PIMDemoUAppApplication) getApplicationContext();
        appInfraInterface = uAppApplication.getAppInfra();

        btnGetUserDetail = findViewById(R.id.btn_GetUserDetail);
        btnGetUserDetail.setOnClickListener(this);
        btnLaunchAsActivity = findViewById(R.id.btn_login_activity);
        btnLaunchAsActivity.setOnClickListener(this);
        btnLaunchAsFragment = findViewById(R.id.btn_login_fragment);
        btnLaunchAsFragment.setOnClickListener(this);
        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
        btnRefreshSession = findViewById(R.id.btn_RefreshSession);
        btnRefreshSession.setOnClickListener(this);
        btn_RefetchUserDetails = findViewById(R.id.btn_RefetchUserDetails);
        btn_RefetchUserDetails.setOnClickListener(this);
        btnISOIDCToken = findViewById(R.id.btn_IsOIDCToken);
        btnISOIDCToken.setOnClickListener(this);
        btnMigrator = findViewById(R.id.btn_MigrateUser);
        btnUpdateMarketingOptin = findViewById(R.id.btn_UpdateMarketingOptin);
        btnMigrator.setOnClickListener(this);
        aSwitch = findViewById(R.id.switch_cookies_consent);
        abTestingSwitch = findViewById(R.id.switch_ab_testing_consent);
        marketingOptedSwitch = findViewById(R.id.switch_marketing_optedin);
        btn_RegistrationPR = findViewById(R.id.btn_RegistrationPR);
        btn_RegistrationPR.setOnClickListener(this);
        btn_IAP = findViewById(R.id.btn_IAP);
        btn_IAP.setOnClickListener(this);
        btn_ECS = findViewById(R.id.btn_ECS);
        btn_ECS.setOnClickListener(this);
        btn_MCS = findViewById(R.id.btn_MEC);
        btn_MCS.setOnClickListener(this);
        btnUpdateMarketingOptin.setOnClickListener(this);
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface);
        PIMDemoUAppSettings pimDemoUAppSettings = new PIMDemoUAppSettings(this);

        aSwitch.setChecked(appInfraInterface.getTagging().getPrivacyConsent() == AppTaggingInterface.PrivacyStatus.OPTIN);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                appInfraInterface.getTagging().setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTIN);
            } else {
                appInfraInterface.getTagging().setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTOUT);
            }
        });

        abTestingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setABTestingStatus(isChecked);
        });

        viewInitlization(pimDemoUAppDependencies, pimDemoUAppSettings);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        spinnerCountrySelection = findViewById(R.id.spinner_CountrySelection);
        spinnerCountryText = findViewById(R.id.spinner_Text);

        if (userDataInterface != null && userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_NOT_LOGGED_IN) {
            spinnerCountrySelection.setVisibility(View.VISIBLE);
            spinnerCountryText.setVisibility(View.GONE);
            String[] stringArray = getResources().getStringArray(R.array.countries_array);

            List<String> countryList = new ArrayList<>(Arrays.asList(stringArray));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
            spinnerCountrySelection.setAdapter(arrayAdapter);
            if (!TextUtils.isEmpty(getSavedCountry())) {
                int index = countryList.indexOf(getSavedCountry());
                spinnerCountrySelection.setSelection(index);
            }
            spinnerCountrySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String countrycode = getCountryCode(countryList.get(position));
                    appInfraInterface.getServiceDiscovery().setHomeCountry(countrycode);
                    if (!countryList.get(position).equals(getSavedCountry())) {
                        editor.putString(SELECTED_COUNTRY, countryList.get(position));
                        editor.apply();
                        uAppApplication.initialisePim();
                        userDataInterface = uAppApplication.getUserDataInterface();
                        pimInterface = uAppApplication.getPIMInterface();
                        if (pimInterface != null)
                            pimInterface.setLoginListener(PIMDemoUAppActivity.this);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            String selectedCountry = getSavedCountry();
            spinnerCountryText.setVisibility(View.VISIBLE);
            spinnerCountryText.setText(selectedCountry);
            spinnerCountrySelection.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra(UDIRedirectReceiverActivity.REDIRECT_TO_CLOSED_APP)) {
            showProgressDialog();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (userDataInterface == null)
            userDataInterface = PIMDemoUAppApplication.getInstance().getUserDataInterface();

        if (userDataInterface != null && !userDataInterface.isOIDCToken() && userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
            btnLaunchAsFragment.setEnabled(false);
        }
    }

    private void viewInitlization(PIMDemoUAppDependencies pimDemoUAppDependencies, PIMDemoUAppSettings pimDemoUAppSettings) {
        if (getIntent().getExtras() != null && getIntent().getExtras().get("SelectedLib").equals("USR")) {
            isUSR = true;
            Log.i(TAG, "Selected Liberary : USR");
            btnLaunchAsActivity.setVisibility(View.GONE);
            btn_RegistrationPR.setVisibility(View.GONE);
            btnMigrator.setVisibility(View.GONE);
            btnISOIDCToken.setVisibility(View.GONE);
            btn_IAP.setVisibility(View.GONE);
            btn_MCS.setVisibility(View.GONE);
            btn_ECS.setVisibility(View.GONE);
            btnGetUserDetail.setVisibility(View.GONE);
            btnLaunchAsFragment.setText("Launch USR");
            aSwitch.setVisibility(View.GONE);
            abTestingSwitch.setVisibility(View.GONE);
            userDataInterface = uAppApplication.getUserDataInterface();
        } else {
            isUSR = false;
            Log.i(TAG, "Selected Liberary : PIM");
            userDataInterface = uAppApplication.getUserDataInterface();
            pimInterface = uAppApplication.getPIMInterface();
            if (pimInterface != null)
                pimInterface.setLoginListener(this);
            if (isUserLoggedIn()) {
                btnLaunchAsActivity.setVisibility(View.GONE);
                btnLaunchAsFragment.setText("Launch User Profile");
                updateMarketingOptinStatus();
            } else {
                btnLaunchAsActivity.setText("Launch UDI As Activity");
                btnLaunchAsFragment.setText("Launch UDI As Fragment");
            }
            initIAP();
            initializeBazaarVoice();
            initMEC();
        }
    }

    private void initIAP() {
        IAPDependencies mIapDependencies = new IAPDependencies(appInfraInterface, userDataInterface);
        mIAPSettings = new IAPSettings(this);
        mIapInterface = new IAPInterface();
        mIapInterface.init(mIapDependencies, mIAPSettings);
        mServiceDiscoveryInterface = appInfraInterface.getServiceDiscovery();
        receiver = new HomeCountryUpdateReceiver(appInfraInterface);
        mServiceDiscoveryInterface.registerOnHomeCountrySet(receiver);
        mCategorizedProductList = new ArrayList<>();
        mCategorizedProductList.add("HD9745/90000");
        mCategorizedProductList.add("HD9630/90");
        mCategorizedProductList.add("HD9240/90");
        mCategorizedProductList.add("HD9621/90");
        mIapLaunchInput = new IAPLaunchInput();
        mIapLaunchInput.setHybrisSupported(true);
        mIapLaunchInput.setIapListener(this);
    }

    private void initMEC() {
        mMecInterface = new MECInterface();
        MECDependencies mMecDependencies = new MECDependencies(appInfraInterface, userDataInterface);
        mMecInterface.init(mMecDependencies, new MECSettings(mContext));
        mMecLaunchInput = new MECLaunchInput();
        mMecLaunchInput.setMecCartUpdateListener(this);

        mMecLaunchInput.setMecBannerConfigurator(this);
        mMecLaunchInput.setSupportsHybris(true);
        mMecLaunchInput.setSupportsRetailer(true);
        mMecLaunchInput.setMecBazaarVoiceInput(mecBazaarVoiceInput);
    }

    public String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        Map<String, String> countryMap = new HashMap<>();
        Locale locale;
        String name;

        for (String code : isoCountryCodes) {
            locale = new Locale("", code);
            name = locale.getDisplayCountry();
            countryMap.put(name, code);
        }
        return countryMap.get(countryName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }

    private void initTheme() {
        UIDHelper.injectCalligraphyFonts();
        int themeIndex = getIntent().getIntExtra(KEY_ACTIVITY_THEME, DEFAULT_THEME);
        if (themeIndex <= 0) {
            themeIndex = DEFAULT_THEME;
        }
        getTheme().applyStyle(themeIndex, true);
        UIDHelper.init(new ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE));
    }

    @Override
    public void onClick(View v) {
        if (!isNetworkConnected()) return;

        if (v == btnLaunchAsActivity) {
            if (!isUSR) {
                PIMLaunchInput launchInput = new PIMLaunchInput();

                HashMap<PIMParameterToLaunchEnum, Object> map = new HashMap<>();
                map.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, isABTestingStatus);

                launchInput.setParameterToLaunch(map);
                ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
                if (pimInterface != null)
                    pimInterface.launch(activityLauncher, launchInput);
            }
        } else if (v == btnLaunchAsFragment) {
            if (isUSR) {
                launchUSR();
            } else {
                launchPIM();
            }
        } else if (v == btnLogout) {
            if (isUserLoggedIn()) {
                showProgressDialog();
                userDataInterface.logoutSession(new LogoutSessionListener() {
                    @Override
                    public void logoutSessionSuccess() {
                        cancelProgressDialog();
                        showToast("Logout Success");
                        finish();
                    }

                    @Override
                    public void logoutSessionFailed(Error error) {
                        cancelProgressDialog();
                        showToast("Logout Failed with error code " + error.getErrCode());
                    }
                });
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btnRefreshSession) {
            if (isUserLoggedIn()) {
                showProgressDialog();
                userDataInterface.refreshSession(new RefreshSessionListener() {
                    @Override
                    public void refreshSessionSuccess() {
                        updateMarketingOptinStatus();
                        cancelProgressDialog();
                        showToast("Refresh session success");
                    }

                    @Override
                    public void refreshSessionFailed(Error error) {
                        cancelProgressDialog();
                        showToast("Refresh session failed due to :" + error.getErrCode() + " and error message :" + error.getErrDesc());
                    }

                    @Override
                    public void forcedLogout() {
                        cancelProgressDialog();
                    }
                });
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_RegistrationPR) {
            if (isUserLoggedIn()) {
                Fragment fragment = new PRGFragment(userDataInterface, appInfraInterface);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.demoAppMenus, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_IAP) {
            if (isUserLoggedIn()) {
                if (mCategorizedProductList.size() > 0) {
                    IAPFlowInput input = new IAPFlowInput(mCategorizedProductList);
                    launchIAP();
                } else {
                    Toast.makeText(this, "Please add CTN", Toast.LENGTH_SHORT).show();
                }
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_ECS) {
            if (isUserLoggedIn()) {
                launchECS();
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_MCS) {
            if (isUserLoggedIn()) {
                MECFlowConfigurator pMecFlowConfigurator = new MECFlowConfigurator();
                pMecFlowConfigurator.setLandingView(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW);
                launchMECasFragment(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW, pMecFlowConfigurator, null);
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btnMigrator) {
            if (isUserLoggedIn()) {
                updateUIOnUserLoggedIn();
                showToast("User is already migrated or logged-in into UDI!");
            } else {
                if (pimInterface != null) {
                    showProgressDialog();
                    pimInterface.migrateJanrainUserToPIM(new UserMigrationListener() {
                        @Override
                        public void onUserMigrationSuccess() {
                            updateUIOnUserLoggedIn();
                            cancelProgressDialog();
                            showToast("User migrated succesfully");
                        }

                        @Override
                        public void onUserMigrationFailed(Error error) {
                            cancelProgressDialog();
                            showToast("user migration failed error code = " + error.getErrCode() + " error message : " + error.getErrDesc());
                        }
                    });
                }
            }
        } else if (v == btn_RefetchUserDetails) {
            if (isUserLoggedIn()) {
                showProgressDialog();
                userDataInterface.refetchUserDetails(new RefetchUserDetailsListener() {
                    @Override
                    public void onRefetchSuccess() {
                        updateMarketingOptinStatus();
                        cancelProgressDialog();
                        showToast("Refetch Success!!");
                    }

                    @Override
                    public void onRefetchFailure(Error error) {
                        cancelProgressDialog();
                        if (error != null && error.getErrCode() == PIMErrorCodes.ACCESS_TOKEN_EXPIRED)
                            showToast("Refetch failed due to due to expired access token.");
                        else
                            showToast("Refetch failed with error code : " + error.getErrCode());
                    }
                });
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btnGetUserDetail) {
            if (isUserLoggedIn()) {
                try {
                    ArrayList<String> detailKeys = new ArrayList<>();
                    detailKeys.add(UserDetailConstants.FAMILY_NAME);
                    detailKeys.add(UserDetailConstants.GIVEN_NAME);
                    detailKeys.add(UserDetailConstants.ACCESS_TOKEN);
                    detailKeys.add(UserDetailConstants.BIRTHDAY);
                    detailKeys.add(UserDetailConstants.EMAIL);
                    detailKeys.add(UserDetailConstants.GENDER);
                    detailKeys.add(UserDetailConstants.MOBILE_NUMBER);
                    detailKeys.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
                    detailKeys.add(UserDetailConstants.UUID);
                    detailKeys.add(UserDetailConstants.ID_TOKEN);
                    detailKeys.add(UserDetailConstants.EXPIRES_IN);
                    detailKeys.add(UserDetailConstants.TOKEN_TYPE);
                    HashMap<String, Object> userDetails = userDataInterface.getUserDetails(detailKeys);
                    Log.i(TAG, "User userDetails : " + userDetails);
                    showInfoDialog(userDetails.toString());
                } catch (UserDataInterfaceException e) {
                    e.printStackTrace();
                    showToast("Error code:" + e.getError().getErrCode() + " Error message :" + e.getError().getErrDesc());
                }
            } else {
                showToast("User is not loged-in, Please login!");
            }

        } else if (v == btnISOIDCToken) {
            if (isUserLoggedIn()) {
                boolean oidcToken = userDataInterface.isOIDCToken();
                if (oidcToken)
                    showToast("User is logged in via UDI");
                else
                    showToast("User is logged in via USR");
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btnUpdateMarketingOptin) {
            if ((!isNetworkConnected())) {
                return;
            }

            if (!isUserLoggedIn()) {
                marketingOptedSwitch.setChecked(false);
                showToast("User is not loged-in, Please login!");
                return;
            }

            showProgressDialog();
            userDataInterface.updateReceiveMarketingEmail(new UpdateUserDetailsHandler() {
                @Override
                public void onUpdateSuccess() {
                    cancelProgressDialog();
                    showToast("Marketing Opted-In updated successfully.");
                    updateMarketingOptinStatus();
                }

                @Override
                public void onUpdateFailedWithError(Error error) {
                    cancelProgressDialog();
                    if (error != null && error.getErrCode() == PIMErrorCodes.ACCESS_TOKEN_EXPIRED)
                        showToast("Updating marketing opted-in failed due to expired access token.");
                    else
                        showToast("Updating marketing opted-in failed with error code : " + error.getErrCode());
                    updateMarketingOptinStatus();
                }
            }, marketingOptedSwitch.isChecked());
        }

    }

    private void launchECS() {
        iapDemoUAppInterface = new EcsDemoTestUAppInterface();
        iapDemoUAppInterface.init(new EcsDemoTestUAppDependencies(appInfraInterface), new EcsDemoTestAppSettings(this));
        iapDemoUAppInterface.launch(new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, null), new EcsLaunchInput());
    }

    private void launchMECasFragment(MECFlowConfigurator.MECLandingView mecLandingView, MECFlowConfigurator pMecFlowConfigurator, ArrayList<String> pIgnoreRetailerList) {
        pMecFlowConfigurator.setLandingView(mecLandingView);
        mMecLaunchInput.setFlowConfigurator(pMecFlowConfigurator);
        try {
            mMecInterface.launch(new ActivityLauncher
                            (mContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, null, 0, null),
                    mMecLaunchInput);
        } catch (MECException e) {
            e.printStackTrace();
        }
    }

    private void launchIAP() {
        try {
            IAPDependencies mIapDependencies = new IAPDependencies(appInfraInterface, userDataInterface);
            mIAPSettings = new IAPSettings(this);
            mIapInterface = new IAPInterface();
            mIapInterface.init(mIapDependencies, mIAPSettings);
            mServiceDiscoveryInterface = appInfraInterface.getServiceDiscovery();
            receiver = new HomeCountryUpdateReceiver(appInfraInterface);
            mServiceDiscoveryInterface.registerOnHomeCountrySet(receiver);
            mCategorizedProductList = new ArrayList<>();
            mCategorizedProductList.add("HD9745/90000");
            mCategorizedProductList.add("HD9630/90");
            mCategorizedProductList.add("HD9240/90");
            mCategorizedProductList.add("HD9621/90");
            mIapLaunchInput = new IAPLaunchInput();
            mIapLaunchInput.setHybrisSupported(true);
            mIapLaunchInput.setIapListener(this);

            mIapInterface.launch(new ActivityLauncher
                            (this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, null, 0, null),
                    mIapLaunchInput);

        } catch (RuntimeException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void launchPIM() {
        PIMLaunchInput launchInput = new PIMLaunchInput();
        FragmentLauncher fragmentLauncher = new FragmentLauncher(this, R.id.pimDemoU_mainFragmentContainer, null);
        HashMap<PIMParameterToLaunchEnum, Object> parameter = new HashMap<>();
        parameter.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, isABTestingStatus());
        launchInput.setParameterToLaunch(parameter);
        if (pimInterface != null)
            pimInterface.launch(fragmentLauncher, launchInput);
    }

    private void launchUSR() {
        FragmentLauncher fragmentLauncher = new FragmentLauncher(this, R.id.demoAppMenus, null);
        URLaunchInput urLaunchInput;
        RLog.d(TAG, " : Registration");
        urLaunchInput = new URLaunchInput();
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.SignIn);
        urLaunchInput.setEndPointScreen(RegistrationLaunchMode.USER_DETAILS);
        urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
        if (userDataInterface.getUserLoggedInState() != UserLoggedInState.USER_LOGGED_IN)
            new URInterface().launch(fragmentLauncher, urLaunchInput);
    }

    private void updateUIOnUserLoggedIn() {
        btnLaunchAsActivity.setVisibility(View.GONE);
        String selectedCountry = sharedPreferences.getString(SELECTED_COUNTRY, "");
        spinnerCountryText.setVisibility(View.VISIBLE);
        spinnerCountryText.setText(selectedCountry);
        spinnerCountrySelection.setVisibility(View.GONE);
        btnLaunchAsFragment.setText("Launch User Profile");
        updateMarketingOptinStatus();
    }

    private Boolean isABTestingStatus() {
        return isABTestingStatus;
    }

    private void setABTestingStatus(Boolean ABTestingStatus) {
        isABTestingStatus = ABTestingStatus;
    }

    private void updateMarketingOptinStatus() {
        ArrayList<String> keyList = new ArrayList<>();
        keyList.add(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
        try {
            final HashMap<String, Object> userDetails = userDataInterface.getUserDetails(keyList);
            isOptedIn = (boolean) userDetails.get(UserDetailConstants.RECEIVE_MARKETING_EMAIL);
            marketingOptedSwitch.setChecked(isOptedIn);
        } catch (UserDataInterfaceException e) {
            e.printStackTrace();
        }
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mContext != null && !NetworkUtility.getInstance().isNetworkAvailable(connectivityManager)) {
            PIMNetworkUtility.getInstance().showErrorDialog(mContext,
                    getSupportFragmentManager(), "OK",
                    "You are offline", "Your internet connection does not seem to be working. Please check and try again");
            return false;
        } else {
            return true;
        }
    }

    private String getSavedCountry() {
        return sharedPreferences.getString(SELECTED_COUNTRY, "");
    }

    private void showProgressDialog() {
        progresDialog = new ProgressAlertDialog(this, R.style.prg_Custom_loaderTheme);
        progresDialog.show();
    }

    private void cancelProgressDialog() {
        if (progresDialog != null && progresDialog.isShowing())
            progresDialog.dismiss();
    }

    private boolean isUserLoggedIn() {
        return userDataInterface != null && (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN);
    }

    private void showToast(final String toastMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showInfoDialog(String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.show();
        android.widget.Button button = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        ViewGroup.LayoutParams params = button.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        button.setBackgroundColor(getResources().getColor(R.color.uid_blue_level_50, null));
        button.setTextColor(getResources().getColor(R.color.uidColorWhite, null));
        button.setTextSize(18);
        button.setLayoutParams(params);
    }

    public RegistrationContentConfiguration getRegistrationContentConfiguration() {
        String valueForEmailVerification = "sample";
        String optInTitleText = getResources().getString(R.string.USR_DLS_OptIn_Navigation_Bar_Title);
        String optInQuessionaryText = getResources().getString(R.string.USR_DLS_OptIn_Header_Label);
        String optInDetailDescription = getResources().getString(R.string.USR_DLS_Optin_Body_Line1);
        //String optInBannerText = getResources().getString(R.string.reg_Opt_In_Join_Now);
        String optInTitleBarText = getResources().getString(R.string.USR_DLS_OptIn_Navigation_Bar_Title);
        RegistrationContentConfiguration registrationContentConfiguration = new RegistrationContentConfiguration();
        registrationContentConfiguration.setValueForEmailVerification(valueForEmailVerification);
        registrationContentConfiguration.setOptInTitleText(optInTitleText);
        registrationContentConfiguration.setOptInQuessionaryText(optInQuessionaryText);
        registrationContentConfiguration.setOptInDetailDescription(optInDetailDescription);
//        registrationContentConfiguration.setOptInBannerText(optInBannerText);
        registrationContentConfiguration.setOptInActionBarText(optInTitleBarText);
        //   registrationContentConfiguration.enableMarketImage(R.drawable.ref_app_home_page);
        registrationContentConfiguration.enableLastName(true);
        registrationContentConfiguration.enableContinueWithouAccount(true);
        return registrationContentConfiguration;

    }

    @Override
    public void onLoginSuccess() {
        showToast("UDI Login Success");
        updateUIOnUserLoggedIn();
        cancelProgressDialog();
    }

    @Override
    public void onLoginFailed(Error error) {
        showToast("UDI Login Failed :" + error.getErrCode() + " and reason is" + error.getErrDesc());
        cancelProgressDialog();
    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {
        RLog.d(TAG, " : onUserRegistrationComplete");
        activity.finish();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
        RLog.d(TAG, " : onPrivacyPolicyClick");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.philips.com"));
        activity.startActivity(browserIntent);
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
        RLog.d(TAG, " : onTermsAndConditionClick");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.philips.com"));
        activity.startActivity(browserIntent);
    }

    @Override
    public void onPersonalConsentClick(Activity activity) {

    }

    private void initializeBazaarVoice() {
        SharedPreferences shared = this.getSharedPreferences("bvEnv", MODE_PRIVATE);
        String name = (shared.getString("BVEnvironment", MECBazaarVoiceEnvironment.PRODUCTION.toString()));
        if (name.equalsIgnoreCase(MECBazaarVoiceEnvironment.PRODUCTION.toString())) {
//            bvCheckBox.setChecked(true);
            mecBazaarVoiceInput = new MECBazaarVoiceInput() {

                @NotNull
                @Override
                public MECBazaarVoiceEnvironment getBazaarVoiceEnvironment() {

                    return MECBazaarVoiceEnvironment.PRODUCTION;

                }

                @NotNull
                @Override
                public String getBazaarVoiceClientID() {

                    return "philipsglobal";

                }

                @NotNull
                @Override
                public String getBazaarVoiceConversationAPIKey() {

                    return "caAyWvBUz6K3xq4SXedraFDzuFoVK71xMplaDk1oO5P4E";

                }
            };
        } else {
            mecBazaarVoiceInput = new MECBazaarVoiceInput() {

                @NotNull
                @Override
                public MECBazaarVoiceEnvironment getBazaarVoiceEnvironment() {

                    return MECBazaarVoiceEnvironment.STAGING;

                }

                @NotNull
                @Override
                public String getBazaarVoiceClientID() {

                    return "philipsglobal";

                }

                @NotNull
                @Override
                public String getBazaarVoiceConversationAPIKey() {

                    return "ca23LB5V0eOKLe0cX6kPTz6LpAEJ7SGnZHe21XiWJcshc";
                }
            };
        }

    }

    @NotNull
    @Override
    public View getBannerViewProductList() {
        if (true) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(com.mec.demouapp.R.layout.banner_view, null);
            return v;
        }
        return null;
    }

    @Override
    public void onFailure(@NotNull Exception exception) {

    }

    @Override
    public void onUpdateCartCount(int count) {

    }

    @Override
    public void onGetCartCount(int count) {

    }

    @Override
    public void onUpdateCartCount() {

    }

    @Override
    public void updateCartIconVisibility(boolean shouldShow) {

    }

    @Override
    public void shouldShowCart(Boolean shouldShow) {

    }

    @Override
    public void onGetCompleteProductList(ArrayList<String> productList) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onSuccess(boolean bool) {

    }

    @Override
    public void onFailure(int errorCode) {

    }
}
