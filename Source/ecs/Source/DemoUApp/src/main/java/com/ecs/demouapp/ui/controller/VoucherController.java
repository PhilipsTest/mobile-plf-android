package com.ecs.demouapp.ui.controller;

import android.content.Context;
import android.os.Message;


import com.ecs.demouapp.ui.session.RequestCode;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.error.ECSError;
import com.philips.platform.ecs.integration.ECSCallback;
import com.philips.platform.ecs.model.voucher.ECSVoucher;

import java.util.List;


public class VoucherController {

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


    public void applyCoupon(String voucherId) {

        ECSUtility.getInstance().getEcsServices().applyVoucher(voucherId, new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                Message message = new Message();
                message.obj = result;

                mVoucherListener.onApplyVoucherResponse(message);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                Message message = new Message();
                message.obj = error;

                mVoucherListener.onApplyVoucherResponse(message);
            }
        });
    }

    public void getAppliedVoucherCode() {

        ECSUtility.getInstance().getEcsServices().fetchAppliedVouchers(new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {

                Message message = new Message();
                message.obj = result;

                mVoucherListener.onGetAppliedVoucherResponse(message);

            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

                Message message = new Message();
                message.obj = error;
                mVoucherListener.onGetAppliedVoucherResponse(message);
            }
        });
    }

    public void getDeleteVoucher(String voucherCode){

        ECSUtility.getInstance().getEcsServices().removeVoucher(voucherCode, new ECSCallback<List<ECSVoucher>, Exception>() {
            @Override
            public void onResponse(List<ECSVoucher> result) {
                Message message = new Message();
                message.obj = result;

                mVoucherListener.onDeleteAppliedVoucherResponse(message);
            }

            @Override
            public void onFailure(Exception error, ECSError ecsError) {

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
