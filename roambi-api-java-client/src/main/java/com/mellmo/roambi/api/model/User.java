/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.JsonUtils;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class User implements IBaseModel {
	
	public static final String FAMILY_NAME = "family_name";
	public static final String GIVEN_NAME = "given_name";
	public static final String PRIMARY_EMAIL = "primary_email";

	private String uid;
	private String givenName;
	private String familyName;
	private String primaryEmail;
	private UserAccount userAccount;

	public User() {
	}

    public User(String uid) {
        this.uid = uid;
    }

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getPrimaryEmail() {
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail;
	}
	
	public UserAccount getUserAccount() {
		return this.userAccount;
	}
	
	public static User fromUserResourcesResponse(String json) {
		JsonObject responseProps = ResponseUtils.responseToObject(json).get("resources").getAsJsonObject();
		return getUser(responseProps);
	}

    public JsonObject toJSON() throws Exception {
        JsonObject resultJson = new JsonObject();
        JsonObject userJson = new JsonObject();

        userJson.addProperty("uid", this.getUid());
        userJson.addProperty("given_name", this.getGivenName());
        userJson.addProperty("family_name", this.getFamilyName());
        userJson.addProperty("primary_email", this.getPrimaryEmail());

        resultJson.add("user", userJson);

        return resultJson;
    }
	
	public static User fromApiResponseToUser(final String json) {
		final JsonObject userJson = ResponseUtils.responseToObject(json).get("user").getAsJsonObject();
		final User user = getUser(userJson);
		return user;
	}

	public static User getUser(final JsonObject json) {
		final User user = new User();
		user.setUid(json.get("uid").getAsString());
		user.setFamilyName(json.get(FAMILY_NAME).getAsString());
		user.setGivenName(json.get(GIVEN_NAME).getAsString());
		user.setPrimaryEmail(json.get(PRIMARY_EMAIL).getAsString());
		user.userAccount = UserAccount.getUserAccount(JsonUtils.getJson(json, "user_account"));
		return user;
	}

	public static PagedList<User> fromApiListResponse(String json) {
		PagedList<User> list = new PagedList<User>();
		List<User> users = new ArrayList<User>();

		JsonObject props = ResponseUtils.responseToObject(json);
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
		
		user.setUid(responseProps.get("uid").getAsString());
        user.setFamilyName(responseProps.get("last_name").getAsString());
        user.setGivenName(responseProps.get("first_name").getAsString());
        user.setPrimaryEmail(responseProps.get("email_address").getAsString());
//        user.createdAt = responseProps.get("created_at").getAsString();
//        user.updatedAt = responseProps.get("updated_at").getAsString();
        
		return user;
	}

}
