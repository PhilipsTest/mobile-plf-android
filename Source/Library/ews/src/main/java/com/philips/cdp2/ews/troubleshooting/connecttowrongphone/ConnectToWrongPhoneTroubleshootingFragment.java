package com.philips.cdp2.ews.troubleshooting.connecttowrongphone;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.base.BaseTroubleShootingFragment;
import com.philips.cdp2.ews.databinding.FragmentConnectToWrongPhoneTroubleshootingLayoutBinding;
import com.philips.cdp2.ews.injections.AppModule;
import com.philips.cdp2.ews.injections.DaggerEWSComponent;
import com.philips.cdp2.ews.injections.EWSConfigurationModule;
import com.philips.cdp2.ews.injections.EWSModule;
import com.philips.cdp2.ews.microapp.EWSLauncherInput;

public class ConnectToWrongPhoneTroubleshootingFragment extends BaseTroubleShootingFragment {

    @NonNull
    FragmentConnectToWrongPhoneTroubleshootingLayoutBinding connectToWrongPhoneTroubleshootingLayoutBinding;

    @NonNull
    ConnectToWrongPhoneTroubleshootingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        connectToWrongPhoneTroubleshootingLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect_to_wrong_phone_troubleshooting_layout, container, false);
        return connectToWrongPhoneTroubleshootingLayoutBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = DaggerEWSComponent.builder()
                .eWSModule(new EWSModule(this.getActivity()
                        , EWSLauncherInput.getFragmentManager()
                        , EWSLauncherInput.getContainerFrameId(), AppModule.getCommCentral()))
                .eWSConfigurationModule(new EWSConfigurationModule(this.getActivity(), AppModule.getContentConfiguration()))
                .build()
                .connectToWrongPhoneTroubleshootingViewModel();

        connectToWrongPhoneTroubleshootingLayoutBinding.setViewmodel(viewModel);

        view.findViewById(R.id.ews_H_03_01_button_yes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.onYesButtonClicked();
                    }
                });
        view.findViewById(R.id.ews_H_03_01_button_no)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.onNoButtonClicked();
                    }
                });
    }

    @NonNull
    @Override
    protected void callTrackPageName() {
        viewModel.trackPageName();
    }
}
