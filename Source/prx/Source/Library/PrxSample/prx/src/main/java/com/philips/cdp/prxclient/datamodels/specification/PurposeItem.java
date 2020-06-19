package com.philips.cdp.prxclient.datamodels.specification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PurposeItem implements Serializable {

	private static final long serialVersionUID = 7461674031962600708L;
	@SerializedName("features")
	@Expose
	public Features features;

	@SerializedName("csItems")
	@Expose
	public CsItems csItems;

	@SerializedName("type")
	@Expose
	public String type;
}