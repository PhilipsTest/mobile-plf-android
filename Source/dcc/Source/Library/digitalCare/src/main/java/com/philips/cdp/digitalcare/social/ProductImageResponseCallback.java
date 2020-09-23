/**
 * ProductImageInteface interface used for attaching and dettaching the product
 * image in contactUs support.
 *
 * @since 11/feb/2015
 *
 *  Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.social;

import android.graphics.Bitmap;


public interface ProductImageResponseCallback {

	void onImageReceived(Bitmap image, String Uri);

	void onImageDettached();

}
