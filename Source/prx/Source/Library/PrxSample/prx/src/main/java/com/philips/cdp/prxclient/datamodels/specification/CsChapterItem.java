package com.philips.cdp.prxclient.datamodels.specification;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CsChapterItem implements Serializable {

	private static final long serialVersionUID = 5009770779919717578L;
	@SerializedName("csChapterCode")
	@Expose
	public String csChapterCode;

	@SerializedName("csItem")
	@Expose
	public List<CsItemItem> csItem;

	@SerializedName("csChapterName")
	@Expose
	public String csChapterName;

	@SerializedName("csChapterRank")
	@Expose
	public String csChapterRank;
}