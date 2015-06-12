package com.philips.cl.di.digitalcare.social.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.text.TextUtils;

import com.philips.cl.di.digitalcare.DigitalCareConfigManager;
import com.philips.cl.di.digitalcare.util.DigiCareLogger;

/**
 * TwitterAuthentication.
 * 
 * @author naveen@philips.com
 * @since 11/Feb/2015
 */
public class TwitterAuthentication {

	private static final String TAG = TwitterAuthentication.class
			.getSimpleName();
	private static TwitterAuthentication mTwitterConnect = null;
	private static Activity mContext = null;
	private TwitterAuthenticationCallback mTwitterAuth = null;
	private String mConsumerKey = null;
	private String mConsumerSecret = null;
	private String mCallbackUrl = null;
	private String mAuthVerifier = null;
	private String mTwitterVerifier = null;
	private Twitter mTwitter = null;
	private RequestToken mRequestToken = null;
	private SharedPreferences mSharedPreferences = null;

	public static final String PREF_NAME = "sample_twitter_pref";
	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	public static final String PREF_USER_NAME = "twitter_user_name";

	public static final int WEBVIEW_REQUEST_CODE = 100;

	private TwitterAuthentication() {
	}

	public static TwitterAuthentication getInstance(Activity activity) {
		mContext = activity;
		if (mTwitterConnect == null)
			mTwitterConnect = new TwitterAuthentication();
		return mTwitterConnect;
	}

	public static TwitterAuthentication getInstance() {
		return mTwitterConnect;
	}

	public void initSDK(TwitterAuthenticationCallback auth) {
		this.mTwitterAuth = auth;
		mConsumerKey = DigitalCareConfigManager.getTwitterConsumerKey();
		mConsumerSecret = DigitalCareConfigManager.getTwitterConsumerSecret();
		mAuthVerifier = "oauth_verifier";

		if (TextUtils.isEmpty(mConsumerKey)
				|| TextUtils.isEmpty(mConsumerSecret)) {
			DigiCareLogger.d(TAG, "Invalid ConsumerKey & ConsumerSecreat key");
			return;
		}
		mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, 0);
		boolean isLoggedIn = mSharedPreferences.getBoolean(
				PREF_KEY_TWITTER_LOGIN, false);

		if (isLoggedIn) {
			DigiCareLogger.d(TAG, "Already LoggedIn");
			mTwitterAuth.onTwitterLoginSuccessful();
		} else {
			DigiCareLogger.d(TAG, "Logging inti Twitter");
			Thread mLoginThread = new Thread(new Runnable() {
				@Override
				public void run() {
					loginToTwitter();
				}
			});
			mLoginThread.setPriority(Thread.MAX_PRIORITY);
			mLoginThread.start();
			Uri uri = mContext.getIntent().getData();

			if (uri != null && uri.toString().startsWith(mCallbackUrl)) {
				String verifier = uri.getQueryParameter(mAuthVerifier);
				try {
					AccessToken accessToken = mTwitter.getOAuthAccessToken(
							mRequestToken, verifier);
					saveTwitterInformation(accessToken);
					mTwitterAuth.onTwitterLoginSuccessful();
				} catch (Exception e) {
					DigiCareLogger.e("Failed to login Twitter!!", e.getMessage());
					mTwitterAuth.onTwitterLoginFailed();
				}
			}
		}
	}

	private void saveTwitterInformation(AccessToken accessToken) {
		DigiCareLogger.d(TAG, "Save Twitter Information");
		long userID = accessToken.getUserId();
		DigiCareLogger.d(TAG, "USer ID : " + userID);

		User user;
		try {
			user = mTwitter.showUser(userID);
			DigiCareLogger.d(TAG, "User : " + user.toString());

			String username = user.getName();
			Editor e = mSharedPreferences.edit();
			e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
			e.putString(PREF_USER_NAME, username);
			e.commit();
			mContext.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTwitterAuth.onTwitterLoginSuccessful();
				}
			});

		} catch (TwitterException e1) {
			e1.printStackTrace();
			mTwitterAuth.onTwitterLoginFailed();
		}
	}

	private void loginToTwitter() {
		boolean isLoggedIn = mSharedPreferences.getBoolean(
				PREF_KEY_TWITTER_LOGIN, false);

		if (!isLoggedIn) {
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(mConsumerKey);
			builder.setOAuthConsumerSecret(mConsumerSecret);

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			mTwitter = factory.getInstance();

			try {
				mRequestToken = mTwitter.getOAuthRequestToken(mCallbackUrl);
				final Intent intent = new Intent(mContext,
						TwitterAuthenticationActivity.class);
				intent.putExtra(TwitterAuthenticationActivity.EXTRA_URL,
						mRequestToken.getAuthenticationURL());
				mContext.startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
	}

	public void onActivityResult(Intent data) {

		DigiCareLogger.d(TAG, "Received Twitter session from DigitalCare Activity");
		if (data != null)
			mTwitterVerifier = data.getExtras().getString(mAuthVerifier);
		DigiCareLogger.d(TAG, "Verifier : " + mTwitterVerifier);

		new Thread(new Runnable() {

			@Override
			public void run() {
				AccessToken accessToken;
				try {
					accessToken = mTwitter.getOAuthAccessToken(mRequestToken,
							mTwitterVerifier);
					DigiCareLogger.d(TAG, "AccessToken : " + accessToken);
					if (accessToken != null)
						saveTwitterInformation(accessToken);
				} catch (Exception e) {
					DigiCareLogger.e("Twitter Login Failed", "" + e);
					mTwitterAuth.onTwitterLoginFailed();
				}
			}
		}).start();
	}

	public void onFailedToAuthenticate() {
		mTwitterAuth.onTwitterLoginFailed();
	}
}
