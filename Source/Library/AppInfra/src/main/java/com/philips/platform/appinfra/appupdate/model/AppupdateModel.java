package com.philips.platform.appinfra.appupdate.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AppupdateModel {

	@SerializedName("version")
	@Expose
	private Version version;
	@SerializedName("messages")
	@Expose
	private Messages messages;
	@SerializedName("requirements")
	@Expose
	private Requirements requirements;

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public Requirements getRequirements() {
		return requirements;
	}

	public void setRequirements(Requirements requirements) {
		this.requirements = requirements;
	}

}
