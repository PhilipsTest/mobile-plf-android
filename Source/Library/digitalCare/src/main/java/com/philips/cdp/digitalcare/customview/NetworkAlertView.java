/**
 * @author naveen@philips.com
 * <p>
 * Copyright (c) 2016 Philips. All rights reserved.
 * @description Network Notification view used during Connection not available
 * in application component wise announcement.
 * @Since Apr 7, 2015
 */
package com.philips.cdp.digitalcare.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.philips.platform.uid.view.widget.AlertDialogFragment;


@SuppressLint("NewApi")
public class NetworkAlertView {

    AlertDialogFragment mAlertDialog = null;
    private ProgressDialog mProgressDialog = null;
    private Dialog mDialog = null;
    private Activity mActivity = null;

    /**
     * @param title      : String
     * @param message    : String
     * @param buttontext : String
     */
    public void showAlertBox(Fragment fragment, String title, String message,
                             String buttontext) {
        if (mAlertDialog == null) {
            AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(fragment.getContext());
            if(title != null){
                builder.setTitle(title);
            }
            mAlertDialog = builder.setMessage(message).setPositiveButton(buttontext, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAlertDialog.dismiss();
                        }
                    }
            ).create();

            mAlertDialog.show(fragment.getFragmentManager(), "AlertDialog");
        }
    }
}
