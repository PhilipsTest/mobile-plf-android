//package com.philips.cdp.digitalcare.rateandreview.fragments;
//
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.LayerDrawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RatingBar;
//
//import com.philips.cdp.digitalcare.R;
//import com.philips.cdp.digitalcare.customview.DigitalCareFontButton;
//import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
//import com.philips.cdp.digitalcare.util.DigiCareLogger;
//
///**
// * This class is responsible for showing the UI for showing the preview of the end user information.
// *
// * @author naveen@philips.com
// * @since 15/September/2015
// */
//
//public class ProductReviewPreviewFragment extends DigitalCareBaseFragment {
//
//
//    private static final String TAG = ProductReviewPreviewFragment.class.getSimpleName();
//    private DigitalCareFontButton mOkButton, mCancelButton = null;
//    private RatingBar mRatingBar = null;
//    private DigitalCareFontTextView mReviewSummaryHeader = null;
//    private DigitalCareFontTextView mReviewDescription = null;
//    private DigitalCareFontTextView mNickName = null;
//    private DigitalCareFontTextView mEmail = null;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        DigiCareLogger.d(TAG, "onCreateView");
//        View mView = inflater.inflate(R.layout.fragment_review_your, container,
//                false);
//        return mView;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        DigiCareLogger.d(TAG, "onActivityCreated");
//        super.onActivityCreated(savedInstanceState);
//        mOkButton = (DigitalCareFontButton) getActivity().findViewById(R.id.your_product_review_preview_send_button);
//        mCancelButton = (DigitalCareFontButton) getActivity().findViewById(R.id.your_product_review_preview_cancel_button);
//        mReviewSummaryHeader = (DigitalCareFontTextView) getActivity().findViewById(R.id.your_product_review_summary_header_text_value);
//        mReviewDescription = (DigitalCareFontTextView) getActivity().findViewById(R.id.your_product_review__header_text_value);
//        mNickName = (DigitalCareFontTextView) getActivity().findViewById(R.id.your_product_review_nickname_header_text_value);
//        mEmail = (DigitalCareFontTextView) getActivity().findViewById(R.id.your_product_review_email_header_text_value);
//        mOkButton.setOnClickListener(this);
//        setRatingBarUI();
//    }
//
//
//    @Override
//    public void setViewParams(Configuration config) {
//
//    }
//
//    @Override
//    public String getActionbarTitle() {
//        return null;
//    }
//
//    @Override
//    public String setPreviousPageName() {
//        return null;
//    }
//
//    /**
//     * Called when a view has been clicked.
//     *
//     * @param v The view that was clicked.
//     */
//    @Override
//    public void onClick(View v) {
//
//        if (v.getId() == (R.id.your_product_review_preview_send_button))
//            showFragment(new ProductReviewThankyouFragment());
//
//    }
//
//    private void setRatingBarUI() {
//        mRatingBar = (RatingBar) getActivity().findViewById(R.id.your_product_review_rating_ratingbar);
//
//        LayerDrawable stars = (LayerDrawable) mRatingBar
//
//                .getProgressDrawable();
//
//        stars.getDrawable(2).setColorFilter(Color.parseColor("#528E18"),
//
//                PorterDuff.Mode.SRC_ATOP);
//
//        stars.getDrawable(1).setColorFilter(Color.parseColor("#528E18"),
//                PorterDuff.Mode.SRC_ATOP);
//
//        stars.getDrawable(0).setColorFilter(Color.parseColor("#CCD9BE"),
//
//                PorterDuff.Mode.SRC_ATOP);
//
//    }
//}
