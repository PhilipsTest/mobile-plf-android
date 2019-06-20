/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.philips.cdp.di.iap.Constants.OrderStatus;
import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.address.AddressFields;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.controller.OrderController;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.response.orders.ContactsResponse;
import com.philips.cdp.di.iap.response.orders.OrderDetail;
import com.philips.cdp.di.iap.response.orders.ProductData;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.session.NetworkImageLoader;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

import java.util.ArrayList;
import java.util.HashMap;


public class OrderDetailsFragment extends InAppBaseFragment implements OrderController.OrderListener, View.OnClickListener, AbstractModel.DataLoadListener {

    public static final String TAG = OrderDetailsFragment.class.getName();
    private Context mContext;

    ArrayList<ProductData> mProducts = new ArrayList<>();
    private TextView mTvQuantity;
    private TextView mTvtotalPrice;
    private TextView mTime;
    private TextView mOrderNumber;
    private TextView mOrderState;
    private ImageView mIvOrderState;
    private TextView mDeliveryName;
    private TextView mDeliveryAddress;
    private TextView mBillingName;
    private TextView mBillingAddress;
    private ScrollView mParentView;
    private TextView mPaymentCardType;
    OrderDetail mOrderDetail;
    private LinearLayout mPaymentModeLayout;
    private OrderController mController;
    private TextView mShippingStatus;
    private LinearLayout mProductListView;

    private String mPhoneContact;
    private String mOpeningHoursWeekdays;
    private String mOpeningHoursSaturday;
    private TextView tvDeliveryMode;
    private TextView tvDeliveryModePrice;
    private TextView tvTotal;
    private TextView tvPriceTotal;
    private TextView tvVat;
    private TextView tvPriceVat;
    private LinearLayout tlProductDetailContainer;
    private Button btncall;
    private TextView tvCardName;
    private TextView tvCardNo;

    //Data and timr
    private TextView tvOpeningTimings;

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndBackButtonVisibility(R.string.iap_order_details, true);
        IAPAnalytics.trackPage(IAPAnalyticsConstant.ORDER_DETAIL_PAGE_NAME);
        setCartIconVisibility(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iap_order_details_fragment, container, false);
        mParentView = view.findViewById(R.id.scrollView);
        mTvQuantity = view.findViewById(R.id.tv_quantity);
        mTvtotalPrice = view.findViewById(R.id.tv_total_price);
        mTime = view.findViewById(R.id.tv_time);
        mOrderNumber = view.findViewById(R.id.tv_order_number);
        mOrderState = view.findViewById(R.id.tv_order_state);
        mIvOrderState = view.findViewById(R.id.iv_order_state);
        mDeliveryName = view.findViewById(R.id.tv_shipping_first_name);
        mDeliveryAddress = view.findViewById(R.id.tv_shipping_address);
        mBillingName = view.findViewById(R.id.tv_billing_first_name);
        mBillingAddress = view.findViewById(R.id.tv_billing_address);
        mPaymentModeLayout = view.findViewById(R.id.ll_payment_mode);
        mPaymentCardType = view.findViewById(R.id.tv_card_type);
        tvOpeningTimings = view.findViewById(R.id.tv_opening_timings);
        btncall = view.findViewById(R.id.btn_call);
        btncall.setOnClickListener(this);


        tvCardName = view.findViewById(R.id.tv_card_name);
        tvCardNo = view.findViewById(R.id.tv_card_no);

        Button mCancelOrder = view.findViewById(R.id.btn_cancel);
        mCancelOrder.setOnClickListener(this);

        tvDeliveryMode = view.findViewById(R.id.tv_delivery_mode);
        tvDeliveryModePrice = view.findViewById(R.id.tv_price_deliverymode);

        tvTotal = view.findViewById(R.id.tv_total);
        tvPriceTotal = view.findViewById(R.id.tv_price_total);

        tvVat = view.findViewById(R.id.tv_vat);
        tvPriceVat = view.findViewById(R.id.tv_price_vat);

