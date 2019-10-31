/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.products.ProductCatalogData;
import com.philips.cdp.di.iap.session.NetworkImageLoader;
import com.philips.cdp.di.iap.stock.IAPStockAvailabilityHelper;
import com.philips.cdp.di.iap.utils.IAPConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY_VIEW = 10;
    private final ImageLoader mImageLoader;
    private Context mContext;

    private List<ProductCatalogData> mProductCatalogList;
    private List<ProductCatalogData> mProductCatalogListToFilter;
    private ProductCatalogData mSelectedProduct;
    private String mCharacterText = "";

    private boolean isSearchFocused() {
        return isSearchFocused;
    }

    public void setSearchFocused(boolean searchFocused) {
        isSearchFocused = searchFocused;
    }

    private boolean isSearchFocused = false;

    public ProductCatalogAdapter(Context pContext, List<ProductCatalogData> mProductCatalogList) {
        mContext = pContext;
        this.mProductCatalogList = mProductCatalogList;
        mProductCatalogListToFilter = mProductCatalogList;
        mImageLoader = NetworkImageLoader.getInstance(mContext).getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == EMPTY_VIEW && isSearchFocused()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
            EmptyViewHolder evh = new EmptyViewHolder(v);
            return evh;
        }

        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.iap_product_catalog_item, parent, false);
        return new ProductCatalogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof EmptyViewHolder) {

            if (isSearchFocused()) {
                CharSequence text = String.format((mContext.getResources().getText(R.string.iap_zero_results_found)).toString(), mCharacterText);
                ((EmptyViewHolder) holder).tvEmptyMsg.setText(text);
                mCharacterText = "";
                return;
            } else {
                return;
            }

        }

        ProductCatalogData productCatalogData = mProductCatalogList.get(holder.getAdapterPosition());
        ProductCatalogViewHolder productHolder = (ProductCatalogViewHolder) holder;

        String imageURL = productCatalogData.getImageURL();
        if (imageURL == null) {
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.PRX + productCatalogData.getCtnNumber() + "_" + IAPAnalyticsConstant.NO_THUMB_NAIL_IMAGE);
        }
        String discountedPrice = productCatalogData.getDiscountedPrice();
        String formattedPrice = productCatalogData.getFormattedPrice();

        productHolder.mProductName.setText(productCatalogData.getProductTitle());
        if (imageURL == null) {
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.ERROR, IAPAnalyticsConstant.PRX + productCatalogData.getCtnNumber() + "_" + IAPAnalyticsConstant.PRODUCT_TITLE_MISSING);
        }
        productHolder.mProductImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_icon));
        productHolder.mPrice.setText(formattedPrice);

        IAPStockAvailabilityHelper iapStockAvailabilityHelper = new IAPStockAvailabilityHelper();
        final boolean stockAvailable = iapStockAvailabilityHelper.isStockAvailable(productCatalogData.getStockLevelStatus(), productCatalogData.getStockLevel());

        if(stockAvailable){
            productHolder.mProductOutOfStock.setVisibility(View.GONE);
        }else {
           // productHolder.mProductOutOfStock.setText(mContext.getString(R.string.iap_out_of_stock));
            productHolder.mProductOutOfStock.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_60));
        }

        if (discountedPrice == null || discountedPrice.equalsIgnoreCase("")) {
            productHolder.mDiscountedPrice.setVisibility(View.GONE);
           // productHolder.mPrice.setTextColor(Utility.getThemeColor(mContext));
        } else if (formattedPrice != null && discountedPrice.equalsIgnoreCase(formattedPrice)) {
            productHolder.mPrice.setVisibility(View.GONE);
            productHolder.mDiscountedPrice.setVisibility(View.VISIBLE);
            productHolder.mDiscountedPrice.setText(discountedPrice);
        } else {
            productHolder.mDiscountedPrice.setVisibility(View.VISIBLE);
            productHolder.mDiscountedPrice.setText(discountedPrice);
            productHolder.mPrice.setPaintFlags(productHolder.mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        getNetworkImage(productHolder, imageURL);

        // For arabic, Hebrew and Perssian the back arrow change from left to right
        if((Locale.getDefault().getLanguage().contentEquals("ar")) || (Locale.getDefault().getLanguage().contentEquals("fa")) || (Locale.getDefault().getLanguage().contentEquals("he"))) {
            productHolder.mArrow.setRotation(180);
        }


        productHolder.mArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                setTheProductDataForDisplayingInProductDetailPage(holder.getAdapterPosition());
            }
        });
    }

    private void setTheProductDataForDisplayingInProductDetailPage(int position) {
        mSelectedProduct = mProductCatalogList.get(position);
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_LAUNCH_PRODUCT_DETAIL);
    }

    public ProductCatalogData getTheProductDataForDisplayingInProductDetailPage() {
        return mSelectedProduct;
    }

    private void getNetworkImage(final ProductCatalogViewHolder productCartProductHolder,
                                 final String imageURL) {
        if(imageURL == null ){
            productCartProductHolder.mProductImage.setDefaultImageResId(R.drawable.no_icon);
            return;
        }

        mImageLoader.get(imageURL, ImageLoader.getImageListener(productCartProductHolder.mProductImage,
                R.drawable.no_icon, android.R.drawable
                        .ic_dialog_alert));
        productCartProductHolder.mProductImage.setImageUrl(imageURL, mImageLoader);
    }

    @Override
    public int getItemCount() {
        if(mProductCatalogList.size()==0){

            if(isSearchFocused()){
                return 1;
            }else{
                return mProductCatalogList.size();
            }
        }else{
            return mProductCatalogList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mProductCatalogList.size() == 0 && isSearchFocused()) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public void tagProducts() {
        String productPrice = "";
        if (mProductCatalogList.size() != 0) {
            StringBuilder products = new StringBuilder();
            for (int i = 0; i < mProductCatalogList.size(); i++) {
                ProductCatalogData catalogData = mProductCatalogList.get(i);
                if (i > 0) {
                    products = products.append(",");
                }

                if (catalogData.getPriceValue() != null)
                    productPrice = catalogData.getPriceValue();

                products = products.append("Tuscany_Campaign").append(";")
                        .append(catalogData.getProductTitle()).append(";").append(";")
                        .append(productPrice);
            }
            IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                    IAPAnalyticsConstant.PRODUCTS, products.toString());
        }
    }

    public void setData(List<ProductCatalogData> mProductCatalogList) {
        this.mProductCatalogList = mProductCatalogList;
    }

    // Filter Class
    public void filter(String charText) {
        setSearchFocused(true);
        mCharacterText = charText;
        charText = charText.toLowerCase(Locale.getDefault());
        ArrayList<ProductCatalogData> lFilteredProductList = new ArrayList<>();
        if (charText.length() == 0) {
            // Show no product found view
        } else {
            for (ProductCatalogData wp : mProductCatalogListToFilter) {
                if (wp.getProductTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    lFilteredProductList.add(wp);
                }
            }
        }

        setData(lFilteredProductList);
        notifyDataSetChanged();
    }

    private class ProductCatalogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NetworkImageView mProductImage;
        TextView mProductName;
        TextView mProductOutOfStock;
        TextView mPrice;
        TextView mArrow;
        TextView mDiscountedPrice;

        ProductCatalogViewHolder(View itemView) {
            super(itemView);
            mProductImage = itemView.findViewById(R.id.image);
            mProductName = itemView.findViewById(R.id.iap_retailerItem_productName_lebel);
            mProductOutOfStock = itemView.findViewById(R.id.iap_retaileritem_product_outOfStock_label);
            mPrice = itemView.findViewById(R.id.iap_retailerItem_price_lebel);
            mArrow = itemView.findViewById(R.id.iap_product_catalog_arrow);
            mDiscountedPrice = itemView.findViewById(R.id.iap_productCatalogItem_discountedPrice_lebel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            boolean isSelected = this.itemView.isSelected();
            this.itemView.setSelected(!isSelected);
            this.itemView.setBackgroundColor(isSelected ? Color.TRANSPARENT : ContextCompat.getColor(this.itemView.getContext(), R.color.uid_list_item_background_selector));
            setTheProductDataForDisplayingInProductDetailPage(getAdapterPosition());
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmptyMsg;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            tvEmptyMsg = itemView.findViewById(R.id.tv_empty_list_found);
        }
    }

}


