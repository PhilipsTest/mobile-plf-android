package com.philips.platform.ths.insurance;

import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.ths.base.THSBaseView;
import com.philips.platform.ths.utility.THSManager;

/**
 * Created by philips on 7/11/17.
 */

public class THSInsuranceDetailPresenter implements THSBasePresenter {
    THSBaseView uiBaseView;

    public  THSInsuranceDetailPresenter(THSBaseView uiBaseView){
        this.uiBaseView = uiBaseView;
    }


    public void fetchHealthPlanList(){
        THSManager.getInstance().getHealthPlans(uiBaseView.getFragmentActivity());

    }

    @Override
    public void onEvent(int componentID) {

    }
}
