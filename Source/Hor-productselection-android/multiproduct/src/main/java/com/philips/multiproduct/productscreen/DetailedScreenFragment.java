package com.philips.multiproduct.productscreen;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.philips.cdp.uikit.customviews.CircleIndicator;
import com.philips.multiproduct.R;
import com.philips.multiproduct.customview.CustomFontTextView;
import com.philips.multiproduct.homefragment.MultiProductBaseFragment;
import com.philips.multiproduct.productscreen.adapter.ProductAdapter;
import com.philips.multiproduct.savedscreen.SavedScreenFragment;

/**
 * This Fragments takes responsibility to show the complete detailed description of the
 * specific product with multiple images.
 * <p/>
 * The Data it shows is from the Philips IT System.
 *
 * @author naveen@philips.com
 * @Date 28/01/2016
 */
public class DetailedScreenFragment extends MultiProductBaseFragment implements View.OnClickListener {


    private ViewPager mViewpager;
    private CircleIndicator mIndicater;
    private CustomFontTextView mProductName = null;
    private Button mSelectButton = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_screen, container, false);

        mViewpager = (ViewPager) view.findViewById(R.id.detailedscreen_pager);
        mIndicater = (CircleIndicator) view.findViewById(R.id.detailedscreen_indicator);
        mProductName = (CustomFontTextView) view.findViewById(R.id.detailed_screen_productname);
        mSelectButton = (Button) view.findViewById(R.id.detailedscreen_select_button);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewpager.setAdapter(new ProductAdapter(getChildFragmentManager()));
        mIndicater.setViewPager(mViewpager);
        mProductName.setTypeface(Typeface.DEFAULT_BOLD);
        mSelectButton.setOnClickListener(this);

    }

    @Override
    public void setViewParams(Configuration config) {

    }

    @Override
    public String getActionbarTitle()  {
        return getResources().getString(R.string.product);
    }

    @Override
    public String setPreviousPageName() {
        return null;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.detailedscreen_select_button)
            if (isConnectionAvailable())
                showFragment(new SavedScreenFragment());
    }
}
