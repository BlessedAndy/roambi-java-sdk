/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.JsonUtils;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class User implements IBaseModel {
	
	public static final String USER = "user";
	public static final String USER_ACCOUNT = "user_account";
	public static final String FAMILY_NAME = "family_name";
	public static final String GIVEN_NAME = "given_name";
	public static final String PRIMARY_EMAIL = "primary_email";
	private static final String NAME = "name";

	private String uid = null;
	private String givenName;
	private String familyName;
	private String primaryEmail;
	private UserAccount userAccount;
	private String name = null;

	public User() {
	}

    public User(String uid) {
        this.uid = uid;
    }

	public String getUid() {
		return uid;
	}

	private void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getName() {
		return StringUtils.isBlank(name) ? String.format("%s %s", givenName, familyName).trim() : name;
	}

	public String getGivenName() {
		return givenName;
	}

	private void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	private void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	private void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}
	
	public UserAccount getUserAccount() {
		return this.userAccount;
	}

    @Deprecated
	public static User fromUserResourcesResponse(String json) {
		JsonObject responseProps = ResponseUtils.responseToObject(json).get("resources").getAsJsonObject();
		return getUser(responseProps);
	}

    public static User fromUserResourcesResponse(InputStream stream) {
        JsonObject responseProps = ResponseUtils.responseToObject(stream).get("resources").getAsJsonObject();
        return getUser(responseProps);
    }

    public JsonObject toJSON() {
        JsonObject resultJson = new JsonObject();
        JsonObject userJson = new JsonObject();

        userJson.addProperty(UID, this.getUid());
        if (this.name != null) {
        	userJson.addProperty(NAME, this.name);
        }
        userJson.addProperty(GIVEN_NAME, this.getGivenName());
        userJson.addProperty(FAMILY_NAME, this.getFamilyName());
        userJson.addProperty(PRIMARY_EMAIL, this.getPrimaryEmail());
        if (this.userAccount != null) {
        	userJson.add(USER_ACCOUNT, this.userAccount.toJSON());
        }

        resultJson.add(USER, userJson);

        return resultJson;
    }

    @Deprecated
	public static User fromApiResponseToUser(final String json) {
		final JsonObject userJson = ResponseUtils.responseToObject(json).get(USER).getAsJsonObject();
		final User user = getUser(userJson);
		return user;
	}

    public static User fromApiResponseToUser(final InputStream stream) {
        final JsonObject userJson = ResponseUtils.responseToObject(stream).get(USER).getAsJsonObject();
        final User user = getUser(userJson);
        return user;
    }

    public static User getUser(final JsonObject json) {
		final User user = new User();
		user.uid = json.get(UID).getAsString();
		user.familyName = json.get(FAMILY_NAME).getAsString();
		user.givenName = json.get(GIVEN_NAME).getAsString();
		user.primaryEmail = json.get(PRIMARY_EMAIL).getAsString();
		user.name = JsonUtils.getString(json, NAME);
		user.userAccount = UserAccount.getUserAccount(JsonUtils.getJson(json, USER_ACCOUNT));
		return user;
	}

    @Deprecated
	public static PagedList<User> fromApiListResponse(String json) {
        JsonObject props = ResponseUtils.responseToObject(json);
        return fromJsonObject(props);
	}

    public static PagedList<User> fromApiListResponse(InputStream stream) {
        JsonObject props = ResponseUtils.responseToObject(stream);
        return fromJsonObject(props);
    }

    private static PagedList<User> fromJsonObject(JsonObject props) {
        PagedList<User> list = new PagedList<User>();
        List<User> users = new ArrayList<User>();

        JsonArray array = props.getAsJsonArray("users");
        for (int i=0; i<array.size(); i++) {
            users.add(User.fromApiListResponse(array.get(i).getAsJsonObject()));
        }
        list.setResults(users);

        if (props.has("list_data")) {
            list.fromApiListResponse(props.get("list_data").getAsJsonObject());
        }

        return list;
    }

    private static User fromApiListResponse(JsonObject responseProps) {
		User user = new User();
		
		user.setUid(responseProps.get(UID).getAsString());
        user.setFamilyName(responseProps.get("last_name").getAsString());
        user.setGivenName(responseProps.get("first_name").getAsString());
        user.setPrimaryEmail(responseProps.get("email_address").getAsString());
//        user.createdAt = responseProps.get("created_at").getAsString();
//        user.updatedAt = responseProps.get("updated_at").getAsString();
        
		return user;
	}

}
