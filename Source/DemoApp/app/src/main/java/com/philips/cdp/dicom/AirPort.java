package com.philips.cdp.dicom;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.philips.cdp.dicommclient.communication.CommunicationStrategy;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.port.DICommPort;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class AirPort extends DICommPort<AirPortProperties> {

    //This name and ID need to match with the appliance's DiComm protocol port name and ID
    private final String NAME = "air";
    private final int PRODUCTID = 1;
    private Gson jsonParser;

    public AirPort(final NetworkNode networkNode, final CommunicationStrategy communicationStrategy) {
        super(networkNode, communicationStrategy);
        jsonParser = new Gson();
    }

    @Override
    public boolean isResponseForThisPort(final String jsonResponse) {
        try {
            AirPortProperties portProperties = jsonParser.fromJson(jsonResponse, AirPortProperties.class);

            // We need to check that the portProperties content is actually not null
            return portProperties.lightOn != null;
        } catch (JsonSyntaxException exception) {
            return false;
        }
    }

    @Override
    protected void processResponse(final String jsonResponse) {
        AirPortProperties airPortProperties = jsonParser.fromJson(jsonResponse, AirPortProperties.class);
        setPortProperties(airPortProperties);
    }

    @Override
    protected String getDICommPortName() {
        return NAME;
    }

    @Override
    protected int getDICommProductId() {
        return PRODUCTID;
    }

    @Override
    public boolean supportsSubscription() {
        return true;
    }

    public void setLight(final boolean light) {
        putProperties(AirPortProperties.KEY_LIGHT_STATE, light ? AirPortProperties.LIGHT_ON_STRING : AirPortProperties.LIGHT_OFF_STRING);
    }
}
