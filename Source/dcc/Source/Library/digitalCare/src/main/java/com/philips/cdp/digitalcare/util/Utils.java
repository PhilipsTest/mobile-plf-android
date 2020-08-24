/**
 * Utils class contains common utility methods required across framework under
 * different scenario's.
 *
 *
 * @since Feb 10, 2015
 *
 *  Copyright (c) 2016 Philips. All rights reserved.
 *
 */
package com.philips.cdp.digitalcare.util;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;


public class Utils {

    public boolean isSimAvailable(final Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
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

    public boolean isTelephonyEnabled(final Context context){

        TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE){
            return false;
        }
        return true;
    }

    public static boolean isCountryChina() {
        if(DigitalCareConfigManager.getInstance().getCountry() != null)
        {
            if(DigitalCareConfigManager.getInstance().getCountry().equals("CN")){
                return true;
            }

        }

        return false;
    }

    public static boolean isTablet(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

        return diagonalInches >= 6.5;
    }
}
