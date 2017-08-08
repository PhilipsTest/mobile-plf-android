/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.userregistration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.URDependancies;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.cdp.registration.ui.utils.URSettings;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.dataservices.utility.SyncScheduler;
import com.philips.platform.baseapp.screens.termsandconditions.TermsAndPrivacyStateData;
import com.philips.platform.baseapp.screens.utility.AppStateConfiguration;
import com.philips.platform.baseapp.screens.utility.BaseAppUtil;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.referenceapp.PushNotificationManager;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.philips.cdp.registration.configuration.URConfigurationConstants.UR;

/**
 * This class contains all initialization & Launching details of UR
 * Setting configuration using App infra.
 *
 * Secret Key and Shared key are same  for TEST and DEV AppState.
 * We do not have revceived China Keys for TEST and DEV AppState.
 * China Keys are available for STAGE only.
 */
public abstract class UserRegistrationState extends BaseState implements UserRegistrationListener, UserRegistrationUIEventListener {

    private static final String TAG = UserRegistrationState.class.getSimpleName();

    private Context activityContext;
    private User userObject;
    private FragmentLauncher fragmentLauncher;
    private Context applicationContext;

    private static final String PROPOSITION_NAME = "OneBackend";

    private static final String STAGE_SECRET_KEY_CHINA = "a3a3d09e2c74b93a409bc242956a6101bd5ff78cfd21473faa7aa21a8ec8493b66fa905dd4916b8ba4325cb988b442f9c6054089b9b36d09bb1538f985b47b22";
    private static final String STAGE_SECRET_KEY_DEFAULT = "f5b62a26d680e5ae8001522a8e3268f966545a1a14a47ea2040793ea825484cd12fce9c46b43e2c2604cb836db64362a0c8b39eb7b162b8b3e83740143337eda";
    private static final String STAGE_SHARED_KEY_CHINA = "6036461d-0914-4afe-9e6e-eefe27fb529a";
    private static final String STAGE_SHARED_KEY_DEFAULT = "f52cd90d-c955-43e1-8380-999e03d0d4c0";

    private static final String TEST_SECRET_KEY_DEFAULT = "fef56143b07f748441862bcc395606bac36a8120279787740d173ebf4b7c31be125ca4478aae2265881ffb97cbe08d4765646edcad8c339a024a16104e25b60d";
    private static final String TEST_SHARED_KEY_DEFAULT = "a76448bf-b2d9-4a88-b435-8135f5b3d0b0";

    private static final String DEVELOPMENT_SECRET_KEY_DEFAULT = TEST_SECRET_KEY_DEFAULT;
    private static final String DEVELOPMENT_SHARED_KEY_DEFAULT = TEST_SHARED_KEY_DEFAULT;
    private static final String UR_COMPLETE = "URComplete";
    private static final String TERMS_CONDITIONS_CLICK = "TermsAndConditions";
    private static final String HSDP_CONFIGURATION_APPLICATION_NAME = "HSDPConfiguration.ApplicationName";
    private static final String HSDP_CONFIGURATION_SECRET = "HSDPConfiguration.Secret";
    private static final String HSDP_CONFIGURATION_SHARED = "HSDPConfiguration.Shared";
    private static final String CHINA_CODE = "CN";
    private static final String DEFAULT = "default";
    private String appState;

    /**
     * AppFlowState constructor
     */
    public UserRegistrationState(String stateID) {
        super(stateID);
    }

