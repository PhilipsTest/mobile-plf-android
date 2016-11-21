package com.philips.platform.uit.matcher;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import com.philips.platform.uit.utils.UIDUtils;
import com.philips.platform.uit.utils.UITTestUtils;

import org.hamcrest.Matcher;

public class FunctionDrawableMatchers {
    public static Matcher<View> isSameHeight(final String funcName, final int expectedValue) {
        return isSameHeight(funcName, expectedValue, -1);
    }

    public static Matcher<View> isSameHeight(final String funcName, final int expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> heightMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameHeight(expectedValue);
                boolean matches = heightMatcher.matches(drawable);
                setValues(heightMatcher.actual, heightMatcher.expected);
                return matches;
            }
        };
    }

    public static Matcher<View> isMinHeight(final String funcName, final int expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> heightMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isMinHeight(expectedValue);
                boolean matches = heightMatcher.matches(drawable);
                setValues(heightMatcher.actual, heightMatcher.expected);
                return matches;
            }
        };
    }

    public static Matcher<View> isSameWidth(final String funcName, final int expectedValue) {
        return isSameWidth(funcName, expectedValue, -1);
    }

    public static Matcher<View> isSameWidth(final String funcName, final int expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> widthMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameWidth(expectedValue);
                boolean matches = widthMatcher.matches(drawable);
                setValues(widthMatcher.actual, widthMatcher.expected);
                return matches;
            }
        };
    }

    public static Matcher<View> isSameRadius(final String funcName, final int index, final float expectedValue) {
        return isSameRadius(funcName, index, expectedValue, -1);
    }

    public static Matcher<View> isSameRadius(final String funcName, final int index, final float expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> radiusMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameRadius(index, expectedValue);
                boolean matches = radiusMatcher.matches(drawable);
                setValues(radiusMatcher.actual, radiusMatcher.expected);
                return matches;
            }
        };
    }

    /**
     * Must be operated on drawables. If the target is ColorStateList, use another function instead.
     *
     * @param funcName
     * @param state
     * @param expectedValue
     * @return
     */
    public static Matcher<View> isSameColor(final String funcName, final int state, final int expectedValue) {
        return isSameColor(funcName, state, expectedValue, -1);
    }

    public static Matcher<View> isSameColor(final String funcName, final int state, final int expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> colorMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameColor(state, expectedValue);
                boolean matches = colorMatcher.matches(drawable);
                setValues(colorMatcher.actual, colorMatcher.expected);
                return matches;
            }
        };
    }

    public static Matcher<View> isSameStrokeColor(final String funcName, final int state, final int expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> colorMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameStrokeColor(state, expectedValue);
                boolean matches = colorMatcher.matches(drawable);
                setValues(colorMatcher.actual, colorMatcher.expected);
                return matches;
            }
        };
    }

    private static Drawable getDrawable(final View view, final String funcName, final int drawableID) {
        Drawable drawable = UITTestUtils.getDrawableWithReflection(view, funcName);
        if (drawable instanceof LayerDrawable && drawableID != -1) {
            drawable = ((LayerDrawable) drawable).findDrawableByLayerId(drawableID);
        }
        return drawable;
    }

    /**
     * Must be operated on ColorStateList. If the target is Drawable, use another function instead.
     *
     * @param funcName
     * @param state
     * @param expectedValue
     * @return
     */
    public static Matcher<View> isSameColorFromColorList(final String funcName, final int state, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ColorStateList colorStateList = UITTestUtils.getColorStateListWithReflection(view, funcName);
                return colorStateList.getColorForState(new int[]{state}, Color.MAGENTA) == expectedValue;
            }
        };
    }

    public static Matcher<View> isSameStrokeWidth(final String funcName, final int expectedValue) {
        return isSameStrokeWidth(funcName, expectedValue, -1);
    }

    public static Matcher<View> isSameStrokeWidth(final String funcName, final int expectedValue, final int drawableID) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                Drawable drawable = getDrawable(view, funcName, drawableID);
                BaseTypeSafteyMatcher<Drawable> strokeMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameStrokeWidth(expectedValue);
                boolean matches = strokeMatcher.matches(drawable);
                setValues(strokeMatcher.actual, strokeMatcher.expected);
                return matches;
            }
        };
    }

    public static Matcher<View> isSameRippleRadius(final String funcName, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!UIDUtils.isMinLollipop()) {
                    return true; //RippleDrawable not supported before 5.0
                }

                Drawable drawable = getDrawable(view, funcName, -1);
                BaseTypeSafteyMatcher<Drawable> radMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameRippleRadius(expectedValue);
                boolean matches = radMatcher.matches(drawable);
                setValues(radMatcher.actual, radMatcher.expected);
                return matches;
            }
        };
    }

    public static Matcher<View> isSameRippleColor(final String funcName, final int attr, final int expectedValue) {
        return new BaseTypeSafteyMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!UIDUtils.isMinLollipop()) {
                    return true; //RippleDrawable not supported before 5.0
                }

                Drawable drawable = getDrawable(view, funcName, -1);
                BaseTypeSafteyMatcher<Drawable> colorMatcher = (BaseTypeSafteyMatcher<Drawable>) DrawableMatcher.isSameRippleColor(attr, expectedValue);
                boolean matches = colorMatcher.matches(drawable);
                setValues(colorMatcher.actual, colorMatcher.expected);
                return matches;
            }
        };
    }
}
