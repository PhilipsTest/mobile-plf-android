/*
 * Copyright © 2016 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.bluelib.demouapp.fragment.connect;

import android.content.Context;
import android.view.View;

import com.philips.cdp2.bluelib.demouapp.R;
import com.philips.cdp2.bluelib.demouapp.fragment.device.BaseDeviceAdapter;
import com.philips.pins.shinelib.SHNDevice;
import com.philips.pins.shinelib.SHNDeviceFoundInfo;

import java.util.List;
import java.util.Locale;

public class ConnectDeviceAdapter extends BaseDeviceAdapter<SHNDeviceFoundInfo> {

    public ConnectDeviceAdapter(List<SHNDeviceFoundInfo> items) {
        super(items);
    }

    @Override
    public void onBindViewHolder(BaseDeviceAdapter.DeviceViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();

        final SHNDeviceFoundInfo deviceFoundInfo = mItems.get(position);
        final SHNDevice device = deviceFoundInfo.getShnDevice();

        holder.rssiView.setVisibility(View.VISIBLE);
        holder.addressView.setVisibility(View.VISIBLE);

        holder.rssiView.setText(String.format(Locale.US, context.getString(R.string.bll_device_detail_rssi), deviceFoundInfo.getRssi()));
        holder.nameView.setText(device.getName() == null ? context.getString(R.string.bll_unknown) : device.getName());
        holder.addressView.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public SHNDeviceFoundInfo getItem(int position) {
        return mItems.get(position);
    }
}
