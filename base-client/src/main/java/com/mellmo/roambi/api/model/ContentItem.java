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
	
	public static ContentItem fromApiDetailsResponse(String json) {
		JsonObject props = ResponseUtils.responseToObject(json);
		return fromApiListItem(props.get("file").getAsJsonObject());
	}

	public static ContentItem fromApiListItem(JsonObject jsonObject) {
		ContentItem item = new ContentItem();
		item.setName(jsonObject.get("title").getAsString());
		item.setUid(jsonObject.get("uid").getAsString());
        if(jsonObject.get("permissions")!=null) {
            JsonObject obj = jsonObject.get("permissions").getAsJsonObject();
            item.setPermissions(new Permissions(obj));
        }
        item.setType(jsonObject.get("file_type").getAsString());
        final JsonElement element = jsonObject.get("file_size");
        if (element != null) {
        	item.setSize(element.getAsLong());
        }
        item.setReadOnly(jsonObject.get("read_only").getAsBoolean());
        if (jsonObject.has("updated_at")) {
        	try {
				item.updatedDate = getDateValue(jsonObject.get("updated_at").getAsString());
			} catch (RuntimeException e) {
				LOG.error(e.getMessage());
			}
        }

		return item;
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

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public Date getUpdatedDate() {
    	return this.updatedDate;
	}
	
	public boolean isFolder() {
		return "FOLDER".equals(this.getType());
	}
}
