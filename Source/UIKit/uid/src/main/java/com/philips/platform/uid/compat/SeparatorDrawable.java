/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 *
 */

package com.philips.platform.uid.compat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;

import com.philips.platform.uid.R;
import com.philips.platform.uid.utils.UIDUtils;

/**
 * The type Divider drawable.
 * This class can be used as utility class for setting divider and and its height
 */
public class SeparatorDrawable extends Drawable {

    private final Paint paint;
    private int height;
    private static final int[] ATTRS = new int[]{android.R.attr.dividerHeight, R.attr.uidSeparatorColor, R.attr.uidSeparatorAlpha};
    public static final int HEIGHT_ATTR_INDEX = 0;
    public static final int SEPARATOR_ATT_INDEX = 1;
    public static final int SEPARATOR_ALPHA_ATTR_INDEX = 2;

    public SeparatorDrawable(@NonNull final Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        this.height = (int) styledAttributes.getDimension(HEIGHT_ATTR_INDEX, 1);
        final int color = styledAttributes.getColor(SEPARATOR_ATT_INDEX, ContextCompat.getColor(context, R.color.uid_gray_level_75));
        final float alpha = styledAttributes.getFloat(SEPARATOR_ALPHA_ATTR_INDEX, 0);
        final int modulateColorAlpha = UIDUtils.modulateColorAlpha(color, alpha);
        paint = new Paint();
        paint.setColor(modulateColorAlpha);
        styledAttributes.recycle();
    }

    @Override
    public void setBounds(final int left, final int top, final int right, final int bottom) {
        super.setBounds(left, top, right, top + height);
    }

    @Override
    public void setBounds(final Rect bounds) {
        bounds.bottom = bounds.top + height;
        super.setBounds(bounds);
    }

    public int getHeight() {
        return height;
    }

    @VisibleForTesting
    public int getColor() {
        return paint.getColor();
    }

    @Override
    public void draw(final Canvas canvas) {
        int saveCount = canvas.save();
        canvas.drawRect(getBounds(), paint);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(final int alpha) {

    }

    @Override
    public void setColorFilter(final ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
