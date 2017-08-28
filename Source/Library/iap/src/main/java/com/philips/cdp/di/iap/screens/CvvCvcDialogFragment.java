/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.response.payment.PaymentMethod;
import com.philips.cdp.di.iap.utils.IAPConstant;

public class CvvCvcDialogFragment extends DialogFragment {
    protected static final int REQUEST_CODE = 0;
    private String mCvv;
    private EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iap_cvc_code_dailog, container, false);
        Bundle bundle = getArguments();
        PaymentMethod mPaymentMethod = null;
        if (bundle.containsKey(IAPConstant.SELECTED_PAYMENT)) {
            mPaymentMethod = (PaymentMethod) bundle.getSerializable(IAPConstant.SELECTED_PAYMENT);
        }
        TextView cardNumber = (TextView) view.findViewById(R.id.tv_cvv_card_number);
        if (mPaymentMethod != null) {
            cardNumber.setText(mPaymentMethod.getCardType().getCode() +" "+mPaymentMethod.getCardNumber());
        }
        final Button continueBtn = (Button) view.findViewById(R.id.continue_btn);
        final Button notNotBtn = (Button) view.findViewById(R.id.not_now_btn);
        mEditText = (EditText) view.findViewById(R.id.et_cvv_digits);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOP
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    continueBtn.setEnabled(false);
                } else {
                    continueBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //NOP
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCvv = mEditText.getText().toString();
                dismiss();
                setShowsDialog(false);
                sendResult(REQUEST_CODE);
            }
        });

        notNotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mEditText.requestFocus();
        return view;
    }

    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.putExtra(IAPConstant.CVV_KEY_BUNDLE, mCvv);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }

}
