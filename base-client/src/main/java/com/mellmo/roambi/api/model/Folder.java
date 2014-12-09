/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;


import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.UidUtils;

/**
 * 
 * @author charles
 *
 */
public class Folder extends ContentItem {
	
	public static final String MY_DOCUMENTS = "My Documents";
	public static final String SYNC = "sync";
	private boolean synced = false;
	
	protected Folder(final JsonObject jsonObject) {
		super(FOLDER, jsonObject);
		if (jsonObject.has(SYNC)) {
			this.synced = jsonObject.get(SYNC).getAsBoolean();
		}
	}
	
	public static Folder getFolder(final JsonObject json) {
		return isPersonalFolder(json) ? new PersonalFolder(json) : new Folder(json); 
	}

	public boolean isSynced() {
		return synced;
	}
	
	@Override
	public boolean isFolder() {
		return true;
	}
	
	public boolean isPersonalFolder() {
		return false;
	}
	
	@Override
	public void setType(final String type) {
		throw new UnsupportedOperationException("Method not supported");
    }
	
	@Override
	public void setSize(final long size) {
		throw new UnsupportedOperationException("Method not supported");
	}
	
	private static boolean isPersonalFolder(final JsonObject json) {
		return json.has(User.USER) || isCurrentUserPersonalFolder(json);
	}
	
	private static boolean isCurrentUserPersonalFolder(final JsonObject json) {
		return StringUtils.equals(MY_DOCUMENTS, json.get(TITLE).getAsString()) && UidUtils.isUuid(json.get(UID).getAsString());
	}
}
