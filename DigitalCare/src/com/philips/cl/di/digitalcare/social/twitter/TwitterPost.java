package com.philips.cl.di.digitalcare.social.twitter;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;

import com.philips.cl.di.digitalcare.DigitalCareConfigManager;
import com.philips.cl.di.digitalcare.social.PostCallback;
import com.philips.cl.di.digitalcare.util.DLog;

/**
 * Thread Task for posting Twitter tweets along with Product Image.
 * 
 * @author naveen@philips.com
 * @since 11/feb/2015
 */
public class TwitterPost extends Thread {

	private String TAG = TwitterPost.class.getSimpleName();
	private Context mContext = null;
	private String mConsumerKey = null;
	private String mConsumerSecret = null;
	private File mFile = null;
	private String mStatus = null;
	private PostCallback mPostCallback = null;
	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	public static final String PREF_NAME = "sample_twitter_pref";

	public TwitterPost(Context c, File f, PostCallback callback, String text) {
		mContext = c;
		mFile = f;
		mStatus = text;
		mPostCallback = callback;
		setPriority(Thread.MAX_PRIORITY);
		DLog.d(TAG, "TwitterPost Thread Started");
		start();
	}

	@Override
	public void run() {
		super.run();

		try {
			mConsumerKey = DigitalCareConfigManager.getTwitterConsumerKey();
			mConsumerSecret = DigitalCareConfigManager
					.getTwitterConsumerSecret();
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(mConsumerKey);
			builder.setOAuthConsumerSecret(mConsumerSecret);

			SharedPreferences mSharedPreferences = null;
			mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, 0);
			String access_token = mSharedPreferences.getString(
					PREF_KEY_OAUTH_TOKEN, "");
			String access_token_secret = mSharedPreferences.getString(
					PREF_KEY_OAUTH_SECRET, "");
			DLog.d(TAG, "Consumer Key in Post Process : " + access_token);
			DLog.d(TAG, "Consumer Secreat Key in post Process : "
					+ access_token_secret);

			AccessToken accessToken = new AccessToken(access_token,
					access_token_secret);
			Twitter twitter = new TwitterFactory(builder.build())
					.getInstance(accessToken);
			StatusUpdate statusUpdate = new StatusUpdate(mStatus);
			if (mFile != null)
				statusUpdate.setMedia(mFile);

			twitter4j.Status response = twitter.updateStatus(statusUpdate);

			DLog.d(TAG, "Twitter Response" + response.getText());
			if (mPostCallback != null)
				mPostCallback.onTaskCompleted();

		} catch (TwitterException e) {
			mPostCallback.onTaskFailed();
			DLog.d(TAG, "Failed to post : " + e);

		}
	}

}
