package com.philips.cdp.uikit.hamburger;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.philips.cdp.uikit.R;
import com.philips.cdp.uikit.costumviews.VectorDrawableImageView;
import com.philips.cdp.uikit.drawable.VectorDrawable;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class HamburgerUtil {

    private Context context;

    public HamburgerUtil(Context context) {
        this.context = context;
    }

    public void moveDrawerToTop(DrawerLayout drawer) {
        Activity activity = (Activity) context;
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.frame_container);
        container.addView(child, 0);
        decor.addView(drawer);
    }

    public int getStatusBarHeight() {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }

    public void updateSmartFooter(final ListView drawerListView, final VectorDrawableImageView footerImage) {
        drawerListView.post(new Runnable() {
            @Override
            public void run() {
                int heightPixels = getDeviceHeightPixels();
                int adaptorTotalHeight = getAdaptorTotalHeight(drawerListView);
                validateLogoView(heightPixels, adaptorTotalHeight, context, footerImage, drawerListView);
            }
        });
    }

    private void validateLogoView(final int heightPixels, final int adaptorTotalHeight, Context context, VectorDrawableImageView footerImage, ListView drawerListView) {
        if (adaptorTotalHeight > heightPixels) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = vi.inflate(R.layout.uikit_footer_view, null);
            VectorDrawableImageView vectorDrawableImageView = (VectorDrawableImageView) view.findViewById(R.id.hamburger_logo);
            setLogoCenterWithMargins(vectorDrawableImageView);
            drawerListView.addFooterView(view, null, false);
            setVectorImage(vectorDrawableImageView);
        } else {
            footerImage.setVisibility(View.VISIBLE);
        }
    }

    private int getDeviceHeightPixels() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity) context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getAdaptorTotalHeight(ListView drawerListView) {
        if (drawerListView != null && drawerListView.getAdapter().getCount() != 0) {
            double listViewItemHeight = context.getResources().getDimension(R.dimen.uikit_hamburger_list_item_height);
            return (int) (drawerListView.getAdapter().getCount() * listViewItemHeight);
        }
        return 0;
    }

    public void setVectorImage(final VectorDrawableImageView vectorDrawableImageView) {
        int resID = R.drawable.uikit_philips_logo;
        vectorDrawableImageView.setImageDrawable(VectorDrawable.create(context, resID));
    }

    private void setLogoCenterWithMargins(final VectorDrawableImageView vectorDrawableImageView) {
        Resources resources = context.getResources();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) resources.getDimension(R.dimen.uikit_hamburger_logo_width), (int) resources.getDimension(R.dimen.uikit_hamburger_logo_height));
        lp.setMargins(0, (int) resources.getDimension(R.dimen.uikit_hamburger_menu_logo_top_margin), 0, (int) resources.getDimension(R.dimen.uikit_hamburger_menu_logo_bottom_margin));
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        vectorDrawableImageView.setLayoutParams(lp);
    }

}
