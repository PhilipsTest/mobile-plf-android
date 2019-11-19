package com.philips.cdp.prodreg.model.registerproduct;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable {

	@SerializedName("attributes")
	private Attributes attributes;

	@SerializedName("id")
	private String id;

	@SerializedName("type")
	private String type;

	public void setAttributes(Attributes attributes){
		this.attributes = attributes;
	}

	public Attributes getAttributes(){
		return attributes;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
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
			",id = '" + id + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}


}