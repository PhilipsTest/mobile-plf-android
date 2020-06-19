package com.philips.cdp.productselection.fragments.detailedscreen.adapter;

/*
 * Created this custom ProgressAlertDialog, since ProgressDialog has been deprecated.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.philips.cdp.productselection.R;
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
            LayoutInflater inflater = systemService.cloneInContext(popUpContext);
            setView(inflater.inflate(R.layout.prodreg_progress, null));
        }
    }
}
