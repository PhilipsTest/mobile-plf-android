/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.ecs.demouapp.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.utils.ECSConstant;
import com.philips.platform.ecs.model.payment.ECSPayment;


public class CvvCvcDialogFragment extends DialogFragment {
    protected static final int REQUEST_CODE = 0;
    private String mCvv;
    private EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecs_cvc_code_dailog, container, false);
        initializeViews(view);
        return view;
    }

    void initializeViews(View view) {
        Bundle bundle = getArguments();
        ECSPayment mPaymentMethod = null;
        if (bundle.containsKey(ECSConstant.SELECTED_PAYMENT)) {
            mPaymentMethod = (ECSPayment) bundle.getSerializable(ECSConstant.SELECTED_PAYMENT);
        }
        TextView cardNumber = view.findViewById(R.id.tv_cvv_card_number);
        if (mPaymentMethod != null) {
            cardNumber.setText(mPaymentMethod.getCardType().getCode() +" "+mPaymentMethod.getCardNumber());
        }
        final Button continueBtn = view.findViewById(R.id.continue_btn);
        final Button notNowBtn = view.findViewById(R.id.not_now_btn);
        mEditText = view.findViewById(R.id.et_cvv_digits);
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

        notNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mEditText.requestFocus();
    }

    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.putExtra(ECSConstant.CVV_KEY_BUNDLE, mCvv);
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }

}
