package com.mellmo.roambi.api.model;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.JsonUtils;

/**
 * 
 * @author charles
 *
 */
public class Role implements IBaseModel {
	
	public static final String LABEL = "label";
	public static final Role VIEWER = new Role("standard", "Viewer");
	public static final Role PUBLISHER = new Role("publisher", "Publisher");
	public static final Role ADMINISTRATOR = new Role("admin", "Administrator");

	final private String uid;
	final private String label;
	
	public Role(final String uid, final String label) {
		this.uid = uid;
		this.label = label;
	}
	
	public String getUid() {
		return uid;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static Role getRole(final JsonObject json) {
		return json != null ? new Role(JsonUtils.getString(json, "uid"), JsonUtils.getString(json, LABEL)) : null;
	}
	
	@Override
	public boolean equals(final Object role) {
		return role instanceof Role && this.uid.equals(((Role)role).getUid()) && this.label.equals(((Role)role).getLabel());
	}
}
