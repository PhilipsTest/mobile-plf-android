/*
 * Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */

package com.philips.platform.appinfra.aikm;


public class GroomLib {


    static {
        System.loadLibrary("native-lib");
    }

    public native char[] ailGroom(char[] testString, int testValue);
}
