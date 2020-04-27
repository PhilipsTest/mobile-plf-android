/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.ecs.demouapp.ui.controller;

import android.content.Context;
import android.os.Message;
import android.util.Log;


import com.ecs.demouapp.ui.address.AddressFields;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.orders.ECSOrderDetail;
import com.philips.platform.ecs.model.payment.ECSPayment;
import com.philips.platform.ecs.model.payment.ECSPaymentProvider;

import java.util.List;

public class PaymentController {

    private Context mContext;
    private PaymentListener mPaymentListener;
    private MakePaymentListener mMakePaymentListener;

    public interface PaymentListener {
        void onGetPaymentDetails(Message msg);

        void onSetPaymentDetails(Message msg);
    }

    public interface MakePaymentListener {
        void onMakePayment(Message msg);

        void onPlaceOrder(Message msg);
    }

    public PaymentController(Context context, PaymentListener listener) {
        mContext = context;
        mPaymentListener = listener;
    }

    public PaymentController(Context context, MakePaymentListener listener) {
        mContext = context;
        mMakePaymentListener = listener;
    }

    public void getPaymentDetails() {

        ECSUtility.getInstance().getEcsServices().fetchPaymentsDetails(new ECSCallback<List<ECSPayment>, Exception>() {
            @Override
            public void onResponse(List<ECSPayment> result) {


                Message message = new Message();
                if( result==null || result.isEmpty()){
                    message.obj = "";
                }else{
                    message.obj = result;
                }
                mPaymentListener.onGetPaymentDetails(message);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                message.obj = error;
                mPaymentListener.onGetPaymentDetails(message);
            }
        });

    }

    public void setPaymentDetails(String paymentId) {

        ECSUtility.getInstance().getEcsServices().setPaymentDetails(paymentId, new ECSCallback<Boolean, Exception>() {
            @Override
            public void onResponse(Boolean result) {
                Message message = new Message();
                message.obj = result;
                mPaymentListener.onSetPaymentDetails(message);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                message.obj = error;
                mPaymentListener.onSetPaymentDetails(message);
            }
        });

    }

    public void placeOrder(String pSecurityCode) {
       /* final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);
        HashMap<String, String> query = new HashMap<>();
        if (pSecurityCode != null)
            query.put(ModelConstants.SECURITY_CODE, pSecurityCode);*/


        ////
        ECSUtility.getInstance().getEcsServices().submitOrder(pSecurityCode, new ECSCallback<ECSOrderDetail, Exception>() {
            @Override
            public void onResponse(ECSOrderDetail result) {
                Log.v("submitOrder","success");
                Message message = new Message();
                message.obj=result;
                mMakePaymentListener.onPlaceOrder(message);


            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {
                Message message = new Message();
                message.obj=error.getMessage();
                mMakePaymentListener.onPlaceOrder(message);

            }
        });

       /* PlaceOrderRequest request = new PlaceOrderRequest(delegate.getStore(), query, this);
        delegate.sendRequest(RequestCode.PLACE_ORDER, request, request);*/
    }

    public void makPayment(ECSOrderDetail orderDetail) {

/*
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.ORDER_NUMBER, orderID);

        final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);


        PaymentRequest request = new PaymentRequest(delegate.getStore(), query, this);
        delegate.sendRequest(RequestCode.MAKE_PAYMENT, request, request);*/

        AddressFields address = CartModelContainer.getInstance().getBillingAddress();
       ECSAddress billingAddress = AddressController.getAddressesObject(address);
        billingAddress.setId(CartModelContainer.getInstance().getAddressId());
      //  params.put(ModelConstants.ADDRESS_ID, CartModelContainer.getInstance().getAddressId());
        //////////////////////////////

      ECSUtility.getInstance().getEcsServices().makePayment(orderDetail, billingAddress, new ECSCallback<ECSPaymentProvider, Exception>() {
          @Override
          public void onResponse(ECSPaymentProvider result) {
              Log.v("makePayment","success");
              Message message = new Message();
              message.obj=result;
              mMakePaymentListener.onMakePayment(message);
          }

          @Override
          public void onFailure(Exception error, ECSError ecsError) {
              Message message = new Message();
              message.obj=error;
              mMakePaymentListener.onMakePayment(message);
          }
      });

    }


    private void sendListener(Message msg) {
        int requestCode = msg.what;
        switch (requestCode) {
            case RequestCode.GET_PAYMENT_DETAILS:
                mPaymentListener.onGetPaymentDetails(msg);
                break;
            case RequestCode.SET_PAYMENT_DETAILS:
                mPaymentListener.onSetPaymentDetails(msg);
                break;
            case RequestCode.PLACE_ORDER:
                mMakePaymentListener.onPlaceOrder(msg);
                break;
            case RequestCode.MAKE_PAYMENT:
                mMakePaymentListener.onMakePayment(msg);
                break;
        }
    }
}
