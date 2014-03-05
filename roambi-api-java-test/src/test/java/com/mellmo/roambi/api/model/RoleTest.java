package com.mellmo.roambi.api.model;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

/**
 * 
 * @author charles
 *
 */
public class RoleTest {
	
	@Test
	public void testGetRoleUid() throws Exception {
		assertEquals(Role.getRoleUid(Role.VIEWER.getLabel()), Role.VIEWER.getUid());
		assertEquals(Role.getRoleUid(Role.PUBLISHER.getLabel()), Role.PUBLISHER.getUid());
		assertEquals(Role.getRoleUid(Role.ADMINISTRATOR.getLabel()), Role.ADMINISTRATOR.getUid());
		
		assertEquals(Role.getRoleUid(Role.VIEWER.getLabel().toUpperCase()), Role.VIEWER.getUid());
		assertEquals(Role.getRoleUid(Role.PUBLISHER.getLabel().toUpperCase()), Role.PUBLISHER.getUid());
		assertEquals(Role.getRoleUid(Role.ADMINISTRATOR.getLabel().toUpperCase()), Role.ADMINISTRATOR.getUid());
		
		assertEquals(Role.getRoleUid(Role.VIEWER.getLabel().toLowerCase()), Role.VIEWER.getUid());
		assertEquals(Role.getRoleUid(Role.PUBLISHER.getLabel().toLowerCase()), Role.PUBLISHER.getUid());
		assertEquals(Role.getRoleUid(Role.ADMINISTRATOR.getLabel().toLowerCase()), Role.ADMINISTRATOR.getUid());
		
		final String label = RandomStringUtils.randomAlphabetic(7);
		assertEquals(Role.getRoleUid(label), label);
		
		assertEquals(Role.getRoleUid(null), "");
	}
	
	@Test
	public void testToJSON() {
		final String jsonString = "{\"role\":{\"uid\":\"standard\",\"label\":\"Viewer\"},\"status\":\"member\",\"enabled\":true}";
		final JsonObject originalJson = ResponseUtils.responseToObject(jsonString).get(UserAccount.ROLE).getAsJsonObject();
		final Role role = Role.getRole(originalJson);
		final JsonObject createdByToJSON = role.toJSON();
		assertEquals(originalJson, createdByToJSON);
	}	
}
