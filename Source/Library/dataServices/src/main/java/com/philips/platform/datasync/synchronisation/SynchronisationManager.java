/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.datasync.synchronisation;

import com.philips.platform.core.Eventing;
import com.philips.platform.core.events.FetchByDateRange;
import com.philips.platform.core.events.ReadDataFromBackendRequest;
import com.philips.platform.core.events.WriteDataToBackendRequest;
import com.philips.platform.core.listeners.SynchronisationChangeListener;
import com.philips.platform.core.listeners.SynchronisationCompleteListener;
import com.philips.platform.core.trackers.DataServicesManager;

import javax.inject.Inject;

public class SynchronisationManager implements SynchronisationChangeListener {

    private volatile boolean isSyncComplete = true;

    SynchronisationCompleteListener mSynchronisationCompleteListener;

    @Inject
    Eventing mEventing;

    public SynchronisationManager() {
        DataServicesManager.getInstance().getAppComponant().injectSynchronisationManager(this);
    }

    public void startSync(String startDate, String endDate, SynchronisationCompleteListener synchronisationCompleteListner) {
        synchronized (this) {
            mSynchronisationCompleteListener = synchronisationCompleteListner;
            if (isSyncComplete) {
                isSyncComplete = false;
                if (startDate == null && endDate == null) {
                    mEventing.post(new ReadDataFromBackendRequest());
                } else {
                    mEventing.post(new FetchByDateRange(startDate, endDate));
                }
            }
        }
    }

    @Override
    public void dataPullSuccess() {
        mEventing.post(new WriteDataToBackendRequest());
    }

    @Override
    public void dataPushSuccess() {

    }

    @Override
    public void dataPartialPullSuccess(String tillDate) {
        mEventing.post(new WriteDataToBackendRequest());
    }

    @Override
    public void dataPullFail(Exception e) {
        isSyncComplete = true;
        if (mSynchronisationCompleteListener != null)
            mSynchronisationCompleteListener.onSyncFailed(e);
        mSynchronisationCompleteListener = null;
    }

    @Override
    public void dataPushFail(Exception e) {
        isSyncComplete = true;
        if (mSynchronisationCompleteListener != null)
            mSynchronisationCompleteListener.onSyncFailed(e);
        mSynchronisationCompleteListener = null;
    }

    @Override
    public void dataSyncComplete() {
        isSyncComplete = true;
        if (mSynchronisationCompleteListener != null)
            mSynchronisationCompleteListener.onSyncComplete();
        mSynchronisationCompleteListener = null;
    }

    public void stopSync() {
        isSyncComplete = true;
        mSynchronisationCompleteListener = null;
    }
}
