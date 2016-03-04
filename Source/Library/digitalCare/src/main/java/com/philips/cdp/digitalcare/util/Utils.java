package com.philips.cdp.digitalcare.util;

import android.content.Context;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;

/**
 * Utils class contains common utility methods required across framework under
 * different scenario's.
 * 
 * @author naveen@philips.com
 * 
 * @since Feb 10, 2015
 * 
 */
public class Utils {

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static boolean isSimAvailable(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT: //SimState = "No Sim Found!";
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = "Network Locked!";
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = "PIN Required to access SIM!";
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = "PUK Required to access SIM!"; // Personal Unblocking Code
                case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = "Unknown SIM State!";
                    return false;
                default:
                    return true;
            }
    }
}
