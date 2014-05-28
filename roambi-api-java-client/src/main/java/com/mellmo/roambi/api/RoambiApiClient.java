/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.Account;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.api.model.IBaseModel;
import com.mellmo.roambi.api.model.PagedList;
import com.mellmo.roambi.api.model.Portal;
import com.mellmo.roambi.api.model.RoambiFilePermission;
import com.mellmo.roambi.api.model.Role;
import com.mellmo.roambi.api.model.User;
import com.mellmo.roambi.api.model.UserAccount;
import com.mellmo.roambi.api.requests.AddPermissionsRequest;
import com.mellmo.roambi.api.requests.RemovePermissionsRequest;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class RoambiApiClient extends RESTClient {
	protected static final String PORTAL_UID = "portalUid";
	protected static final String FOLDERUID = "folderUid";
	protected static final String FILE_UID = "fileUid";
	protected static final String TARGET_FILE = "targetFile";
	protected static final String USER_UID = "userUid";
	protected static final String GROUP_UID = "groupUid";
	public static final String DIRECTORY_UID = "directory_uid";
	public static final String FOLDER_UID = "folder_uid";
	public static final String OVERWRITE = "overwrite";
	public static final String TEMPLATE_UID = "template_uid";
	public static final String TITLE = "title";
	private static final String CODE = "code";
	private static final String GRANT_TYPE = "grant_type";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";
	protected static final String ACCEPT = "Accept";
	public static final String DEFAULT_API_SERVICE_BASE_URL = "https://api.roambi.com/";
	private static final String TOKEN_ENDPOINT = "token";
	private static final String AUTHORIZE_ENDPOINT = "authorize";
	protected static final Logger LOG = Logger.getLogger(RoambiApiClient.class);

	private String clientId;
	private String clientSecret;
	private RoambiApiApplication application;
	private int apiVersion;
	private String baseServiceUrl;
	private URI serviceUri;
	private HttpClient httpClient;
	private String authorizationCode;
	private String accessToken;
	private String refreshToken;
	private User currentUser = null;
	private String currentAccountUid = null;
    private String redirect_uri = "roambi-api://client.roambi.com/authorize";

	public RoambiApiClient(String serviceUrl, int apiVersion, String clientId, String clientSecret, String redirect_uri, RoambiApiApplication app) {
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

    public RoambiApiClient(String serviceUrl, int apiVersion, String clientId, String clientSecret, String redirect_uri, String proxyHost, int proxyPort, RoambiApiApplication app) {
        this(serviceUrl, apiVersion, clientId, clientSecret, redirect_uri, app);
        HostConfiguration config = httpClient.getHostConfiguration();
        config.setProxy(proxyHost, proxyPort);
    }

	public void setCurrentAccount(String accountUid) {
		currentAccountUid = accountUid;
	}
	
	public User currentUser() throws ApiException {
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

	public List<Account> getUserAccounts() throws ApiException {
		String url = RoambiApiResource.USER_RESOURCES.url(baseServiceUrl, apiVersion, null);
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return Account.fromUserResourcesResponse(this.method.getResponseBodyAsString());
			}
		};
		return (List<Account>) handler.invokeApi();
	}
	
	private User getCurrentUser() throws ApiException {
		String url = RoambiApiResource.USER_RESOURCES.url(baseServiceUrl, apiVersion, null);
		return (User) new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromUserResourcesResponse(this.method.getResponseBodyAsString());
			}
		}.invokeApi();
	}
	
    public List<Group> getGroups() throws ApiException {
    	final String url = buildUrl(RoambiApiResource.LIST_GROUPS);
        ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
            public Object onSuccess() throws HttpException, IOException {
                return Group.fromApiResponseToGroups(this.method.getResponseBodyAsString());
            }
        };
        return (List<Group>) handler.invokeApi();
	}
	
	public Group getGroupInfo(final String groupUid) throws ApiException {
		final String url = buildUrl(RoambiApiResource.GROUPS_UID, required(GROUP_UID, groupUid));
		return invokeMethodGetGroupResponse(buildGetMethod(url));
	}
	
	public Group createGroup(final String name) throws ApiException {
		final String url = buildUrl(RoambiApiResource.LIST_GROUPS);
		final HttpMethodBase method = buildPostMethod(url, required(Group.NAME, name));
		return invokeMethodGetGroupResponse(method);
	}
		
	public Group setGroupInfo(final String groupUid, final String name, final String description) throws ApiException {
		return updateGroupInfo(groupUid, required(Group.NAME, name), required(Group.DESCRIPTION, description));
	}

	public Group setGroupName(final String groupUid, final String name) throws ApiException {
		return updateGroupInfo(groupUid, required(Group.NAME, name));
	}
	
	public Group setGroupDescription(final String groupUid, final String description) throws ApiException {
		return updateGroupInfo(groupUid, required(Group.DESCRIPTION, description));
	}
	
	private Group updateGroupInfo(final String groupUid, final NameValuePair... params) throws ApiException {
		final String url = buildUrl(RoambiApiResource.GROUPS_UID_INFO, required(GROUP_UID, groupUid));
		final HttpMethodBase method = buildPutMethod(url, params);
		return invokeMethodGetGroupResponse(method);
	}
	
	public void deleteGroup(final String groupUid) throws ApiException {
		invokeMethodGetDeleteResourceResponse(buildUrl(RoambiApiResource.GROUPS_UID, required(GROUP_UID, groupUid)), "deleteGroup");
	}
	
	public Group addGroupUsers(final String groupUid, final String... users) throws ApiException {
		return invokeMethodGetGroupResponse(buildPostMethod(buildUrl(RoambiApiResource.GROUPS_UID_USERS, required(GROUP_UID, groupUid)), Group.USERS, users));
	}
	
	public Group removeGroupUsers(final String groupUid, final String... users) throws ApiException {
		return invokeMethodGetGroupResponse(buildPostMethod(buildUrl(RoambiApiResource.GROUPS_UID_USERS_REMOVE, required(GROUP_UID, groupUid)), Group.USERS, users));
	}
	
	private Group invokeMethodGetGroupResponse(final HttpMethodBase method) throws ApiException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return Group.fromApiResponseToGroup(this.method.getResponseBodyAsString());
			}
		};
		return (Group) handler.invokeApi();
	}
	
	public void removeGroupUser(final String groupUid, final String userUid) throws ApiException {
		invokeMethodGetDeleteResourceResponse(buildUrl(RoambiApiResource.GROUPS_UID_USERS_UID, required(GROUP_UID, groupUid), required(USER_UID, userUid)), "removeGroupUser");
	}
	
	public void removeUserFromAllGroups(final String userUid) throws ApiException {
		invokeMethodGetDeleteResourceResponse(buildUrl(RoambiApiResource.GROUPS_USERS_UID, required(USER_UID, userUid)), "removeUserFromAllGroups");
	}
	
	public User inviteUser(final String primary_email, final String given_name, final String family_name, final Role role) throws ApiException {
		final HttpMethodBase method = buildPostMethod(buildUrl(RoambiApiResource.LIST_USERS), required(User.PRIMARY_EMAIL, primary_email),
																							  required(User.GIVEN_NAME, given_name),
																							  required(User.FAMILY_NAME, family_name),
																							  required(UserAccount.ROLE, role));
		return invokeMethodGetUserResponse(method);
	}
	
	public User getUserInfo(final String userUid) throws ApiException {
		return invokeMethodGetUserResponse(buildGetMethod(buildUrl(RoambiApiResource.USERS_UID, required(USER_UID, userUid))));
	}
	
	public User setUserRole(final String userUid, final Role role) throws ApiException {
		return updateUser(userUid, required(UserAccount.ROLE, role));
	}
	
	public User setUserRole(final String userUid, final String role) throws ApiException {
		return updateUser(userUid, required(UserAccount.ROLE, Role.getRoleUid(role)));
	}
	
	public User enableUser(final String userUid) throws ApiException {
		return updateUser(userUid, required(UserAccount.ENABLED, true));
	}
	
	public User disableUser(final String userUid) throws ApiException {
		return updateUser(userUid, required(UserAccount.ENABLED, false));
	}
	
	public User updateUser(final String userUid, final Role role, final boolean enabled) throws ApiException {
		return updateUser(userUid, required(UserAccount.ROLE, role), required(UserAccount.ENABLED, enabled));
	}
	
	public User updateUser(final String userUid, final String role, final boolean enabled) throws ApiException {
		return updateUser(userUid, required(UserAccount.ROLE, Role.getRoleUid(role)), required(UserAccount.ENABLED, enabled));
	}
	
    private User updateUser(final String userUid, final NameValuePair... params) throws ApiException {
		return invokeMethodGetUserResponse(buildPutMethod(buildUrl(RoambiApiResource.USERS_UID, required(USER_UID, userUid)), params));
	}

	protected User invokeMethodGetUserResponse(final HttpMethodBase method) throws ApiException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromApiResponseToUser(this.method.getResponseBodyAsString());
			}
		};
		return (User) handler.invokeApi();
	}
	
	public PagedList<User> getUsers() throws ApiException {
		return getUsers(buildGetMethod(buildUrl(RoambiApiResource.LIST_USERS)));
	}
	
	private PagedList<User> getUsers(final HttpMethodBase method) throws ApiException {
		ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromApiListResponse(this.method.getResponseBodyAsString());
			}
		};
		return (PagedList<User>) handler.invokeApi();
	}
	
	public User getUserByEmail(final String email) throws ApiException {
		final PagedList<User> list = getUsers(buildGetMethod(buildUrl(RoambiApiResource.USERS_SEARCH), required("email", email)));
		return list.getResults().size() > 0 ? list.getResults().get(0) : null;
	}
	
	public List<Portal> getPortals() throws ApiException {
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(buildUrl(RoambiApiResource.LIST_PORTALS))) {
			public Object onSuccess() throws HttpException, IOException {
				return Portal.fromApiListResponse(this.method.getResponseBodyAsString());
			}
		};
		return (List<Portal>) handler.invokeApi();
	}

	public List<ContentItem> getPortalContents(String portalUid, String folderUid) throws ApiException {
		return getPortalContents(portalUid, folderUid, null);
	}
	
	public List<ContentItem> getPortalContents(final String portalUid, final String folderUid, final String fileTypes) throws ApiException {
		LOG.debug("Getting contents for portal '" + portalUid + "' and folder '" + folderUid + "' with file_types '" + fileTypes + "'");
		final String url = buildUrl(RoambiApiResource.PORTAL_CONTENTS, required(PORTAL_UID, portalUid));
		final GetMethod method = buildGetMethod(url, removeNull(optional(FOLDER_UID, folderUid), optional("file_types", fileTypes)));
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				LOG.debug("Contents JSON: " + this.method.getResponseBodyAsString());
				return ContentItem.fromApiListResponse(this.method.getResponseBodyAsString());
			}
		};
		return (List<ContentItem>) handler.invokeApi();
	}
	
	public ContentItem createFolder(final ContentItem parentFolder, final String title) throws ApiException {
		final HttpMethodBase method = buildPostMethod(buildUrl(RoambiApiResource.CREATE_FOLDER), removeNull(required(TITLE, title), optional(FOLDER_UID, parentFolder)));
		return invokeMethodGetContentItemApiDetailsResponse(method, true);
	}
	
	public ContentItem createFile(ContentItem parentFolder, String title, File sourceFile) throws FileNotFoundException, ApiException {
		final PostMethod method = buildPostMethod(buildUrl(RoambiApiResource.CREATE_FILE), toPart(required(TITLE, title)), toPart(required(FOLDER_UID, parentFolder)), toPart("upload", sourceFile));
		return invokeMethodGetContentItemApiDetailsResponse(method, false);
	}
	
	public void deleteFile(final String fileUid) throws ApiException {
		final String url = buildUrl(RoambiApiResource.DELETE_FILE, required(FILE_UID, fileUid));
		LOG.debug("fileUid: " + fileUid + " " + invokeMethodGetDeleteResourceResponse(url));
	}
	
	public void deleteFolder(final String folderUid) throws ApiException {
		final String url = buildUrl(RoambiApiResource.DELETE_FOLDER, required(FOLDERUID, folderUid));
		LOG.debug("folderUid: " + folderUid + " " + invokeMethodGetDeleteResourceResponse(url));
	}
	
	private void invokeMethodGetDeleteResourceResponse(final String url, final String func) throws ApiException {
		final String result =  invokeMethodGetDeleteResourceResponse(url);
		if (LOG.isDebugEnabled()) {
			LOG.debug(func + ": " + result);
		}
	}
	
	private String invokeMethodGetDeleteResourceResponse(final String url) throws ApiException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(buildDeleteMethod(url)) {
			@Override
			public Object onSuccess() throws HttpException, IOException {
				return this.method.getResponseBodyAsString();
			}
		};
		final String result = (String) handler.invokeApi();
		return result;
	}

    public ContentItem addPermission(ContentItem contentItem, Group group, RoambiFilePermission permission) throws ApiException {
    	return addPermission(contentItem, asList("group", group), null, permission);
    }
    
    public ContentItem addPermission(ContentItem contentItem, User user, RoambiFilePermission permission) throws ApiException {
    	return addPermission(contentItem, null, asList("user", user), permission);
    }

    public ContentItem addPermission(ContentItem contentItem, List<String> groups, List<String> users, RoambiFilePermission permission) throws ApiException {
    	final String url = buildUrl(contentItem.isFolder()?RoambiApiResource.ADD_FOLDER_PERMISSION:RoambiApiResource.ADD_PERMISSION, required("contentItem uid", contentItem));
		final AddPermissionsRequest request = new AddPermissionsRequest(users, groups, permission);
		final PostMethod method = buildPostMethod(url, getStringRequestEntity(TEXT_JSON, request.toJsonBody()));
		return invokeMethodGetContentItemApiDetailsResponse(method, contentItem.isFolder());
    }
    
	public ContentItem removePermission(ContentItem contentItem, User user) throws ApiException {
		return removePermission(contentItem, null, asList("user", user));
	}

    public ContentItem removePermission(ContentItem contentItem, Group group) throws ApiException {
        return removePermission(contentItem, asList("group", group), null);
    }

    public ContentItem removePermission(ContentItem contentItem, List<String> groups, List<String> users) throws ApiException {
    	final String url = buildUrl(contentItem.isFolder()?RoambiApiResource.REMOVE_FOLDER_PERMISSION:RoambiApiResource.REMOVE_PERMISSION, required("contentItem uid", contentItem));
        final RemovePermissionsRequest request = new RemovePermissionsRequest(users, groups);
        final PostMethod method = buildPostMethod(url, getStringRequestEntity(TEXT_JSON, request.toJsonBody()));
        return invokeMethodGetContentItemApiDetailsResponse(method, contentItem.isFolder());
    }
    
    private static List<String> asList(final String name, final IBaseModel model) {
    	List<String> list = new ArrayList<String>();
    	list.add(required(name + " uid", model).getValue());
    	return list;
    }
    
	public ContentItem updateFileName(ContentItem targetFile, String portalUid, String title) throws ApiException {
		final String url = buildUrl(RoambiApiResource.UPDATE_FILE, required(PORTAL_UID, portalUid), required(TARGET_FILE, targetFile));
        final PostMethod method = buildPostMethod(url, TEXT_JSON, required(TITLE, title));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
	}

    public ContentItem updateFileDirectory(ContentItem targetFile, String portalUid, ContentItem directory) throws ApiException {
    	final String url = buildUrl(RoambiApiResource.UPDATE_FILE, required(PORTAL_UID, portalUid), required(TARGET_FILE, targetFile));
        final PostMethod method = buildPostMethod(url, TEXT_JSON, required(DIRECTORY_UID, directory));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
    }

    public ContentItem updateFileData(final ContentItem targetFile, final InputStream inputStream, final String contentType) throws ApiException {
    	final String url = buildUrl(RoambiApiResource.UPDATE_FILE_DATA, required(TARGET_FILE, targetFile));
        checkArgument(inputStream != null, "inputStream cannot be null.");
        final PostMethod method = buildPostMethod(url, new InputStreamRequestEntity(inputStream, required("contentType", contentType).getValue()));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
    }

    public ContentItem updateFileData(ContentItem targetFile, File sourceFile) throws ApiException, FileNotFoundException {
    	final String url = buildUrl(RoambiApiResource.UPDATE_FILE_DATA, required(TARGET_FILE, targetFile));
        final PostMethod methd = buildPostMethod(url, toPart("upload", sourceFile));
        return invokeMethodGetContentItemApiDetailsResponse(methd, false);
    }

	public ApiJob createAnalyticsFile(ContentItem sourceFile, ContentItem templateFile, ContentItem destinationFolder, String title, boolean overwrite) throws ApiException {
		final HttpMethodBase method = buildPostMethod(buildUrl(RoambiApiResource.CREATE_ANALYTICS_FILE), TEXT_JSON,
													  required(TITLE, title),
													  required("source_file_uid", sourceFile),
													  required(TEMPLATE_UID, templateFile),
													  required(OVERWRITE, overwrite),
													  required(DIRECTORY_UID, destinationFolder));
		return invokeMethodGetApiJobResponse(method);
	}
	
	public ApiJob createAnalyticsFile(final File sourceFile, final ContentItem templateFile, final ContentItem folder, final String title, final boolean overwrite) throws ApiException, FileNotFoundException {
		final PostMethod method = buildPostMethod(buildUrl( RoambiApiResource.CREATE_ANALYTICS_FILE),
												  toPart("publish_options", required(TITLE, title), required(TEMPLATE_UID, templateFile), required(OVERWRITE, overwrite), required(FOLDER_UID, folder)),
												  toPart("source_file", sourceFile));
		return invokeMethodGetApiJobResponse(method);
	}

	private ApiJob invokeMethodGetApiJobResponse(final HttpMethodBase method) throws ApiException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return null;
			}
		};
		return (ApiJob) handler.invokeApi();
	}
	
    public ContentItem getFolderInfo(final String folderUid) throws ApiException {
		return invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(buildUrl(RoambiApiResource.FOLDER_INFO, required(FOLDERUID, folderUid))), true);
	}

	public ContentItem getFileInfo(String fileUid) throws ApiException {
		return invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(buildUrl(RoambiApiResource.FILE_INFO, required(FILE_UID, fileUid))), false);
	}
	
    public ContentItem setFileInfo(String fileUid, ContentItem item) throws ApiException {
        final String url = buildUrl(RoambiApiResource.FILE_INFO, required(FILE_UID, fileUid));
        final PostMethod method = buildPostMethod(url, TEXT_JSON, required(TITLE, item != null ? item.getName() : null));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
    }

    public InputStream downloadFile(final String fileUid) throws ApiException, IOException {
		final HttpMethodBase method = buildGetMethod(buildUrl(RoambiApiResource.DOWNLOAD_FILE, required(FILE_UID, fileUid)));
		try {
			httpClient.executeMethod(method);
			if (method.getStatusCode() == 200) {
				return ResponseUtils.getResponseInputStream(method);
			}
			else {
				throw ApiException.fromApiResponse(method.getStatusCode(), ResponseUtils.getResponseInputStream(method));
			}
		} catch (IOException e) {
			LOG.error(e.getMessage());
            method.releaseConnection();
			throw e;
		}
	}

	public ApiJob getJob(String jobUid) throws ApiException {
		final String url = RoambiApiResource.GET_JOB.url(baseServiceUrl, apiVersion, required("jobUid", jobUid).getValue());
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return ApiJob.fromApiResponse(this.method.getResponseBodyAsStream());
			}
		};
		
		return (ApiJob) handler.invokeApi();
	}
	
	private String buildUrl(final RoambiApiResource apiResource, final NameValuePair... params) {
		return apiResource.url(	baseServiceUrl, apiVersion, getAccountUid(),
								CollectionUtils.collect(Arrays.asList(params),
														new Transformer<NameValuePair, String>() {
															@Override
															public String transform(NameValuePair param) {
																return param.getValue();
															}
														}).toArray(new String[params.length]));
	}
	
	protected String getAccessToken() throws ApiException {
		if (!isAuthenticated()) authenticate();
		return this.accessToken;
	}
	
	private String getAccountUid() {
		checkArgument(!Strings.isNullOrEmpty(currentAccountUid), "Current Account is not set.");
		return currentAccountUid;
	}

	private boolean authenticate() throws ApiException {
		if (accessToken == null) {
			accessToken = getAccessTokenFromServer();

//            //set the default account
//            if(accessToken != null) {
//                List<Account> accounts = getUserAccounts();
//                if (accounts.size() > 0) {
//                    setCurrentAccount(accounts.get(0).getUid());
//                }
//            }
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
																   new NameValuePair("redirect_uri", redirect_uri),
																   new NameValuePair(GRANT_TYPE, "authorization_code")));
	}
	
	private String getAuthorizationCodeFromServer(String authUrl, String username, String password) throws ApiException {

        if (username == null || password == null) {
            throw new IllegalArgumentException("user name and password cannot be null");
        }

        try {
            httpClient.getParams().setAuthenticationPreemptive(true);
            Credentials authCredentials = new UsernamePasswordCredentials(username, password);
            httpClient.getState().setCredentials(new AuthScope(serviceHost(), servicePort(), AuthScope.ANY_REALM), authCredentials);

            PostMethod authPost = null;
            try {
                authPost = new PostMethod(authUrl);
                authPost.setRequestHeader(ACCEPT, APPLICATION_JSON);

                int result = httpClient.executeMethod(authPost);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Auth result: " + result);
                    //for (Header header : authPost.getResponseHeaders()) {
                    //	log.debug(header.getName() + " :: " + header.getValue());
                    //}
                    String responseBody = authPost.getResponseBodyAsString();
                    LOG.debug(responseBody);
                }

                switch(result) {
                    case 302:
                        LOG.debug("Detected redirect response");
                        Header header = authPost.getResponseHeader("Location");
                        if (header.getValue() != null && header.getValue().startsWith(redirect_uri)) {
                            LOG.debug("Handling successful authentication");
                            final URI redirectUri = URI.create(header.getValue());
                            List<org.apache.http.NameValuePair> params = URLEncodedUtils.parse(redirectUri, UTF_8);
                            for (org.apache.http.NameValuePair pair : params) {
                                if (CODE.equals(pair.getName())) {
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
            handleApiException(e);
        } catch (IllegalArgumentException e) {
            handleApiException(e);
        }


		return authorizationCode;
	}
	
	private String getAccessTokenFromServer(final HttpMethodBase method) throws ApiException {
		try {
			final int result = httpClient.executeMethod(method);
			if (result == 400 || result == 401) {
                throw ApiException.fromApiResponse(result, method.getResponseBodyAsStream());
            }
			if (LOG.isDebugEnabled()) {
				LOG.debug("Auth result: " + result);
				for (Header header : method.getResponseHeaders()) {
					LOG.debug(header.getName() + " :: " + header.getValue());
				}
				final String responseBody = method.getResponseBodyAsString();
				LOG.debug(responseBody);
			}
			final JsonObject responseObject = ResponseUtils.responseToObject(method.getResponseBodyAsString());
			refreshToken = responseObject.get(REFRESH_TOKEN).getAsString();
			final String token = responseObject.get(ACCESS_TOKEN).getAsString();
			return token;
		} catch (IOException ex) {
            handleApiException(ex);
            return null;
		} finally {
			method.releaseConnection();
		}
	}

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

	private void handleApiException(Exception ex) {
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


	private String normalizeServiceUrl(String serviceUrl) {
		if (serviceUrl.endsWith("/")) {
			return serviceUrl;
		}
		else {
			return serviceUrl + "/";
		}
	}

	private String serviceHost() {
		return this.serviceUri.getHost();
	}
	
	private int servicePort() {
		return this.serviceUri.getPort();
	}


	private abstract class ApiInvocationHandler {
		protected HttpMethodBase method = null;
		protected Object result = null;

		public ApiInvocationHandler(HttpMethodBase method) {
			this.method = method;
		}

		public abstract Object onSuccess() throws HttpException, IOException;

		public Object invokeApi() throws ApiException {
			try {
				httpClient.executeMethod(method);

				if (method.getStatusCode() == 200) {
					result = onSuccess();
				}
				else if (method.getStatusCode() == 202) {
					result = ApiJob.fromApiResponse(method.getResponseBodyAsStream());
				}
                else if (isRateLimitExceeded(method)) {
                    throw new ApiException(403, "Forbidden", "Api Rate Limit Exceeded");
                }
				else {
					throw ApiException.fromApiResponse(method.getStatusCode(), method.getResponseBodyAsStream());
				}
			} catch (HttpException httpEx) {
				handleApiException(httpEx);
			} catch (IOException ioe) {
				handleApiException(ioe);
			} finally {
				if (method != null) {
					method.releaseConnection();
				}
			}
			return result;
		}

	}
	
	protected ContentItem invokeMethodGetContentItemApiDetailsResponse(final HttpMethodBase method, final boolean isFolder) throws ApiException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				if (isFolder) {
					return ContentItem.fromApiFolderDetailsResponse(this.method.getResponseBodyAsString());
				}
				else {
					return ContentItem.fromApiDetailsResponse(this.method.getResponseBodyAsString());
				}
			}
		};
		return (ContentItem) handler.invokeApi();
	}

	private boolean isRateLimitExceeded(final HttpMethodBase method) {
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
