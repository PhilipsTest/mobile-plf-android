package com.philips.cdp.prodreg.model_request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable {

	@SerializedName("attributes")
	private Attributes attributes;

	@SerializedName("type")
	private String type;

	public void setAttributes(Attributes attributes){
		this.attributes = attributes;
	}

	public Attributes getAttributes(){
		return attributes;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"attributes = '" + attributes + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}