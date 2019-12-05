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

import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.UIFlow;
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
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.utility.RALog;
import com.philips.platform.baseapp.screens.webview.WebViewStateData;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.UserDataListener;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import static android.content.Context.MODE_PRIVATE;
import static com.philips.platform.baseapp.screens.Optin.MarketingOptin.AB_TEST_OPTIN_IMAGE_KEY;

/**
 * This class contains all initialization & Launching details of UR
 * Setting configuration using App infra.
 * <p>
 * Secret Key and Shared key are same  for TEST and DEV AppState.
 * We do not have revceived China Keys for TEST and DEV AppState.
 * China Keys are available for STAGE only.
 */
public abstract class UserRegistrationState extends BaseState implements UserDataListener, UserRegistrationUIEventListener {

    private static final String TAG = UserRegistrationState.class.getSimpleName();

    protected FragmentLauncher fragmentLauncher;
    private Context applicationContext;

    private static final String PROPOSITION_NAME = "OneBackend";

    private static final String UR_COMPLETE = "URComplete";
    private static final String TERMS_CONDITIONS_CLICK = "TermsAndConditions";
    private static final String HSDP_CONFIGURATION_APPLICATION_NAME = "HSDPConfiguration.ApplicationName";
    private static final String HSDP_CONFIGURATION_SECRET = "HSDPConfiguration.Secret";
    private static final String HSDP_CONFIGURATION_SHARED = "HSDPConfiguration.Shared";
    protected static final String CHINA_CODE = "CN";
    protected static final String DEFAULT = "default";
    private URInterface urInterface;
    public static String AB_TEST_UR_PRIORITY_KEY = "ur_priority";

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
        updateDataModel();
        launchUR();
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentLauncher.getFragmentActivity();
    }

    @Override
    public void init(Context context) {
        this.applicationContext = context;
        initializeUserRegistrationLibrary();
    }


    protected AppInfraInterface getAppInfra() {
        return ((AppFrameworkApplication) applicationContext).getAppInfra();
    }

    @Override
    public void onUserRegistrationComplete(Activity activity) {

        if (null != activity && getUserDataInterface().getUserLoggedInState().ordinal() >= UserLoggedInState.PENDING_HSDP_LOGIN.ordinal()) {
            setUrCompleted();
            getApplicationContext().determineChinaFlow();
            //calling this method again after successful login to update the hybris flow boolean value if user changes the country while logging-in
            getApplicationContext().getIap().isCartVisible();

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

    @Override
    public void updateDataModel() {

    }

    public UserDataInterface getUserDataInterface() {

        return urInterface.getUserDataInterface();
    }


    /**
     * Launch registration fragment
     */
    private void launchUR() {
        RALog.d(TAG, " LaunchUr called ");
        getUserDataInterface().addUserDataInterfaceListener(this);
        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.enableAddtoBackStack(true);
        RegistrationContentConfiguration contentConfiguration = new RegistrationContentConfiguration();
        contentConfiguration.enableContinueWithouAccount(true);
        ABTestClientInterface abTesting = getAppInfra().getAbTesting();
        String testValue = abTesting.getTestValue(AB_TEST_OPTIN_IMAGE_KEY, applicationContext.getString(R.string.Ra_default_value), ABTestClientInterface.UPDATETYPE.APP_UPDATE);
        getAppInfra().getAbTesting().tagEvent("LaunchingRegistration", null);
        if (testValue.equalsIgnoreCase(applicationContext.getString(R.string.RA_abTesting_Sonicare))) {
            contentConfiguration.enableMarketImage(R.drawable.abtesting_sonicare);
            contentConfiguration.setOptInTitleText(applicationContext.getString(R.string.RA_mkt_optin_title_text));
            contentConfiguration.setOptInQuessionaryText(applicationContext.getString(R.string.RA_quessionary_text));
        } else if (testValue.equalsIgnoreCase(applicationContext.getString(R.string.RA_abTesting_Kitchen))) {
            contentConfiguration.enableMarketImage(R.drawable.abtesting_kitchen);
        } else {
            contentConfiguration.enableMarketImage(R.drawable.abtesting_norelco);
        }

        String testValue2 = abTesting.getTestValue(AB_TEST_UR_PRIORITY_KEY, applicationContext.getString(R.string.Ra_registration_value), ABTestClientInterface.UPDATETYPE.APP_UPDATE);
        RALog.d(TAG, "Ra_registration_value testValue2 " + testValue2 + "  val " + applicationContext.getString(R.string.Ra_registration_value));

        if (testValue2.equalsIgnoreCase(applicationContext.getString(R.string.Ra_registration_value))) {
            RALog.d(TAG, "Ra_registration_value Registration ");
            RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);

        } else {
            RALog.d(TAG, " Ra_registration_value SignIn ");
            RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.SignIn);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.SignIn);

        }
        urLaunchInput.setRegistrationContentConfiguration(contentConfiguration);
        urLaunchInput.setUIFlow(UIFlow.FLOW_B);
        URInterface urInterface = new URInterface();
        urInterface.launch(fragmentLauncher, urLaunchInput);
    }

    /**
     * For doing dynamic initialisation Of User registration
     */
    public void initializeUserRegistrationLibrary() {
        RALog.d(TAG, " initializeUserRegistrationLibrary called ");
        URDependancies urDependancies = new URDependancies(getAppInfra());
        URSettings urSettings = new URSettings(applicationContext);
        urInterface = new URInterface();
        urInterface.init(urDependancies, urSettings);
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
        launchWebView(Constants.PRIVACY, getApplicationContext().getString(R.string.USR_PrivacyNoticeText));
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
        launchWebView(Constants.TERMS_AND_CONDITIONS, getApplicationContext().getString(R.string.USR_TermsAndConditionsText));
    }

    @Override
    public void onPersonalConsentClick(Activity activity) {

    }

    public void launchWebView(String serviceId, String title) {
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
            WebViewStateData webViewStateData = new WebViewStateData();
            webViewStateData.setServiceId(serviceId);
            webViewStateData.setTitle(title);
            baseState.setUiStateData(webViewStateData);
            baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
        }
    }

    public String getVersion() {
        return RegistrationHelper.getRegistrationApiVersion();
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) getFragmentActivity().getApplication();
    }

    protected void setUrCompleted() {
        if (getUserDataInterface() != null) {
            getApplicationContext().getAppInfra().getCloudLogging().setHSDPUserUUID(getUserDataInterface().getHSDPUUID());
            getAppInfra().getAbTesting().tagEvent("MarketingOptinstatusSuccess", null);
        }
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Constants.UR_LOGIN_COMPLETED, true);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return getFragmentActivity().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
    }

    @Override
    public void logoutSessionSuccess() {
        RALog.d(TAG, " User Logout success  ");
        getAppInfra().getRestClient().clearCacheResponse();
    }

    @Override
    public void logoutSessionFailed(Error error) {
        RALog.d(TAG, "User logout failed");
    }

    @Override
    public void onRefetchSuccess() {
        //NOP
    }

    @Override
    public void onRefetchFailure(Error error) {
        //NOP
    }

    @Override
    public void refreshSessionSuccess() {
        //NOP
    }

    @Override
    public void refreshSessionFailed(Error error) {
        //NOP
    }

    @Override
    public void forcedLogout() {
        //NOP
    }
}