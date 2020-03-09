package com.philips.cdp.di.iap.screens;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.AppliedVoucherAdapter;
import com.philips.cdp.di.iap.analytics.IAPAnalytics;
import com.philips.cdp.di.iap.analytics.IAPAnalyticsConstant;
import com.philips.cdp.di.iap.controller.VoucherController;
import com.philips.cdp.di.iap.eventhelper.EventHelper;
import com.philips.cdp.di.iap.eventhelper.EventListener;
import com.philips.cdp.di.iap.response.voucher.GetAppliedValue;
import com.philips.cdp.di.iap.response.voucher.Vouchers;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.utils.AlertListener;
import com.philips.cdp.di.iap.utils.IAPConstant;
import com.philips.cdp.di.iap.utils.NetworkUtility;
import com.philips.cdp.di.iap.utils.Utility;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.EditText;
import com.philips.platform.uid.view.widget.Label;

import java.util.List;


public class VoucherFragment extends InAppBaseFragment implements View.OnClickListener,VoucherController.VoucherListener ,EventListener ,AlertListener{


    VoucherController mVoucherController;
    Button mApplyVoucherButton;
    Context mContext;
    EditText voucherEditText;
    RelativeLayout voucherLayout,voucherBottomLayout;
    public AppliedVoucherAdapter mAppliedVoucherAdapter;
    private RecyclerView mRecyclerView;
    Label acceptedCodeLabel,totalCost;
    ScrollView headerParent;
    View line;
    List<Vouchers> getAppliedValueList;
    public static final String TAG = VoucherFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventHelper.getInstance().registerEventNotification(IAPConstant.IAP_DELETE_VOUCHER, this);
        EventHelper.getInstance().registerEventNotification(IAPConstant.IAP_DELETE_VOUCHER_CONFIRM, this);

