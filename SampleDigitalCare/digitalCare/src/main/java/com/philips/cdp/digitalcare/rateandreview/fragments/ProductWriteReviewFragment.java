package com.philips.cdp.digitalcare.rateandreview.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.analytics.AnalyticsTracker;
import com.philips.cdp.digitalcare.customview.DigitalCareFontButton;
import com.philips.cdp.digitalcare.customview.DigitalCareFontTextView;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.localematch.LocaleMatchHandler;
import com.philips.cdp.digitalcare.rateandreview.productreview.model.BazaarReviewModel;
import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.util.Locale;

/**
 * This class is responsible for showing the UI for getting the end user information before submiting the
 * product review in the Philips Page.
 *
 * @author naveen@philips.com
 * @since 15/September/2015
 */
public class ProductWriteReviewFragment extends DigitalCareBaseFragment {

    private static final String TAG = ProductWriteReviewFragment.class.getSimpleName();
    private static final String PRODUCT_TERMS_DIALOG_URL = "http://%s/content/7543b-%s/termsandconditions.htm";
    private LinearLayout mParentLayout = null;
    private FrameLayout.LayoutParams mLayoutParams = null;
    private DigitalCareFontButton mOkButton, mCancelButton = null;
    private ImageView mProductImage = null;
    private DigitalCareFontTextView mProductTitle = null;
    private DigitalCareFontTextView mProductCtn = null;
    private DigitalCareFontTextView mTermsText = null;
    private RatingBar mRatingBarVerticle, mRatingBarHorizontal = null;
    private Switch mSwitch = null;
    private EditText mSummaryHeaderEditText, mSummaryDescriptionEditText, mNicknameEditText, mEmailEditText = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DigiCareLogger.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_review_write, container,
                false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        DigiCareLogger.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mParentLayout = (LinearLayout) getActivity().findViewById(R.id.write_product_review_include_container);
        mLayoutParams = (FrameLayout.LayoutParams) mParentLayout
                .getLayoutParams();
        Configuration config = getResources().getConfiguration();
        mOkButton = (DigitalCareFontButton) getActivity().findViewById(R.id.your_product_review_send_button);
        mCancelButton = (DigitalCareFontButton) getActivity().findViewById(R.id.your_product_review_cancel_button);
        mProductImage = (ImageView) getActivity().findViewById(R.id.review_write_rate_productimage);
        mProductTitle = (DigitalCareFontTextView) getActivity().findViewById(R.id.review_write_rate_name);
        mProductCtn = (DigitalCareFontTextView) getActivity().findViewById(R.id.review_write_rate_variant);
        mTermsText = (DigitalCareFontTextView) getActivity().findViewById(R.id.review_write_rate_product_terms_termstext);
        mRatingBarVerticle = (RatingBar) getActivity().findViewById(R.id.review_write_rate_product_ratingBar);
        mRatingBarHorizontal = (RatingBar) getActivity().findViewById(R.id.review_write_rate_product_ratingBar_horizontal);
        mSwitch = (Switch) getActivity().findViewById(R.id.review_write_rate_product_terms_switch);
        mSummaryDescriptionEditText = (EditText) getActivity().findViewById(R.id.review_write_rate_product_header_description);
        mSummaryHeaderEditText = (EditText) getActivity().findViewById(R.id.review_write_rate_product_header_summary);
        mNicknameEditText = (EditText) getActivity().findViewById(R.id.review_write_rate_product_nickname_header_value);
        mEmailEditText = (EditText) getActivity().findViewById(R.id.review_write_rate_product_email_header_value);


        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mTermsText.setOnClickListener(this);
        setRatingBarUI();

        try {
            AnalyticsTracker.trackPage(AnalyticsConstants.PAGE_REVIEW_WRITING,
                    getPreviousName());
        } catch (Exception e) {
            DigiCareLogger.e(TAG, "IllegaleArgumentException : " + e);
        }
        setListeners();

