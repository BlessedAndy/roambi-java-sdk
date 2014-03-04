/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.JsonUtils;

/**
 * 
 * @author charles
 *
 */
public class UserAccount {
	
	public enum Status {
		INVITED		("invited"),
		REQUESTED	("requested"),
		MEMBER		("member");
		
		private final String status;
		Status(final String status) {
			this.status = status;
		}
		
		public String getStatus() {
			return this.status;
		}
	}
	
	private Role role;
	private String status;
	private boolean enabled = true;
	public static final String ENABLED = "enabled";
	public static final String ROLE = "role";
	public static final String STATUS = "status"; 

	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Status getStatus() {
		return Status.valueOf(this.status.toUpperCase());
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public static UserAccount getUserAccount(final JsonObject json) {
		if (json != null) {
			final UserAccount userAccount = new UserAccount();
			userAccount.status = JsonUtils.getString(json, STATUS);
			if (JsonUtils.hasBoolean(json, ENABLED)) {
				userAccount.enabled = json.get(ENABLED).getAsBoolean();
			}
			userAccount.role = Role.getRole(JsonUtils.getJson(json, ROLE));
			return userAccount;
		}
		return null;
	}
}
