package com.philips.platform.baseapp.screens.privacysettings;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;
import android.widget.Toast;

import com.philips.cdp.registration.consents.URConsentProvider;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.AppStates;
import com.philips.platform.appframework.flowmanager.base.BaseFlowManager;
import com.philips.platform.appframework.flowmanager.base.BaseState;
import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appframework.ui.dialogs.DialogView;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.webview.WebViewStateData;
import com.philips.platform.csw.CswDependencies;
import com.philips.platform.csw.CswInterface;
import com.philips.platform.csw.CswLaunchInput;
import com.philips.platform.csw.permission.MyAccountUIEventListener;
import com.philips.platform.csw.permission.PermissionHelper;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;

import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.ArrayList;
import java.util.List;

public class PrivacySettingsState extends BaseState implements MyAccountUIEventListener {

    private static final long serialVersionUID = 3700147972673050370L;

    public PrivacySettingsState() {
        super(AppStates.PRIVACY_SETTINGS_STATE);
    }

    private static final String PRIVACY_NOTICE = "PrivacyNotice";
    private Context actContext;
    private FragmentLauncher fragmentLauncher;

    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        actContext = fragmentLauncher.getFragmentActivity();
        RestInterface restInterface = getRestClient();
        if (restInterface.isInternetReachable()) {
            CswDependencies dependencies = new CswDependencies(getApplicationContext().getAppInfra());
            PermissionHelper.getInstance().setMyAccountUIEventListener(PrivacySettingsState.this);
            CswInterface cswInterface = getCswInterface();
            UappSettings uappSettings = new UappSettings(actContext);
            cswInterface.init(dependencies, uappSettings);
            cswInterface.launch(fragmentLauncher, buildLaunchInput(true, actContext));
        } else {
            String title = actContext.getString(R.string.MYA_Offline_title);
            String message = actContext.getString(R.string.MYA_Offline_message);
            showDialog(title, message);
        }
    }

    private void showDialog(String title, String message) {
        new DialogView(title, message).showDialog(getFragmentActivity());
    }

    private CswInterface getCswInterface() {
        return new CswInterface();
    }

    private CswLaunchInput buildLaunchInput(boolean addToBackStack, Context context) {
        CswLaunchInput cswLaunchInput = new CswLaunchInput(context, createConsentDefinitions(context));
        cswLaunchInput.addToBackStack(addToBackStack);
        return cswLaunchInput;
    }

    /**
     * <p>
     * Creates a list of ConsentDefinitions</p
     *
     * @param context : can be used to for localized strings <code>context.getString(R.string.consent_definition)</code>
     * @return non-null list (may be empty though)
     */
    @VisibleForTesting
    List<ConsentDefinition> createConsentDefinitions(Context context) {
        AppFrameworkApplication app = (AppFrameworkApplication) context.getApplicationContext();
        final List<ConsentDefinition> consentDefinitions = new ArrayList<>();
        consentDefinitions.add(URConsentProvider.fetchMarketingConsentDefinition());
        consentDefinitions.add(getClickStreamConsentDefinition(context));
        app.getAppInfra().getConsentManager().registerConsentDefinitions(consentDefinitions);
        return consentDefinitions;
    }
    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) getFragmentActivity().getApplication();
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentLauncher.getFragmentActivity();
    }

    private RestInterface getRestClient() {
        return ((AppFrameworkApplication) actContext.getApplicationContext()).getAppInfra().getRestClient();
    }

    private ConsentDefinition getClickStreamConsentDefinition(Context context) {
        List<String> consentTypes = new ArrayList<>();
        consentTypes.add(((AppFrameworkApplication) context.getApplicationContext()).getAppInfra().getTagging().getClickStreamConsentIdentifier());
        consentTypes.add(((AppFrameworkApplication) context.getApplicationContext()).getAppInfra().getCloudLogging().getCloudLoggingConsentIdentifier());
        consentTypes.add(((AppFrameworkApplication) context.getApplicationContext()).getAppInfra().getAbTesting().getAbTestingConsentIdentifier());
        return new ConsentDefinition(R.string.RA_MYA_Click_Stream_Hosting_Consent, R.string.RA_MYA_Consent_Click_Stream_Help_Text,
                consentTypes, 1);
    }

    @Override
    public void init(Context context) {
        AppFrameworkApplication app = (AppFrameworkApplication) context.getApplicationContext();

        createConsentDefinitions(context);
    }

    @Override
    public void updateDataModel() {

    }

    @Override
    public void onPrivacyNoticeClicked() {
        launchWebView(Constants.PRIVACY);
    }

    private void launchWebView(String serviceId) {
        BaseFlowManager targetFlowManager = getApplicationContext().getTargetFlowManager();
        BaseState baseState = null;
        try {
            baseState = targetFlowManager.getNextState(targetFlowManager.getCurrentState(), PRIVACY_NOTICE);
        } catch (NoEventFoundException | NoStateException | NoConditionFoundException | StateIdNotSetException | ConditionIdNotSetException
                e) {
            Toast.makeText(getFragmentActivity(), getFragmentActivity().getString(R.string.RA_something_wrong), Toast.LENGTH_SHORT).show();
        }
        if (null != baseState) {
            WebViewStateData webViewStateData = new WebViewStateData();
            webViewStateData.setServiceId(serviceId);
            webViewStateData.setTitle(actContext.getString(R.string.csw_privacy_notice));
            baseState.setUiStateData(webViewStateData);
            baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
        }
    }
}
