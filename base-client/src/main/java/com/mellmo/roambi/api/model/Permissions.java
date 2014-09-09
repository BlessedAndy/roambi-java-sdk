/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

public class Permissions {


    Map<String, String> Groups = new HashMap<String, String>();
    Map<String, String> Users = new HashMap<String, String>();

    public Permissions(JsonObject jsonObject) {
        JsonArray groupArray = jsonObject.get("groups").getAsJsonArray();
        JsonArray usersArray = jsonObject.get("users").getAsJsonArray();

        fillMap(groupArray, Groups);
        fillMap(usersArray, Users);
    }

    private void fillMap(JsonArray array, Map<String,String> map) {
        Iterator itr = array.iterator();
        while(itr.hasNext()) {
            JsonObject obj = (JsonObject)itr.next();
            if(obj.has("everyone")) {
                map.put("everyone", obj.get("everyone").getAsString());
            } else {
                map.put(obj.get("uid").getAsString(), obj.get("access").getAsString());
            }
        }
    }

    public String getUserAccess(String uid) {
        return Users.get(uid);
    }

    public String getGroupAccess(String uid){
        return Groups.get(uid);
    }

    public Map<String, String> getGroups() {
        return Groups;
    }
    public Map<String, String> getUsers() {
        return Users;
    }
}
