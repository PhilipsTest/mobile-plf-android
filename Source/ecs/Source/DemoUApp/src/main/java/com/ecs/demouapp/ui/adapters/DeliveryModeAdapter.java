/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.screens.OnSetDeliveryModeListener;
import com.philips.platform.ecs.model.address.DeliveryCost;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;

import java.util.List;


public class DeliveryModeAdapter extends RecyclerView.Adapter<DeliveryModeAdapter.DeliverySelectionHolder> {

    private List<ECSDeliveryMode> mModes;
    private OnSetDeliveryModeListener mListener;

    public DeliveryModeAdapter(final List<ECSDeliveryMode> modes,
                               OnSetDeliveryModeListener listener) {
        mModes = modes;
        mListener = listener;
    }

    @Override
    public DeliverySelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.ecs_delivery_mode_spinner_item, null);
        return new DeliverySelectionHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliverySelectionHolder holder, int position) {

        ECSDeliveryMode modes = mModes.get(position);
        if (modes.getName() != null && !modes.getName().equals(""))
            holder.deliveryModeName.setText(modes.getName());
        holder.deliveryModeDescription.setText(modes.getDescription());

        // mSelectedIndex = 0;
        //holder.deliveryRadioBtnToggle.setChecked(true);

        //TODO :Cost is not in server response so value setting to 0.0.Report to Hybris.
        DeliveryCost deliveryCost = modes.getDeliveryCost();
        if (deliveryCost != null) {
            String cost = deliveryCost.getFormattedValue();
            holder.deliveryModePrice.setText(cost);
        } else {
            holder.deliveryModePrice.setText("0.0");
        }

        changeConfirmAndRadioButtonState(holder, modes.isSelected());

        holder.iap_delivery_mode_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                markDeliveryModeSelected(holder);
                notifyDataSetChanged();
            }
        });

        holder.deliveryConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartModelContainer.getInstance().setDeliveryModes(mModes);
                mListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    private void markDeliveryModeSelected(RecyclerView.ViewHolder holder) {
        ECSDeliveryMode selectedDeliveryMode = mModes.get(holder.getAdapterPosition());
        selectedDeliveryMode.setSelected(true);

        for (int i = 0; i < mModes.size(); i++) {

            if (i != holder.getAdapterPosition()) {
                mModes.get(i).setSelected(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mModes.size();
    }

    public class DeliverySelectionHolder extends RecyclerView.ViewHolder {

        private TextView deliveryModeName;
        private TextView deliveryModeDescription;
        private TextView deliveryModePrice;
        private Button deliveryConfirmBtn;
        private RadioButton deliveryRadioBtnToggle;
        private LinearLayout iap_delivery_mode_select;


        public DeliverySelectionHolder(View view) {
            super(view);
            deliveryModeName = (TextView) view.findViewById(R.id.iap_title_ups_parcel);
            deliveryModeDescription = (TextView) view.findViewById(R.id.iap_available_time);
            deliveryModePrice = (TextView) view.findViewById(R.id.iap_delivery_parcel_amount);
            deliveryConfirmBtn = (Button) view.findViewById(R.id.iap_delivery_confirm_btn);
            deliveryRadioBtnToggle = (RadioButton) view.findViewById(R.id.iap_ups_parcel_radio_btn);
            iap_delivery_mode_select = (LinearLayout) view.findViewById(R.id.iap_delivery_mode_select);
        }

    }
    private void changeConfirmAndRadioButtonState(DeliverySelectionHolder holder, boolean isEnable) {

        if (isEnable) {
            holder.deliveryRadioBtnToggle.setChecked(true);
            holder.deliveryConfirmBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deliveryRadioBtnToggle.setChecked(false);
            holder.deliveryConfirmBtn.setVisibility(View.GONE);
        }
    }
}