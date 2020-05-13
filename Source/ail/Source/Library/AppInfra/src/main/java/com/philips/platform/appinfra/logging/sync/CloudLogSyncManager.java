package com.philips.platform.appinfra.logging.sync;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.text.TextUtils;
import android.util.Log;

import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.consentmanager.ConsentStatusChangedListener;
import com.philips.platform.appinfra.consentmanager.FetchConsentCallback;
import com.philips.platform.appinfra.logging.LoggingConfiguration;
import com.philips.platform.appinfra.logging.database.AILCloudLogDBManager;
import com.philips.platform.appinfra.rest.RestInterface;
import com.philips.platform.pif.chi.ConsentError;
import com.philips.platform.pif.chi.datamodel.ConsentDefinition;
import com.philips.platform.pif.chi.datamodel.ConsentDefinitionStatus;
import com.philips.platform.pif.chi.datamodel.ConsentStates;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by abhishek on 5/14/18.
 */

public class CloudLogSyncManager implements Observer<Integer>, RestInterface.NetworkConnectivityChangeListener,ConsentStatusChangedListener {

    private static CloudLogSyncManager cloudLogSyncManager;

    private static int NUMBER_OF_CORES = 2;

    private static int MAX_NUMBER_OF_CORES = 4;

    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private BlockingQueue<Runnable> mSyncDataWorkQueue;

    private ThreadPoolExecutor threadPoolExecutor;

    private AppInfraInterface appInfra;

    private LoggingConfiguration loggingConfiguration;

    private LiveData<Integer> dbLogCount;

    private String secretKey, sharedKey, productKey;

    private boolean consentStatus;

    private boolean isInternetAvailable;

    private CloudLogSyncManager(AppInfraInterface appInfra, final LoggingConfiguration loggingConfiguration) {
        this.appInfra = appInfra;
        this.loggingConfiguration = loggingConfiguration;
        mSyncDataWorkQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                MAX_NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mSyncDataWorkQueue);
        dbLogCount = AILCloudLogDBManager.getInstance(appInfra).getLogCount();
        dbLogCount.observeForever(this); //ToDo :
        secretKey = loggingConfiguration.getCLSecretKey();
        sharedKey = loggingConfiguration.getCLSharedKey();
        productKey = loggingConfiguration.getCLProductKey();
        isInternetAvailable = appInfra.getRestClient().isInternetReachable();
        appInfra.getRestClient().registerNetworkChangeListener(this);
        forceSync();

    }

    private int getMaxPoolSize() {
       return (NUMBER_OF_CORES>MAX_NUMBER_OF_CORES) ?  NUMBER_OF_CORES : MAX_NUMBER_OF_CORES;
    }

    public static CloudLogSyncManager getInstance(AppInfraInterface appInfra, LoggingConfiguration loggingConfiguration) {
        if (cloudLogSyncManager == null) {
            cloudLogSyncManager = new CloudLogSyncManager(appInfra, loggingConfiguration);
        }
        return cloudLogSyncManager;
    }

    public boolean checkWhetherToSyncCloudLog() {
        //Add consent part here
        if (isInternetAvailable && isSecretKeyAndSharedKeyAvailable()) {
            return true;
        }
        return false;
    }

    private boolean isSecretKeyAndSharedKeyAvailable() {
        if (TextUtils.isEmpty(secretKey) || TextUtils.isEmpty(sharedKey) || TextUtils.isEmpty(productKey)) {
            return false;
        }
        return true;
    }

    @Override
    public void onChanged(final @Nullable Integer currentLogCount) {
        //Log.d("SyncTesting", "Inside cloud log db change:: count::" + currentLogCount);
        if (checkWhetherToSyncCloudLog()) {
            if (currentLogCount != null && currentLogCount >= loggingConfiguration.getBatchLimit()) {
                checkForConsentAndSync();
            }
        } else {
            //Log.d("SyncTesting", "Sync disabled");
            threadPoolExecutor.getQueue().clear();
        }

    }

    private void checkForConsentAndSync() {
        try {
            ConsentDefinition consentDefinition = appInfra.getConsentManager().getConsentDefinitionForType(appInfra.getCloudLogging().getCloudLoggingConsentIdentifier());
            if (consentDefinition != null) {
                appInfra.getConsentManager().addConsentStatusChangedListener(appInfra.getConsentManager().getConsentDefinitionForType(appInfra.getCloudLogging().getCloudLoggingConsentIdentifier()),this);
                appInfra.getConsentManager().fetchConsentState(consentDefinition, new FetchConsentCallback() {
                    @Override
                    public void onGetConsentSuccess(ConsentDefinitionStatus consentDefinitionStatus) {
                        if (ConsentStates.active.equals(consentDefinitionStatus.getConsentState())) {
                            threadPoolExecutor.execute(new CloudLogSyncRunnable(appInfra, sharedKey, secretKey, productKey));
                        } else {
                            //Log.d("SyncTesting", "Sync disabled");
                            threadPoolExecutor.getQueue().clear();
                        }
                    }

                    @Override
                    public void onGetConsentFailed(ConsentError error) {
                        //Log.d("SyncTesting", "Failed to get consent status");
                    }
                });
            }
        } catch (RuntimeException exception) {
            Log.e("SyncTesting", "Consent definition is not registered yet");
        }
    }

    private void forceSync() {
        //Log.d("SyncTesting", "Inside force sync");
        if (checkWhetherToSyncCloudLog()) {
            checkForConsentAndSync();
        }

    }


    @Override
    public void onConnectivityStateChange(boolean isConnected) {
        isInternetAvailable = isConnected;
        forceSync();
    }

    @Override
    public void consentStatusChanged(@NonNull ConsentDefinition consentDefinition, @Nullable ConsentError consentError, boolean requestedStatus) {
        forceSync();
    }
}
