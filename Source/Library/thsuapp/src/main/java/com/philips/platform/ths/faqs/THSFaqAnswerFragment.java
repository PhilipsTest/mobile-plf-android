/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.faqs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.ths.utility.THSConstants;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Label;

public class THSFaqAnswerFragment extends THSBaseFragment{
    public static final String TAG = THSFaqAnswerFragment.class.getSimpleName();
    Label mLabelQuestion;
    Label mLabelAnswer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_faqs, container, false);

        mLabelQuestion = (Label) view.findViewById(R.id.ths_question);
        mLabelAnswer = (Label) view.findViewById(R.id.ths_ans);

        Bundle bundle = getArguments();
        if(bundle!=null) {
            FaqBean faqBean = (FaqBean) bundle.getSerializable(THSConstants.THS_FAQ_ITEM);
            String header = bundle.getString(THSConstants.THS_FAQ_HEADER);

            mLabelQuestion.setText(faqBean.getQuestion());
            mLabelAnswer.setText(faqBean.getAnswer());

            ActionBarListener actionBarListener = getActionBarListener();
            if (null != actionBarListener) {
                actionBarListener.updateActionBar(header, true);
            }
        }
        return view;
    }
}
