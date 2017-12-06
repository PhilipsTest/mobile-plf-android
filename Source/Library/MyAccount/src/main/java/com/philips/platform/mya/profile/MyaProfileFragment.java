/*
 * Copyright (c) Koninklijke Philips N.V. 2017
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.mya.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.philips.platform.mya.MyaHelper;
import com.philips.platform.mya.R;
import com.philips.platform.mya.base.mvp.MyaBaseFragment;
import com.philips.platform.myaplugin.user.UserDataModelProvider;
import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.Map;

import static com.philips.platform.mya.launcher.MyaInterface.USER_PLUGIN;


public class MyaProfileFragment extends MyaBaseFragment implements MyaProfileContract.View {

    private RecyclerView recyclerView;
    private MyaProfileContract.Presenter presenter;
    private TextView userNameTextView;
    private String PROFILE_BUNDLE = "profile_bundle";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mya_profile_fragment, container, false);
        UIDHelper.injectCalligraphyFonts();
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        userNameTextView = view.findViewById(R.id.mya_user_name);
        presenter = new MyaProfilePresenter(this);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(PROFILE_BUNDLE, getArguments());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewActive(this);
    }

    @Override
    public void onPause() {
        presenter.onViewInactive();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        Bundle arguments;
        if (savedInstanceState == null) {
            arguments = getArguments();
        } else {
            arguments = savedInstanceState.getBundle(PROFILE_BUNDLE);
        }
        if(arguments!=null)
            presenter.setUserName((UserDataModelProvider) arguments.getSerializable(USER_PLUGIN));
        presenter.getProfileItems(MyaHelper.getInstance().getAppInfra());
    }


    @Override
    public int getActionbarTitleResId() {
        return R.string.MYA_My_account;
    }

    @Override
    public String getActionbarTitle(Context context) {
        return context.getString(R.string.MYA_My_account);
    }

    @Override
    public boolean getBackButtonState() {
        return false;
    }

    @Override
    public void showProfileItems(final Map<String, String> profileList) {
        MyaProfileAdaptor myaProfileAdaptor = new MyaProfileAdaptor(profileList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        RecyclerViewSeparatorItemDecoration contentThemedRightSeparatorItemDecoration = new RecyclerViewSeparatorItemDecoration(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(contentThemedRightSeparatorItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myaProfileAdaptor);
        myaProfileAdaptor.setOnClickListener(getOnClickListener(profileList));
    }

    private View.OnClickListener getOnClickListener(final Map<String, String> profileList) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewType = recyclerView.indexOfChild(view);
                String key = (String) profileList.keySet().toArray()[viewType];
                String value = profileList.get(key);
                String profileItem = value != null ? value : key;
                boolean handled = presenter.handleOnClickProfileItem(profileItem, getArguments());
                if (!handled) {
                    boolean onClickMyaItem = MyaHelper.getInstance().getMyaListener().onClickMyaItem(key);
                    handleTransition(onClickMyaItem, profileItem);
                }
            }
        };
    }


    @Override
    public void setUserName(String userName) {
        userNameTextView.setText(userName);
    }

    @Override
    public void showPassedFragment(Fragment fragment) {
        showFragment(fragment);
    }

    private void handleTransition(boolean onClickMyaItem, String profileItem) {
        // code to be added in future
    }

}
