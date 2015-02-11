/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class Portal {

	private String uid = null;
	private String title = null;

	public Portal() {
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    @Deprecated
	public static List<Portal> fromApiListResponse(String json) {
		JsonObject props = ResponseUtils.responseToObject(json);
        return fromJsonObject(props);
	}

    public static List<Portal> fromApiListResponse(InputStream stream) {
        JsonObject props = ResponseUtils.responseToObject(stream);
        return fromJsonObject(props);
    }

    private static List<Portal> fromJsonObject(JsonObject props) {
        List<Portal> portals = new ArrayList<Portal>();

        JsonArray array = props.getAsJsonArray("portals");
        for (int i=0; i<array.size(); i++) {
            JsonObject portalProps = array.get(i).getAsJsonObject();

            Portal p = new Portal();
            p.title = portalProps.get("title").getAsString();
            p.uid = portalProps.get("uid").getAsString();

            portals.add(p);
        }
        return portals;
    }

    public static Portal fromApiDetailsResponse(String json) {
		JsonObject props = ResponseUtils.responseToObject(json);

		Portal portal = new Portal();
		portal.title = props.get("title").getAsString();
		portal.uid = props.get("uid").getAsString();
		
		return portal;
	}
}
