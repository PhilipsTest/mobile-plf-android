/**
 * RateThisAppFragment class is used to rate the app and review the product.
 *
 * @author: naveen@philips.com
 * @since: Jan 11, 2015
 * <p>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.fragments.rateandreview;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.fragments.rateandreview.fragments.ProductReviewFragment;
import com.philips.cdp.digitalcare.fragments.rateandreview.fragments.RateThisAppFragmentContract;
import com.philips.cdp.digitalcare.fragments.rateandreview.fragments.RateThisAppFragmentPresenter;
import com.philips.cdp.digitalcare.fragments.rateandreview.fragments.WriteReviewFragment;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.productdetails.model.ViewProductDetailsModel;

import java.util.HashMap;
import java.util.Map;

import static com.philips.cdp.digitalcare.analytics.AnalyticsConstants.ACTION_KEY_SPECIAL_EVENTS;
import static com.philips.cdp.digitalcare.analytics.AnalyticsConstants.ACTION_VALUE_RATE_THIS_APP;
import static com.philips.cdp.digitalcare.analytics.AnalyticsConstants.ACTION_VALUE_WRITE_PRODUCT_REVIEW;

@SuppressWarnings("serial")
public class RateThisAppFragment extends DigitalCareBaseFragment implements RateThisAppFragmentContract {
    private static String TAG = RateThisAppFragment.class.getSimpleName();
    private final String APPRATER_PLAYSTORE_BROWSER_BASEURL = "https://play.google.com/store/apps/details?id=";
    private final String APPRATER_PLAYSTORE_APP_BASEURL = "market://details?id=";
    private Button mRatePlayStoreBtn = null;
    private Button mRatePhilipsBtn = null;
    private Uri mStoreUri = null;
    private RateThisAppFragmentPresenter rateThisAppFragmentPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rateThisAppFragmentPresenter = new RateThisAppFragmentPresenter(this);
        View mView = inflater.inflate(R.layout.consumercare_fragment_tellus, container,false);
        intiView(mView);
        mStoreUri = Uri.parse(APPRATER_PLAYSTORE_BROWSER_BASEURL+ getContext().getPackageName());
        return mView;
    }

    private void intiView(View view) {
        mRatePlayStoreBtn = (Button) view.findViewById(
                R.id.tellus_PlayStoreReviewButton);
        mRatePhilipsBtn = (Button) view.findViewById(
                R.id.tellus_PhilipsReviewButton);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRatePlayStoreBtn.setOnClickListener(this);
        mRatePhilipsBtn.setTransformationMethod(null);
        mRatePlayStoreBtn.setTransformationMethod(null);
        mRatePhilipsBtn.setOnClickListener(this);
        rateThisAppFragmentPresenter.handleProductData();
        rateThisAppFragmentPresenter.validateContryChina();
        DigitalCareConfigManager.getInstance().getTaggingInterface().
                trackPageWithInfo(AnalyticsConstants.PAGE_RATE_THIS_APP,
                        getPreviousName(), getPreviousName());
    }

    @Override
    public void hidePlayStoreBtn() {
        mRatePlayStoreBtn.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        setViewParams(config);
    }

    private void rateThisApp() {
        mStoreUri = Uri.parse(APPRATER_PLAYSTORE_BROWSER_BASEURL
                +getContext().getPackageName());

        Uri uri = Uri.parse(APPRATER_PLAYSTORE_APP_BASEURL+getContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, mStoreUri));
        }
    }

    private void rateProductReview() {
        if (isConnectionAvailable())
            showFragment(new WriteReviewFragment());
            //showFragment(new ProductReviewFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(isConnectionAvailable()) {
            if (id == R.id.tellus_PhilipsReviewButton) {
                Map<String, String> contextData = new HashMap<>();
                contextData.put(ACTION_KEY_SPECIAL_EVENTS,ACTION_VALUE_WRITE_PRODUCT_REVIEW);
                DigitalCareConfigManager.getInstance().getTaggingInterface().
                        trackActionWithInfo(AnalyticsConstants.ACTION_SEND_DATA,
                                contextData);
                rateProductReview();
            } else if (id == R.id.tellus_PlayStoreReviewButton) {
                Map<String, String> contextData = new HashMap<>();
                contextData.put(ACTION_KEY_SPECIAL_EVENTS,ACTION_VALUE_RATE_THIS_APP);
                DigitalCareConfigManager.getInstance().getTaggingInterface().
                        trackActionWithInfo(AnalyticsConstants.ACTION_SEND_DATA,
                                contextData);
                rateThisApp();
            }
        }
    }

    @Override
    public void setViewParams(Configuration config) {

    }

    @Override
    public String getActionbarTitle() {
        String title = getResources().getString(R.string.dcc_tellUs_header);
        return title;
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_RATE_THIS_APP;
    }

    @Override
    public void onPRXProductPageReceived(ViewProductDetailsModel data) {
        mRatePhilipsBtn.setVisibility(data.getProductInfoLink() ==null? View.GONE: View.VISIBLE);
    }
}
