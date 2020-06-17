
package com.ecs.demotestuapp.integration;

import android.content.Intent;
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
import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.ecs.ECSServices;
import com.philips.platform.pif.DataInterface.USR.UserDataInterface;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;
import com.philips.platform.pif.DataInterface.USR.enums.UserLoggedInState;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;


public class EcsDemoTestActivity extends FragmentActivity implements View.OnClickListener{


    private long mLastClickTime = 0;

    AutoCompleteTextView atPropositionID;

    String[] propositionIDs = {"Tuscany2016", "IAP_MOB_DKA", "IAP_MOB_OHC", "IAP_MOB_PHC"};
    private UserDataInterface mUserDataInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.demo_test_layout);
        atPropositionID = findViewById(R.id.at_propositionID);

        ArrayAdapter<String> atAdapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, propositionIDs);

        atPropositionID.setThreshold(1);
        atPropositionID.setAdapter(atAdapter);

        showAppVersion();

        mUserDataInterface = DependencyHolder.INSTANCE.getuAppDependencies().getUserDataInterface();
        ECSDataHolder.INSTANCE.setUserDataInterface(mUserDataInterface);
        ECSServices ecsServices = new ECSServices(null, (AppInfra) DependencyHolder.INSTANCE.getuAppDependencies().getAppInfra());


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

        if (isUserLoggedIn()) {
            setJanRainID();
        }
    }

    private void actionBar() {
        setTitle("ECS Demo Test App");
    }

    @Override
    public void onClick(final View view) {
        if (!isClickable()) return;

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

}