        tlProductDetailContainer = view.findViewById(R.id.tl_product_detail_container);


        mProductListView = view.findViewById(R.id.product_detail);
        mShippingStatus = view.findViewById(R.id.tv_shipping_status_message);

        Bundle bundle = getArguments();
        if (null != bundle) {
            if (bundle.containsKey(IAPConstant.ORDER_DETAIL)) {
                mOrderDetail = bundle.getParcelable(IAPConstant.ORDER_DETAIL);
                if (mOrderDetail != null) {
                    IAPAnalytics.trackAction(IAPAnalyticsConstant.SEND_DATA, IAPAnalyticsConstant.PURCHASE_ID,
                            mOrderDetail.getCode());
                }
                updateUIwithDetails(mOrderDetail);
            }
        }

        return view;
    }

    private void setCallTimings() {

        mShippingStatus.setText(String.format(mContext.getString(R.string.iap_order_status_msg), mPhoneContact));
        btncall.setText(mContext.getString(R.string.iap_call) + " " + PhoneNumberUtils.formatNumber(mPhoneContact,
                HybrisDelegate.getInstance().getStore().getCountry()));
        tvOpeningTimings.setText(mOpeningHoursWeekdays + "\n" + mOpeningHoursSaturday);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static OrderDetailsFragment createInstance
            (Bundle args, InAppBaseFragment.AnimationType animType) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onGetOrderList(Message msg) {

    }

    @Override
    public void onGetOrderDetail(Message msg) {
        hideProgressBar();
        mParentView.setVisibility(View.VISIBLE);
        if (msg.obj instanceof IAPNetworkError) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), mContext);
        } else {
            if (msg.what == RequestCode.GET_ORDER_DETAIL) {
                if (msg.obj instanceof OrderDetail) {
                    mOrderDetail = (OrderDetail) msg.obj;
                    updateUIwithDetails(mOrderDetail);
                }
            }
        }

    }

    @Override
    public void updateUiOnProductList() {
        ArrayList<OrderDetail> detailList = new ArrayList<>();
        detailList.add(mOrderDetail);
        if (mController == null)
            mController = new OrderController(mContext, this);
        ArrayList<ProductData> productList = mController.getProductData(detailList);
        mProducts.clear();

        if (productList.size() > 0) {
            mController.getPhoneContact(productList.get(0).getSubCategory());
        }

        for (final ProductData product : productList) {
            View productInfo = View.inflate(mContext, R.layout.iap_order_details_item, null);
            mProductListView.addView(productInfo);
            ((TextView) productInfo.findViewById(R.id.tv_productName)).setText(product.getProductTitle());
            ((TextView) productInfo.findViewById(R.id.tv_quantity)).setText(String.valueOf(product.getQuantity()));
            ((TextView) productInfo.findViewById(R.id.tv_total_price)).setText(product.getFormatedPrice());
            getNetworkImage(((NetworkImageView) productInfo.findViewById(R.id.iv_product_image)), product.getImageURL());
            Button trackOrderButton = productInfo.findViewById(R.id.btn_track_order);

            if (product.getTrackOrderUrl() == null) {
                trackOrderButton.setEnabled(false);
            } else {
                trackOrderButton.setEnabled(true);
            }

            trackOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(IAPConstant.ORDER_TRACK_URL, product.getTrackOrderUrl());
                    addFragment(WebTrackUrl.createInstance(bundle, AnimationType.NONE), null, true);
                }
            });
        }

        int totalQuantity = 0;
        for (ProductData data : productList) {
            totalQuantity += data.getQuantity();
        }
        mTvQuantity.setText(String.format(mContext.getString(R.string.iap_no_of_products), totalQuantity + ""));

        setProductSummary(productList);
    }

    private void setProductSummary(ArrayList<ProductData> productList) {
        if (!productList.isEmpty()) {
            populateProductNameQuantityAndPrice(productList);
        }
    }

    private void populateProductNameQuantityAndPrice(ArrayList<ProductData> productList) {

        for (ProductData productData : productList) {

            View v = View.inflate(mContext, R.layout.iap_order_detail_summary_product, null);
            TextView product_quantity_name = v.findViewById(R.id.tv_product_quantity_name);
            TextView price_product = v.findViewById(R.id.tv_price_product);
            product_quantity_name.setText(productData.getQuantity() + "" + "x" + " " + productData.getProductTitle());
            price_product.setText(productData.getFormatedPrice());
            tlProductDetailContainer.addView(v);
        }

    }

    @Override
    public void onGetPhoneContact(Message msg) {
        hideProgressBar();
        if (msg.obj instanceof ContactsResponse) {
            ContactsResponse contactsResponse = (ContactsResponse) msg.obj;
            if (contactsResponse.getData() != null && contactsResponse.getData().getPhone()!=null) {
                mPhoneContact = contactsResponse.getData().getPhone().get(0).getPhoneNumber();
                mOpeningHoursWeekdays = contactsResponse.getData().getPhone().get(0).getOpeningHoursWeekdays();
                mOpeningHoursSaturday = contactsResponse.getData().getPhone().get(0).getOpeningHoursSaturday();

                setCallTimings();
                btncall.setEnabled(true);
            }
        }
    }

    private void getNetworkImage(final NetworkImageView networkImage, final String imageURL) {
        ImageLoader mImageLoader;
        // Instantiate the RequestQueue.
        mImageLoader = NetworkImageLoader.getInstance(mContext)
                .getImageLoader();

        mImageLoader.get(imageURL, ImageLoader.getImageListener(networkImage,
                R.drawable.no_icon, android.R.drawable
                        .ic_dialog_alert));
        networkImage.setImageUrl(imageURL, mImageLoader);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            Bundle bundle = new Bundle();
            if (mOrderDetail != null) {
                if (mPhoneContact == null) {
                    NetworkUtility.getInstance().showErrorDialog(mContext, getFragmentManager(),
                            mContext.getString(R.string.iap_ok), mContext.getString(R.string.iap_server_error), mContext.getString(R.string.iap_something_went_wrong));
                } else {
                    bundle.putString(IAPConstant.CUSTOMER_CARE_NUMBER, mPhoneContact);
                    bundle.putString(IAPConstant.CUSTOMER_CARE_WEEKDAYS_TIMING, mOpeningHoursWeekdays);
                    bundle.putString(IAPConstant.CUSTOMER_CARE_SATURDAY_TIMING, mOpeningHoursSaturday);
                    bundle.putString(IAPConstant.IAP_ORDER_ID, mOrderDetail.getCode());
                    addFragment(CancelOrderFragment.createInstance(bundle, AnimationType.NONE), CancelOrderFragment.TAG, true);
                }
            }
        }

        if (v.getId() == R.id.btn_call) {
            dialCallCenter(mPhoneContact);
        }

    }

    public void updateUIwithDetails(OrderDetail detail) {
        mTime.setText(Utility.getFormattedDate(detail.getCreated()));
        String orderStatus = detail.getStatusDisplay();
        String statusString = orderStatus.substring(0, 1).toLowerCase() + orderStatus.substring(1);

        mOrderState.setText(String.format(mContext.getString(R.string.iap_order_state), statusString));

        int stateImageID = getDrawableIDFromOrderState(statusString);
        //Need to be done once all resources are there .
        // mIvOrderState.setImageResource(stateImageID);
        mOrderNumber.setText(String.format(mContext.getString(R.string.iap_order_number_msg), detail.getCode()));

        mTvQuantity.setText(" (" + mOrderDetail.getDeliveryItemsQuantity() + " item)");
        if (detail.getDeliveryOrderGroups() != null) {
            if (mController == null)
                mController = new OrderController(mContext, this);

            ArrayList<OrderDetail> detailList = new ArrayList<>();
            detailList.add(detail);
            mController.requestPrxData(detailList, this);
        }
        if (detail.getTotalPriceWithTax() != null) {
            tvPriceTotal.setText(detail.getTotalPriceWithTax().getFormattedValue());
        }
        if (detail.getDeliveryMode() != null && detail.getDeliveryCost() != null) {
            tvDeliveryMode.setText(String.format(getResources().getString(R.string.iap_delivery_ups_parcel), detail.getDeliveryMode().getCode().toLowerCase()));
            tvDeliveryModePrice.setText(detail.getDeliveryCost().getFormattedValue());
        }else{
            tvDeliveryMode.setVisibility(View.GONE);
            tvDeliveryModePrice.setVisibility(View.GONE);
        }

        if (detail.getTotalTax() != null) {
            tvPriceVat.setText(detail.getTotalTax().getFormattedValue());
        }

        if (detail.getDeliveryAddress() != null) {
            mDeliveryName.setText(detail.getDeliveryAddress().getFirstName() + " " + detail.getDeliveryAddress().getLastName());
            // mDeliveryAddress.setText(Utility.formatAddress(detail.getDeliveryAddress().getFormattedAddress()) + "\n" + detail.getDeliveryAddress().getCountry().getName());

            AddressFields selectedAddress = Utility.prepareOrderAddressFields(detail.getDeliveryAddress());
            mDeliveryAddress.setText(Utility.getAddressToDisplay(selectedAddress));
        }

        if (detail.getPaymentInfo() != null) {
            if (detail.getPaymentInfo().getBillingAddress() != null) {
                mBillingName.setText(detail.getPaymentInfo().getBillingAddress().getFirstName() + " " + detail.getPaymentInfo().getBillingAddress().getLastName());
                //   mBillingAddress.setText(Utility.formatAddress(detail.getPaymentInfo().getBillingAddress().getFormattedAddress()) + "\n" + detail.getDeliveryAddress().getCountry().getName());

                AddressFields selectedAddress = Utility.prepareOrderAddressFields(detail.getPaymentInfo().getBillingAddress());
                mBillingAddress.setText(Utility.getAddressToDisplay(selectedAddress));
            }
            if (detail.getPaymentInfo().getCardType() != null) {
                mPaymentCardType.setText(detail.getPaymentInfo().getCardType().getCode());
                tvCardName.setText(detail.getPaymentInfo().getBillingAddress().getFirstName());
                tvCardNo.setText(detail.getPaymentInfo().getCardNumber());
            } else {
                mPaymentModeLayout.setVisibility(View.GONE);
            }


        }

        if (detail.getStatusDisplay() != null && detail.getStatusDisplay().equalsIgnoreCase(IAPConstant.ORDER_COMPLETED)) {
            mShippingStatus.setText(getString(R.string.iap_order_completed_text_default));
        }
    }


    private int getDrawableIDFromOrderState(String statusString) {
        int drawableID = 0;

        if (statusString.equalsIgnoreCase(OrderStatus.PENDING.getDescription())) {

        }
        if (statusString.equalsIgnoreCase(OrderStatus.PROCESSING.getDescription())) {

        }

        if (statusString.equalsIgnoreCase(OrderStatus.COMPLETED.getDescription())) {

        }
        return drawableID;
    }


    @Override
    public void onModelDataLoadFinished(Message msg) {
        if (processResponseFromPrx(msg)) return;
        hideProgressBar();

    }

    @Override
    public void onModelDataError(Message msg) {
        hideProgressBar();
    }

    @SuppressWarnings("unchecked")
    private boolean processResponseFromPrx(final Message msg) {
        if (msg.obj instanceof HashMap) {
            final HashMap<String, SummaryModel> obj = (HashMap<String, SummaryModel>) msg.obj;
            if (!obj.isEmpty()) {
                updateUiOnProductList();
            } else {
                hideProgressBar();
                return true;
            }
        }
        return false;
    }

    void dialCallCenter(String phoneNumber) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        String p = "tel:" + PhoneNumberUtils.formatNumber(phoneNumber,
                HybrisDelegate.getInstance().getStore().getCountry());
        i.setData(Uri.parse(p));
        startActivity(i);
    }
}
