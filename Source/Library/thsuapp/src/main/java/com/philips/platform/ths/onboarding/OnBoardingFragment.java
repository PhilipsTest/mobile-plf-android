package com.philips.platform.ths.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;

/**
 * Created by philips on 10/25/17.
 */

public class OnBoardingFragment  extends THSBaseFragment implements View.OnClickListener{

    public static final String TAG = OnBoardingFragment.class.getSimpleName();
    private OnBoardingPresenter onBoardingPresenter;
    private TextView tv_skip;
    private Button btn_take_tour;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBoardingPresenter = new OnBoardingPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.ths_onboarding_layout, container, false);
        tv_skip=(TextView)view.findViewById(R.id.tv_skip);
        btn_take_tour=(Button) view.findViewById(R.id.btn_take_tour);
        tv_skip.setOnClickListener(this);
        btn_take_tour.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        onBoardingPresenter.onEvent(v.getId());
    }
}
