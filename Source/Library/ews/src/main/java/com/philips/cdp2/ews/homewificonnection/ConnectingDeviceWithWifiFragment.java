package com.philips.cdp2.ews.homewificonnection;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.view.BaseFragment;
import com.philips.cdp2.ews.view.EWSActivity;

/**
 * Created by salvatorelafiura on 10/10/2017.
 */

public class ConnectingDeviceWithWifiFragment extends BaseFragment implements ConnectingDeviceWithWifiViewModel.ConnectingDeviceToWifiCallback {
    private final static String HOME_WIFI_SSID = "homeWiFiSSID";
    private final static String HOME_WIFI_PWD = "homeWiFiPassword";
    @Nullable
    ConnectingDeviceWithWifiViewModel viewModel;

    public static Fragment newInstance(String homeWiFiSSID, String homeWiFiPassword) {
        Bundle data = new Bundle();
        data.putString(HOME_WIFI_SSID, homeWiFiSSID);
        data.putString(HOME_WIFI_PWD, homeWiFiPassword);
        ConnectingDeviceWithWifiFragment fragment = new ConnectingDeviceWithWifiFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EWSActivity activity = (EWSActivity) getActivity();
        activity.hideCloseButton();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = createViewModel();
        viewModel.setFragmentCallback(this);
        viewModel.startConnecting(getArguments().getString(HOME_WIFI_SSID), getArguments().getString(HOME_WIFI_PWD));
        return inflater.inflate(R.layout.fragment_connecting_phone_to_hotspot_layout, container,
                false);
    }

    private ConnectingDeviceWithWifiViewModel createViewModel() {
        return ((EWSActivity) getActivity()).getEWSComponent().connectingDeviceWithWifiViewModel();
    }

    @Override
    public void registerReceiver(@NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter) {
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(@NonNull BroadcastReceiver receiver) {
        getActivity().unregisterReceiver(receiver);
    }
}
