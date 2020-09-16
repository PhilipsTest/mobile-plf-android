/**
 * This class responsible for showing the GPS Alert status in the dialog
 *
 *
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.customview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;

public class GpsAlertView {

	private AlertDialog.Builder mDialogBuilder = null;
	private AlertDialog mAlertDialog = null;
	private static GpsAlertView gpsAlertView = null;

	private GpsAlertView() {
	}

	public static GpsAlertView getInstance() {

		if (gpsAlertView == null) {
			gpsAlertView = new GpsAlertView();
		}
		return gpsAlertView;
	}

	public void showAlert(
			final DigitalCareBaseFragment digitalCareBaseFragment,
			int title_res_id, int message_res_id,
			int positiveButtonText_res_id, int negativeButtonText_res_id) {
		if (mDialogBuilder == null) {
			mDialogBuilder = new AlertDialog.Builder(
					digitalCareBaseFragment.getActivity());

			mDialogBuilder.setMessage(digitalCareBaseFragment.getResources()
					.getString(message_res_id));

			mDialogBuilder.setPositiveButton(positiveButtonText_res_id,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							digitalCareBaseFragment
									.startActivityForResult(
											new Intent(
													android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
											0);

							dialog.dismiss();
							mDialogBuilder = null;

						}
					});

			mDialogBuilder.setNegativeButton(negativeButtonText_res_id,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
							mDialogBuilder = null;
							//
						}
					}).setIcon(android.R.drawable.ic_dialog_alert);

			mAlertDialog = mDialogBuilder.create();
			mAlertDialog.show();
		}
	}

	public void removeAlert() {
		if (mAlertDialog != null) {
			mAlertDialog.dismiss();
			mAlertDialog = null;
			mDialogBuilder = null;
		}
	}

}
