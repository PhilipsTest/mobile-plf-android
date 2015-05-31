package com.philips.cl.di.digitalcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.philips.cl.di.digitalcare.analytics.AnalyticsConstants;
import com.philips.cl.di.digitalcare.analytics.AnalyticsTracker;
import com.philips.cl.di.digitalcare.contactus.ContactUsFragment;
import com.philips.cl.di.digitalcare.locatephilips.LocatePhilipsFragment;
import com.philips.cl.di.digitalcare.productdetails.ProductDetailsFragment;
import com.philips.cl.di.digitalcare.productregistration.ProductRegistrationFragment;
import com.philips.cl.di.digitalcare.rateandreview.RateThisAppFragment;
import com.philips.cl.di.digitalcare.social.ProductImageHelper;
import com.philips.cl.di.digitalcare.social.facebook.FacebookHelper;
import com.philips.cl.di.digitalcare.social.facebook.FacebookScreenFragment;
import com.philips.cl.di.digitalcare.social.twitter.TwitterAuthentication;
import com.philips.cl.di.digitalcare.util.DLog;
import com.philips.cl.di.digitalcare.util.DigitalCareContants;

/**
 * DigitalCareActivity is the main container class for Digital Care fragments.
 * Also responsible for fetching Product images, Facebook authentication & also
 * Twitter authentication.
 * 
 * @author : Ritesh.jha@philips.com
 * 
 * @since : 5 Dec 2014
 */
public class DigitalCareActivity extends DigitalCareBaseActivity {
	private static final String TAG = "DigitalCareActivity";
	private ProductImageHelper mImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_digi_care);
		try {
			initActionBar();
		} catch (ClassCastException e) {
			DLog.e(TAG, "Actionbar: " + e.getMessage());
		}
		showScreen();
	}

	private void showScreen() {
		String screenChoice = DigitalCareConfigManager.getLaunchingScreen();
		if (screenChoice
				.equalsIgnoreCase(DigitalCareContants.OPTION_SUPPORT_SCREEN)) {
			showFragment(new SupportHomeFragment());
			enableActionBarHome();
		}
		else if (screenChoice
				.equalsIgnoreCase(DigitalCareContants.OPTION_REGISTER_PRODUCT)) {
			showFragment(new ProductRegistrationFragment());
		}
		
		else if (screenChoice
				.equalsIgnoreCase(DigitalCareContants.OPTION_WHAT_ARE_YOU_THINKING)) {
			showFragment(new RateThisAppFragment());
		}
		else if (screenChoice
				.equalsIgnoreCase(DigitalCareContants.OPTION_FIND_PHILIPS_NEARBY)) {
			showFragment(new LocatePhilipsFragment());
		}
		
		else if (screenChoice
				.equalsIgnoreCase(DigitalCareContants.OPTION_CONTACT_US)) {
			showFragment(new ContactUsFragment());
		}
		else if (screenChoice
				.equalsIgnoreCase(DigitalCareContants.OPTION_PRODUCS_DETAILS)) {
			showFragment(new ProductDetailsFragment());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		DLog.i(TAG, "DigitalCareActivity onActivityResult");

		if (requestCode == 64206) {
			FacebookScreenFragment fbFrag = new FacebookScreenFragment();
			fbFrag.onFaceBookCallback(this, requestCode, resultCode, data);

			FacebookHelper mFacebookHelper = FacebookHelper.getInstance();
			mFacebookHelper.onFaceBookCallback(this, requestCode, resultCode,
					data);
			AnalyticsTracker.trackAction(
					AnalyticsConstants.ACTION_KEY_RECEIPT_PHOTO,
					AnalyticsConstants.ACTION_KEY_PHOTO,
					AnalyticsConstants.ACTION_VALUE_PHOTO_VALUE);
		}
		if (resultCode == Activity.RESULT_CANCELED
				&& requestCode == TwitterAuthentication.WEBVIEW_REQUEST_CODE) {
			TwitterAuthentication mTwitter = TwitterAuthentication
					.getInstance();
			mTwitter.onFailedToAuthenticate();
		}
		if (resultCode == Activity.RESULT_OK
				&& requestCode == TwitterAuthentication.WEBVIEW_REQUEST_CODE) {
			TwitterAuthentication mTwitter = TwitterAuthentication
					.getInstance();
			mTwitter.onActivityResult(data);
		} else if (resultCode == Activity.RESULT_OK) {
			AnalyticsTracker.trackAction(
					AnalyticsConstants.ACTION_KEY_RECEIPT_PHOTO,
					AnalyticsConstants.ACTION_KEY_PHOTO,
					AnalyticsConstants.ACTION_VALUE_PHOTO_VALUE);

			if (requestCode == DigitalCareContants.IMAGE_PICK) {
				if (mImage == null)
					mImage = ProductImageHelper.getInstance();

				mImage.processProductImage(data, requestCode);
			} else if (requestCode == DigitalCareContants.IMAGE_CAPTURE) {
				if (mImage == null)
					mImage = ProductImageHelper.getInstance();
				mImage.processProductImage(data, requestCode);
			}
		}
	}
}
