/*
 * Copyright (c) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.pins.shinelib.utility;

/**
 * Created by 310188215 on 15/04/15.
 */
public class Utilities {
    public static String byteToString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            sb.append(data.length).append(" ");
            for (byte value : data) {
                sb.append(String.format("0x%02X ", value));
            }
        } else {
            sb.append("null");
        }
        return sb.toString();
    }
}
