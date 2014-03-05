package com.mellmo.roambi.api.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

/**
 * 
 * @author charles
 *
 */
public class UserAccountTest {

	@Test
	public void testToJSON() {
		final String jsonString = "{\"user_account\":{\"role\":{\"uid\":\"standard\",\"label\":\"Viewer\"},\"status\":\"member\",\"enabled\":true}}";
		testToJSON(jsonString);
	}

	@Test
	public void testToJSON_UserAccountHasNoRole() {
		final String jsonString = "{\"user_account\":{\"status\":\"member\",\"enabled\":true}}";
		testToJSON(jsonString);
	}
	
	private void testToJSON(final String jsonString) {
		final JsonObject originalJson = ResponseUtils.responseToObject(jsonString).get(User.USER_ACCOUNT).getAsJsonObject();
		final UserAccount userAccount = UserAccount.getUserAccount(originalJson);
		final JsonObject createdByToJSON = userAccount.toJSON();
		assertEquals(originalJson, createdByToJSON);
	}
}
