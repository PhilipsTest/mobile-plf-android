
package com.ecs.demotestuapp.integration;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.adapter.CategoryExpandableAdapter;
import com.ecs.demotestuapp.jsonmodel.GroupItem;
import com.ecs.demotestuapp.jsonmodel.JSONConfiguration;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.google.gson.Gson;
import com.philips.cdp.registration.configuration.RegistrationConfiguration;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.RegistrationContentConfiguration;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.appconfiguration.AppConfigurationInterface;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;
import com.philips.platform.pif.DataInterface.USR.listeners.LogoutSessionListener;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMInterface;
import com.philips.platform.pim.PIMLaunchInput;
import com.philips.platform.pim.PIMParameterToLaunchEnum;
import com.philips.platform.pim.listeners.UserLoginListener;
import com.philips.platform.uappframework.launcher.ActivityLauncher;
import com.philips.platform.uid.view.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EcsDemoTestActivity extends FragmentActivity implements View.OnClickListener,
        UserLoginListener {

    private Button mRegister;

    private UserDataInterface mUserDataInterface;


    URInterface urInterface;
//    PIMInterface pimInterface;
    private long mLastClickTime = 0;

    EditText etPropositionID;
    private AppInfraInterface mAppInfraInterface;
    private AppConfigurationInterface configInterface;
    private AppConfigurationInterface.AppConfigurationError configError;

    //String[] propositionIDs = {"Tuscany2016", "IAP_MOB_DKA", "IAP_MOB_OHC", "IAP_MOB_PHC"};
    String[] propositionIDs = {"Tuscany2016", "IAP_MOB_DKA", "IAP_MOB_OHC", "IAP_MOB_PHC"};
    private PIMInterface pimInterface;


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

        etPropositionID = findViewById(R.id.et_propositionID);

       /* ArrayAdapter<String> atAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, propositionIDs);

        atPropositionID.setThreshold(1);
        atPropositionID.setAdapter(atAdapter);*/


        mAppInfraInterface=new AppInfra.Builder().build(this);
        configInterface = mAppInfraInterface.getConfigInterface();
        configError = new AppConfigurationInterface.AppConfigurationError();

        try {
            String propertyForKey = (String) configInterface.getPropertyForKey("propositionid", "MEC", configError);
            etPropositionID.setText(propertyForKey);
        }catch (Exception e){

        }

        showAppVersion();

        if (getApplicationName(getApplicationContext()).equals("PIM Demo App")) {

            pimInterface = new PIMInterface();
            pimInterface.init(new EcsDemoTestUAppDependencies(new AppInfra.Builder().build(this)), new EcsDemoTestAppSettings(this));
            mUserDataInterface = pimInterface.getUserDataInterface();

        }else{

            urInterface = new URInterface();
            urInterface.init(new EcsDemoTestUAppDependencies(new AppInfra.Builder().build(this)), new EcsDemoTestAppSettings(this));
            mUserDataInterface = urInterface.getUserDataInterface();
        }


        ECSDataHolder.INSTANCE.setUserDataInterface(mUserDataInterface);
        ECSServices ecsServices = new ECSServices(null, new AppInfra.Builder().build(getApplicationContext()));


        ECSDataHolder.INSTANCE.setECSService(ecsServices);

        actionBar();

        JSONConfiguration jsonConfiguration = readConfigJsonFile("configuration.json");


       // RecyclerView recyclerView = findViewById(R.id.recycler_view);

        ExpandableListView expandableListView = findViewById(R.id.expandable_category);
       // GroupAdapter adapter = new GroupAdapter(jsonConfiguration.getOcc(), this);

        List<GroupItem> groupItemsOCC = jsonConfiguration.getOcc();
        List<GroupItem> groupItemsPIL = jsonConfiguration.getPil();

        List<List<GroupItem>> listOfGroupItems = new ArrayList<>();
        listOfGroupItems.add(groupItemsOCC);
        listOfGroupItems.add(groupItemsPIL);


        CategoryExpandableAdapter categoryExpandableAdapter = new CategoryExpandableAdapter(this,listOfGroupItems);
        expandableListView.setAdapter(categoryExpandableAdapter);
        expandableListView.expandGroup(1); //keep PIL one open
       // recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(adapter);
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

    private void gotoLogInScreen() {

        if (getApplicationName(getApplicationContext()).equals("PIM Demo App")) {

            PIMLaunchInput launchInput = new PIMLaunchInput();
            launchInput.setUserLoginListener(this);
            ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
            HashMap<PIMParameterToLaunchEnum, Object> parameter = new HashMap<>();
            parameter.put(PIMParameterToLaunchEnum.PIM_AB_TESTING_CONSENT, Boolean.TRUE);
            launchInput.setParameterToLaunch(parameter);
            if(pimInterface!=null)pimInterface.launch(activityLauncher, launchInput);
        }
        else {

            URLaunchInput urLaunchInput = new URLaunchInput();
            urLaunchInput.enableAddtoBackStack(true);
            RegistrationContentConfiguration contentConfiguration = new RegistrationContentConfiguration();
            contentConfiguration.enableLastName(true);
            contentConfiguration.enableContinueWithouAccount(true);
            RegistrationConfiguration.getInstance().setPrioritisedFunction(RegistrationFunction.Registration);
            urLaunchInput.setRegistrationContentConfiguration(contentConfiguration);
            urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);


            ActivityLauncher activityLauncher = new ActivityLauncher(this, ActivityLauncher.
                    ActivityOrientation.SCREEN_ORIENTATION_SENSOR, null, 0, null);
            if(urInterface!=null)urInterface.launch(activityLauncher, urLaunchInput);
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
            gotoLogInScreen();
        }

    }

    public void register(View view) {
        register();
    }

    public void set(View view) {

        ECSDataHolder.INSTANCE.resetData();
        ECSDataHolder.INSTANCE.getEcsServices().setPropositionID(etPropositionID.getText().toString().trim());

        configInterface.setPropertyForKey("propositionid", "MEC", etPropositionID.getText().toString(), configError);

        Toast.makeText(this, "Proposition id is set", Toast.LENGTH_SHORT).show();
    }

    public void remove(View view) {
        etPropositionID.setText("");
        ECSDataHolder.INSTANCE.resetData();
        ECSDataHolder.INSTANCE.getEcsServices().setPropositionID(null);

        configInterface.setPropertyForKey("propositionid", "MEC", etPropositionID.getText().toString(), configError);

        Toast.makeText(this, "Proposition id is set", Toast.LENGTH_SHORT).show();
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
        finish();
        mRegister.setText("Log out");
        setJanRainID();
        initializeRegistrationComponant();
    }

    @Override
    public void onLoginFailed(Error error) {

    }
}
