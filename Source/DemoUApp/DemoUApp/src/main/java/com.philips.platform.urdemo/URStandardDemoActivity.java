
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.platform.urdemo;

import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.janrain.android.Jump;
import com.janrain.android.engage.session.JRSession;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.configuration.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.hsdp.HsdpUser;
import com.philips.cdp.registration.listener.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.utils.*;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.thememanager.*;
import com.philips.platform.uid.utils.UIDLocaleHelper;
import com.philips.platform.urdemo.themesettings.*;
import com.philips.platform.urdemolibrary.R;

import java.io.*;
import java.util.*;

import static android.view.View.*;

public class URStandardDemoActivity extends Activity implements OnClickListener,
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
//        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        UIDHelper.init(new ThemeConfiguration(this, ColorRange.GROUP_BLUE, ContentColor.ULTRA_LIGHT, NavigationColor.ULTRA_LIGHT, AccentRange.AQUA));
//        initTheme();
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

        Switch mCountrySelectionSwitch = (Switch) findViewById(R.id.county_selection_switch);
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

        mCountrySelectionSwitch = (Switch) findViewById(R.id.county_selection_switch);
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
                    Toast.makeText(getApplicationContext(), "choice: Stagging",
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
                    RLog.i("Restored teest",""+restoredText);

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
        ActivityLauncher activityLauncher;
        URInterface urInterface;
        initCountrySelection();

        int i = v.getId();
        if (i == R.id.btn_registration_with_account) {
            RLog.d(RLog.ONCLICK, "RegistrationSampleActivity : Registration");
            //  RegistrationSampleApplication.getInstance().getAppInfra().getTagging().setPreviousPage("demoapp:home");
            urLaunchInput = new URLaunchInput();
            urLaunchInput.setEndPointScreen(RegistrationLaunchMode.ACCOUNT_SETTINGS);
            urLaunchInput.setAccountSettings(true);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
            urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
            urLaunchInput.setUIFlow(UIFlow.FLOW_B);
            urLaunchInput.setUserRegistrationUIEventListener(this);
            activityLauncher = new ActivityLauncher(ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, 0);

//            activityLauncher = new ActivityLauncher(ActivityLauncher.
//                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, getThemeConfig(), themeResourceId, null);
            urInterface = new URInterface();
            urInterface.launch(activityLauncher, urLaunchInput);
            final UIFlow abTestingUIFlow = RegUtility.getUiFlow();
            switch (abTestingUIFlow) {
                case FLOW_A:
                    Toast.makeText(mContext, "UI Flow Type A", Toast.LENGTH_LONG).show();
                    RLog.d(RLog.AB_TESTING, "UI Flow Type A");
                    break;
                case FLOW_B:
                    Toast.makeText(mContext, "UI Flow Type B", Toast.LENGTH_LONG).show();
                    RLog.d(RLog.AB_TESTING, "UI Flow Type B");
                    break;
                case FLOW_C:
                    Toast.makeText(mContext, "UI Flow Type C", Toast.LENGTH_LONG).show();
                    RLog.d(RLog.AB_TESTING, "UI Flow Type C");
                    break;
                default:
                    break;
            }

        } else if (i == R.id.btn_marketing_opt_in) {
            RLog.d(RLog.ONCLICK, "RegistrationSampleActivity : Registration");
            urLaunchInput = new URLaunchInput();
            urLaunchInput.setEndPointScreen(RegistrationLaunchMode.MARKETING_OPT);
            urLaunchInput.setAccountSettings(false);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
            urLaunchInput.setRegistrationContentConfiguration(getRegistrationContentConfiguration());
            urLaunchInput.setUIFlow(UIFlow.FLOW_C);
            urLaunchInput.setUserRegistrationUIEventListener(this);
            activityLauncher = new ActivityLauncher(ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, 0);

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
                case FLOW_C:
                    Toast.makeText(mContext, "UI Flow Type C", Toast.LENGTH_LONG).show();
                    RLog.d(RLog.AB_TESTING, "UI Flow Type C");
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
            urLaunchInput.setAccountSettings(false);
            activityLauncher = new ActivityLauncher(ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, 0);
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
//            Intent intent = new Intent(this, ThemeSettingsActivity.class);
//            startActivity(intent);
//
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
        } else {
            gender = Gender.FEMALE;
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
                    System.out.println("Message " + message);
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
        String valueForRegistration = "sample";
        String valueForEmailVerification = "sample";
        String optInTitleText = getResources().getString(R.string.reg_Opt_In_Be_The_First);
        String optInQuessionaryText = getResources().getString(R.string.reg_Opt_In_What_Are_You_Going_To_Get);
        String optInDetailDescription = getResources().getString(R.string.reg_Opt_In_Special_Offers);
        String optInBannerText = getResources().getString(R.string.reg_Opt_In_Join_Now);
        String optInTitleBarText = getResources().getString(R.string.reg_RegCreateAccount_NavTitle);
        RegistrationContentConfiguration registrationContentConfiguration = new RegistrationContentConfiguration();
        registrationContentConfiguration.setValueForRegistration(valueForRegistration);
        registrationContentConfiguration.setValueForEmailVerification(valueForEmailVerification);
        registrationContentConfiguration.setOptInTitleText(optInTitleText);
        registrationContentConfiguration.setOptInQuessionaryText(optInQuessionaryText);
        registrationContentConfiguration.setOptInDetailDescription(optInDetailDescription);
        registrationContentConfiguration.setOptInBannerText(optInBannerText);
        registrationContentConfiguration.setOptInActionBarText(optInTitleBarText);
        return registrationContentConfiguration;

    }


    private SharedPreferences defaultSharedPreferences;
    ContentColor contentColor;
    ColorRange colorRange;
    NavigationColor navigationColor;
    private AccentRange accentColorRange;
    private int themeResourceId = 0;

    private void initTheme() {
        final ThemeConfiguration themeConfig = getThemeConfig();
        themeResourceId = getThemeResourceId(getResources(), getPackageName(), colorRange, contentColor);
        themeConfig.add(navigationColor);
        themeConfig.add(accentColorRange);
        setTheme(themeResourceId);
        UIDLocaleHelper.getInstance().setFilePath(getCatalogAppJSONAssetPath());

        UIDHelper.init(themeConfig);
    }

    public ThemeConfiguration getThemeConfig() {
        final ThemeHelper themeHelper = new ThemeHelper(defaultSharedPreferences);
        colorRange = themeHelper.initColorRange();
        navigationColor = themeHelper.initNavigationRange();
        contentColor = themeHelper.initContentTonalRange();
        accentColorRange = themeHelper.initAccentRange();
        return new ThemeConfiguration(this, colorRange, navigationColor, contentColor, accentColorRange);
    }

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
            Log.e(ThemeSettingsActivity.class.getName(), e.getMessage());
        } catch (IOException e) {
            Log.e(ThemeSettingsActivity.class.getName(), e.getMessage());
        }
        return null;
    }
}
