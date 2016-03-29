package com.philips.cdp.dicom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.Util;
import com.philips.cdp.backend.ProdRegHelper;
import com.philips.cdp.backend.ProdRegRequestInfo;
import com.philips.cdp.demo.R;
import com.philips.cdp.dicommclient.appliance.CurrentApplianceManager;
import com.philips.cdp.dicommclient.appliance.DICommAppliance;
import com.philips.cdp.dicommclient.appliance.DICommApplianceListener;
import com.philips.cdp.dicommclient.discovery.DiscoveryManager;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp.dicommclient.port.common.DevicePort;
import com.philips.cdp.dicommclient.port.common.DevicePortProperties;
import com.philips.cdp.dicommclient.port.common.PairingHandler;
import com.philips.cdp.dicommclient.port.common.PairingListener;
import com.philips.cdp.handler.ErrorType;
import com.philips.cdp.handler.ProdRegListener;
import com.philips.cdp.localematch.enums.Catalog;
import com.philips.cdp.localematch.enums.Sector;
import com.philips.cdp.model.ProdRegResponse;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.ui.utils.RegistrationLaunchHelper;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class DetailActivity extends AppCompatActivity {

    private String TAG = getClass().toString();
    private EditText mEditTextName;
    private SwitchCompat mLightSwitch;
    private AirPurifier mCurrentPurifier;
    private TextView mCtn;
    private TextView mSerial_number;
    private Button mRegister;

    private DICommApplianceListener diCommApplianceListener = new DICommApplianceListener() {

        @Override
        public void onAppliancePortUpdate(final DICommAppliance appliance, final DICommPort<?> port) {
            if (port instanceof AirPort) {
                updateLightSwitchView((AirPort) port);
            } else if (port instanceof DevicePort) {
                updateDeviceNameView((DevicePort) port);
            }
        }

        @Override
        public void onAppliancePortError(final DICommAppliance appliance, final DICommPort<?> port, final com.philips.cdp.dicommclient.request.Error error) {
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        mCurrentPurifier = (AirPurifier) CurrentApplianceManager.getInstance().getCurrentAppliance();

        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mRegister = (Button) findViewById(R.id.register);

        mLightSwitch = (SwitchCompat) findViewById(R.id.switchLight);
        mLightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean isChecked) {
                updateLightProperty(isChecked);
            }
        });

        final Button buttonSet = (Button) findViewById(R.id.buttonSet);
        buttonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                updateNameProperty(mEditTextName.getText().toString());
            }
        });

        findViewById(R.id.buttonPair).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startPairing();
            }
        });

        updateLightSwitchView(mCurrentPurifier.getAirPort());
        updateDeviceNameView(mCurrentPurifier.getDevicePort());
    }

    private void updateNameProperty(final String name) {
        DevicePort devicePort = mCurrentPurifier.getDevicePort();
        if (devicePort != null) {
            devicePort.setDeviceName(name);
        }
    }

    private void updateLightProperty(final boolean isChecked) {
        AirPort airPort = mCurrentPurifier.getAirPort();
        if (airPort != null) {
            airPort.setLight(isChecked);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CurrentApplianceManager.getInstance().addApplianceListener(diCommApplianceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CurrentApplianceManager.getInstance().removeApplianceListener(diCommApplianceListener);
    }

    private void updateDeviceNameView(final DevicePort devicePort) {
        DevicePortProperties properties = devicePort.getPortProperties();
        if (properties != null) {
            mRegister.setVisibility(View.VISIBLE);
            mEditTextName.setText(properties.getCtn() + "Serial No" + properties.getSerial());
            mCtn = (TextView) findViewById(R.id.ctn);
            mSerial_number = (TextView) findViewById(R.id.serial_number);
            mCtn.setText(properties.getCtn());
            mSerial_number.setText(properties.getSerial());
        }
    }

    private void updateLightSwitchView(final AirPort port) {
        AirPortProperties properties = port.getPortProperties();
        if (properties != null) {
            mLightSwitch.setChecked(properties.getLightOn());
        }
    }

    private void startPairing() {

        PairingHandler<AirPurifier> pairingHandler = new PairingHandler<>(mCurrentPurifier, new PairingListener() {
            @Override
            public void onPairingSuccess(final NetworkNode networkNode) {
                Log.d(TAG, "onPairingSuccess() called with: " + "networkNode = [" + networkNode + "]");

                DiscoveryManager<AirPurifier> discoveryManager = (DiscoveryManager<AirPurifier>) DiscoveryManager.getInstance();
                discoveryManager.insertApplianceToDatabase(mCurrentPurifier);

                showToast(getResources().getString(R.string.pairing_successful));
            }

            @Override
            public void onPairingFailed(final NetworkNode networkNode) {
                Log.d(TAG, "onPairingFailed() called with: " + "networkNode = [" + networkNode + "]");
                showToast(getResources().getString(R.string.pairing_failed));
            }
        });

        pairingHandler.startPairing();
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        final User mUser = new User(this);

        if (mUser.isUserSignIn(DetailActivity.this) && mUser.getEmailVerificationStatus(DetailActivity.this)) {
            if (mCtn.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(DetailActivity.this, getResources().getString(R.string.enter_ctn_number), Toast.LENGTH_SHORT).show();
            } else {
                registerProduct();
            }
        } else {
            Toast.makeText(DetailActivity.this, getResources().getString(R.string.Account_Merge_EnterPassword_Placeholder_txtFiled), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "On Click : User Registration");
            RegistrationLaunchHelper.launchRegistrationActivityWithAccountSettings(this);
            Util.navigateFromUserRegistration();
        }
    }

    private void registerProduct() {
        ProdRegRequestInfo prodRegRequestInfo = new ProdRegRequestInfo(mCtn.getText().toString(), mSerial_number.getText().toString(), Sector.B2C, Catalog.CONSUMER);
        ProdRegHelper prodRegHelper = new ProdRegHelper();
        prodRegHelper.setLocale("en", "GB");
        prodRegRequestInfo.setPurchaseDate("2016-03-21");
        final ProdRegListener listener = new ProdRegListener() {
            @Override
            public void onProdRegSuccess(ResponseData responseData) {
                Toast.makeText(DetailActivity.this, getResources().getString(R.string.product_registered_successfully), Toast.LENGTH_SHORT).show();
                ProdRegResponse prodRegResponse = (ProdRegResponse) responseData;
                if (prodRegResponse.getData() != null)
                    Log.d(TAG, " Response Data : " + prodRegResponse.getData());
            }

            @Override
            public void onProdRegFailed(ErrorType errorType) {
                Log.d(TAG, "Negative Response Data : " + errorType.getDescription() + " with error code : " + errorType.getCode());
                Toast.makeText(DetailActivity.this, errorType.getDescription(), Toast.LENGTH_SHORT).show();
            }
        };
        prodRegHelper.registerProduct(this, prodRegRequestInfo, listener);
    }
}
