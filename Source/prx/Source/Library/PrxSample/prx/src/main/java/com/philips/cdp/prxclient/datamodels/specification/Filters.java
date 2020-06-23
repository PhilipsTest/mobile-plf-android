package com.philips.cdp.prxclient.datamodels.specification;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Filters implements Serializable {

	private static final long serialVersionUID = 2540428616162907708L;
	@SerializedName("purpose")
	@Expose
	public List<PurposeItem> purpose;
}