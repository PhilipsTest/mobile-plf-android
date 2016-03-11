
package com.philips.cdp.registration.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureApiError;
import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.dao.UserRegistrationFailureInfo;
import com.philips.cdp.registration.events.JumpFlowDownloadStatusListener;
import com.philips.cdp.registration.handlers.SocialProviderLoginHandler;
import com.philips.cdp.registration.handlers.UpdateUserRecordHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.settings.UserRegistrationInitializer;
import com.philips.cdp.registration.ui.utils.RegConstants;

public class ContinueSocialProviderLogin implements Jump.SignInResultHandler,
        Jump.SignInCodeHandler,JumpFlowDownloadStatusListener {

	private SocialProviderLoginHandler mSocialProviderLoginHandler;

	private Context mContext;

	private UpdateUserRecordHandler mUpdateUserRecordHandler;

	public ContinueSocialProviderLogin(SocialProviderLoginHandler socialProviderLoginHandler,
	        Context context, UpdateUserRecordHandler updateUserRecordHandler) {
		mSocialProviderLoginHandler = socialProviderLoginHandler;
		mContext = context;
		mUpdateUserRecordHandler = updateUserRecordHandler;
	}

	public void onSuccess() {
		Jump.saveToDisk(mContext);
		User user = new User(mContext);
		user.buildCoppaConfiguration();
		mUpdateUserRecordHandler.updateUserRecordRegister();
		mSocialProviderLoginHandler.onContinueSocialProviderLoginSuccess();
	}

	public void onCode(String code) {
	}

	public void onFailure(SignInError error) {
		UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
		userRegistrationFailureInfo.setError(error.captureApiError);
		handleInvalidInputs(error.captureApiError, userRegistrationFailureInfo);
		userRegistrationFailureInfo.setErrorCode(error.captureApiError.code);
		mSocialProviderLoginHandler
		        .onContinueSocialProviderLoginFailure(userRegistrationFailureInfo);
	}

	private void handleInvalidInputs(CaptureApiError error,
	        UserRegistrationFailureInfo userRegistrationFailureInfo) {
		if (null != error && null != error.error
		        && error.error.equals(RegConstants.INVALID_FORM_FIELDS)) {
			try {
				JSONObject object = error.raw_response;
				JSONObject jsonObject = (JSONObject) object.get(RegConstants.INVALID_FIELDS);
				if (jsonObject != null) {

					if (!jsonObject.isNull(RegConstants.SOCIAL_REGISTRATION_EMAIL_ADDRESS)) {
						userRegistrationFailureInfo.setEmailErrorMessage(getErrorMessage(jsonObject
						        .getJSONArray(RegConstants.SOCIAL_REGISTRATION_EMAIL_ADDRESS)));
					}
					if (!jsonObject.isNull(RegConstants.SOCIAL_REGISTRATION_DISPLAY_NAME)) {
						userRegistrationFailureInfo
						        .setDisplayNameErrorMessage(getErrorMessage(jsonObject
						                .getJSONArray(RegConstants.SOCIAL_REGISTRATION_DISPLAY_NAME)));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private String getErrorMessage(JSONArray jsonArray)
	        throws JSONException {
		if (null == jsonArray) {
			return null;
		}
		return (String) jsonArray.get(0);
	}

	private JSONObject mUser;
	private String mUserRegistrationToken;

	public void registerNewUser(final JSONObject user, final String userRegistrationToken){
		UserRegistrationInitializer.getInstance().registerJumpFlowDownloadListener(this);
		mUser = user;
		mUserRegistrationToken = userRegistrationToken;
		if (UserRegistrationInitializer.getInstance().isJumpInitializated()) {
			Jump.registerNewUser(user, userRegistrationToken, this);
		}else if(!UserRegistrationInitializer.getInstance().isRegInitializationInProgress()){
			RegistrationHelper.getInstance().initializeUserRegistration(mContext, RegistrationHelper.getInstance().getLocale());
		}

	}

	@Override
	public void onFlowDownloadSuccess() {
		Jump.registerNewUser(mUser, mUserRegistrationToken, this);
		UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();

	}

	@Override
	public void onFlowDownloadFailure() {
		if (mSocialProviderLoginHandler != null) {
			UserRegistrationFailureInfo userRegistrationFailureInfo = new UserRegistrationFailureInfo();
			userRegistrationFailureInfo.setErrorDescription(mContext.getString(R.string.JanRain_Server_Connection_Failed));
			userRegistrationFailureInfo.setErrorCode(RegConstants.REGISTER_SOCIAL_FAILED_SERVER_ERROR);
			mSocialProviderLoginHandler.onLoginFailedWithError(userRegistrationFailureInfo);
		}
		UserRegistrationInitializer.getInstance().unregisterJumpFlowDownloadListener();

	}
}
