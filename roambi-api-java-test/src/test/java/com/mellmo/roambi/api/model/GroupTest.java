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
public class GroupTest {

	@Test
	public void testToJSON() {
		final String jsonString = "{\"group\":{\"uid\":\"52fd6b9bc2e692f7e9fee20c\",\"name\":\"Xab\\\\u20acc$\",\"description\":\"Xa \\\\u20ac \\\\u20ac \\\\u20ac\",\"created_at\":\"2014-02-14T01:04:27Z\",\"updated_at\":\"2014-02-14T01:04:27Z\",\"users\":[{\"uid\":\"5c5bf5fe-d283-4b42-8c66-0259d352d019\",\"name\":\"Valencia Orange\",\"given_name\":\"Valencia\",\"family_name\":\"Orange\",\"primary_email\":\"carlos@hello.com\"},{\"uid\":\"b6ca2501-8d8e-4ca5-9ac6-8ff346b24447\",\"name\":\"Charles Lastname2\",\"given_name\":\"Charles\",\"family_name\":\"Lastname2\",\"primary_email\":\"charles@hello.com\"}]}}";
		testToJSON(jsonString);
	}

	@Test
	public void testToJSON_GroupUsersIsNull() {
		final String jsonString = "{\"group\":{\"uid\":\"52fd6b9bc2e692f7e9fee20c\",\"name\":\"Xab\\\\u20acc$\",\"description\":\"Xa \\\\u20ac \\\\u20ac \\\\u20ac\",\"created_at\":\"2014-02-14T01:04:27Z\",\"updated_at\":\"2014-02-14T01:04:27Z\"}}";
		testToJSON(jsonString);
	}
	
	@Test
	public void testToJSON_GroupUsersIsEmptyList() {
		final String jsonString = "{\"group\":{\"uid\":\"52fd6b9bc2e692f7e9fee20c\",\"name\":\"Xab\\\\u20acc$\",\"description\":\"Xa \\\\u20ac \\\\u20ac \\\\u20ac\",\"created_at\":\"2014-02-14T01:04:27Z\",\"updated_at\":\"2014-02-14T01:04:27Z\",\"users\":[]}}";
		testToJSON(jsonString);
	}
	
	@Test
	public void testToJSON_GroupDescriptionIsNull() {
		final String jsonString = "{\"group\":{\"uid\":\"53166a2ac2e6d932f37cf2ad\",\"name\":\"fsjBuqA\",\"description\":null,\"created_at\":\"2014-03-05T00:04:58Z\",\"updated_at\":\"2014-03-05T00:04:58Z\"}}";
		testToJSON(jsonString);
	}
	
	private void testToJSON(final String jsonString) {
		final JsonObject originalJson = ResponseUtils.responseToObject(jsonString).get(Group.GROUP).getAsJsonObject();
		final Group group = Group.fromApiResponseToGroup(jsonString);
		final JsonObject createdByToJSON = group.toJSON().getAsJsonObject(Group.GROUP);
		assertEquals(originalJson, createdByToJSON);
	}
}
