package com.philips.cdp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.philips.cdp.demo.R;
import com.philips.cdp.registration.ui.utils.RegistrationLaunchHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnUserRegistration;
    private Button mBtnProductRegistration;
    private Button mBtnDiCom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnUserRegistration = (Button) findViewById(R.id.btn_user_registration);
        mBtnUserRegistration.setOnClickListener(this);

        mBtnProductRegistration = (Button) findViewById(R.id.btn_product_registration);
        mBtnProductRegistration.setOnClickListener(this);

        mBtnDiCom = (Button) findViewById(R.id.btn_dicom);
        mBtnDiCom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_user_registration:
                ProductLog.producrlog(ProductLog.ONCLICK, "On Click : User Registration");
                RegistrationLaunchHelper.launchRegistrationActivityWithAccountSettings(this);
                Util.navigateFromUserRegistration();
                break;
            case R.id.btn_product_registration:
                intent = new Intent(this, ProductActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_dicom:
                intent = new Intent(this, DiComActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
