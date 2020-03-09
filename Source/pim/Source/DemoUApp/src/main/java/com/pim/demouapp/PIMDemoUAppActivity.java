
package com.pim.demouapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ecs.demotestuapp.integration.EcsDemoTestAppSettings;
import com.ecs.demotestuapp.integration.EcsDemoTestUAppDependencies;
import com.ecs.demotestuapp.integration.EcsDemoTestUAppInterface;
import com.ecs.demouapp.integration.EcsLaunchInput;
import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.cdp.di.mec.integration.MECBannerConfigurator;
import com.philips.cdp.di.mec.integration.MECBazaarVoiceInput;
import com.philips.cdp.di.mec.integration.MECDependencies;
import com.philips.cdp.di.mec.integration.MECFlowConfigurator;
import com.philips.cdp.di.mec.integration.MECInterface;
import com.philips.cdp.di.mec.integration.MECLaunchInput;
import com.philips.cdp.di.mec.integration.MECListener;
import com.philips.cdp.di.mec.integration.MECSettings;
import com.philips.cdp.di.mec.screens.reviews.MECBazaarVoiceEnvironment;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.tagging.AppTaggingInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.RefreshSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UserLoginListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UserMigrationListener;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.PIMParameterToLaunchEnum;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappSettings;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
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

public class PIMDemoUAppActivity extends AppCompatActivity implements View.OnClickListener, UserRegistrationUIEventListener, UserLoginListener, IAPListener, MECListener, MECBannerConfigurator {
    private String TAG = PIMDemoUAppActivity.class.getSimpleName();
    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    //Theme
    public static final String KEY_ACTIVITY_THEME = "KEY_ACTIVITY_THEME";
    public static final String SELECTED_COUNTRY = "SELECTED_COUNTRY";


    private Button btnLaunchAsActivity, btnLaunchAsFragment, btnLogout, btn_ECS, btn_MCS, btnRefreshSession, btnISOIDCToken, btnMigrator, btnGetUserDetail, btn_RegistrationPR, btn_IAP;
    private Switch aSwitch, abTestingSwitch;
    private UserDataInterface userDataInterface;
    private PIMInterface pimInterface;
    private URInterface urInterface;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo_uapp);

        mContext = this;
        Label appversion = findViewById(R.id.appversion);
        appversion.setText("Version : " + BuildConfig.VERSION_NAME);

        appInfraInterface = new AppInfra.Builder().build(this);//PIMDemoUAppInterface.mAppInfra;

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
        btnISOIDCToken = findViewById(R.id.btn_IsOIDCToken);
        btnISOIDCToken.setOnClickListener(this);
        btnMigrator = findViewById(R.id.btn_MigrateUser);
        btnMigrator.setOnClickListener(this);
        aSwitch = findViewById(R.id.switch_cookies_consent);
        abTestingSwitch = findViewById(R.id.switch_ab_testing_consent);
        btn_RegistrationPR = findViewById(R.id.btn_RegistrationPR);
        btn_RegistrationPR.setOnClickListener(this);
        btn_IAP = findViewById(R.id.btn_IAP);
        btn_IAP.setOnClickListener(this);
        btn_ECS = findViewById(R.id.btn_ECS);
        btn_ECS.setOnClickListener(this);
        btn_MCS = findViewById(R.id.btn_MEC);
        btn_MCS.setOnClickListener(this);
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface);
        PIMDemoUAppSettings pimDemoUAppSettings = new PIMDemoUAppSettings(this);

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
//        pimInterface = new PIMInterface();
//        pimInterface.init(pimDemoUAppDependencies, pimDemoUAppSettings);
//        userDataInterface = pimInterface.getUserDataInterface();

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        spinnerCountrySelection = findViewById(R.id.spinner_CountrySelection);
        spinnerCountryText = findViewById(R.id.spinner_Text);
        if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_NOT_LOGGED_IN) {
            spinnerCountrySelection.setVisibility(View.VISIBLE);
            spinnerCountryText.setVisibility(View.GONE);
            String[] stringArray = getResources().getStringArray(R.array.countries_array);

            List<String> countryList = new ArrayList<>(Arrays.asList(stringArray));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
            spinnerCountrySelection.setAdapter(arrayAdapter);
            spinnerCountrySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String countrycode = getCountryCode(countryList.get(position));
                    appInfraInterface.getServiceDiscovery().setHomeCountry(countrycode);
                    editor.putString(SELECTED_COUNTRY, countryList.get(position));
                    editor.apply();
                    pimInterface = new PIMInterface();
                    pimInterface.init(pimDemoUAppDependencies, pimDemoUAppSettings);
                    userDataInterface = pimInterface.getUserDataInterface();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            String selectedCountry = sharedPreferences.getString(SELECTED_COUNTRY, "");
            spinnerCountryText.setVisibility(View.VISIBLE);
            spinnerCountryText.setText(selectedCountry);
            spinnerCountrySelection.setVisibility(View.GONE);
        }
    }


    private void initMEC() {
//        ignorelistedRetailer.add("Frys.com");
//        ignorelistedRetailer.add("Amazon - US");
//        ignorelistedRetailer.add("BestBuy.com");

        UappDependencies uappDependencies = new UappDependencies(new AppInfra.Builder().build(mContext));
        UappSettings uappSettings = new UappSettings(mContext);
        mMecInterface = new MECInterface();
        pimInterface.init(uappDependencies, uappSettings);

        MECDependencies mIapDependencies = new MECDependencies(new AppInfra.Builder().build(mContext), pimInterface.getUserDataInterface());

        mMecInterface.init(mIapDependencies, new MECSettings(mContext));

        mMecLaunchInput = new MECLaunchInput();
        mMecLaunchInput.setMecListener(this);


        mMecLaunchInput.setMecBannerConfigurator(this);
        mMecLaunchInput.setSupportsHybris(true);
        mMecLaunchInput.setSupportsRetailer(false);
        mMecLaunchInput.setMecBazaarVoiceInput(mecBazaarVoiceInput);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userDataInterface.isOIDCToken() && userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
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
            urInterface = new URInterface();
            urInterface.init(pimDemoUAppDependencies, pimDemoUAppSettings);
            userDataInterface = urInterface.getUserDataInterface();
        } else {
            isUSR = false;
            Log.i(TAG, "Selected Liberary : PIM");
            pimInterface = new PIMInterface();
            pimInterface.init(pimDemoUAppDependencies, pimDemoUAppSettings);
            userDataInterface = pimInterface.getUserDataInterface();
            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                btnLaunchAsActivity.setVisibility(View.GONE);
                btnLaunchAsFragment.setText("Launch User Profile");
            } else {
                btnLaunchAsActivity.setText("Launch PIM As Activity");
                btnLaunchAsFragment.setText("Launch PIM As Fragment");
            }
            IAPDependencies mIapDependencies = new IAPDependencies(appInfraInterface, pimInterface.getUserDataInterface());
            mIAPSettings = new IAPSettings(this);
            mIapInterface = new IAPInterface();
            mIapInterface.init(mIapDependencies, mIAPSettings);
            mServiceDiscoveryInterface = appInfraInterface.getServiceDiscovery();
            receiver = new HomeCountryUpdateReceiver(appInfraInterface);
            mServiceDiscoveryInterface.registerOnHomeCountrySet(receiver);
