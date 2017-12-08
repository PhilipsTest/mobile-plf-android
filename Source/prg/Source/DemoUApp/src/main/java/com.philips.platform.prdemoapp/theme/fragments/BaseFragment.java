/*
 * (C) Koninklijke Philips N.V., 2016.
 * All rights reserved.
 *
 */
package com.philips.platform.prdemoapp.theme.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.philips.platform.prdemoapp.theme.NavigationController;


public abstract class BaseFragment extends Fragment implements NavigationController.BackPressListener {

    public abstract int getPageTitle();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        (getActivity()).setTitle(getPageTitle());
    }


    @Override
    public boolean handleBackPress() {
        return false;
    }
}