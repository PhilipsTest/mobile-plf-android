package com.philips.cl.di.digitalcare.social.twitter;


/**
 * @description This is an callback interface to get Twitter authentication callback for Twitter Support Fragment. 
 * @author naveen@philips.com
 * @since 11/feb/2015
 *
 */
public interface TwitterAuth {
	
	void onTwitterLoginFailed();
	
	void onTwitterLoginSuccessful();

}
