package com.philips.platform.catalogapp.fragments;

import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.databinding.FragmentRatingbarBinding;
import com.philips.platform.uid.drawable.FontIconDrawable;
import com.philips.platform.uid.view.widget.RatingBar;

import uk.co.chrisjenx.calligraphy.TypefaceUtils;


public class RatingBarFragment extends BaseFragment {

    private FragmentRatingbarBinding fragmentRatingbarBinding;
    public RatingBar inputRatingBar;
    public ObservableBoolean isRatingZero = new ObservableBoolean(Boolean.TRUE);

    @Override
    public int getPageTitle() {
        return R.string.page_title_ratingbar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRatingbarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ratingbar, container, false);
        fragmentRatingbarBinding.setFrag(this);
        inputRatingBar = fragmentRatingbarBinding.ratingInput;
        inputRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(android.widget.RatingBar ratingBar, float rating, boolean fromUser) {
                isRatingZero.set(rating > 0 ? false : true);
            }
        });
        return fragmentRatingbarBinding.getRoot();
    }

}
