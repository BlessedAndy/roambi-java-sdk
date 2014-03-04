package com.mellmo.roambi.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.Account;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.api.model.PagedList;
import com.mellmo.roambi.api.model.Role;
import com.mellmo.roambi.api.model.User;
import com.mellmo.roambi.api.model.UserAccount;

public class GetUserResourcesTest extends ApiClientTestBase {

	@Test
	public void testValidUserResources() throws ApiException {
		List<Account> accounts = client.getUserAccounts();
		Assert.assertNotNull(accounts);
		Assert.assertEquals(1, accounts.size());
		
		for (Account account : accounts) {
			System.out.println ("ACCOUNT: " + account.getUid());
		}
	}

    @Test
    public void testGetUsers() throws ApiException {
        List<Account> accounts = client.getUserAccounts();
        Assert.assertNotNull(accounts);
        Assert.assertEquals(1, accounts.size());

        for (Account account : accounts) {
            System.out.println ("ACCOUNT: " + account.getUid());
            client.setCurrentAccount(account.getUid());
            PagedList<User> pagedUsers = client.getUsers();
            List<User> users = pagedUsers.getResults();
            for(User u:users) {
                System.out.println("User: " + u.getGivenName());
            }
        }
    }

    @Test
    public void testGetGroups() throws ApiException {
        List<Group> grps = client.getGroups();
        Assert.assertNotNull(grps);
        Assert.assertEquals(2, grps.size());

    }
	
    @Ignore
	@Test
	public void testInviteUser() throws ApiException {
		final String primary_email = getRandomEmail();
		final String given_name = RandomStringUtils.randomAlphabetic(4);
		final String family_name = RandomStringUtils.randomAlphabetic(7);
		final Role role = Role.ADMINISTRATOR;
		final User user = client.inviteUser(primary_email, given_name, family_name, role);
		assertNotNull(user);
		final UserAccount userAccount = user.getUserAccount();
		assertNotNull(userAccount);
		assertEquals(role, userAccount.getRole());
		assertEquals(primary_email, user.getPrimaryEmail());
		assertEquals(given_name, user.getGivenName());
		assertEquals(family_name, user.getFamilyName());
		assertEquals(UserAccount.Status.INVITED, userAccount.getStatus());
	}
	
	@Test
	@Ignore
	public void testGetUser() throws Exception {
		final String user_uid = "5c5bf5fe-d283-4b42-8c66-0259d352d019";
		final User user = client.getUserInfo(user_uid);
		assertNotNull(user);
		assertEquals(user_uid, user.getUid());
		final UserAccount userAccount = user.getUserAccount();
		assertNotNull(userAccount);
	}
	
	@Test
	@Ignore
	public void testUpdateUser() throws Exception {
    	final String user_uid = "5c5bf5fe-d283-4b42-8c66-0259d352d019";
    	final Role role = Role.VIEWER;
    	final boolean enabled = true;
    	final User user = client.updateUser(user_uid, role, enabled);
    	assertNotNull(user);
    	final UserAccount userAccount = user.getUserAccount();
    	assertNotNull(userAccount);
    	assertNotNull(userAccount.getRole());
    	assertEquals(role.getUid(), userAccount.getRole().getUid());
    	assertEquals(enabled, userAccount.isEnabled());
    }
    
    @Test
    @Ignore
    public void testSetUserRole() throws Exception {
    	final String user_uid = "5c5bf5fe-d283-4b42-8c66-0259d352d019";
    	final Role role = Role.PUBLISHER;
    	final User user = client.setUserRole(user_uid, role);
    	assertNotNull(user);
    	final UserAccount userAccount = user.getUserAccount();
    	assertNotNull(userAccount);
    	assertNotNull(userAccount.getRole());
    	assertEquals(role.getUid(), userAccount.getRole().getUid());
    }
    
    @Test
    @Ignore
    public void testGetGroupInfo() throws Exception {
    	final String group_uid = "52fd6b9bc2e692f7e9fee20c";
    	final Group group = client.getGroupInfo(group_uid);
    	assertNotNull(group);
    	assertEquals(group_uid, group.getUid());
    }
    
    @Test
    @Ignore
    public void testCreateGroup() throws Exception {
    	final String name = RandomStringUtils.randomAlphabetic(7);
    	final Group group = client.createGroup(name);
    	assertNotNull(group);
    	assertEquals(name, group.getName());
    	assertNull(group.getDescription());
    }
    
