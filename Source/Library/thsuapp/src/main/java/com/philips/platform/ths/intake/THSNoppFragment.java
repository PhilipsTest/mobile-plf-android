/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.intake;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.base.THSBasePresenter;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Label;

public class THSNoppFragment extends THSBaseFragment {
    public static final String TAG = THSNoppFragment.class.getSimpleName();
    private ActionBarListener actionBarListener;
    Label legalTextsLabel;
    THSBasePresenter mTHSNoppPresenter;
    private RelativeLayout mRelativeLayoutNopContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_nopp_fragment, container, false);
        legalTextsLabel = (Label) view.findViewById(R.id.ths_intake_nopp_agreement_text);
        mRelativeLayoutNopContainer = (RelativeLayout) view.findViewById(R.id.nop_container);
        mTHSNoppPresenter = new THSNoppPresenter(this);

       

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionBarListener = getActionBarListener();
        createCustomProgressBar(mRelativeLayoutNopContainer,BIG);
        ((THSNoppPresenter) mTHSNoppPresenter).showLegalTextForNOPP();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != actionBarListener) {
            actionBarListener.updateActionBar("NOPP", true);
        }
    }
}
