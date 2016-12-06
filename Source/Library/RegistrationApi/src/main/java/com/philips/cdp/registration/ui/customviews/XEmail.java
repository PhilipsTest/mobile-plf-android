/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.customviews;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.apptagging.AppTagging;
import com.philips.cdp.registration.apptagging.AppTagingConstants;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.RegistrationSettings;
import com.philips.cdp.registration.settings.RegistrationSettingsURL;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.RLog;

public class XEmail extends RelativeLayout implements TextWatcher, OnClickListener,
        OnFocusChangeListener {

    private Context mContext;

    private EditText mEtEmail;

    private TextView mTvErrDescriptionView;

    private boolean mValidEmail;

    private onUpdateListener mUpdateStatusListener;

    private RelativeLayout mRlEtEmail;

    private FrameLayout mFlInvalidFieldAlert;
    private String mSavedEmaillError;
    private String  country;
    public XEmail(Context context) {
        super(context);
        this.mContext = context;
        initUi(R.layout.reg_email);

    }

    public XEmail(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initUi(R.layout.reg_email);
        country = RegistrationHelper.getInstance().getCountryCode();
        checkingEmailorMobile();

    }

    public final void initUi(int resourceId) {
        RLog.d(RLog.SERVICE_DISCOVERY,"China Flow : "+ RegistrationHelper.getInstance().isChinaFlow());
        LayoutInflater li = LayoutInflater.from(mContext);
        li.inflate(resourceId, this, true);
        mRlEtEmail = (RelativeLayout) findViewById(R.id.rl_reg_parent_verified_field);
        mEtEmail = (EditText) findViewById(R.id.et_reg_email);
        mEtEmail.setOnClickListener(this);
        mEtEmail.setOnFocusChangeListener(this);
        mEtEmail.addTextChangedListener(this);
        mTvErrDescriptionView = (TextView) findViewById(R.id.tv_reg_email_err);
        mFlInvalidFieldAlert = (FrameLayout) findViewById(R.id.fl_reg_email_field_err);
    }

    private void checkingEmailorMobile() {
        //need to changed by service discover as 01 or 02
        if (RegistrationHelper.getInstance().isChinaFlow()) {
            mEtEmail.setHint(getResources().getString(R.string.reg_CreateAccount_PhoneNumber));
            mEtEmail.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else {
            mEtEmail.setHint(getResources().getString(R.string.reg_EmailAddPlaceHolder_txtField));
        }
    }

    public String getEmailId() {
        return mEtEmail.getText().toString().trim();
    }

    public boolean isValidEmail() {
        return mValidEmail;
    }

    public void setValidEmail(boolean mValidEmail) {
        this.mValidEmail = mValidEmail;
    }

    private boolean validateEmail() {
        if (mEtEmail != null) {
            //need to change by service discover
            if (RegistrationHelper.getInstance().isChinaFlow()) {
                if (isEmailandMobile()) return true;
            } else {
                if (isEmail()) return true;
            }
            setValidEmail(false);
            return false;
        }
        return false;
    }

    private boolean isEmail() {
        if (FieldsValidator.isValidEmail(mEtEmail.getText().toString().trim())) {
            setValidEmail(true);
            return true;
        }
        return false;
    }

    private boolean isEmailandMobile() {
        if (FieldsValidator.isValidEmail(mEtEmail.getText().toString().trim())){
            setValidEmail(true);
            return true;
        }else if(FieldsValidator.isValidMobileNumber(mEtEmail.getText().toString().trim())){
            setValidEmail(true);
            return true;
        }
        return false;
    }

    public void setErrDescription(String mErrDescription) {
        mTvErrDescriptionView.setText(mErrDescription);
        mSavedEmaillError = mErrDescription;
    }

    public String getSavedEmailErrDescription(){
        return mSavedEmaillError;
    }

    private void handleEmail(boolean hasFocus) {
        if (!hasFocus) {
            showEtEmailFocusDisable();
            mEtEmail.setFocusable(true);
        } else {
            showEtEmailFocusEnable();
        }
    }

    public void showEtEmailFocusEnable() {
        mRlEtEmail.setBackgroundResource(R.drawable.reg_et_focus_enable);
    }

    public void showEtEmailFocusDisable() {
        mRlEtEmail.setBackgroundResource(R.drawable.reg_et_focus_disable);
    }

    private void showEmailIsInvalidAlert() {
        mRlEtEmail.setBackgroundResource(R.drawable.reg_et_focus_error);
        mEtEmail.setTextColor(ContextCompat.getColor(mContext,R.color.reg_error_box_color));
        mFlInvalidFieldAlert.setVisibility(VISIBLE);
        mTvErrDescriptionView.setVisibility(VISIBLE);
    }

    public void showValidEmailAlert() {
        mRlEtEmail.setBackgroundResource(R.drawable.reg_et_focus_disable);
        mEtEmail.setTextColor(ContextCompat.getColor(mContext,R.color.reg_edt_text_feild_color));
        mFlInvalidFieldAlert.setVisibility(GONE);
        mTvErrDescriptionView.setVisibility(GONE);
    }

    public void showInvalidAlert() {
        mEtEmail.setTextColor( ContextCompat.getColor(mContext,R.color.reg_error_box_color));
        mRlEtEmail.setBackgroundResource(R.drawable.reg_et_focus_error);
        mFlInvalidFieldAlert.setVisibility(VISIBLE);
    }

    public void setOnUpdateListener(onUpdateListener updateStatusListener) {
        mUpdateStatusListener = updateStatusListener;
    }

    private void raiseUpdateUIEvent() {
        if (null != mUpdateStatusListener) {
            mUpdateStatusListener.onUpadte();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mEtEmail.setTextColor(ContextCompat.getColor(mContext,R.color.reg_edt_text_feild_color));
        if (v.getId() == R.id.et_reg_email) {
            handleEmail(hasFocus);
            raiseUpdateUIEvent();
            if (!hasFocus) {
                handleOnFocusChanges();
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    public void showErrPopUp() {
        mTvErrDescriptionView.setVisibility(View.VISIBLE);
    }

    public boolean isEmailErrorVisible(){
        if(mTvErrDescriptionView.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (validateEmail()) {
            showValidEmailAlert();
        } else {
            if (mEtEmail.getText().toString().trim().length() == 0) {
                setErrDescription(getResources().getString(R.string.reg_EmptyField_ErrorMsg));
            } else {
                if (RegistrationHelper.getInstance().isChinaFlow()) {
                    setErrDescription(getResources().getString(R.string.reg_Invalid_PhoneNumber_ErrorMsg));
                }else {
                    setErrDescription(getResources().getString(R.string.reg_InvalidEmailAdddress_ErrorMsg));
                }
            }
        }
    }

    private void handleOnFocusChanges() {

        if (validateEmail()) {
            showValidEmailAlert();
            mValidEmail = true;
        } else {
            if (mEtEmail.getText().toString().trim().length() == 0) {
                AppTagging.trackAction(AppTagingConstants.SEND_DATA,
                        AppTagingConstants.USER_ALERT,
                        AppTagingConstants.FIELD_CANNOT_EMPTY_EMAIL);
                setErrDescription(getResources().getString(R.string.reg_EmptyField_ErrorMsg));
            } else {
                if (RegistrationHelper.getInstance().isChinaFlow()){
                    AppTagging.trackAction(AppTagingConstants.SEND_DATA,
                            AppTagingConstants.USER_ALERT, AppTagingConstants.INVALID_MOBILE);
                    setErrDescription(getResources().getString(R.string.reg_Invalid_PhoneNumber_ErrorMsg));
                }else {

                    AppTagging.trackAction(AppTagingConstants.SEND_DATA,
                            AppTagingConstants.USER_ALERT, AppTagingConstants.INVALID_EMAIL);
                    setErrDescription(getResources().getString(R.string.reg_InvalidEmailAdddress_ErrorMsg));
                }


            }
            showEmailIsInvalidAlert();
        }
    }


    @Override
    public void afterTextChanged(Editable s) {
        raiseUpdateUIEvent();
        if (validateEmail()) {
            if (mTvErrDescriptionView != null && mFlInvalidFieldAlert != null) {
                mTvErrDescriptionView.setVisibility(GONE);
                mFlInvalidFieldAlert.setVisibility(GONE);
            }
        }
    }

    public void setHint(String hintText) {
        if (mEtEmail != null) {
            mEtEmail.setHint(hintText);
        }
    }

    public void setClickableTrue(boolean isClickable) {
        if (mEtEmail != null) {
            mEtEmail.setClickable(isClickable);
            mEtEmail.setEnabled(isClickable);
        }
    }

    public boolean isShown() {
        if (mEtEmail != null && mEtEmail.isShown()) {
            return true;
        } else {
            return false;
        }
    }

    public void setImeOptions(int option) {
        mEtEmail.setImeOptions(option);
    }


}
