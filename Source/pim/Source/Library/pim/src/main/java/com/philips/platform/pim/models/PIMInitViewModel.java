package com.philips.platform.pim.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.philips.platform.pim.utilities.PIMInitState;

public class PIMInitViewModel extends AndroidViewModel {

    private MutableLiveData<PIMInitState> pimInitState;

    public PIMInitViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PIMInitState> getMuatbleInitLiveData(){
        if(pimInitState == null){
            pimInitState = new MutableLiveData<PIMInitState>();
        }
        return pimInitState;
    }
}
