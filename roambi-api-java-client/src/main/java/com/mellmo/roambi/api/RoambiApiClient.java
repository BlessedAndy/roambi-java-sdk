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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
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
import com.mellmo.roambi.api.model.PagedList;
import com.mellmo.roambi.api.model.Portal;
import com.mellmo.roambi.api.model.RoambiFilePermission;
import com.mellmo.roambi.api.model.Role;
import com.mellmo.roambi.api.model.User;
import com.mellmo.roambi.api.model.UserAccount;
import com.mellmo.roambi.api.requests.AddPermissionsRequest;
import com.mellmo.roambi.api.requests.AddPermissionsRequest.FilePermission;
import com.mellmo.roambi.api.requests.RemovePermissionsRequest;
import com.mellmo.roambi.api.utils.JsonUtils;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class RoambiApiClient extends RESTClient {
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
        String url = RoambiApiResource.LIST_GROUPS.url(baseServiceUrl, apiVersion, getAccountUid());
        ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
            public Object onSuccess() throws HttpException, IOException {
                return Group.fromApiResponseToGroups(this.method.getResponseBodyAsString());
            }
        };
        return (List<Group>) handler.invokeApi();
	}
	
	public Group getGroupInfo(final String groupUid) throws ApiException {
		final String url = RoambiApiResource.GROUPS_UID.url(baseServiceUrl, apiVersion, getAccountUid(), groupUid);
		return invokeMethodGetGroupResponse(buildGetMethod(url));
	}
	
	public Group createGroup(final String name) throws ApiException {
		final String url = RoambiApiResource.LIST_GROUPS.url(baseServiceUrl, apiVersion, getAccountUid());
		final HttpMethodBase method = buildPostMethod(url, toParam(Group.NAME, name));
		return invokeMethodGetGroupResponse(method);
	}
		
	public Group setGroupInfo(final String groupUid, final String name, final String description) throws ApiException {
		return updateGroupInfo(groupUid, toParam(Group.NAME, name), toParam(Group.DESCRIPTION, description));
	}

	public Group setGroupName(final String groupUid, final String name) throws ApiException {
		return updateGroupInfo(groupUid, toParam(Group.NAME, name));
	}
	
	public Group setGroupDescription(final String groupUid, final String description) throws ApiException {
		return updateGroupInfo(groupUid, toParam(Group.DESCRIPTION, description));
	}
	
	private Group updateGroupInfo(final String groupUid, final NameValuePair... params) throws ApiException {
		final String url = RoambiApiResource.GROUPS_UID_INFO.url(baseServiceUrl, apiVersion, getAccountUid(), groupUid);
		final HttpMethodBase method = buildPutMethod(url, params);
		return invokeMethodGetGroupResponse(method);
	}
	
	public void deleteGroup(final String groupUid) throws ApiException {
		getDeleteResourceResponse(RoambiApiResource.GROUPS_UID.url(baseServiceUrl, apiVersion, getAccountUid(), groupUid), "deleteGroup");
	}
	
	public Group addGroupUsers(final String groupUid, final String... users) throws ApiException {
		return updateGroupUsers(RoambiApiResource.GROUPS_UID_USERS, groupUid, users);
	}
	
	public Group removeGroupUsers(final String groupUid, final String... users) throws ApiException {
		return updateGroupUsers(RoambiApiResource.GROUPS_UID_USERS_REMOVE, groupUid, users);
	}
	
	private Group updateGroupUsers(final RoambiApiResource endpoint, final String groupUid, final String... users) throws ApiException {
		final String url = endpoint.url(baseServiceUrl, apiVersion, getAccountUid(), groupUid);
		final HttpMethodBase method = RESTClient.buildPostMethod(getAccessToken(), url, APPLICATION_JSON, Group.USERS, users);
		return invokeMethodGetGroupResponse(method);
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
		getDeleteResourceResponse(RoambiApiResource.GROUPS_UID_USERS_UID.url(baseServiceUrl, apiVersion, getAccountUid(), groupUid, userUid), "removeGroupUser");
	}
	
	public void removeUserFromAllGroups(final String userUid) throws ApiException {
		getDeleteResourceResponse(RoambiApiResource.GROUPS_USERS_UID.url(baseServiceUrl, apiVersion, getAccountUid(), userUid), "removeUserFromAllGroups");
	}
	
	public User inviteUser(final String primary_email, final String given_name, final String family_name, final Role role) throws ApiException {
		final String url = RoambiApiResource.LIST_USERS.url(baseServiceUrl, apiVersion, getAccountUid());
		final HttpMethodBase method = buildPostMethod(url, toParam(User.PRIMARY_EMAIL, primary_email),
														   toParam(User.GIVEN_NAME, given_name),
														   toParam(User.FAMILY_NAME, family_name),
														   toParam(UserAccount.ROLE, role));
		return invokeMethodGetUserResponse(method);
	}
	
	public User getUserInfo(final String userUid) throws ApiException {
		final String url = RoambiApiResource.USERS_UID.url(baseServiceUrl, apiVersion, getAccountUid(), userUid);
		return invokeMethodGetUserResponse(buildGetMethod(url));
	}
	
	public User setUserRole(final String userUid, final Role role) throws ApiException {
		return updateUser(userUid, toParam(UserAccount.ROLE, role));
	}
	
	public User enableUser(final String userUid) throws ApiException {
		return updateUser(userUid, toParam(UserAccount.ENABLED, true));
	}
	
	public User disableUser(final String userUid) throws ApiException {
		return updateUser(userUid, toParam(UserAccount.ENABLED, false));
	}
	
	public User updateUser(final String userUid, final Role role, final boolean enabled) throws ApiException {
		return updateUser(userUid, toParam(UserAccount.ROLE, role), toParam(UserAccount.ENABLED, enabled));
	}
	
    private User updateUser(final String userUid, final NameValuePair... params) throws ApiException {
		final String url = RoambiApiResource.USERS_UID.url(baseServiceUrl, apiVersion, getAccountUid(), userUid);
		final HttpMethodBase method = buildPutMethod(url, params);
		return invokeMethodGetUserResponse(method);
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
		String url = RoambiApiResource.LIST_USERS.url(baseServiceUrl, apiVersion, getAccountUid());
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromApiListResponse(this.method.getResponseBodyAsString());
			}
		};
		return (PagedList<User>) handler.invokeApi();
	}
	
	public List<Portal> getPortals() throws ApiException {
		String url = RoambiApiResource.LIST_PORTALS.url(baseServiceUrl, apiVersion, getAccountUid());
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
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
		final String url = RoambiApiResource.PORTAL_CONTENTS.url(baseServiceUrl, apiVersion, getAccountUid(), portalUid);
		final GetMethod getMethod = buildGetMethod(url);
		final List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
		if (folderUid != null) {
			queryParams.add(new NameValuePair(FOLDER_UID, folderUid));
		}
		if (fileTypes != null) {
			queryParams.add(new NameValuePair("file_types", fileTypes));
		}
		if (queryParams.size() > 0) {
			getMethod.setQueryString(queryParams.toArray(new NameValuePair[queryParams.size()]));
		}
		final ApiInvocationHandler handler = new ApiInvocationHandler(getMethod) {
			public Object onSuccess() throws HttpException, IOException {
				LOG.debug("Contents JSON: " + this.method.getResponseBodyAsString());
				return ContentItem.fromApiListResponse(this.method.getResponseBodyAsString());
			}
		};
		return (List<ContentItem>) handler.invokeApi();
	}
	
	public ContentItem createFolder(final ContentItem parentFolder, final String title) throws ApiException {
		final String url = RoambiApiResource.CREATE_FOLDER.url(this.baseServiceUrl,  this.apiVersion, getAccountUid(), new String[0]);
		final PostMethod method = buildApiPostMethod(getAccessToken(), url);
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair(TITLE, title));
		if (parentFolder != null) {
			params.add(new NameValuePair(FOLDER_UID, parentFolder.getUid()));
		}
		final NameValuePair[] data = params.toArray(new NameValuePair[params.size()]);
		method.setRequestBody(data);
		return invokeMethodGetContentItemApiDetailsResponse(method, true);
	}
	
	public ContentItem createFile(ContentItem parentFolder, String title, File sourceFile) throws FileNotFoundException, ApiException {
        checkArgument( parentFolder != null, "parentFolder cannot be null");
        checkArgument(! Strings.isNullOrEmpty(parentFolder.getUid()), "parentFolder UID cannot be null");
        checkArgument(! Strings.isNullOrEmpty(title), "Title cannot be null");
        checkArgument(sourceFile != null, "sourceFile cannot be null");
		String url = RoambiApiResource.CREATE_FILE.url(baseServiceUrl, apiVersion, getAccountUid());
		PostMethod fileUpload = buildApiPostMethod(getAccessToken(), url);
		Part[] parts = {
				new StringPart(TITLE, title),
				new StringPart(FOLDER_UID, parentFolder.getUid()),
				new FilePart("upload", sourceFile, contentTypeForFile(sourceFile), null)
		};
		MultipartRequestEntity multipartEntity = new MultipartRequestEntity(parts, fileUpload.getParams());
		fileUpload.setRequestEntity(multipartEntity);
		return invokeMethodGetContentItemApiDetailsResponse(fileUpload, false);
	}
	
	public void deleteFile(final String fileUid) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(fileUid), "FileUid is not set.");
		final String url = RoambiApiResource.DELETE_FILE.url(baseServiceUrl, apiVersion, getAccountUid(), fileUid);
		LOG.debug("fileUid: " + fileUid + " " + getDeleteResourceResponse(url));
	}
	
	public void deleteFolder(final String folderUid) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(folderUid), "FolderUid is not set.");
		final String url = RoambiApiResource.DELETE_FOLDER.url(baseServiceUrl, apiVersion, getAccountUid(), folderUid);
		LOG.debug("folderUid: " + folderUid + " " + getDeleteResourceResponse(url));
	}
	
	protected void getDeleteResourceResponse(final String url, final String func) throws ApiException {
		final String result =  getDeleteResourceResponse(url);
		if (LOG.isDebugEnabled()) {
			LOG.debug(func + ": " + result);
		}
	}
	
	protected String getDeleteResourceResponse(final String url) throws ApiException {
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
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(group.getUid()), "Group uid is not set.");
        List<String> groups = new ArrayList<String>();
        groups.add(group.getUid());
        return addPermission(contentItem, groups, null, permission);
    }

    public ContentItem addPermission(ContentItem contentItem, User user, RoambiFilePermission permission) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(user.getUid()), "User uid is not set.");
        List<String> users = new ArrayList<String>();
        users.add(user.getUid());
        return addPermission(contentItem, null, users, permission);
    }

    public ContentItem addPermission(ContentItem contentItem, List<String> groups, List<String> users, RoambiFilePermission permission) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
    	final String url = getAddPermissionUrl(contentItem);
		final PostMethod publishMethod = buildApiPostMethod(getAccessToken(), url);
		final AddPermissionsRequest request = new AddPermissionsRequest();
        final List<FilePermission> groupsPermission = new ArrayList<FilePermission>();
        if (groups !=null ) {
            for(String group:groups) {
                groupsPermission.add(request.new FilePermission(group, permission));
            }
        }
        request.setGroups(groupsPermission);
        final List<FilePermission> usersPermission = new ArrayList<FilePermission>();
        if (users != null) {
            for (String user:users) {
                usersPermission.add(request.new FilePermission(user, permission));
            }
        }
        request.setUsers(usersPermission);
		setRequesStringEntity(publishMethod, request.toJsonBody());
		return invokeMethodGetContentItemApiDetailsResponse(publishMethod, contentItem.isFolder());
    }
    
    protected String getAddPermissionUrl(final ContentItem contentItem) {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
		if (contentItem.isFolder()) {
            return RoambiApiResource.ADD_FOLDER_PERMISSION.url(baseServiceUrl, apiVersion, getAccountUid(), contentItem.getUid());
        }
		else {
            return RoambiApiResource.ADD_PERMISSION.url(baseServiceUrl, apiVersion, getAccountUid(), contentItem.getUid());
        }
	}

	public ContentItem removePermission(ContentItem contentItem, User user) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(user.getUid()), "User uid is not set.");
        List<String> users = new ArrayList<String>();
        users.add(user.getUid());
        return removePermission(contentItem, new ArrayList<String>(), users);
	}

    public ContentItem removePermission(ContentItem contentItem, Group group) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(group.getUid()), "Group uid is not set.");
        List<String> groups = new ArrayList<String>();
        groups.add(group.getUid());
        return removePermission(contentItem, groups, new ArrayList<String>());
    }

    public ContentItem removePermission(ContentItem contentItem, List<String> groups, List<String> users) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
    	final String url = getRemovePermissionUrl(contentItem);
        final PostMethod publishMethod = buildApiPostMethod(getAccessToken(), url);
        final RemovePermissionsRequest request = new RemovePermissionsRequest(users, groups);
        setRequesStringEntity(publishMethod, request.toJsonBody());
        return invokeMethodGetContentItemApiDetailsResponse(publishMethod, contentItem.isFolder());
    }
    
    protected String getRemovePermissionUrl(final ContentItem contentItem) {
        checkArgument(!Strings.isNullOrEmpty(contentItem.getUid()), "ContentItem uid is not set.");
		if (contentItem.isFolder()) {
            return RoambiApiResource.REMOVE_FOLDER_PERMISSION.url(baseServiceUrl, apiVersion, getAccountUid(), contentItem.getUid());
        }
		else {
            return RoambiApiResource.REMOVE_PERMISSION.url(baseServiceUrl, apiVersion, getAccountUid(), contentItem.getUid());
        }
	}

	public ContentItem updateFileName(ContentItem targetFile, String portalUid, String title) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(targetFile.getUid()), "TargetFile uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(portalUid), "PortalUid is not set.");
        checkArgument(!Strings.isNullOrEmpty(title), "Title is not set.");

		String url = RoambiApiResource.UPDATE_FILE.url(baseServiceUrl, apiVersion, getAccountUid(), portalUid, targetFile.getUid());

		PostMethod fileUpdateMethod = buildApiPostMethod(getAccessToken(), url);
        NameValuePair[] params = new NameValuePair[] {
                new NameValuePair(TITLE, title)
        };
        setRequesStringEntity(fileUpdateMethod, JsonUtils.createJsonFromParameters(params));
        return invokeMethodGetContentItemApiDetailsResponse(fileUpdateMethod, false);
	}

    public ContentItem updateFileDirectory(ContentItem targetFile, String portalUid, ContentItem directory) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(targetFile.getUid()), "TargetFile uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(directory.getUid()), "directory uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(portalUid), "PortalUid uid is not set.");

        String url = RoambiApiResource.UPDATE_FILE.url(baseServiceUrl, apiVersion, getAccountUid(), portalUid, targetFile.getUid());

        PostMethod fileUpdateMethod = buildApiPostMethod(getAccessToken(), url);
        NameValuePair[] params = new NameValuePair[] {
                new NameValuePair(DIRECTORY_UID, directory.getUid())
        };
        setRequesStringEntity(fileUpdateMethod, JsonUtils.createJsonFromParameters(params));
        return invokeMethodGetContentItemApiDetailsResponse(fileUpdateMethod, false);
    }


    public ContentItem updateFileData(ContentItem targetFile, InputStream inputStream, String contentType) throws ApiException, FileNotFoundException {
        checkArgument(!Strings.isNullOrEmpty(targetFile.getUid()), "TargetFile uid is not set.");

        String url = RoambiApiResource.UPDATE_FILE_DATA.url(baseServiceUrl, apiVersion, getAccountUid(), targetFile.getUid());
        PostMethod fileUpdateMethod = buildApiPostMethod(getAccessToken(), url);

        RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, contentType);
        fileUpdateMethod.setRequestEntity(requestEntity);
        return invokeMethodGetContentItemApiDetailsResponse(fileUpdateMethod, false);
    }

    public ContentItem updateFileData(ContentItem targetFile, File sourceFile) throws ApiException, FileNotFoundException {
        checkArgument(!Strings.isNullOrEmpty(targetFile.getUid()), "TargetFile uid is not set.");

        String url = RoambiApiResource.UPDATE_FILE_DATA.url(baseServiceUrl, apiVersion, getAccountUid(), targetFile.getUid());
        PostMethod fileUpdateMethod = buildApiPostMethod(getAccessToken(), url);
        Part[] parts = {
                new FilePart("new_file", sourceFile, contentTypeForFile(sourceFile), null),
                new FilePart("upload", sourceFile, contentTypeForFile(sourceFile), null)
        };
        MultipartRequestEntity multipartEntity = new MultipartRequestEntity(parts, fileUpdateMethod.getParams());
        fileUpdateMethod.setRequestEntity(multipartEntity);
        return invokeMethodGetContentItemApiDetailsResponse(fileUpdateMethod, false);
    }

	public ApiJob createAnalyticsFile(ContentItem sourceFile, ContentItem templateFile, ContentItem destinationFolder, String title, boolean overwrite) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(sourceFile.getUid()), "SourceFile uid is not set.");
        checkArgument(!Strings.isNullOrEmpty(templateFile.getUid()), "TemplateFile uid is not set.");

		String url = RoambiApiResource.CREATE_ANALYTICS_FILE.url(baseServiceUrl, apiVersion, getAccountUid());
		PostMethod publishMethod = buildApiPostMethod(getAccessToken(), url);
		NameValuePair[] params = new NameValuePair[] {
			new NameValuePair(TITLE, title),
			new NameValuePair("source_file_uid", sourceFile.getUid()),
			new NameValuePair(TEMPLATE_UID, templateFile.getUid()),
			new NameValuePair(OVERWRITE, Boolean.toString(overwrite)),
			new NameValuePair(DIRECTORY_UID, destinationFolder.getUid())
		};
		//publishMethod.setRequestBody(JsonUtils.createJsonFromParameters(params));
		setRequesStringEntity(publishMethod, JsonUtils.createJsonFromParameters(params));
		ApiInvocationHandler handler = new ApiInvocationHandler(publishMethod) {
			public Object onSuccess() throws HttpException, IOException {
				return ContentItem.fromApiDetailsResponse(this.method.getResponseBodyAsString());
			}
		};
		return (ApiJob) handler.invokeApi();
	}
	
	public ApiJob createAnalyticsFile(final File sourceFile, final ContentItem templateFile, final ContentItem folder, final String title, final boolean overwrite) throws ApiException, FileNotFoundException {
		checkArgument((templateFile != null && !Strings.isNullOrEmpty(templateFile.getUid())), "templateFile uid is not set.");
		checkArgument((sourceFile != null && sourceFile.exists()), "sourceFile does not exist.");
		checkArgument((folder != null && !Strings.isNullOrEmpty(folder.getUid())), "folderr uid is not set.");
		checkArgument(!Strings.isNullOrEmpty(title), "title is not set.");
		final String url = RoambiApiResource.CREATE_ANALYTICS_FILE.url(baseServiceUrl, apiVersion, getAccountUid());
		final PostMethod method = buildApiPostMethod(getAccessToken(), url);
		final NameValuePair[] params = new NameValuePair[] {
			new NameValuePair(TITLE, title),
			new NameValuePair(TEMPLATE_UID, templateFile.getUid()),
			new NameValuePair(OVERWRITE, Boolean.toString(overwrite)),
			new NameValuePair(FOLDER_UID, folder.getUid())
		};
		final Part[] parts = {
			new StringPart("publish_options", JsonUtils.createJsonFromParameters(params)),
			new FilePart("source_file", sourceFile, contentTypeForFile(sourceFile), null)
		};
		final MultipartRequestEntity multipartEntity = new MultipartRequestEntity(parts, method.getParams());
		method.setRequestEntity(multipartEntity);
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return ContentItem.fromApiDetailsResponse(this.method.getResponseBodyAsString());
			}
		};
		return (ApiJob) handler.invokeApi();
	}
	
    public ContentItem getFolderInfo(final String folderUid) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(folderUid), "FolderUid uid is not set.");

		String url = RoambiApiResource.FOLDER_INFO.url(baseServiceUrl, apiVersion, getAccountUid(), folderUid);
		return invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(url), true);
	}

    public ContentItem getFileInfo(String fileUid) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(fileUid), "FileUid is not set.");
        String url = RoambiApiResource.FILE_INFO.url(baseServiceUrl, apiVersion, getAccountUid(), fileUid);
        return invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(url), false);
    }

    public ContentItem setFileInfo(String fileUid, ContentItem item) throws ApiException {
        checkArgument(!Strings.isNullOrEmpty(fileUid), "FileUid is not set.");
        checkArgument(!Strings.isNullOrEmpty(item.getUid()), "Item uid is not set.");

        String url = RoambiApiResource.FILE_INFO.url(baseServiceUrl, apiVersion, getAccountUid(), fileUid);
        PostMethod postMethod = buildApiPostMethod(getAccessToken(), url);

        NameValuePair[] params = new NameValuePair[] {
                new NameValuePair(TITLE, item.getName())
        };
        setRequesStringEntity(postMethod, JsonUtils.createJsonFromParameters(params));
        return invokeMethodGetContentItemApiDetailsResponse(postMethod, false);
    }

	private void setRequesStringEntity(final EntityEnclosingMethod method, final String entityContent) {
		try {
            final RequestEntity re = new StringRequestEntity(entityContent, TEXT_JSON, UTF_8);
            method.setRequestEntity(re);
        } catch (UnsupportedEncodingException ue) {
            LOG.error("Unable to set request body (encoding not supported): " + ue.getLocalizedMessage());
        }
	}
    
    public InputStream downloadFile(final String fileUid) throws ApiException, IOException {
        checkArgument(!Strings.isNullOrEmpty(fileUid), "FileUid is not set.");
		final String url = RoambiApiResource.DOWNLOAD_FILE.url(baseServiceUrl, apiVersion, getAccountUid(), fileUid);
		final HttpMethodBase method = buildGetMethod(url);
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
        checkArgument(!Strings.isNullOrEmpty(jobUid), "JobUid is not set.");
		String url = RoambiApiResource.GET_JOB.url(baseServiceUrl, apiVersion, jobUid);
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return ApiJob.fromApiResponse(this.method.getResponseBodyAsStream());
			}
		};
		
		return (ApiJob) handler.invokeApi();
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

	private PostMethod buildApiPostMethod(String accessToken, String url) {
		PostMethod postMethod = new PostMethod(url);
		addAuthorizationHeader(postMethod, accessToken);
		return postMethod;
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

	public static String contentTypeForFile(File file) {
		if (file.getName().endsWith(".xls")) {
			return "application/excel";
		}
		else if (file.getName().endsWith(".xlsx")) {
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		}
		else if (file.getName().endsWith(".csv")) {
			return "text/csv";
		}
		else {
			return new MimetypesFileTypeMap().getContentType(file);
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
