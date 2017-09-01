package com.philips.cdp.registration.ui.social;


import com.philips.cdp.registration.*;
import com.philips.cdp.registration.dao.*;
import com.philips.cdp.registration.events.*;
import com.philips.cdp.registration.handlers.*;
import com.philips.cdp.registration.settings.*;
import com.philips.cdp.registration.ui.utils.*;

import org.json.*;

import javax.inject.*;

public class MergeSocialToSocialAccountPresenter implements NetworkStateListener, SocialProviderLoginHandler {

    private MergeSocialToSocialAccountContract mergeSocialToSocialAccountContract;

    @Inject
    User mUser;

    public MergeSocialToSocialAccountPresenter(MergeSocialToSocialAccountContract mergeSocialToSocialAccountContract) {
        URInterface.getComponent().inject(this);
        this.mergeSocialToSocialAccountContract = mergeSocialToSocialAccountContract;
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
    }

    public void cleanUp() {
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
    }


    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        mergeSocialToSocialAccountContract.connectionStatus(isOnline);
        mergeSocialToSocialAccountContract.mergeStatus(isOnline);
    }


    @Override
    public void onLoginSuccess() {
        mergeSocialToSocialAccountContract.mergeSuccess();
    }

    @Override
    public void onLoginFailedWithError(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        mergeSocialToSocialAccountContract.mergeFailure(userRegistrationFailureInfo.getErrorDescription());
    }

    @Override
    public void onLoginFailedWithTwoStepError(JSONObject prefilledRecord, String socialRegistrationToken) {
        mergeSocialToSocialAccountContract.mergeFailureIgnored();
    }

    @Override
    public void onLoginFailedWithMergeFlowError(String mergeToken, String existingProvider, String conflictingIdentityProvider, String conflictingIdpNameLocalized, String existingIdpNameLocalized, String emailId) {
        mergeSocialToSocialAccountContract.mergeFailureIgnored();
    }

    @Override
    public void onContinueSocialProviderLoginSuccess() {
        mergeSocialToSocialAccountContract.mergeSuccess();
    }

    @Override
    public void onContinueSocialProviderLoginFailure(UserRegistrationFailureInfo userRegistrationFailureInfo) {
        mergeSocialToSocialAccountContract.mergeFailureIgnored();
    }

    public void logout() {
        mUser.logout(null);
    }

    public void loginUserUsingSocialProvider(String mConflictProvider, String mMergeToken) {
        mUser.loginUserUsingSocialProvider(mergeSocialToSocialAccountContract.getActivityContext(), mConflictProvider, this, mMergeToken);
    }

    public String getLoginWithDetails() {
        if (FieldsValidator.isValidEmail(mUser.getEmail())) {
            return mUser.getEmail();
        }
        return mUser.getMobile();
    }

    public boolean getReceiveMarketingEmail() {
        return mUser.getReceiveMarketingEmail();
    }
}
