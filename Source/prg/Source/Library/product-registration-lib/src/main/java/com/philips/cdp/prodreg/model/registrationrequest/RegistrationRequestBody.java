package com.philips.cdp.prodreg.model.registrationrequest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegistrationRequestBody implements Serializable {

	private static final long serialVersionUID = 8755071722985419088L;
	@SerializedName("data")
	private Data data;

	@SerializedName("meta")
	private Meta meta;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setMeta(Meta meta){
		this.meta = meta;
	}

	public Meta getMeta(){
		return meta;
	}

	@Override
 	public String toString(){
		return 
			"RegistrationRequestBody{" + 
			"data = '" + data + '\'' + 
			",meta = '" + meta + '\'' + 
			"}";
		}
}