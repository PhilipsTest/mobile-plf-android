package com.philips.cdp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.philips.cdp.backend.RegistrationRequestManager;
import com.philips.cdp.demo.R;
import com.philips.cdp.model.ProductMetaData;
import com.philips.cdp.model.ProductResponse;
import com.philips.cdp.model.RegisteredDataResponse;
import com.philips.cdp.productbuilder.ProductMetaDataBuilder;
import com.philips.cdp.productbuilder.RegisteredBuilder;
import com.philips.cdp.productbuilder.RegistrationBuilder;
import com.philips.cdp.prxclient.Logger.PrxLogger;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;
import com.philips.cdp.registration.HttpClient;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.UserWithProduct;
import com.philips.cdp.registration.handlers.ProductRegistrationHandler;
import com.philips.cdp.registration.ui.utils.RegistrationLaunchHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ProductRegistrationActivity implements View.OnClickListener {
    private Button mBtnUserRegistration;
    private Button mBtnProductRegistration;

    private String mCtn = "HD8969/09";
    private String mSectorCode = "B2C";
    private String mLocale = "ru_RU";
    private String mCatalogCode = "CONSUMER";
    private String TAG = getClass().toString();

    private static void launchProductActivity(Context context) {
        Intent registrationIntent = new Intent(context, ProductActivity.class);
        context.startActivity(registrationIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnUserRegistration = (Button) findViewById(R.id.btn_user_registration);
        mBtnUserRegistration.setOnClickListener(this);

        mBtnProductRegistration = (Button) findViewById(R.id.btn_product_registration);
        mBtnProductRegistration.setOnClickListener(this);
    }

    private void registerProduct(final String accessToken, final String serialNumber) {
        PrxLogger.enablePrxLogger(true);

        RegistrationBuilder registrationBuilder = new RegistrationBuilder(mCtn, accessToken, serialNumber);
        registrationBuilder.setmSectorCode(mSectorCode);
        registrationBuilder.setmLocale(mLocale);
        registrationBuilder.setmCatalogCode(mCatalogCode);
        registrationBuilder.setRegistrationChannel("MS81376");
        registrationBuilder.setPurchaseDate("2016-02-15");

//        Pair p2 = new Pair("purchaseDate", "2016-02-15");
//        Pair p3 = new Pair("registrationChannel", "MS81376");

        RegistrationRequestManager mRequestManager = new RegistrationRequestManager(this, "REGISTRATION");
        mRequestManager.executeRequest(registrationBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                ProductResponse productResponse = (ProductResponse) responseData;
                Log.d(TAG, "Positive Response Data : " + productResponse.isSuccess());
                if (productResponse.getData() != null)
                    Log.d(TAG, " Response Data : " + productResponse.getData());
            }

            @Override
            public void onResponseError(String error, int code) {
                Log.d(TAG, "Negative Response Data : " + error + " with error code : " + code);
                Toast.makeText(MainActivity.this, "error in response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registeredProduct(final String accessToken) {
        PrxLogger.enablePrxLogger(true);

        RegisteredBuilder registeredDataBuilder = new RegisteredBuilder(accessToken);

        RegistrationRequestManager mRequestManager = new RegistrationRequestManager(this, "REGISTERED");
        mRequestManager.executeRequest(registeredDataBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                RegisteredDataResponse registeredDataResponse = (RegisteredDataResponse) responseData;
                Log.d(TAG, " Response Data : " + registeredDataResponse.isSuccess());
                Toast.makeText(MainActivity.this, "productMetaData Response Data : " + registeredDataResponse.isSuccess(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseError(String error, int code) {
                Log.d(TAG, "Negative Response Data : " + error + " with error code : " + code);
                Toast.makeText(MainActivity.this, "error in response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void productMetaData(final String accessToken) {
        PrxLogger.enablePrxLogger(true);

        ProductMetaDataBuilder productMetaDataBuilder = new ProductMetaDataBuilder(mCtn, accessToken);
        productMetaDataBuilder.setmSectorCode(mSectorCode);
        productMetaDataBuilder.setmLocale(mLocale);
        productMetaDataBuilder.setmCatalogCode(mCatalogCode);

        RegistrationRequestManager mRequestManager = new RegistrationRequestManager(this, "METADATA");
        mRequestManager.executeRequest(productMetaDataBuilder, new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                ProductMetaData productMetaData = (ProductMetaData) responseData;
                Log.d(TAG, "productMetaData Response Data : " + productMetaData.isSuccess());
                Toast.makeText(MainActivity.this, "productMetaData Response Data : " + productMetaData.isSuccess(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponseError(String error, int code) {
                Log.d(TAG, "Negative Response Data : " + error + " with error code : " + code);
                Toast.makeText(MainActivity.this, "error in response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_registration:
                ProductLog.producrlog(ProductLog.ONCLICK, "On Click : User Registration");
                RegistrationLaunchHelper.launchRegistrationActivityWithAccountSettings(this);
                break;
            case R.id.btn_product_registration:
                final User mUser = new User(this);
                if (mUser.isUserSignIn(MainActivity.this) && mUser.getEmailVerificationStatus(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "user signed in", Toast.LENGTH_SHORT).show();
//                    registerProduct(mUser.getAccessToken(), "ab123456789012");
//                     productMetaData(mUser.getAccessToken());
//                    registeredProduct(mUser.getAccessToken());

                    UserWithProduct userWithProduct = new UserWithProduct(this);
                    userWithProduct.getRefreshedAccessToken(new ProductRegistrationHandler() {
                        @Override
                        public void onRegisterSuccess(final String response) {
//                            registerProductExisting(response);
                            registerProduct(response, "ab123456789012");
                        }

                        @Override
                        public void onRegisterFailedWithFailure(final int error) {
                            return;
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "user not signed in", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void registerProductExisting(final String response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new HttpClient();
                Pair p1 = new Pair("productSerialNumber", "ab123456789012");
                Pair p2 = new Pair("purchaseDate", "2016-02-15");
                Pair p3 = new Pair("registrationChannel", "MS81376");
                List<Pair<String, String>> al = new ArrayList<Pair<String, String>>();
                al.add(p1);
                al.add(p2);
                al.add(p3);
//                httpClient.callPost("https://acc.philips.co.uk/prx/registration/B2C/de_DE/CONSUMER/products/HD8978/01.register.type.product?",al,response);
                String response2 = httpClient.callPost("https://acc.philips.co.uk/prx/registration/B2C/ru_RU/CONSUMER/products/HD8969/09.register.type.product?", al, response);
                Log.d(TAG, " Response Data : " + response2);
            }
        }).start();
    }
}
