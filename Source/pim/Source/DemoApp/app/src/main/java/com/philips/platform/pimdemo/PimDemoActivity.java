package com.philips.platform.pimdemo;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;


import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.utils.UIDLangPackActivity;
import com.philips.platform.uid.view.widget.Button;
import com.pim.demouapp.PIMDemoUAppDependencies;
import com.pim.demouapp.PIMDemoUAppInterface;
import com.pim.demouapp.PIMDemoUAppLaunchInput;
import com.pim.demouapp.PIMDemoUAppLaunchInput.RegistrationLib;
import com.pim.demouapp.PIMDemoUAppSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PimDemoActivity extends UIDLangPackActivity {

    private Spinner selectLibrary, spinnerCountrySelection,spinnerLaunchFLow;
    private CheckBox enableChuck;
    private Switch enableMigrationSwitch;
    private AppInfraInterface appInfraInterface;
    private Context mContext;
    private PIMDemoUAppInterface pimDemoUAppInterface;
    private String TAG = PimDemoActivity.class.getSimpleName();
    private Map<String, String> countryMap = new LinkedHashMap<>();
    private List<String> countryList = new ArrayList<>();
    private String homeCountryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo);

        initCountryMap();
        ininitalizeView();
        updateLibraryListData();
        updateCountryListData();
        initLaunchFlowData();

        mContext = getApplicationContext();
        appInfraInterface = ((PimDemoApplication) mContext).getAppInfra();
        pimDemoUAppInterface = new PIMDemoUAppInterface();
        homeCountryCode = appInfraInterface.getServiceDiscovery().getHomeCountry();
        Log.d(TAG, "onCreate :: homeCountry -> " + homeCountryCode);
        initPIMDemoUApp(homeCountryCode);
        if (homeCountryCode != null && getCountryName(homeCountryCode) != null)
            spinnerCountrySelection.setSelection(((PIMSpinnerAdapter) spinnerCountrySelection.getAdapter()).getPosition(getCountryName(homeCountryCode)));

        if (getIntent().hasExtra("REDIRECT_TO_CLOSED_APP")) {
            Log.d(TAG, "REDIRECT_TO_CLOSED_APP");
            launchPIMDemoUapp(getIntent().getExtras());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pimDemoUAppInterface.isUserLoggedIn()) {
            enableMigrationSwitch.setEnabled(false);
            enableChuck.setEnabled(false);
            spinnerCountrySelection.setEnabled(false);
            spinnerLaunchFLow.setEnabled(false);
        } else {
            enableMigrationSwitch.setEnabled(true);
            enableChuck.setEnabled(true);
            spinnerCountrySelection.setEnabled(true);
            spinnerLaunchFLow.setEnabled(true);
        }

        if (enableMigrationSwitch.isChecked())
            selectLibrary.setEnabled(true);
        else
            selectLibrary.setEnabled(false);
    }

    private void ininitalizeView() {
        Button launchUApp = findViewById(R.id.launch);
        enableChuck = findViewById(R.id.pim_checkbox);
        selectLibrary = findViewById(R.id.selectLibrary);
        spinnerCountrySelection = findViewById(R.id.spinner_CountrySelection);
        spinnerLaunchFLow = findViewById(R.id.spinner_LaunchFlow);
        enableMigrationSwitch = findViewById(R.id.enableMigrationSwitch);
        enableMigrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "enableMigrationSwitch : " + isChecked);
                initPIMDemoUApp(countryMap.get(spinnerCountrySelection.getSelectedItem()));
                if (isChecked) {
                    selectLibrary.setEnabled(true);
                } else
                    selectLibrary.setEnabled(false);
            }
        });

        launchUApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "launchUApp clicked : " + appInfraInterface.getServiceDiscovery().getHomeCountry());
                saveChuckInSharedPrefs();
                Bundle bundle = new Bundle();
                bundle.putString("LaunchFlow",spinnerLaunchFLow.getSelectedItem().toString());
                launchPIMDemoUapp(bundle);
            }
        });

    }

    private void saveChuckInSharedPrefs() {
        SharedPreferences preferences = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CHUCK", enableChuck.isChecked());
        editor.apply();
    }

    private void updateLibraryListData() {
        List<String> libraryList = new ArrayList<>();
        libraryList.add("UDI");
        libraryList.add("USR");
        PIMSpinnerAdapter arrayAdapter = new PIMSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, libraryList);
        selectLibrary.setAdapter(arrayAdapter);
        selectLibrary.setSelection(1, false);
        selectLibrary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedLib = libraryList.get(position);
                Log.d(TAG, "selectedLib" + selectedLib);
                initPIMDemoUApp(countryMap.get(spinnerCountrySelection.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateCountryListData() {
        PIMSpinnerAdapter arrayAdapter = new PIMSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        spinnerCountrySelection.setAdapter(arrayAdapter);
        spinnerCountrySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String countrycode = countryMap.get(countryList.get(position));
                Log.d(TAG, "spinnerCountrySelection : " + position + " Country Code : " + countrycode);
                if(countrycode != null && !countrycode.equals(homeCountryCode)) {
                    appInfraInterface.getServiceDiscovery().setHomeCountry(countrycode);
                    homeCountryCode = countrycode;
                    initPIMDemoUApp(countrycode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initLaunchFlowData(){
        List<String> launchFlowList = new ArrayList<>();
        launchFlowList.add("noPrompt");
        launchFlowList.add("login");
        launchFlowList.add("create");
        PIMSpinnerAdapter arrayAdapter = new PIMSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, launchFlowList);
        spinnerLaunchFLow.setAdapter(arrayAdapter);
        spinnerLaunchFLow.setSelection(0, false);
        spinnerLaunchFLow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected launch flow" + launchFlowList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void launchPIMDemoUapp(Bundle bundle) {
        pimDemoUAppInterface.launch(new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, bundle), new PIMDemoUAppLaunchInput());
    }

    private void initPIMDemoUApp(String countryCode) {
        Log.d(TAG, "initPIMDemoUApp called with country code : " + countryCode);
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface, getComponentType(countryCode));
        pimDemoUAppInterface.init(pimDemoUAppDependencies, new PIMDemoUAppSettings(mContext));
    }

    private RegistrationLib getComponentType(String countryCode) {
        if (enableMigrationSwitch.isChecked()) {
            if (selectLibrary.getSelectedItemPosition() == 0)
                return RegistrationLib.UDI;
            else
                return RegistrationLib.USR;
        } else {
            String[] usr_countries = new String[]{"CN", "RU"};
            List<String> urCountriesList = Arrays.asList(usr_countries);
            if (urCountriesList.contains(countryCode))
                return RegistrationLib.USR;
            else
                return RegistrationLib.UDI;
        }
    }

    private String getCountryName(String countryCode) {
        for (Map.Entry<String, String> set : countryMap.entrySet()) {
            if (set.getValue().equalsIgnoreCase(countryCode))
                return set.getKey();
        }
        return null;
    }

    private void initCountryMap() {
        String[] countryArray = getResources().getStringArray(R.array.countries_array);
        for (String pair : countryArray) {
            String[] countryCodeName = pair.split("\\|");
            if (countryCodeName.length == 2)
                countryMap.put(countryCodeName[0], countryCodeName[1]);
        }
        countryList = new ArrayList<>(countryMap.keySet());
    }
}
