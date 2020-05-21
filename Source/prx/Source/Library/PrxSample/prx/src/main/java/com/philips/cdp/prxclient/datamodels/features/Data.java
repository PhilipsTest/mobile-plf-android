package com.philips.cdp.prxclient.datamodels.features;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data implements Serializable {

	private static final long serialVersionUID = 6960659089260274640L;
	public static List videoExtensionList;
	public static String[] videoExtensions = {"WEBM","MPG","MP2","MPEG","MPE","MPV","OGG","MP4","M4P","M4V","AVI","WMV","MOV","QT","FLV","SWF","AVCHD"};

	static {
		videoExtensionList = Arrays.asList(videoExtensions);
	}

	@SerializedName("keyBenefitArea")
	public List<KeyBenefitAreaItem> keyBenefitArea;

	@SerializedName("code")
	public List<CodeItem> code;

	@SerializedName("featureHighlight")
	public List<FeatureHighlightItem> featureHighlight;

	public String getSingleAssetImageFromFeatureCode(String featureCode){
        if(code == null) return null;

		for (CodeItem codeItem:code) {
			if(codeItem.code.equalsIgnoreCase(featureCode)) {
				if (isImage(codeItem.extension)) {
					return codeItem.asset;
				}
			}
		}
		return null;
	}

	private boolean isImage(String extension) {
		return !videoExtensionList.contains(extension);
	}
}