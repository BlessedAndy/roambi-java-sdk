/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.requests;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RemovePermissionsRequest {

	private List<String> users;
	private List<String> groups;

	public RemovePermissionsRequest() {
		users = new ArrayList<String>();
		groups = new ArrayList<String>();
	}

	public RemovePermissionsRequest(List<String> users, List<String> groups) {
		this.users = users != null ? users : new ArrayList<String>();
		this.groups = groups != null ? groups : new ArrayList<String>();
	}

	public List<String> getUsers() {
		return users;
	}

    public List<String> getGroups() {
        return groups;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
	
	public String toJsonBody() {
		JSONObject body = new JSONObject();
		JSONArray usersList = new JSONArray();
		for (String user : users) {
			usersList.add(user);
		}

		JSONArray groupsList = new JSONArray();
		for (String group : groups) {
			groupsList.add(group);
		}

		body.put("users", usersList);
		body.put("groups", groupsList);

		return body.toString();
	}

}
