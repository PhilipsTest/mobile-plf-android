package com.philips.platform.catalogapp.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.databinding.FragmentRatingbarBinding;


public class RatingBarFragment extends BaseFragment {

    private FragmentRatingbarBinding fragmentRatingbarBinding;
    public ObservableBoolean isRatingZero = new ObservableBoolean(Boolean.TRUE);

    @Override
    public int getPageTitle() {
        return R.string.page_title_ratingbar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRatingbarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ratingbar, container, false);
        fragmentRatingbarBinding.setFrag(this);
        fragmentRatingbarBinding.ratingInput.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                isRatingZero.set(rating>0?false:true);
            }
        });
        return fragmentRatingbarBinding.getRoot();
    }




}
