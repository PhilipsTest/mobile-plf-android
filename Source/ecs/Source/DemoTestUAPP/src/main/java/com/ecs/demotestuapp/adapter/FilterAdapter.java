package com.ecs.demotestuapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.model.FilterStateItem;
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel;
import com.philips.platform.uid.view.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends ArrayAdapter<FilterStateItem> {
    private Context mContext;
    private ArrayList<FilterStateItem> listState;
    private FilterAdapter myAdapter;
    private boolean isFromView = false;

    public FilterAdapter(Context context, int resource, List<FilterStateItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<FilterStateItem>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.filter_spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                    listState.get(position).setEcsStockLevel(ECSStockLevel.valueOf(listState.get(position).getTitle()));
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}