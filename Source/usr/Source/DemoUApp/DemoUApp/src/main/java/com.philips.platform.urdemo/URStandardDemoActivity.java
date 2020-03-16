
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.platform.urdemo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.adobe.mobile.Config;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.UserLoginState;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.coppa.base.Consent;
import com.philips.cdp.registration.coppa.base.CoppaExtension;
import com.philips.cdp.registration.coppa.interfaces.CoppaConsentUpdateCallback;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.listener.HSDPAuthenticationListener;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.Gender;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentStates;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.Switch;
import com.philips.platform.urdemolibrary.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class URStandardDemoActivity extends UIDActivity implements OnClickListener,
        UserRegistrationUIEventListener, UserRegistrationListener, RefreshLoginSessionHandler, HSDPAuthenticationListener {

    private static final String USR_PERSONAL_CONSENT = "USR_PERSONAL_CONSENT";
    private String TAG = "URStandardDemoActivity";

    private final String HSDP_UUID_SHOULD_UPLOAD = "hsdpUUIDUpload";
    private final String HSDP_SKIP_HSDP_LOGIN = "skipHSDPLogin";
    private final String PERSONAL_CONSENT = "personalConsentRequired";
    private final String CUSTOMOPTIN = "customOptin";
    private final String SKIPOPTIN = "skipOptin";
    private final String COUNTY_SELECTION = "ShowCountrySelection";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String restoredText;
    private RadioGroup mRadioGender;
    private LinearLayout mLlConfiguration;
    private LinearLayout LlcoppaItems;
    private RadioGroup mRadioGroup;
    private CheckBox mCheckBox;
    private User mUser;
    private Button mBtnRegistrationWithAccountSettings;
    private CoppaExtension coppaExtension;
    private Switch mSkipHSDPSwitch, hsdpUuidUpload, consentConfirmationStatus, updateCoppaConsentStatus,mEnablePersonalConsentSwitch, countryShowSwitch;
    private Switch mSkipOptin,  customOptin;

    private Label btn_registration_with_hsdp_status_lbl;

    URInterface urInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initTheme();
        Config.setDebugLogging(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.usr_demoactivity);

        mBtnRegistrationWithAccountSettings = findViewById(R.id.btn_registration_with_account);
        mBtnRegistrationWithAccountSettings.setOnClickListener(this);

        Button mBtnRegistrationMarketingOptIn = findViewById(R.id.btn_marketing_opt_in);
        mBtnRegistrationMarketingOptIn.setOnClickListener(this);

        Button mBtnRegistrationWithHsdp = findViewById(R.id.btn_registration_with_hsdp);
        mBtnRegistrationWithHsdp.setOnClickListener(this);

        Button mBtnRegistrationWithHsdpStatus = findViewById(R.id.btn_registration_with_hsdp_status);
        mBtnRegistrationWithHsdpStatus.setOnClickListener(this);

        Button mBtnRegistrationWithOutAccountSettings = findViewById(R.id.btn_registration_without_account);
        mBtnRegistrationWithOutAccountSettings.setOnClickListener(this);

        btn_registration_with_hsdp_status_lbl = findViewById(R.id.btn_registration_with_hsdp_status_lbl);
        urInterface = new URInterface();
        final Button mBtnHsdpRefreshAccessToken = findViewById(R.id.btn_refresh_token);
        mBtnHsdpRefreshAccessToken.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        if (RegistrationConfiguration.getInstance().isHsdpFlow()) {
            mBtnHsdpRefreshAccessToken.setVisibility(VISIBLE);
        } else {
            mBtnHsdpRefreshAccessToken.setVisibility(GONE);
        }

        mUser = new User(mContext);
        mUser.registerUserRegistrationListener(this);
        mUser.registerHSDPAuthenticationListener(this);
        Button mBtnRefresh = findViewById(R.id.btn_refresh_user);
        mBtnRefresh.setOnClickListener(this);


        Button btn_refresh_user_hsdp = findViewById(R.id.btn_refresh_user_hsdp);
        btn_refresh_user_hsdp.setOnClickListener(this);


        Button mBtnUpdateDOB = findViewById(R.id.btn_update_date_of_birth);
        mBtnUpdateDOB.setOnClickListener(this);
        Button mBtnUpdateGender = findViewById(R.id.btn_update_gender);
        mBtnUpdateGender.setOnClickListener(this);
        mRadioGender = findViewById(R.id.genderRadio);
        mRadioGender.check(R.id.Male);

        mLlConfiguration = findViewById(R.id.ll_configuartion);
        LlcoppaItems = findViewById(R.id.CoppaItems);
        mRadioGroup = findViewById(R.id.myRadioGroup);
        mSkipHSDPSwitch = findViewById(R.id.skip_hsdp_switch);
        mEnablePersonalConsentSwitch = findViewById(R.id.enable_personal_consent_switch);
        countryShowSwitch = findViewById(R.id.showCountrySelection);
        mSkipOptin = findViewById(R.id.enable_skip_optin);
        customOptin = findViewById(R.id.enable_custom_optin);
        hsdpUuidUpload = findViewById(R.id.switch_hsdp_uuid_upload);
        consentConfirmationStatus = findViewById(R.id.updateCoppaConsentConfirmationStatus);
        updateCoppaConsentStatus = findViewById(R.id.updateCoppaConsentStatus);
        SharedPreferences prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
        restoredText = prefs.getString("reg_environment", null);
        mSkipHSDPSwitch.setChecked(prefs.getBoolean("reg_delay_hsdp_configuration", false));
        mEnablePersonalConsentSwitch.setChecked(prefs.getBoolean("reg_personal_consent_configuration", false));
        countryShowSwitch.setChecked(prefs.getBoolean("reg_country_selection", false));
        mSkipOptin.setChecked(prefs.getBoolean("reg_skipoptin_configuration", false));
        customOptin.setChecked(prefs.getBoolean("reg_customoptin_configuration", false));


        final String restoredHSDPText = prefs.getString("reg_hsdp_environment", null);
        if (restoredText != null) {

            switch (RegUtility.getConfiguration(restoredText)) {
                case EVALUATION:
                    mRadioGroup.check(R.id.Evalution);
                    break;
                case DEVELOPMENT:
                    mRadioGroup.check(R.id.Development);
                    break;
                case PRODUCTION:
                    mRadioGroup.check(R.id.Production);
                    break;
                case STAGING:
                    mRadioGroup.check(R.id.Stagging);
                    break;
                case TESTING:
                    mRadioGroup.check(R.id.Testing);
                    break;
            }

        }

        mLlConfiguration.setVisibility(GONE);
        LlcoppaItems.setVisibility(GONE);

        Button mBtnChangeConfiguaration = findViewById(R.id.btn_change_configuration);
        mBtnChangeConfiguaration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlConfiguration.setVisibility(VISIBLE);
            }
        });

        Button mBtnCoppa = findViewById(R.id.coppa);
        mBtnCoppa.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser.getUserLoginState() != UserLoginState.USER_LOGGED_IN) {
                    showToast(" User not Signed in");
                } else {
                    LlcoppaItems.setVisibility(VISIBLE);
                }
            }
        });
        updateSkipHsdpStatus(false);

        mSkipHSDPSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateSkipHsdpStatus(b);
            }
        });

        countryShowSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                showCountry(isChecked);
            }
        });

        mEnablePersonalConsentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enablePersonalConsentSwitch(isChecked);
            }
        });
        mSkipOptin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSkipOptin(isChecked);
            }
        });

        customOptin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateCustomOptin(isChecked);
            }
        });;


        Button fethContent = findViewById(R.id.fetchConcent);
        fethContent.setOnClickListener(this);


        consentConfirmationStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateConfirmationStatus(b);
            }
        });


        updateCoppaConsentStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateCoppaConsentStatus(b);
            }

        });

        mCheckBox = findViewById(R.id.cd_hsdp);
        if (restoredHSDPText != null || RegistrationConfiguration.getInstance().isHsdpFlow()) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(false);
        }

        if (getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).getBoolean("reg_delay_hsdp_configuration", false)) {
            mSkipHSDPSwitch.setChecked(true);
        }
        updateHSDPUuidSwitch(false);
        if (getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).getBoolean("reg_skipoptin_configuration", false)) {
            mSkipOptin.setChecked(true);
        }

        if (getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).getBoolean("reg_customoptin_configuration", false)) {
            customOptin.setChecked(true);
        }




        hsdpUuidUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateHSDPUuidSwitch(b);
            }
        });
        Button mBtnApply = findViewById(R.id.Apply);
        mBtnApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlConfiguration.setVisibility(GONE);

                //Resetn
                UserRegistrationInitializer.getInstance().resetInitializationState();

                int checkedId = mRadioGroup.getCheckedRadioButtonId();
                // find which radio button is selected

                if (checkedId == R.id.Evalution) {
                    Toast.makeText(getApplicationContext(), "choice: Evalution",
                            Toast.LENGTH_SHORT).show();
                    restoredText = Configuration.EVALUATION.getValue();
                } else if (checkedId == R.id.Testing) {
                    Toast.makeText(getApplicationContext(), "choice: Testing",
                            Toast.LENGTH_SHORT).show();
                    restoredText = Configuration.TESTING.getValue();
                } else if (checkedId == R.id.Development) {
                    Toast.makeText(getApplicationContext(), "choice: Development",
                            Toast.LENGTH_SHORT).show();
                    restoredText = Configuration.DEVELOPMENT.getValue();
                } else if (checkedId == R.id.Production) {
                    Toast.makeText(getApplicationContext(), "choice: Production",
                            Toast.LENGTH_SHORT).show();
                    restoredText = Configuration.PRODUCTION.getValue();
                } else if (checkedId == R.id.Stagging) {
                    Toast.makeText(getApplicationContext(), "choice: Staging",
                            Toast.LENGTH_SHORT).show();
                    restoredText = Configuration.STAGING.getValue();
                }

                if (restoredText != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).edit();
                    editor.putString("reg_environment", restoredText);
                    if (mCheckBox.isChecked()) {
                        editor.putString("reg_hsdp_environment", restoredText).apply();
                        InitHsdp init = new InitHsdp();
                        init.initHSDP(RegUtility.getConfiguration(restoredText), getApplicationContext(), URDemouAppInterface.appInfra);
                        mBtnHsdpRefreshAccessToken.setVisibility(VISIBLE);
                        urInterface.init(new URDemouAppDependencies(URDemouAppInterface.appInfra), new URDemouAppSettings(getApplicationContext()));

                    } else {
                        editor.remove("reg_hsdp_environment").commit();
                        mBtnHsdpRefreshAccessToken.setVisibility(GONE);
                    }

                    if (mSkipHSDPSwitch.isChecked()) {
                        editor.putBoolean("reg_delay_hsdp_configuration", true).apply();
                        InitHsdp init = new InitHsdp();
                        init.initHSDP(RegUtility.getConfiguration(restoredText), getApplicationContext(), URDemouAppInterface.appInfra);
                        urInterface.init(new URDemouAppDependencies(URDemouAppInterface.appInfra), new URDemouAppSettings(getApplicationContext()));

                    } else {
                        editor.remove("reg_delay_hsdp_configuration").apply();

                    }



                    if (countryShowSwitch.isChecked()) {
                        editor.putBoolean("reg_country_selection", countryShowSwitch.isChecked()).apply();
                        urInterface.init(new URDemouAppDependencies(URDemouAppInterface.appInfra), new URDemouAppSettings(getApplicationContext()));

                    } else{
                        editor.remove("reg_country_selection").apply();

                    }


                    if (mEnablePersonalConsentSwitch.isChecked()) {
                        editor.putBoolean("reg_personal_consent_configuration", mEnablePersonalConsentSwitch.isChecked()).apply();
                        urInterface.init(new URDemouAppDependencies(URDemouAppInterface.appInfra), new URDemouAppSettings(getApplicationContext()));

                    } else{
                        editor.remove("reg_personal_consent_configuration").apply();

                    }

                    if (mSkipOptin.isChecked()) {
                        editor.putBoolean("reg_skipoptin_configuration", mSkipOptin.isChecked()).apply();
                        urInterface.init(new URDemouAppDependencies(URDemouAppInterface.appInfra), new URDemouAppSettings(getApplicationContext()));

                    } else{
                        editor.remove("reg_skipoptin_configuration").apply();

                    }

                    if (customOptin.isChecked()) {
                        editor.putBoolean("reg_customoptin_configuration", customOptin.isChecked()).apply();
                        urInterface.init(new URDemouAppDependencies(URDemouAppInterface.appInfra), new URDemouAppSettings(getApplicationContext()));

                    } else{
                        editor.remove("reg_customoptin_configuration").apply();

                    }


                    updateSkipHsdpStatus(mSkipHSDPSwitch.isChecked());
                    SharedPreferences prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
                    String restoredText = prefs.getString("reg_hsdp_environment", null);
                    RLog.d("Restored teest", "" + restoredText);
                }

            }
        });
        Button mBtnCancel = findViewById(R.id.Cancel);
        mBtnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlConfiguration.setVisibility(GONE);
            }
        });

        if (mCheckBox.isChecked()) {
            mBtnHsdpRefreshAccessToken.setVisibility(VISIBLE);
        } else {
            mBtnHsdpRefreshAccessToken.setVisibility(GONE);
        }
    }

    private void enablePersonalConsentSwitch(boolean isChecked) {
        RLog.d("enablePersonalConsentSwitch", " Going to set :" + isChecked);
        final AppInfraInterface appInfraInterface = URDemouAppInterface.appInfra;
        appInfraInterface.getConfigInterface().setPropertyForKey(PERSONAL_CONSENT, "UserRegistration", String.valueOf(isChecked), configError);
        mEnablePersonalConsentSwitch.setChecked(isChecked);
    }

    private void showCountry(boolean isChecked){
        RLog.d("reg_country_selection","reg_country_selection"+ isChecked);
        final AppInfraInterface appInfraInterface = URDemouAppInterface.appInfra;
        appInfraInterface.getConfigInterface().setPropertyForKey(COUNTY_SELECTION, "UserRegistration", String.valueOf(isChecked), configError);
        countryShowSwitch.setChecked(isChecked);


    }

    private void updateAuthoriseHSDP() {
        final UserLoginState userLoginState = mUser.getUserLoginState();
        RLog.d(TAG, " : updateAuthoriseHSDP :UserLoginState :" + mUser.getUserLoginState());
        if (userLoginState == UserLoginState.PENDING_HSDP_LOGIN) {
            RLog.d(TAG, " : updateAuthoriseHSDP : making  HSDP call");
            //make HSDP login call
            mUser.authorizeHSDP(this);

        }
    }

    private void updateCustomOptin(boolean b) {
        RLog.d("updateCustomOptin", " Going to set :" + b);
        final AppInfraInterface appInfraInterface = URDemouAppInterface.appInfra;
        appInfraInterface.getConfigInterface().setPropertyForKey(CUSTOMOPTIN, "UserRegistration", String.valueOf(b), configError);
        customOptin.setChecked(b);
    }


    private void updateSkipOptin(boolean b) {
        RLog.d("updateSkipOptin", " Going to set :" + b);
        final AppInfraInterface appInfraInterface = URDemouAppInterface.appInfra;
        appInfraInterface.getConfigInterface().setPropertyForKey(SKIPOPTIN, "UserRegistration", String.valueOf(b), configError);
        mSkipOptin.setChecked(b);
    }



    private void updateSkipHsdpStatus(boolean b) {
        RLog.d("updateSkipHsdpStatus", " Going to set :" + b);
        final AppInfraInterface appInfraInterface = URDemouAppInterface.appInfra;
        appInfraInterface.getConfigInterface().setPropertyForKey(HSDP_SKIP_HSDP_LOGIN, "UserRegistration", String.valueOf(b), configError);
        mSkipHSDPSwitch.setChecked(b);
    }


    final AppConfigurationInterface.AppConfigurationError configError = new
            AppConfigurationInterface.AppConfigurationError();

    private void updateHSDPUuidSwitch(boolean b) {
        RLog.d("updateHSDPUuidSwitch", " Going to set :" + b);
        final AppInfraInterface appInfraInterface = URDemouAppInterface.appInfra;
        appInfraInterface.getConfigInterface().setPropertyForKey(HSDP_UUID_SHOULD_UPLOAD, "UserRegistration", String.valueOf(b), configError);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerationWithAccountSettingButtonEnableOnUserSignedIn();

    }

    private void registerationWithAccountSettingButtonEnableOnUserSignedIn() {
   //     if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN || mUser.getUserLoginState() == UserLoginState.PENDING_HSDP_LOGIN)
            mBtnRegistrationWithAccountSettings.setEnabled(true);
     //   else
     //       mBtnRegistrationWithAccountSettings.setEnabled(false);
    }

    @Override
    protected void onStop() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mUser.unRegisterUserRegistrationListener(this);
        mUser.unRegisterHSDPAuthenticationListener(this);
        super.onDestroy();

    }

    private void updateCoppaConsentStatus(boolean b) {

        if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN) {

            coppaExtension = new CoppaExtension(mContext);
            coppaExtension.buildConfiguration();

            coppaExtension.updateCoppaConsentStatus(b, new CoppaConsentUpdateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(mContext, "updateCoppaConsentStatus success", Toast.LENGTH_LONG).show();
                    showToast("updateCoppaConsentStatus success");
                }

                @Override
                public void onFailure(int message) {
                    showToast("updateCoppaConsentStatus failure");
                }
            });
        } else {
            showToast(" User not Signed in");
        }
    }

    private void updateConfirmationStatus(boolean b) {

        if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN) {

            coppaExtension = new CoppaExtension(mContext);
            coppaExtension.buildConfiguration();

            coppaExtension.updateCoppaConsentConfirmationStatus(b, new CoppaConsentUpdateCallback() {
                @Override
                public void onSuccess() {
                    showToast("updateCoppaConsentConfirmationStatus success");
                }

                @Override
                public void onFailure(int message) {
                    showToast("updateCoppaConsentConfirmationStatus failure" + message);
                }
            });
        } else {
            showToast(" User not Signed in");
        }

    }

    @Override
    public void onClick(View v) {
        URLaunchInput urLaunchInput;
        CoppaExtension coppaExtension;
        ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
        int i = v.getId();
        if (i == R.id.btn_registration_with_account) {
            RLog.d(TAG, "Logout");

            HsdpUser hsdpUser = new HsdpUser(mContext);
            if (RegistrationConfiguration.getInstance().isHsdpFlow() && null != hsdpUser.getHsdpUserRecord()) {
                showLogoutSpinner();
                hsdpLogout();
            } else {
                mUser.logout(null);
                LlcoppaItems.setVisibility(GONE);

            }

        } else if (i == R.id.btn_marketing_opt_in) {
            marketingOptIn(activityLauncher, urInterface);

        } else if (i == R.id.btn_registration_without_account) {
            launchRegisteration(activityLauncher, urInterface);

        } else if (i == R.id.btn_registration_with_hsdp) {
            UserLoginState userLoginState = mUser.getUserLoginState();
            if (userLoginState == UserLoginState.PENDING_HSDP_LOGIN) {
                RLog.d(TAG, " : updateAuthoriseHSDP(true)");
                updateAuthoriseHSDP();
            } else {
                showToast(userLoginState.name());
                RLog.d(TAG, " :HSDP button Clicked with userLoginState  without user signedin:" + userLoginState);
            }
        } else if (i == R.id.btn_registration_with_hsdp_status) {
            UserLoginState userLoginState = mUser.getUserLoginState();
            btn_registration_with_hsdp_status_lbl.setVisibility(View.VISIBLE);
            btn_registration_with_hsdp_status_lbl.setText("User Login State : " + userLoginState);
        } else if (i == R.id.btn_refresh_user) {
            RLog.d(TAG, " : Refresh User ");
            handleRefreshAccessToken();

        } else if (i == R.id.btn_refresh_user_hsdp) {
            RLog.d(TAG, " : Refresh Hsdp User ");
            handleHSDPRefreshAccessToken();

        }
        else if (i == R.id.btn_refresh_token) {
            if (RegistrationConfiguration.getInstance().isHsdpFlow()) {
                User user = new User(mContext);
                if (user.getUserLoginState() != UserLoginState.USER_LOGGED_IN) {
                    showToast("Please login before refreshing access token");
                } else {
                    mProgressDialog.setMessage("Refreshing...");
                    mProgressDialog.show();
                    user.refreshLoginSession(this);
                }
            }

        } else if (i == R.id.btn_update_gender) {
            handleGender();
        } else if (i == R.id.btn_update_date_of_birth) {
            User user = new User(mContext);
            handleDoBUpdate(user.getDateOfBirth());
        } else if (i == R.id.fetchConcent) {
            if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN) {
                coppaExtension = new CoppaExtension(mContext);
                Consent c = coppaExtension.getConsent();
                Toast.makeText(mContext, "Confirmation State " + c.getConfirmationGiven() +
                        "\n consent state" + c.getGiven(), Toast.LENGTH_LONG).show();
            } else {
                showToast(" User not Signed in");
            }
        } else if (i == R.id.updateCoppaConsentStatus) {


            if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN) {

                coppaExtension = new CoppaExtension(mContext);
                coppaExtension.buildConfiguration();

                coppaExtension.updateCoppaConsentStatus(true, new CoppaConsentUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "updateCoppaConsentStatus success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int message) {
                        Toast.makeText(mContext, "updateCoppaConsentStatus failure" + message, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                showToast(" User not Signed in");
            }
        } else if (i == R.id.updateCoppaConsentConfirmationStatus) {

            if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN) {

                coppaExtension = new CoppaExtension(mContext);
                coppaExtension.buildConfiguration();

                coppaExtension.updateCoppaConsentConfirmationStatus(true, new CoppaConsentUpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "updateCoppaConsentConfirmationStatus success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int message) {
                        Toast.makeText(mContext, "updateCoppaConsentConfirmationStatus failure" + message, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                showToast(" User not Signed in");
            }

        }
    }

    private void launchRegisteration(ActivityLauncher activityLauncher, URInterface urInterface) {
        URLaunchInput urLaunchInput;
        RLog.d(TAG, " : Registration");
        urLaunchInput = new URLaunchInput();
        urLaunchInput.setRegistrationFunction(RegistrationFunction.SignIn);
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.setEndPointScreen(RegistrationLaunchMode.USER_DETAILS);
        urLaunchInput.setUserPersonalConsentStatus(ConsentStates.inactive);

        RegistrationContentConfiguration registrationContentConfiguration = getRegistrationContentConfiguration();
        registrationContentConfiguration.setPersonalConsentContentErrorResId(R.string.personalConsentAcceptanceText_Error);
        final ArrayList<String> types = new ArrayList<>();
        types.add(USR_PERSONAL_CONSENT);
        ConsentDefinition consentDefination = new ConsentDefinition(R.string.personalConsentText, R.string.personalConsentAcceptanceText,
                types, 1);

        registrationContentConfiguration.setPersonalConsentDefinition(consentDefination);
        urLaunchInput.setRegistrationContentConfiguration(registrationContentConfiguration);
        urInterface.launch(activityLauncher, urLaunchInput);
    }

    private void marketingOptIn(ActivityLauncher activityLauncher, URInterface urInterface) {
        URLaunchInput urLaunchInput;
        RLog.d(TAG, " : Registration");
        urLaunchInput = new URLaunchInput();
        urLaunchInput.setEndPointScreen(RegistrationLaunchMode.MARKETING_OPT);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
        urLaunchInput.setUIFlow(UIFlow.FLOW_B);
        urLaunchInput.setUserRegistrationUIEventListener(this);

        urLaunchInput.setUserPersonalConsentStatus(ConsentStates.inactive);
        RegistrationContentConfiguration registrationContentConfiguration = getRegistrationContentConfiguration();
        registrationContentConfiguration.setPersonalConsentContentErrorResId(R.string.personalConsentAcceptanceText_Error);
        final ArrayList<String> types = new ArrayList<>();
        types.add(USR_PERSONAL_CONSENT);
        ConsentDefinition consentDefination = new ConsentDefinition(R.string.personalConsentText, R.string.personalConsentAcceptanceText,
                types, 1);

        registrationContentConfiguration.setPersonalConsentDefinition(consentDefination);
        urLaunchInput.setRegistrationContentConfiguration(registrationContentConfiguration);

        urInterface.launch(activityLauncher, urLaunchInput);
        final UIFlow uiFlow = RegUtility.getUiFlow();

        switch (uiFlow) {

            case FLOW_A:
                Toast.makeText(mContext, "UI Flow Type A", Toast.LENGTH_LONG).show();
                RLog.d(TAG, "UI Flow Type A");
                break;
            case FLOW_B:
                Toast.makeText(mContext, "UI Flow Type B", Toast.LENGTH_LONG).show();
                RLog.d(TAG, "UI Flow Type B");
                break;
            default:
                break;
        }
    }

    private void hsdpLogout() {
        mUser.logout(new LogoutHandler() {
            @Override
            public void onLogoutSuccess() {
                hideLogoutSpinner();
                showToast("Logout success");
                LlcoppaItems.setVisibility(GONE);

            }

            @Override
            public void onLogoutFailure(int responseCode, String message) {
                hideLogoutSpinner();
                showToast("Code " + responseCode + "Message" + message);
            }
        });
    }

    private void handleGender() {
        mProgressDialog.setMessage("Updating...");
        mProgressDialog.show();
        Gender gender;

        if (mRadioGender.getCheckedRadioButtonId() == R.id.Male) {
            gender = Gender.MALE;
        } else if (mRadioGender.getCheckedRadioButtonId() == R.id.Female) {
            gender = Gender.FEMALE;
        } else {
            gender = Gender.NONE;
        }

        final User user1 = new User(mContext);
        user1.updateGender(new UpdateUserDetailsHandler() {
            @Override
            public void onUpdateSuccess() {
                mProgressDialog.hide();
                showToast("onUpdateSuccess");
            }

            @Override
            public void onUpdateFailedWithError(Error error) {
                mProgressDialog.hide();
                showToast("onUpdateFailedWithError" + error.getErrCode());
            }
        }, gender);


    }

    private void handleDoBUpdate(Date userDOB) {
        int year, month, day;
        Calendar calendar = new GregorianCalendar();
        if (userDOB != null) {
            calendar.setTime(userDOB);
        } else {
            RLog.d(this.getClass().getSimpleName(), "Date is null");
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mProgressDialog.setMessage("Updating...");
                        mProgressDialog.show();

                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth, 0, 0);

                        final User user1 = new User(mContext);
                        user1.updateDateOfBirth(new UpdateUserDetailsHandler() {
                            @Override
                            public void onUpdateSuccess() {
                                mProgressDialog.hide();
                                showToast("onUpdateSuccess");
                            }

                            @Override
                            public void onUpdateFailedWithError(Error error) {
                                mProgressDialog.hide();
                                showToast("onUpdateFailedWithError" + error.getErrCode());
                            }
                        }, c.getTime());
                    }
                }, year, month, day);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();

    }

    private void handleRefreshAccessToken() {
        if (mUser.getUserLoginState().ordinal() >= UserLoginState.PENDING_HSDP_LOGIN.ordinal()) {
            mUser.refreshLoginSession(new RefreshLoginSessionHandler() {
                @Override
                public void onRefreshLoginSessionSuccess() {
                    showToast("Success to refresh access token" + mUser.getAccessToken());
                }

                @Override
                public void onRefreshLoginSessionFailedWithError(int error) {
                    showToast("Failed to refresh access token");
                }

                @Override
                public void forcedLogout() {
                    showToast("Forced Logout");
                }
            });
        } else {
            showToast("Please login");
        }
    }

    private void handleHSDPRefreshAccessToken() {
        if (mUser.getUserLoginState() == UserLoginState.USER_LOGGED_IN) {
            mUser.refreshHSDPLoginSession(new RefreshLoginSessionHandler() {
                @Override
                public void onRefreshLoginSessionSuccess() {
                    showToast("Success to refresh access token" + mUser.getAccessToken());
                }

                @Override
                public void onRefreshLoginSessionFailedWithError(int error) {
                    showToast("Failed to refresh access token");
                }

                @Override
                public void forcedLogout() {
                    showToast("Forced Logout");
                }
            });
        } else {
            showToast("Please login");
        }
    }



    @Override
    public void onUserRegistrationComplete(Activity activity) {
        RLog.d(TAG, " : onUserRegistrationComplete");
        if (activity != null)
            activity.finish();
        showToast("HSDP Skip login status : " + RegistrationConfiguration.getInstance().isHSDPSkipLoginConfigurationAvailable());
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

        RLog.d(TAG, " : onPersonalConsentClick");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.philips.com"));
        activity.startActivity(browserIntent);
    }

    @Override
    public void onUserLogoutSuccess() {
        RLog.d(TAG, " : onUserLogoutSuccess");
    }

    @Override
    public void onUserLogoutFailure() {
        RLog.d(TAG, "  : onUserLogoutFailure");
    }

    @Override
    public void onUserLogoutSuccessWithInvalidAccessToken() {
        RLog.d(TAG, "  : onUserLogoutSuccessWithInvalidAccessToken");
        showToast("onUserLogoutSuccessWithInvalidAccessToken ");
    }


    @Override
    public void onHSDPLoginSuccess() {
        RLog.d(TAG, "  : onHSDPLoginSuccess");
        showToast("HSDP Login Success ");
    }

    @Override
    public void onHSDPLoginFailure(int errorCode, String msg) {
        RLog.d(TAG, "  : onHSDPLoginFailure with " + msg);
        showToast("HSDPLogin Failure with error code : " + errorCode + " and reason :" + msg);
    }


    private void dimissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    final Handler handler = new Handler();

    private void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(URStandardDemoActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onRefreshLoginSessionSuccess() {
        dimissDialog();
        RLog.d(TAG, " Access token: " + mUser.getHsdpAccessToken());
        showToast("Success to refresh hsdp access token");
    }

    @Override
    public void onRefreshLoginSessionFailedWithError(int error) {
        dimissDialog();
        showToast("Failed to refresh hsdp access token");
    }

    @Override
    public void forcedLogout() {
        dimissDialog();
        showToast("Failed to refresh hsdp access token");
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

    private void showLogoutSpinner() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, com.philips.cdp.registration.R.style.reg_Custom_loaderTheme);
            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog.setCancelable(false);
        }
        if (!(isFinishing()) && (mProgressDialog != null)) mProgressDialog.show();
    }


    private void hideLogoutSpinner() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void initTheme() {
        int themeResourceID = new ThemeHelper(this).getThemeResourceId();
        int themeIndex = themeResourceID;
        if (themeIndex <= 0) {
            themeIndex = R.style.Theme_DLS_Blue_UltraLight;
        }
        getTheme().applyStyle(themeIndex, true);
        UIDHelper.init(new ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE));

    }
}
