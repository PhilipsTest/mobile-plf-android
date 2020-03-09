/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.cart.ShoppingCartData;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.session.NetworkImageLoader;
import com.philips.cdp.di.iap.stock.IAPStockAvailabilityHelper;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.di.iap.view.CountDropDown;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.ArrayList;
import java.util.Locale;

import static com.philips.cdp.di.iap.utils.IAPConstant.IAP_APPLY_VOUCHER;

public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final Context mContext;
    private final Resources mResources;
    private ArrayList<ShoppingCartData> mData = new ArrayList<>();
    private final OutOfStockListener mOutOfStock;
    private UIPicker mPopupWindow;
    private ShoppingCartData shoppingCartDataForProductDetailPage;

    private Drawable countArrow;
    private boolean mIsFreeDelivery;
    private int mSelectedItemPosition = -1;
    private int mQuantityStatus;
    private int mNewCount;
    private FooterShoppingCartViewHolder shoppingCartFooter;

    public interface OutOfStockListener {
        void onOutOfStock(boolean isOutOfStock);
    }

    public ShoppingCartAdapter(Context context, ArrayList<ShoppingCartData> shoppingCartData,
                               OutOfStockListener isOutOfStock) {
        mContext = context;
        mResources = context.getResources();
        mData = shoppingCartData;
        mOutOfStock = isOutOfStock;
    }

    public void setCountArrow(final Context context, final boolean isEnable) {
        if (isEnable) {
            countArrow = context.getDrawable(R.drawable.iap_product_count_drop_down);
            countArrow.setColorFilter(new
                    PorterDuffColorFilter(mContext.getResources().getColor(R.color.uid_quiet_button_icon_selector), PorterDuff.Mode.MULTIPLY));
        } else {
            countArrow = VectorDrawableCompat.create(context.getResources(), R.drawable.iap_product_disable_count_drop_down, mContext.getTheme());
        }
        int width = (int) mResources.getDimension(R.dimen.iap_count_drop_down_icon_width);
        int height = (int) mResources.getDimension(R.dimen.iap_count_drop_down_icon_height);
        countArrow.setBounds(0, 0, width, height);
    }

    @Override
    public int getItemViewType(final int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionFooter(final int position) {
        return position == mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_FOOTER) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_shopping_cart_footer, parent, false);
            return new FooterShoppingCartViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_shopping_cart_data, parent, false);
            return new ShoppingCartProductHolder(v);
        }
        return null;
    }

    private void bindCountView(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ShoppingCartData data = mData.get(position);
                int stockLevel = mData.get(position).getStockLevel();
                if(stockLevel > 50){
                    stockLevel = 50;
                }

                CountDropDown countPopUp = new CountDropDown(v,v.getContext(), stockLevel, data
                        .getQuantity(), new CountDropDown.CountUpdateListener() {
                    @Override
                    public void countUpdate(final int oldCount, final int newCount) {
                        mSelectedItemPosition = position;
                        mQuantityStatus = getQuantityStatus(newCount, oldCount);
                        mNewCount = newCount;
                        EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_UPDATE_PRODUCT_COUNT);
                    }
                });
                countPopUp.createPopUp(v,stockLevel);
                mPopupWindow = countPopUp.getPopUpWindow();
                countPopUp.show();
            }
        });
    }

    public int getNewCount() {
        return mNewCount;
    }

    public int getQuantityStatusInfo() {
        return mQuantityStatus;
    }

    private int getQuantityStatus(int newCount, int oldCount) {
        if (newCount > oldCount) {
            return 1;
        }
        else if (newCount < oldCount) {
            return 0;
        }
        else {
            return -1;
        }
    }

    private void showProductDetails(final int selectedItem) {
        mSelectedItemPosition = selectedItem;
        setTheProductDataForDisplayingInProductDetailPage(selectedItem);
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    private void setTheProductDataForDisplayingInProductDetailPage(int position) {
        shoppingCartDataForProductDetailPage = mData.get(position);
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.PRODUCT_DETAIL_FRAGMENT);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mData.size() == 0)
            return;

        if (holder instanceof ShoppingCartProductHolder) {

            final ShoppingCartData cartData = mData.get(holder.getAdapterPosition());
            ShoppingCartProductHolder shoppingCartProductHolder = (ShoppingCartProductHolder) holder;

            // For arabic, Hebrew and Perssian the back arrow change from left to right
            if((Locale.getDefault().getLanguage().contentEquals("ar")) || (Locale.getDefault().getLanguage().contentEquals("fa")) || (Locale.getDefault().getLanguage().contentEquals("he"))) {
                shoppingCartProductHolder.mIvOptions.setRotation(180);
            }


            if(mData.size()==1 || position==mData.size()-1){
                shoppingCartProductHolder.viewBottomSpace.setVisibility(View.GONE);
            }

            String imageURL = cartData.getImageURL();
            shoppingCartProductHolder.mTvPrice.setText(cartData.getProductTitle());
            shoppingCartProductHolder.mTvQuantity.setText(Integer.toString(cartData.getQuantity()));
            shoppingCartProductHolder.mTvAfterDiscountPrice.setText(cartData.getFormattedPrice());

            checkForOutOfStock(cartData.getStockLevel(), cartData.getQuantity(), shoppingCartProductHolder, cartData.getStockLevelStatus());
            getNetworkImage(shoppingCartProductHolder, imageURL);
            shoppingCartProductHolder.mTvQuantity.setCompoundDrawables(null, null, countArrow, null);
            bindCountView(shoppingCartProductHolder.mQuantityLayout, holder.getAdapterPosition());

            shoppingCartProductHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mSelectedItemPosition = holder.getAdapterPosition();
                    EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_DELETE_PRODUCT_CONFIRM);
                }
            });
        } else {
            //Footer Layout
            shoppingCartFooter = (FooterShoppingCartViewHolder) holder;
            ShoppingCartData data;

            if (mData.get(0) != null) {
                data = mData.get(0);

                shoppingCartFooter.mTotalCost.setText(data.getFormattedTotalPriceWithTax());
                shoppingCartFooter.mVatValue.setText(data.getVatValue());
                if (null != data.getDeliveryMode()) {
                    handleTax(data, shoppingCartFooter);



                   // ==================



                    shoppingCartFooter.mExtraOption.setVisibility(View.VISIBLE);

                    String deliveryCost = mData.get(0).getDeliveryMode().getDeliveryCost().getFormattedValue();
                    String deliveryMethod = mData.get(0).getDeliveryMode().getName();
                    String deliveryModeDescription=mData.get(0).getDeliveryMode().getDescription();

                    if ((deliveryCost.trim()).equalsIgnoreCase("$ 0.00") ) {
                        mIsFreeDelivery = true;
                    }

                    if(Utility.isPromotionRunning())
                    {
                        shoppingCartFooter.mDeliveryModeFree.setText(R.string.iap_free_Delivery);
                        shoppingCartFooter.mDeliveryModeFree.setVisibility(View.VISIBLE);
                    }

                    shoppingCartFooter.mDeliveryPrice.setText(deliveryCost);
                    shoppingCartFooter.mDeliveryPrice.setVisibility(View.VISIBLE);


                    shoppingCartFooter.mDeliveryUpsVal.setText(deliveryCost);
                    shoppingCartFooter.mDeliveryUpsVal.setVisibility(View.VISIBLE);


                    shoppingCartFooter.mDeliveryVia.setVisibility(View.VISIBLE);
                    String freeDeliverySpendOn = mContext.getResources().getString(R.string.iap_delevery_via);
                    freeDeliverySpendOn = String.format(freeDeliverySpendOn, deliveryMethod);
                    shoppingCartFooter.mDeliveryTitle.setText(freeDeliverySpendOn);
                    shoppingCartFooter.mDeliveryTitle.setVisibility(View.VISIBLE);

                    shoppingCartFooter.mDeliveryModeDescription.setText(deliveryModeDescription);
                    shoppingCartFooter.mDeliveryModeDescription.setVisibility(View.VISIBLE);



                    //====================

                    shoppingCartFooter.mDeliveryUPSParcelContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_EDIT_DELIVERY_MODE);
                        }
                    });

                } else {
                    shoppingCartFooter.mDeliveryUPSParcelContainer.setVisibility(View.GONE);
                    mIsFreeDelivery = true;
                }

                shoppingCartFooter.mVoucherContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventHelper.getInstance().notifyEventOccurred(IAP_APPLY_VOUCHER);
                    }
                });

                for (int i = 0; i < mData.size(); i++) {
                    View priceInfo = View.inflate(mContext, R.layout.iap_price_item, null);
                    TextView mProductName = priceInfo.findViewById(R.id.product_name);
                    TextView mProductPrice = priceInfo.findViewById(R.id.product_price);
                    mProductName.setText(Integer.toString(mData.get(i).getQuantity()) + "x " + mData.get(i).getProductTitle().toString());
                    mProductPrice.setText(mData.get(i).getFormattedTotalPrice().toString());
                    shoppingCartFooter.mPriceContainer.addView(priceInfo);
                    //shoppingCartFooter.total_discount.setText("- "+mData.get(i).getTotalDiscounts());
                }


                //Show discounts ==========
                if(mData.get(0).getAppliedOrderPromotionEntityList()!=null) {
                    for (int i = 0; i < mData.get(0).getAppliedOrderPromotionEntityList().size(); i++) {

                        View discountInfo = View.inflate(mContext, R.layout.iap_item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.iap_tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.iap_tv_discount_value);

                        tvDiscountValue.setText("- " + mData.get(0).getAppliedOrderPromotionEntityList().get(i).getPromotion().getPromotionDiscount().getFormattedValue());
                        tvDiscountText.setText(mData.get(0).getAppliedOrderPromotionEntityList().get(i).getPromotion().getDescription());


                        shoppingCartFooter.gridDiscount.addView(discountInfo);
                    }
                }

                if(mData.get(0).getAppliedVouchers()!=null) {
                    for (int i = 0; i < mData.get(0).getAppliedVouchers().size(); i++) {

                        View discountInfo = View.inflate(mContext, R.layout.iap_item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.iap_tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.iap_tv_discount_value);

                        tvDiscountText.setText(mData.get(0).getAppliedVouchers().get(i).getName());
                        tvDiscountValue.setText("- " + mData.get(0).getAppliedVouchers().get(i).getAppliedValue().getFormattedValue());
                        shoppingCartFooter.gridDiscount.addView(discountInfo);
                    }
                }
            }

            if(Utility.isVoucherEnable() ){
                shoppingCartFooter.mExtraOption.setVisibility(View.VISIBLE);
                shoppingCartFooter.mVoucherContainer.setVisibility(View.VISIBLE);
                shoppingCartFooter.mAppliedVoucherCode.setText(R.string.iap_promotional_gift);
            }else{
                shoppingCartFooter.mVoucherContainer.setVisibility(View.GONE);
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductDetails(holder.getAdapterPosition());
            }
        });

    }

    private void handleTax(ShoppingCartData data, FooterShoppingCartViewHolder shoppingCartFooter) {
        if (!data.isVatInclusive()) {
            shoppingCartFooter.mVatValue.setVisibility(View.GONE);
            if (data.getVatValue() != null) {
                shoppingCartFooter.mVatInclusiveValue.setVisibility(View.VISIBLE);
                shoppingCartFooter.mDeliveryUpsVal.setVisibility(View.VISIBLE);
                shoppingCartFooter.mDeliveryUpsVal.setText(data.getVatValue());
            }
        } else {
            if (data.getVatValue() != null) {
                shoppingCartFooter.mVatValue.setVisibility(View.VISIBLE);
                shoppingCartFooter.mVatValue.setText(data.getVatValue());
            }
        }
    }

    private void checkForOutOfStock(int stockLevel, int quantity, ShoppingCartProductHolder shoppingCartProductHolder, String stockLevelStatus) {
        IAPStockAvailabilityHelper iapStockAvailabilityHelper = new IAPStockAvailabilityHelper();
        final boolean isStockAvailable = iapStockAvailabilityHelper.checkIfRequestedQuantityAvailable(stockLevelStatus, stockLevel, quantity);

        setStockAvailability(shoppingCartProductHolder,isStockAvailable, stockLevel);

    }

    private void setStockAvailability(ShoppingCartProductHolder shoppingCartProductHolder, boolean isStockAvailable, int stocklevel) {
        shoppingCartProductHolder.mQuantityLayout.setEnabled(isStockAvailable);
        setCountArrow(mContext, isStockAvailable);
        mOutOfStock.onOutOfStock(isStockAvailable);
        if(!isStockAvailable){
            shoppingCartProductHolder.out_of_stock.setVisibility(View.VISIBLE);
            mOutOfStock.onOutOfStock(false);
            shoppingCartProductHolder.out_of_stock.setText(mContext.getString(R.string.iap_out_of_stock));
            shoppingCartProductHolder.out_of_stock.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_60));

        }
        else{
            shoppingCartProductHolder.out_of_stock.setVisibility(View.GONE);
        }

    }

    private void getNetworkImage(final ShoppingCartProductHolder shoppingCartProductHolder, final String imageURL) {
        ImageLoader mImageLoader;
        mImageLoader = NetworkImageLoader.getInstance(mContext)
                .getImageLoader();

        mImageLoader.get(imageURL, ImageLoader.getImageListener(shoppingCartProductHolder.mNetworkImage,
                R.drawable.no_icon, android.R.drawable
                        .ic_dialog_alert));
        shoppingCartProductHolder.mNetworkImage.setImageUrl(imageURL, mImageLoader);
    }

    public void onStop() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public ShoppingCartData getTheProductDataForDisplayingInProductDetailPage() {
        return shoppingCartDataForProductDetailPage;
    }


    public void setTheProductDataForDisplayingInProductDetailPage(ShoppingCartData shoppingCartData) {
        shoppingCartDataForProductDetailPage=shoppingCartData;
    }

    public boolean isFreeDelivery() {
        return mIsFreeDelivery;
    }

    public void setDelvieryVisibility(boolean visibility){
        if (visibility) {
            shoppingCartFooter.mDeliveryUPSParcelContainer.setVisibility(View.VISIBLE);
            shoppingCartFooter.summary_delivery_container.setVisibility(View.VISIBLE);
            shoppingCartFooter.mVatInclusiveValue.setVisibility(View.VISIBLE);
            shoppingCartFooter.mVatValue.setVisibility(View.VISIBLE);
        }
        else
        {
            shoppingCartFooter.mDeliveryUPSParcelContainer.setVisibility(View.GONE);
            shoppingCartFooter.summary_delivery_container.setVisibility(View.GONE);
            shoppingCartFooter.mVatInclusiveValue.setVisibility(View.GONE);
            shoppingCartFooter.mVatValue.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mData.size() == 0) {
            return 0;
        } else {
            return mData.size() + 1;
        }
    }

     class ShoppingCartProductHolder extends RecyclerView.ViewHolder {
        NetworkImageView mNetworkImage;
        TextView mTvPrice;
        TextView mTvActualPrice;
        RelativeLayout mQuantityLayout;
        TextView mTvAfterDiscountPrice;
        TextView mTvQuantity;
        TextView mIvOptions;
        Button deleteBtn;
        View shoppingCartView;
        View viewBottomSpace;
        Label out_of_stock;

        ShoppingCartProductHolder(final View shoppingCartView) {
            super(shoppingCartView);
            this.shoppingCartView = shoppingCartView;
            mNetworkImage = shoppingCartView.findViewById(R.id.image);
            mTvPrice = shoppingCartView.findViewById(R.id.price_label);
            mTvActualPrice = shoppingCartView.findViewById(R.id.actual_price);
            mQuantityLayout = shoppingCartView.findViewById(R.id.quantity_count_layout);
            mTvAfterDiscountPrice = shoppingCartView.findViewById(R.id.after_discount_price);
            out_of_stock = shoppingCartView.findViewById(R.id.out_of_stock);
            mTvQuantity = shoppingCartView.findViewById(R.id.quantity_val);
            mIvOptions = shoppingCartView.findViewById(R.id.right_arrow);
            deleteBtn = shoppingCartView.findViewById(R.id.delete_btn);
            deleteBtn.setTextColor(ContextCompat.getColor(mContext, R.color.uid_signal_red_level_45));
            viewBottomSpace= shoppingCartView.findViewById(R.id.bottom_space);

        }
    }

     class FooterShoppingCartViewHolder extends RecyclerView.ViewHolder {
        TextView mDeliveryPrice,mDeliveryModeDescription;
        TextView mVatValue;
        TextView mVatInclusiveValue;
        TextView mTotalItems;
        TextView mTotalCost;
        TextView mDeliveryVia;
        TextView mDeliveryUpsVal;
        Label mDeliveryModeFree,total_discount;
        ImageView mEditIcon;
        TextView mExtraOption;
        TextView mDeliveryTitle;
        LinearLayout mPriceContainer;
        RelativeLayout mDeliveryUPSParcelContainer;
        private RelativeLayout mVoucherContainer;
        Label mAppliedVoucherCode;
        private GridLayout summary_delivery_container;
        private LinearLayout gridDiscount;


        FooterShoppingCartViewHolder(View itemView) {
            super(itemView);
            mDeliveryPrice = itemView.findViewById(R.id.delivery_price);
            mDeliveryModeDescription = itemView.findViewById(R.id.iap_delivery_mode_description);
            mDeliveryModeFree = itemView.findViewById(R.id.iap_delivery_mode_free);
            mVatValue = itemView.findViewById(R.id.including_tax_val);
            mVatInclusiveValue = itemView.findViewById(R.id.vat_inclusive);
            mTotalItems = itemView.findViewById(R.id.total_label);
            mTotalCost = itemView.findViewById(R.id.total_cost_val);
            mDeliveryVia = itemView.findViewById(R.id.delivery_via_ups);
            mDeliveryUpsVal = itemView.findViewById(R.id.delivery_ups_val);
            mExtraOption = itemView.findViewById(R.id.extra_option);
            mEditIcon = itemView.findViewById(R.id.edit_icon);
            mDeliveryTitle = itemView.findViewById(R.id.delivery_ups_title);
            mPriceContainer = itemView.findViewById(R.id.price_container);
            mDeliveryUPSParcelContainer = itemView.findViewById(R.id.delivery_ups_parcel_container);
            summary_delivery_container= itemView.findViewById(R.id.summary_delivery_container);
            mAppliedVoucherCode = itemView.findViewById(R.id.voucherLabel);
            mVoucherContainer = itemView.findViewById(R.id.voucher_container);
            summary_delivery_container= itemView.findViewById(R.id.summary_delivery_container);
           // total_discount = itemView.findViewById(R.id.total_discount);
            gridDiscount = itemView.findViewById(R.id.linear_layout_discount);
        }
    }

    public void tagProducts() {
        StringBuilder products = new StringBuilder();
        for (int i = 0; i < mData.size(); i++) {
            if (i > 0) {
                products = products.append(",");
            }
            products = products.append(mData.get(i).getCategory()).append(";")
                    .append(mData.get(i).getProductTitle()).append(";").append(String.valueOf(mData.get(i).getQuantity()))
                    .append(";").append(mData.get(i).getValuePrice());
        }
        IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA,
                IAPAnalyticsConstant.PRODUCTS, products.toString());
    }
}
