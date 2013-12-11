/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.utils.ResponseUtils;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 9/10/13
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpMethodTest extends ApiClientTestBase {
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String ACCEPT = "Accept";
    protected static final String UTF_8 = "utf-8";

    private String getAuthorizationCodeFromServer(HttpClient httpClient, String authUrl, String username, String password, String serviceHost, int servicePort) throws ApiException {
        String authorizationCode = null;
        String redirect_uri = getRedirectURI();
        if (username == null || password == null) {
            throw new IllegalArgumentException("user name and password cannot be null");
        }

        try {
            httpClient.getParams().setAuthenticationPreemptive(true);
            Credentials authCredentials = new UsernamePasswordCredentials(username, password);
            httpClient.getState().setCredentials(new AuthScope(serviceHost, servicePort, AuthScope.ANY_REALM), authCredentials);

            PostMethod authPost = null;
            //GetMethod authPost = null;
            try {
                authPost = new PostMethod(authUrl);
                authPost.setRequestHeader(ACCEPT, APPLICATION_JSON);
                //authPost.addParameter("client_id", consumerKey());

                int result = httpClient.executeMethod(authPost);
                if (log.isDebugEnabled()) {
                    log.debug("Auth result: " + result);
                    String responseBody = authPost.getResponseBodyAsString();
                    log.debug(responseBody);
                }

                switch(result) {
                    case 302:
                        log.debug("Detected redirect response");
                        Header header = authPost.getResponseHeader("Location");
                        if (header.getValue() != null && header.getValue().startsWith(redirect_uri)) {
                            log.debug("Handling successful authentication");
                            URI redirectUri = new URI(header.getValue());
                            List<NameValuePair> params = URLEncodedUtils.parse(redirectUri, UTF_8);
                            for (org.apache.http.NameValuePair pair : params) {
                                if ("code".equals(pair.getName())) {
                                    authorizationCode = pair.getValue();
                                }
                            }
                        }
                        if (authorizationCode == null) {
                            throw new IllegalStateException("no authorization code found");
                        }
                        break;
                    case 400:
                    case 401:
                        throw ApiException.fromApiResponse(authPost.getStatusCode(), authPost.getResponseBodyAsStream());
                    default:
                        throw new IllegalStateException("Invalid status code:" + result + ". url =" + authUrl);
                }
            }
            finally {
                if (authPost != null) {
                    authPost.releaseConnection();
                }
            }

        } catch (IOException e) {
            throw new ApiException(400, e.getMessage(), "");
        } catch (URISyntaxException e) {
            throw new ApiException(400, e.getMessage(), "");
        }
        return authorizationCode;
    }

    private String getAccessTokenFromServer(HttpClient httpClient, String tokenUrl, String authorizationCode) throws ApiException {
        String token = null;

        try {
            tokenUrl = tokenUrl + "?client_secret=" + consumerSecret();
            PostMethod authPost = new PostMethod(tokenUrl);
            authPost.setRequestHeader(ACCEPT, APPLICATION_JSON);

            //authPost.addParameter("client_secret", consumerSecret());
            authPost.addParameter("client_id", consumerKey());
            authPost.addParameter("code", authorizationCode);
            authPost.addParameter("redirect_uri", getRedirectURI());
            authPost.addParameter("grant_type", "authorization_code");

            int result = httpClient.executeMethod(authPost);

            if (log.isDebugEnabled()) {
                log.debug("Auth result: " + result);
                for (Header header : authPost.getResponseHeaders()) {
                    log.debug(header.getName() + " :: " + header.getValue());
                }
                String responseBody = authPost.getResponseBodyAsString();
                log.debug(responseBody);
            }

            if(result == 400 || result == 401) {
                throw ApiException.fromApiResponse(result, authPost.getResponseBodyAsStream());
            }

            JsonObject responseObject = ResponseUtils.responseToObject(authPost.getResponseBodyAsString());
            String refreshToken = responseObject.get("refresh_token").getAsString();
            token = responseObject.get("access_token").getAsString();
        } catch (IOException ex) {
            throw new ApiException(400, "bad request", "");
        }

        return token;
    }

    public String getAuthorizationCodeRequestUrl(String authUrl, String redirectURI){
        //http://localhost:3000/1/authorize?client_id=a1285fdfae2fb4082c90e9bb&redirect_uri=http://foo.bar&response_type=code

        String authCodeUrl = authUrl + "?redirect_uri=http://foo.bar&response_type=code";
        return authCodeUrl;
    }

    @Test
    public void testAuthorizeAsHttpGet() throws URISyntaxException, ApiException {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        URI serviceUri = new URI(serverUrl());

        //String url = getAuthorizationCodeRequestUrl(client.getAuthorizeServerUrl(),getRedirectURI());

        String url = new AuthorizationCodeRequestUrl(client.getAuthorizeServerUrl(), consumerKey()).setRedirectUri(getRedirectURI()).build();

        String authCode = getAuthorizationCodeFromServer(httpClient, url, getUsername(), getPassword(), serviceUri.getHost(), serviceUri.getPort());
        Assert.assertNotNull(authCode);

        url = client.getTokenServerUrl();

        String token = getAccessTokenFromServer(httpClient, url, authCode );

        Assert.assertNotNull(token);



    }


}
