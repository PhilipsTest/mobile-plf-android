
package com.philips.platform.pim;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.pim.manager.PIMSettingManager;
import com.philips.platform.pim.utilities.PIMSecureStorageHelper;

import net.openid.appauth.AuthorizationManagementActivity;

public class UDIRedirectReceiverActivity extends Activity {

    public static final String REDIRECT_TO_CLOSED_APP = "REDIRECT_TO_CLOSED_APP";

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        if (PIMSettingManager.getInstance().getPimOidcConfigration() != null) {
            startActivity(AuthorizationManagementActivity.createResponseHandlingIntent(
                    this, getIntent().getData()));
        } else if (getIntent().getData() != null) {
            AppInfra appInfra = new AppInfra.Builder().build(this);
            PIMSecureStorageHelper pimSecureStorageHelper = new PIMSecureStorageHelper(appInfra);
            Log.i("PIM","intent => "+getIntent().getData().toString());
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
        mainIntent.putExtra(REDIRECT_TO_CLOSED_APP, true);
        context.startActivity(mainIntent);
    }
}
