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
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.cart.ShoppingCartData;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.session.NetworkImageLoader;
import com.philips.cdp.di.iap.stock.IAPStockAvailabilityHelper;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.ArrayList;
import java.util.Locale;

import static com.philips.cdp.di.iap.R.id.delivery_via_ups;

public class CheckOutHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private Context mContext;
    private Resources mResources;
    private ArrayList<ShoppingCartData> mData = new ArrayList<>();
    private OutOfStockListener mOutOfStock;
    private UIPicker mPopupWindow;
    private ShoppingCartData shoppingCartDataForProductDetailPage;

    private Drawable countArrow;
    private boolean mIsFreeDelivery;
    private int mSelectedItemPosition = -1;
    private int mQuantityStatus;
    private int mNewCount;
    private OrderSummaryUpdateListner orderSummaryUpdateListner;
    private AddressFields mBillingAddress;
    RelativeLayout mVoucherContainer;

    public void setOrderSummaryUpdateListner(OrderSummaryUpdateListner orderSummaryUpdateListner) {
        this.orderSummaryUpdateListner = orderSummaryUpdateListner;
    }

    public interface OrderSummaryUpdateListner {
        void onGetCartUpdate();
    }

    public interface OutOfStockListener {
        void onOutOfStock(boolean isOutOfStock);
    }

    public CheckOutHistoryAdapter(Context context, ArrayList<ShoppingCartData> shoppingCartData,
                                  OutOfStockListener isOutOfStock) {
        mContext = context;
        mResources = context.getResources();
        mData = shoppingCartData;
        setCountArrow(context, true);
        mOutOfStock = isOutOfStock;
    }

    private void setCountArrow(final Context context, final boolean isEnable) {
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_checkout_history_footer, parent, false);
            return new FooterShoppingCartViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_checkout_history_data, parent, false);
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
        shoppingCartDataForProductDetailPage = mData.get(position);
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.PRODUCT_DETAIL_FRAGMENT_FROM_ORDER);
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

            String imageURL = cartData.getImageURL();
            shoppingCartProductHolder.mTvPrice.setText(cartData.getProductTitle());
            shoppingCartProductHolder.mTvAfterDiscountPrice.setText(cartData.getFormattedPrice());

            checkForOutOfStock(cartData.getStockLevel(), cartData.getQuantity(), shoppingCartProductHolder, cartData.getStockLevelStatus());
            getNetworkImage(shoppingCartProductHolder, imageURL);
        } else {
            //Footer Layout
            FooterShoppingCartViewHolder shoppingCartFooter = (FooterShoppingCartViewHolder) holder;
            ShoppingCartData data;

            if (mData.get(0) != null) {
                data = mData.get(0);

                shoppingCartFooter.mTotalCost.setText(data.getFormattedTotalPriceWithTax());
                shoppingCartFooter.mVatValue.setText(data.getVatValue());
                if (null != data.getDeliveryMode()) {
                    handleTax(data, shoppingCartFooter);

                    String deliveryCost = data.getDeliveryMode().getDeliveryCost().getFormattedValue();
                    String deliveryMethod = data.getDeliveryMode().getName();
                    String deliveryModeDescription=data.getDeliveryMode().getDescription();

                    if ((deliveryCost.substring(1, (deliveryCost.length()))).equalsIgnoreCase("0.00")) {
                        mIsFreeDelivery = true;
                    }
                    if(Utility.isPromotionRunning())
                    {
                        shoppingCartFooter.mDeliveryModeFree.setText(R.string.iap_free_Delivery);
                    }

                    shoppingCartFooter.mDeliveryPrice.setText(deliveryCost);
                    shoppingCartFooter.mDeliveryUpsVal.setText(deliveryCost);

                    if (deliveryMethod != null) {
                        String freeDeliverySpendOn = mContext.getResources().getString(R.string.iap_delevery_via);
                        freeDeliverySpendOn = String.format(freeDeliverySpendOn, deliveryMethod);
                        shoppingCartFooter.mDeliveryTitle.setText(freeDeliverySpendOn);
                    } else {
                        shoppingCartFooter.mDeliveryTitle.setVisibility(View.GONE);
                    }

                    shoppingCartFooter.mDeliveryDescriprion.setText(deliveryModeDescription);
                    shoppingCartFooter.mDeliveryVia.setVisibility(View.VISIBLE);
                    shoppingCartFooter.mDeliveryUpsVal.setVisibility(View.VISIBLE);
                    shoppingCartFooter.mVatValue.setVisibility(View.VISIBLE);

                } else {
                    shoppingCartFooter.mExtraOption.setVisibility(View.GONE);
                    shoppingCartFooter.mDeliveryUPSParcelContainer.setVisibility(View.GONE);
                    mIsFreeDelivery = true;
                }

                final AddressFields shippingAddressFields = CartModelContainer.getInstance().getShippingAddressFields();
                if (data.getDeliveryAddressEntity() != null) {

                    shoppingCartFooter.mShippingName.setText(data.getDeliveryAddressEntity().getFirstName() + " " + data.getDeliveryAddressEntity().getLastName());
                    if(shippingAddressFields!=null) {
                        shoppingCartFooter.mShippingAddress.setText(Utility.getAddressToDisplayForOrderSummary(shippingAddressFields));
                    }
                }

                mBillingAddress = CartModelContainer.getInstance().getBillingAddress();
                mBillingAddress.setCountry(shippingAddressFields.getCountry());
                if (null != mBillingAddress) {
                    String billingName = mBillingAddress.getFirstName() + " " + mBillingAddress.getLastName();
                    shoppingCartFooter.mBillingName.setText(billingName);
                    shoppingCartFooter.mBillingAddress.setText(Utility.getAddressToDisplayForOrderSummary(mBillingAddress));
                }

                for (int i = 0; i < mData.size(); i++) {
                    View priceInfo = View.inflate(mContext, R.layout.iap_price_item, null);
                    TextView mProductName = (TextView) priceInfo.findViewById(R.id.product_name);
                    TextView mProductPrice = (TextView) priceInfo.findViewById(R.id.product_price);
                    mProductName.setText(Integer.toString(mData.get(i).getQuantity()) + "x " + mData.get(i).getProductTitle().toString());
                    mProductPrice.setText(mData.get(i).getFormattedTotalPrice().toString());
                    shoppingCartFooter.mPriceContainer.addView(priceInfo);
                    //shoppingCartFooter.mTotalDiscount.setText("- "+mData.get(i).getTotalDiscounts().toString());
                }
            }


            //Show discounts ==========
            if(null!=mData && mData.size()>0 && null!= mData.get(0) && mData.get(0).getAppliedOrderPromotionEntityList()!=null) {
                for (int i = 0; i < mData.get(0).getAppliedOrderPromotionEntityList().size(); i++) {
                    if (i<mData.size()
                            && null != mData.get(i)
                            && null != mData.get(i).getAppliedOrderPromotionEntityList()
                            && null != mData.get(i).getAppliedOrderPromotionEntityList().get(i)
                            && null != mData.get(i).getAppliedOrderPromotionEntityList().get(i).getPromotion()
                            && null != mData.get(i).getAppliedOrderPromotionEntityList().get(i).getPromotion().getPromotionDiscount()) {
                        View discountInfo = View.inflate(mContext, R.layout.iap_item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.iap_tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.iap_tv_discount_value);

                        tvDiscountText.setText(mData.get(i).getAppliedOrderPromotionEntityList().get(i).getPromotion().getDescription());
                        tvDiscountValue.setText("- " + mData.get(i).getAppliedOrderPromotionEntityList().get(i).getPromotion().getPromotionDiscount().getFormattedValue());
                        shoppingCartFooter.llDiscount.addView(discountInfo);
                    }
                }
            }
            if(null!=mData && mData.size()>0 && null!=mData.get(0) && mData.get(0).getAppliedVouchers()!=null) {
                for (int i = 0; i < mData.get(0).getAppliedVouchers().size(); i++) {
                    if (i<mData.size() && null != mData.get(i) && null != mData.get(i).getAppliedVouchers() && null != mData.get(i).getAppliedVouchers().get(i)
                            && null != mData.get(i).getAppliedVouchers().get(i).getAppliedValue()) {
                        View discountInfo = View.inflate(mContext, R.layout.iap_item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.iap_tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.iap_tv_discount_value);

                        tvDiscountText.setText(mData.get(i).getAppliedVouchers().get(i).getName());
                        tvDiscountValue.setText("- " + mData.get(i).getAppliedVouchers().get(i).getAppliedValue().getFormattedValue());
                        shoppingCartFooter.llDiscount.addView(discountInfo);
                    }
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
        final IAPStockAvailabilityHelper iapStockAvailabilityHelper = new IAPStockAvailabilityHelper();
        final boolean isStockAvailable = iapStockAvailabilityHelper.checkIfRequestedQuantityAvailable(stockLevelStatus, stockLevel, quantity);

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

    public ShoppingCartData getTheProductDataForDisplayingInProductDetailPage() {
        return shoppingCartDataForProductDetailPage;
    }

    public boolean isFreeDelivery() {
        return mIsFreeDelivery;
    }

    @Override
    public int getItemCount() {
        if (mData.size() == 0) {
            return 0;
        } else {
            return mData.size() + 1;
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
            mDeliveryVia = (TextView) itemView.findViewById(delivery_via_ups);
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
