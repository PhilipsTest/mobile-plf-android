
package com.ecs.demotestuapp.integration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.adapter.GroupAdapter;
import com.ecs.demotestuapp.jsonmodel.JSONConfiguration;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.ECSServices;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pif.DataInterface.USR.listeners.UserLoginListener;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.PIMParameterToLaunchEnum;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.view.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;


public class EcsDemoTestActivity extends FragmentActivity implements View.OnClickListener, UserLoginListener,
        UserRegistrationUIEventListener {

    private Button mRegister;

    private UserDataInterface mUserDataInterface;


    URInterface urInterface;
    PIMInterface pimInterface;
    private long mLastClickTime = 0;

    AutoCompleteTextView atPropositionID;

    String[] propositionIDs = {"Tuscany2016", "IAP_MOB_DKA", "IAP_MOB_OHC", "IAP_MOB_PHC"};


    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.processName : context.getString(stringId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.demo_test_layout);
        mRegister = findViewById(R.id.btn_register);

        atPropositionID = findViewById(R.id.at_propositionID);

        ArrayAdapter<String> atAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, propositionIDs);

        atPropositionID.setThreshold(1);
        atPropositionID.setAdapter(atAdapter);

        showAppVersion();

        if (!getApplicationName(getApplicationContext()).equals("PIM Demo App")) {
            urInterface = new URInterface();
            urInterface.init(new EcsDemoTestUAppDependencies(new AppInfra.Builder().build(this)), new EcsDemoTestAppSettings(this));
            mUserDataInterface = urInterface.getUserDataInterface();
        } else {
            pimInterface = new PIMInterface();
            pimInterface.init(new EcsDemoTestUAppDependencies(new AppInfra.Builder().build(this)), new EcsDemoTestAppSettings(this));
            mUserDataInterface = pimInterface.getUserDataInterface();
        }
        ECSServices ecsServices = new ECSServices(null, new AppInfra.Builder().build(getApplicationContext()));


        ECSDataHolder.INSTANCE.setECSService(ecsServices);

        actionBar();

        JSONConfiguration jsonConfiguration = readConfigJsonFile("configuration.json");


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GroupAdapter adapter = new GroupAdapter(jsonConfiguration.getGroup(), this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private JSONConfiguration readConfigJsonFile(String file) {

        JSONConfiguration config = new Gson().fromJson(loadJSONFromAsset(file), JSONConfiguration.class);
        return config;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRegistrationComponant();
    }

    private void initializeRegistrationComponant() {
        if (isUserLoggedIn()) {
            setJanRainID();
            ECSDataHolder.INSTANCE.setUserDataInterface(mUserDataInterface);
            mRegister.setText("Log out");
        } else {
            mRegister.setText("Log in");
        }
    }


    private void actionBar() {
        setTitle("ECS Demo Test App");
    }

    @Override
    public void onClick(final View view) {
        if (!isClickable()) return;

    }

    private void gotoUSRLogInScreen() {

        if (getApplicationName(getApplicationContext()) == "pimApp") {
            URLaunchInput urLaunchInput = new URLaunchInput();
            urLaunchInput.setUserRegistrationUIEventListener(this);
            urLaunchInput.enableAddtoBackStack(true);
            RegistrationContentConfiguration contentConfiguration = new RegistrationContentConfiguration();
            contentConfiguration.enableLastName(true);
            contentConfiguration.enableContinueWithouAccount(true);
            RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);
            urLaunchInput.setRegistrationContentConfiguration(contentConfiguration);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);


            ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
            urInterface.launch(activityLauncher, urLaunchInput);
        } else {
            PIMLaunchInput launchInput = new PIMLaunchInput();
//            FragmentLauncher fragmentLauncher = new FragmentLauncher(this, R.id.pimDemoU_mainFragmentContainer, null);
            launchInput.setUserLoginListener(this);
            ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
            HashMap<PIMParameterToLaunchEnum, Object> parameter = new HashMap<>();
            parameter.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, Boolean.TRUE);
            launchInput.setParameterToLaunch(parameter);
            pimInterface.launch(activityLauncher, launchInput);
        }


    }


    private void showAppVersion() {
        String code = null;
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        TextView versionView = findViewById(R.id.appversion);
        versionView.setText(String.valueOf(code));
    }

    //User Registration interface functions
    @Override
    public void onUserRegistrationComplete(Activity activity) {
        activity.finish();
        mRegister.setText("Log out");
        setJanRainID();
        initializeRegistrationComponant();
    }

    @Override
    public void onPrivacyPolicyClick(Activity activity) {
    }

    @Override
    public void onTermsAndConditionClick(Activity activity) {
    }

    @Override
    public void onPersonalConsentClick(Activity activity) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    boolean isClickable() {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        return true;
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return null;
        } catch (Exception e) {
            return null;
        }
        return json;
    }


    boolean isUserLoggedIn() {
        return mUserDataInterface != null && mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN;
    }

    private void register() {
        if (mRegister.getText().toString().equalsIgnoreCase("Log out")) {
            if (mUserDataInterface.getUserLoggedInState() == UserLoggedInState.USER_LOGGED_IN) {
                mUserDataInterface.logoutSession(new LogoutSessionListener() {
                    @Override
                    public void logoutSessionSuccess() {
                        finish();
                    }

                    @Override
                    public void logoutSessionFailed(Error error) {
                        Toast.makeText(EcsDemoTestActivity.this, "Logout went wrong", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                Toast.makeText(EcsDemoTestActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
            }
        } else {
            gotoUSRLogInScreen();
        }

    }

    public void register(View view) {
        register();
    }

    public void set(View view) {

        ECSDataHolder.INSTANCE.resetData();
        ECSDataHolder.INSTANCE.getEcsServices().setPropositionID(atPropositionID.getText().toString().trim());
    }

    public void remove(View view) {
        atPropositionID.setText("");
        ECSDataHolder.INSTANCE.resetData();
        ECSDataHolder.INSTANCE.getEcsServices().setPropositionID(null);
    }

    public void setJanRainID() {

        ArrayList<String> detailsKey = new ArrayList<>();
        detailsKey.add(UserDetailConstants.ACCESS_TOKEN);
        try {
            HashMap<String, Object> userDetailsMap = mUserDataInterface.getUserDetails(detailsKey);
            String janrainID = userDetailsMap.get(UserDetailConstants.ACCESS_TOKEN).toString();
            ECSDataHolder.INSTANCE.setJanrainID(janrainID);
            ECSDataHolder.INSTANCE.setUserDataInterface(mUserDataInterface);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginFailed(Error error) {

    }
}
