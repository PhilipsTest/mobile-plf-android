package utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.philips.platform.uid.thememanager.UIDHelper;
import com.philips.platform.uid.view.widget.AlertDialogFragment;

public class PIMNetworkUtility {
    private static PIMNetworkUtility mNetworkUtility;
    private AlertDialogFragment alertDialogFragment;
    public static final String ALERT_DIALOG_TAG = "ALERT_DIALOG_TAG";

    public static PIMNetworkUtility getInstance() {
        synchronized (PIMNetworkUtility.class) {
            if (mNetworkUtility == null) {
                mNetworkUtility = new PIMNetworkUtility();
            }
        }
        return mNetworkUtility;
    }

    public void showErrorDialog(Context context, FragmentManager pFragmentManager,
                                String pButtonText, String pErrorString, String pErrorDescription) {

        //Track pop up
        if (!((Activity) context).isFinishing()) {
            showDLSDialog(UIDHelper.getPopupThemedContext(context), pButtonText, pErrorString, pErrorDescription, pFragmentManager);
        }
    }


    private void showDLSDialog(final Context context, String pButtonText, String pErrorString, String pErrorDescription, final FragmentManager pFragmentManager) {
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(context)
                .setMessage(pErrorDescription).
                        setPositiveButton(pButtonText, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dismissAlertFragmentDialog(alertDialogFragment);
                            }
                        });

        builder.setTitle(pErrorString);
        if (alertDialogFragment != null) {
            dismissAlertFragmentDialog(alertDialogFragment);
        }
        alertDialogFragment = builder.create();
        if(alertDialogFragment==null) {
            alertDialogFragment = builder.setCancelable(false).create();
        }

        if (!alertDialogFragment.isVisible()) {
            alertDialogFragment.show(pFragmentManager, ALERT_DIALOG_TAG);
        }

    }

    private void dismissAlertFragmentDialog(AlertDialogFragment alertDialogFragment) {

        if(alertDialogFragment!=null && alertDialogFragment.isVisible())
            alertDialogFragment.dismiss();
    }

}
