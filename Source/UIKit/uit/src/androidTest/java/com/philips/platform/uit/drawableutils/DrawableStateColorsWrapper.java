/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.uit.drawableutils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.philips.platform.uit.utils.UITTestUtils;

public class DrawableStateColorsWrapper extends BaseStateColorsImpl {

    private static final String TINT_COLOR_FIELD = "mTint";

    private Drawable.ConstantState gradientConstantState;

    public DrawableStateColorsWrapper(Drawable drawable) {
        super(drawable);
        gradientConstantState = (Drawable.ConstantState) GradientDrawableUtils.getField(constantState, "mDrawableState");
    }

    @Override
    public int getDefaultColor() {
        return getColorStateList().getDefaultColor();
    }

    @Override
    public int getStateColor(int attr) {
        return getColorStateList().getColorForState(new int[]{attr}, Color.WHITE);
    }

    @Override
    public int getGradientSolidColor() {
        return getColorStateList().getColorForState(new int[]{android.R.attr.state_enabled}, Color.WHITE);
    }

    @Override
    public int getStrokeSolidColor() {
        throw new RuntimeException("Not supported. We should never reach here");
    }

    @Override
    public int getStrokeSolidStateColor(int attr) {
        throw new RuntimeException("Not supported. We should never reach here");
    }

    @Override
    protected Drawable.ConstantState getConstantStateForRadius() {
        return gradientConstantState;
    }

    @Override
    protected Drawable.ConstantState getConstantStateForStrokeWidth() {
        return gradientConstantState;
    }

    private ColorStateList getColorStateList() {
        Drawable.ConstantState wrappedConstantState = constantState;
        if (Build.VERSION.SDK_INT >= 21) {
            wrappedConstantState = UITTestUtils.getWrappedClipDrawableFromReflection(drawable).getConstantState();
        }
        return (ColorStateList) GradientDrawableUtils.getField(wrappedConstantState, TINT_COLOR_FIELD);
    }
}
