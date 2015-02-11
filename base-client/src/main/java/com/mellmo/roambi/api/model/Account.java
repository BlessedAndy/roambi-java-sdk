/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class Account {

	private String uid;
	private String title;

	public Account() {
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Deprecated
    public static List<Account> fromUserResourcesResponse(String json) 
    {
        JsonObject responseProps = ResponseUtils.responseToObject(json).get("resources").getAsJsonObject();
        return buildAccounts(responseProps);
    }

    public static List<Account> fromUserResourcesResponse(InputStream stream)
    {
        JsonObject responseProps = ResponseUtils.responseToObject(stream).get("resources").getAsJsonObject();
        return buildAccounts(responseProps);
    }

    public static List<Account> fromListResponse(String json)
    {
        JsonObject responseProps = ResponseUtils.responseToObject(json);
        return buildAccounts(responseProps);
    }
	
	private static List<Account> buildAccounts(JsonObject responseProps)
    {
        List<Account> accounts = new ArrayList<Account>();

        JsonArray array = responseProps.getAsJsonArray("accounts");
        for (int i=0; i<array.size(); i++) {
            JsonObject props = array.get(i).getAsJsonObject();
            
            Account a = new Account();
            a.uid = props.get("uid").getAsString();
            a.setTitle(props.get("title").getAsString());

            accounts.add(a);
        }
        
        return accounts;
    }

}
