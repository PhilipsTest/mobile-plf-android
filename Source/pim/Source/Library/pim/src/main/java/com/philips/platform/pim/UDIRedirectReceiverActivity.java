/*
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philips.platform.pim;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.manager.PIMUserManager;
import com.philips.platform.pim.utilities.PIMSecureStorageHelper;

import net.openid.appauth.AuthorizationManagementActivity;
import net.openid.appauth.AuthorizationService;

/**
 * Activity that receives the redirect Uri sent by the OpenID endpoint. It forwards the data
 * received as part of this redirect to {@link AuthorizationManagementActivity}, which
 * destroys the browser tab before returning the result to the completion
 * {@link android.app.PendingIntent}
 * provided to {@link AuthorizationService#performAuthorizationRequest}.
 * <p>
 * App developers using this library must override the `appAuthRedirectScheme`
 * property in their `build.gradle` to specify the custom scheme that will be used for
 * the OAuth2 redirect. If custom scheme redirect cannot be used with the identity provider
 * you are integrating with, then a custom intent filter should be defined in your
 * application manifest instead. For example, to handle
 * `https://www.example.com/oauth2redirect`:
 * <p>
 * ```xml
 * <intent-filter>
 * <action android:name="android.intent.action.VIEW"/>
 * <category android:name="android.intent.category.DEFAULT"/>
 * <category android:name="android.intent.category.BROWSABLE"/>
 * <data android:scheme="https"
 * android:host="www.example.com"
 * android:path="/oauth2redirect" />
 * </intent-filter>
 * ```
 */
public class UDIRedirectReceiverActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        if (PIMSettingManager.getInstance().getPimOidcConfigration() != null) {
            startActivity(AuthorizationManagementActivity.createResponseHandlingIntent(
                    this, getIntent().getData()));
        } else if (getIntent().getData() != null) {
            PIMSecureStorageHelper pimSecureStorageHelper = new PIMSecureStorageHelper(getApplicationContext(), new AppInfra.Builder().build(this));
            pimSecureStorageHelper.saveAuthorizationResponse(getIntent().getData().toString());
            launchAppOnRedirect(this);
        }
        finish();
    }

    public void launchAppOnRedirect(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        mainIntent.putExtra("RedirectOnAppKill", true);
        context.startActivity(mainIntent);
    }
}
