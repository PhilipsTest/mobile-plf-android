
package com.philips.cdp.registration.ui.customviews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.ui.utils.EmailValidator;
import com.philips.cdp.registration.ui.utils.RegUtility;

public class XEmail extends RelativeLayout implements TextWatcher, OnClickListener,
        OnFocusChangeListener {

	private Context mContext;

	private ImageView mIvEmailErrAlert;

	private EditText mEtEmail;

	private TextView mTvErrDescriptionView;

	private boolean mValidEmail;

	private ImageView mIvArrowUpView;

	private onUpdateListener mUpdateStatusListener;

	private RelativeLayout mRlEtEmail;

	public XEmail(Context context) {
		super(context);
		this.mContext = context;
		initUi(R.layout.x_email);
	}

	public XEmail(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initUi(R.layout.x_email);
	}

	public final void initUi(int resourceId) {
		LayoutInflater li = LayoutInflater.from(mContext);
		li.inflate(resourceId, this, true);
		mRlEtEmail = (RelativeLayout) findViewById(R.id.rl_reg_parent_verified_field);
		mEtEmail = (EditText) findViewById(R.id.et_reg_email);
		mEtEmail.setOnClickListener(this);
		mEtEmail.setOnFocusChangeListener(this);
		mEtEmail.addTextChangedListener(this);
		mTvErrDescriptionView = (TextView) findViewById(R.id.tv_reg_name_err);
		mIvEmailErrAlert = (ImageView) findViewById(R.id.iv_reg_email_error_alert);
		mIvEmailErrAlert.setOnClickListener(this);
		mIvArrowUpView = (ImageView) findViewById(R.id.iv_reg_up_arrow);

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
		if (!EmailValidator.isValidEmail(mEtEmail.getText().toString().trim())) {
			setValidEmail(false);
			return false;
		}
		setValidEmail(true);
		return true;
	}

	public void setErrDescription(String mErrDescription) {
		mTvErrDescriptionView.setText(mErrDescription);
	}

	private void handleEmail(boolean hasFocus) {
		if (!hasFocus) {
			showEtEmailFocusDisable();
			mEtEmail.setFocusable(true);
			if (mEtEmail.getText().toString().trim().length() == 0) {
				showEmailInvalidAlert();
			}
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

	public void showEmailInvalidAlert() {
		mIvEmailErrAlert.setVisibility(View.VISIBLE);
	}

	public void showInvalidAlert() {
		mIvEmailErrAlert.setVisibility(View.VISIBLE);
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
		if (v.getId() == R.id.et_reg_email) {
			handleEmail(hasFocus);
			raiseUpdateUIEvent();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_reg_email_error_alert) {
			toggleErrorUi();
		}

	}

	private void toggleErrorUi() {
		if (mTvErrDescriptionView.isShown()) {
			hideErrPopUp();
		} else {
			showErrPopUp();
		}
	}

	public void showErrPopUp() {
		mTvErrDescriptionView.setVisibility(View.VISIBLE);
		mIvArrowUpView.setVisibility(View.VISIBLE);
	}

	private void hideErrPopUp() {
		mTvErrDescriptionView.setVisibility(View.GONE);
		mIvArrowUpView.setVisibility(View.GONE);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (validateEmail()) {
			RegUtility.invalidalertvisibilitygone(mEtEmail);
			mIvEmailErrAlert.setVisibility(View.GONE);
			mValidEmail = true;
		} else {
			RegUtility.invalidalertvisibilityview(mEtEmail);
			if (mEtEmail.getText().toString().trim().length() == 0) {
				setErrDescription(getResources().getString(R.string.EmptyField_ErrorMsg));
			} else {
				setErrDescription(getResources().getString(R.string.InvalidEmailAdddress_ErrorMsg));
			}
			mIvEmailErrAlert.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		raiseUpdateUIEvent();
		if (validateEmail()) {
			mTvErrDescriptionView.setVisibility(View.GONE);
			mIvArrowUpView.setVisibility(View.GONE);

		}
	}
}
