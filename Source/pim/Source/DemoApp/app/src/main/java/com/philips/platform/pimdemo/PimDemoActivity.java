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
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.Button;
import com.pim.demouapp.PIMDemoUAppDependencies;
import com.pim.demouapp.PIMDemoUAppInterface;
import com.pim.demouapp.PIMDemoUAppLaunchInput;
import com.pim.demouapp.PIMDemoUAppLaunchInput.RegistrationLib;
import com.pim.demouapp.PIMDemoUAppSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PimDemoActivity extends UIDActivity {

    private Spinner selectLibrary,spinnerCountrySelection;
    private CheckBox enableChuck;
    private Switch enableMigrationSwitch;
    private AppInfraInterface appInfraInterface;
    private Context mContext;
    private PIMDemoUAppInterface pimDemoUAppInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo);

        ininitalizeView();
        updateLibraryListData();
        updateCountryListData();

        mContext = getApplicationContext();
        appInfraInterface = ((PimDemoApplication)mContext).getAppInfra();
        pimDemoUAppInterface = new PIMDemoUAppInterface();
        String homeCountry = appInfraInterface.getServiceDiscovery().getHomeCountry();
        if(homeCountry != null)
            spinnerCountrySelection.setSelection(getCountryPosition(homeCountry));
        initPIMDemoUApp(homeCountry);
        if(getIntent().hasExtra("REDIRECT_TO_CLOSED_APP")){
            launchPIMDemoUapp(getIntent().getExtras());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pimDemoUAppInterface.isUserLoggedIn()){
            enableMigrationSwitch.setEnabled(false);
            enableChuck.setEnabled(false);
            spinnerCountrySelection.setEnabled(false);
        }else {
            enableMigrationSwitch.setEnabled(true);
            enableChuck.setEnabled(true);
            spinnerCountrySelection.setEnabled(true);
        }

        if(enableMigrationSwitch.isChecked())
            selectLibrary.setEnabled(true);
        else
            selectLibrary.setEnabled(false);
    }

    private void ininitalizeView(){
        Button launchUApp = findViewById(R.id.launch);
        enableChuck = findViewById(R.id.pim_checkbox);
        selectLibrary = findViewById(R.id.selectLibrary);
        spinnerCountrySelection = findViewById(R.id.spinner_CountrySelection);
        enableMigrationSwitch = findViewById(R.id.enableMigrationSwitch);
        enableMigrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                initPIMDemoUApp(getCountryCode(spinnerCountrySelection.getSelectedItem().toString()));
                if(isChecked) {
                    selectLibrary.setEnabled(true);
                }else
                    selectLibrary.setEnabled(false);
            }
        });

        launchUApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChuckInSharedPrefs();
                launchPIMDemoUapp(null);
            }
        });

    }

    private void saveChuckInSharedPrefs(){
        SharedPreferences preferences = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CHUCK", enableChuck.isChecked());
        editor.apply();
    }

    private void updateLibraryListData(){
        List<String> libraryList = new ArrayList<>();
        libraryList.add("UDI");
        libraryList.add("USR");
        PIMSpinnerAdapter arrayAdapter = new PIMSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, libraryList);
        selectLibrary.setAdapter(arrayAdapter);
        selectLibrary.setSelection(1,false);
        selectLibrary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedLib = libraryList.get(position);
                Log.i("UDIDemoActivity","selectedLib"+selectedLib);
                initPIMDemoUApp(appInfraInterface.getServiceDiscovery().getHomeCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateCountryListData(){
        String[] stringArray = getResources().getStringArray(R.array.countries_array);
        List<String> countryList = new ArrayList<>(Arrays.asList(stringArray));
        PIMSpinnerAdapter arrayAdapter = new PIMSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        spinnerCountrySelection.setAdapter(arrayAdapter);
        spinnerCountrySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String countrycode = getCountryCode(countryList.get(position));
                appInfraInterface.getServiceDiscovery().setHomeCountry(countrycode);
                initPIMDemoUApp(countrycode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void launchPIMDemoUapp(Bundle bundle) {
        pimDemoUAppInterface.launch(new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, bundle), new PIMDemoUAppLaunchInput());
    }

    private void initPIMDemoUApp(String countryCode){
        PIMDemoUAppDependencies pimDemoUAppDependencies = new PIMDemoUAppDependencies(appInfraInterface,getComponentType(countryCode));
        pimDemoUAppInterface.init(pimDemoUAppDependencies,new PIMDemoUAppSettings(mContext));
    }

    private RegistrationLib getComponentType(String countryCode){
        if(enableMigrationSwitch.isChecked()){
            if(selectLibrary.getSelectedItemPosition() == 0)
                return RegistrationLib.UDI;
            else
                return RegistrationLib.USR;
        }else {
            String[] usr_countries = new String[]{"CN", "RU"};
            List<String> urCountriesList = Arrays.asList(usr_countries);
            if (urCountriesList.contains(countryCode))
                return RegistrationLib.USR;
            else
                return RegistrationLib.UDI;
        }
    }

    private String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        Map<String, String> countryMap = new HashMap<>();
        Locale locale;
        String name;

        for (String code : isoCountryCodes) {
            locale = new Locale("", code);
            name = locale.getDisplayCountry();
            countryMap.put(name, code);
        }
        return countryMap.get(countryName);
    }

    private int getCountryPosition(String code){
        Locale locale = new Locale("", code);
        String name = locale.getDisplayCountry();

        String[] stringArray = getResources().getStringArray(R.array.countries_array);
        List<String> countryList = new ArrayList<>(Arrays.asList(stringArray));

        if(countryList.contains(name))
            return countryList.indexOf(name);
        else
            return 0;
    }
}
