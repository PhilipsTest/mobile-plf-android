package com.philips.platform.baseapp.screens.myaccount;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.philips.cdp.registration.consents.MarketingConsentHandler;
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
import com.philips.platform.appframework.homescreen.HamburgerActivity;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.screens.utility.Constants;
import com.philips.platform.baseapp.screens.webview.WebViewStateData;
import com.philips.platform.mya.MyaHelper;
import com.philips.platform.mya.MyaTabConfig;
import com.philips.platform.mya.catk.CatkInputs;
import com.philips.platform.mya.catk.ConsentInteractor;
import com.philips.platform.mya.catk.ConsentsClient;
import com.philips.platform.mya.csw.permission.MyAccountUIEventListener;
import com.philips.platform.mya.error.MyaError;
import com.philips.platform.mya.interfaces.MyaListener;
import com.philips.platform.mya.launcher.MyaDependencies;
import com.philips.platform.mya.launcher.MyaInterface;
import com.philips.platform.mya.launcher.MyaLaunchInput;
import com.philips.platform.mya.launcher.MyaSettings;
import com.philips.platform.myaplugin.uappadaptor.DataInterface;
import com.philips.platform.myaplugin.uappadaptor.DataModelType;
import com.philips.platform.myaplugin.user.UserDataModelProvider;
import com.philips.platform.pif.chi.ConsentConfiguration;
import com.philips.platform.pif.chi.ConsentDefinitionRegistry;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.launcher.UiLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MyAccountState extends BaseState implements MyAccountUIEventListener {

    public MyAccountState() {
        super(AppStates.MY_ACCOUNT);
    }

    private Context actContext;
    private FragmentLauncher fragmentLauncher;
    private static final String PRIVACY_NOTICE = "PrivacyNotice";

    @Override
    public void navigate(UiLauncher uiLauncher) {
        fragmentLauncher = (FragmentLauncher) uiLauncher;
        actContext = fragmentLauncher.getFragmentActivity();

        ((AbstractAppFrameworkBaseActivity) actContext).handleFragmentBackStack(null, "", getUiStateData().getFragmentLaunchState());

        MyaLaunchInput launchInput = new MyaLaunchInput(actContext, new MyaListener() {
            @Override
            public boolean onClickMyaItem(String itemName) {
                if (itemName.equalsIgnoreCase(actContext.getString(com.philips.platform.mya.R.string.mya_log_out)) && actContext instanceof HamburgerActivity) {
                    ((HamburgerActivity) actContext).onLogoutResultSuccess();
                }
                return false;
            }

            @Override
            public DataInterface getDataInterface(DataModelType modelType) {
                return new UserDataModelProvider(actContext);

            }

            @Override
            public void onError(MyaError myaError) {

            }
        });
        launchInput.addToBackStack(true);
        launchInput.setMyAccountUIEventListener(this);

        MyaTabConfig myaTabConfig = new MyaTabConfig("config_tab",new TabTestFragment());
        MyaInterface myaInterface = getInterface();
        launchInput.setMyaTabConfig(myaTabConfig);
        myaInterface.init(getUappDependencies(actContext), new MyaSettings(actContext.getApplicationContext()));
        myaInterface.launch(fragmentLauncher, launchInput);
    }

    private Locale getCompleteLocale(AppFrameworkApplication frameworkApplication) {
        Locale locale = Locale.US;
        if (frameworkApplication != null && frameworkApplication.getAppInfra().getInternationalization() != null && frameworkApplication.getAppInfra().getInternationalization().getUILocaleString() != null) {
            String[] localeComponents = frameworkApplication.getAppInfra().getInternationalization().getBCP47UILocale().split("-");
            if (localeComponents.length == 2) {
                locale = new Locale(localeComponents[0], localeComponents[1]);
            }
        }
        return locale;
    }

    /**
     * <p>
     * Creates a list of ConsentDefinitions</p
     *
     * @param context       : can be used to for localized strings <code>context.getString(R.string.consent_definition)</code>
     * @param currentLocale : locale of the strings
     * @return non-null list (may be empty though)
     */
    @VisibleForTesting
    List<ConsentDefinition> createCatkDefinitions(Context context, Locale currentLocale) {
        final List<ConsentDefinition> definitions = new ArrayList<>();
        ConsentDefinition momentConsentDefinition = new ConsentDefinition(context.getString(R.string.RA_MYA_Consent_Moment_Text), context.getString(R.string.RA_MYA_Consent_Moment_Help),
                Collections.singletonList("moment"), 1, currentLocale);
        ConsentDefinitionRegistry.add(momentConsentDefinition);
        definitions.add(momentConsentDefinition);
        ConsentDefinition coachingConsentDefinition = new ConsentDefinition(context.getString(R.string.RA_MYA_Consent_Coaching_Text), context.getString(R.string.RA_MYA_Consent_Coaching_Help),
                Collections.singletonList("coaching"), 1, currentLocale);
        ConsentDefinitionRegistry.add(coachingConsentDefinition);
        definitions.add(coachingConsentDefinition);
        ConsentDefinition binaryConsentDefinition = new ConsentDefinition(context.getString(R.string.RA_MYA_Consent_Binary_Text), context.getString(R.string.RA_MYA_Consent_Binary_Help),
                Collections.singletonList("binary"), 1, currentLocale);
        ConsentDefinitionRegistry.add(binaryConsentDefinition);
        definitions.add(binaryConsentDefinition);
        ConsentDefinition clickStreamConsentDefinition = new ConsentDefinition(context.getString(R.string.RA_MYA_Consent_Clickstream_Text), context.getString(R.string.RA_MYA_Consent_Clickstream_Help),
                Collections.singletonList("clickstream"), 1, currentLocale);
        definitions.add(clickStreamConsentDefinition);
        ConsentDefinitionRegistry.add(clickStreamConsentDefinition);
        ConsentDefinition researchConsentDefinition = new ConsentDefinition(context.getString(R.string.RA_MYA_Consent_ResearchAnalytics_Text), context.getString(R.string.RA_MYA_Consent_ResearchAnalytics_Help),
                Arrays.asList("research", "analytics"), 1, currentLocale);
        ConsentDefinitionRegistry.add(researchConsentDefinition);
        definitions.add(researchConsentDefinition);
        return definitions;
    }

    private List<ConsentDefinition> createUserRegistrationDefinitions(Context context, Locale currentLocale) {
        final List<ConsentDefinition> definitions = new ArrayList<>();
        definitions.add(new ConsentDefinition(context.getString(R.string.RA_Setting_Philips_Promo_Title), context
                .getString(R.string.RA_MYA_Marketing_Help_Text), Collections.singletonList("marketing"), 1, currentLocale));
        return definitions;
    }

    @Override
    public void init(Context context) {
        AppFrameworkApplication app = (AppFrameworkApplication) context.getApplicationContext();

        Locale currentLocale = getCompleteLocale(app);
        CatkInputs catkInputs = new CatkInputs.Builder()
                .setContext(context)
                .setAppInfraInterface(app.getAppInfra())
                .setConsentDefinitions(createCatkDefinitions(context, currentLocale))
                .build();
        ConsentsClient.getInstance().init(catkInputs);

        List<ConsentDefinition> urDefinitions = Collections.singletonList(URConsentProvider.fetchMarketingConsentDefinition(context, currentLocale));

        List<ConsentConfiguration> consentHandlerMappings = new ArrayList<>();
        consentHandlerMappings.add(new ConsentConfiguration(catkInputs.getConsentDefinitions(), new ConsentInteractor(ConsentsClient.getInstance())));
        consentHandlerMappings.add(new ConsentConfiguration(urDefinitions, new MarketingConsentHandler(context, urDefinitions)));
        MyaHelper.getInstance().setConfigurations(consentHandlerMappings);
    }

    @Override
    public void updateDataModel() {

    }

    public MyaInterface getInterface() {
        return new MyaInterface();
    }

    @NonNull
    protected MyaDependencies getUappDependencies(Context actContext) {
        AppInfraInterface appInfra = ((AppFrameworkApplication) actContext.getApplicationContext()).getAppInfra();
        return new MyaDependencies(appInfra, MyaHelper.getInstance().getConsentConfigurationList());
    }

    @Override
    public void onPrivacyNoticeClicked() {
        launchWebView(Constants.PRIVACY);
    }

    public void launchWebView(String serviceId) {
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
            baseState.setUiStateData(webViewStateData);
            baseState.navigate(new FragmentLauncher(getFragmentActivity(), R.id.frame_container, (ActionBarListener) getFragmentActivity()));
        }
    }

    protected AppFrameworkApplication getApplicationContext() {
        return (AppFrameworkApplication) getFragmentActivity().getApplication();
    }

    public FragmentActivity getFragmentActivity() {
        return fragmentLauncher.getFragmentActivity();
    }
}
