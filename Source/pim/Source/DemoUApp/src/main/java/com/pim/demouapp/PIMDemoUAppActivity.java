
package com.pim.demouapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.philips.cdp.di.iap.integration.IAPDependencies;
import com.philips.cdp.di.iap.integration.IAPFlowInput;
import com.philips.cdp.di.iap.integration.IAPInterface;
import com.philips.cdp.di.iap.integration.IAPLaunchInput;
import com.philips.cdp.di.iap.integration.IAPListener;
import com.philips.cdp.di.iap.integration.IAPSettings;
import com.philips.cdp.di.iap.utils.IAPUtility;
import com.philips.cdp.registration.ThemeHelper;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfraInterface;
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
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

public class PIMDemoUAppActivity extends AppCompatActivity implements View.OnClickListener, UserRegistrationUIEventListener, UserLoginListener, IAPListener {
    private String TAG = PIMDemoUAppActivity.class.getSimpleName();
    private final int DEFAULT_THEME = R.style.Theme_DLS_Blue_UltraLight;
    //Theme
    public static final String KEY_ACTIVITY_THEME = "KEY_ACTIVITY_THEME";

    private Button btnLaunchAsActivity, btnLaunchAsFragment, btnLogout, btnRefreshSession, btnISOIDCToken, btnMigrator, btnGetUserDetail, btn_RegistrationPR, btn_IAP;
    private Switch aSwitch;
    private UserDataInterface userDataInterface;
    private PIMInterface pimInterface;
    private URInterface urInterface;
    private boolean isUSR;
    private Context mContext;

    @NonNull
    private AppInfraInterface appInfraInterface;
    private IAPInterface mIapInterface;
    private IAPSettings mIAPSettings;
    private IAPLaunchInput mIapLaunchInput;
    private ArrayList<String> mCategorizedProductList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo_uapp);

        mContext = this;
        Label appversion = findViewById(R.id.appversion);
        appversion.setText("Version : " + BuildConfig.VERSION_NAME);

        appInfraInterface = PIMDemoUAppInterface.mAppInfra;

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
        btn_RegistrationPR = findViewById(R.id.btn_RegistrationPR);
        btn_RegistrationPR.setOnClickListener(this);
        btn_IAP = findViewById(R.id.btn_IAP);
        btn_IAP.setOnClickListener(this);
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface);
        PIMDemoUAppSettings pimDemoUAppSettings = new PIMDemoUAppSettings(this);

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                appInfraInterface.getTagging().setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTIN);
            } else {
                appInfraInterface.getTagging().setPrivacyConsent(AppTaggingInterface.PrivacyStatus.OPTOUT);
            }
        });
        if (getIntent().getExtras() != null && getIntent().getExtras().get("SelectedLib").equals("USR")) {
            isUSR = true;
            Log.i(TAG, "Selected Liberary : USR");
            btnLaunchAsActivity.setVisibility(View.GONE);
            btn_RegistrationPR.setVisibility(View.GONE);
            btnMigrator.setVisibility(View.GONE);
            btnISOIDCToken.setVisibility(View.GONE);
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
            mIapInterface = new IAPInterface();
            mIAPSettings = new IAPSettings(this);
            mCategorizedProductList = new ArrayList<>();
            mCategorizedProductList.add("HD9745/90000");
            mCategorizedProductList.add("HD9630/90");
            mCategorizedProductList.add("HD9240/90");
            mCategorizedProductList.add("HD9621/90");
            mIapInterface.init(mIapDependencies, mIAPSettings);
            mIapLaunchInput = new IAPLaunchInput();
            mIapLaunchInput.setHybrisSupported(true);
            mIapLaunchInput.setIapListener(this);

            IAPUtility.getInstance().setHybrisSupported(true);

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initTheme() {
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
            Fragment fragment = new PRGFragment(pimInterface);
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

    private void launchIAP(int pLandingViews, IAPFlowInput pIapFlowInput, ArrayList<String> pIgnoreRetailerList) {
        try {
            int themeResourceID = new ThemeHelper(this).getThemeResourceId();
            mIapInterface.launch(new ActivityLauncher
                            (this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT, null, themeResourceID, null),
                    mIapLaunchInput);

        } catch (RuntimeException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void launchPIM() {
        PIMLaunchInput launchInput = new PIMLaunchInput();
        FragmentLauncher fragmentLauncher = new FragmentLauncher(this, R.id.pimDemoU_mainFragmentContainer, null);
        launchInput.setUserLoginListener(this);
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
        btnLaunchAsFragment.setText("Launch User Profile");
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
}
