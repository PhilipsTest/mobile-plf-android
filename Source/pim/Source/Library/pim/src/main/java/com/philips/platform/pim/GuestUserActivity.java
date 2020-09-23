package com.philips.platform.pim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class GuestUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instantiateWithGuestUser();
    }

    private void instantiateWithGuestUser(){
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.launchUrl(this, Uri.parse("http://www.google.com"));

        String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";  // Change when in stable

        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {

            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                Log.i("SHASHI","SHASHI : onCustomTabsServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("SHASHI","SHASHI onServiceDisconnected: ");
            }
        };
        boolean ok = CustomTabsClient.bindCustomTabsService(this
                , CUSTOM_TAB_PACKAGE_NAME, connection);
        Log.i("SHASHI","SHASHI ok: "+ok);

    }
}