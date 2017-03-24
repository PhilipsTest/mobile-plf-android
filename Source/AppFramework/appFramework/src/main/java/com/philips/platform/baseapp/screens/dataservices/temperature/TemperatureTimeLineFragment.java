package com.philips.platform.baseapp.screens.dataservices.temperature;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cdp.registration.User;
import com.philips.cdp.uikit.customviews.CircularProgressbar;
import com.philips.platform.appframework.R;
import com.philips.platform.appframework.flowmanager.exceptions.NullEventException;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;
import com.philips.platform.baseapp.base.AppFrameworkApplication;
import com.philips.platform.baseapp.base.AppFrameworkBaseFragment;
import com.philips.platform.baseapp.screens.dataservices.DataServicesState;
import com.philips.platform.baseapp.screens.dataservices.characteristics.CharacteristicsDialogFragment;
import com.philips.platform.baseapp.screens.dataservices.consents.ConsentDialogFragment;
import com.philips.platform.baseapp.screens.dataservices.database.datatypes.MomentType;
import com.philips.platform.baseapp.screens.dataservices.registration.UserRegistrationInterfaceImpl;
import com.philips.platform.baseapp.screens.dataservices.settings.SettingsFragment;
import com.philips.platform.baseapp.screens.dataservices.utility.Utility;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.SyncType;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBFetchRequestListner;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.listeners.SynchronisationCompleteListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TemperatureTimeLineFragment extends AppFrameworkBaseFragment implements View.OnClickListener, DBFetchRequestListner<Moment>,DBRequestListener<Moment>,DBChangeListener, SynchronisationCompleteListener {
    public static final String TAG = TemperatureTimeLineFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    ArrayList<? extends Moment> mData = new ArrayList();
    private TemperatureTimeLineFragmentcAdapter mAdapter;
    DataServicesManager mDataServicesManager;
    ImageButton mAddButton;
    TemperaturePresenter mTemperaturePresenter;
    TemperatureMomentHelper mTemperatureMomentHelper;
    TextView mTvConsents, mTvCharacteristics, mTvSettings;
    private Context mContext;
    SharedPreferences mSharedPreferences;
    ProgressDialog mProgressBar;
    UserRegistrationInterfaceImpl userRegistrationInterface;
    User mUser;
    Utility mUtility;
    private Handler handler = new Handler();
    private ProgressBar settingsProgressBar;
    private WeakReference<TemperatureTimeLineFragment> temperatureTimeLineFragmentWeakReference;


    @Override
    public String getActionbarTitle() {
        return getContext().getResources().getString(R.string.RA_DataServicesScreen_Title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        temperatureTimeLineFragmentWeakReference = new WeakReference<TemperatureTimeLineFragment>(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        temperatureTimeLineFragmentWeakReference = null;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataServicesManager = DataServicesManager.getInstance();
        mUser = new User(mContext);
        userRegistrationInterface = new UserRegistrationInterfaceImpl(mContext, mUser);
        mTemperatureMomentHelper = new TemperatureMomentHelper();

        //EventHelper.getInstance().registerEventNotification(EventHelper.MOMENT, this);
        mTemperaturePresenter = new TemperaturePresenter(mContext, MomentType.TEMPERATURE, this);
        mUtility = new Utility();
        mSharedPreferences = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        mProgressBar = new ProgressDialog(getContext());
        mProgressBar.setCancelable(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDataServicesManager.registerDBChangeListener(this);
        mDataServicesManager.registerSynchronisationCompleteListener(this);
        setProgressBarVisibility(true);

        Thread t = new Thread(new BuildModel());
        t.start();
    }

    public class BuildModel implements Runnable {

        @Override
        public void run() {
            Log.i(DataServicesState.TAG, "TemperatureTimeLieFragment on start");
            if (isFragmentAlive()) {
                if (mUser != null && !mUser.isUserSignIn() && handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isFragmentAlive()) {
                                Toast.makeText(getContext(), "Please Login", Toast.LENGTH_SHORT).show();
                                mAddButton.setVisibility(View.INVISIBLE);
                                mTvConsents.setVisibility(View.INVISIBLE);
                                setProgressBarVisibility(false);
                                return;
                            }
                        }
                    });
                    return;
                }
                Log.i(DataServicesState.TAG, "TemperatureTimeLieFragment on start - before delete check user");
                if (isFragmentAlive())
                    deleteUserDataIfNewUserLoggedIn();
                Log.i(DataServicesState.TAG, "TemperatureTimeLieFragment on start - before fetchData");
                mTemperaturePresenter.fetchData(TemperatureTimeLineFragment.this);

                //Reseting the sync Flags
        /*mDataServicesManager.setPullComplete(true);
        mDataServicesManager.setPushComplete(true);*/

                if (getActivity() == null) return;
                if (!mUtility.isOnline(getActivity())) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getActivity(), "Please check your connection", Toast.LENGTH_LONG).show();
                        }
                    });

                    return;
                }

                if (!mSharedPreferences.getBoolean("isSynced", false) && handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isFragmentAlive()) {
                                showProgressDialog();
                            }
                        }
                    });
                }
            }
        }
    }

    private boolean isFragmentAlive() {
        return getActivity() != null && temperatureTimeLineFragmentWeakReference != null && isAdded();
    }

    public void setProgressBarVisibility(boolean isVisible) {
        if (isVisible) {
            settingsProgressBar.setVisibility(View.VISIBLE);
        } else {
            settingsProgressBar.setVisibility(View.GONE);
        }
    }

    private void deleteUserDataIfNewUserLoggedIn() {
        try {
            if (getLastStoredEmail() == null) {
                storeLastEmail();
                return;
            }

            if (!isSameEmail()) {
                userRegistrationInterface.clearUserData(this);
            }
            storeLastEmail();
        }catch (NullPointerException e){
            AppFrameworkApplication.loggingInterface.log(LoggingInterface.LogLevel.DEBUG, TAG,
                    e.getMessage());        }
    }

    private boolean isSameEmail() {
        if (getLastStoredEmail().equalsIgnoreCase(mUser.getEmail()))
            return true;
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        DataServicesManager.getInstance().unRegisterDBChangeListener();
        mDataServicesManager.unRegisterSynchronisationCosmpleteListener();
        //cancelPendingIntent();
        //mDataServicesManager.stopCore();
        dismissProgressDialog();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.af_data_sync_fragment, container, false);
        mAdapter = new TemperatureTimeLineFragmentcAdapter(getContext(), mData, mTemperaturePresenter);
        settingsProgressBar = (CircularProgressbar) view.findViewById(R.id.settings_progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.timeline);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAddButton = (ImageButton) view.findViewById(R.id.add);
        mRecyclerView.setAdapter(mAdapter);
        mAddButton.setOnClickListener(this);
        mTvConsents = (TextView) view.findViewById(R.id.tv_set_consents);
        mTvCharacteristics = (TextView) view.findViewById(R.id.tv_set_characteristics);
        mTvSettings = (TextView) view.findViewById(R.id.tv_settings);

        mTvConsents.setOnClickListener(this);
        mTvCharacteristics.setOnClickListener(this);
        mTvSettings.setOnClickListener(this);
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // EventHelper.getInstance().unregisterEventNotification(EventHelper.MOMENT, this);
        //mDataServicesManager.releaseDataServicesInstances();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.add:
                mTemperaturePresenter.addOrUpdateMoment(TemperaturePresenter.ADD, null);
                break;
            case R.id.tv_set_consents:
                ConsentDialogFragment dFragment = new ConsentDialogFragment();
                dFragment.show(getFragmentManager(), "Dialog");

                break;
            case R.id.tv_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                settingsFragment.show(getFragmentManager(), "settings");

                break;

            case R.id.tv_set_characteristics:

                CharacteristicsDialogFragment characteristicsDialogFragment = new CharacteristicsDialogFragment();
                characteristicsDialogFragment.show(getFragmentManager(), "Character");

                break;
        }
    }

    @Override
    public void onSuccess(final List<? extends Moment> data) {
        DSLog.i(DSLog.LOG, "on Success Temperature");
        mTemperaturePresenter.fetchData(this);
    }

    @Override
    public void onFailure(final Exception exception) {
        onFailureRefresh(exception);
    }

    private void onFailureRefresh(final Exception e) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e != null && e.getMessage() != null) {
                    DSLog.i(TAG, "http : UI update Failed" + e.getMessage());
                    if (mContext != null)
                        Toast.makeText(mContext, "UI update Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    DSLog.i(TAG, "http : UI update Failed");
                    if (mContext != null)
                        Toast.makeText(mContext, "UI update Failed", Toast.LENGTH_SHORT).show();
                }
                if (mSharedPreferences.getBoolean("isSynced", false)) {
                    dismissProgressDialog();
                }
                setProgressBarVisibility(false);
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressBar != null && !mProgressBar.isShowing()) {
            mProgressBar.setMessage("Loading Please wait!!!");
            mProgressBar.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressBar != null && mProgressBar.isShowing()) {
            mProgressBar.dismiss();
        }
    }

    String getLastStoredEmail() {
        AppInfraInterface gAppInfra = ((AppFrameworkApplication) getContext().getApplicationContext()).getAppInfra();
        SecureStorageInterface ssInterface = gAppInfra.getSecureStorage();
        SecureStorageInterface.SecureStorageError ssError = new SecureStorageInterface.SecureStorageError();
        String decryptedData = ssInterface.fetchValueForKey("last_email", ssError);
        return decryptedData;
    }

    void storeLastEmail() {
        AppInfraInterface gAppInfra = ((AppFrameworkApplication) getContext().getApplicationContext()).getAppInfra();
        SecureStorageInterface ssInterface = gAppInfra.getSecureStorage();
        SecureStorageInterface.SecureStorageError ssError = new SecureStorageInterface.SecureStorageError();
        ssInterface.storeValueForKey("last_email", mUser.getEmail(), ssError);
    }

    @Override
    public void dBChangeSuccess(SyncType type) {
        DSLog.i(DSLog.LOG,"TemperatureTimeLineFragment onDBSuccess");
        if(type!=SyncType.MOMENT)return;
        DSLog.i(DSLog.LOG,"TemperatureTimeLineFragment onDBSuccess - MOMENTS");
        mTemperaturePresenter.fetchData(TemperatureTimeLineFragment.this);
        /*if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mTemperaturePresenter.fetchData(TemperatureTimeLineFragment.this);
            }
        });
*/

    }

    @Override
    public void dBChangeFailed(final Exception e) {
        showToastOnUiThread("Exception :" + e.getMessage());
    }

    @Override
    public void onSyncComplete() {
        DSLog.i(TAG, "Sync completed");
    }

    @Override
    public void onSyncFailed(final Exception exception) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSharedPreferences.getBoolean("isSynced", false)) {
                    dismissProgressDialog();
                }
                //Toast.makeText(getActivity(), "Exception :" + exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showToastOnUiThread(final String msg){

        if(getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onFetchSuccess(final List<? extends Moment> data) {
        DSLog.i(DSLog.LOG,"On Sucess ArrayList TemperatureTimeLineFragment");
        if (getActivity() == null) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DSLog.i(DSLog.LOG, "http TEmperature TimeLine : UI updated");
                mData = (ArrayList<? extends Moment>) data;
                mAdapter.setData(mData);
                mAdapter.notifyDataSetChanged();

                if (mDataServicesManager.getSyncTypes()!=null && mDataServicesManager.getSyncTypes().size()<=0) {
                    dismissProgressDialog();
                    Toast.makeText(getContext(),"No Sync Types Configured",Toast.LENGTH_LONG).show();
                    return;
                }

                if (mSharedPreferences.getBoolean("isSynced", false)) {
                    dismissProgressDialog();
                }
                setProgressBarVisibility(false);
            }
        });
    }

    @Override
    public void onFetchFailure(Exception exception) {
        onFailureRefresh(exception);
    }
}
