/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.cdp.di.iap.controller;

import android.content.Context;
import android.os.Message;

import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.core.StoreSpec;
import com.philips.cdp.di.iap.model.AbstractModel;
import com.philips.cdp.di.iap.model.ContactCallRequest;
import com.philips.cdp.di.iap.model.OrderDetailRequest;
import com.philips.cdp.di.iap.model.OrderHistoryRequest;
import com.philips.cdp.di.iap.prx.PRXSummaryExecutor;
import com.philips.cdp.di.iap.response.orders.Entries;
import com.philips.cdp.di.iap.response.orders.OrderDetail;
import com.philips.cdp.di.iap.response.orders.ProductData;
import com.philips.cdp.di.iap.session.HybrisDelegate;
import com.philips.cdp.di.iap.session.RequestCode;
import com.philips.cdp.di.iap.utils.ModelConstants;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderController implements AbstractModel.DataLoadListener {

    private Context mContext;
    private OrderListener mOrderListener;
    private HybrisDelegate mDelegate;
    private StoreSpec mStore;

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
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CURRENT_PAGE, String.valueOf(pageNo));
        OrderHistoryRequest model = new OrderHistoryRequest(getStore(), query, this);
        model.setContext(mContext);
        getHybrisDelegate().sendRequest(RequestCode.GET_ORDERS, model, model);
    }

    public void getOrderDetails(String orderNumber) {
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.ORDER_NUMBER, orderNumber);
        OrderDetailRequest request = new OrderDetailRequest(getStore(), query, this);
        request.setContext(mContext);
        getHybrisDelegate().sendRequest(RequestCode.GET_ORDER_DETAIL, request, request);
    }

    public void getPhoneContact(String subCategory){
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.CATEGORY, subCategory);

        ContactCallRequest request = new ContactCallRequest(getStore(), query, this);
        getHybrisDelegate().sendRequest(RequestCode.GET_PHONE_CONTACT, request, request);
    }

    @Override
    public void onModelDataLoadFinished(Message msg) {
        sendListener(msg);
    }

    @Override
    public void onModelDataError(Message msg) {
        sendListener(msg);
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

    public void requestPrxData(final List<OrderDetail> details, AbstractModel.DataLoadListener listener)
    {
        ArrayList<String> ctnToBeRequested = new ArrayList<>();
        for (OrderDetail detail : details) {
            List<Entries> entries = detail.getDeliveryOrderGroups().get(0).getEntries();
            for (Entries entry : entries) {
                ctnToBeRequested.add(entry.getProduct().getCode());
            }
        }
        PRXSummaryExecutor builder = new PRXSummaryExecutor(mContext, ctnToBeRequested, listener);
        builder.preparePRXDataRequest();

    }

    public ArrayList<ProductData> getProductData(List<OrderDetail> orderDetail) {

        HashMap<String, SummaryModel> list = CartModelContainer.getInstance().getPRXSummaryList();
        ArrayList<ProductData> products = new ArrayList<>();
        String ctn;
        for(OrderDetail detail : orderDetail)
        {
            List<Entries> entries = detail.getDeliveryOrderGroups().get(0).getEntries();
            for (Entries entry : entries) {
                ctn = entry.getProduct().getCode();
                ProductData productItem = new ProductData(entry);
                Data data;
                if (list.containsKey(ctn)) {
                    data = list.get(ctn).getData();
                } else {
                    continue;
                }
                setProductData(products, detail, entry, productItem, data);
            }
        }

        return products;
    }

    public void setProductData(ArrayList<ProductData> products, OrderDetail detail, Entries entry, ProductData productItem, Data data) {
        productItem.setImageURL(data.getImageURL());
        productItem.setProductTitle(data.getProductTitle());
        productItem.setQuantity(entry.getQuantity());
        productItem.setFormatedPrice(String.valueOf(entry.getTotalPrice().getFormattedValue()));
        productItem.setCtnNumber(entry.getProduct().getCode());
        productItem.setOrderCode(detail.getCode());
        productItem.setSubCategory(data.getSubcategory());
        products.add(productItem);
    }

    public void setHybrisDelegate(HybrisDelegate delegate) {
        mDelegate = delegate;
    }

    HybrisDelegate getHybrisDelegate() {
        if (mDelegate == null) {
            mDelegate = HybrisDelegate.getInstance(mContext);
        }
        return mDelegate;
    }

    public void setStore(StoreSpec store) {
        mStore = store;
    }

    StoreSpec getStore() {
        if (mStore == null) {
            mStore = getHybrisDelegate().getStore();
        }
        return mStore;
    }
}
