/*
 * Copyright (c) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */

package com.philips.cdp2.ews.connectionsuccessful;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.base.BaseFragment;
import com.philips.cdp2.ews.common.callbacks.FragmentCallback;
import com.philips.cdp2.ews.databinding.FragmentConnectionSuccessfulBinding;
import com.philips.cdp2.ews.injections.AppModule;
import com.philips.cdp2.ews.injections.DaggerEWSComponent;
import com.philips.cdp2.ews.injections.EWSConfigurationModule;
import com.philips.cdp2.ews.injections.EWSModule;
import com.philips.cdp2.ews.microapp.EWSActionBarListener;
import com.philips.cdp2.ews.microapp.EWSLauncherInput;

import javax.inject.Inject;

public class ConnectionSuccessfulFragment extends BaseFragment implements
        FragmentCallback {

    @Inject
    ConnectionSuccessfulViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((EWSActionBarListener) getContext()).closeButton(false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentConnectionSuccessfulBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connection_successful, container, false);
        viewModel = createViewModel();
        binding.setViewModel(viewModel);
        viewModel.setFragmentCallback(this);
        return binding.getRoot();
    }

    @NonNull
    private ConnectionSuccessfulViewModel createViewModel() {
        return DaggerEWSComponent.builder()
                .eWSModule(new EWSModule(this.getActivity()
                        , EWSLauncherInput.getFragmentManager()
                        , EWSLauncherInput.getContainerFrameId(), AppModule.getCommCentral()))
                .eWSConfigurationModule(new EWSConfigurationModule(this.getActivity(), AppModule.getContentConfiguration()))
                .build().connectionSuccessfulViewModel();
    }

    @Override
    public boolean handleBackEvent() {
        // Do nothing, back disabled in this screen
        return true;
    }

    @Override
    protected void callTrackPageName() {
        viewModel.trackPageName();
    }

    @Override
    public void finishMicroApp() {
        getActivity().finish();
    }
}