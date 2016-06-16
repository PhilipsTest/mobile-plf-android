package com.philips.cdp.prodreg.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.philips.cdp.localematch.PILLocaleManager;
import com.philips.cdp.prodreg.R;
import com.philips.cdp.prodreg.Util;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.configuration.Flow;
import com.philips.cdp.registration.configuration.JanRainConfiguration;
import com.philips.cdp.registration.configuration.PILConfiguration;
import com.philips.cdp.registration.configuration.RegistrationClientId;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.configuration.RegistrationDynamicConfiguration;
import com.philips.cdp.registration.configuration.SigninProviders;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RegistrationLaunchHelper;
import com.philips.cdp.tagging.Tagging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class LaunchFragment extends Fragment implements View.OnClickListener {

    String configurationType[] = {"Evaluation", "Testing", "Development", "Staging", "Production"};
    int count = 0;
    List<String> list = Arrays.asList(configurationType);
    private String TAG = getClass().toString();
    private TextView txt_title, configurationTextView;
    private Spinner spinner;
    private SharedPreferences sharedPreferences;
    private Button user_registration_button, pr_button, reg_list_button;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        setUp(view);
        return view;
    }

    private void setUp(final View view) {
        context = getActivity();
        final String PRODUCT_REGISTRATION = "prod_demo";
        sharedPreferences = context.getSharedPreferences(PRODUCT_REGISTRATION, Context.MODE_PRIVATE);
        initViews(view);
        setOnClickListeners();
        setSpinnerAdaptor();
        final int position = list.indexOf(sharedPreferences.getString("reg_env", "Evaluation"));
        setSpinnerSelection(position);
        spinner.setOnItemSelectedListener(getSpinnerListener());
    }

    @NonNull
    private AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                final String configuration = adapter.getItemAtPosition(position).toString();

                if (count > 0) {
                    User user = new User(context);
                    user.logout(null);
                    Log.d(TAG, "Before Configuration" + configuration);
                    if (configuration.equalsIgnoreCase("Development")) {
                        initialiseUserRegistration("Development");
                    } else if (configuration.equalsIgnoreCase("Testing")) {
                        initialiseUserRegistration("Testing");
                    } else if (configuration.equalsIgnoreCase("Evaluation")) {
                        initialiseUserRegistration("Evaluation");
                    } else if (configuration.equalsIgnoreCase("Staging")) {
                        initialiseUserRegistration("Staging");
                    } else if (configuration.equalsIgnoreCase("Production")) {
                        initialiseUserRegistration("Production");
                    }
                    Log.d(TAG, "After Configuration" + configuration);
                    configurationTextView.setText(configuration);
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        };
    }

    private void setSpinnerSelection(final int position) {
        if (position >= 0) {
            spinner.setSelection(position);
            configurationTextView.setText(configurationType[position]);
        } else {
            configurationTextView.setText(configurationType[0]);
        }
    }

    private void setSpinnerAdaptor() {
        final ArrayAdapter<String> configType = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, configurationType);
        configType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(configType);
    }

    public void setOnClickListeners() {
        user_registration_button.setOnClickListener(this);
        pr_button.setOnClickListener(this);
        reg_list_button.setOnClickListener(this);
    }

    private void initViews(final View view) {
        spinner = (Spinner) view.findViewById(R.id.spinner);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        user_registration_button = (Button) view.findViewById(R.id.btn_user_registration);
        pr_button = (Button) view.findViewById(R.id.btn_product_registration);
        reg_list_button = (Button) view.findViewById(R.id.btn_register_list);
        configurationTextView = (TextView) view.findViewById(R.id.configuration);
    }

    private void hideSpinnerLayout(final Spinner spinner) {
        spinner.setVisibility(View.GONE);
        txt_title.setVisibility(View.GONE);
    }

    private void initialiseUserRegistration(final String development) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reg_env", development);
        editor.commit();
        final JanRainConfiguration janRainConfiguration = new JanRainConfiguration();
        final RegistrationClientId registrationClientId = new RegistrationClientId();
        registrationClientId.setDevelopmentId("ad7nn99y2mv5berw5jxewzagazafbyhu");
        registrationClientId.setEvaluationId("4r36zdbeycca933nufcknn2hnpsz6gxu");
        registrationClientId.setProductionId("mz6tg5rqrg4hjj3wfxfd92kjapsrdhy3");
        registrationClientId.setStagingId("f2stykcygm7enbwfw2u9fbg6h6syb8yd");
        registrationClientId.setTestingId("xru56jcnu3rpf8q7cgnkr7xtf9sh8pp7");
        janRainConfiguration.setClientIds(registrationClientId);
        RegistrationDynamicConfiguration.getInstance().setJanRainConfiguration(janRainConfiguration);

        PILConfiguration pilConfiguration = new PILConfiguration();
        pilConfiguration.setMicrositeId("77000");
        pilConfiguration.setRegistrationEnvironment(development);
        RegistrationDynamicConfiguration.getInstance().setPilConfiguration(pilConfiguration);
        Flow flow = new Flow();
        flow.setEmailVerificationRequired(true);
        flow.setTermsAndConditionsAcceptanceRequired(true);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("NL", "16");
        hashMap.put("GB", "16");
        hashMap.put("default", "16");
        flow.setMinAgeLimit(hashMap);
        RegistrationDynamicConfiguration.getInstance().setFlow(flow);
        SigninProviders signinProviders = new SigninProviders();
        HashMap<String, ArrayList<String>> providers = new HashMap<>();
        ArrayList<String> defaultSignInProviders = new ArrayList<>();
        defaultSignInProviders.add("facebook");
        defaultSignInProviders.add("googleplus");
        providers.put("default", defaultSignInProviders);

        signinProviders.setProviders(providers);
        RegistrationDynamicConfiguration.getInstance().setSignInProviders(signinProviders);

        initRegistration();
        hideSpinnerLayout(spinner);
    }

    private void initRegistration() {

        Tagging.enableAppTagging(true);
        Tagging.setTrackingIdentifier("integratingApplicationAppsId");
        Tagging.setLaunchingPageName("demo_app_home");
        RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);

        String languageCode = Locale.getDefault().getLanguage();
        String countryCode = Locale.getDefault().getCountry();

        PILLocaleManager localeManager = new PILLocaleManager(context);
        localeManager.setInputLocale(languageCode, countryCode);

        RegistrationHelper.getInstance().initializeUserRegistration(context);
        Tagging.init(context, "Product Registration");
    }

    @Override
    public void onClick(View v) {
        final String env = sharedPreferences.getString("reg_env", "Evaluation");

        switch (v.getId()) {
            case R.id.btn_user_registration:
                initialiseUserRegistration(env);
                RegistrationLaunchHelper.launchRegistrationActivityWithAccountSettings(context);
                Util.navigateFromUserRegistration();
                break;
            case R.id.btn_product_registration:
                initialiseUserRegistration(env);
                showFragment(new ManualRegistrationFragment(), ManualRegistrationFragment.TAG);
                break;
            case R.id.btn_register_list:
                initialiseUserRegistration(env);
                showFragment(new ProductListFragment(), ProductListFragment.TAG);
                break;
            default:
                break;
        }
    }

    private void showFragment(final Fragment fragment, final String TAG) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parent_layout, fragment,
                TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }
}
