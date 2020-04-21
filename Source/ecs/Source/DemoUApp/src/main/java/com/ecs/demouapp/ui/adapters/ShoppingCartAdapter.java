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

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.eventhelper.EventHelper;
import com.ecs.demouapp.ui.session.NetworkImageLoader;
import com.ecs.demouapp.ui.stock.ECSStockAvailabilityHelper;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.Utility;
import com.ecs.demouapp.ui.view.CountDropDown;
import com.philips.platform.ecs.model.cart.ECSShoppingCart;
import com.philips.platform.ecs.model.cart.ECSEntries;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.UIPicker;

import java.util.List;

import static com.ecs.demouapp.ui.utils.ECSConstant.IAP_APPLY_VOUCHER;


public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final Context mContext;
    private final Resources mResources;
    private ECSShoppingCart mData ;
    private final OutOfStockListener mOutOfStock;
    private UIPicker mPopupWindow;
    private ECSEntries shoppingCartDataForProductDetailPage;

    private Drawable countArrow;
    private boolean mIsFreeDelivery;
    private int mSelectedItemPosition = -1;
    private int mQuantityStatus;
    private int mNewCount;
    private FooterShoppingCartViewHolder shoppingCartFooter;

    private List<ECSEntries> entries ;

    public interface OutOfStockListener {
        void onOutOfStock(boolean isOutOfStock);
    }

    public ShoppingCartAdapter(Context context, ECSShoppingCart mData,
                               OutOfStockListener isOutOfStock) {
        mContext = context;
        mResources = context.getResources();
        this.mData = mData;
        this.entries = mData.getEntries();
        mOutOfStock = isOutOfStock;
    }

    public void setCountArrow(final Context context, final boolean isEnable) {
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
        return position == entries.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_FOOTER) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_shopping_cart_footer, parent, false);
            return new FooterShoppingCartViewHolder(v);
        } else if (viewType == TYPE_ITEM) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecs_shopping_cart_data, parent, false);
            return new ShoppingCartProductHolder(v);
        }
        return null;
    }

    private void bindCountView(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ECSEntries data = entries.get(position);

                CountDropDown countPopUp = new CountDropDown(v,v.getContext(), data.getProduct().getStock().getStockLevel(), data
                        .getQuantity(), new CountDropDown.CountUpdateListener() {
                    @Override
                    public void countUpdate(final int oldCount, final int newCount) {
                        mSelectedItemPosition = position;
                        mQuantityStatus = getQuantityStatus(newCount, oldCount);
                        mNewCount = newCount;
                        EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_UPDATE_PRODUCT_COUNT);
                    }
                });
                countPopUp.createPopUp(v,data.getProduct().getStock().getStockLevel());
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
        shoppingCartDataForProductDetailPage = entries.get(position);
        EventHelper.getInstance().notifyEventOccurred(ECSConstant.PRODUCT_DETAIL_FRAGMENT);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (entries.size() == 0)
            return;

        if (holder instanceof ShoppingCartProductHolder) {

            final ECSEntries cartData = entries.get(holder.getAdapterPosition());
            ShoppingCartProductHolder shoppingCartProductHolder = (ShoppingCartProductHolder) holder;

            if(entries.size()==1 || position==entries.size()-1){
                shoppingCartProductHolder.viewBottomSpace.setVisibility(View.GONE);
            }

            String imageURL = cartData.getProduct().getSummary().getImageURL();
            shoppingCartProductHolder.mTvPrice.setText(cartData.getProduct().getSummary().getProductTitle());
            shoppingCartProductHolder.mTvQuantity.setText(Integer.toString(cartData.getQuantity()));
            shoppingCartProductHolder.mTvAfterDiscountPrice.setText(cartData.getBasePrice().getFormattedValue());

            checkForOutOfStock(cartData.getProduct().getStock().getStockLevel(), cartData.getQuantity(), shoppingCartProductHolder, cartData.getProduct().getStock().getStockLevelStatus());
            getNetworkImage(shoppingCartProductHolder, imageURL);
            shoppingCartProductHolder.mTvQuantity.setCompoundDrawables(null, null, countArrow, null);
            bindCountView(shoppingCartProductHolder.mQuantityLayout, holder.getAdapterPosition());

            shoppingCartProductHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mSelectedItemPosition = holder.getAdapterPosition();
                    EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_DELETE_PRODUCT_CONFIRM);
                }
            });
        } else {
            //Footer Layout
            shoppingCartFooter = (FooterShoppingCartViewHolder) holder;
            ECSEntries data;

            if (entries.get(0) != null) {
                data = entries.get(0);

                shoppingCartFooter.mTotalCost.setText(mData.getTotalPriceWithTax().getFormattedValue());
                shoppingCartFooter.mVatValue.setText(mData.getTotalTax().getFormattedValue()+"");
                if (null != mData.getDeliveryMode()) {
                    handleTax(data, shoppingCartFooter);

                    shoppingCartFooter.mExtraOption.setVisibility(View.VISIBLE);

                    String deliveryCost = mData.getDeliveryMode().getDeliveryCost().getFormattedValue();
                    String deliveryMethod = mData.getDeliveryMode().getName();
                    String deliveryModeDescription=mData.getDeliveryMode().getDescription();

                    if ((deliveryCost.trim()).equalsIgnoreCase("$ 0.00") ) {
                        mIsFreeDelivery = true;
                    }

                    if(Utility.isPromotionRunning())
                    {
                        shoppingCartFooter.mDeliveryModeFree.setText(R.string.iap_delivery_promotion);
                        shoppingCartFooter.mDeliveryModeFree.setVisibility(View.VISIBLE);
                    }

                    shoppingCartFooter.mDeliveryPrice.setText(deliveryCost);
                    shoppingCartFooter.mDeliveryPrice.setVisibility(View.VISIBLE);


                    shoppingCartFooter.mDeliveryUpsVal.setText(deliveryCost);
                    shoppingCartFooter.mDeliveryUpsVal.setVisibility(View.VISIBLE);


                    shoppingCartFooter.mDeliveryVia.setVisibility(View.VISIBLE);
                    String freeDeliverySpendOn = mContext.getResources().getString(R.string.iap_delivery_ups_parcel);
                    freeDeliverySpendOn = String.format(freeDeliverySpendOn, deliveryMethod);
                    shoppingCartFooter.mDeliveryTitle.setText(freeDeliverySpendOn);
                    shoppingCartFooter.mDeliveryTitle.setVisibility(View.VISIBLE);

                    shoppingCartFooter.mDeliveryModeDescription.setText(deliveryModeDescription);
                    shoppingCartFooter.mDeliveryModeDescription.setVisibility(View.VISIBLE);


                    shoppingCartFooter.mDeliveryUPSParcelContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventHelper.getInstance().notifyEventOccurred(ECSConstant.IAP_EDIT_DELIVERY_MODE);
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

                for (int i = 0; i < entries.size(); i++) {
                    View priceInfo = View.inflate(mContext, R.layout.ecs_price_item, null);
                    TextView mProductName = priceInfo.findViewById(R.id.product_name);
                    TextView mProductPrice = priceInfo.findViewById(R.id.product_price);
                    mProductName.setText(Integer.toString(entries.get(i).getQuantity()) + "x " + entries.get(i).getProduct().getSummary().getProductTitle().toString());
                    mProductPrice.setText(entries.get(i).getTotalPrice().getFormattedValue().toString());
                    shoppingCartFooter.mPriceContainer.addView(priceInfo);
                    //shoppingCartFooter.total_discount.setText("- "+mData.get(i).getTotalDiscounts());
                }


                //Show discounts ==========
                if(mData.getAppliedOrderPromotions()!=null) {
                    for (int i = 0; i < mData.getAppliedOrderPromotions().size(); i++) {

                        View discountInfo = View.inflate(mContext, R.layout.item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.tv_discount_value);

                        tvDiscountText.setText(mData.getAppliedOrderPromotions().get(i).getPromotion().getDescription());
                        tvDiscountValue.setText("- " + mData.getAppliedOrderPromotions().get(i).getPromotion().getPromotionDiscount().getFormattedValue());
                        shoppingCartFooter.gridDiscount.addView(discountInfo);
                    }
                }

                if(mData.getAppliedVouchers()!=null) {
                    for (int i = 0; i < mData.getAppliedVouchers().size(); i++) {

                        View discountInfo = View.inflate(mContext, R.layout.item_discount, null);
                        TextView tvDiscountText = discountInfo.findViewById(R.id.tv_discount_text);
                        TextView tvDiscountValue = discountInfo.findViewById(R.id.tv_discount_value);

                        tvDiscountText.setText(mData.getAppliedVouchers().get(i).getDescription());
                        tvDiscountValue.setText("- " + mData.getAppliedVouchers().get(i).getAppliedValue().getFormattedValue());
                        shoppingCartFooter.gridDiscount.addView(discountInfo);
                    }
                }
            }

            if(Utility.isVoucherEnable()){
                shoppingCartFooter.mExtraOption.setVisibility(View.VISIBLE);
            }

            if(Utility.isVoucherEnable()) {
                shoppingCartFooter.mVoucherContainer.setVisibility(View.VISIBLE);
                shoppingCartFooter.mAppliedVoucherCode.setText(R.string.iap_promotion_gift_code);

                } else {
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
        ECSStockAvailabilityHelper ECSStockAvailabilityHelper = new ECSStockAvailabilityHelper();
        final boolean isStockAvailable = ECSStockAvailabilityHelper.checkIfRequestedQuantityAvailable(stockLevelStatus, stockLevel, quantity);

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

    public ECSEntries getTheProductDataForDisplayingInProductDetailPage() {
        return shoppingCartDataForProductDetailPage;
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
        if (entries.size() == 0) {
            return 0;
        } else {
            return entries.size() + 1;
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
            mDeliveryVia = itemView.findViewById(R.id.ecs_delivery_via_ups);
            mDeliveryUpsVal = itemView.findViewById(R.id.delivery_ups_val);
            mExtraOption = itemView.findViewById(R.id.extra_option);
            mEditIcon = itemView.findViewById(R.id.edit_icon);
            mDeliveryTitle = itemView.findViewById(R.id.delivery_ups_title);
            mPriceContainer = itemView.findViewById(R.id.price_container);
            mDeliveryUPSParcelContainer = itemView.findViewById(R.id.delivery_ups_parcel_container);
            summary_delivery_container= itemView.findViewById(R.id.summary_delivery_container);
            mAppliedVoucherCode = itemView.findViewById(R.id.voucherLabel);
            mVoucherContainer = itemView.findViewById(R.id.voucher_container);
           // total_discount = itemView.findViewById(R.id.total_discount);
            gridDiscount = itemView.findViewById(R.id.linear_layout_discount);
        }
    }

}
