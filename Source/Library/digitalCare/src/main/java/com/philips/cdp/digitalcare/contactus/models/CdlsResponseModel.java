package com.philips.cdp.digitalcare.contactus.models;

import com.philips.cdp.digitalcare.contactus.models.CdlsEmailModel;

/**
 *	CdlsBean is bean class for all CDLS related objects.
 * 
 * @author : Ritesh.jha@philips.com
 * 
 * @since: 16 Dec 2014
 * Copyright (c) 2016 Philips. All rights reserved.
 */

public class CdlsResponseModel {
	private Boolean mSuccess = null;
	private CdlsEmailModel mEmail = null;
	private CdlsChatModel mChat = null;
	private CdlsPhoneModel mPhone = null;
	private CdlsErrorModel mError = null;

	public CdlsResponseModel(boolean success, CdlsPhoneModel phone, CdlsChatModel chat,
			CdlsEmailModel email, CdlsErrorModel error) {
		mSuccess = success;
		mPhone = phone;
		mChat = chat;
		mEmail = email;
		mError = error;
	}

	public boolean getSuccess() {
		return mSuccess;
	}

	public CdlsErrorModel getError() {
		return mError;
	}

	public CdlsEmailModel getEmail() {
		return mEmail;
	}

	public CdlsChatModel getChat() {
		return mChat;
	}

	public CdlsPhoneModel getPhone() {
		return mPhone;
	}
}
