/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import com.google.gson.JsonObject;

/**
 * 
 * @author charles
 *
 */
public class PersonalFolder extends Folder {
	
	private final User user; 
	
	protected PersonalFolder(final JsonObject json) {
		super(json);
		this.user = json.has(User.USER) ? User.getUser(json.getAsJsonObject(User.USER)) : null;
	}

	public User getUser() {
		return user;
	}
	
	public boolean isPersonalFolder() {
		return true;
	}
}
