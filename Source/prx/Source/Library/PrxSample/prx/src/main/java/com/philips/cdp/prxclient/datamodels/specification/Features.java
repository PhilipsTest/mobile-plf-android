package com.philips.cdp.prxclient.datamodels.specification;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Features implements Serializable {

	private static final long serialVersionUID = 4714472613554904424L;
	@SerializedName("feature")
	@Expose
	public List<FeatureItem> feature;
}