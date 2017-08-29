/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.baseapp.screens.cocoversion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.cdp.uikit.BuildConfig;
import com.philips.platform.appframework.R;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseActivity;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseFragment;

import java.util.ArrayList;


/**
 * Created by philips on 4/18/17.
 */

public class CocoVersionFragment extends AbstractAppFrameworkBaseFragment {
    public static final String TAG = CocoVersionFragment.class.getSimpleName();
    private RecyclerView recyclerViewCoco;
    private ArrayList<CocoVersionItem> cocoVersionItemList = new ArrayList<CocoVersionItem>();

    private CocoVersionAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        updateActionBar();
    }

    protected void updateActionBar() {
        ((AbstractAppFrameworkBaseActivity) getActivity()).updateActionBarIcon(false);
    }


    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.RA_Coco_Version);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.af_coco_version_information, container, false);
        recyclerViewCoco = (RecyclerView) view.findViewById(R.id.coco_version_view);
        recyclerViewCoco.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        startAppTagging(TAG);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getCOCOVersion();
        adapter = new CocoVersionAdapter(getActivity(), cocoVersionItemList);
        recyclerViewCoco.setAdapter(adapter);
    }

    public void getCOCOVersion() {
        CocoVersionItem ai = new CocoVersionItem();
        ai.title = getResources().getString(R.string.RA_COCO_AppInfra);
        ai.version = (com.philips.platform.appinfra.BuildConfig.VERSION_NAME);

        CocoVersionItem UserReg = new CocoVersionItem();
        UserReg.title = getResources().getString(R.string.RA_COCO_UR);
        UserReg.version = com.philips.cdp.registration.BuildConfig.VERSION_NAME;

        CocoVersionItem uikit = new CocoVersionItem();

        uikit.title = getResources().getString(R.string.RA_COCO_UIKIT);
        uikit.version = (BuildConfig.VERSION_NAME);

        CocoVersionItem connectivity = new CocoVersionItem();
        connectivity.title = getResources().getString(R.string.RA_COCO_Connectivity);
        connectivity.version = com.philips.cdp.dicommclient.BuildConfig.VERSION_NAME;

        CocoVersionItem iap = new CocoVersionItem();
        iap.title = getResources().getString(R.string.RA_COCO_IAP);
        iap.version = com.philips.cdp.di.iap.BuildConfig.VERSION_NAME;

        CocoVersionItem digitalCare = new CocoVersionItem();
        digitalCare.title = getResources().getString(R.string.RA_COCO_CC);
        digitalCare.version = com.philips.cdp.digitalcare.BuildConfig.VERSION_NAME;

        CocoVersionItem prodReg = new CocoVersionItem();
        prodReg.title = getResources().getString(R.string.RA_COCO_PR);
        prodReg.version = com.philips.cdp.product_registration_lib.BuildConfig.VERSION_NAME;

        CocoVersionItem dataService = new CocoVersionItem();
        dataService.title = getResources().getString(R.string.RA_COCO_DS);
        dataService.version = com.philips.platform.dataservices.BuildConfig.VERSION_NAME;


        cocoVersionItemList.add(ai);
        cocoVersionItemList.add(UserReg);
        cocoVersionItemList.add(uikit);
        cocoVersionItemList.add(connectivity);
        cocoVersionItemList.add(iap);
        cocoVersionItemList.add(digitalCare);
        cocoVersionItemList.add(prodReg);
        cocoVersionItemList.add(dataService);

    }
}
