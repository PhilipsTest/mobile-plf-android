
/*
 *  Copyright (c) Koninklijke Philips N.V., 2016
 *  All rights are reserved. Reproduction or dissemination
 *  * in whole or in part is prohibited without the prior written
 *  * consent of the copyright holder.
 * /
 */

package com.philips.cdp.registration.ui.utils;

import android.app.*;
import android.view.*;
import android.widget.Button;

import com.philips.cdp.registration.*;
import com.philips.platform.uid.view.widget.*;

public class RegAlertDialog {
	private static AlertDialog alertDialogBuilder;
	public static void showResetPasswordDialog(String title, String content, Activity activity, View.OnClickListener continueBtnClickListener) {

		alertDialogBuilder = new AlertDialog.Builder(activity).create();
		alertDialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialogBuilder.setCancelable(false);
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.reg_dialog_reset_password, null);
		Button continueBtn = (Button) view.findViewById(R.id.btn_reg_continue);
		Label titleView = (Label)view.findViewById(R.id.tv_reg_header_dialog_title);
		Label contentView = (Label)view.findViewById(R.id.tv_reg_dialog_content);
		titleView.setText(title);
		contentView.setText(content);
		continueBtn.setOnClickListener(continueBtnClickListener);
		alertDialogBuilder.setView(view);
		alertDialogBuilder.show();
	}

	public static void showDialog(String title, String content,String content2, String buttonText, Activity activity, View.OnClickListener continueBtnClickListener) {
		dismissDialog();
		alertDialogBuilder = new AlertDialog.Builder(activity).create();
		alertDialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialogBuilder.setCancelable(false);
		LayoutInflater layoutInflater = activity.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.reg_dialog_reset_password, null);
		Button continueBtn = (Button) view.findViewById(R.id.btn_reg_continue);
		Label titleView = (Label)view.findViewById(R.id.tv_reg_header_dialog_title);
		Label contentView = (Label)view.findViewById(R.id.tv_reg_dialog_content);
		Label contentView2 = (Label)view.findViewById(R.id.tv_reg_dialog_content_2);
		titleView.setText(title);
		contentView.setText(content);
		if(content2!=null && content2.length()>0){
			contentView2.setVisibility(View.VISIBLE);
			contentView2.setText(content2);
		}
		continueBtn.setText(buttonText);
		continueBtn.setOnClickListener(continueBtnClickListener);
		alertDialogBuilder.setView(view);
		alertDialogBuilder.show();
	}
	public static void dismissDialog(){
		if(alertDialogBuilder!=null){
			alertDialogBuilder.dismiss();
		}
	}
}

