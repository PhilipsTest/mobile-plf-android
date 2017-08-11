/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.response.addresses.DeliveryCost;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;

import java.util.List;

public class DeliveryModeAdapter extends ArrayAdapter<DeliveryModes> {

    private Context mContext;
    private List<DeliveryModes> mModes;

    public DeliveryModeAdapter(final Context context, int txtViewResourceId, final List<DeliveryModes> modes) {
        super(context, txtViewResourceId, modes);
        mContext = context;
        mModes = modes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.iap_delivery_mode_spinner_item, null);

        TextView deliveryModeName = (TextView) view.findViewById(R.id.iap_title_ups_parcel);
        TextView deliveryModeDescription = (TextView) view.findViewById(R.id.iap_available_time);
        TextView deliveryModePrice = (TextView) view.findViewById(R.id.iap_delivery_parcel_amount);

        DeliveryModes modes = mModes.get(position);

        if (modes.getName() != null && !modes.getName().equals(""))
            deliveryModeName.setText(modes.getName());
        deliveryModeDescription.setText(modes.getDescription());

        //TODO :Cost is not in server response so value setting to 0.0.Report to Hybris.
        DeliveryCost deliveryCost = modes.getDeliveryCost();
        if (deliveryCost != null) {
            String cost = deliveryCost.getFormattedValue();
            deliveryModePrice.setText(cost);
        } else {
            deliveryModePrice.setText("0.0");
        }
        return view;
    }
}
