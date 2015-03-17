package com.philips.cl.di.digitalcare.social.twitter;

import java.io.File;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.cl.di.digitalcare.DigitalCareBaseFragment;
import com.philips.cl.di.digitalcare.R;
import com.philips.cl.di.digitalcare.customview.DigitalCareFontButton;
import com.philips.cl.di.digitalcare.social.PostCallback;
import com.philips.cl.di.digitalcare.social.ProductImageHelper;
import com.philips.cl.di.digitalcare.social.ProductImageResponseCallback;
import com.philips.cl.di.digitalcare.util.DLog;

/**
 * @description This Screen helps endusers to send the product info/concern
 *              along with Product image to the Philips Twitter Support page.
 *              
 * @author naveen@philips.com
 * @since Feb 10, 2015
 */
public class TwitterFragment extends DigitalCareBaseFragment implements
		OnCheckedChangeListener, ProductImageResponseCallback, PostCallback {

	private static final String TAG = TwitterFragment.class
			.getSimpleName();
	private String mUsername;
	private View mTwitterView = null;
	private File mFile = null;
	private SharedPreferences mSharedPreferences = null;
	private LinearLayout mContainer = null;
	private DigitalCareFontButton mCancelPort = null;
	private DigitalCareFontButton mSendPort = null;
	private CheckBox mCheckBox = null;
	private EditText mProdInformation = null;
	private ImageView mProductImage = null;
	private ImageView mProductCloseButton = null;
	private ProgressDialog mPostProgress = null;
	private LayoutParams mContainerParams = null;

	private TextView mTweetfrom = null;
	private ImageView mTwitterIcon = null;
	private final String DESCRIPTION = "@PhilipsCare can you help me with my Airfryer HD9220/20 I think it is broken. Nulllaaaaaaaa";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mTwitterView = inflater.inflate(R.layout.fragment_facebook_screen,
				container, false);
		mSharedPreferences = getActivity().getSharedPreferences(
				TwitterAuthentication.PREF_NAME, 0);
		mUsername = mSharedPreferences.getString(TwitterAuthentication.PREF_USER_NAME,
				"");
		DLog.d(TAG, "Twitter UI Created with Uname value.." + mUsername);
		return mTwitterView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Resources mResources = getActivity().getResources();

		mContainer = (LinearLayout) getActivity().findViewById(
				R.id.fbPostContainer);
		mContainerParams = (LayoutParams) mContainer
				.getLayoutParams();
		mCancelPort = (DigitalCareFontButton) getActivity().findViewById(
				R.id.facebookCancelPort);
		mSendPort = (DigitalCareFontButton) getActivity().findViewById(
				R.id.facebookSendPort);
		mTweetfrom = (TextView) getActivity().findViewById(
				R.id.fb_Post_FromHeaderText);
		mTwitterIcon = (ImageView) getActivity().findViewById(
				R.id.socialLoginIcon);
		mCheckBox = (CheckBox) getActivity()
				.findViewById(R.id.fb_Post_CheckBox);
		mProdInformation = (EditText) getActivity().findViewById(
				R.id.share_text);
		mProductImage = (ImageView) getActivity().findViewById(
				R.id.fb_post_camera);
		mProductCloseButton = (ImageView) getActivity().findViewById(
				R.id.fb_Post_camera_close);

		mCancelPort.setOnClickListener(this);
		mSendPort.setOnClickListener(this);
		mCheckBox.setOnCheckedChangeListener(this);
		mProductImage.setOnClickListener(this);
		mProductCloseButton.setOnClickListener(this);

		Configuration mConfig = mResources.getConfiguration();
		setViewParams(mConfig);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DLog.d(TAG, "Configuration Changed");
		setViewParams(newConfig);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.facebookCancelPort:
			backstackFragment();
			break;
		case R.id.facebookSendPort:
			new TwitterPost(getActivity(), mFile, this)
					.execute(mProdInformation.getText().toString());
			mPostProgress = new ProgressDialog(getActivity());
			mPostProgress.setMessage("Posting to Philips Twitter Support...");
			mPostProgress.setCancelable(false);
			break;
		case R.id.fb_Post_CheckBox:
			break;
		case R.id.fb_post_camera:
			ProductImageHelper.getInstance(getActivity(), this).pickImage();
			break;
		case R.id.fb_Post_camera_close:
			onImageDettach();
			break;
		default:
			break;
		}
	}

	@Override
	public void setViewParams(Configuration config) {
		configureValues();
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			DLog.d(TAG, "PORTRAIT Orientation");
			mContainerParams.leftMargin = mContainerParams.rightMargin = mLeftRightMarginPort;
		} else {
			DLog.d(TAG, "Horizontal Orientaton");
			mContainerParams.leftMargin = mContainerParams.rightMargin = mLeftRightMarginLand;
		}
		mContainer.setLayoutParams(mContainerParams);
	}

	private void configureValues() {
		mTweetfrom.setText("From @" + mUsername);
		mTwitterIcon.setImageResource(R.drawable.social_twitter_icon);
		mCheckBox.setChecked(true);
		mProdInformation.setHint("");
		mProdInformation.setText(DESCRIPTION);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			mProdInformation.setText(DESCRIPTION);
			mProdInformation.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			mProdInformation.setEnabled(true);
			mProdInformation.setFocusable(true);
		} else {
			mProdInformation.setText("");
			mProdInformation.setInputType(InputType.TYPE_NULL);
			mProdInformation.setEnabled(false);
			mProdInformation.setFocusable(false);
		}
	}

	@Override
	public void onImageReceived(Bitmap image, String Uri) {
		mFile = new File(Uri);
		Toast.makeText(getActivity(),
				"Image Path : " + mFile.getAbsolutePath(), Toast.LENGTH_SHORT)
				.show();
		mProductImage.setImageBitmap(image);
		mProductImage.setScaleType(ScaleType.FIT_XY);
		mProductCloseButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onImageDettach() {
		mFile = null;
		DLog.d(TAG, "Product Image Dettached");
		mProductImage.setImageDrawable(getActivity().getResources()
				.getDrawable(R.drawable.social_photo_default));
		mProductImage.setScaleType(ScaleType.FIT_XY);
		mProductCloseButton.setVisibility(View.GONE);
	}

	@Override
	public void onTaskCompleted() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(), "Posted Successfully!!",
						Toast.LENGTH_SHORT).show();
				closeProgress();
				backstackFragment();
			}
		});
	}

	@Override
	public void onTaskFailed() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(), "Failed to post..",
						Toast.LENGTH_SHORT).show();
				closeProgress();
			}
		});
	}

	private void closeProgress() {
		if (mPostProgress.isShowing())
			mPostProgress.dismiss();
	}

	@Override
	public String getActionbarTitle() {
		return getResources()
				.getString(R.string.social_login_post);
	}
}
