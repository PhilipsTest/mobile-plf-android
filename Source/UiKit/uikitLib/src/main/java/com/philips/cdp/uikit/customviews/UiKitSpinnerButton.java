/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.uikit.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.philips.cdp.uikit.R;

/**
 * Created by 310240027 on 5/5/2016.
 */
public class UiKitSpinnerButton extends FrameLayout implements View.OnClickListener {

    View view;
    ProgressBar progressBar;
    UIKitButton button;
    private OnClickListener listener;

    public UiKitSpinnerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        view = LayoutInflater.from(context).inflate(R.layout.uikit_spinner_button, this);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarPB);
        progressBar.setVisibility(ProgressBar.GONE);
            /*if (progressBar.getIndeterminateDrawable() != null) {
                progressBar.getIndeterminateDrawable().setColorFilter( ContextCompat.getColor(context, R.color.uikit_enricher4), PorterDuff.Mode.SRC_ATOP);
            }*/
        progressBar.incrementProgressBy(1);
        button = (UIKitButton) view.findViewById(R.id.buttonPB);
        button.setOnClickListener(this);
    }

    public void setProgress(int progress) {
        if (progress > 0) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null)
                listener.onClick(this);
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if (listener != null)
                listener.onClick(this);
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof UIKitButton) {
            enableProgress();
        }
    }

    public void enableProgress() {
        button.setText("");
        button.setEnabled(false);
        button.setClickable(false);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setProgress(0);
    }

    public void disableProgress(String text) {
        button.setText(text);
        button.setEnabled(true);
        button.setClickable(true);
        progressBar.setVisibility(ProgressBar.GONE);
        progressBar.setProgress(0);

    }

}
