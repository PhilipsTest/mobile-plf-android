package com.ecs.demotestuapp.jsonmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class JSONConfiguration implements Serializable {

	@SerializedName("occ")
	private List<GroupItem> occ;

	public List<GroupItem> getPil() {
		return pil;
	}

	public void setPil(List<GroupItem> pil) {
		this.pil = pil;
	}

	private List<GroupItem> pil;

	public void setOcc(List<GroupItem> occ){
		this.occ = occ;
	}

	public List<GroupItem> getOcc(){
		return occ;
	}
}