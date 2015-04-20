
package com.philips.cl.di.reg.ui.utils;

import android.content.Context;

import com.philips.cl.di.reg.R;

public class JanrainErrorMessage {

	private static final int INTERNET_NOT_AVAILABLE = 15;

	private static final int EMAIL_ALREADY_IN_USE = 14;

	private static final int INVALID_USERNAME_PASSWORD = 10;

	private static final int INVALID_PASSWORD = 9;

	private static final int JANRAIN_SIGN_IN_INVALID_INPUT = 2;

	private static final int JANRAIN_FORGOT_PASSWORD_INVALID_INPUT = 11;

	private String errorMessage;

	private Context mContext;

	public JanrainErrorMessage(Context pContext) {
		setContext(pContext);
	}

	public String getError(int error) {
		if (error == JANRAIN_SIGN_IN_INVALID_INPUT
		        || error == JANRAIN_FORGOT_PASSWORD_INVALID_INPUT) {
			errorMessage = getErrorMessage(R.string.JanRain_Invalid_Input); // "Invalid parameters";
		} else if (error == INVALID_PASSWORD) {
			errorMessage = getErrorMessage(R.string.JanRain_LogIn_Failed); // "Invalid Password";
		} else if (error == INVALID_USERNAME_PASSWORD) {
			errorMessage = getErrorMessage(R.string.JanRain_Invalid_Credentials); // "Invalid username or password";
		} else if (error == EMAIL_ALREADY_IN_USE) {
			errorMessage = "Email address already in use"; // "Email address already in use";
		} else if (error == INTERNET_NOT_AVAILABLE) {
			errorMessage = getErrorMessage(R.string.JanRain_Error_Check_Internet); // "Engage error";
		} else {
			errorMessage = getErrorMessage(R.string.JanRain_LogIn_Failed);
		}
		return errorMessage;
	}

	private String getErrorMessage(int pError) {
		return getContext().getString(pError);
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context pContext) {
		this.mContext = pContext;
	}
}
