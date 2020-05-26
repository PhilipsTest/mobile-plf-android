/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.uappframework.launcher;


import android.content.Context;
import android.os.Bundle;

import com.philips.platform.uid.thememanager.ThemeConfiguration;

/**
 * The purpose of the class if to launch micro-app as activity
 * @since 1.0.0
 */

public class ActivityLauncher extends UiLauncher {

    private static final long serialVersionUID = -4985232973624436758L;

    /**
     * These are Flags used for specifying screen orientation.
     * <p/>
     * <p> <b>Note : </b> The flags are similar to default android screen orientation flags</p>
     */


    public enum ActivityOrientation {
        SCREEN_ORIENTATION_UNSPECIFIED(-1),
        SCREEN_ORIENTATION_LANDSCAPE(0),
        SCREEN_ORIENTATION_PORTRAIT(1),
        SCREEN_ORIENTATION_USER(2),
        SCREEN_ORIENTATION_BEHIND(3),
        SCREEN_ORIENTATION_SENSOR(4),
        SCREEN_ORIENTATION_NOSENSOR(5),
        SCREEN_ORIENTATION_SENSOR_LANDSCAPE(6),
        SCREEN_ORIENTATION_SENSOR_PORTRAIT(7),
        SCREEN_ORIENTATION_REVERSE_LANDSCAPE(8),
        SCREEN_ORIENTATION_REVERSE_PORTRAIT(9),
        SCREEN_ORIENTATION_FULL_SENSOR(10),
        SCREEN_ORIENTATION_USER_LANDSCAPE(11),
        SCREEN_ORIENTATION_USER_PORTRAIT(12),
        SCREEN_ORIENTATION_FULL_USER(13),
        SCREEN_ORIENTATION_LOCKED(14);

        private int value;

        ActivityOrientation(int value) {
            this.value = value;
        }
        /**
         To retrieve Screen Orientation.
         @return Orientation value
         @since 1.0.0
         */

        public int getOrientationValue() {
            return this.value;
        }
    }
    private int mUiKitTheme;

    private ActivityLauncher.ActivityOrientation mScreenOrientation = null;

    protected Bundle mBundle;

    private ThemeConfiguration mDLSThemeConfiguration;
    private Context activityContext;

    /**
     Constructor
     @param activityContext takes activity context instance
     @param screenOrientation takes screen Orientation
     @param dlsThemeConfiguration takes DLS configuration
     @param dlsUiKitTheme takes UiKit Theme
     @param bundle bundle object
     @since 1805
     */
    public ActivityLauncher(Context activityContext, ActivityLauncher.ActivityOrientation screenOrientation, ThemeConfiguration dlsThemeConfiguration,
                            int dlsUiKitTheme, Bundle bundle) {
        this.activityContext = activityContext;
        mScreenOrientation = screenOrientation;
        mBundle = bundle;
        mDLSThemeConfiguration =dlsThemeConfiguration;
        mUiKitTheme = dlsUiKitTheme;
    }
    /**
     * Get the screen orientation
     @return screen orientation
     @since 1.0.0
     */
    public ActivityLauncher.ActivityOrientation getScreenOrientation() {
        return mScreenOrientation;
    }

    /**
     * Get the UI Kit theme
     * @return UI Kit theme
     * @since 1.0.0
     */
    public int getUiKitTheme() {
        return this.mUiKitTheme;
    }

    /**
     * Get the bundle
     * @return returns the bundle
     * @since 1.0.0
     */
    public Bundle getBundle() {
        return mBundle;
    }

    /**
     * Get the DLS Theme Configuration
     * @return returns the theme configuration
     * @since 2.2.0
     */
    public ThemeConfiguration getDlsThemeConfiguration() {
        return mDLSThemeConfiguration;
    }

    /**
     * Api used to return activity instance
     * @return instance of activity passed in constructor
     * @since 1805
     */
    public Context getActivityContext() {
        return activityContext;
    }
}

