/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.utils;

import android.content.*;

import com.philips.cdp.registration.settings.RegistrationSettings;


public class RegPreferenceUtility {

    public static void storePreference(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(RegistrationSettings.REGISTRATION_API_PREFERENCE, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getStoredState(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(
                RegistrationSettings.REGISTRATION_API_PREFERENCE, 0);
        return myPrefs.getBoolean(key, false);
    }
}
