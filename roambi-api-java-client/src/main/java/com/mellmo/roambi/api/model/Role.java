/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;

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

	@SuppressWarnings("unchecked")
	private transient static final Map rolesMap = Collections.unmodifiableMap(Collections.synchronizedMap(new CaseInsensitiveMap() {{
		put(VIEWER.getLabel(), VIEWER.getUid());
		put(PUBLISHER.getLabel(), PUBLISHER.getUid());
		put(ADMINISTRATOR.getLabel(), ADMINISTRATOR.getUid());
	}}));

	final private String uid;
	final private String label;
	
	public Role(final String uid) {
		this.uid = uid;
		this.label = uid;
	}
	
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
		return json != null ? new Role(JsonUtils.getString(json, UID), JsonUtils.getString(json, LABEL)) : null;
	}
	
	public static String getRoleUid(final String label) {
		return rolesMap.containsKey(label) ? (String) rolesMap.get(label) : StringUtils.defaultString(label);
	}
	
	@Override
	public boolean equals(final Object role) {
		return role instanceof Role && this.uid.equals(((Role)role).getUid()) && this.label.equals(((Role)role).getLabel());
	}
	
	public JsonObject toJSON() {
		final JsonObject roleJson = new JsonObject();
		roleJson.addProperty(UID, this.uid);
		roleJson.addProperty(LABEL, this.label);
		return roleJson;
	}
}