        setViewParams(config);
        float density = getResources().getDisplayMetrics().density;
        setButtonParams(density);
    }

    protected void setListeners() {
        mSummaryDescriptionEditText.addTextChangedListener(mReviewDescriptionWatcher);
        mSummaryHeaderEditText.addTextChangedListener(mReviewHeaderWatcher);
        mEmailEditText.addTextChangedListener(mEmailWatcher);
    }

    protected final TextWatcher mEmailWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.toString().contains("@"))
                Toast.makeText(getActivity(), "Email is valid", Toast.LENGTH_SHORT).show();

        }
    };


    protected final TextWatcher mReviewHeaderWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if ((s.toString() != null) && (s.toString() != ""))
                Toast.makeText(getActivity(), "Header is not null", Toast.LENGTH_SHORT).show();

        }
    };

    protected final TextWatcher mReviewDescriptionWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if ((s.toString() != null) && (s.toString().length() > 49))
                Toast.makeText(getActivity(), "Now the Description is successfullt Exceded 50 characters", Toast.LENGTH_SHORT).show();

        }
    };


    private void setButtonParams(float density) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (getActivity().getResources()
                .getDimension(R.dimen.support_btn_height) * density));
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (getActivity().getResources()
                .getDimension(R.dimen.support_btn_height) * density));

        params.topMargin = (int) getActivity().getResources().getDimension(R.dimen.marginTopButton);
        params.weight = 1;
        param.topMargin = (int) getActivity().getResources().getDimension(R.dimen.marginTopButton);

        mCancelButton.setLayoutParams(params);
        mOkButton.setLayoutParams(params);
        mSummaryHeaderEditText.setLayoutParams(params);
        mNicknameEditText.setLayoutParams(params);
        mEmailEditText.setLayoutParams(params);

    /*   mRatingBarHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.
                LayoutParams.WRAP_CONTENT));*/

    }

    @Override
    public void setViewParams(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutParams.leftMargin = mLayoutParams.rightMargin = mLeftRightMarginPort;
        } else {
            mLayoutParams.leftMargin = mLayoutParams.rightMargin = mLeftRightMarginLand;
        }
        mParentLayout.setLayoutParams(mLayoutParams);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setViewParams(newConfig);
    }

    private void setRatingBarUI() {
        mRatingBarVerticle = (RatingBar) getActivity().findViewById(R.id.review_write_rate_product_ratingBar);
        mRatingBarVerticle.setNumStars(5);
        mRatingBarVerticle.setMax(5);
        mRatingBarVerticle.setStepSize(1f);
        setRatingBarLayers(mRatingBarVerticle);
        /*LayerDrawable stars = (LayerDrawable) mRatingBarVerticle
                .getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#528E18"),
                PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor("#528E18"),
                PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.parseColor("#CCD9BE"),
                PorterDuff.Mode.SRC_ATOP);*/

        mRatingBarHorizontal = (RatingBar) getActivity().findViewById(R.id.review_write_rate_product_ratingBar);
        mRatingBarHorizontal.setNumStars(5);
        mRatingBarHorizontal.setMax(5);
        mRatingBarHorizontal.setStepSize(1f);
        //     mRatingBarHorizontal.set
        setRatingBarLayers(mRatingBarHorizontal);


    }


    protected void setRatingBarLayers(RatingBar ratingbar) {
        LayerDrawable stars = (LayerDrawable) ratingbar
                .getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#528E18"),
                PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor("#528E18"),
                PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.parseColor("#CCD9BE"),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.bazzarvoice_productreview_writescreen_actionbar_title);
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_REVIEW_WRITING;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == (R.id.your_product_review_send_button))
            submitReview();
        if (v.getId() == (R.id.review_write_rate_product_terms_termstext))
            showEULAAlert(getTermsAndConditionsPage().toString());
        else if (v.getId() == R.id.your_product_review_cancel_button)
            backstackFragment();
    }

    protected Uri getTermsAndConditionsPage() {

        Locale info = DigitalCareConfigManager.getInstance().getLocaleMatchResponseWithCountryFallBack();
        String locale = info.toString();
        String countryFallbackUrl = LocaleMatchHandler.getPRXUrl(locale);
        String termsAndConditionsUrl = countryFallbackUrl.replace("www.", "brand-reviews.");

        return Uri.parse(String.format(PRODUCT_TERMS_DIALOG_URL, termsAndConditionsUrl, locale));
    }


    public String getCtn() {
        return DigitalCareConfigManager.getInstance().getConsumerProductInfo().getCtn();
    }

    public String getProductTitle() {
        return DigitalCareConfigManager.getInstance().getConsumerProductInfo().getProductTitle();
    }

    public String getNickNameValue(EditText editTextView) {
        return editTextView.getText().toString();
    }

    public String getEmailValue(EditText editTextView) {
        return editTextView.getText().toString();
    }

    public String getReviewSummaryValue(EditText editTextView) {
        return editTextView.getText().toString();
    }

    public String getReviewDescriptionValue(EditText editTextView) {
        return editTextView.getText().toString();
    }

    public float getRatingValue(RatingBar ratingBarView) {
        return ratingBarView.getRating();
    }

    public boolean getLegalTermValue(Switch switchView) {
        return switchView.isChecked();
    }

    /**
     * Does some client-side validation before calling the necessary
     * BazaarFunctions function to submit a review (only previews to facilitate
     * easier testing). When the response comes in, it launches the next
     * activity.
     * <p/>
     * If the photo has not uploaded yet, we put off submitting and show an
     * "Uploading Photo..." dialog.
     */
    private void submitReview() {
        if (getRatingValue(mRatingBarVerticle) == 0) {
            Toast.makeText(getActivity(),
                    "You must give a rating between 1 and 5.",
                    Toast.LENGTH_SHORT).show();
        } else if (getReviewSummaryValue(mSummaryHeaderEditText).equals("")) {
            Toast.makeText(getActivity(), "You must enter a summary.",
                    Toast.LENGTH_SHORT).show();
        } else if (getReviewDescriptionValue(mSummaryDescriptionEditText).equals("")) {
            Toast.makeText(getActivity(), "You must enter a description.",
                    Toast.LENGTH_SHORT).show();
        } else if (getNickNameValue(mNicknameEditText).equals("")) {
            Toast.makeText(getActivity(), "You must enter a nick name.",
                    Toast.LENGTH_SHORT).show();
        } else if (getEmailValue(mEmailEditText).equals("")) {
            Toast.makeText(getActivity(), "You must enter a email.",
                    Toast.LENGTH_SHORT).show();
        } else if (!getLegalTermValue(mSwitch)) {
            Toast.makeText(getActivity(), "You must agree the term and conditions.",
                    Toast.LENGTH_SHORT).show();
        } else {

            BazaarReviewModel reviewModel = new BazaarReviewModel();
            reviewModel.setRating((float) mRatingBarVerticle.getRating());
            reviewModel.setSummary(mSummaryHeaderEditText.getText().toString());
            reviewModel.setReview(mSummaryDescriptionEditText.getText().toString());
            reviewModel.setNickname(mNicknameEditText.getText().toString());
            reviewModel.setEmail(mEmailEditText.getText().toString());

            ProductReviewPreviewFragment productReviewPreviewFragment = new ProductReviewPreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("productReviewModel", reviewModel);

            productReviewPreviewFragment.setArguments(bundle);
            showFragment(productReviewPreviewFragment);
        }
    }
}
