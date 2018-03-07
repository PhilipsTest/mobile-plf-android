/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.demouapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.RegistrationLaunchMode;
import com.philips.cdp.registration.consents.MarketingConsentHandler;
import com.philips.cdp.registration.handlers.LogoutHandler;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.mya.MyaTabConfig;
import com.philips.platform.mya.catk.CatkInputs;
import com.philips.platform.mya.catk.ConsentInteractor;
import com.philips.platform.mya.catk.ConsentsClient;
import com.philips.platform.mya.csw.CswDependencies;
import com.philips.platform.mya.csw.CswInterface;
import com.philips.platform.mya.csw.CswLaunchInput;
import com.philips.platform.mya.csw.dialogs.DialogView;
import com.philips.platform.mya.csw.permission.MyAccountUIEventListener;
import com.philips.platform.mya.csw.permission.PermissionHelper;
import com.philips.platform.mya.demouapp.DemoAppActivity;
import com.philips.platform.mya.demouapp.MyAccountDemoUAppInterface;
import com.philips.platform.mya.demouapp.R;
import com.philips.platform.mya.demouapp.theme.fragments.BaseFragment;
import com.philips.platform.mya.error.MyaError;
import com.philips.platform.mya.interfaces.MyaListener;
import com.philips.platform.mya.launcher.MyaDependencies;
import com.philips.platform.mya.launcher.MyaInterface;
import com.philips.platform.mya.launcher.MyaLaunchInput;
import com.philips.platform.mya.launcher.MyaSettings;
import com.philips.platform.pif.chi.ConsentConfiguration;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.philips.platform.mya.csw.justintime.JustInTimeConsentDependencies.appInfra;


public class LaunchFragment extends BaseFragment implements View.OnClickListener,MyAccountUIEventListener {

