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
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.app.tagging.AppTagging;
import com.philips.cdp.registration.app.tagging.AppTagingConstants;
import com.philips.cdp.registration.ui.utils.FieldsValidator;
import com.philips.cdp.registration.ui.utils.FontLoader;
import com.philips.cdp.registration.ui.utils.RegConstants;

public class XUserName extends RelativeLayout implements TextWatcher, OnFocusChangeListener,
        OnClickListener {

	private Context mContext;

	private EditText mEtUserName;

	private boolean mValidName;

	private OnUpdateListener mUpdateStatusListener;

	private RelativeLayout mRlEtName;

	private TextView mTvErrDescriptionView;

	private FrameLayout mFlInvaliFielddAlert;

	private TextView mTvCloseIcon;

	public XUserName(Context context) {
		super(context);
		this.mContext = context;
		initUi(R.layout.reg_user_name);
	}

	public XUserName(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initUi(R.layout.reg_user_name);
	}

	public final void initUi(int resourceId) {

		/** inflate amount layout */
		LayoutInflater li = LayoutInflater.from(mContext);
		li.inflate(resourceId, this, true);

		mEtUserName = (EditText) findViewById(R.id.et_reg_fname);
		mEtUserName.setOnFocusChangeListener(this);
		mEtUserName.addTextChangedListener(this);
		mRlEtName = (RelativeLayout) findViewById(R.id.rl_reg_parent_verified_field);

		mTvErrDescriptionView = (TextView) findViewById(R.id.tv_reg_email_err);
		mFlInvaliFielddAlert = (FrameLayout)findViewById(R.id.fl_reg_name_field_err);
		mTvCloseIcon = (TextView) findViewById(R.id.iv_reg_close);
		FontLoader.getInstance().setTypeface(mTvCloseIcon, RegConstants.PUIICON_TTF);
	}

	@Override
	public void onClick(View v) {
	}

	public void setOnUpdateListener(OnUpdateListener updateStatusListener) {
		mUpdateStatusListener = updateStatusListener;
	}

	private void raiseUpdateUIEvent() {
		if (null != mUpdateStatusListener) {
			mUpdateStatusListener.onUpdate();
		}
	}

	private void showInvalidUserNameAlert() {
		mEtUserName.setTextColor( ContextCompat.getColor(mContext,R.color.reg_error_box_color));
		mRlEtName.setBackgroundResource(R.drawable.reg_et_focus_error);
		mFlInvaliFielddAlert.setVisibility(View.VISIBLE);
		mTvErrDescriptionView.setVisibility(VISIBLE);
	}

	private void showValidUserNameAlert() {
		mFlInvaliFielddAlert.setVisibility(GONE);
		mTvErrDescriptionView.setVisibility(GONE);
	}

	private boolean validateName() {
		if (!FieldsValidator.isValidName(mEtUserName.getText().toString().trim())) {
			setValidName(false);
			return false;
		}
		setValidName(true);
		return true;
	}

	public String getName() {
		return mEtUserName.getText().toString().trim();
	}

	public boolean isValidName() {
		return mValidName;
	}

	public void setValidName(boolean mValidName) {
		this.mValidName = mValidName;
	}

	private void handleName(boolean hasFocus) {
		if (!hasFocus) {
			showNameEtFocusDisable();
			mEtUserName.setFocusable(true);
		} else {
			showEtNameFocusEnable();
		}
	}

	public void setErrDescription(String mErrDescription) {
		mTvErrDescriptionView.setText(mErrDescription);
	}

	public void showEtNameFocusEnable() {
		mRlEtName.setBackgroundResource(R.drawable.reg_et_focus_enable);
	}

	public void showNameEtFocusDisable() {
		mRlEtName.setBackgroundResource(R.drawable.reg_et_focus_disable);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		mEtUserName.setTextColor( ContextCompat.getColor(mContext,R.color.reg_edit_text_field_color));
		if (v.getId() == R.id.et_reg_fname) {
			handleName(hasFocus);
			raiseUpdateUIEvent();
			if(!hasFocus){
				handleOnFocusChanges();}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (validateName()) {
			showValidUserNameAlert();
		} else {
			if (mEtUserName.getText().toString().trim().length() == 0) {
				setErrDescription(getResources().getString(R.string.reg_EmptyField_ErrorMsg));
			}
		}
	}

	private void handleOnFocusChanges() {
		if (validateName()) {
			showValidUserNameAlert();
		} else {
			if (mEtUserName.getText().toString().trim().length() == 0) {
				AppTagging.trackAction(AppTagingConstants.SEND_DATA,AppTagingConstants.USER_ALERT,AppTagingConstants.FIELD_CANNOT_EMPTY_NAME);
				setErrDescription(getResources().getString(R.string.reg_EmptyField_ErrorMsg));
				showInvalidUserNameAlert();
			}

		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (validateName()) {
			mTvErrDescriptionView.setVisibility(View.GONE);
			mFlInvaliFielddAlert.setVisibility(GONE);
		}
		raiseUpdateUIEvent();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cancelLongPress();
		onCancelPendingInputEvents();
		return super.onTouchEvent(event);
	}
}
