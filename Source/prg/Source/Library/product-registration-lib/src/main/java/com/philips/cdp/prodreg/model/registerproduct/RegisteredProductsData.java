package com.philips.cdp.prodreg.model.registerproduct;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.philips.cdp.prodreg.model.registeredproducts.model.ProductRegistrationsItem;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONObject;

import java.util.List;

public class RegisteredProductsData extends ResponseData {

	@SerializedName("productRegistrations")
	private List<ProductRegistrationsItem> productRegistrations;

	public void setProductRegistrations(List<ProductRegistrationsItem> productRegistrations){
		this.productRegistrations = productRegistrations;
	}

	public List<ProductRegistrationsItem> getProductRegistrations(){
		return productRegistrations;
	}

//	@Override
//	public String toString(){
//		return
//				"Tnin{" +
//						"productRegistrations = '" + productRegistrations + '\'' +
//						"}";
//	}

	public ResponseData parseJsonResponseData(JSONObject response) {

		RegisteredProductsData registrationResponse;
		registrationResponse = new Gson().fromJson(response.toString(), RegisteredProductsData.class);
		return registrationResponse;
	}

}