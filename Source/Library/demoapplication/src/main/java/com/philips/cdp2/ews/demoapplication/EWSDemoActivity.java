package com.philips.cdp2.ews.demoapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.philips.cdp2.ews.configuration.BaseContentConfiguration;
import com.philips.cdp2.ews.configuration.ContentConfiguration;
import com.philips.cdp2.ews.configuration.HappyFlowContentConfiguration;
import com.philips.cdp2.ews.configuration.TroubleShootContentConfiguration;
import com.philips.cdp2.ews.microapp.EWSDependencies;
import com.philips.cdp2.ews.microapp.EWSInterface;
import com.philips.cdp2.ews.microapp.EWSLauncherInput;
import com.philips.cdp2.ews.tagging.Actions;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uappframework.uappinput.UappDependencies;
import com.philips.platform.uappframework.uappinput.UappSettings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.philips.platform.uappframework.launcher.ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_PORTRAIT;

public class EWSDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner configSpinner;
    private static final String WAKEUP_LIGHT = "wl";
    private static final String AIRPURIFIER = "ap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewsdemo);
        findViewById(R.id.btnLaunchEws).setOnClickListener(this);

        configSpinner = (Spinner) findViewById(R.id.configurationSelection);
        configSpinner.setOnItemSelectedListener(itemSelectedListener);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.configurations));
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        configSpinner.setAdapter(aa);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLaunchEws:
                launchEwsUApp();
                break;
            default:
                break;
        }
    }

    private void launchEwsUApp() {
        AppInfraInterface appInfra = new AppInfra.Builder().build(getApplicationContext());
        EWSInterface ewsInterface = new EWSInterface();
        ewsInterface.init(createUappDependencies(appInfra, createProductMap()), new UappSettings(getApplicationContext()));
        ewsInterface.launch(new ActivityLauncher(SCREEN_ORIENTATION_PORTRAIT, -1), new EWSLauncherInput());
    }

    @NonNull
    private UappDependencies createUappDependencies(AppInfraInterface appInfra,
                                                    Map<String, String> productKeyMap) {
        return new EWSDependencies(appInfra, productKeyMap,
                new ContentConfiguration(createBaseContentConfiguration(),
                                        createHappyFlowConfiguration(),
                                        createTroubleShootingConfiguration()));
    }

    @NonNull
    private Map<String, String> createProductMap() {
        Map<String, String> productKeyMap = new HashMap<>();
        productKeyMap.put(EWSInterface.PRODUCT_NAME, Actions.Value.PRODUCT_NAME_SOMNEO);
        return productKeyMap;
    }

    @NonNull
    private BaseContentConfiguration createBaseContentConfiguration(){
        if (isDefaultValueSelected()){
            return new BaseContentConfiguration();
        }else{
            return new BaseContentConfiguration(R.string.lbl_devicename, R.string.lbl_appname);
        }
    }

    @NonNull
    private HappyFlowContentConfiguration createHappyFlowConfiguration(){
        if(isDefaultValueSelected()){
            return new HappyFlowContentConfiguration.Builder().build();
        }else{
            return new HappyFlowContentConfiguration.Builder()
                    .setGettingStartedScreenTitle(R.string.label_ews_get_started_title)
                    .setSetUpScreenTitle(R.string.lbl_setup_screen_title)
                    .setSetUpScreenBody(R.string.lbl_setup_screen_body)
                    .build();
        }
    }

    private void updateCurrentContent(String currentContent) {
        try {
            Configuration config = new Configuration(getResources().getConfiguration());
            config.setLocale(new Locale(currentContent));
            getResources().getConfiguration().updateFrom(config);
        } catch (Exception e) {
            Log.e(EWSDemoActivity.class.getName(), e.toString());
        }
    }

    private boolean isDefaultValueSelected(){
        if (configSpinner.getSelectedItem().equals(getResources().getString(R.string.default_value))){
            return true;
        }
        return false;
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 1:
                    updateCurrentContent(WAKEUP_LIGHT);
                    break;
                case 2:
                    updateCurrentContent(AIRPURIFIER);
                    break;
                case 0:
                default:
                    updateCurrentContent("");
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
        }
    };
    @NonNull
    private TroubleShootContentConfiguration createTroubleShootingConfiguration(){
        return new TroubleShootContentConfiguration.Builder()
                .setConnectWrongPhoneTitle(R.string.lbl_ews_H_03_01_title)
                .setConnectWrongPhoneBody(R.string.lbl_ews_H_03_01_body)
                .setConnectWrongPhoneImage(R.drawable.navigation_image)
                .setConnectWrongPhoneQuestion(R.string.lbl_ews_H_03_01_question)

                .setResetConnectionTitle(R.string.lbl_ews_H_03_02_title)
                .setResetConnectionBody(R.string.lbl_ews_H_03_02_body)
                .setResetConnectionImage(R.drawable.navigation_image)

                .setResetDeviceTitle(R.string.lbl_ews_H_03_03_title)
                .setResetDeviceBody(R.string.lbl_ews_H_03_03_body)
                .setResetDeviceImage(R.drawable.navigation_image)

                .setSetUpAccessPointTitle(R.string.lbl_ews_H_03_04_title)
                .setSetUpAccessPointBody(R.string.lbl_ews_H_03_04_body)
                .setSetUpAccessPointImage(R.drawable.navigation_image)
                .build();
    }
}