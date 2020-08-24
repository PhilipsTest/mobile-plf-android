/**
 * This interfaces send callback message to Facebook & Twitter
 *              after successfully posting the messages to there respective
 *              Philips Wall.
 *
 *
 * @since Mar 13, 2015
 *
 *  Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.social;


public interface PostCallback {

	void onTaskCompleted();

	void onTaskFailed();
}
