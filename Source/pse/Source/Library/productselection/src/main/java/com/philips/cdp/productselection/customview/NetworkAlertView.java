package com.philips.cdp.productselection.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;


@SuppressLint("NewApi")
public class NetworkAlertView {

    AlertDialog mAlertDialog = null;
    private Dialog mDialog = null;
    private Activity mActivity = null;

    /**
     * @param title      : String
     * @param message    : String
     * @param buttontext : String
     */
    public void showAlertBox(Activity activity, String title, String message,
                             String buttontext) {

        if (mAlertDialog == null) {

            mAlertDialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mAlertDialog.dismiss();

                                }
                            }).show();

        }

    }
}
