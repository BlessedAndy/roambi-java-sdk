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

public class Group {

	private String uid;
	private String name;
	
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
    
    public static List<Group> fromApiDetailsResponse(String json) {
        
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
    
    private static Group buildGroup(JsonObject props)
    {
        Group group = new Group();
        group.uid = props.get("uid").getAsString();
        group.name = props.get("name").getAsString();
//        group.createdAt = props.get("created_at").getAsString();
//        group.updatedAt = props.get("updated_at").getAsString();
        
        return group;
    }

}
