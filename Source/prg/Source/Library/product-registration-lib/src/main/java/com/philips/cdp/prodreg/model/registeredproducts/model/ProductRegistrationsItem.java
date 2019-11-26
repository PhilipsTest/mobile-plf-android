package com.philips.cdp.prodreg.model.registeredproducts.model;

import com.google.gson.annotations.SerializedName;
import com.philips.cdp.prodreg.model.registerproduct.Data;

import java.io.Serializable;

public class ProductRegistrationsItem implements Serializable {

	@SerializedName("data")
	private com.philips.cdp.prodreg.model.registerproduct.Data data;

	public void setData(com.philips.cdp.prodreg.model.registerproduct.Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"ProductRegistrationsItem{" + 
			"data = '" + data + '\'' + 
			"}";
		}
}