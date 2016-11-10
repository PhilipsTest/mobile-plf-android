package com.philips.platform.core.monitors;

import android.support.annotation.NonNull;

import com.philips.platform.core.dbinterfaces.DBSavingInterface;
import com.philips.platform.core.events.ConsentBackendGetRequest;
import com.philips.platform.core.events.ConsentBackendSaveRequest;
import com.philips.platform.core.events.ConsentBackendSaveResponse;
import com.philips.platform.core.events.DatabaseConsentSaveRequest;
import com.philips.platform.core.events.ExceptionEvent;
import com.philips.platform.core.events.MomentChangeEvent;
import com.philips.platform.core.events.MomentSaveRequest;

import java.sql.SQLException;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class SavingMonitor extends EventMonitor{
    private static final String TAG = SavingMonitor.class.getSimpleName();
    @NonNull
    DBSavingInterface dbInterface;

    public SavingMonitor(DBSavingInterface dbInterface) {
        this.dbInterface = dbInterface;
    }

    public void onEventAsync(final MomentSaveRequest momentSaveRequest) throws SQLException {
        boolean saved = dbInterface.saveMoment(momentSaveRequest.getMoment());
        if (saved) {
            eventing.post(new MomentChangeEvent(momentSaveRequest.getReferenceId(), momentSaveRequest.getMoment()));
        } else {
            eventing.post(new ExceptionEvent("Failed to insert", new SQLException()));
        }
    }

    public void onEventAsync(final DatabaseConsentSaveRequest consentSaveRequest) throws SQLException {
        boolean saved = dbInterface.saveConsent(consentSaveRequest.getConsent());

        if(!saved){
            eventing.post(new ExceptionEvent("Failed to insert", new SQLException()));
            return;
        }

        if(consentSaveRequest.isDefaultConsent()){
            eventing.post(new ConsentBackendGetRequest(1));
        }else{
            eventing.post(new ConsentBackendSaveRequest(ConsentBackendSaveRequest.RequestType.SAVE, consentSaveRequest.getConsent()));
        }
        /*if (saved && !consentSaveRequest.isDefaultConsent()) {

        } else {
            eventing.post(new ExceptionEvent("Failed to insert", new SQLException()));
        }*/
    }

    public void onEventAsync(final ConsentBackendSaveResponse consentBackendSaveResponse) throws SQLException {
        if(consentBackendSaveResponse.getConsent()!=null){
         dbInterface.saveBackEndConsent(consentBackendSaveResponse.getConsent());
        }else{
           // eventing.post(new ExceptionEvent("Failed to insert", new SQLException()));
        }

    }
}
