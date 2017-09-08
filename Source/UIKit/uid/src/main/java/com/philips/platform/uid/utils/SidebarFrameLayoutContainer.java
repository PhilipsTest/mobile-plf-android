/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 *
 */
package com.philips.platform.uid.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.philips.platform.uid.R;
import com.philips.platform.uid.thememanager.ThemeUtils;

public class SidebarFrameLayoutContainer extends FrameLayout {

    public SidebarFrameLayoutContainer(@NonNull final Context context) {
        super(context);
    }

    public SidebarFrameLayoutContainer(@NonNull final Context context, @NonNull final AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SidebarFrameLayoutContainer(@NonNull final Context context, @NonNull final AttributeSet attrs, final int defStyleAttr) {
        super(setThemedContext(context, attrs), attrs, defStyleAttr);
        /*TypedArray bgColorTypedArray = context.obtainStyledAttributes(attrs, new int[]{R.attr.uidNavigationPrimaryBackgroundColor});
        int bgColorResourceId = bgColorTypedArray.getInt(0, 0);
        bgColorTypedArray.recycle();
        setBackgroundColor(bgColorResourceId);*/
    }

    private static Context setThemedContext(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, new int[]{R.attr.uidSidebarContextType});
        int resourceId = typedArray.getInt(0, 0);
        typedArray.recycle();
        TypedArray bgColorTypedArray = context.obtainStyledAttributes(attrs, new int[]{R.attr.uidContentPrimaryBackgroundColor});
        int bgColorResourceId = bgColorTypedArray.getInt(0, 0);
        bgColorTypedArray.recycle();
        switch (resourceId){
            case 0:

                //setBackgroundColor(bgColorResourceId);//getres(R.styleable.PhilipsUID_uidContentPrimaryBackgroundColor, context.getTheme()));
                return ThemeUtils.getContentThemedContext(context);
            case 1: return ThemeUtils.getNavigationThemedContext(context);
            default: return context;
        }
    }
}