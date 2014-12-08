/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class ContentItem implements IBaseModel {
	
	protected static final String UPDATED_AT = "updated_at";
	protected static final String FILE_TYPE = "file_type";
	public static final String ANALYTICS = "ANALYTICS";
	protected static final String FOLDER = "FOLDER";
	protected static final Logger LOG = Logger.getLogger(ContentItem.class);
	private String uid;
	private String name;
    private Permissions permissions;
    private boolean readOnly;
    private long size;
    private String type;
    private Date updatedDate;

    public ContentItem() {
	}
    
    public ContentItem(final String uid) {
        this.uid = uid;
        this.name = null;
    }

    public ContentItem(String uid, String name) {
        setUid(uid);
        setName(name);
    }
    
	protected ContentItem(final JsonObject jsonObject) {
		this(jsonObject.get(FILE_TYPE).getAsString(), jsonObject);
	}
	
	protected ContentItem(final String type, final JsonObject jsonObject) {
		this.name = jsonObject.get(TITLE).getAsString();
		this.uid = jsonObject.get("uid").getAsString();
		if (jsonObject.get("permissions") != null) {
			final JsonObject obj = jsonObject.get("permissions").getAsJsonObject();
			this.permissions = new Permissions(obj);
		}
		this.type = type;
		final JsonElement element = jsonObject.get("file_size");
		if (element != null) {
			this.size = element.getAsLong();
		}
		this.readOnly = jsonObject.get("read_only").getAsBoolean();
		if (jsonObject.has(UPDATED_AT)) {
			try {
				this.updatedDate = getDateValue(jsonObject.get(UPDATED_AT).getAsString());
			} catch (RuntimeException e) {
				LOG.error(e.getMessage());
			}
		}
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public static List<ContentItem> fromApiListResponse(String json) {
		List<ContentItem> contents = new ArrayList<ContentItem>();
		JsonObject props = ResponseUtils.responseToObject(json);
		JsonArray array = props.getAsJsonArray("portal_contents");
		for (int i=0; i<array.size(); i++) {
			contents.add(fromApiListItem(array.get(i).getAsJsonObject()));
		}
		return contents;
	}
	
	public static ContentItem fromApiFolderDetailsResponse(final String json) {
		final JsonObject props = ResponseUtils.responseToObject(json);
		return fromApiListItem(props.get("folder").getAsJsonObject());
	}
	
	public static ContentItem fromApiFileDetailsResponse(String json) {
		final JsonObject props = ResponseUtils.responseToObject(json);
		return fromApiListItem(props.get("file").getAsJsonObject());
	}
	
	public static ContentItem fromApiItemDetailsResponse(final String json) {
		final JsonObject props = ResponseUtils.responseToObject(json);
		final String memberName = props.has("file") ? "file" : "folder";
		return fromApiListItem(props.get(memberName).getAsJsonObject());
	}
	
	public static ContentItem fromApiListItem(JsonObject jsonObject) {
		final String type = jsonObject.get(FILE_TYPE).getAsString();
		if (ANALYTICS.equals(type))		return new AnalyticsFile(jsonObject);
		else if (FOLDER.equals(type))	return Folder.getFolder(jsonObject);
		else 							return new ContentItem(type, jsonObject);
	}
	
	private static Date getDateValue(final String timestamp) {
		// My Document folder's timestamp looks like this "2013-05-09T22:25:26+00:00", 
		// others timestamp looks like "2013-10-18T21:15:17Z", both in UTC time
		final Calendar cal = DatatypeConverter.parseDateTime(timestamp);
		return cal.getTime();
	}

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
    
    public Date getUpdatedDate() {
    	return this.updatedDate;
	}
	
	public boolean isFolder() {
		return FOLDER.equals(this.getType());
	}
}
