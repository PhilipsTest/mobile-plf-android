package com.philips.cl.di.dev.pa.purifier;

import com.philips.cl.di.dev.pa.datamodel.AirPortInfo;
import com.philips.cl.di.dev.pa.firmware.FirmwarePortInfo;

/**
 * The listener interface for receiving airPurifierEvent events.
 * The class that is interested in processing a airPurifierEvent
 * event implements this interface. When
 * the airPurifierEvent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see AirPortInfo
 */
public interface AirPurifierEventListener {
	
	/**
	 * Sensor data received.
	 *
	 * @param airPurifierEvent the air purifier event
	 */
	public void onAirPurifierEventReceived(AirPortInfo airPurifierEvent) ;
	
	public void onFirmwareEventReceived(FirmwarePortInfo firmwarePortInfo);
}
