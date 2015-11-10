package com.philips.cdp.uikit.costumviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.philips.cdp.uikit.R;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class BaseSlider extends SeekBar {

    private Context mcontext;
    private int themeBaseColor;
    private int thumbStrokeWidth;
    private Drawable thumb;


    public BaseSlider(final Context context) {
        super(context);
    }

    public BaseSlider(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        processAttributes();
        init(context);
    }

    public BaseSlider(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext = context;
        processAttributes();
        init(context);
    }

    public BaseSlider(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mcontext = context;
    }

    private void init(Context context) {
        thumb = getShapeDrawable();
        setProgressDrawable(sliderBar());
        setThumb(thumb);
        setThumbOffset(0);
    }





    @SuppressWarnings("deprecation")
    //we need to support API lvl 14+, so cannot change to context.getColor(): sticking with deprecated API for now
    private void processAttributes() {
        TypedArray a = mcontext.getTheme().obtainStyledAttributes(new int[]{R.attr.baseColor});
        themeBaseColor = a.getColor(0, getResources().getColor(R.color.uikit_philips_blue));
        a.recycle();
        thumbStrokeWidth = (int) getResources().getDimension(R.dimen.slider_thumb_stroke_width);
    }

    private LayerDrawable sliderBar() {
        LayerDrawable slidebar = (LayerDrawable) ContextCompat.getDrawable(mcontext, R.drawable.uikit_slider_bar);
        slidebar.getConstantState().newDrawable().mutate();
        ClipDrawable progressbar = (ClipDrawable) slidebar.findDrawableByLayerId(android.R.id.progress);
        ColorFilter progressFilter = new PorterDuffColorFilter(themeBaseColor, PorterDuff.Mode.SRC_ATOP);
        progressbar.setColorFilter(progressFilter);

        return slidebar;
    }
    @SuppressWarnings("deprecation")
    //we need to support API lvl 14+, so cannot change to context.getColor(): sticking with deprecated API for now
    private GradientDrawable getShapeDrawable() {
        Resources resources = getResources();
        final GradientDrawable gradientDrawable = (GradientDrawable) resources.getDrawable(R.drawable.uikit_slider_circle);
        GradientDrawable d = (GradientDrawable) gradientDrawable.getConstantState().newDrawable().mutate();
        int strokeColor = Color.argb(128, Color.red(themeBaseColor), Color.green(themeBaseColor), Color.blue(themeBaseColor));
        d.setStroke(thumbStrokeWidth, strokeColor);
        return d;
    }

    public  Drawable getThumb() {
        return thumb;
    }

}
