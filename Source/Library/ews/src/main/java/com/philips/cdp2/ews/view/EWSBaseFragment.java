/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.cdp2.ews.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp2.ews.R;
import com.philips.cdp2.ews.injections.EWSComponent;
import com.philips.cdp2.ews.navigation.ScreenFlowController;
import com.philips.cdp2.ews.navigation.ScreenFlowParticipant;
import com.philips.cdp2.ews.tagging.EWSTagger;

public abstract class EWSBaseFragment<T extends ViewDataBinding> extends BaseFragment implements ScreenFlowParticipant {

    protected T viewDataBinding;

    @CallSuper
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        injectDependencies();
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        bindViewModel(viewDataBinding);

        setHasOptionsMenu(hasMenu());

        return viewDataBinding.getRoot();
    }

    protected boolean hasMenu() {
        return true;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        String pageName = getPageName();
        if (pageName != null) {
            EWSTagger.trackPage(pageName);
        }
    }

    @Nullable
    protected abstract String getPageName();

    private void injectDependencies() {
        inject(getEwsComponent());
    }

    @NonNull
    protected EWSComponent getEwsComponent() {
        return ((EWSActivity) getActivity()).getEWSComponent();
    }

    protected abstract void bindViewModel(final T viewDataBinding);

    protected abstract void inject(final EWSComponent ewsComponent);

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getNavigationIconId() {
        return ScreenFlowController.NAVIGATION_BACK_ICON;
    }

    @StringRes
    @Override
    public int getToolbarTitle() {
        return R.string.ews_title;
    }
}