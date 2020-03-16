package com.philips.cdp.registration.ui.traditional;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.events.NetworkStateListener;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.pif.DataInterface.USR.enums.Error;
import com.philips.platform.pif.DataInterface.USR.listeners.UpdateUserDetailsHandler;

/**
 * Created by philips on 28/07/17.
 */

public class MarketingAccountPresenter implements NetworkStateListener, UpdateUserDetailsHandler {

    private String TAG = "MarketingAccountPresenter";

    MarketingAccountContract marketingAccountContract;

    public MarketingAccountPresenter(MarketingAccountContract marketingAccountContract){
        this.marketingAccountContract = marketingAccountContract;
    }
    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.d(TAG, " onNetWorkStateReceived : " + isOnline);
        marketingAccountContract.handleUiState();
    }

    @Override
    public void onUpdateSuccess() {
        marketingAccountContract.trackRemarketing();
        RLog.d(TAG, "onUpdateSuccess ");
        marketingAccountContract.hideRefreshProgress();
        marketingAccountContract.handleRegistrationSuccess();
    }

    @Override
    public void onUpdateFailedWithError(final Error error) {
        RLog.d(TAG, "onUpdateFailedWithError ");
        marketingAccountContract.hideRefreshProgress();
    }

    public void register(){
        RegistrationHelper.getInstance().registerNetworkStateListener(this);
    }
    public void unRegister(){
        RegistrationHelper.getInstance().unRegisterNetworkListener(this);
    }

    public void updateMarketingEmail(User user, boolean isUpdate){
        user.updateReceiveMarketingEmail(this, isUpdate);
    }
}
