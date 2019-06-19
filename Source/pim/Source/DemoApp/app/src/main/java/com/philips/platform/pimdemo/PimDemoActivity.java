package com.philips.platform.pimdemo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.Button;
import com.pim.demouapp.PIMDemoUAppActivity;
import com.pim.demouapp.PIMDemoUAppDependencies;
import com.pim.demouapp.PIMDemoUAppInterface;
import com.pim.demouapp.PIMDemoUAppLaunchInput;
import com.pim.demouapp.PIMDemoUAppSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PimDemoActivity extends UIDActivity {

    private PIMDemoUAppInterface uAppInterface;
    private Spinner spinnerCountrySelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo);
        uAppInterface = new PIMDemoUAppInterface();
        Button launchUApp = findViewById(R.id.launch);
        launchUApp.setOnClickListener(v -> {
            Intent intent = new Intent(PimDemoActivity.this, PIMDemoUAppActivity.class);
            startActivity(intent);
        });

        PimDemoApplication pimDemoApplication = (PimDemoApplication)getApplicationContext();
        AppInfraInterface appInfraInterface = pimDemoApplication.getAppInfra();

        spinnerCountrySelection = findViewById(R.id.spinner_CountrySelection);
        List<String> countryList = new ArrayList<>();
        countryList.add("United States");
        countryList.add("Netherlands");
        countryList.add("Belgium");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,countryList);
        spinnerCountrySelection.setAdapter(arrayAdapter);
        spinnerCountrySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String countrycode = getCountryCode(countryList.get(position));

                appInfraInterface.getServiceDiscovery().setHomeCountry(countrycode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uAppInterface.init(new PIMDemoUAppDependencies(appInfraInterface), new PIMDemoUAppSettings(getApplicationContext()));
    }

    public String getCountryCode(String countryName) {
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
}
