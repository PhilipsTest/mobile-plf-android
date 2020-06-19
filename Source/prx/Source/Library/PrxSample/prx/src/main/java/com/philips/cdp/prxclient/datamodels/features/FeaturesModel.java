package com.philips.cdp.prxclient.datamodels.features;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.io.Serializable;

public class FeaturesModel extends ResponseData implements Serializable {

	private static final long serialVersionUID = 5897313023261737994L;
	@SerializedName("data")
	@Expose
	public Data data;

	@SerializedName("success")
	@Expose
	public boolean success;

	@Override
	public ResponseData parseJsonResponseData(JSONObject response) {
		if (response != null) {
			return new Gson().fromJson(response.toString(), FeaturesModel.class);
		}
		return null;
	}
}