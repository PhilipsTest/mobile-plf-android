/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.response.addresses.Addresses;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.uid.view.widget.RadioButton;

import java.util.List;

public class AddressSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Addresses> mAddresses;

    private int mSelectedIndex=0; //As Oth position is taken by header
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;


    public AddressSelectionAdapter(final List<Addresses> addresses) {
        mAddresses = addresses;
        mSelectedIndex = 0; //As Oth position is taken by header
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {

            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_address_selection_item, parent, false);
                return new AddressSelectionHolder(view);

            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_address_selection_footer, parent, false);
                return new AddressSelectionFooter(view);
            default:

        }
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mAddresses.size()+1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder == null) return;

        if (holder instanceof AddressSelectionHolder) {
            Addresses address = mAddresses.get(position);
            AddressSelectionHolder addressSelectionHolder = (AddressSelectionHolder) holder;
            addressSelectionHolder.tvToggle.setText(address.getFirstName() + " " + address.getLastName());
            addressSelectionHolder.address.setText(Utility.formatAddress(address.getFormattedAddress() + "\n" + address.getCountry().getName()));
            updatePaymentButtonsVisibility(addressSelectionHolder.paymentOptions,addressSelectionHolder.delete, position);
            setToggleStatus(addressSelectionHolder.toggle, position);
            bindToggleButton(addressSelectionHolder, addressSelectionHolder.toggle);
            bindDeliverToThisAddress(addressSelectionHolder.deliverToThisAddress);

            if(mAddresses.size()==1)addressSelectionHolder.delete.setEnabled(false);
            addressSelectionHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventHelper.getInstance().notifyEventOccurred(IAPConstant.ADDRESS_SELECTION_EVENT_DELETE);
                }
            });

            addressSelectionHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventHelper.getInstance().notifyEventOccurred(IAPConstant.ADDRESS_SELECTION_EVENT_EDIT);
                }
            });
        }

    }

    private void bindAddNewAddress(final View newAddress) {
        newAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                EventHelper.getInstance().notifyEventOccurred(IAPConstant.ADD_NEW_ADDRESS);
            }
        });
    }

    private void bindDeliverToThisAddress(Button deliver) {
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                EventHelper.getInstance().notifyEventOccurred(IAPConstant.DELIVER_TO_THIS_ADDRESS);
            }
        });
    }

    private void updatePaymentButtonsVisibility(final ViewGroup paymentOptions,final Button dleteButton, final int position) {
        if (mSelectedIndex == position) {
            paymentOptions.setVisibility(View.VISIBLE);
            if(this.getItemCount()== 1){
                dleteButton.setEnabled(false);
            }
        } else {
            paymentOptions.setVisibility(View.GONE);
        }
    }

    private void setToggleStatus(final RadioButton toggle, final int position) {
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



    public void setAddresses(final List<Addresses> data) {
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
            tvToggle=(TextView)view.findViewById(R.id.tv_rbtn_toggle);
            paymentOptions = (ViewGroup) view.findViewById(R.id.payment_options);
            deliverToThisAddress = (Button) view.findViewById(R.id.btn_deliver_to_this_address);
            edit=(Button)view.findViewById(R.id.btn_edit_address);
            delete=(Button)view.findViewById(R.id.btn_delete_address);
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

        if(isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private class AddressSelectionFooter extends RecyclerView.ViewHolder {

        public AddressSelectionFooter(View view) {
            super(view);
            bindAddNewAddress(view);
        }
    }
}