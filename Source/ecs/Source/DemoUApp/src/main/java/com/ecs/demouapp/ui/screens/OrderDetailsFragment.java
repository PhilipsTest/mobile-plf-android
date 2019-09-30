/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.screens;

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
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.Constants.OrderStatus;
import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.analytics.ECSAnalytics;
import com.ecs.demouapp.ui.analytics.ECSAnalyticsConstant;
import com.ecs.demouapp.ui.controller.OrderController;
import com.ecs.demouapp.ui.model.Contact.ContactsResponse;
import com.ecs.demouapp.ui.response.orders.ProductData;
import com.ecs.demouapp.ui.session.IAPNetworkError;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.session.NetworkImageLoader;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.ecs.demouapp.ui.utils.NetworkUtility;
import com.ecs.demouapp.ui.utils.Utility;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.model.orders.ConsignmentEntries;
import com.philips.cdp.di.ecs.model.orders.Entries;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.util.ECSConfiguration;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class OrderDetailsFragment extends InAppBaseFragment implements OrderController.OrderListener, View.OnClickListener {

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
    ECSOrderDetail mOrderDetail;
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
        ECSAnalytics.trackPage(ECSAnalyticsConstant.ORDER_DETAIL_PAGE_NAME);
        setCartIconVisibility(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecs_order_details_fragment, container, false);
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
            if (bundle.containsKey(ECSConstant.ORDER_DETAIL)) {
                mOrderDetail = bundle.getParcelable(ECSConstant.ORDER_DETAIL);
                if (mOrderDetail != null) {
                    ECSAnalytics.trackAction(ECSAnalyticsConstant.SEND_DATA, ECSAnalyticsConstant.PURCHASE_ID,
                            mOrderDetail.getCode());
                    updateUIwithDetails(mOrderDetail);
                    getPhoneContact(mOrderDetail.getEntries().get(0).getProduct().getSummary().getSubcategory());
                }

            }
        }

        return view;
    }

    private void setCallTimings() {

        mShippingStatus.setText(String.format(mContext.getString(R.string.iap_order_status_msg), mPhoneContact));
        btncall.setText(mContext.getString(R.string.iap_call) + " " + PhoneNumberUtils.formatNumber(mPhoneContact,
                ECSConfiguration.INSTANCE.getCountry()));
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
            ECSUtility.showECSAlertDialog(mContext,"Error",((IAPNetworkError) msg.obj).getMessage());
        } else {
            if (msg.what == RequestCode.GET_ORDER_DETAIL) {
                if (msg.obj instanceof ECSOrderDetail) {
                    mOrderDetail = (ECSOrderDetail) msg.obj;
                    updateUIwithDetails(mOrderDetail);

                    updateSummaryData(mOrderDetail);
                }
            }
        }

    }

    @Override
    public void updateUiOnProductList() {
   /*     ArrayList<OrderDetail> detailList = new ArrayList<>();
        detailList.add(mOrderDetail);
        if (mController == null)
            mController = new OrderController(mContext, this);


        if (productList.size() > 0) {
            mController.getPhoneContact(productList.get(0).getSubCategory());
        }

        for (final ProductData product : productList) {
            View productInfo = View.inflate(mContext, R.layout.ecs_order_details_item, null);
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
                    bundle.putString(ECSConstant.ORDER_TRACK_URL, product.getTrackOrderUrl());
                    addFragment(WebTrackUrl.createInstance(bundle, AnimationType.NONE), null, true);
                }
            });
        }

        int totalQuantity = 0;
        for (ProductData data : productList) {
            totalQuantity += data.getQuantity();
        }
        mTvQuantity.setText(String.format(mContext.getString(R.string.iap_no_of_products), totalQuantity + ""));

        setProductSummary(productList);*/
    }




    public void updateSummaryData(ECSOrderDetail mOrderDetail) {
        ArrayList<ECSOrderDetail> detailList = new ArrayList<>();
        detailList.add(this.mOrderDetail);
        if (mController == null)
            mController = new OrderController(mContext, this);


        if (mOrderDetail.getEntries().size() > 0) {

            //TODO
            getPhoneContact(mOrderDetail.getEntries().get(0).getProduct().getSummary().getSubcategory());
        }

        for (final Entries entries : mOrderDetail.getEntries()) {
            View productInfo = View.inflate(mContext, R.layout.ecs_order_details_item, null);
            mProductListView.addView(productInfo);
            ((TextView) productInfo.findViewById(R.id.tv_productName)).setText(entries.getProduct().getSummary().getProductTitle());
            ((TextView) productInfo.findViewById(R.id.tv_quantity)).setText(String.valueOf(entries.getQuantity()));
            ((TextView) productInfo.findViewById(R.id.tv_total_price)).setText(entries.getTotalPrice().getFormattedValue());
            getNetworkImage(((NetworkImageView) productInfo.findViewById(R.id.iv_product_image)), entries.getProduct().getSummary().getImageURL());
            Button trackOrderButton = productInfo.findViewById(R.id.btn_track_order);

            ConsignmentEntries consignmentEntries = mController.getEntriesFromConsignMent(mOrderDetail, entries.getProduct().getCode());
            if (mController.getOrderTrackUrl(consignmentEntries) == null) {
                trackOrderButton.setEnabled(false);
            } else {
                trackOrderButton.setEnabled(true);
            }

            trackOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(ECSConstant.ORDER_TRACK_URL, mController.getOrderTrackUrl(consignmentEntries));
                    addFragment(WebTrackUrl.createInstance(bundle, AnimationType.NONE), null, true);
                }
            });
        }

        int totalQuantity = 0;
        for (Entries entries : mOrderDetail.getEntries()) {
            totalQuantity += entries.getQuantity();
        }
        mTvQuantity.setText(String.format(mContext.getString(R.string.iap_no_of_products), totalQuantity + ""));

        setProductSummary(mOrderDetail);
    }

    private void setProductSummary(ECSOrderDetail mOrderDetail) {
            populateProductNameQuantityAndPrice(mOrderDetail);
    }

    private void populateProductNameQuantityAndPrice(ECSOrderDetail mOrderDetail) {

        for (Entries entries : mOrderDetail.getEntries()) {

            View v = View.inflate(mContext, R.layout.ecs_order_detail_summary_product, null);
            TextView product_quantity_name = v.findViewById(R.id.tv_product_quantity_name);
            TextView price_product = v.findViewById(R.id.tv_price_product);
            product_quantity_name.setText(entries.getQuantity() + "" + "x" + " " + entries.getProduct().getSummary().getProductTitle());
            price_product.setText(entries.getProduct().getPrice().getFormattedValue());
            tlProductDetailContainer.addView(v);
        }

    }

    @Override
    public void onGetPhoneContact(Message msg) {
        hideProgressBar();
        //TODO
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
                    bundle.putString(ECSConstant.CUSTOMER_CARE_NUMBER, mPhoneContact);
                    bundle.putString(ECSConstant.CUSTOMER_CARE_WEEKDAYS_TIMING, mOpeningHoursWeekdays);
                    bundle.putString(ECSConstant.CUSTOMER_CARE_SATURDAY_TIMING, mOpeningHoursSaturday);
                    bundle.putString(ECSConstant.IAP_ORDER_ID, mOrderDetail.getCode());
                    addFragment(CancelOrderFragment.createInstance(bundle, AnimationType.NONE), CancelOrderFragment.TAG, true);
                }
            }
        }

        if (v.getId() == R.id.btn_call) {
            dialCallCenter(mPhoneContact);
        }

    }

    public void updateUIwithDetails(ECSOrderDetail detail) {
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

            ArrayList<ECSOrderDetail> detailList = new ArrayList<>();
            detailList.add(detail);

            //call prx here
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

        if (detail.getStatusDisplay() != null && detail.getStatusDisplay().equalsIgnoreCase(ECSConstant.ORDER_COMPLETED)) {
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
                ECSConfiguration.INSTANCE.getCountry());
        i.setData(Uri.parse(p));
        startActivity(i);
    }

    public void getPhoneContact(String subCategory){

        String SUFFIX_CONTACT_PHONE_URL = subCategory + ".querytype.(fallback)";

        String url =  "https://www.philips.com/prx/cdls/B2C/" +
                ECSConfiguration.INSTANCE.getLocale() + "/CARE/".concat(SUFFIX_CONTACT_PHONE_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ContactsResponse contactsResponse = new Gson().fromJson(response.toString(), ContactsResponse.class);

                Message message = new Message();
                message.obj = contactsResponse;
                onGetPhoneContact(message);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ECSUtility.showECSAlertDialog(mContext, "Error", error.getMessage());
                hideProgressBar();

            }
        }, null, null, null);

        ECSConfiguration.INSTANCE.getAppInfra().getRestClient().getRequestQueue().add(jsonObjectRequest);

    }
}
