/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class User {

	private String uid;
	private String givenName;
	private String familyName;
	private String primaryEmail;

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

	public static User fromUserResourcesResponse(String json) {
		User user = new User();

		JsonObject responseProps = ResponseUtils.responseToObject(json).get("resources").getAsJsonObject();
		user.setUid(responseProps.get("uid").getAsString());
		user.setFamilyName(responseProps.get("family_name").getAsString());
		user.setGivenName(responseProps.get("given_name").getAsString());
		user.setPrimaryEmail(responseProps.get("primary_email").getAsString());

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
