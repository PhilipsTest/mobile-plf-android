/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.demouapp.port.time;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.cdp2.commlib.core.communication.CommunicationStrategy;

public class TimePort extends DICommPort<TimePortProperties> {

    private static final String TAG = "TimePort";

    private static final String TIME_PORT_NAME = "time";
    private static final int TIME_PORT_PRODUCTID = 0;

    public TimePort(CommunicationStrategy communicationStrategy) {
        super(communicationStrategy);
    }

    @Override
    public boolean isResponseForThisPort(String jsonResponse) {
        return (parseResponse(jsonResponse) != null);
    }

    @Override
    protected void processResponse(String jsonResponse) {
        TimePortProperties timePortProperties = parseResponse(jsonResponse);

        if (timePortProperties == null) {
            DICommLog.e(TAG, "TimePort is null.");
        } else {
            setPortProperties(timePortProperties);
        }
    }

    @Override
    protected String getDICommPortName() {
        return TIME_PORT_NAME;
    }

    @Override
    protected int getDICommProductId() {
        return TIME_PORT_PRODUCTID;
    }

    @Override
    public boolean supportsSubscription() {
        return true;
    }

    private TimePortProperties parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }
        TimePortProperties timePortInfo = null;
        try {
            timePortInfo = gson.fromJson(response, TimePortProperties.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            DICommLog.e(TAG, e.getMessage());
        }
        return timePortInfo;
    }
}
