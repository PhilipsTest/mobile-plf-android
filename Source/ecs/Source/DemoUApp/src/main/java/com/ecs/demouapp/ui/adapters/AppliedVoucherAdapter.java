/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.philips.platform.ecs.model.voucher.GetAppliedValue;
import com.philips.platform.ecs.model.voucher.ECSVoucher;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.ArrayList;
import java.util.List;

public class AppliedVoucherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context mContext;
    private Resources mResources;
    private List<ECSVoucher> mData = new ArrayList<>();
    private OutOfStockListener mOutOfStock;
    private UIPicker mPopupWindow;
    private GetAppliedValue shoppingCartDataForProductDetailPage;

    private Drawable countArrow;
    private boolean mIsFreeDelivery;
    private int mSelectedItemPosition = -1;
    private int mQuantityStatus;
    private int mNewCount;

    public interface OutOfStockListener {
        void onOutOfStock(boolean isOutOfStock);
    }

    public AppliedVoucherAdapter(Context context, List<ECSVoucher> vouchers) {
        mContext = context;
        mResources = context.getResources();
        mData = vouchers;
    }


    @Override
    public int getItemViewType(final int position) {
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(final int position) {
        return position == mData.size();
    }
    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_applied_voucher_item, parent, false);
            return new AppliedVoucherViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mData.size() == 0) {
            return;
        }

            final ECSVoucher vouchers = mData.get(holder.getAdapterPosition());

            AppliedVoucherViewHolder appliedVoucherViewHolder = (AppliedVoucherViewHolder) holder;
            appliedVoucherViewHolder.mIapVoucherItemLabel.setText(R.string.iap_voucher_code );
            appliedVoucherViewHolder.mIapDiscountedPrecentageLabel.setText(vouchers.getVoucherCode());
            appliedVoucherViewHolder.mIapDiscountedPriceLabel.setText("- "+vouchers.getAppliedValue().getFormattedValue());



        appliedVoucherViewHolder.mIapCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mSelectedItemPosition = holder.getAdapterPosition();
                    if(mSelectedItemPosition!=-1){
                        EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_DELETE_VOUCHER);
                    }
                }
            });


    }


    public void onStop() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }



     class AppliedVoucherViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mIappVoucherItemLayout;
        RelativeLayout mIapAppliedVocuherLayout;
        Label mIapVoucherItemLabel;
        Label mIapDiscountedPrecentageLabel;
        Label mIapDiscountedPriceLabel;
        Label mIapCross;


         AppliedVoucherViewHolder(View itemView) {
            super(itemView);
            mIapVoucherItemLabel = itemView.findViewById(R.id.iap_voucherItem_label);
            mIapDiscountedPrecentageLabel = itemView.findViewById(R.id.iap_discountedPrecentage_label);
            mIapDiscountedPriceLabel = itemView.findViewById(R.id.iap_discountedPrice_label);
            mIapCross = itemView.findViewById(R.id.iap_cross);
            mIapAppliedVocuherLayout = itemView.findViewById(R.id.iap_applied_vocuher_layout);
            mIappVoucherItemLayout = itemView.findViewById(R.id.iap_voucherItem_layout);
        }
    }

}
