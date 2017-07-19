package com.philips.platform.ths.providerdetails;

import android.os.Bundle;

import com.americanwell.sdk.entity.SDKError;
import com.americanwell.sdk.entity.provider.Provider;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.philips.platform.ths.R;
import com.philips.platform.ths.appointment.THSAvailableProviderCallback;
import com.philips.platform.ths.appointment.THSDatePickerFragment;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.intake.THSSymptomsFragment;
import com.philips.platform.ths.registration.THSConsumer;
import com.philips.platform.ths.sdkerrors.THSSDKError;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.ths.utility.THSManager;

import java.util.Date;
import java.util.List;

public class THSProviderDetailsPresenter implements THSBasePresenter,THSProviderDetailsCallback{

    THSPRoviderDetailsViewInterface viewInterface;

    Provider mProvider;
    THSBaseFragment mThsBaseFragment;

    public THSProviderDetailsPresenter(THSPRoviderDetailsViewInterface viewInterface, THSBaseFragment thsBaseFragment){
        this.viewInterface = viewInterface;
        mThsBaseFragment = thsBaseFragment;
    }

    public void fetchProviderDetails(){
        try {
            if (viewInterface.getTHSProviderInfo() != null)
                getPTHManager().getProviderDetails(viewInterface.getContext(), viewInterface.getTHSProviderInfo(), this);
            else
                viewInterface.dismissRefreshLayout();
        } catch (AWSDKInstantiationException e) {
            e.printStackTrace();
        }

    }

    protected THSManager getPTHManager() {
        return THSManager.getInstance();
    }

    @Override
    public void onProviderDetailsReceived(Provider provider, SDKError sdkError) {
        viewInterface.updateView(provider);
    }

    @Override
    public void onProviderDetailsFetchError(Throwable throwable) {

    }

    @Override
    public void onEvent(int componentID) {
        if (componentID == R.id.detailsButtonOne) {
            THSConsumer THSConsumer = new THSConsumer();
            THSConsumer.setConsumer(viewInterface.getConsumerInfo());

            Bundle bundle = new Bundle();
            bundle.putParcelable(THSConstants.THS_PROVIDER_INFO, viewInterface.getTHSProviderInfo());

            mThsBaseFragment.addFragment(new THSSymptomsFragment(), THSSymptomsFragment.TAG,bundle);
        }else if(componentID == R.id.detailsButtonTwo){
            Bundle bundle = new Bundle();
            bundle.putParcelable(THSConstants.THS_PRACTICE_INFO ,viewInterface.getPracticeInfo());
            bundle.putParcelable(THSConstants.THS_PROVIDER_INFO ,viewInterface.getTHSProviderInfo());
            bundle.putBoolean(THSConstants.THS_IS_DETAILS,false);
            mThsBaseFragment.addFragment(new THSDatePickerFragment(), THSDatePickerFragment.TAG,bundle);
        }else if(componentID == R.id.detailsButtonContinue){

        }else if(componentID == R.id.calendar_container){
            Bundle bundle = new Bundle();
            bundle.putParcelable(THSConstants.THS_PRACTICE_INFO,viewInterface.getPracticeInfo());
            bundle.putParcelable(THSConstants.THS_PROVIDER_INFO ,viewInterface.getTHSProviderInfo());
            bundle.putBoolean(THSConstants.THS_IS_DETAILS,true);
            mThsBaseFragment.addFragment(new THSDatePickerFragment(), THSDatePickerFragment.TAG,bundle);
        }
    }
}
