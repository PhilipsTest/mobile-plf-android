package com.philips.platform.pimdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PIMSpinnerAdapter extends ArrayAdapter<String> {


    private List<String> spinnerItems;

    public PIMSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> spinnerItems) {
        super(context, resource, spinnerItems);
        this.spinnerItems = spinnerItems;
    }

    @Override
    public int getPosition(@Nullable String item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        TextView textView =  convertView.findViewById(android.R.id.text1);
        textView.setText(spinnerItems.get(position));
        convertView.setPadding(0, convertView.getPaddingTop(), convertView.getPaddingRight(), convertView.getPaddingBottom());
        return convertView;
    }
}
