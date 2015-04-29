/*----------------------------------------------------------------------------
Copyright(c) Philips Electronics India Ltd
All rights reserved. Reproduction in whole or in part is prohibited without
the written consent of the copyright holder.

Project           : SaecoAvanti
----------------------------------------------------------------------------*/

package com.philips.cl.di.reg.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.cl.di.reg.R;

public class XRegError extends RelativeLayout {

	private Context mContext;

	private TextView mTvError;

	public XRegError(Context context) {
		super(context);
		mContext = context;
		initUi(R.layout.error_mapping);
	}

	public XRegError(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initUi(R.layout.error_mapping);
	}

	private void initUi(int resourceId) {

		/** inflate amount layout */
		LayoutInflater li = LayoutInflater.from(mContext);
		li.inflate(resourceId, this, true);

		mTvError = (XTextView) findViewById(R.id.tv_error_message);
	}

	public void setError(String errorMsg) {
		mTvError.setText(errorMsg);
		setVisibility(VISIBLE);
	}

	public void hideError() {
		setVisibility(GONE);
	}

}
