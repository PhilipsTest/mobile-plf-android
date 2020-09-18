
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.sample;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.StyleRes;
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

import com.janrain.android.Jump;
import com.janrain.android.engage.session.JRSession;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.Configuration;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.handlers.RefreshLoginSessionHandler;
import com.philips.cdp.registration.handlers.UpdateUserDetailsHandler;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.Gender;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.cdp.registration.ui.utils.RegConstants;
import com.philips.cdp.registration.ui.utils.RegUtility;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.UIFlow;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ColorRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.utils.UIDActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class URStandardDemoActivity extends UIDActivity implements OnClickListener,
        UserRegistrationUIEventListener, UserRegistrationListener, RefreshLoginSessionHandler {

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String restoredText;
    private RadioGroup mRadioGender;
    private LinearLayout mLlConfiguration;
    private RadioGroup mRadioGroup;
    private CheckBox mCheckBox;
    private User mUser;
    private boolean isCountrySelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.usr_demoactivity);

        Button mBtnRegistrationWithAccountSettings = (Button) findViewById(R.id.btn_registration_with_account);
        mBtnRegistrationWithAccountSettings.setOnClickListener(this);

        Button mBtnRegistrationMarketingOptIn = (Button) findViewById(R.id.btn_marketing_opt_in);
        mBtnRegistrationMarketingOptIn.setOnClickListener(this);

        Button mBtnRegistrationWithOutAccountSettings = (Button) findViewById(R.id.btn_registration_without_account);
        mBtnRegistrationWithOutAccountSettings.setOnClickListener(this);

        final Button mBtnHsdpRefreshAccessToken = (Button) findViewById(R.id.btn_refresh_token);
        mBtnHsdpRefreshAccessToken.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        if (RegistrationConfiguration.getInstance().isHsdpFlow()) {
            mBtnHsdpRefreshAccessToken.setVisibility(VISIBLE);
        } else {
            mBtnHsdpRefreshAccessToken.setVisibility(GONE);
        }

        com.philips.platform.uid.view.widget.Switch mCountrySelectionSwitch = (com.philips.platform.uid.view.widget.Switch) findViewById(R.id.county_selection_switch);
        mUser = new User(mContext);
        mUser.registerUserRegistrationListener(this);
        Button mBtnRefresh = (Button) findViewById(R.id.btn_refresh_user);
        mBtnRefresh.setOnClickListener(this);

        Button mBtnUpdateDOB = (Button) findViewById(R.id.btn_update_date_of_birth);
        mBtnUpdateDOB.setOnClickListener(this);
        Button mBtnUpdateGender = (Button) findViewById(R.id.btn_update_gender);
        mBtnUpdateGender.setOnClickListener(this);
        mRadioGender = (RadioGroup) findViewById(R.id.genderRadio);
        mRadioGender.check(R.id.Male);

