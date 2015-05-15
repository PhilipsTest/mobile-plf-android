package com.philips.cl.di.dev.pa.ews;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.philips.cl.di.dev.pa.R;
import com.philips.cl.di.dev.pa.datamodel.SessionDto;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dev.pa.util.Fonts;
import com.philips.cl.di.dev.pa.util.MetricsTracker;
import com.philips.cl.di.dev.pa.util.TrackPageConstants;
import com.philips.cl.di.dev.pa.util.UnicodeSpecialCharacter;
import com.philips.cl.di.dev.pa.view.FontButton;
import com.philips.cl.di.dev.pa.view.FontTextView;

public class EWSStepFourFragment extends EWSBaseFragment {

	public static final String EXTRA_PASSWORD = "password";
	public static final String EXTRA_ADV_SETTING = "adv_setting";

	private FontTextView passwordLabelStep3, wifiNetworkAddStep3;
	private EditText passwordStep3, deviceNameStep3, ipAddStep3,
			subnetMaskStep3, routerAddStep3;
	private ImageView showPasswordImgStep3, showAdvanceConfigImg,
			hideAdvanceConfigImg;
	private FontButton editSavePlaceNameBtnStep3, nextBtn;
	private RelativeLayout advSettingLayoutStep3;
	private LinearLayout advSettingBtnLayoutStep3;
	private boolean isPasswordVisibelStep3 = true;
	private OnFocusChangeListener focusListener;
	private ButtonClickListener buttonClickListener;
	private String ssid;
	private String password = "";
	private boolean advSetting = false;
	private ArrayList<Integer> unicodes;
	private ImageView separtorUp;
	private ImageView separtorDown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ews_step4, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MetricsTracker.trackPage(TrackPageConstants.EWS_CONNECT_TO_HOME_WIFI);
		
