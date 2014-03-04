/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class Group {

	public static final String USERS = "users";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	private String uid;
	private String name;
	private String description = null;
	private final List<User> users = new ArrayList<User>();
	
	public Group() {
		uid = null;
		name = null;
	}
	
	public Group(String uid, String name) {
		this.uid = uid;
		this.name = name;
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
    
    public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
	
	public List<User> getUsers() {
		return this.users;
	}
	
	public boolean hasUser(final String userUid) {
		if (userUid != null) {
			for (User user:this.users) {
				if (user.getUid() != null && user.getUid().equals(userUid)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<Group> fromApiResponseToGroups(final String json) {
        
        List<Group> groups = new ArrayList<Group>();
        JsonObject responseProps = ResponseUtils.responseToObject(json);
        JsonArray array = responseProps.getAsJsonArray("groups");
        for (int i=0; i<array.size(); i++) {
            JsonObject props = array.get(i).getAsJsonObject();
            
            Group g = buildGroup(props);
            groups.add(g);
        }
        
        return groups;
	}
	
	public static Group fromApiResponseToGroup(final String json) {
		final JsonObject responseJson = ResponseUtils.responseToObject(json);
		final JsonObject groupJson = responseJson.getAsJsonObject("group");
		final Group group = buildGroup(groupJson);
		if (groupJson.has(USERS) && groupJson.get(USERS).isJsonArray()) {
			final JsonArray usersJson = groupJson.getAsJsonArray(USERS);
			for (JsonElement userJson:usersJson) {
				final User user = User.getUser((JsonObject) userJson);
				group.users.add(user);
			}
		}
		return group;
	}
	
	private static Group buildGroup(JsonObject props)
    {
        final Group group = new Group();
        group.uid = props.get("uid").getAsString();
        group.name = props.get(NAME).getAsString();
        if (props.has(DESCRIPTION) && !props.get(DESCRIPTION).isJsonNull()) {
        	group.description = props.get(DESCRIPTION).getAsString();
        }
        return group;
    }
}
