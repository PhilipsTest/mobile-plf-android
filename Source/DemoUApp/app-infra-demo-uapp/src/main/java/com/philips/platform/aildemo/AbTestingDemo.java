package com.philips.platform.aildemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.abtestclient.ABTestClientInterface;
import com.philips.platform.appinfra.abtestclient.ABTestClientManager;
import com.philips.platform.appinfra.demo.R;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;

/**
 * Created by 310243577 on 10/3/2016.
 */

public class AbTestingDemo extends Activity {

    String[] valueTypes = {"App Update", "App Restart"};
    ABTestClientInterface.UPDATETYPES valueType;
    private ABTestClientInterface abTestingInterface;
    private TextView value;
    private Button btValue;
    private Button btCacheStatus;
    private Button btRefresh;
    private TextView cacheStatusValue;
    private TextView refreshStatus;
    private Spinner requestType;
    private EditText testName;
    private EditText defaultValue;

    byte[] plainByte;
    byte[] encryptedByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.abtesting);

        value = (TextView) findViewById(R.id.value);
        btValue = (Button) findViewById(R.id.bttestValue);
        btCacheStatus = (Button) findViewById(R.id.btcachestatus);
        btRefresh = (Button) findViewById(R.id.btrefresh);
        cacheStatusValue = (TextView) findViewById(R.id.cachestatusValue);
        refreshStatus = (TextView) findViewById(R.id.refreshstatus);
        requestType = (Spinner) findViewById(R.id.spinnerRequestType);
        testName = (EditText) findViewById(R.id.tesName);
        defaultValue = (EditText) findViewById(R.id.defaultValue);
        AppInfraInterface appInfra = AILDemouAppInterface.getInstance().getAppInfra();
        SecureStorageInterface mSecureStorage = appInfra.getSecureStorage();

        //abTestingInterface = AILDemouAppInterface.mAppInfra.getAbTesting();
        abTestingInterface = new ABTestClientManager((AppInfra) appInfra);

       // testName.setText("DOT-ReceiveMarketingOptIn");
        defaultValue.setText("Experience K");

        ArrayAdapter<String> input_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, valueTypes);
        requestType.setAdapter(input_adapter);

        requestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        double enc = 4324332423432432432435425435435346465464547657567.000343242342;
        btValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (requestType.getSelectedItem().toString().equalsIgnoreCase("App Update")) {
                    valueType = ABTestClientInterface.UPDATETYPES.ONLY_AT_APP_UPDATE;
                } else if (requestType.getSelectedItem().toString().equalsIgnoreCase("App Restart")) {
                    valueType = ABTestClientInterface.UPDATETYPES.EVERY_APP_START;
                }
                String test = abTestingInterface.getTestValue(testName.getText().toString(), defaultValue.getText().toString(),
                        valueType, null);
                value.setText("Experience" + test);
            }
        });

        btCacheStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(abTestingInterface.getCacheStatus() != null) {
                    String cacheStatus = abTestingInterface.getCacheStatus().toString();
                    if(cacheStatus != null)
                        cacheStatusValue.setText(cacheStatus);
                }
            }
        });

        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abTestingInterface.updateCache(new ABTestClientInterface.OnRefreshListener() {
                    @Override
                    public void onSuccess() {
                        refreshStatus.setText("SUCCESS");
                    }

                    @Override
                    public void onError(ERRORVALUES error, String message) {
                        refreshStatus.setText(message);
                    }
                });
            }
        });

//        abTestingInterface.getExperience("philipsmobileappabtest1content", "Experience k ", null,
//                this);
//
    }

//    @Override
//    public void onExperienceReceived(String experience) {
//            value.setText("Experience"+experience);
//    }
}