    @Test
    @Ignore
    public void testUpdateGroup() throws Exception {
    	final String group_uid = "52fd6b9bc2e692f7e9fee20c";
    	final String name = "Xab\u20ACc$";
    	final String description = "Xa \u20AC \u20AC \u20AC";
    	final Group group = client.setGroupInfo(group_uid, name, description);
    	assertNotNull(group);
    	assertEquals(name, group.getName());
    	assertEquals(description, group.getDescription());
    }
    
    @Test
    @Ignore
    public void testAddGroupUsers() throws Exception {
    	final String group_uid = "52fd6b9bc2e692f7e9fee20c";
    	final Group group = client.addGroupUsers(group_uid, "5c5bf5fe-d283-4b42-8c66-0259d352d019", "b6ca2501-8d8e-4ca5-9ac6-8ff346b24447");
    	assertNotNull(group);
    	assertEquals(group_uid, group.getUid());
    	assertEquals(2, group.getUsers().size());
    }
    
    @Test
    @Ignore
    public void testAddNoneMemberUserToGroup() throws Exception {
    	final String group_uid = "52fd6b9bc2e692f7e9fee20c";
    	final Group group = client.addGroupUsers(group_uid, "79aad484-981b-48b0-9a03-f8a3d3cae106");
    	assertNotNull(group);
    	assertEquals(group_uid, group.getUid());
    	assertEquals(1, group.getUsers().size());
    }
    
    @Test
    @Ignore
    public void testRemoveGroupUser() throws Exception {
    	final String group_uid = "52fd6b9bc2e692f7e9fee20c";
    	client.addGroupUsers(group_uid, "5c5bf5fe-d283-4b42-8c66-0259d352d019", "b6ca2501-8d8e-4ca5-9ac6-8ff346b24447");
    	Group group = client.getGroupInfo(group_uid);
    	assertNotNull(group);
    	assertEquals(group_uid, group.getUid());
    	assertEquals(2, group.getUsers().size());
    	
    	final String user_uid = "b6ca2501-8d8e-4ca5-9ac6-8ff346b24447";
    	client.removeGroupUser(group_uid, user_uid);
    	group = client.getGroupInfo(group_uid);
    	assertNotNull(group);
    	assertEquals(group_uid, group.getUid());
    	assertEquals(1, group.getUsers().size());
    }
    
    @Test
    @Ignore
    public void testRemoveGroupUsers() throws Exception {
    	final String group_uid = "52fd6b9bc2e692f7e9fee20c";
    	final Group group = client.removeGroupUsers(group_uid, "5c5bf5fe-d283-4b42-8c66-0259d352d019", "b6ca2501-8d8e-4ca5-9ac6-8ff346b24447");
    	assertNotNull(group);
    	assertEquals(group_uid, group.getUid());
    	assertEquals(0, group.getUsers().size());
    }
    
    @Test
    @Ignore
    public void testRemoveUserFromAllGroups() throws Exception {
    	final String user_uid = "5c5bf5fe-d283-4b42-8c66-0259d352d019";
    	final List<Group> groups = client.getGroups();
    	for (Group group:groups) {
    		final Group g = client.addGroupUsers(group.getUid(), user_uid);
    		assertNotNull(g);
    		assertFalse(g.getUsers().isEmpty());
    		assertTrue(g.hasUser(user_uid));
    	}    	
    	client.removeUserFromAllGroups(user_uid);
    	for (Group group:groups) {
    		final Group g = client.getGroupInfo(group.getUid());
    		assertNotNull(g);
    		assertFalse(g.hasUser(user_uid));
    	}
    }
    
    @Test
    @Ignore
    public void testDeleteGroup() throws Exception {
    	final String name = getTimeStampedName("test", "");
    	Group group = client.createGroup(name);
    	final String group_uid = group.getUid();
    	assertNotNull(group);
    	assertEquals(name, group.getName());
    	group = client.getGroupInfo(group_uid);
    	assertNotNull(group);
    	client.deleteGroup(group.getUid());
    	try {
    		group = client.getGroupInfo(group_uid);
    		fail("expect api exception");
    	} catch (ApiException e) {
    		assertEquals("invalid_resource", e.getCode());
    		assertTrue(e.getMessage().contains("group_uid"));
    	}
    }
}
