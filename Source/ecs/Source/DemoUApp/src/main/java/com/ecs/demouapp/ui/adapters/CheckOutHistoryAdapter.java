/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.session.NetworkImageLoader;
import com.ecs.demouapp.ui.stock.ECSStockAvailabilityHelper;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.Utility;
import com.philips.cdp.di.ecs.model.cart.ECSShoppingCart;
import com.philips.cdp.di.ecs.model.cart.ECSEntries;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.List;


public class CheckOutHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private  ECSShoppingCart mData ;
    private Context mContext;
    private Resources mResources;
    private OutOfStockListener mOutOfStock;
    private UIPicker mPopupWindow;
    private ECSEntries shoppingCartDataForProductDetailPage;

    private Drawable countArrow;
    private boolean mIsFreeDelivery;
    private int mSelectedItemPosition = -1;
    private int mQuantityStatus;
    private int mNewCount;
    private OrderSummaryUpdateListner orderSummaryUpdateListner;
    private AddressFields mBillingAddress;
    RelativeLayout mVoucherContainer;

    private List<ECSEntries> entries ;

    public void setOrderSummaryUpdateListner(OrderSummaryUpdateListner orderSummaryUpdateListner) {
        this.orderSummaryUpdateListner = orderSummaryUpdateListner;
    }

    public interface OrderSummaryUpdateListner {
        void onGetCartUpdate();
    }

    public interface OutOfStockListener {
        void onOutOfStock(boolean isOutOfStock);
    }

    public CheckOutHistoryAdapter(Context context, ECSShoppingCart mData,
                                  OutOfStockListener isOutOfStock) {
        mContext = context;
        mResources = context.getResources();
        this.mData = mData;
        this.entries = mData.getEntries();
        setCountArrow(context, true);
        mOutOfStock = isOutOfStock;
    }


    private void setCountArrow(final Context context, final boolean isEnable) {
        if (isEnable) {
            countArrow = context.getDrawable(R.drawable.ecs_product_count_drop_down);
            countArrow.setColorFilter(new
                    PorterDuffColorFilter(mContext.getResources().getColor(R.color.uid_quiet_button_icon_selector), PorterDuff.Mode.MULTIPLY));
        } else {
            countArrow = VectorDrawableCompat.create(context.getResources(), R.drawable.ecs_product_disable_count_drop_down, mContext.getTheme());
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
        return position == mData.getEntries().size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_checkout_history_footer, parent, false);
            return new FooterShoppingCartViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_checkout_history_data, parent, false);
            return new ShoppingCartProductHolder(v);
        }
        return null;
    }


    public int getNewCount() {
        return mNewCount;
    }

    public int getQuantityStatusInfo() {
        return mQuantityStatus;
    }

    private void showProductDetails(int selectedItem) {
        mSelectedItemPosition = selectedItem;
        setTheProductDataForDisplayingInProductDetailPage(selectedItem);
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    private void setTheProductDataForDisplayingInProductDetailPage(int position) {
        shoppingCartDataForProductDetailPage = entries.get(position);
        EventHelper.getInstance().notifyEventOccurred(ECSConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (entries.size() == 0)
            return;

        if (holder instanceof ShoppingCartProductHolder) {

            final ECSEntries cartData = entries.get(holder.getAdapterPosition());
            ShoppingCartProductHolder shoppingCartProductHolder = (ShoppingCartProductHolder) holder;
            String imageURL = cartData.getProduct().getSummary().getImageURL();
            shoppingCartProductHolder.mTvPrice.setText(cartData.getProduct().getSummary().getProductTitle());
            shoppingCartProductHolder.mTvAfterDiscountPrice.setText(cartData.getBasePrice().getFormattedValue());

            checkForOutOfStock(cartData.getProduct().getStock().getStockLevel(), cartData.getQuantity(), shoppingCartProductHolder, cartData.getProduct().getStock().getStockLevelStatus());
            getNetworkImage(shoppingCartProductHolder, imageURL);
        } else {
            //Footer Layout
            FooterShoppingCartViewHolder shoppingCartFooter = (FooterShoppingCartViewHolder) holder;
            ECSEntries data;

            if (entries.get(0) != null) {
                data = entries.get(0);

                shoppingCartFooter.mTotalCost.setText(mData.getTotalPriceWithTax().getFormattedValue());
                shoppingCartFooter.mVatValue.setText(mData.getTotalTax().getFormattedValue()+"");
                if (null != mData.getDeliveryMode()) {
                    handleTax(data, shoppingCartFooter);

                    String deliveryCost = mData.getDeliveryMode().getDeliveryCost().getFormattedValue();
                    String deliveryMethod = mData.getDeliveryMode().getName();
                    String deliveryModeDescription=mData.getDeliveryMode().getDescription();

                    if ((deliveryCost.substring(1, (deliveryCost.length()))).equalsIgnoreCase("0.00")) {
                        mIsFreeDelivery = true;
                    }
                    if(Utility.isPromotionRunning())
                    {
                        shoppingCartFooter.mDeliveryModeFree.setText(R.string.iap_delivery_promotion);
                    }

                    shoppingCartFooter.mDeliveryPrice.setText(deliveryCost);
                    shoppingCartFooter.mDeliveryUpsVal.setText(deliveryCost);

                        String freeDeliverySpendOn = mContext.getResources().getString(R.string.iap_delivery_ups_parcel);
                        freeDeliverySpendOn = String.format(freeDeliverySpendOn, deliveryMethod);
                        shoppingCartFooter.mDeliveryTitle.setText(freeDeliverySpendOn);


                    shoppingCartFooter.mDeliveryDescriprion.setText(deliveryModeDescription);
                    shoppingCartFooter.mDeliveryVia.setVisibility(View.VISIBLE);
                    shoppingCartFooter.mDeliveryUpsVal.setVisibility(View.VISIBLE);


                } else {
                    shoppingCartFooter.mExtraOption.setVisibility(View.GONE);
                    shoppingCartFooter.mDeliveryUPSParcelContainer.setVisibility(View.GONE);
                    mIsFreeDelivery = true;
                }

                if (mData.getDeliveryAddress() != null) {
                    final AddressFields shippingAddressFields = CartModelContainer.getInstance().getShippingAddressFields();
                    shoppingCartFooter.mShippingName.setText(mData.getDeliveryAddress().getFirstName() + " " + mData.getDeliveryAddress().getLastName());
                    if(shippingAddressFields!=null) {
                        shoppingCartFooter.mShippingAddress.setText(Utility.getAddressToDisplay(shippingAddressFields));
                    }
                }

                mBillingAddress = CartModelContainer.getInstance().getBillingAddress();
                if (null != mBillingAddress) {
                    String billingName = mBillingAddress.getFirstName() + " " + mBillingAddress.getLastName();
                    shoppingCartFooter.mBillingName.setText(billingName);
                    shoppingCartFooter.mBillingAddress.setText(Utility.getAddressToDisplay(mBillingAddress));
                }

                for (int i = 0; i < entries.size(); i++) {
                    View priceInfo = View.inflate(mContext, R.layout.ecs_price_item, null);
                    TextView mProductName = (TextView) priceInfo.findViewById(R.id.product_name);
                    TextView mProductPrice = (TextView) priceInfo.findViewById(R.id.product_price);
                    mProductName.setText(Integer.toString(entries.get(i).getQuantity()) + "x " + entries.get(i).getProduct().getSummary().getProductTitle().toString());
                    mProductPrice.setText(entries.get(i).getTotalPrice().getFormattedValue().toString());
                    shoppingCartFooter.mPriceContainer.addView(priceInfo);
                    //shoppingCartFooter.mTotalDiscount.setText("- "+mData.get(i).getTotalDiscounts().toString());
                }
            }


            //Show discounts ==========

            if(mData.getAppliedOrderPromotions()!=null) {
                for (int i = 0; i < mData.getAppliedOrderPromotions().size(); i++) {
                    if(null!=mData.getAppliedOrderPromotions().get(i) && null!=mData.getAppliedOrderPromotions().get(i).getPromotion() && null!=mData.getAppliedOrderPromotions().get(i).getPromotion().getPromotionDiscount()) {
                        View discountInfo = View.inflate(mContext, R.layout.item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.tv_discount_value);

                        tvDiscountText.setText(mData.getAppliedOrderPromotions().get(i).getPromotion().getDescription());
                        tvDiscountValue.setText("- " + mData.getAppliedOrderPromotions().get(i).getPromotion().getPromotionDiscount().getFormattedValue());
                        shoppingCartFooter.llDiscount.addView(discountInfo);
                    }
                }
            }

            if(mData.getAppliedVouchers()!=null)
            for(int i=0;i< mData.getAppliedVouchers().size();i++){
                if(null!=mData.getAppliedVouchers().get(i)  && null!=mData.getAppliedVouchers().get(i).getName() &&  null!=mData.getAppliedVouchers().get(i).getAppliedValue()) {
                    View discountInfo = View.inflate(mContext, R.layout.item_discount, null);
                    TextView tvDiscountText = discountInfo.findViewById(R.id.tv_discount_text);
                    TextView tvDiscountValue = discountInfo.findViewById(R.id.tv_discount_value);

                    tvDiscountText.setText(mData.getAppliedVouchers().get(i).getName());
                    tvDiscountValue.setText("- " + mData.getAppliedVouchers().get(i).getAppliedValue().getFormattedValue());
                    shoppingCartFooter.llDiscount.addView(discountInfo);
                }
            }

        }
        orderSummaryUpdateListner.onGetCartUpdate();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductDetails(holder.getAdapterPosition());
            }
        });

    }

    private void handleTax(ECSEntries data, FooterShoppingCartViewHolder shoppingCartFooter) {
        if (!mData.isNet()) {
            shoppingCartFooter.mVatValue.setVisibility(View.GONE);
            if (mData.getTotalTax() != null) {
                shoppingCartFooter.mVatInclusiveValue.setVisibility(View.VISIBLE);
                shoppingCartFooter.mDeliveryUpsVal.setVisibility(View.VISIBLE);
                shoppingCartFooter.mDeliveryUpsVal.setText(mData.getTotalTax().getFormattedValue());
            }
        } else {
            if (mData.getTotalTax() != null) {
                shoppingCartFooter.mVatValue.setVisibility(View.VISIBLE);
                shoppingCartFooter.mVatValue.setText(mData.getTotalTax().getFormattedValue());
            }
        }
    }

    private void checkForOutOfStock(int stockLevel, int quantity, ShoppingCartProductHolder shoppingCartProductHolder, String stockLevelStatus) {
        final ECSStockAvailabilityHelper ECSStockAvailabilityHelper = new ECSStockAvailabilityHelper();
        final boolean isStockAvailable = ECSStockAvailabilityHelper.checkIfRequestedQuantityAvailable(stockLevelStatus, stockLevel, quantity);

        mOutOfStock.onOutOfStock(isStockAvailable);

        if(!isStockAvailable){
            shoppingCartProductHolder.mTvAfterDiscountPrice.setVisibility(View.VISIBLE);
            shoppingCartProductHolder.mTvAfterDiscountPrice.setText("Only " + stockLevel + " left");
            setCountArrow(mContext, false);
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

    public ECSEntries getTheProductDataForDisplayingInProductDetailPage() {
        return shoppingCartDataForProductDetailPage;
    }

    public boolean isFreeDelivery() {
        return mIsFreeDelivery;
    }

    @Override
    public int getItemCount() {
        if (entries.size() == 0) {
            return 0;
        } else {
            return entries.size() + 1;
        }
    }

    private class ShoppingCartProductHolder extends RecyclerView.ViewHolder {
        NetworkImageView mNetworkImage;
        TextView mTvPrice;
        TextView mTvActualPrice;
        TextView mTvAfterDiscountPrice;
        TextView mIvOptions;

        ShoppingCartProductHolder(final View itemView) {
            super(itemView);
            mNetworkImage = (NetworkImageView) itemView.findViewById(R.id.image);
            mTvPrice = (TextView) itemView.findViewById(R.id.price_label);
            mTvActualPrice = (TextView) itemView.findViewById(R.id.actual_price);
            mTvAfterDiscountPrice = (TextView) itemView.findViewById(R.id.after_discount_price);
            mIvOptions = (TextView) itemView.findViewById(R.id.right_arrow);
        }
    }

    private class FooterShoppingCartViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llDiscount;
        TextView mDeliveryPrice;
        TextView mVatValue;
        TextView mVatInclusiveValue;
        TextView mTotalItems;
        TextView mTotalCost;
        TextView mDeliveryVia;
        TextView mDeliveryUpsVal;
        Label mDeliveryDescriprion,mDeliveryModeFree,mTotalDiscount;
        ImageView mEditIcon;
        TextView mExtraOption;
        TextView mDeliveryTitle;
        TextView mShippingName;
        TextView mShippingAddress;
        TextView mBillingName;
        TextView mBillingAddress;
        LinearLayout mPriceContainer;
        RelativeLayout mDeliveryUPSParcelContainer;

        FooterShoppingCartViewHolder(View itemView) {
            super(itemView);
            mDeliveryPrice = (TextView) itemView.findViewById(R.id.delivery_price);
            mVatValue = (TextView) itemView.findViewById(R.id.including_tax_val);
            mDeliveryModeFree = itemView.findViewById(R.id.iap_delivery_mode_free);
            mVatInclusiveValue = (TextView) itemView.findViewById(R.id.vat_inclusive);
            mTotalItems = (TextView) itemView.findViewById(R.id.total_label);
            mTotalCost = (TextView) itemView.findViewById(R.id.total_cost_val);
            mDeliveryVia = (TextView) itemView.findViewById(R.id.ecs_delivery_via_ups);
            mDeliveryUpsVal = (TextView) itemView.findViewById(R.id.delivery_ups_val);
            mDeliveryDescriprion = itemView.findViewById(R.id.iap_delivery_mode_description);
            mExtraOption = (TextView) itemView.findViewById(R.id.extra_option);
            mEditIcon = (ImageView) itemView.findViewById(R.id.edit_icon);
            mDeliveryTitle = (TextView) itemView.findViewById(R.id.delivery_ups_title);
            mShippingName = (TextView) itemView.findViewById(R.id.tv_shipping_name);
            mShippingAddress = (TextView) itemView.findViewById(R.id.tv_shipping_address);
            mBillingName = (TextView) itemView.findViewById(R.id.tv_billing_name);
            mBillingAddress = (TextView) itemView.findViewById(R.id.tv_billing_address);
            mPriceContainer = (LinearLayout) itemView.findViewById(R.id.price_container);
            mDeliveryUPSParcelContainer = (RelativeLayout) itemView.findViewById(R.id.delivery_ups_parcel_container);
            mVoucherContainer  = (RelativeLayout) itemView.findViewById(R.id.voucher_container);
           // mTotalDiscount = itemView.findViewById(R.id.total_discount);
            llDiscount = itemView.findViewById(R.id.linear_layout_discount);

        }
    }
}
