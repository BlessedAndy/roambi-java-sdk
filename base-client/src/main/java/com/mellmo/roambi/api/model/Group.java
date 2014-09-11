/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.JsonUtils;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class Group implements IBaseModel {

	public static final String UPDATED_AT = "updated_at";
	public static final String CREATED_AT = "created_at";
	public static final String GROUP = "group";
	public static final String USERS = "users";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	private String uid;
	private String name;
	private String description = null;
	private Date createdAt = null;
	private Date updatedAt = null;
	private List<User> users = null;
	
	private Group() {
		uid = null;
		name = null;
	}
	
	public Group(final String uid, final String name) {
		this.uid = uid;
		this.name = name;
	}
	
	public Group(final String uid) {
		this.uid = uid;
		this.name = uid;
	}

	public String getUid() {
        return uid;
    }

//    public void setUid(String uid) {
//        this.uid = uid;
//    }

    public String getName() {
        return name;
    }

    public String getDescription() {
		return description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public List<User> getUsers() {
		return this.users;
	}
	
	public boolean hasUser(final String userUid) {
		if (userUid != null && this.users != null) {
			for (User user:this.users) {
				if (user.getUid() != null && user.getUid().equals(userUid)) {
					return true;
				}
			}
		}
		return false;
	}

    public JsonObject toJSON() {

        JsonObject resultJson = new JsonObject();
        JsonObject groupJson = new JsonObject();

        groupJson.addProperty(UID, this.getUid());
        groupJson.addProperty(NAME, this.getName());
        groupJson.addProperty(DESCRIPTION, this.getDescription());
        if (this.createdAt != null) {
        	groupJson.addProperty(CREATED_AT, JsonUtils.printDate(this.createdAt));
        }
        if (this.updatedAt != null) {
        	groupJson.addProperty(UPDATED_AT, JsonUtils.printDate(this.updatedAt));
        }
        if (this.users != null) {
        	groupJson.add(USERS, getJSONUsers(getUsers()));
        }
        resultJson.add(GROUP, groupJson);
        return resultJson;
    }

    private static JsonArray getJSONUsers(List<User> users) {
        JsonArray userArray = new JsonArray();
        for(User u:users) {
            userArray.add((JsonElement)u.toJSON().getAsJsonObject(User.USER));
        }
        return userArray;
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
		final JsonObject groupJson = responseJson.getAsJsonObject(GROUP);
		final Group group = buildGroup(groupJson);
		if (groupJson.has(USERS) && groupJson.get(USERS).isJsonArray()) {
			final JsonArray usersJson = groupJson.getAsJsonArray(USERS);
			if (group.users == null) {
				group.users = new ArrayList<User>();
			}
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
        group.uid = props.get(UID).getAsString();
        group.name = props.get(NAME).getAsString();
        if (props.has(DESCRIPTION) && !props.get(DESCRIPTION).isJsonNull()) {
        	group.description = props.get(DESCRIPTION).getAsString();
        }
        group.createdAt = JsonUtils.getDate(props, CREATED_AT);
        group.updatedAt = JsonUtils.getDate(props, UPDATED_AT);
        return group;
    }
}
