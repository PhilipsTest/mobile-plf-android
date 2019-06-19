package com.philips.platform.pim.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.philips.platform.pim.utilities.PIMInitState;

public class PIMInitViewModel extends ViewModel {

    private MutableLiveData<PIMInitState> pimInitState;

    public MutableLiveData<PIMInitState> getMuatbleInitLiveData(){
        if(pimInitState == null){
            pimInitState = new MutableLiveData<PIMInitState>();
        }
        return pimInitState;
    }


}
