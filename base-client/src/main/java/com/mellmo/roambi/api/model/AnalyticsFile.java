/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author charles
 *
 */
public class AnalyticsFile extends ContentItem {

	protected static final String LAUNCHPAD_URL = "launchpad_url";
	protected static final String SHARE_URL = "share_url";
	protected static final String VERSION_UID = "version_uid";
	protected static final String VIEW_TYPE = "view_type";
	private Set<Entry<String, JsonElement>> launchpadUrl = null;
	private Map<String, String> launchpadUrls = null;
	private String shareUrl;
	private String versionUid;
	private String viewType;
	
	protected AnalyticsFile(final JsonObject jsonObject) {
		super(ANALYTICS, jsonObject);
		if (jsonObject.has(LAUNCHPAD_URL)) {
			this.launchpadUrl = jsonObject.get(LAUNCHPAD_URL).getAsJsonObject().entrySet();
		}
		if (jsonObject.has(SHARE_URL)) {
			this.shareUrl = jsonObject.get(SHARE_URL).getAsString();
		}
		if (jsonObject.has(VERSION_UID)) {
			this.versionUid = jsonObject.get(VERSION_UID).getAsString();
		}
		if (jsonObject.has(VIEW_TYPE)) {
			this.viewType = jsonObject.get(VIEW_TYPE).getAsString();
		}
	}
	
	public String getLaunchpadUrl(final String client) {
		return getLaunchpadUrls().get(client);
	}
	public Map<String, String> getLaunchpadUrls() {
		if (launchpadUrls == null) {
			launchpadUrls = new HashMap<String, String>();
			if (this.launchpadUrl != null) {
				for (Entry<String, JsonElement> entry:launchpadUrl) {
					launchpadUrls.put(entry.getKey(), entry.getValue().getAsString());
				}
			}
		}
		return launchpadUrls;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public String getVersionUid() {
		return versionUid;
	}
	
	public String getViewType() {
		return viewType;
	}
	
	@Override
	public boolean isFolder() {
		return false;
	}
	
	@Override
	public void setType(final String type) {
		throw new UnsupportedOperationException("Method not supported");
    }

}