package com.philips.cdp.prxclient.datamodels.features;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeatureItem implements Serializable {

	private static final long serialVersionUID = 6716648931315785132L;
	@SerializedName("featureLongDescription")
	public String featureLongDescription;

	@SerializedName("featureCode")
	public String featureCode;

	@SerializedName("featureRank")
	public String featureRank;

	@SerializedName("featureName")
	public String featureName;

	@SerializedName("featureGlossary")
	public String featureGlossary;

	@SerializedName("featureReferenceName")
	public String featureReferenceName;

	@SerializedName("featureTopRank")
	public String featureTopRank;

	public String getSingleFeatureImage() {
		return singleFeatureImage;
	}

	public void setSingleFeatureImage(String singleFeatureImage) {
		this.singleFeatureImage = singleFeatureImage;
	}

	public String singleFeatureImage;
}