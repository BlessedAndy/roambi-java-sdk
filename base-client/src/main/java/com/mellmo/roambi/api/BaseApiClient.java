/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.common.base.Strings;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.Account;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.User;

public abstract class BaseApiClient extends RESTClient {
	public static final String TITLE = "title";
	protected static final String CODE = "code";
	protected static final String REDIRECT_URI = "redirect_uri";
	protected static final String GRANT_TYPE = "grant_type";
	protected static final String ACCESS_TOKEN = "access_token";
	protected static final String REFRESH_TOKEN = "refresh_token";
	protected static final String ACCEPT = "Accept";
	public static final String DEFAULT_API_SERVICE_BASE_URL = "https://api.roambi.com/";
	protected static final String TOKEN_ENDPOINT = "token";
	protected static final String AUTHORIZE_ENDPOINT = "authorize";
	protected static final Logger LOG = LoggerFactory.getLogger(BaseApiClient.class);

	protected String clientId;
	protected String clientSecret;
	protected RoambiApiApplication application;
	protected int apiVersion;
	protected String baseServiceUrl;
	protected URI serviceUri;
	protected HttpClient httpClient;
	protected String authorizationCode;
	protected String accessToken;
	protected String refreshToken;
	protected User currentUser = null;
	protected String currentAccountUid = null;
	protected String redirect_uri = "roambi-api://client.roambi.com/authorize";
	protected int retries = 3;
    
    public BaseApiClient() {
    }
    
    public BaseApiClient(String serviceUrl, int apiVersion, String accessToken) {
    	this.baseServiceUrl = normalizeServiceUrl(serviceUrl);
    	this.serviceUri = URI.create(serviceUrl);
    	this.apiVersion = apiVersion;
    	this.accessToken = accessToken;

    	httpClient = new HttpClient();
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    }

	public BaseApiClient(String serviceUrl, int apiVersion, String clientId, String clientSecret, String redirect_uri, RoambiApiApplication app) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.application = app;
		this.apiVersion = apiVersion;
		this.baseServiceUrl = normalizeServiceUrl(serviceUrl);
		this.serviceUri = URI.create(serviceUrl);
        this.redirect_uri = redirect_uri;

