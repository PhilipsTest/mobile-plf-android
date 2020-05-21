package com.philips.cdp.prxclient.datamodels.specification;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.io.Serializable;

public class SpecificationModel extends ResponseData implements Serializable {

	private static final long serialVersionUID = -4223759915273396007L;
	@SerializedName("data")
	@Expose
	public Data data;

	@SerializedName("success")
	@Expose
	public boolean success;

	@Override
	public ResponseData parseJsonResponseData(JSONObject response) {
		JSONObject specificationResponse = response;
		if (specificationResponse != null) {
			return new Gson().fromJson(specificationResponse.toString(), SpecificationModel.class);

		}
		return null;
	}
}