        View rootView = inflater.inflate(R.layout.iap_voucher_detail, container, false);
        voucherLayout = (RelativeLayout) rootView.findViewById(R.id.voucher_detail_layout);
        mApplyVoucherButton = (Button) rootView.findViewById(R.id.voucher_btn);
        voucherEditText = (EditText) rootView.findViewById(R.id.coupon_editText);
        voucherEditText.addTextChangedListener(new IAPTextWatcher(voucherEditText));
        Label voucherCodeLabel = (Label) rootView.findViewById(R.id.enter_voucher_label);
        mApplyVoucherButton.setEnabled(false);
        mApplyVoucherButton.setOnClickListener(this);
        mVoucherController = new VoucherController(mContext, VoucherFragment.this);
        mRecyclerView = rootView.findViewById(R.id.applied_voucher_cart_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        acceptedCodeLabel=(Label) rootView.findViewById(R.id.iap_accepted_code);
        headerParent=(ScrollView)rootView.findViewById(R.id.sv_header_parent);
        line=(View) rootView.findViewById(R.id.line);
        voucherBottomLayout=(RelativeLayout)rootView.findViewById(R.id.voucher_bottom_layout);
        totalCost=(Label) rootView.findViewById(R.id.total_cost_val);
        mVoucherController.getAppliedVoucherCode();



        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndBackButtonVisibility(R.string.iap_apply_gift, true);
        IAPAnalytics.trackPage(IAPAnalyticsConstant.APPLY_VOUCHER);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAppliedVoucherAdapter != null)
            mAppliedVoucherAdapter.onStop();
        hideProgressBar();
        NetworkUtility.getInstance().dismissErrorDialog();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.voucher_btn) {

            if (null != voucherEditText.getText().toString()) {
                createCustomProgressBar(voucherLayout, BIG);
                mVoucherController.applyCoupon(voucherEditText.getText().toString().trim());
                voucherEditText.getText().clear();
            }
        }
    }

    @Override
    public void onApplyVoucherResponse(Message msg) {
        hideProgressBar();
        if(null==msg.obj) {
            /*Toast.makeText(getActivity(), "Vouchers Applied Successfully",
                    Toast.LENGTH_SHORT).show();*/
            // mVoucherFragment.getActivity().getSupportFragmentManager().popBackStackImmediate();
            NetworkUtility.getInstance().showVoucherSuccessMessage(msg, getFragmentManager(),mContext);
            mVoucherController.getAppliedVoucherCode();
        }
        if ((msg.obj instanceof IAPNetworkError)) {
            NetworkUtility.getInstance().showVoucherErrorMessage(msg, getFragmentManager(),mContext);
        }
    }

    @Override
    public void onGetAppliedVoucherResponse(Message msg) {
        hideProgressBar();
        Double count = 0.00;
        if(msg.obj instanceof GetAppliedValue){
            GetAppliedValue getAppliedValue = (GetAppliedValue) msg.obj;
            getAppliedValueList = getAppliedValue.getVouchers();
            mAppliedVoucherAdapter=new AppliedVoucherAdapter(mContext,getAppliedValueList);
            mRecyclerView.setAdapter(mAppliedVoucherAdapter);



          if(getAppliedValueList.size()>0){
              acceptedCodeLabel.setVisibility(View.VISIBLE);
              headerParent.setVisibility(View.VISIBLE);
          }
          else {
              acceptedCodeLabel.setVisibility(View.GONE);
              headerParent.setVisibility(View.GONE);
          }
          for(int i=0; i<getAppliedValueList.size();i++){
              count=count+Double.parseDouble(getAppliedValueList.get(i).getAppliedValue().getValue());

          }

            String sValue = (String) String.format("%.2f", count);
            totalCost.setText("$ "+sValue);
            mAppliedVoucherAdapter.notifyDataSetChanged();
        }else {
            acceptedCodeLabel.setVisibility(View.GONE);
            headerParent.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDeleteAppliedVoucherResponse(Message msg) {
        hideProgressBar();
        if(mAppliedVoucherAdapter.getSelectedItemPosition()!=-1){
            getAppliedValueList.remove(mAppliedVoucherAdapter.getSelectedItemPosition());
            mAppliedVoucherAdapter.notifyItemRemoved(mAppliedVoucherAdapter.getSelectedItemPosition());
            mAppliedVoucherAdapter.notifyItemChanged(mAppliedVoucherAdapter.getSelectedItemPosition());
        }
        mVoucherController.getAppliedVoucherCode();

    }

    @Override
    public void onEventReceived(String event) {
        hideProgressBar();
        if (event.equalsIgnoreCase(IAPConstant.IAP_DELETE_VOUCHER)) {
            Utility.showActionDialog(mContext, getString(R.string.iap_delete_voucher), getString(R.string.iap_cancel)
                    , getString(R.string.iap_delete_voucher), getString(R.string.iap_delete_voucher_sure), getFragmentManager(), this);
        }else if (event.equalsIgnoreCase(IAPConstant.IAP_DELETE_VOUCHER_CONFIRM)) {
            if(mAppliedVoucherAdapter.getSelectedItemPosition()!=-1){
               createCustomProgressBar(voucherLayout, BIG);
               mVoucherController.getDeleteVoucher( getAppliedValueList.get(mAppliedVoucherAdapter.getSelectedItemPosition()).getVoucherCode());
            }




        }
    }

    @Override
    public void onPositiveBtnClick() {
        EventHelper.getInstance().notifyEventOccurred(IAPConstant.IAP_DELETE_VOUCHER_CONFIRM);
    }

    @Override
    public void onNegativeBtnClick() {

    }


    private class IAPTextWatcher implements TextWatcher {
        private android.widget.EditText mEditText;

        IAPTextWatcher(android.widget.EditText editText) {
            mEditText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //  Do nothing
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (voucherEditText .getText().toString().trim().length() !=0) {
                    mApplyVoucherButton.setEnabled(true);
                }
                else {
                    mApplyVoucherButton.setEnabled(false);
                }
        }

        public synchronized void afterTextChanged(Editable text) {
            if (voucherEditText .getText().toString().trim().length() ==0) {
                mApplyVoucherButton.setEnabled(false);
            }
        }
    }

}