		ViewGroup scrollView = (ScrollView) getView().findViewById(R.id.scrollView);
		setBackground(scrollView, R.drawable.ews_nav_bar_2x, Color.BLACK, .1F);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			password = bundle.getString(EXTRA_PASSWORD);
			advSetting = bundle.getBoolean(EXTRA_ADV_SETTING, false);
		}
		ALog.i(ALog.EWS, "EWSStepThreeFragment bundle: " + bundle
				+ ", password: " + password + ", advSetting: " + advSetting);

		unicodes = UnicodeSpecialCharacter.getSpecialCharaterUnicodes();

		((EWSActivity) getActivity()).setActionBarHeading(EWSConstant.EWS_STEP_THREE);

		focusListener = new EditTextFocusChangeListener();
		buttonClickListener = new ButtonClickListener();
		initializeXmlVariable();
		initializeListener();
		enablePasswordFild();
		setPurifierDetils();

		deviceNameStep3.setFilters(new InputFilter[] {
				purifierNamefilter,
				new InputFilter.LengthFilter(
						EWSConstant.EWS_PURIFIER_NAME_LENGTH) });
		passwordStep3.setFilters(new InputFilter[] { passwordFilter });
	}

	private void setPurifierDetils() {
		ssid = ((EWSActivity) getActivity()).getNetworkSSID();
		String passwordLabel = getString(R.string.step3_msg1)
				+ " <b>" + ssid + "</b>";
		passwordLabelStep3.setText(Html.fromHtml(passwordLabel));
		if (SessionDto.getInstance().getDeviceDto() != null) {
			String name = SessionDto.getInstance().getDeviceDto().getName();
			if (name.equals(EWSConstant.EWS_PURIFIER_DEFAULT_NAME)) {
				name = getString(R.string.default_purifier_name);
				((EWSActivity) getActivity()).sendDeviceNameToPurifier(name);
			}
			ALog.i(ALog.EWS, "Name: " + name);
			deviceNameStep3.setText(name);
		}

		if (SessionDto.getInstance().getDeviceWifiDto() != null) {
			ipAddStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getIpaddress());
			subnetMaskStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getNetmask());
			routerAddStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getGateway());
			wifiNetworkAddStep3.setText(SessionDto.getInstance().getDeviceWifiDto().getMacaddress());
		}

		if (advSetting) {
			advSettingLayoutStep3.setVisibility(View.VISIBLE);
			advSettingBtnLayoutStep3.setVisibility(View.GONE);
		}
	}

	private void enablePasswordFild() {
		if (((EWSActivity) getActivity()).getEWSServiceObject() == null) {
			return;
		}
		if (((EWSActivity) getActivity()).getEWSServiceObject()
				.isNoPasswordSSID()) {
			passwordStep3.setEnabled(false);
			passwordStep3.setBackgroundResource(R.drawable.ews_edit_txt_2_bg_gray);
		} else {
			passwordStep3.setEnabled(true);
			passwordStep3.setBackgroundResource(R.drawable.ews_edit_txt_2_bg);
			passwordStep3.setText(password);
		}

	}

	private void initializeXmlVariable() {
		passwordLabelStep3 = (FontTextView) getView().findViewById(
				R.id.ews_step3_password_lb);
		wifiNetworkAddStep3 = (FontTextView) getView().findViewById(
				R.id.ews_step3_wifi_add);

		passwordStep3 = (EditText) getView().findViewById(R.id.ews_step3_password);
		passwordStep3.setTypeface(Fonts.getCentraleSansLight(getActivity()));
		passwordStep3.setOnFocusChangeListener(focusListener);
		deviceNameStep3 = (EditText) getView().findViewById(R.id.ews_step3_place_name_edittxt);
		deviceNameStep3.setTypeface(Fonts.getCentraleSansLight(getActivity()));
		deviceNameStep3.setOnFocusChangeListener(focusListener);
		ipAddStep3 = (EditText) getView().findViewById(R.id.ews_step3_ip_edittxt);
		ipAddStep3.setTypeface(Fonts.getCentraleSansLight(getActivity()));
		ipAddStep3.setOnFocusChangeListener(focusListener);
		subnetMaskStep3 = (EditText) getView().findViewById(R.id.ews_step3_subnet_edittxt);
		subnetMaskStep3.setTypeface(Fonts.getCentraleSansLight(getActivity()));
		subnetMaskStep3.setOnFocusChangeListener(focusListener);
		routerAddStep3 = (EditText) getView().findViewById(R.id.ews_step3_router_edittxt);
		routerAddStep3.setTypeface(Fonts.getCentraleSansLight(getActivity()));
		routerAddStep3.setOnFocusChangeListener(focusListener);

		showPasswordImgStep3 = (ImageView) getView().findViewById(R.id.ews_password_enable_img);
		showAdvanceConfigImg = (ImageView) getView().findViewById(R.id.ews_adv_config_img);
		hideAdvanceConfigImg = (ImageView) getView().findViewById(R.id.ews_hide_adv_setting);

		nextBtn = (FontButton) getView().findViewById(R.id.ews_step3_next_btn);
		editSavePlaceNameBtnStep3 = (FontButton) getView().findViewById(R.id.ews_step3_edit_name_btn);

		advSettingLayoutStep3 = (RelativeLayout) getView().findViewById(R.id.ews_step3_adv_config_layout);
		advSettingBtnLayoutStep3 = (LinearLayout) getView().findViewById(R.id.ews_adv_config_layout);
		
		separtorUp = (ImageView) getView().findViewById(R.id.place_name_separator_up);
		separtorDown = (ImageView) getView().findViewById(R.id.place_name_separator_down);
	}

	
	private void initializeListener() {
		passwordStep3.setOnFocusChangeListener(focusListener);
		deviceNameStep3.setOnFocusChangeListener(focusListener);
		ipAddStep3.setOnFocusChangeListener(focusListener);
		subnetMaskStep3.setOnFocusChangeListener(focusListener);
		routerAddStep3.setOnFocusChangeListener(focusListener);
		showAdvanceConfigImg.setOnClickListener(buttonClickListener);
		hideAdvanceConfigImg.setOnClickListener(buttonClickListener);
		nextBtn.setOnClickListener(buttonClickListener);
		showPasswordImgStep3.setOnClickListener(buttonClickListener);
		editSavePlaceNameBtnStep3.setOnClickListener(buttonClickListener);
	}

	private InputFilter purifierNamefilter = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			if (source.equals(" ")) {
				return source;
			}
			if (source.toString().length() >= 0) {
				for (char ch : source.toString().toCharArray()) {
					if (unicodes.contains((int) ch)) {
						return source.subSequence(0, 0);
					}
				}
				return source;
			} else {
				return source;
			}
		}
	};

	InputFilter passwordFilter = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) {
				int passwordInt = ((int) source.charAt(i));

				if (passwordInt > 126 || passwordInt < 32) {
					ALog.i(ALog.EWS, "Password char int: " + passwordInt);
					return "";
				}
			}
			return source;
		}
	};

	private class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ews_password_enable_img:
				passwordFieldEnableClickEvent();
				break;
			case R.id.ews_adv_config_img:
				advSettingLayoutStep3.setVisibility(View.VISIBLE);
				advSettingBtnLayoutStep3.setVisibility(View.GONE);
				break;
			case R.id.ews_hide_adv_setting:
				advSettingLayoutStep3.setVisibility(View.GONE);
				advSettingBtnLayoutStep3.setVisibility(View.VISIBLE);
				break;
			case R.id.ews_step3_edit_name_btn:
				editPurifierNameClickEvent();
				break;
			case R.id.ews_step3_next_btn:
				if (ssid == null || ssid.isEmpty())	return;
				nextButtonClick();
				break;
			default:
				ALog.i(ALog.EWS, "Default...");
				break;
			}
		}
	}
	
	private void nextButtonClick() {
		ALog.i(ALog.EWS, "step3 next button click");
		String ipAdd = "";
		if (ipAddStep3.getText() != null) ipAdd = ipAddStep3.getText().toString();
		String subnetMask = "";
		if (subnetMaskStep3.getText() != null) subnetMask = subnetMaskStep3.getText().toString();
		String gateWay = "";
		if (routerAddStep3.getText() != null) gateWay = routerAddStep3.getText().toString();
		
		((EWSActivity) getActivity()).sendNetworkDetails(ssid, passwordStep3.getText().toString(), ipAdd, subnetMask, gateWay);
		((EWSActivity) getActivity()).setAdvSettingViewVisibility(advSettingLayoutStep3.isShown());
	}

	private void passwordFieldEnableClickEvent() {
		if (isPasswordVisibelStep3) {
			isPasswordVisibelStep3 = false;
			showPasswordImgStep3.setImageResource(R.drawable.password_normal);
			passwordStep3.setTransformationMethod(new PasswordTransformationMethod());
			Editable editable = passwordStep3.getText();
			Selection.setSelection(editable, passwordStep3.length());
		} else {
			isPasswordVisibelStep3 = true;
			showPasswordImgStep3.setImageResource(R.drawable.password_visible);
			passwordStep3.setTransformationMethod(null);
			Editable editable = passwordStep3.getText();
			Selection.setSelection(editable, passwordStep3.length());
		}
	}

	private void editPurifierNameClickEvent() {
		if (editSavePlaceNameBtnStep3.getText().toString()
				.equals(getResources().getString(R.string.edit))) {
			deviceNameStep3.setBackgroundResource(R.drawable.ews_edit_txt_2_bg);
			deviceNameStep3.setEnabled(true);
			Editable editable = deviceNameStep3.getText();
			Selection.setSelection(editable, deviceNameStep3.length());
			editSavePlaceNameBtnStep3.setText(getResources().getString(	R.string.save));
			separtorUp.setVisibility(View.INVISIBLE);
			separtorDown.setVisibility(View.INVISIBLE);
		} else {
			ALog.i(ALog.EWS, "step3 save name button click");
			deviceNameStep3.setBackgroundColor(Color.WHITE);
			deviceNameStep3.setEnabled(false);
			editSavePlaceNameBtnStep3.setText(getResources().getString(R.string.edit));
			separtorUp.setVisibility(View.VISIBLE);
			separtorDown.setVisibility(View.VISIBLE);
			String purifierName = deviceNameStep3.getText().toString();
			ALog.i(ALog.EWS, "Edit text value: " + purifierName);
			if (purifierName != null && purifierName.trim().length() > 0) {
				((EWSActivity) getActivity()).sendDeviceNameToPurifier(purifierName.trim());
			} else {
				if (SessionDto.getInstance().getDeviceDto() == null) {
					return;
				}
				deviceNameStep3.setText(SessionDto.getInstance().getDeviceDto().getName());
			}
		}
	}

	private class EditTextFocusChangeListener implements OnFocusChangeListener {

		public void onFocusChange(View v, boolean hasFocus) {

			if (!hasFocus) {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}
	}
}
