/*
 * Created this custom ProgressAlertDialog, since ProgressDialog has been deprecated.
 */
package com.philips.cdp.digitalcare.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.philips.cdp.digitalcare.R;
import com.philips.platform.uid.thememanager.UIDHelper;

public class ProgressAlertDialog extends AlertDialog {
    public ProgressAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        setProgressView(context);
    }

    private void setProgressView(Context context) {
        Context popUpContext = UIDHelper.getPopupThemedContext(context);
        LayoutInflater systemService = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (systemService != null) {
            if(popUpContext!=null) {
                LayoutInflater inflater = systemService.cloneInContext(popUpContext);
                if(inflater!=null){
                    setView(inflater.inflate(R.layout.consumercare_progress, null));
                }
            }

        }
    }
}