package com.philips.platform.catalogapp.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.databinding.FragmentCheckboxBinding;

public class CheckBoxFragment extends BaseFragment {

    public ObservableBoolean isCheckBoxEnabled = new ObservableBoolean(Boolean.TRUE);
    private FragmentCheckboxBinding fragmentCheckboxBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCheckboxBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkbox, container, false);
        fragmentCheckboxBinding.setFrag(this);
        return fragmentCheckboxBinding.getRoot();
    }

    @Override
    public int getPageTitle() {
        return R.string.page_title_checkbox;
    }

    public void setEnabled(boolean enabled) {
        isCheckBoxEnabled.set(enabled);
    }
}
