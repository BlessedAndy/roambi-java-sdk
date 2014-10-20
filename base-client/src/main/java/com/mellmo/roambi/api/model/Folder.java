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
public class Folder extends ContentItem {
	
	protected static final String SYNC = "sync";
	private boolean synced = false;
	
	protected Folder(final JsonObject jsonObject) {
		super(FOLDER, jsonObject);
		if (jsonObject.has(SYNC)) {
			this.synced = jsonObject.get(SYNC).getAsBoolean();
		}
	}

	public boolean isSynced() {
		return synced;
	}
	
	@Override
	public boolean isFolder() {
		return true;
	}
	
	@Override
	public void setType(final String type) {
		throw new UnsupportedOperationException("Method not supported");
    }
	
	@Override
	public void setSize(final long size) {
		throw new UnsupportedOperationException("Method not supported");
	}
}
