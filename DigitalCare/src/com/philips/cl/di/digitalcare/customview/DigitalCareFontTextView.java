package com.philips.cl.di.digitalcare.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.philips.cl.di.digitalcare.R;

/*
 *	DigitalCareFontTextView is the custom Text View.
 * 
 * Author : Ritesh.jha@philips.com
 * 
 * Creation Date : 5 Dec 2014
 */
public class DigitalCareFontTextView extends TextView {

	public DigitalCareFontTextView(Context context) {
		super(context);
	}

	public DigitalCareFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyAttributes(this, context, attrs);
	}

	public DigitalCareFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		applyAttributes(this, context, attrs);
	}

	private void applyAttributes(TextView view, Context context, AttributeSet attrs) {

		if (attrs != null) {
			final TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.DigitalCareFontTextView);
			final String typeface =
					a.getString(R.styleable.DigitalCareFontTextView_fontAssetName);
			a.recycle();

			//set the font using class DigitalCareFontLoader
			DigitalCareFontLoader.getInstance().setTypeface(view, typeface);			
		}
	}


	public void setTypeface(String typeface) {
		DigitalCareFontLoader.getInstance().setTypeface(this, typeface);
	}
}