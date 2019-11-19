package com.philips.cdp.prodreg.model.registeredproducts.model;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Promotions implements Serializable {

	@SerializedName("postponed")
	private List<Object> postponed;

	public void setPostponed(List<Object> postponed){
		this.postponed = postponed;
	}

	public List<Object> getPostponed(){
		return postponed;
	}

	@Override
 	public String toString(){
		return 
			"Promotions{" + 
			"postponed = '" + postponed + '\'' + 
			"}";
		}
}