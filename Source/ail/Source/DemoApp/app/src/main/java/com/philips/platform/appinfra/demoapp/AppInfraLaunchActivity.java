package com.philips.platform.appinfra.demoapp;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.philips.platform.aildemo.AILDemouAppDependencies;
import com.philips.platform.aildemo.AILDemouAppInterface;
import com.philips.platform.aildemo.AILDemouAppSettings;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.consentmanager.FetchConsentCallback;
import com.philips.platform.appinfra.consentmanager.PostConsentCallback;
import com.philips.platform.appinfra.logging.AppInfraLogging;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentDefinitionStatus;
import com.philips.platform.uappframework.launcher.ActivityLauncher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppInfraLaunchActivity extends AppCompatActivity {

    private Switch cloudLoggingConsentSwitch;

    private AppInfraInterface appInfra;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_infra_launch);
        appInfra = ((AppInfraApplication) getApplication()).getAppInfra();
        Button button = findViewById(R.id.launch_demo_micro_app);
        button.setOnClickListener(v -> invokeMicroApp());
        cloudLoggingConsentSwitch = findViewById(R.id.cloud_logging_switch);



        printSHA1Key();

        firebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        firebaseAnalytics.setCurrentScreen(AppInfraLaunchActivity.this, "Test page", "Test page" /* class override */);

        cloudLoggingConsentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConsentDefinition consentDefinition = appInfra.getConsentManager().getConsentDefinitionForType(AppInfraLogging.CLOUD_CONSENT);
            appInfra.getConsentManager().storeConsentState(consentDefinition, isChecked, new PostConsentCallback() {
                @Override
                public void onPostConsentFailed(ConsentError error) {
                    Log.v("SyncTesting", "Error while saving consent");
                }

                @Override
                public void onPostConsentSuccess() {
                    Log.v("SyncTesting", "Changed consent sucessfully");
                }
            });
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appInfra.getConsentManager().fetchConsentTypeState(AppInfraLogging.CLOUD_CONSENT, new FetchConsentCallback() {
                    @Override
                    public void onGetConsentSuccess(ConsentDefinitionStatus consentDefinitionStatus) {
                        if (consentDefinitionStatus != null && consentDefinitionStatus.getConsentState() != null) {
                            switch (consentDefinitionStatus.getConsentState()) {
                                case inactive:
                                case rejected:
                                    cloudLoggingConsentSwitch.setChecked(false);
                                    break;
                                case active:
                                    cloudLoggingConsentSwitch.setChecked(true);
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onGetConsentFailed(ConsentError error) {
                        Log.v("LoggingActivity", "Getting consent failed");
                    }
                });
            }
        },2000);


        Button firebase = findViewById(R.id.btnFirebase);
        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

    }


    private void invokeMicroApp() {
        AILDemouAppInterface uAppInterface = AILDemouAppInterface.getInstance();
        AppInfraApplication appInfraApplication = (AppInfraApplication) getApplication();
        uAppInterface.init(new AILDemouAppDependencies(appInfraApplication.getAppInfra()), new AILDemouAppSettings(getApplicationContext()));// pass App-infra instance instead of null
        uAppInterface.launch(new ActivityLauncher(this, ActivityLauncher.ActivityOrientation.SCREEN_ORIENTATION_UNSPECIFIED, null, 0, null),null);// pass launch input if required
    }

    private void printSHA1Key(){

        PackageInfo info;
        try {

            info = getPackageManager().getPackageInfo(
                    "com.philips.platform.appinfra.demoapp", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
                System.out.println("Hash key" + something);
            }

        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}
