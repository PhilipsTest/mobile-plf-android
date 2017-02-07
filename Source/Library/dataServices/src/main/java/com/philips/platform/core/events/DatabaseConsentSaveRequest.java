package com.philips.platform.core.events;


import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.listeners.DBRequestListener;

import java.util.List;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class DatabaseConsentSaveRequest extends Event {


    private final List<ConsentDetail> consentDetails;

    private final DBRequestListener dbRequestListener;


    public DBRequestListener getDbRequestListener() {
        return dbRequestListener;
    }

    public DatabaseConsentSaveRequest(List<ConsentDetail> consentDetails, DBRequestListener dbRequestListener) {
        this.consentDetails = consentDetails;
        this.dbRequestListener = dbRequestListener;

    }

    public List<ConsentDetail> getConsentDetails() {
        return consentDetails;
    }
}
