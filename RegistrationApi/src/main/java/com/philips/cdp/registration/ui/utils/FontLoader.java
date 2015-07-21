
package com.philips.cdp.registration.ui.utils;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Typeface;
import android.widget.TextView;

public class FontLoader {

	private static FontLoader mInstance;

	private Map<String, Typeface> mFonts;

	private FontLoader() {
		mFonts = new HashMap<String, Typeface>();
	}

	public static FontLoader getInstance() {
		if (mInstance == null) {
			mInstance = new FontLoader();
		}

		return mInstance;
	}

	public void setTypeface(TextView tv, String fontName) {
		if (fontName == null || fontName.isEmpty()) {
			return;
		}

		fontName = RegConstants.FONT_PATH + fontName;

		if (!tv.isInEditMode()) {
			Typeface typeface = mFonts.get(fontName);
			if (typeface == null) {
				typeface = Typeface.createFromAsset(tv.getContext().getAssets(), fontName);

				mFonts.put(fontName, typeface);
			}
			tv.setTypeface(typeface);
		}
	}

}
