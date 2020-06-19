package com.philips.cdp.prxclient.datamodels.specification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UnitOfMeasure implements Serializable {

	private static final long serialVersionUID = 8843113320809077214L;
	@SerializedName("unitOfMeasureSymbol")
	@Expose
	public String unitOfMeasureSymbol;

	@SerializedName("unitOfMeasureName")
	@Expose
	public String unitOfMeasureName;

	@SerializedName("unitOfMeasureCode")
	@Expose
	public String unitOfMeasureCode;
}