    public int checkedId = R.id.radioButton;
    private ArrayList<ConsentConfiguration> consentConfigurationList;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.demo_u_app_layout, container, false);
        setUp(view);
        init();
        return view;
    }

    private void setUp(final View view) {
        initViews(view);
    }

    private void initViews(final View view) {
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                LaunchFragment.this.checkedId = checkedId;
            }
        });

        Button launch_my_account = (Button) view.findViewById(R.id.launch_my_account);
        launch_my_account.setOnClickListener(this);
    }

    @Override
    public int getPageTitle() {
        return 0;
    }

    @Override
    public void onClick(View v) {

        try {
            MyaDependencies uappDependencies = new MyaDependencies(MyAccountDemoUAppInterface.getAppInfra());

            MyaLaunchInput launchInput = new MyaLaunchInput(getContext());
            launchInput.setMyaListener(getMyaListener());
            MyaInterface myaInterface = new MyaInterface();
            myaInterface.init(getUappDependencies(), new MyaSettings(getContext()));
            myaInterface.init(uappDependencies, new MyaSettings(getActivity()));
            if (checkedId == R.id.radioButton) {
                ActivityLauncher activityLauncher = new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_SENSOR,
                        ((DemoAppActivity) getActivity()).getThemeConfig(),
                        ((DemoAppActivity) getActivity()).getThemeResourceId(), null);
                myaInterface.launch(activityLauncher, launchInput);
            } else {
                myaInterface.launch(new FragmentLauncher(getActivity(), R.id.mainContainer, new ActionBarListener() {
                    @Override
                    public void updateActionBar(@StringRes int i, boolean b) {
                        ((DemoAppActivity) getActivity()).setTitle(i);
                    }

                    @Override
                    public void updateActionBar(String s, boolean b) {
                        ((DemoAppActivity) getActivity()).setTitle(s);
                    }
                }), launchInput);
            }
        launchInput.setUserDataInterface(MyAccountDemoUAppInterface.getUserDataInterface());
        MyaTabConfig myaTabConfig = new MyaTabConfig(getString(R.string.mya_config_tab), new TabTestFragment());
        launchInput.setMyaTabConfig(myaTabConfig);
        String[] profileItems = {"MYA_My_details"};
        String[] settingItems = {"MYA_Country", "Mya_Privacy_Settings"};
        launchInput.setProfileMenuList(Arrays.asList(profileItems));
        launchInput.setSettingsMenuList(Arrays.asList(settingItems));
        myaInterface.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, -1, null), launchInput);

        } catch (CatkInputs.InvalidInputException e) {
            e.printStackTrace();
        }

    }

    private MyaListener getMyaListener() {
        return new MyaListener() {
            @Override
            public boolean onSettingsMenuItemSelected(String itemName) {
                if (itemName.equalsIgnoreCase(getString(com.philips.platform.mya.R.string.mya_log_out))) {

                } else if (itemName.equals("Mya_Privacy_Settings")) {
                    RestInterface restInterface = getRestClient();
                    if (restInterface.isInternetReachable()) {
                        CswDependencies dependencies = new CswDependencies(appInfra, consentConfigurationList);
                        PermissionHelper.getInstance().setMyAccountUIEventListener(LaunchFragment.this);
                        CswInterface cswInterface = getCswInterface();
                        UappSettings uappSettings = new UappSettings(getContext());
                        cswInterface.init(dependencies, uappSettings);
                        cswInterface.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_FULL_SENSOR, null, -1, null), buildLaunchInput(true, getContext()));
                        return true;
                    } else {
                        String title = getString(com.philips.platform.mya.R.string.MYA_Offline_title);
                        String message = getString(com.philips.platform.mya.R.string.MYA_Offline_message);
                        showDialog(title, message);
                    }
                }
                return false;
            }

            @Override
            public boolean onProfileMenuItemSelected(String itemName) {
                if (itemName.equals(getString(com.philips.platform.mya.R.string.MYA_My_details)) || itemName.equalsIgnoreCase("MYA_My_details")) {
                    URLaunchInput urLaunchInput = new URLaunchInput();
                    urLaunchInput.enableAddtoBackStack(true);
                    urLaunchInput.setEndPointScreen(RegistrationLaunchMode.USER_DETAILS);
                    URInterface urInterface = new URInterface();
                    urInterface.launch(new ActivityLauncher(ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_FULL_SENSOR, null, -1, null), urLaunchInput);
                    return true;
                }
                return false;
            }

            @Override
            public void onError(MyaError myaError) {
                Toast.makeText(getContext(), myaError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutClicked() {

                User user = MyAccountDemoUAppInterface.getUserObject();
                if (user.isUserSignIn()) {
                    user.logout(new LogoutHandler() {
                        @Override
                        public void onLogoutSuccess() {
                        }

                        @Override
                        public void onLogoutFailure(int responseCode, String message) {
                            new DialogView().showDialog(getActivity(), getString(com.philips.platform.mya.R.string.MYA_Offline_title),getString(com.philips.platform.mya.R.string.MYA_Offline_message));
                        }
                    });
                }

            }
        };
    }

    public MyaInterface getInterface() {
        return new MyaInterface();
    }

    @NonNull
    protected MyaDependencies getUappDependencies() {
        return new MyaDependencies(appInfra);
    }

    private RestInterface getRestClient() {
        return appInfra.getRestClient();
    }

    @Override
    public void onPrivacyNoticeClicked() {

    }

    private CswInterface getCswInterface() {
        return new CswInterface();
    }

    private void init() {
        CatkInputs catkInputs = new CatkInputs.Builder()
                .setContext(getContext())
                .setAppInfraInterface(appInfra)
                .setConsentDefinitions(createCatkDefinitions(getContext()))
                .build();
        ConsentsClient.getInstance().init(catkInputs);
        List<ConsentDefinition> urDefinitions = createUserRegistrationDefinitions(getContext());
        setConsentConfiguration(getContext(), appInfra, catkInputs, urDefinitions);
    }

    /**
     * <p>
     * Creates a list of ConsentDefinitions</p
     *
     * @param context : can be used to for localized strings <code>context.getString(R.string.consent_definition)</code>
     * @return non-null list (may be empty though)
     */
    @VisibleForTesting
    List<ConsentDefinition> createCatkDefinitions(Context context) {
        final List<ConsentDefinition> definitions = new ArrayList<>();
        return definitions;
    }

    private List<ConsentDefinition> createUserRegistrationDefinitions(Context context) {
        final List<ConsentDefinition> definitions = new ArrayList<>();
        return definitions;
    }

    private void setConsentConfiguration(Context context, AppInfraInterface appInfra, CatkInputs catkInputs, List<ConsentDefinition> urDefinitions) {
        consentConfigurationList = new ArrayList<>();
        consentConfigurationList.add(new ConsentConfiguration(catkInputs.getConsentDefinitions(), new ConsentInteractor(ConsentsClient.getInstance())));
        consentConfigurationList.add(new ConsentConfiguration(urDefinitions, new MarketingConsentHandler(context, urDefinitions, appInfra)));
    }

    private void showDialog(String title, String message) {
        new DialogView().showDialog(getActivity(), title, message);
    }

    private CswLaunchInput buildLaunchInput(boolean addToBackStack, Context context) {
        CswLaunchInput cswLaunchInput = new CswLaunchInput(context);
        cswLaunchInput.addToBackStack(addToBackStack);
        return cswLaunchInput;
    }

}
