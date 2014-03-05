package com.mellmo.roambi.api.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

/**
 * 
 * @author charles
 *
 */
public class UserTest {
	
	@Test
	public void testToJSON() {
		final String jsonString = "{\"user\":{\"uid\":\"5c5bf5fe-d283-4b42-8c66-0259d352d019\",\"name\":\"Valencia Orange\",\"given_name\":\"Valencia\",\"family_name\":\"Orange\",\"primary_email\":\"carlos@mellmo.com\",\"user_account\":{\"role\":{\"uid\":\"standard\",\"label\":\"Viewer\"},\"status\":\"member\",\"enabled\":true}}}";
		testToJSON(jsonString);
	}

	private void testToJSON(final String jsonString) {
		final JsonObject originalJson = ResponseUtils.responseToObject(jsonString).get(User.USER).getAsJsonObject();
		assertTrue(originalJson.has("name"));
		final User user = User.getUser(originalJson);
		final JsonObject createdByToJSON = user.toJSON().getAsJsonObject(User.USER);
		assertEquals(originalJson, createdByToJSON);
	}
	
	@Test
	public void testToJSON_UserHasNoUserAccount() {
		final String jsonString = "{\"user\":{\"uid\":\"5c5bf5fe-d283-4b42-8c66-0259d352d019\",\"name\":\"Valencia Orange\",\"given_name\":\"Valencia\",\"family_name\":\"Orange\",\"primary_email\":\"carlos@mellmo.com\"}}";
		testToJSON(jsonString);
	}
}
