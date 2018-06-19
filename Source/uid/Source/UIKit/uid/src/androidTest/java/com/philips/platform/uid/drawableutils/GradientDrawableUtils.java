/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.uid.drawableutils;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableWrapper;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradientDrawableUtils {

    public interface StateColors {
        int getDefaultColor();
        int getStateColor(int[] attr);
        float[] getCornerRadius();
        int getStrokeWidth();
        int getGradientSolidColor();
        int getStrokeSolidColor();
        int getStrokeSolidStateColor(int attr);
        int getStrokeSolidStateColor(int[] attr);
        int getRippleRadius();
        int getRippleColor(int attr);

        float getInnerRadiusRatio();

        float getRingThicknessRatio();

        int[] getColors();
    }

    public static StateColors getStateColors(Drawable d) {
        int version = Build.VERSION.SDK_INT;
        StateColors impl;
        if (d instanceof DrawableWrapper) {
            impl = new DrawableStateColorsWrapper(d);
        } else {
            impl = new MarshmallowStateColors(d);
        }
        return impl;
    }

    static Object getField(Drawable.ConstantState state, String fieldName) {
        try {
            Field field = getTintField(state.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(state);
        } catch (IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Field getTintField(Class<?> type, String fieldName) {
        List<Field> allInheritedFields = getAllInheritedFields(type);
        for (Field field : allInheritedFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    private static List<Field> getAllInheritedFields(Class<?> classType) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> cls = classType; cls != null; cls = cls.getSuperclass()) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        }
        return fields;
    }
}
