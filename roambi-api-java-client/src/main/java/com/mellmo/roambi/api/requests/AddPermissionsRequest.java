/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.requests;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.mellmo.roambi.api.model.RoambiFilePermission;

public class AddPermissionsRequest {
	
	public static class FilePermission {
		public String uid;
		public RoambiFilePermission permission;
		
		public FilePermission(String uid, RoambiFilePermission perm) {
			this.uid = uid;
			this.permission = perm;
		}
	}

	private RoambiFilePermission publicPermission = null;
	private List<FilePermission> users;
	private List<FilePermission> groups;

	public AddPermissionsRequest() {
		users = new ArrayList<FilePermission>();
		groups = new ArrayList<FilePermission>();
	}
	
	public AddPermissionsRequest(final List<String> users, final List<String> groups, RoambiFilePermission permission) {
		if (permission == RoambiFilePermission.PUBLIC) {
			publicPermission = permission;
		}
		this.users = asList(users, permission);
		this.groups = asList(groups, permission);
	}
	
	private static List<FilePermission> asList(final List<String> targets, final RoambiFilePermission permission) {
    	final List<FilePermission> permissions = new ArrayList<FilePermission>();
    	if (targets !=null ) {
            for(String group:targets) {
            	permissions.add(new FilePermission(group, permission));
            }
        }
    	return permissions;
    }

	public AddPermissionsRequest(List<FilePermission> users, List<FilePermission> groups) {
		this.users = users;
		this.groups = groups;
	}
	
	public List<FilePermission> getUsers() {
		return users;
	}
	
	public void setUsers(List<FilePermission> users) {
		this.users = users;
	}
	
	public List<FilePermission> getGroups() {
		return groups;
	}

	public void setGroups(List<FilePermission> groups) {
		this.groups = groups;
	}
	
	public String toJsonBody() {
		JSONObject body = new JSONObject();
		JSONObject usersMap = new JSONObject();
		for (FilePermission user : users) {
			//JSONObject userPermission = new JSONObject();
			//userPermission.put(user.uid, user.permission.toString());
			usersMap.put(user.uid, user.permission.toString());			
		}

		JSONObject groupsMap = new JSONObject();
		for (FilePermission group : groups) {
			//JSONObject groupPermission = new JSONObject();
			//groupPermission.put(group.uid, group.permission.toString());
			groupsMap.put(group.uid, group.permission.toString());
		}
		
		if (this.publicPermission != null) {
			JSONObject publicMap = new JSONObject();
			publicMap.put(RoambiFilePermission.READ, "true");
			body.put("public_access", publicMap);
		}

		body.put("users", usersMap);
		body.put("groups", groupsMap);

		return body.toString();
	}

}
