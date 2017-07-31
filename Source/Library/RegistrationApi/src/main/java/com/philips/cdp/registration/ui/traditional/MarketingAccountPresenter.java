package com.philips.cdp.registration.ui.traditional;

import com.philips.cdp.registration.User;
import com.philips.cdp.registration.events.NetworStateListener;
import com.philips.cdp.registration.handlers.UpdateUserDetailsHandler;
import com.philips.cdp.registration.settings.RegistrationHelper;
import com.philips.cdp.registration.ui.utils.RLog;

/**
 * Created by philips on 28/07/17.
 */

public class MarketingAccountPresenter implements NetworStateListener, UpdateUserDetailsHandler {

    MarketingAccountContract marketingAccountContract;

    public MarketingAccountPresenter(MarketingAccountContract marketingAccountContract){
        this.marketingAccountContract = marketingAccountContract;
    }
    @Override
    public void onNetWorkStateReceived(boolean isOnline) {
        RLog.i(RLog.NETWORK_STATE, "CreateAccoutFragment :onNetWorkStateReceived : " + isOnline);
        marketingAccountContract.handleUiState();
    }

    @Override
    public void onUpdateSuccess() {
        marketingAccountContract.trackRemarketing();
        RLog.i("MarketingAccountFragment", "onUpdateSuccess ");
        marketingAccountContract.hideRefreshProgress();
        marketingAccountContract.handleRegistrationSuccess();
    }

    @Override
    public void onUpdateFailedWithError(final int error) {
        RLog.i("MarketingAccountFragment", "onUpdateFailedWithError ");
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
