/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.controller;

import android.content.Context;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ecs.demouapp.ui.integration.ECSConfigure;
import com.ecs.demouapp.ui.model.Contact.ContactsResponse;
import com.ecs.demouapp.ui.response.orders.ProductData;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.google.gson.Gson;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.orders.Consignment;
import com.philips.cdp.di.ecs.model.orders.ConsignmentEntries;
import com.philips.cdp.di.ecs.model.orders.ECSOrderHistory;
import com.philips.cdp.di.ecs.model.orders.Entries;
import com.philips.cdp.di.ecs.model.orders.ECSOrderDetail;
import com.philips.cdp.di.ecs.model.summary.Data;
import com.philips.cdp.di.ecs.util.ECSConfiguration;
import com.philips.platform.appinfra.rest.request.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderController {

    private Context mContext;
    private OrderListener mOrderListener;

    public interface OrderListener {
        void onGetOrderList(Message msg);

        void onGetOrderDetail(Message msg);

        void updateUiOnProductList();

        void onGetPhoneContact(Message msg);
    }

    public OrderController(Context context, OrderListener listener) {
        mContext = context;
        mOrderListener = listener;
    }

    public void getOrderList(int pageNo) {

        ECSUtility.getInstance().getEcsServices().fetchOrderHistory(pageNo, 0, new ECSCallback<ECSOrderHistory, Exception>() {
            @Override
            public void onResponse(ECSOrderHistory result) {
                Message message = new Message();
                message.obj = result;

                mOrderListener.onGetOrderList(message);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                Message message = new Message();
                message.obj = error;
                mOrderListener.onGetOrderList(message);
            }
        });
       /* HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CURRENT_PAGE, String.valueOf(pageNo));
        OrderHistoryRequest model = new OrderHistoryRequest(getStore(), query, this);
        getHybrisDelegate().sendRequest(RequestCode.GET_ORDERS, model, model);*/
    }

    public void getOrderDetails(String orderNumber) {

        Message message = new Message();
        message.what = RequestCode.GET_ORDER_DETAIL;
        ECSUtility.getInstance().getEcsServices().fetchOrderDetail(orderNumber, new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail orderDetail) {

                message.obj = orderDetail;
                mOrderListener.onGetOrderDetail(message);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                message.obj = error;
                mOrderListener.onGetOrderDetail(message);
            }
        });
       /* HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.ORDER_NUMBER, orderNumber);
        OrderDetailRequest request = new OrderDetailRequest(getStore(), query, this);
        getHybrisDelegate().sendRequest(RequestCode.GET_ORDER_DETAIL, request, request);*/
    }

    public void getPhoneContact(String subCategory){
        //TODO
       /* HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CATEGORY, subCategory);

        ContactCallRequest request = new ContactCallRequest(getStore(), query, this);
        getHybrisDelegate().sendRequest(RequestCode.GET_PHONE_CONTACT, request, request);*/

       String SUFFIX_CONTACT_PHONE_URL = subCategory + ".querytype.(fallback)";

       String url =  "https://www.philips.com/prx/cdls/B2C/" +
               ECSConfiguration.INSTANCE.getLocale() + "/CARE/".concat(SUFFIX_CONTACT_PHONE_URL);

       //JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, appUpdateUrl, null,
        //				getJsonResponseListener(refreshListener), getJsonErrorListener(refreshListener), null, null, null);


       new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {

               ContactsResponse contactsResponse = new Gson().fromJson(response.toString(), ContactsResponse.class);

              // mOrderListener.onGetPhoneContact(msg);

           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {



           }
       },null,null,null);

       /* new JsonObjectRequest(appInfraJSONRequest.getMethod(), appInfraJSONRequest.getURL(), appInfraJSONRequest.getJSONRequest()
                , appInfraJSONRequest.getJSONSuccessResponseListener(), appInfraJSONRequest.getJSONFailureResponseListener(),
                appInfraJSONRequest.getHeader(), appInfraJSONRequest.getParams(), appInfraJSONRequest.getTokenProviderInterface());*/


    }


    private void sendListener(Message msg) {
        if (null == mOrderListener) return;

        int requestCode = msg.what;

        switch (requestCode) {
            case RequestCode.GET_ORDERS:
                mOrderListener.onGetOrderList(msg);
                break;
            case RequestCode.GET_ORDER_DETAIL:
                mOrderListener.onGetOrderDetail(msg);
                break;
            case RequestCode.GET_PHONE_CONTACT:
                mOrderListener.onGetPhoneContact(msg);
                break;
        }
    }

    public ArrayList<ProductData> getProductData(List<ECSOrderDetail> orderDetail) {

        ArrayList<ProductData> products = new ArrayList<>();
        for(ECSOrderDetail detail : orderDetail) {
            if (detail.getDeliveryOrderGroups() != null) {
                List<Entries> entries = detail.getDeliveryOrderGroups().get(0).getEntries();
                for (Entries entry : entries) {
                    ProductData productItem = new ProductData(entry);
                    Data data = entry.getProduct().getSummary();
                    if(data!=null) {
                        setProductData(products, detail, entry, productItem, data);
                    }
                }
            }
        }

        return products;
    }

    public void setProductData(ArrayList<ProductData> products, ECSOrderDetail detail, Entries entry, ProductData productItem, Data data) {
        productItem.setImageURL(data.getImageURL());
        productItem.setProductTitle(data.getProductTitle());
        productItem.setQuantity(entry.getQuantity());
        productItem.setFormatedPrice(String.valueOf(entry.getTotalPrice().getFormattedValue()));
        productItem.setCtnNumber(entry.getProduct().getCode());
        productItem.setOrderCode(detail.getCode());
        productItem.setSubCategory(data.getSubcategory());
        productItem.setMarketingTextHeader(data.getMarketingTextHeader());
        ConsignmentEntries entries = getEntriesFromConsignMent(detail, entry.getProduct().getCode());
        productItem.setTrackOrderUrl(getOrderTrackUrl(entries));
        products.add(productItem);
    }

    public ConsignmentEntries getEntriesFromConsignMent(ECSOrderDetail detail, String ctn) {
        if (detail.getConsignments() == null) return null;
        for (Consignment consignment : detail.getConsignments()) {
            for (ConsignmentEntries entries : consignment.getEntries()) {
                String consignmentCtn = entries.getOrderEntry().getProduct().getCode();
                if (ctn.trim().equalsIgnoreCase(consignmentCtn.trim())) {
                    return entries;
                }
            }
        }
        return null;
    }


    public String getOrderTrackUrl(ConsignmentEntries entries) {
        if (entries == null) return null;
        if (isArrayNullOrEmpty(entries.getTrackAndTraceIDs()) || isArrayNullOrEmpty(entries.getTrackAndTraceUrls())) {
            return null;
        }
        String trackAndTraceID = entries.getTrackAndTraceIDs().get(0);
        String trackAndTraceUrl = entries.getTrackAndTraceUrls().get(0);
        return getTrackUrl(trackAndTraceID, trackAndTraceUrl);
    }

    private String getTrackUrl(String trackAndTraceID, String trackAndTraceUrl) {
        //sample URL
        //{300068874=http:\/\/www.fedex.com\/Tracking?action=track&cntry_code=us&tracknumber_list=300068874}
        String urlWithEndCurlyBrace = trackAndTraceUrl.replace("{" + trackAndTraceID + "=", "");
        return urlWithEndCurlyBrace.replace("}", "");
    }

    private boolean isArrayNullOrEmpty(List traceIdOrTraceURL) {
        return traceIdOrTraceURL == null || traceIdOrTraceURL.size() == 0;
    }




}
