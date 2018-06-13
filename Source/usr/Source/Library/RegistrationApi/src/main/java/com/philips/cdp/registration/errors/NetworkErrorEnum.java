package com.philips.cdp.registration.errors;

import android.content.Context;

import com.philips.cdp.registration.R;

/**
 * Created by philips on 4/17/18.
 */

public enum NetworkErrorEnum {

    NETWORK_ERROR(ErrorCodes.NETWORK_ERROR, R.string.Generic_Network_ErrorMsg),
    NO_NETWORK(ErrorCodes.NO_NETWORK, R.string.Network_ErrorMsg);

    int errorCode;
    int stringId;

    NetworkErrorEnum(int errorCode, int stringId) {
        this.errorCode = errorCode;
        this.stringId = stringId;
    }

    public static int getStringId(int errorCode) {

        if (errorCode == ErrorCodes.NO_NETWORK) {
            return NetworkErrorEnum.NO_NETWORK.stringId;
        } else {
            return NetworkErrorEnum.NETWORK_ERROR.stringId;
        }
    }


    public static String getLocalizedError(Context context, int errorCode) {
        if (errorCode == ErrorCodes.NETWORK_ERROR) {
            return context.getString(R.string.Generic_Network_Error) + " " + "[" + errorCode + "]";
        }
        return context.getString(getStringId(errorCode));
    }
}
