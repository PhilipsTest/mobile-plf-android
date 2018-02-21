/*
 *
 *  * Copyright (c) Koninklijke Philips N.V. 2017
 *  * All rights are reserved. Reproduction or dissemination in whole or in part
 *  * is prohibited without the prior written consent of the copyright holder.
 *
 */

package com.philips.cdp.registration.myaccount;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.registration.R;
import com.philips.cdp.registration.User;
import com.philips.cdp.registration.ui.traditional.RegistrationBaseFragment;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Label;

import java.text.SimpleDateFormat;
import java.util.Date;


public class UserDetailsFragment extends RegistrationBaseFragment implements MyaDetailContract.View {

    private Label email_address, mobile_number;
    private Label nameLabel, genderLabel, name_value, dob_value, email_address_heading;
    private final String DETAILS_BUNDLE = "details_bundle";
    private UserDetailPresenter myaDetailPresenter;
    private User user;

    @Override
    protected void setViewParams(Configuration config, int width) {
    // Do not do anything
    }

    @Override
    protected void handleOrientation(View view) {
    // Do not do anything
    }

    @Override
    public int getTitleResourceId() {
        return R.string.reg_MyDetails_TitleTxt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mya_user_detail_fragment, container, false);
        initViews(view);
        setRetainInstance(true);
        myaDetailPresenter = new UserDetailPresenter(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(DETAILS_BUNDLE, getArguments());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = getUser();
        UserDataModelProvider userDataModelProvider = new UserDataModelProvider(user);
        userDataModelProvider.fillUserData();
        myaDetailPresenter.setUserDetails(userDataModelProvider);
    }

    @NonNull
    private User getUser() {
        return new User(getActivity());
    }

    @VisibleForTesting
    public void setUser(User user){
        this.user = user;
    }

    private void initViews(View view) {
        nameLabel = view.findViewById(R.id.mya_name);
        email_address = view.findViewById(R.id.email_address_value);
        mobile_number = view.findViewById(R.id.mobile_number_value);
        genderLabel = view.findViewById(R.id.gender_value);
        email_address_heading = view.findViewById(R.id.email_address_heading);
        name_value = view.findViewById(R.id.name_value);
        dob_value = view.findViewById(R.id.dob_value);
    }
    @Override
    public void setUserName(String name) {
        if (!TextUtils.isEmpty(name)) {
            name_value.setText(name);
        }
    }

    @Override
    public void setCircleText(String data) {
        nameLabel.setText(data);
    }

    @Override
    public void setEmail(String email) {
        if (TextUtils.isEmpty(email) || email.equalsIgnoreCase("null")) {

        } else {
            email_address.setText(email);
        }
    }

    @Override
    public void setGender(String gender) {
        if (!TextUtils.isEmpty(gender) && !gender.equalsIgnoreCase("null")) {
            genderLabel.setText(gender);
        } else {
            genderLabel.setText(getString(R.string.MYA_Not_Available));
        }
    }

    @Override
    public void setDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            String tempDate = formatter.format(dateOfBirth);
            dob_value.setText(tempDate);
        } else
            dob_value.setText(getString(R.string.MYA_Not_Available));
    }

    @Override
    public void setMobileNumber(String number) {
        if (TextUtils.isEmpty(number) || number.equalsIgnoreCase("null")) {

        } else {
            mobile_number.setText(number);
        }
    }

    @Override
    public void handleArrowVisibility(String email, String mobileNumber) {

    }

    @Override
    public boolean exitMyAccounts() {
        return false;
    }
}
