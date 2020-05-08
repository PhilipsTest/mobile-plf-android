package com.ecs.demouapp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.session.NetworkImageLoader;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.philips.platform.ecs.model.retailers.ECSRetailer;


import java.util.ArrayList;

public class BuyFromRetailersAdapter extends RecyclerView.Adapter<BuyFromRetailersAdapter.RetailerViewHolder> {
    private  Context mContext;
    private  FragmentManager mFragmentManager;
    private  ImageLoader mImageLoader;
    private ArrayList<ECSRetailer> mStoreList;
    private BuyFromRetailersListener mBuyFromRetailersListener;

    public interface BuyFromRetailersListener {
        void onClickAtRetailer(String buyURL, ECSRetailer storeEntity);
    }

    public BuyFromRetailersAdapter(Context context, FragmentManager fragmentManager,
                                   ArrayList<ECSRetailer> storeList, BuyFromRetailersListener pBuyFromRetailersListener) {
        mContext = context;
        mFragmentManager = fragmentManager;
        mStoreList = storeList;
        mBuyFromRetailersListener = pBuyFromRetailersListener;
    }

    @Override
    public RetailerViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.ecs_retailer_item, null);
        return new RetailerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RetailerViewHolder holder, final int position) {
        final ECSRetailer storeEntity = mStoreList.get(position);
        String imageURL = storeEntity.getLogoURL();
        holder.mStoreName.setText(storeEntity.getName());
        holder.mLogo.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_icon));
        String productAvailability = storeEntity.getAvailability();

        if (productAvailability.equalsIgnoreCase("yes")) {
            holder.mProductAvailability.setText(mContext.getString(R.string.iap_in_stock));
            holder.mProductAvailability.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_green_level_60));
        } else {
            holder.mProductAvailability.setText(mContext.getString(R.string.iap_out_of_stock));
            holder.mProductAvailability.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_60));
        }

        final String buyURL = storeEntity.getBuyURL()+"source = mobile";
        holder.mArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (!NetworkUtility.getInstance().isNetworkAvailable(connectivityManager)) {
                    NetworkUtility.getInstance().showErrorDialog(mContext, mFragmentManager,
                            mContext.getString(R.string.iap_ok), mContext.getString(R.string.iap_you_are_offline), mContext.getString(R.string.iap_no_internet));
                } else {
                    tagOnSelectRetailer(storeEntity);
                    mBuyFromRetailersListener.onClickAtRetailer(buyURL, storeEntity);
                }
            }
        });

        getNetworkImage(holder, imageURL);
    }

    void tagOnSelectRetailer(ECSRetailer storeEntity) {
        ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA, ECSAnalyticsConstant.RETAILER_SELECTED,
                storeEntity.getName());
    }

    private void getNetworkImage(final RetailerViewHolder retailerHolder, final String imageURL) {
        mImageLoader = NetworkImageLoader.getInstance(mContext)
                .getImageLoader();
        mImageLoader.get(imageURL, ImageLoader.getImageListener(retailerHolder.mLogo,
                R.drawable.no_icon, android.R.drawable
                        .ic_dialog_alert));
        retailerHolder.mLogo.setImageUrl(imageURL, mImageLoader);
    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }

    public class RetailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NetworkImageView mLogo;
        TextView mStoreName;
        TextView mProductAvailability;
        TextView mArrow;

        public RetailerViewHolder(View itemView) {
            super(itemView);
            mLogo = itemView.findViewById(R.id.iap_retailer_image);
            mStoreName = itemView.findViewById(R.id.iap_retailerItem_onlineStoreName_lebel);
            mProductAvailability = itemView.findViewById(R.id.iap_retailerItem_onlineStoreAvailability_lebel);
            mArrow = itemView.findViewById(R.id.retailer_arrow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!NetworkUtility.getInstance().isNetworkAvailable(connectivityManager)) {
                NetworkUtility.getInstance().showErrorDialog(mContext, mFragmentManager, mContext.getString(R.string.iap_ok), mContext.getString(R.string.iap_you_are_offline), mContext.getString(R.string.iap_no_internet));
            } else {
                final String buyURL = mStoreList.get(getAdapterPosition()).getBuyURL();
                ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA,
                        ECSAnalyticsConstant.RETAILER_SELECTED,
                        mStoreList.get(getAdapterPosition()).getName());
                boolean isSelected = this.itemView.isSelected();
                this.itemView.setSelected(!isSelected);
                this.itemView.setBackgroundColor(isSelected ? Color.TRANSPARENT : ContextCompat.getColor(this.itemView.getContext(), R.color.uid_list_item_background_selector));
                mBuyFromRetailersListener.onClickAtRetailer(buyURL, mStoreList.get(getAdapterPosition()));
            }
        }
    }

}
