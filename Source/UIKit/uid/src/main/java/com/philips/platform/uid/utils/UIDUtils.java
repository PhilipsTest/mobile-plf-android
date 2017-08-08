/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 *
 */

package com.philips.platform.uid.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;

import com.philips.platform.uid.R;
import com.philips.platform.uid.thememanager.ThemeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class UIDUtils {

    private static final String TAG = "UIDUtils";

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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            UIDLog.e(TAG, e.getMessage());
        }
    }

    public static void animateAlpha(final View view, float toAlpha, int duration, final Runnable endAction) {
        ViewPropertyAnimator animator = view.animate().alpha(toAlpha).setDuration(duration);
        if (endAction != null) {
            animator.setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }
            });
        }
        animator.start();
    }

    public static Drawable setTintOnDrawable(Drawable drawable, int tintId, Context context) {
        ColorStateList colorStateList = ThemeUtils.buildColorStateList(context, tintId);
        Drawable compatDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(compatDrawable, colorStateList);
        return compatDrawable;
    }

    public static int getActionBarSize(Context context) {
        final TypedArray styledAttributes = context.getTheme()
                .obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        int size = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return size;
    }

    public static int getDeviceWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
}