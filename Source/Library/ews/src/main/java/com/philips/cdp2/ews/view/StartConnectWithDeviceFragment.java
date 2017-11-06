/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */

package com.philips.cdp2.ews.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.databinding.FragmentStartConnectWithDeviceBinding;
import com.philips.cdp2.ews.tagging.Page;
import com.philips.cdp2.ews.viewmodel.StartConnectWithDeviceViewModel;

public class StartConnectWithDeviceFragment extends BaseFragment {

    private StartConnectWithDeviceViewModel viewModel;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentStartConnectWithDeviceBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start_connect_with_device, container, false);
        viewModel = createViewModel();
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @NonNull
    private StartConnectWithDeviceViewModel createViewModel() {
        return ((EWSActivity) getActivity()).getEWSComponent().ewsGettingStartedViewModel();
    }

    @Override
    protected String getPageName() {
        return Page.GET_STARTED;
    }
}
