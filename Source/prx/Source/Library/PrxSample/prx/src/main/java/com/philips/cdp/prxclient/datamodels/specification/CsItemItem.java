package com.philips.cdp.prxclient.datamodels.specification;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CsItemItem implements Serializable {

	private static final long serialVersionUID = -6883419441341473202L;
	@SerializedName("csItemRank")
	@Expose
	public String csItemRank;

	@SerializedName("unitOfMeasure")
	@Expose
	public UnitOfMeasure unitOfMeasure;

	@SerializedName("csItemName")
	@Expose
	public String csItemName;

	@SerializedName("csItemCode")
	@Expose
	public String csItemCode;

	@SerializedName("csItemIsFreeFormat")
	@Expose
	public String csItemIsFreeFormat;

	@SerializedName("csValue")
	@Expose
	public List<CsValueItem> csValue;
}