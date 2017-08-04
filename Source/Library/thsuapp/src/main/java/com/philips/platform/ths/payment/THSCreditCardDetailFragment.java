/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Button;
import com.philips.platform.uid.view.widget.EditText;


public class THSCreditCardDetailFragment extends THSBaseFragment implements View.OnClickListener {
    public static final String TAG = THSCreditCardDetailFragment.class.getSimpleName();
    private ActionBarListener actionBarListener;


    THSCreditCardDetailPresenter mTHSCreditCardDetailPresenter;
    private RelativeLayout mProgressbarContainer;

    EditText mCardHolderNameEditText;
    EditText mCardNumberEditText;
    EditText mCardExpiryMonthEditText;
    EditText mCardExpiryYearEditText;
    EditText mCVCcodeEditText;
    private Button mPaymentDetailContinueButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_payment_detail, container, false);
        mTHSCreditCardDetailPresenter = new THSCreditCardDetailPresenter(this);
        mCardHolderNameEditText = (EditText) view.findViewById(R.id.ths_payment_detail_card_holder_name_edittext);
        mCardNumberEditText = (EditText) view.findViewById(R.id.ths_payment_detail_card_number_edittext);
        mCardExpiryMonthEditText = (EditText) view.findViewById(R.id.ths_payment_detail_card_expiration_month_edittext);
        mCardExpiryYearEditText = (EditText) view.findViewById(R.id.ths_payment_detail_card_expiration_year_edittext);
        mCVCcodeEditText = (EditText) view.findViewById(R.id.ths_payment_detail_card_cvc_edittext);
        mPaymentDetailContinueButton = (Button) view.findViewById(R.id.ths_payment_detail_continue_button);
        mPaymentDetailContinueButton.setOnClickListener(this);
        mProgressbarContainer = (RelativeLayout) view.findViewById(R.id.ths_payment_detail_container);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionBarListener = getActionBarListener();
        createCustomProgressBar(mProgressbarContainer, BIG);
        mTHSCreditCardDetailPresenter.getPaymentMethod();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != actionBarListener) {
            actionBarListener.updateActionBar("Payment method", true);
        }

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ths_payment_detail_continue_button) ;
        mTHSCreditCardDetailPresenter.onEvent(R.id.ths_payment_detail_continue_button);
    }
}
