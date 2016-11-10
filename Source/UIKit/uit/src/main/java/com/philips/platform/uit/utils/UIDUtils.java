/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.uit.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;

import java.lang.reflect.Method;

public class UIDUtils {

    public static boolean isMinLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    //Prior to 6.0.1 version, radius is automatically decided as per view bounds.
    //Call hidden api to set radius
    public static void setRippleMaxRadius(Drawable drawable, int radius) {
        try {
            Method setMaxRadius = drawable.getClass().getDeclaredMethod("setMaxRadius", Integer.TYPE);
            setMaxRadius.setAccessible(true);
            setMaxRadius.invoke(drawable, radius);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int modulateColorAlpha(int color, float alphaMod) {
        return ColorUtils.setAlphaComponent(color, Math.round(Color.alpha(color) * alphaMod));
    }
}