package com.philips.cdpp.dicommtestapp.presenters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.cdpp.dicommtestapp.R;
import com.philips.cdpp.dicommtestapp.appliance.PropertyPort;

import nl.rwslinkman.presentable.Presenter;

public class PortOverviewPresenter implements Presenter<PropertyPort, PortOverviewPresenter.ViewHolder>
{
    private int redColor;
    private int blackColor;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        redColor = ContextCompat.getColor(parent.getContext(), android.R.color.holo_red_dark);
        blackColor = ContextCompat.getColor(parent.getContext(), android.R.color.black);

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_port_overview, parent, false);

        ViewHolder viewHolder = new PortOverviewPresenter.ViewHolder(v);
        viewHolder.titleView = (TextView) v.findViewById(R.id.item_port_overview_title);
        viewHolder.subtitleView = (TextView) v.findViewById(R.id.item_port_overview_subtitle);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, PropertyPort item) {
        String subTitle = "Loading...";
        int textColor = blackColor;
        if(!item.getErrorText().isEmpty()) {
            subTitle = item.getErrorText();
            textColor = redColor;
        }
        else if(!item.getStatusText().isEmpty()) {
            subTitle = item.getStatusText();
            textColor = blackColor;
        }

        viewHolder.titleView.setText(item.getPortName());

        viewHolder.subtitleView.setTextColor(textColor);
        viewHolder.subtitleView.setText(subTitle);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView subtitleView;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
