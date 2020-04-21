/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.uid.view.widget.RadioButton;

import java.util.List;

public class AddressSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ECSAddress> mAddresses;

    private int mSelectedIndex = 0; //As Oth position is taken by header
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context mContext;
    private String mJanRainEmail;

    public AddressSelectionAdapter(final List<ECSAddress> addresses, String mJanRainEmail) {
        mAddresses = addresses;
        mSelectedIndex = 0; //As Oth position is taken by header
        this.mJanRainEmail=mJanRainEmail;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        mContext = parent.getContext();

        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_address_selection_item, parent, false);
            return new AddressSelectionHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_address_selection_footer, parent, false);
            return new AddressSelectionFooter(view);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mAddresses.size() + 1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder == null) return;

        if (holder instanceof AddressSelectionHolder) {
            ECSAddress address = mAddresses.get(position);
            AddressSelectionHolder addressSelectionHolder = (AddressSelectionHolder) holder;
            addressSelectionHolder.tvToggle.setText(address.getFirstName() + " " + address.getLastName());
            AddressFields selectedAddress = Utility.prepareAddressFields(address, mJanRainEmail);
            addressSelectionHolder.address.setText(Utility.getAddressToDisplay(selectedAddress));
            updatePaymentButtonsVisibility(addressSelectionHolder.paymentOptions, addressSelectionHolder.delete, position);
            setToggleStatus(addressSelectionHolder.toggle, position);
            bindToggleButton(addressSelectionHolder, addressSelectionHolder.toggle);
            bindDeliverToThisAddress(addressSelectionHolder.deliverToThisAddress);

            if (mAddresses.size() == 1) addressSelectionHolder.delete.setEnabled(false);
            addressSelectionHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventHelper.getInstance().notifyEventOccurred(ECSConstant.ADDRESS_SELECTION_EVENT_DELETE);
                }
            });

            addressSelectionHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventHelper.getInstance().notifyEventOccurred(ECSConstant.ADDRESS_SELECTION_EVENT_EDIT);
                }
            });
        }

    }

    void bindAddNewAddress(final View newAddress) {
        newAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                EventHelper.getInstance().notifyEventOccurred(ECSConstant.ADD_NEW_ADDRESS);
            }
        });
    }

    void bindDeliverToThisAddress(Button deliver) {
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                EventHelper.getInstance().notifyEventOccurred(ECSConstant.DELIVER_TO_THIS_ADDRESS);
            }
        });
    }

    void updatePaymentButtonsVisibility(final ViewGroup paymentOptions, final Button dleteButton, final int position) {
        if (mSelectedIndex == position) {
            paymentOptions.setVisibility(View.VISIBLE);
            if (this.getItemCount() == 1) {
                dleteButton.setEnabled(false);
            }
        } else {
            paymentOptions.setVisibility(View.GONE);
        }
    }

    void setToggleStatus(final RadioButton toggle, final int position) {
        if (mSelectedIndex == position) {
            toggle.setChecked(true);
        } else {
            toggle.setChecked(false);
        }
    }

    private void bindToggleButton(final AddressSelectionHolder holder, final RadioButton toggle) {
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mSelectedIndex = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    public int getSelectedPosition() {
        return mSelectedIndex;
    }

    public int getOptionsClickPosition() {
        return this.getSelectedPosition();
    }


    public void setAddresses(final List<ECSAddress> data) {
        mSelectedIndex = 0;
        mAddresses = data;
    }

    public class AddressSelectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button deliverToThisAddress;
        TextView address;
        RadioButton toggle;
        TextView tvToggle;
        ViewGroup paymentOptions;
        Button edit;
        Button delete;

        public AddressSelectionHolder(final View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.tv_address);
            toggle = (RadioButton) view.findViewById(R.id.rbtn_toggle);
            tvToggle = (TextView) view.findViewById(R.id.tv_rbtn_toggle);
            paymentOptions = (ViewGroup) view.findViewById(R.id.payment_options);
            deliverToThisAddress = (Button) view.findViewById(R.id.btn_deliver_to_this_address);
            edit = (Button) view.findViewById(R.id.btn_edit_address);
            delete = (Button) view.findViewById(R.id.btn_delete_address);
            if(mContext!=null) {
                delete.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_45));
            }
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedIndex = getAdapterPosition();
            setToggleStatus(toggle, getAdapterPosition());
            notifyDataSetChanged();
        }
    }

    private boolean isPositionFooter(int position) {
        return position == mAddresses.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    class AddressSelectionFooter extends RecyclerView.ViewHolder {

        public AddressSelectionFooter(View view) {
            super(view);
            bindAddNewAddress(view);
        }
    }
}