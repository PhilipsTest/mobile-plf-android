/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.platform.csw.permission;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.plataform.mya.model.error.ConsentError;
import com.philips.plataform.mya.model.listener.ConsentResponseListener;
import com.philips.plataform.mya.model.network.NetworkHelper;
import com.philips.plataform.mya.model.response.ConsentModel;
import com.philips.plataform.mya.model.response.ConsentStatus;
import com.philips.platform.csw.CswBaseFragment;
import com.philips.platform.mya.consentwidgets.R;
import com.philips.platform.uid.view.widget.Switch;

import java.util.List;

import static com.janrain.android.engage.JREngage.getApplicationContext;

public class PermissionView extends CswBaseFragment implements
        PermissionInterface {

    private PermissionPresenter permissionPresenter;
    private Switch mConsentSwitch;
    private ProgressDialog mProgressDialog;

    @Override
    protected void setViewParams(Configuration config, int width) {

    }

    @Override
    protected void handleOrientation(View view) {

    }

    @Override
    public int getTitleResourceId() {
        return R.string.csw_permissions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csw_permission_view, container, false);
        initUI(view);
        getConsentStatus();
        return view;
    }

    private void getConsentStatus() {
        showProgressDialog();
        NetworkHelper consentObj = new NetworkHelper();
        consentObj.getLatestConsentStatus(getApplicationContext(), new ConsentResponseListener() {
            @Override
            public void onResponseSuccessConsent(List<ConsentModel> responseData) {
                hideProgressDialog();
                if (responseData.get(0).getStatus().equals(ConsentStatus.active)) {
                    mConsentSwitch.setChecked(true);
                } else {
                    mConsentSwitch.setChecked(false);
                }
                Log.d(" Consent : ", "getDateTime :" + responseData.get(0).getDateTime());
                Log.d(" Consent : ", "getLanguage :" + responseData.get(0).getLanguage());
                Log.d(" Consent : ", "status :" + responseData.get(0).getStatus());
                Log.d(" Consent : ", "policyRule :" + responseData.get(0).getPolicyRule());
                Log.d(" Consent : ", "Resource type :" + responseData.get(0).getResourceType());
                Log.d(" Consent : ", "subject  :" + responseData.get(0).getSubject());
            }

            @Override
            public void onResponseFailureConsent(ConsentError consentError) {
                hideProgressDialog();
                Log.d(" Consent : ", "fail  :" + consentError.getDescription());
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        permissionPresenter = new PermissionPresenter(this, getContext());
    }

    private void initUI(View view) {
        mConsentSwitch = (Switch) view.findViewById(R.id.toggleicon);
    }

    private void showProgressDialog() {
        if (!(getActivity().isFinishing())) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity(), R.style.reg_Custom_loaderTheme);
                mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                mProgressDialog.setCancelable(false);
            }
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }
}
