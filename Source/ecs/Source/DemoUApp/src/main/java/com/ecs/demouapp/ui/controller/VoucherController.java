package com.ecs.demouapp.ui.controller;

import android.content.Context;
import android.os.Message;


import com.ecs.demouapp.ui.model.AbstractModel;
import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.cdp.di.ecs.integration.ECSCallback;
import com.philips.cdp.di.ecs.model.voucher.GetAppliedValue;


public class VoucherController implements AbstractModel.DataLoadListener {

    private Context mContext;
    private VoucherListener mVoucherListener ;

    public interface VoucherListener {
        void onApplyVoucherResponse(Message msg);
        void onGetAppliedVoucherResponse(Message msg);
        void onDeleteAppliedVoucherResponse(Message msg);
    }

    public VoucherController(Context mContext, VoucherListener voucherListener) {
        this.mContext = mContext;
        this.mVoucherListener=voucherListener;
    }

    @Override
    public void onModelDataLoadFinished(Message msg) {
        sendListener(msg);
    }

    @Override
    public void onModelDataError(Message msg) {
        sendListener(msg);
    }

    public void applyCoupon(String voucherId) {
       /* final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);
        HashMap<String, String> query = new HashMap<>();
        query.put(ModelConstants.VOUCHER_ID, voucherId);
        GetApplyVoucherRequest request = new GetApplyVoucherRequest(delegate.getStore(), query, this);
        delegate.sendRequest(APPLY_VOUCHER, request, request);*/

        ECSUtility.getInstance().getEcsServices().setVoucher(voucherId, new ECSCallback<GetAppliedValue, Exception>() {
            @Override
            public void onResponse(GetAppliedValue result) {
                Message message = new Message();
                message.obj = result;

                mVoucherListener.onApplyVoucherResponse(message);
            }

            @Override
            public void onFailure(Exception error, String detailErrorMessage, int errorCode) {

                Message message = new Message();
                message.obj = error;

                mVoucherListener.onApplyVoucherResponse(message);
            }
        });
    }

    public void getAppliedVoucherCode() {

       /* final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);
        GetAppliedVoucherRequest request = new GetAppliedVoucherRequest(delegate.getStore(), null,this);
        delegate.sendRequest(GET_APPLIED_VOUCHER, request, request);*/

        ECSUtility.getInstance().getEcsServices().getVoucher(new ECSCallback<GetAppliedValue, Exception>() {
            @Override
            public void onResponse(GetAppliedValue result) {

                Message message = new Message();
                message.obj = result;

                mVoucherListener.onGetAppliedVoucherResponse(message);

            }

            @Override
            public void onFailure(Exception error, String detailErrorMessage, int errorCode) {

                Message message = new Message();
                message.obj = error;
                mVoucherListener.onGetAppliedVoucherResponse(message);
            }
        });
    }

    public void getDeleteVoucher(String voucherCode){
       /* final HybrisDelegate delegate = HybrisDelegate.getInstance(mContext);

        DeleteVoucherRequest deleteVoucherRequest = new DeleteVoucherRequest(delegate.getStore(), null, this, voucherCode);
        delegate.sendRequest(DELETE_VOUCHER, deleteVoucherRequest, deleteVoucherRequest);*/

        ECSUtility.getInstance().getEcsServices().removeVoucher(voucherCode, new ECSCallback<GetAppliedValue, Exception>() {
            @Override
            public void onResponse(GetAppliedValue result) {
                Message message = new Message();
                message.obj = result;

                mVoucherListener.onDeleteAppliedVoucherResponse(message);
            }

            @Override
            public void onFailure(Exception error, String detailErrorMessage, int errorCode) {

                Message message = new Message();
                message.obj = error;

                mVoucherListener.onDeleteAppliedVoucherResponse(message);
            }
        });
    }

    private void sendListener(Message msg) {
        int requestCode = msg.what;
        switch (requestCode) {
            case RequestCode.APPLY_VOUCHER:
                mVoucherListener.onApplyVoucherResponse(msg);
                break;
            case RequestCode.GET_APPLIED_VOUCHER:
                mVoucherListener.onGetAppliedVoucherResponse(msg);
                break;
            case RequestCode. DELETE_VOUCHER:
                mVoucherListener.onDeleteAppliedVoucherResponse(msg);
                break;

        }
    }
}