    /**
     * BaseState overridden methods
     *
     * @param uiLauncher requires the UiLauncher object
     */
    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        activityContext = getFragmentActivity();
        updateDataModel();
        launchUR();
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentLauncher.getFragmentActivity();
    }

    @Override
    public void init(Context context) {
        this.applicationContext = context;
        appState = ((AppFrameworkApplication) context.getApplicationContext()).getAppState();
        initHSDP(getConfiguration());

        initializeUserRegistrationLibrary();
    }

    private void initHSDP(AppStateConfiguration configuration) {
        AppInfraInterface appInfra = ((AppFrameworkApplication) applicationContext).getAppInfra();
        AppConfigurationInterface appConfigurationInterface = appInfra.getConfigInterface();

        AppConfigurationInterface.AppConfigurationError configError = new
                AppConfigurationInterface.AppConfigurationError();

        switch (configuration){
            case STAGING:
                setStageConfig(appConfigurationInterface, configError);
                break;

            case DEVELOPMENT:
                setDevConfig(appConfigurationInterface, configError);
                break;

            case TEST:
                setTestConfig(appConfigurationInterface, configError);
                break;

            default:
                setStageConfig(appConfigurationInterface, configError);
        }
    }

    private void setStageConfig(AppConfigurationInterface appConfigurationInterface,
                                AppConfigurationInterface.AppConfigurationError configError) {
        Map<String, String> hsdpAppNames = new HashMap<>();
        hsdpAppNames.put(CHINA_CODE, PROPOSITION_NAME);
        hsdpAppNames.put(DEFAULT, PROPOSITION_NAME);

        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_APPLICATION_NAME,
                UR, hsdpAppNames, configError);

        Map<String, String> hsdpSecrets = new HashMap<>();
        hsdpSecrets.put(CHINA_CODE, STAGE_SECRET_KEY_CHINA);
        hsdpSecrets.put(DEFAULT, STAGE_SECRET_KEY_DEFAULT);
        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_SECRET,
                UR, hsdpSecrets, configError);

        Map<String, String> hsdpSharedIds = new HashMap<>();
        hsdpSharedIds.put(CHINA_CODE, STAGE_SHARED_KEY_CHINA);
        hsdpSharedIds.put(DEFAULT, STAGE_SHARED_KEY_DEFAULT);

        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_SHARED,
                UR, hsdpSharedIds, configError);
    }

    private void setTestConfig(AppConfigurationInterface appConfigurationInterface,
                               AppConfigurationInterface.AppConfigurationError configError) {
        Map<String, String> hsdpAppNames = new HashMap<>();
        hsdpAppNames.put(DEFAULT, PROPOSITION_NAME);

        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_APPLICATION_NAME,
                UR, hsdpAppNames, configError);

        Map<String, String> hsdpSecrets = new HashMap<>();
        hsdpSecrets.put(DEFAULT, TEST_SECRET_KEY_DEFAULT);
        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_SECRET,
                UR, hsdpSecrets, configError);

        Map<String, String> hsdpSharedIds = new HashMap<>();
        hsdpSharedIds.put(DEFAULT, TEST_SHARED_KEY_DEFAULT);

        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_SHARED,
                UR, hsdpSharedIds, configError);
    }

    private void setDevConfig(AppConfigurationInterface appConfigurationInterface,
                              AppConfigurationInterface.AppConfigurationError configError) {
        Map<String, String> hsdpAppNames = new HashMap<>();
        hsdpAppNames.put(DEFAULT, PROPOSITION_NAME);

        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_APPLICATION_NAME,
                UR, hsdpAppNames, configError);

        Map<String, String> hsdpSecrets = new HashMap<>();
        hsdpSecrets.put(DEFAULT, DEVELOPMENT_SECRET_KEY_DEFAULT);
        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_SECRET,
                UR, hsdpSecrets, configError);

        Map<String, String> hsdpSharedIds = new HashMap<>();
        hsdpSharedIds.put(DEFAULT, DEVELOPMENT_SHARED_KEY_DEFAULT);

        appConfigurationInterface.setPropertyForKey(HSDP_CONFIGURATION_SHARED,
                UR, hsdpSharedIds, configError);
    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {
        setUrCompleted();
        if (null != activity) {
            getApplicationContext().determineChinaFlow();

            //Register GCM token with data services on login success
            if (BaseAppUtil.isDSPollingEnabled(activity.getApplicationContext())) {
                RALog.d(PushNotificationManager.TAG, "Polling is enabled");
                SyncScheduler.getInstance().scheduleSync();

            } else {
                RALog.d(PushNotificationManager.TAG, "Push notification is enabled");
                ((AppFrameworkApplication) activity.getApplicationContext()).getDataServiceState().registerForReceivingPayload();
                ((AppFrameworkApplication) activity.getApplicationContext()).getDataServiceState().registerDSForRegisteringToken();
                PushNotificationManager.getInstance().startPushNotificationRegistration(activity.getApplicationContext());
            }
            BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
            BaseState baseState = null;
            try {
                baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), UR_COMPLETE);
            } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                    e) {
                RALog.d(TAG, e.getMessage());
                Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
            }
            if (null != baseState) {
                getFragmentActivity().finish();
                baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
            }
        }
    }

    @Override
    public void updateDataModel() {

    }

    public User getUserObject(Context context) {
        userObject = new User(context);
        return userObject;
    }

    /**
     * Registering for UIStateListener callbacks
     *
     * @param uiStateListener
     */
    public void registerUIStateListener(URStateListener uiStateListener) {
    }


    /**
     * Launch registration fragment
     */
    private void launchUR() {
        RALog.d(TAG," LaunchUr called ");
        userObject = new User(getApplicationContext());
        userObject.registerUserRegistrationListener(this);
        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.enableAddtoBackStack(true);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        URInterface urInterface = new URInterface();
        urInterface.launch(fragmentLauncher, urLaunchInput);
    }

    /**
     * For doing dynamic initialisation Of User registration
     */
    public void initializeUserRegistrationLibrary() {
        RALog.d(TAG," initializeUserRegistrationLibrary called ");
        URDependancies urDependancies = new URDependancies(((AppFrameworkApplication)applicationContext).getAppInfra());
        URSettings urSettings = new URSettings(applicationContext);
        URInterface urInterface = new URInterface();
        urInterface.init(urDependancies, urSettings);
    }

    public void unregisterUserRegistrationListener() {
        RALog.d(TAG," unregisterUserRegistrationListener called ");
        userObject.unRegisterUserRegistrationListener(this);
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        BaseState baseState = null;
        try {
            baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), TERMS_CONDITIONS_CLICK);
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            RALog.d(TAG, e.getMessage());
            Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
        }
        if (null != baseState) {
            TermsAndPrivacyStateData termsAndPrivacyStateData=new TermsAndPrivacyStateData();
            termsAndPrivacyStateData.setTermsAndPrivacyEnum(TermsAndPrivacyStateData.TermsAndPrivacyEnum.PRIVACY_CLICKED);
            baseState.setUiStateData(termsAndPrivacyStateData);
            baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
        }

    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        BaseState baseState = null;
        try {
            baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), TERMS_CONDITIONS_CLICK);
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            RALog.d(TAG, e.getMessage());
            Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
        }
        if (null != baseState) {
            TermsAndPrivacyStateData termsAndPrivacyStateData=new TermsAndPrivacyStateData();
            termsAndPrivacyStateData.setTermsAndPrivacyEnum(TermsAndPrivacyStateData.TermsAndPrivacyEnum.TERMS_CLICKED);
            baseState.setUiStateData(termsAndPrivacyStateData);
            baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
        }
    }

    @Override
    public void onUserLogoutSuccess() {
        RALog.d(TAG," User Logout success  ");

    }

    @Override
    public void onUserLogoutFailure() {

        RALog.d(TAG, "User logout failed");
    }

    @Override
    public void onUserLogoutSuccessWithInvalidAccessToken() {
    }

    public String getVersion() {
        return RegistrationHelper.getRegistrationApiVersion();
    }

    public String getComponentID(Context c) {
        return c.getResources().getString(R.string.RA_COCO_UR);
    }

    public AppStateConfiguration getConfiguration() {
        if (appState.equalsIgnoreCase(AppStateConfiguration.STAGING.getValue()))
            return AppStateConfiguration.STAGING;
        else if (appState.equalsIgnoreCase(AppStateConfiguration.DEVELOPMENT.getValue()))
            return AppStateConfiguration.DEVELOPMENT;
        else if (appState.equalsIgnoreCase(AppStateConfiguration.TEST.getValue()))
            return AppStateConfiguration.TEST;

        return AppStateConfiguration.STAGING;
    }

    private AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) getFragmentActivity().getApplication();
    }

    protected void setUrCompleted() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.UR_LOGIN_COMPLETED, true);
        editor.commit();
    }

    private SharedPreferences getSharedPreferences() {
        return getFragmentActivity().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
    }
}