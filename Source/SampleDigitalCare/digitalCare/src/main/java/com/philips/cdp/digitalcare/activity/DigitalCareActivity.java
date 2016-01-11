package com.philips.cdp.digitalcare.activity;

import android.os.Bundle;

import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.homefragment.SupportHomeFragment;
import com.philips.cdp.digitalcare.social.ProductImageHelper;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.util.DigitalCareConstants;

/*import com.philips.cdp.digitalcare.social.twitter.TwitterAuthentication;*/

/*import com.philips.cdp.digitalcare.social.facebook.FacebookHelper;
 import com.philips.cdp.digitalcare.social.facebook.FacebookScreenFragment;*/

/**
 * DigitalCareActivity is the main container class for Digital Care fragments.
 * Also responsible for fetching Product images, Facebook authentication & also
 * Twitter authentication.
 *
 * @author : Ritesh.jha@philips.com
 * @since : 5 Dec 2014
 */
public  class DigitalCareActivity extends DigitalCareBaseActivity {
    private static final String TAG = DigitalCareActivity.class.getSimpleName();
    private static int mEnterAnimation = -1;
    private static int mExitAnimation = -1;

    private ProductImageHelper mImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digi_care);
        try {
            initActionBar();
        } catch (ClassCastException e) {
            DigiCareLogger.e(TAG, "Actionbar: " + e.getMessage());
        }
        animateThisScreen();
        showFragment(new SupportHomeFragment());
        enableActionBarHome();
    }

    private void animateThisScreen() {
        Bundle bundleExtras = getIntent().getExtras();

        String startAnim = null;
        String endAnim = null;

        int startAnimation = bundleExtras.getInt(DigitalCareConstants.START_ANIMATION_ID);
        int endAnimation = bundleExtras.getInt(DigitalCareConstants.STOP_ANIMATION_ID);
        int orientation = bundleExtras.getInt(DigitalCareConstants.SCREEN_ORIENTATION);

        if (startAnimation == 0 && endAnimation == 0) {
            return;
        }

        startAnim = getResources().getResourceName(startAnimation);
        endAnim = getResources().getResourceName(endAnimation);

        String packageName = getPackageName();
        mEnterAnimation = getApplicationContext().getResources().getIdentifier(startAnim,
                "anim", packageName);
        mExitAnimation = getApplicationContext().getResources().getIdentifier(endAnim, "anim",
                packageName);
        setRequestedOrientation(orientation);
        overridePendingTransition(mEnterAnimation, mExitAnimation);
    }

	/*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		DigiCareLogger.i(TAG, "DigitalCareActivity onActivityResult");

		*//*
         * if (requestCode == DigitalCareContants.FACEBOOK_REQUESTC0DE) {
		 * DigiCareLogger.e(TAG, "Facebook Authentication..");
		 * startFaceBookSDK(requestCode, resultCode, data);
		 * 
		 * }
		 *//*
        if (resultCode == FragmentActivity.RESULT_CANCELED
				&& requestCode == TwitterAuthentication.WEBVIEW_REQUEST_CODE) {
			DigiCareLogger.e(TAG, "Twitter failed to authenticate");
			stopTwitterSDK();
		}
		if (resultCode == FragmentActivity.RESULT_OK
				&& requestCode == TwitterAuthentication.WEBVIEW_REQUEST_CODE) {
			DigiCareLogger.i(TAG, "Twitter Authenticated successfully");
			startTwitterSDK(data);
		} else if (resultCode == FragmentActivity.RESULT_OK) {
			AnalyticsTracker.trackAction(
					AnalyticsConstants.ACTION_RECEIPT_PHOTO,
					AnalyticsConstants.ACTION_KEY_PHOTO,
					AnalyticsConstants.ACTION_VALUE_PHOTO_VALUE);

			if (requestCode == DigitalCareContants.IMAGE_PICK) {
				startImageParse(requestCode, data);

			} else if (requestCode == DigitalCareContants.IMAGE_CAPTURE) {
				startImageParse(requestCode, data);
			}
		}
	}

	protected void startTwitterSDK(Intent data) {
		TwitterAuthentication mTwitter = TwitterAuthentication.getInstance();
		mTwitter.onActivityResult(data);
	}

	protected void stopTwitterSDK() {
		TwitterAuthentication mTwitter = TwitterAuthentication.getInstance();
		mTwitter.onFailedToAuthenticate();
	}*/

	/*
     * protected void startFaceBookSDK(int requestCode, int resultCode, Intent
	 * data) { FacebookScreenFragment fbFrag = new FacebookScreenFragment();
	 * fbFrag.onFaceBookCallback(this, requestCode, resultCode, data);
	 * 
	 * FacebookHelper mFacebookHelper = FacebookHelper.getInstance();
	 * mFacebookHelper.onFaceBookCallback(this, requestCode, resultCode, data);
	 * AnalyticsTracker.trackAction(
	 * AnalyticsConstants.ACTION_KEY_RECEIPT_PHOTO,
	 * AnalyticsConstants.ACTION_KEY_PHOTO,
	 * AnalyticsConstants.ACTION_VALUE_PHOTO_VALUE); }
	 */

	/*protected void startImageParse(int requestCode, Intent data) {

		if (mImage == null)
			mImage = ProductImageHelper.getInstance();

		mImage.processProductImage(data, requestCode);

	}*/
}
