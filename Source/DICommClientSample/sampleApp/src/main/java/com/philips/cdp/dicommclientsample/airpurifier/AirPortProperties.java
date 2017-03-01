/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 */
package com.philips.cdp.dicommclientsample.airpurifier;

import com.philips.cdp2.commlib.core.port.PortProperties;

public interface AirPortProperties extends PortProperties {
    String KEY_LIGHT_STATE = "aqil"; // Air Quality Indicator Light

    boolean getLightOn();

    boolean lightIsSet();
}