		httpClient = new HttpClient();
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	}

    public BaseApiClient(String serviceUrl, int apiVersion, String clientId, String clientSecret, String redirect_uri, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword, String proxyUserDomain, RoambiApiApplication app) throws IOException {
        this(serviceUrl, apiVersion, clientId, clientSecret, redirect_uri, app);
        clientProxyConfig(proxyHost, proxyPort, proxyUsername, proxyPassword, proxyUserDomain);
    }
    
    private void clientProxyConfig(String proxyHost, int proxyPort, String proxyUsername, String proxyPassword, String proxyUserDomain) throws IOException {
        HostConfiguration config = httpClient.getHostConfiguration();
        if (proxyUsername != null) {
        	if (proxyUserDomain == null) {
        		proxyUserDomain = "";
        	}
        	LOG.info("providing credentials for "+proxyHost+":"+proxyPort+" un="+proxyUsername);
			httpClient.getState().setProxyCredentials(new AuthScope(proxyHost, proxyPort,AuthScope.ANY_REALM), 
					new NTCredentials(proxyUsername, proxyPassword, InetAddress.getLocalHost().getCanonicalHostName(), proxyUserDomain));
        }
        config.setProxy(proxyHost, proxyPort);
    }

	public void setCurrentAccount(String accountUid) {
		currentAccountUid = accountUid;
	}
	
	public User currentUser() throws ApiException, IOException {
		if (currentUser == null) {
			currentUser = getCurrentUser();
		}
		return currentUser;
	}

	public boolean isAuthenticated() {
		return (accessToken != null);
	}
	
	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}
	
	public void setRefreshToken(final String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public abstract List<Account> getUserAccounts() throws ApiException, IOException;
	
	public abstract User getCurrentUser() throws ApiException, IOException;

	protected String getAccessToken() throws ApiException {
		if (!isAuthenticated()) authenticate();
		return this.accessToken;
	}
	
	protected String getRefreshToken() {
		return this.refreshToken;
	}
	
	protected String getAccountUid() {
		checkArgument(!Strings.isNullOrEmpty(currentAccountUid), "Current Account is not set.");
		return currentAccountUid;
	}

	private boolean authenticate() throws ApiException {
		if (accessToken == null) {
			accessToken = getAccessTokenFromServer();
		}

		return isAuthenticated();
	}
	
	private String getAccessTokenFromServer() throws ApiException {
		String url = new AuthorizationCodeRequestUrl(getAuthorizeServerUrl(), clientId)
		            		.setRedirectUri(redirect_uri)
		            		.build();

		String accessToken = authenticateWithUserCredentials(url, application.getUsername(), application.getPassword());

		return accessToken;
	}

	private String authenticateWithUserCredentials(String authUrl, String username, String password) throws ApiException {
		if (authorizationCode == null) {
			authorizationCode = getAuthorizationCodeFromServer(authUrl, username, password);
		}
		return getAccessTokenFromServer(buildAccessTokenHttpMethod(new NameValuePair(CODE, authorizationCode),
																   new NameValuePair(REDIRECT_URI, redirect_uri),
																   new NameValuePair(GRANT_TYPE, "authorization_code")));
	}
	
	protected abstract String getAuthorizationCodeFromServer(String authUrl, String username, String password) throws ApiException;

	protected abstract String getAccessTokenFromServer(final HttpMethodBase method) throws ApiException;

	public boolean refreshToken() throws ApiException {
		checkArgument(!Strings.isNullOrEmpty(this.refreshToken), "refresh_token is not set.");
		accessToken = null;
		accessToken = getAccessTokenFromServer(buildAccessTokenHttpMethod(new NameValuePair(GRANT_TYPE, REFRESH_TOKEN),
																		  new NameValuePair(REFRESH_TOKEN, this.refreshToken)));
		return isAuthenticated();
	}

	private HttpMethodBase buildAccessTokenHttpMethod(final NameValuePair... params) {
		final PostMethod method = new PostMethod(getTokenServerUrl());
		method.setRequestHeader(ACCEPT, APPLICATION_JSON);
		method.addParameter("client_secret", clientSecret);
		method.addParameter("client_id", clientId);
		for (NameValuePair param:params) {
			method.addParameter(param.getName(), param.getValue());
		}
		return method;
	}

	protected void handleApiException(Exception ex) {
		LOG.info("Exception while communicating with the Roambi API: " + ex.getLocalizedMessage());
	}

	public String getBaseServiceUrl() {
		return baseServiceUrl;
	}

	public String getAuthorizeServerUrl() {
		return baseServiceUrl + apiVersion + "/" + AUTHORIZE_ENDPOINT; 
	}

	public String getTokenServerUrl() {
		return baseServiceUrl + apiVersion + "/" + TOKEN_ENDPOINT; 
	}


	protected String normalizeServiceUrl(String serviceUrl) {
		if (serviceUrl.endsWith("/")) {
			return serviceUrl;
		}
		else {
			return serviceUrl + "/";
		}
	}

	protected String serviceHost() {
		return this.serviceUri.getHost();
	}
	
	protected int servicePort() {
		return this.serviceUri.getPort();
	}
	
	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}


	protected abstract class ApiInvocationHandler {
		protected HttpMethodBase method = null;

		public ApiInvocationHandler(HttpMethodBase method) {
			this.method = method;
		}

		public abstract Object onSuccess() throws HttpException, IOException;
		
		public Object invokeApi() throws ApiException, IOException {
            boolean neverRefreshedToken = true;
			int tries = 0;
			try {
				do {
					tries++;
					if (LOG.isDebugEnabled() && retries > 0) {
						LOG.debug(String.format("invokeApi: tries=%d, retries=%d", tries, retries));
					}
					try {
						return invoke();
					} catch (HttpException e) {
						LOG.error(logError(tries, e));
						if (tries > retries) {
							throw e;
						}						
					} catch (IOException e) {
						LOG.error(logError(tries, e));
						if (tries > retries) {
							throw e;
						}
					} catch (ApiException e ) {
						
                        if ("invalid_token".equals(e.getCode()) && neverRefreshedToken) {
                            neverRefreshedToken = false;
                            tries--; // don't count the last attempt
                            refreshToken();
                            setAuthorizationHeader(method, getAccessToken());
                        } else if (e.getStatus() == 503) {
                        	LOG.error(logError(tries, e));
                        	if (tries > retries) {
                        		throw e;
                        	}
                        } else if (e.getStatus() == 504) {
                        	LOG.error(logError(tries, e));
                        	if (tries > retries) {
                        		throw e;
                        	}
                        } else {
                            throw e;
                        }
                    }
					try {
						Thread.sleep(tries * 1000);	// TODO: might need to change how long thread sleeps
					} catch (InterruptedException e) {
						LOG.warn(e.getMessage());
					}
				} while (tries <= retries);
			} finally {
				if (method != null) {
					method.releaseConnection();
				}
			}
			return null;	// this line of code is not reachable. eclipse is so stupid
		}
		
		private Object invoke() throws HttpException, IOException, ApiException {
			httpClient.executeMethod(method);
			if (method.getStatusCode() == 200) {
				return onSuccess();
			}
			else if (method.getStatusCode() == 202) {
				return ApiJob.fromApiResponse(method.getResponseBodyAsStream());
			}
            else if (isRateLimitExceeded(method)) {
                throw new ApiException(403, "Forbidden", "Api Rate Limit Exceeded");
            }
			else {
				throw ApiException.fromApiResponse(method.getStatusCode(), method.getResponseBodyAsStream());
			}
		}

		private String logError(int tries, final Exception e) {
			return String.format("tries=%d, retries=%d, %s", tries, retries, e.getMessage());
		}
	}
	
	protected boolean isRateLimitExceeded(final HttpMethodBase method) {
		// api endpoint also throw 403 error which is not api rate limit exceeded error. So if response content-type is json, then we take error message from the body instead
		/* 
		 * when api requests exceed max limit the server response is
		 * 403
		 * Content-Type: text/plain;charset=utf-8
		 * response body: 403 Forbidden (Rate Limit Exceeded)
		 */
		boolean result = (method.getStatusCode() == 403);
		if (result) {
			final Header header = method.getResponseHeader("Content-Type");
			if (header != null) {
				final String value = header.getValue();
				if (value != null && value.indexOf(APPLICATION_JSON) > -1) {
					result = false;
				}
				else if (LOG.isDebugEnabled()) {
					LOG.debug("Server return 403 status code .... ");
					LOG.debug("Content-Type: " + value);
					try {
						LOG.debug("Body: " + method.getResponseBodyAsString());
					} catch (IOException e) {
						LOG.debug(e.getMessage());
					}
				}
			}
		}
		return result;
	}
}