//            mIAPSettings = new IAPSettings(this);
            mCategorizedProductList = new ArrayList<>();
            mCategorizedProductList.add("HD9745/90000");
            mCategorizedProductList.add("HD9630/90");
            mCategorizedProductList.add("HD9240/90");
            mCategorizedProductList.add("HD9621/90");
//            mIapInterface.init(mIapDependencies, mIAPSettings);
            mIapLaunchInput = new IAPLaunchInput();
            mIapLaunchInput.setHybrisSupported(true);
            mIapLaunchInput.setIapListener(this);
            initializeBazaarVoice();
            initMEC();
        }
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
        finish();
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
        if (v == btnLaunchAsActivity) {
            if (!isUSR) {
                PIMLaunchInput launchInput = new PIMLaunchInput();
                launchInput.setUserLoginListener(this);

                HashMap<PIMParameterToLaunchEnum, Object> map = new HashMap<>();
                map.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, isABTestingStatus);

                launchInput.setParameterToLaunch(map);
                ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
                pimInterface.launch(activityLauncher, launchInput);
            }
        } else if (v == btnLaunchAsFragment) {
            if (isUSR) {
                launchUSR();
            } else {
                launchPIM();
            }
        } else if (v == btnLogout) {
            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                userDataInterface.logoutSession(new LogoutSessionListener() {
                    @Override
                    public void logoutSessionSuccess() {
                        showToast("Logout Success");
                        finish();
                    }

                    @Override
                    public void logoutSessionFailed(Error error) {
                        showToast("Logout Failed due to " + error.getErrCode() + " and error message :" + error.getErrDesc());
                    }
                });
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btnRefreshSession) {
            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                userDataInterface.refreshSession(new RefreshSessionListener() {
                    @Override
                    public void refreshSessionSuccess() {
                        showToast("Refresh session success");
                    }

                    @Override
                    public void refreshSessionFailed(Error error) {
                        showToast("Refresh session failed due to :" + error.getErrCode() + " and error message :" + error.getErrDesc());
                    }

                    @Override
                    public void forcedLogout() {

                    }
                });
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_RegistrationPR) {
            Fragment fragment = new PRGFragment(pimInterface, appInfraInterface);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pimDemoU_mainFragmentContainer, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
        } else if (v == btn_IAP) {
            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                if (mCategorizedProductList.size() > 0) {
                    IAPFlowInput input = new IAPFlowInput(mCategorizedProductList);
                    launchIAP(IAPLaunchInput.IAPFlows.IAP_PRODUCT_CATALOG_VIEW, input, null);
                } else {
                    Toast.makeText(this, "Please add CTN", Toast.LENGTH_SHORT).show();
                }
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_ECS) {
            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                launchECS();
            } else {
                showToast("User is not loged-in, Please login!");
            }
        } else if (v == btn_MCS) {
             showToast("Not implemented");
//            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
//                MECFlowConfigurator pMecFlowConfigurator = new MECFlowConfigurator();
//                pMecFlowConfigurator.setLandingView(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW);
//                launchMECasFragment(MECFlowConfigurator.MECLandingView.MEC_PRODUCT_LIST_VIEW, pMecFlowConfigurator, null);
//            } else {
//                showToast("User is not loged-in, Please login!");
//            }
        } else if (v == btnMigrator) {
            userDataInterface.migrateUserToPIM(new UserMigrationListener() {
                @Override
                public void onUserMigrationSuccess() {
                    showToast("User migrated succesfully");
                }

                @Override
                public void onUserMigrationFailed(Error error) {
                    showToast("user migration failed error code = " + error.getErrCode() + " error message : " + error.getErrDesc());
                }
            });
        } else if (v == btnGetUserDetail) {
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
                showToast("User Details  are :" + userDetails.toString());
            } catch (UserDataInterfaceException e) {
                e.printStackTrace();
                showToast("Error code:" + e.getError().getErrCode() + " Error message :" + e.getError().getErrDesc());
            }

        } else if (v == btnISOIDCToken) {
            if (userDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                boolean oidcToken = userDataInterface.isOIDCToken();
                showToast("isOIDCToken : " + oidcToken);
            } else {
                showToast("User is not loged-in, Please login!");
            }
        }

    }

    private EcsDemoTestUAppInterface iapDemoUAppInterface;
    private MECInterface mMecInterface;

    private void launchECS() {
        iapDemoUAppInterface = new EcsDemoTestUAppInterface();
        iapDemoUAppInterface.init(new EcsDemoTestUAppDependencies(appInfraInterface), new EcsDemoTestAppSettings(this));
        iapDemoUAppInterface.launch(new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, null), new EcsLaunchInput());
    }


    private void launchMECasFragment(MECFlowConfigurator.MECLandingView mecLandingView, MECFlowConfigurator pMecFlowConfigurator, ArrayList<String> pIgnoreRetailerList) {
        pMecFlowConfigurator.setLandingView(mecLandingView);
        mMecLaunchInput.setFlowConfigurator(pMecFlowConfigurator);
        mMecInterface.launch(new ActivityLauncher
                        (mContext, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, null, 0, null),
                mMecLaunchInput);

    }

    private void launchIAP(int pLandingViews, IAPFlowInput pIapFlowInput, ArrayList<String> pIgnoreRetailerList) {
        try {
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
        launchInput.setUserLoginListener(this);
        HashMap<PIMParameterToLaunchEnum, Object> parameter = new HashMap<>();
        parameter.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, isABTestingStatus());
        launchInput.setParameterToLaunch(parameter);
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
            urInterface.launch(fragmentLauncher, urLaunchInput);

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

    private void showToast(final String toastMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, toastMsg, Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public void onLoginSuccess() {
        showToast("PIM Login Success");
        btnLaunchAsActivity.setVisibility(View.GONE);
        String selectedCountry = sharedPreferences.getString(SELECTED_COUNTRY, "");
        spinnerCountryText.setVisibility(View.VISIBLE);
        spinnerCountryText.setText(selectedCountry);
        spinnerCountrySelection.setVisibility(View.GONE);
        btnLaunchAsFragment.setText("Launch User Profile");
        IAPDependencies mIapDependencies = new IAPDependencies(appInfraInterface, pimInterface.getUserDataInterface());
        mIAPSettings = new IAPSettings(this);
        mIapInterface.init(mIapDependencies, mIAPSettings);
        MECDependencies mecDependencies = new MECDependencies(appInfraInterface, pimInterface.getUserDataInterface());
        mMecInterface = new MECInterface();
        mMecInterface.init(mecDependencies, new MECSettings(mContext));
        initializeBazaarVoice();
    }

    @Override
    public void onLoginFailed(Error error) {
        showToast("PIM Login Failed :" + error.getErrCode() + " and reason is" + error.getErrDesc());
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

    public Boolean isABTestingStatus() {
        return isABTestingStatus;
    }

    public void setABTestingStatus(Boolean ABTestingStatus) {
        isABTestingStatus = ABTestingStatus;
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
}
