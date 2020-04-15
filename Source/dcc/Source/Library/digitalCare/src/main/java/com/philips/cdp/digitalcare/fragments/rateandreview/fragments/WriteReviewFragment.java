package com.philips.cdp.digitalcare.fragments.rateandreview.fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WriteReviewFragment extends DigitalCareBaseFragment {
    private ImageView mImage;
    private TextView mProductName;
    private RatingBar mRating;
    private RadioGroup rgRecommendProduct;

    public WriteReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void setViewParams(Configuration config) {

    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.dcc_review);
    }

    @Override
    public String setPreviousPageName() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.consumercare_fragment_write_review, container,false);
        mImage = mView.findViewById(R.id.dcc_product_image);
        mProductName = mView.findViewById(R.id.iap_product_label);
        mRating = mView.findViewById(R.id.dcc_rating_bar);
        rgRecommendProduct = mView.findViewById(R.id.dcc_rg);
        setData();
        return mView;
    }

    private void setData() {
        mProductName.setText(DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductName());
        if (DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductImage() != null) {
            ImageRequest request = new ImageRequest(DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductImage(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                             {
                                if (DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductImage() != null) {
                                    mImage.setVisibility(View.VISIBLE);
                                    mImage.setImageBitmap(bitmap);
                                }
                            }
                        }
                    }, 0, 0, null, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Map<String, String> contextData = new HashMap<>();
                            contextData.put(AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR,
                                    error.getMessage());
                            contextData.put(AnalyticsConstants.ACTION_KEY_URL,
                                    DigitalCareConfigManager.getInstance().getViewProductDetailsData().getProductImage());
                            DigitalCareConfigManager.getInstance().getTaggingInterface().
                                    trackActionWithInfo(AnalyticsConstants.ACTION_SET_ERROR,
                                            contextData);
                        }
                    });
            RequestQueue imageRequestQueue = Volley.newRequestQueue(getContext());
            imageRequestQueue.add(request);
        }

        rgRecommendProduct.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.dcc_rb_yes) {

                }
                if (checkedId == R.id.dcc_rb_no) {

                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
