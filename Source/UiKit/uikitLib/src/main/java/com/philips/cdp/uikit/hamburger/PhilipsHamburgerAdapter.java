package com.philips.cdp.uikit.hamburger;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.uikit.R;
import com.philips.cdp.uikit.com.philips.cdp.uikit.utils.OnDataNotified;
import com.philips.cdp.uikit.costumviews.VectorDrawableImageView;
import com.philips.cdp.uikit.drawable.VectorDrawable;

import java.util.ArrayList;

public class PhilipsHamburgerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HamburgerItem> hamburgerItems;
    private OnDataNotified onDataNotified;

    public PhilipsHamburgerAdapter(Context context, ArrayList<HamburgerItem> hamburgerItems) {
        this.context = context;
        this.hamburgerItems = hamburgerItems;
    }

    public void setOnDataNotified(OnDataNotified onDataNotified) {
        this.onDataNotified = onDataNotified;
    }

    @Override
    public int getCount() {
        return hamburgerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return hamburgerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.uikit_drawer_list_item, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.imgIcon = (VectorDrawableImageView) convertView.findViewById(R.id.hamburger_list_icon);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.hamburger_item_text);
            viewHolder.txtCount = (TextView) convertView.findViewById(R.id.list_counter);
            convertView.setTag(viewHolder);
            addStates(convertView);
            addStatesToText(viewHolder.txtTitle);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        setValuesToViews(position, viewHolder.imgIcon, viewHolder.txtTitle, viewHolder.txtCount);
        notifyCounter();
        return convertView;
    }

    private void setValuesToViews(final int position, final VectorDrawableImageView imgIcon, final TextView txtTitle, final TextView txtCount) {
        int icon = hamburgerItems.get(position).getIcon();
        setImageView(imgIcon, icon, txtTitle);
        txtTitle.setText(hamburgerItems.get(position).getTitle());
        String count = hamburgerItems.get(position).getCount();
        setTextView(txtCount, count);
    }

    private void setTextView(final TextView txtCount, final String count) {
        if (count != null && !count.equals("0")) {
            txtCount.setText(count);
        } else {
            txtCount.setVisibility(View.GONE);
        }
    }

    private void setImageView(final VectorDrawableImageView imgIcon, final int icon, TextView txtTitle) {
        if (icon > 0) {
            imgIcon.setImageDrawable(VectorDrawable.create(context, icon));
        } else {
            imgIcon.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) txtTitle.getLayoutParams();
            layoutParams.leftMargin = (int) context.getResources().getDimension(R.dimen.uikit_hamburger_item_title_left_margin);
            txtTitle.setLayoutParams(layoutParams);
        }
    }

    @SuppressWarnings("deprecation")
    //we need to support API lvl 14+, so cannot change to context.setBackgroundDrawable(): sticking with deprecated API for now
    private void addStates(View convertView) {
        StateListDrawable states = new StateListDrawable();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.brightColor, R.attr.baseColor});
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(typedArray.getColor(0, -1)));
        states.addState(new int[]{},
                new ColorDrawable(typedArray.getColor(1, -1)));
        convertView.setBackgroundDrawable(states);
    }

    @SuppressWarnings("deprecation")
    //we need to support API lvl 14+, so cannot change to context.getColor(): sticking with deprecated API for now
    private void addStatesToText(TextView txtTitle) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[]{R.attr.veryLightColor});
        int[][] states = new int[][]{new int[]{android.R.attr.state_activated}, new int[]{android.R.attr.state_pressed}, new int[]{-android.R.attr.state_activated}};
        int[] colors = new int[]{context.getResources().getColor(android.R.color.white), context.getResources().getColor(android.R.color.white), typedArray.getColor(0, -1)};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        txtTitle.setTextColor(colorStateList);
    }

    public String getCounter() {
        int counter = 0;
        for (HamburgerItem hamburgerItem : hamburgerItems) {
            counter += Integer.parseInt(hamburgerItem.getCount());
        }
        return String.valueOf(counter);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        notifyCounter();
    }

    private void notifyCounter() {
        if (onDataNotified != null)
            onDataNotified.onDataSetChanged(getCounter());
    }

    static class ViewHolderItem {
        VectorDrawableImageView imgIcon;
        TextView txtTitle;
        TextView txtCount;
    }
}
