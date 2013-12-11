/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.Account;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.api.model.PagedList;
import com.mellmo.roambi.api.model.User;

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
    public void testGetToken() throws ApiException {
    }
}
