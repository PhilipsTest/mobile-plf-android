package com.philips.cdp.prxclient.datamodels.features;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeatureHighlightItem implements Serializable {

	private static final long serialVersionUID = 4150127431517255657L;
	@SerializedName("featureCode")
	public String featureCode;

	@SerializedName("featureHighlightRank")
	public String featureHighlightRank;

	@SerializedName("featureReferenceName")
	public String featureReferenceName;
}