/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mellmo.roambi.api.exceptions.ApiException;

/**
 * Created with IntelliJ IDEA.
 * User: pcheng
 * Date: 8/14/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class AuthTest extends ApiClientTestBase {

    @Test(expected =  ApiException.class)
    public void testBadConsumerKey() throws ApiException {

        final RoambiApiClient client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey() + "bad", consumerSecret(), getRedirectURI(), this);
        client.currentUser(); // authenticate

    }

    @Test(expected =  ApiException.class)
    public void testBadConsumerSecret() throws ApiException {

        final RoambiApiClient client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey(), consumerSecret() + "bad", getRedirectURI(), this);
        client.currentUser(); // authenticate
    }

    @Test(expected =  ApiException.class)
    public void testBadRedirectURI() throws ApiException {

        final RoambiApiClient client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey(), consumerSecret(), getRedirectURI() + "bad", this);
        client.currentUser(); // authenticate
    }

    static class RoambiApiApplicationWrapper implements RoambiApiApplication {
        RoambiApiApplication app;
        RoambiApiApplicationWrapper(RoambiApiApplication app) {
            this.app = app;
        }
        @Override
        public String getUsername() {
            return app.getUsername();
        }

        @Override
        public String getPassword() {
            return app.getPassword();
        }
    }

    @Test(expected =  ApiException.class)
    public void testBadUserName() throws ApiException {

        RoambiApiApplicationWrapper badUsernameApp = new RoambiApiApplicationWrapper(this) {
            @Override
            public String getUsername() {
                return super.getUsername() + "bad";
            }
        };
        final RoambiApiClient client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey(), consumerSecret(), getRedirectURI(), badUsernameApp);
        client.currentUser(); // authenticate
    }

    @Test(expected =  ApiException.class)
    public void testBadPassword() throws ApiException {

        RoambiApiApplicationWrapper badPasswordApp = new RoambiApiApplicationWrapper(this) {
            @Override
            public String getPassword() {
                return super.getPassword() + "bad";
            }
        };
        final RoambiApiClient client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey(), consumerSecret(), getRedirectURI(), badPasswordApp);
        client.currentUser(); // authenticate
    }
	
	@Test
	public void testRefreshToken() throws ApiException {
		assertTrue(client.refreshToken());
	}
}
