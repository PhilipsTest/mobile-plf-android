package com.philips.cdp.uikit.com.philips.cdp.uikit.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philips.cdp.uikit.R;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class TabUtils {

    private int selectedColor;
    //Provide theeming facility for text color.
    private int enabledColor;
    private Context context;
    private TabLayout tabLayout;
    ColorFilter enabledFilter;
    ColorFilter selectedFilter;

    public TabUtils(Context context, TabLayout tabLayout, boolean withIcon) {
        this.context = context;
        this.tabLayout = tabLayout;
        initSelectionColors();
        initIconColorFilters();
    }

    public TabLayout.Tab newTab(int titleResID, int imageDrawable, final int badgeCount) {
        TabLayout.Tab newTab = tabLayout.newTab();
        View customView;
        if (imageDrawable > 0) {
            customView = LayoutInflater.from(context).inflate(R.layout.uikit_tab_with_image, null);
            //Set icon for the tab
            ImageView iconView = (ImageView) customView.findViewById(R.id.tab_icon);
            Drawable d = ResourcesCompat.getDrawable(context.getResources(), imageDrawable, null);
            iconView.setImageDrawable(getTabIconSelector(d));
            iconView.setVisibility(View.VISIBLE);
        } else {
            customView = LayoutInflater.from(context).inflate(R.layout.uikit_tab_textonly, null);
        }

        //We must do this to get the full tab view width
        customView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //Set title text
        TextView title = (TextView) customView.findViewById(R.id.tab_title);
        if (titleResID <= 0) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(titleResID);
        }
        title.setTextColor(getTextSelector());

        //Hide divider for the first view
        if (tabLayout.getTabCount() == 0) {
            customView.findViewById(R.id.tab_divider).setVisibility(View.GONE);
        }
        newTab.setCustomView(customView);
        return newTab;
    }

    public void setIcon(TabLayout.Tab tab, Drawable drawable, boolean useTheme) {
        ImageView iconView = (ImageView) tab.getCustomView().findViewById(R.id.tab_icon);
        Drawable target = useTheme ? getTabIconSelector(drawable) : drawable;
        iconView.setImageDrawable(target);
        iconView.setVisibility(View.VISIBLE);
    }

    public void setTitle(TabLayout.Tab tab, String title) {
        TextView titleView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
        titleView.setText(title);
        titleView.setVisibility(View.VISIBLE);
    }

    public void setTitle(TabLayout.Tab tab, int resID) {
        TextView titleView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
        titleView.setText(resID);
        titleView.setVisibility(View.VISIBLE);
    }

    public static void adjustTabs(final TabLayout tabLayout, final Context context) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                //Measure width of 1st child. HSView by default has full screen view
                int tabLayoutWidth = ((ViewGroup) tabLayout.getChildAt(0)).getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(metrics);
                int windowWidth = metrics.widthPixels;

                boolean isTablet = context.getResources().getBoolean(R.bool.uikit_istablet);
                if (tabLayoutWidth <= windowWidth) {
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    int gravity = isTablet ? TabLayout.GRAVITY_CENTER : TabLayout.GRAVITY_FILL;
                    tabLayout.setTabGravity(gravity);
                } else {
                    if (tabLayout.getTabMode() != TabLayout.MODE_SCROLLABLE)
                        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });
    }

    private ColorStateList getTextSelector() {
        int[][] states = {{android.R.attr.state_selected}, {}};
        int[] colors = {selectedColor, enabledColor};
        return new ColorStateList(states, colors);
    }

    //Focussed color is the base color of the current theme.
    private void initSelectionColors() {
        enabledColor = ContextCompat.getColor(context, R.color.uikit_tab_text_enabled_color);
        //Selected Color
        TypedArray array = context.obtainStyledAttributes(R.styleable.PhilipsUIKit);
        selectedColor = array.getColor(R.styleable.PhilipsUIKit_baseColor, 0);
        array.recycle();
    }

    private void initIconColorFilters() {
        enabledFilter = new PorterDuffColorFilter(enabledColor, PorterDuff.Mode.SRC_ATOP);
        selectedFilter = new PorterDuffColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
    }

    private Drawable getTabIconSelector(Drawable drawable) {
        Drawable enabled = drawable.getConstantState().newDrawable().mutate();
        Drawable selected = drawable.getConstantState().newDrawable().mutate();

        ColorFilterStateListDrawable selector = new ColorFilterStateListDrawable();
        selector.addState(new int[]{android.R.attr.state_selected}, selected, selectedFilter);
        selector.addState(new int[]{}, enabled, enabledFilter);

        return selector;
    }
}