//        mCountrySelectionSwitch = (Switch) findViewById(R.id.county_selection_switch);
        mLlConfiguration = (LinearLayout) findViewById(R.id.ll_configuartion);
        mRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        SharedPreferences prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
        restoredText = prefs.getString("reg_environment", null);
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
        Button mBtnChangeConfiguaration = (Button) findViewById(R.id.btn_change_configuration);
        mBtnChangeConfiguaration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlConfiguration.setVisibility(VISIBLE);
            }
        });

        mCountrySelectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCountrySelection = isChecked;
            }
        });


        mCheckBox = (CheckBox) findViewById(R.id.cd_hsdp);
        if (restoredHSDPText != null) {
            mCheckBox.setChecked(true);
        }

        Button mBtnApply = (Button) findViewById(R.id.Apply);
        mBtnApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlConfiguration.setVisibility(GONE);

                //Resetn
                UserRegistrationInitializer.getInstance().resetInitializationState();
                //Logout mUser
                clearData();

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
                    //  RegistrationSampleApplication.getInstance().initRegistration(Configuration.PRODUCTION);
                } else if (checkedId == R.id.Stagging) {
                    Toast.makeText(getApplicationContext(), "choice: Staging",
                            Toast.LENGTH_SHORT).show();
                    restoredText = Configuration.STAGING.getValue();
                }

                if (restoredText != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE).edit();
                    editor.putString("reg_environment", restoredText);
                    if (mCheckBox.isChecked()) {
                        editor.putString("reg_hsdp_environment", restoredText).commit();
                        mBtnHsdpRefreshAccessToken.setVisibility(VISIBLE);
                    } else {
                        editor.remove("reg_hsdp_environment").commit();
                        mBtnHsdpRefreshAccessToken.setVisibility(GONE);
                    }

                    SharedPreferences prefs = getSharedPreferences("reg_dynamic_config", MODE_PRIVATE);
                    String restoredText = prefs.getString("reg_hsdp_environment", null);
                    RLog.d("Restored teest", "" + restoredText);

                }

            }
        });
        Button mBtnCancel = (Button) findViewById(R.id.Cancel);
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

    private void clearData() {
        HsdpUser hsdpUser = new HsdpUser(mContext);
        hsdpUser.deleteFromDisk();
        if (JRSession.getInstance() != null) {
            JRSession.getInstance().signOutAllAuthenticatedUsers();
        }
        Jump.signOutCaptureUser(mContext);

    }

    @Override
    protected void onStart() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationSampleActivity : onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        AppTagging.collectLifecycleData(this);
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationSampleActivity : onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        AppTagging.pauseCollectingLifecycleData();
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationSampleActivity : onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        RLog.d(RLog.ACTIVITY_LIFECYCLE, "RegistrationSampleActivity : onStop");

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mUser.unRegisterUserRegistrationListener(this);
        RLog.d(RLog.EVENT_LISTENERS, "RegistrationSampleActivity unregister : RegisterUserRegistrationListener");
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        URLaunchInput urLaunchInput;
        ActivityLauncher activityLauncher = new ActivityLauncher(ActivityLauncher.
                ActivityOrientation.SCREEN_ORIENTATION_SENSOR, 0);
        URInterface urInterface;
        initCountrySelection();

        int i = v.getId();
        if (i == R.id.btn_registration_with_account) {
            RLog.d(RLog.ONCLICK, "Logout");

            HsdpUser hsdpUser = new HsdpUser(mContext);
            if (RegistrationConfiguration.getInstance().isHsdpFlow() && null != hsdpUser.getHsdpUserRecord()) {
                showLogoutSpinner();
                mUser.logout(new LogoutHandler() {
                    @Override
                    public void onLogoutSuccess() {
                        hideLogoutSpinner();
                        Toast.makeText(mContext, "Logout success", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLogoutFailure(int responseCode, String message) {
                        hideLogoutSpinner();
                        Toast.makeText(mContext, "Code "+ responseCode +"Message"+message, Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                mUser.logout(null);
            }

        } else if (i == R.id.btn_marketing_opt_in) {
            RLog.d(RLog.ONCLICK, "RegistrationSampleActivity : Registration");
            urLaunchInput = new URLaunchInput();
            urLaunchInput.setEndPointScreen(RegistrationLaunchMode.MARKETING_OPT);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
            urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
            urLaunchInput.setUIFlow(UIFlow.FLOW_B);
            urLaunchInput.setUserRegistrationUIEventListener(this);

            urInterface = new URInterface();
            urInterface.launch(activityLauncher, urLaunchInput);
            final UIFlow uiFlow = RegUtility.getUiFlow();

            switch (uiFlow) {

                case FLOW_A:
                    Toast.makeText(mContext, "UI Flow Type A", Toast.LENGTH_LONG).show();
                    RLog.d(RLog.AB_TESTING, "UI Flow Type A");
                    break;
                case FLOW_B:
                    Toast.makeText(mContext, "UI Flow Type B", Toast.LENGTH_LONG).show();
                    RLog.d(RLog.AB_TESTING, "UI Flow Type B");
                    break;
                default:
                    break;
            }

        } else if (i == R.id.btn_registration_without_account) {
            RLog.d(RLog.ONCLICK, "RegistrationSampleActivity : Registration");
            urLaunchInput = new URLaunchInput();
            urLaunchInput.setRegistrationFunction(RegistrationFunction.SignIn);
            urLaunchInput.setUserRegistrationUIEventListener(this);
            urLaunchInput.setEndPointScreen(RegistrationLaunchMode.DEFAULT);
            urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
            urInterface = new URInterface();
            urInterface.launch(activityLauncher, urLaunchInput);

        } else if (i == R.id.btn_refresh_user) {
            RLog.d(RLog.ONCLICK, "RegistrationSampleActivity : Refresh User ");
            handleRefreshAccessToken();

        } else if (i == R.id.btn_refresh_token) {
            if (RegistrationConfiguration.getInstance().isHsdpFlow()) {
                User user = new User(mContext);
                if (!user.isUserSignIn()) {
                    Toast.makeText(this, "Please login before refreshing access token", Toast.LENGTH_LONG).show();
                } else {
                    mProgressDialog.setMessage("Refreshing...");
                    mProgressDialog.show();
                    user.refreshLoginSession(this);
                }
            }

        } else if (i == R.id.btn_update_gender) {

            User user1 = new User(mContext);
            if (!user1.isUserSignIn()) {
                Toast.makeText(this, "Please login before refreshing access token", Toast.LENGTH_LONG).show();
            } else {
                handleGender();
            }
        } else if (i == R.id.btn_update_date_of_birth) {
            User user = new User(mContext);
            if (!user.isUserSignIn()) {
                Toast.makeText(this, "Please login before updating user", Toast.LENGTH_LONG).show();
            } else {
                handleDoBUpdate(user.getDateOfBirth());
            }
        }
    }

    private void handleGender() {

        mProgressDialog.setMessage("Updating...");
        mProgressDialog.show();
        Gender gender;

        if (mRadioGender.getCheckedRadioButtonId() == R.id.Male) {
            gender = Gender.MALE;
        } else if (mRadioGender.getCheckedRadioButtonId() == R.id.Female) {
            gender = Gender.FEMALE;
        }else {
           // gender = Gender.NONE;
            gender = Gender.MALE;
        }

        final User user1 = new User(mContext);
        user1.updateGender(new UpdateUserDetailsHandler() {
            @Override
            public void onUpdateSuccess() {
                mProgressDialog.hide();
                showToast("onUpdateSuccess");
            }

            @Override
            public void onUpdateFailedWithError(int error) {
                mProgressDialog.hide();
                showToast("onUpdateFailedWithError" + error);
            }
        }, gender);


    }

    private void handleDoBUpdate(Date userDOB) {
        int year, month, day;
        Calendar calendar = new GregorianCalendar();
        if (userDOB != null) {
            calendar.setTime(userDOB);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
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
                            public void onUpdateFailedWithError(int error) {
                                mProgressDialog.hide();
                                showToast("onUpdateFailedWithError" + error);
                            }
                        }, c.getTime());
                    }
                }, year, month, day);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();

    }

    private void initCountrySelection() {
        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();
        String countrySelection = isCountrySelection ? "true" : "false";

    }


    private void handleRefreshAccessToken() {

        final User user = new User(this);
        if (user.isUserSignIn()) {
            user.refreshLoginSession(new RefreshLoginSessionHandler() {
                @Override
                public void onRefreshLoginSessionSuccess() {
                    showToast("Success to refresh access token" + user.getAccessToken());
                }

                @Override
                public void onRefreshLoginSessionFailedWithError(int error) {
                    showToast("Failed to refresh access token");

                }

                @Override
                public void onRefreshLoginSessionInProgress(String message) {
                    showToast(message);
                }
            });
        } else {
            Toast.makeText(this, "Plase login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {
        RLog.d(RLog.EVENT_LISTENERS, "RegistrationSampleActivity : onUserRegistrationComplete");
        Toast.makeText(this,"User is logged in ",Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
        RLog.d(RLog.EVENT_LISTENERS, "RegistrationSampleActivity : onPrivacyPolicyClick");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + getResources().getString(
                com.philips.cdp.registration.R.string.reg_Philips_URL_txt)));
        activity.startActivity(browserIntent);
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
        RLog.d(RLog.EVENT_LISTENERS, "RegistrationSampleActivity : onTermsAndConditionClick");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + getResources().getString(
                com.philips.cdp.registration.R.string.reg_Philips_URL_txt)));
        activity.startActivity(browserIntent);
    }

    @Override
    public void onUserLogoutSuccess() {
        RLog.d(RLog.HSDP, "RegistrationSampleActivity : onUserLogoutSuccess");
    }

    @Override
    public void onUserLogoutFailure() {
        RLog.d(RLog.HSDP, "  RegistrationSampleActivity : onUserLogoutFailure");
    }

    @Override
    public void onUserLogoutSuccessWithInvalidAccessToken() {
        RLog.d(RLog.HSDP, "RegistrationSampleActivity  : onUserLogoutSuccessWithInvalidAccessToken");
        showToast("onUserLogoutSuccessWithInvalidAccessToken ");
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
        RLog.d(RLog.HSDP, "RegistrationSampleActivity Access token: " + mUser.getHsdpAccessToken());
        showToast("Success to refresh hsdp access token");
    }

    @Override
    public void onRefreshLoginSessionFailedWithError(int error) {
        dimissDialog();
        if (error == Integer.parseInt(RegConstants.INVALID_ACCESS_TOKEN_CODE)
                || error == Integer.parseInt(RegConstants.INVALID_REFRESH_TOKEN_CODE)) {
            showToast("Failed to refresh hsdp Invalid access token");
            return;
        }
        showToast("Failed to refresh hsdp access token");
    }

    @Override
    public void onRefreshLoginSessionInProgress(String message) {
        showToast(message);
    }


    public RegistrationContentConfiguration getRegistrationContentConfiguration() {
        String valueForRegistrationTitle = "sample";
        String valueForEmailVerification = "sample";
        String optInTitleText = getResources().getString(R.string.reg_DLS_OptIn_Navigation_Bar_Title);
        String optInQuessionaryText = getResources().getString(R.string.reg_DLS_OptIn_Header_Label);
        String optInDetailDescription = getResources().getString(R.string.reg_DLS_Optin_Body_Line1);
        String optInBannerText = getResources().getString(R.string.reg_Opt_In_Join_Now);
        String optInTitleBarText = getResources().getString(R.string.reg_DLS_OptIn_Navigation_Bar_Title);
        RegistrationContentConfiguration registrationContentConfiguration = new RegistrationContentConfiguration();
        registrationContentConfiguration.setValueForEmailVerification(valueForEmailVerification);
        registrationContentConfiguration.setOptInTitleText(optInTitleText);
        registrationContentConfiguration.setOptInQuessionaryText(optInQuessionaryText);
        registrationContentConfiguration.setOptInDetailDescription(optInDetailDescription);
        registrationContentConfiguration.setOptInBannerText(optInBannerText);
        registrationContentConfiguration.setOptInActionBarText(optInTitleBarText);
        registrationContentConfiguration.enableLastName(true);
        registrationContentConfiguration.enableContinueWithouAccount(true);
        return registrationContentConfiguration;

    }


    private SharedPreferences defaultSharedPreferences;
    ContentColor contentColor;
    ColorRange colorRange;
    NavigationColor navigationColor;
    private AccentRange accentColorRange;
    private int themeResourceId = 0;

    @StyleRes
    int getThemeResourceId(Resources resources, final String packageName, final ColorRange colorRange, final ContentColor contentColor) {
        final String themeName = String.format("Theme.DLS.%s.%s", toCamelCase(colorRange.name()), toCamelCase(contentColor.name()));

        return resources.getIdentifier(themeName, "style", packageName);
    }

    static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    public String getCatalogAppJSONAssetPath() {
        try {
            File f = new File(getCacheDir() + "/catalogapp.json");
            InputStream is = getAssets().open("catalogapp.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
            return f.getPath();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
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
}
