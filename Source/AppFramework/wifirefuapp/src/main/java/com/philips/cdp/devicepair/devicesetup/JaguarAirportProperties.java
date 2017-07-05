/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.devicepair.devicesetup;

import com.google.gson.annotations.SerializedName;

public class JaguarAirportProperties implements AirPortProperties {

    public static final String LIGHT_ON_STRING = "1";

    public static final String LIGHT_OFF_STRING = "0";

    @SerializedName(KEY_LIGHT_STATE)
    String lightOn;

    @Override
    public boolean getLightOn() {
        return Integer.parseInt(lightOn) == 1;
    }

    @Override
    public boolean lightIsSet() {
        return lightOn != null;
    }
}
