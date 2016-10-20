package cdp.philips.com.mydemoapp.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.listener.UserRegistrationListener;
import com.philips.cdp.registration.listener.UserRegistrationUIEventListener;
import com.philips.cdp.registration.settings.RegistrationFunction;
import com.philips.cdp.registration.ui.utils.URInterface;
import com.philips.cdp.registration.ui.utils.URLaunchInput;
import com.philips.platform.uappframework.launcher.FragmentLauncher;
import com.philips.platform.uappframework.listener.ActionBarListener;

import cdp.philips.com.mydemoapp.DataSyncApplication;
import cdp.philips.com.mydemoapp.R;
import cdp.philips.com.mydemoapp.reciever.BaseAppBroadcastReceiver;
import cdp.philips.com.mydemoapp.temperature.TemperatureTimeLineFragment;

import static com.philips.cdp.prxclient.RequestManager.mContext;

public class DemoActivity extends AppCompatActivity implements UserRegistrationListener, UserRegistrationUIEventListener, ActionBarListener{

    private ActionBarListener actionBarListener;
    AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.af_user_registration_activity);
        User user = new User(this);
        if(user.isUserSignIn()){
            showFragment(new TemperatureTimeLineFragment(), TemperatureTimeLineFragment.TAG);
        }else {
            startRegistrationFragment();
        }
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        setUpBackendSynchronizationLoop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelPendingIntent();
    }

    private void setUpBackendSynchronizationLoop() {
        PendingIntent dataSyncIntent = getPendingIntent();

        // Start the first time after 5 seconds
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 5 * 1000;

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, BaseAppBroadcastReceiver.DATA_FETCH_FREQUENCY, dataSyncIntent);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, BaseAppBroadcastReceiver.class);
        intent.setAction(BaseAppBroadcastReceiver.ACTION_USER_DATA_FETCH);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }

    public void cancelPendingIntent() {
        PendingIntent dataSyncIntent = getPendingIntent();
        dataSyncIntent.cancel();
        alarmManager.cancel(dataSyncIntent);
    }

    void startRegistrationFragment(){
        loadPlugIn();
        runUserRegistration();
    }

    private void loadPlugIn(){
        User userObject = new User(mContext);
        userObject.registerUserRegistrationListener(this);
    }

    private void runUserRegistration(){
        launchRegistrationFragment(false);
    }

    /**
     * Launch registration fragment
     */
    private void launchRegistrationFragment(boolean isAccountSettings) {
        int containerID = R.id.frame_container_user_reg;
        URLaunchInput urLaunchInput = new URLaunchInput();
        urLaunchInput.setUserRegistrationUIEventListener(this);
        urLaunchInput.setAccountSettings(isAccountSettings);
        urLaunchInput.enableAddtoBackStack(true);
        urLaunchInput.setRegistrationFunction(RegistrationFunction.Registration);
        FragmentLauncher fragmentLauncher = new FragmentLauncher
                (DemoActivity.this,containerID,actionBarListener);
        URInterface urInterface = new URInterface();
        urInterface.launch(fragmentLauncher, urLaunchInput);
    }

    @Override
    public void onUserLogoutSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserLogoutFailure() {
        Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserLogoutSuccessWithInvalidAccessToken() {

    }

    @Override
    public void onUserRegistrationComplete(final Activity activity) {
        showFragment(new TemperatureTimeLineFragment(), TemperatureTimeLineFragment.TAG);
    }

    public void showFragment(Fragment fragment, String fragmentTag) {
        int containerId = R.id.frame_container_user_reg;

        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(containerId, fragment, fragmentTag);
            fragmentTransaction.addToBackStack(fragmentTag);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrivacyPolicyClick(final Activity activity) {

    }

    @Override
    public void onTermsAndConditionClick(final Activity activity) {

    }

    @Override
    public void updateActionBar(@StringRes final int i, final boolean b) {

    }

    @Override
    public void updateActionBar(final String s, final boolean b) {

    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFrag = fragmentManager.findFragmentById(R.id.frame_container_user_reg);
        if (currentFrag instanceof TemperatureTimeLineFragment) {
            finishAffinity();
        }else {
            super.onBackPressed();
        }
    }
}
