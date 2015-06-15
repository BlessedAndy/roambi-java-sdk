/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import java.io.File;
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
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.httpclient.AllTrustingSSLProtocolSocketFactory;
import com.mellmo.roambi.api.model.Account;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.Folder;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.mellmo.roambi.api.utils.UidUtils.*;

public class RoambiApiClient extends BaseApiClient {
	protected static final String ID = "id";
	protected static final String PATH = "path";
	protected static final String UPLOAD = "upload";
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
	
	public RoambiApiClient(String serviceUrl, int apiVersion, String clientId, String clientSecret, String redirect_uri, RoambiApiApplication app) {
		super(serviceUrl, apiVersion, clientId, clientSecret, redirect_uri, app);
	}
	
	public RoambiApiClient(String serviceUrl, int apiVersion, String clientId, String clientSecret, String redirect_uri, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword, String proxyUserDomain, RoambiApiApplication app) throws IOException {
		super(serviceUrl, apiVersion, clientId, clientSecret, redirect_uri, proxyHost, proxyPort, proxyUsername, proxyPassword, proxyUserDomain, app);
	}

    /**
     * disables SSL host verification
     *
     * Useful when troubleshooting configuration issues.
     * Not recommended for production use.
     */
    public void disableSSLVerification() {
        ProtocolSocketFactory sslSocketFactory = new AllTrustingSSLProtocolSocketFactory();

        Protocol protocol = new Protocol("https", sslSocketFactory, 443);
        Protocol.registerProtocol("https", protocol);
    }

	public List<Account> getUserAccounts() throws ApiException, IOException {
		String url = RoambiApiResource.USER_RESOURCES.url(baseServiceUrl, apiVersion, null);
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return Account.fromUserResourcesResponse(this.method.getResponseBodyAsStream());
			}
		};
		return (List<Account>) handler.invokeApi();
	}
	
	public User getCurrentUser() throws ApiException, IOException {
		String url = RoambiApiResource.USER_RESOURCES.url(baseServiceUrl, apiVersion, null);
		return (User) new ApiInvocationHandler(buildGetMethod(url)) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromUserResourcesResponse(this.method.getResponseBodyAsStream());
			}
		}.invokeApi();
	}
	
    public List<Group> getGroups() throws ApiException, IOException {
    	final String url = buildUrl(RoambiApiResource.LIST_GROUPS);
        ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(url)) {
            public Object onSuccess() throws HttpException, IOException {
                return Group.fromApiResponseToGroups(this.method.getResponseBodyAsStream());
            }
        };
        return (List<Group>) handler.invokeApi();
	}
	
	public Group getGroupInfo(final String groupUid) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.GROUPS_UID, required(GROUP_UID, groupUid));
		return invokeMethodGetGroupResponse(buildGetMethod(url));
	}
	
	public Group createGroup(final String name) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.LIST_GROUPS);
		final HttpMethodBase method = buildPostMethod(url, required(Group.NAME, name));
		return invokeMethodGetGroupResponse(method);
	}
		
	public Group setGroupInfo(final String groupUid, final String name, final String description) throws ApiException, IOException {
		return updateGroupInfo(groupUid, required(Group.NAME, name), required(Group.DESCRIPTION, description));
	}

	public Group setGroupName(final String groupUid, final String name) throws ApiException, IOException {
		return updateGroupInfo(groupUid, required(Group.NAME, name));
	}
	
	public Group setGroupDescription(final String groupUid, final String description) throws ApiException, IOException {
		return updateGroupInfo(groupUid, required(Group.DESCRIPTION, description));
	}
	
	private Group updateGroupInfo(final String groupUid, final NameValuePair... params) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.GROUPS_UID_INFO, required(GROUP_UID, groupUid));
		final HttpMethodBase method = buildPutMethod(url, params);
		return invokeMethodGetGroupResponse(method);
	}
	
	public void deleteGroup(final String groupUid) throws ApiException, IOException {
		invokeMethodGetDeleteResourceResponse(buildUrl(RoambiApiResource.GROUPS_UID, required(GROUP_UID, groupUid)), "deleteGroup");
	}
	
	public Group addGroupUsers(final String groupUid, final String... users) throws ApiException, IOException {
		return invokeMethodGetGroupResponse(buildPostMethod(buildUrl(RoambiApiResource.GROUPS_UID_USERS, required(GROUP_UID, groupUid)), Group.USERS, users));
	}
	
	public Group removeGroupUsers(final String groupUid, final String... users) throws ApiException, IOException {
		return invokeMethodGetGroupResponse(buildPostMethod(buildUrl(RoambiApiResource.GROUPS_UID_USERS_REMOVE, required(GROUP_UID, groupUid)), Group.USERS, users));
	}
	
	private Group invokeMethodGetGroupResponse(final HttpMethodBase method) throws ApiException, IOException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return Group.fromApiResponseToGroup(this.method.getResponseBodyAsStream());
			}
		};
		return (Group) handler.invokeApi();
	}
	
	public void removeGroupUser(final String groupUid, final String userUid) throws ApiException, IOException {
		invokeMethodGetDeleteResourceResponse(buildUrl(RoambiApiResource.GROUPS_UID_USERS_UID, required(GROUP_UID, groupUid), required(USER_UID, userUid)), "removeGroupUser");
	}
	
	public void removeUserFromAllGroups(final String userUid) throws ApiException, IOException {
		invokeMethodGetDeleteResourceResponse(buildUrl(RoambiApiResource.GROUPS_USERS_UID, required(USER_UID, userUid)), "removeUserFromAllGroups");
	}
	
	public User inviteUser(final String primary_email, final String given_name, final String family_name, final Role role) throws ApiException, IOException {
		final HttpMethodBase method = buildPostMethod(buildUrl(RoambiApiResource.LIST_USERS), required(User.PRIMARY_EMAIL, primary_email),
																							  required(User.GIVEN_NAME, given_name),
																							  required(User.FAMILY_NAME, family_name),
																							  required(UserAccount.ROLE, role));
		return invokeMethodGetUserResponse(method);
	}
	
	public User getUserInfo(final String userUid) throws ApiException, IOException {
		return invokeMethodGetUserResponse(buildGetMethod(buildUrl(RoambiApiResource.USERS_UID, required(USER_UID, userUid))));
	}
	
	public User setUserRole(final String userUid, final Role role) throws ApiException, IOException {
		return updateUser(userUid, required(UserAccount.ROLE, role));
	}
	
	public User setUserRole(final String userUid, final String role) throws ApiException, IOException {
		return updateUser(userUid, required(UserAccount.ROLE, Role.getRoleUid(role)));
	}
	
	public User enableUser(final String userUid) throws ApiException, IOException {
		return updateUser(userUid, required(UserAccount.ENABLED, true));
	}
	
	public User disableUser(final String userUid) throws ApiException, IOException {
		return updateUser(userUid, required(UserAccount.ENABLED, false));
	}
	
	public User updateUser(final String userUid, final Role role, final boolean enabled) throws ApiException, IOException {
		return updateUser(userUid, required(UserAccount.ROLE, role), required(UserAccount.ENABLED, enabled));
	}
	
	public User updateUser(final String userUid, final String role, final boolean enabled) throws ApiException, IOException {
		return updateUser(userUid, required(UserAccount.ROLE, Role.getRoleUid(role)), required(UserAccount.ENABLED, enabled));
	}
	
    private User updateUser(final String userUid, final NameValuePair... params) throws ApiException, IOException {
		return invokeMethodGetUserResponse(buildPutMethod(buildUrl(RoambiApiResource.USERS_UID, required(USER_UID, userUid)), params));
	}

	protected User invokeMethodGetUserResponse(final HttpMethodBase method) throws ApiException, IOException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromApiResponseToUser(this.method.getResponseBodyAsStream());
			}
		};
		return (User) handler.invokeApi();
	}
	
	public PagedList<User> getUsers() throws ApiException, IOException {
		return getUsers(buildGetMethod(buildUrl(RoambiApiResource.LIST_USERS)));
	}
	
	private PagedList<User> getUsers(final HttpMethodBase method) throws ApiException, IOException {
		ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return User.fromApiListResponse(this.method.getResponseBodyAsStream());
			}
		};
		return (PagedList<User>) handler.invokeApi();
	}
	
	public User getUserByEmail(final String email) throws ApiException, IOException {
		final PagedList<User> list = getUsers(buildGetMethod(buildUrl(RoambiApiResource.USERS_SEARCH), required("email", email)));
		return list.getResults().size() > 0 ? list.getResults().get(0) : null;
	}
	
	public List<Portal> getPortals() throws ApiException, IOException {
		ApiInvocationHandler handler = new ApiInvocationHandler(buildGetMethod(buildUrl(RoambiApiResource.LIST_PORTALS))) {
			public Object onSuccess() throws HttpException, IOException {
				return Portal.fromApiListResponse(this.method.getResponseBodyAsStream());
			}
		};
		return (List<Portal>) handler.invokeApi();
	}

	public List<ContentItem> getPortalContents(String portalUid, String folderUid) throws ApiException, IOException {
		return getPortalContents(portalUid, folderUid, null);
	}
	
	public List<ContentItem> getPortalContents(final String portalUid, final String folderUid, final String fileTypes) throws ApiException, IOException {
		LOG.debug("Getting contents for portal '" + portalUid + "' and folder '" + folderUid + "' with file_types '" + fileTypes + "'");
		final String url = buildUrl(RoambiApiResource.PORTAL_CONTENTS, required(PORTAL_UID, portalUid));
		final GetMethod method = buildGetMethod(url, removeNull(optional(FOLDER_UID, folderUid), optional("file_types", fileTypes)));
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
                if (LOG.isDebugEnabled()) {
				    LOG.debug("Contents JSON: " + this.method.getResponseBodyAsString());
                }
				return ContentItem.fromApiListResponse(this.method.getResponseBodyAsStream());
			}
		};
		return (List<ContentItem>) handler.invokeApi();
	}
	
	public ContentItem createFolder(final ContentItem parentFolder, final String title) throws ApiException, IOException {
		final HttpMethodBase method = buildPostMethod(buildUrl(RoambiApiResource.CREATE_FOLDER), removeNull(required(TITLE, title), optional(FOLDER_UID, parentFolder)));
		return invokeMethodGetContentItemApiDetailsResponse(method, true);
	}
	
	public ContentItem createFile(ContentItem parentFolder, String title, File sourceFile) throws ApiException, IOException {
		final PostMethod method = buildPostMethod(buildUrl(RoambiApiResource.CREATE_FILE), toPart(required(TITLE, title)), toPart(required(FOLDER_UID, parentFolder)), toPart(UPLOAD, sourceFile));
		return invokeMethodGetContentItemApiDetailsResponse(method, false);
	}
	
	public void deleteFile(final String fileUid) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.DELETE_FILE, required(FILE_UID, fileUid));
        invokeMethodGetDeleteResourceResponse(url, "deleteFile");
	}
	
	public void deleteFolder(final String folderUid) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.FOLDERS_UID, required(FOLDERUID, folderUid));
        invokeMethodGetDeleteResourceResponse(url, "deleteFolder");
	}

	private void invokeMethodGetDeleteResourceResponse(final String url, final String func) throws ApiException, IOException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(buildDeleteMethod(url)) {
			@Override
			public Object onSuccess() throws HttpException, IOException {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(func + ": " + this.method.getResponseBodyAsString());
                }

                return this.method.getResponseBodyAsStream();
			}
		};
		handler.invokeApi();
		return;
	}

	public Folder enableFolderSync(final String folderUid) throws ApiException, IOException {
		return setFolderSync(folderUid, true);
	}
	
	public Folder disableFolderSync(final String folderUid) throws ApiException, IOException {
		return setFolderSync(folderUid, false);
	}
	
	public Folder setFolderSync(final String folderUid, final boolean sync) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.FOLDERS_UID, required(FOLDERUID, folderUid));
		final HttpMethodBase method = buildPutMethod(url, required(Folder.SYNC, sync));
		return (Folder) invokeMethodGetContentItemApiDetailsResponse(method);
	}
	
	public ContentItem addPublicAccess(ContentItem contentItem) throws ApiException, IOException {
		return addPermission(contentItem, new ArrayList<String>(), new ArrayList<String>(), RoambiFilePermission.PUBLIC);
	}

	public ContentItem addPermission(ContentItem contentItem, Group group, RoambiFilePermission permission) throws ApiException, IOException {
    	return addPermission(contentItem, asList(Group.GROUP, group), null, permission);
    }
    
    public ContentItem addPermission(ContentItem contentItem, User user, RoambiFilePermission permission) throws ApiException, IOException {
    	return addPermission(contentItem, null, asList(User.USER, user), permission);
    }

    public ContentItem addPermission(ContentItem contentItem, List<String> groups, List<String> users, RoambiFilePermission permission) throws ApiException, IOException {
    	final String url = buildUrl(contentItem.isFolder()?RoambiApiResource.ADD_FOLDER_PERMISSION:RoambiApiResource.ADD_PERMISSION, required("contentItem uid", contentItem));
		final AddPermissionsRequest request = new AddPermissionsRequest(users, groups, permission);
		final PostMethod method = buildPostMethod(url, getStringRequestEntity(TEXT_JSON, request.toJsonBody()));
		return invokeMethodGetContentItemApiDetailsResponse(method, contentItem.isFolder());
    }
    
	public ContentItem removePermission(ContentItem contentItem, User user) throws ApiException, IOException {
		return removePermission(contentItem, null, asList(User.USER, user));
	}

    public ContentItem removePermission(ContentItem contentItem, Group group) throws ApiException, IOException {
        return removePermission(contentItem, asList(Group.GROUP, group), null);
    }

    public ContentItem removePermission(ContentItem contentItem, List<String> groups, List<String> users) throws ApiException, IOException {
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
    
    public ContentItem updateFileData(final ContentItem targetFile, final InputStream inputStream, final String contentType) throws ApiException, IOException {
    	final String url = buildUrl(RoambiApiResource.UPDATE_FILE_DATA, required(TARGET_FILE, targetFile));
        checkArgument(inputStream != null, "inputStream cannot be null.");
        final PostMethod method = buildPostMethod(url, new InputStreamRequestEntity(inputStream, required("contentType", contentType).getValue()));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
    }

    public ContentItem updateFileData(ContentItem targetFile, File sourceFile) throws ApiException, IOException {
    	final String url = buildUrl(RoambiApiResource.UPDATE_FILE_DATA, required(TARGET_FILE, targetFile));
        final PostMethod methd = buildPostMethod(url, toPart(UPLOAD, sourceFile));
        return invokeMethodGetContentItemApiDetailsResponse(methd, false);
    }

	public ApiJob createAnalyticsFile(ContentItem sourceFile, ContentItem templateFile, ContentItem destinationFolder, String title, boolean overwrite) throws ApiException, IOException {
		final HttpMethodBase method = buildPostMethod(buildUrl(RoambiApiResource.CREATE_ANALYTICS_FILE), TEXT_JSON,
													  required(TITLE, title),
													  required("source_file_uid", sourceFile),
													  required(TEMPLATE_UID, templateFile),
													  required(OVERWRITE, overwrite),
													  required(DIRECTORY_UID, destinationFolder));
		return invokeMethodGetApiJobResponse(method);
	}
	
	public ApiJob createAnalyticsFile(final File sourceFile, final ContentItem templateFile, final ContentItem folder, final String title, final boolean overwrite) throws ApiException, IOException {
		final PostMethod method = buildPostMethod(buildUrl( RoambiApiResource.CREATE_ANALYTICS_FILE),
												  toPart("publish_options", required(TITLE, title), required(TEMPLATE_UID, templateFile), required(OVERWRITE, overwrite), required(FOLDER_UID, folder)),
												  toPart("source_file", sourceFile));
		return invokeMethodGetApiJobResponse(method);
	}

	private ApiJob invokeMethodGetApiJobResponse(final HttpMethodBase method) throws ApiException, IOException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return null;
			}
		};
		return (ApiJob) handler.invokeApi();
	}
	
    public ContentItem getFolderInfo(final String folderUid) throws ApiException, IOException {
    	return isUid(folderUid) ? invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(buildUrl(RoambiApiResource.FOLDER_INFO, required(FOLDERUID, folderUid))), true) : getItemInfo(folderUid);
	}
    
    public ContentItem getItemInfo(final String id) throws ApiException, IOException {
    	if (isPath(id))		return getItemInfo(PATH, id);
		else 				return getItemInfo(ID, id);
    }
    
    private ContentItem getItemInfo(final String paramName, final String paramValue) throws ApiException, IOException {
    	return invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(buildUrl(RoambiApiResource.ITEM_INFO), required(paramName, paramValue)));
    }

	public ContentItem getFileInfo(String fileUid) throws ApiException, IOException {
		return invokeMethodGetContentItemApiDetailsResponse(buildGetMethod(buildUrl(RoambiApiResource.FILES_UID_INFO, required(FILE_UID, fileUid))), false);
	}
	
    @Deprecated public ContentItem setFileInfo(String fileUid, ContentItem item) throws ApiException, IOException {
        final String url = buildUrl(RoambiApiResource.FILES_UID_INFO, required(FILE_UID, fileUid));
        final PostMethod method = buildPostMethod(url, TEXT_JSON, required(TITLE, item != null ? item.getName() : null));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
    }
    
    public ContentItem setFileInfo(final String fileUid, final String directoryUid, final String title) throws ApiException, IOException {
    	final String url = buildUrl(RoambiApiResource.FILES_UID_INFO, required(FILE_UID, fileUid));
    	final HttpMethodBase method = buildPutMethod(url, required(DIRECTORY_UID, directoryUid), required(TITLE, title));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
    }
    
	public ContentItem setFileTitle(final String fileUid, final String title) throws ApiException, IOException {
		final String url = buildUrl(RoambiApiResource.FILES_UID_INFO, required(FILE_UID, fileUid));
        final PostMethod method = buildPostMethod(url, TEXT_JSON, required(TITLE, title));
        return invokeMethodGetContentItemApiDetailsResponse(method, false);
	}

    public ContentItem moveFile(final String fileUid, final String directoryUid) throws ApiException, IOException {
    	final String url = buildUrl(RoambiApiResource.FILES_UID_INFO, required(FILE_UID, fileUid));
        final PostMethod method = buildPostMethod(url, TEXT_JSON, required(DIRECTORY_UID, directoryUid));
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

	public ApiJob getJob(String jobUid) throws ApiException, IOException {
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
															@Override public String transform(final NameValuePair param) {
																return apiResource.isRfs ? toUid(param.getValue()) : param.getValue();
															}
														}).toArray(new String[params.length]));
	}
	
	private String toUid(final String value) {
		try {
			if (isPath(value))			return getItemInfo(PATH, value).getUid();
			else if (isEmail(value))	return getItemInfo(ID, value).getUid();
		} catch (Exception e) {
			LOG.error("Failed to find content item with " + value);
		}
		return value;
	}

	protected String getAuthorizationCodeFromServer(String authUrl, String username, String password) throws ApiException {

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
                    LOG.debug(authPost.getResponseBodyAsString());
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
	
	protected String getAccessTokenFromServer(final HttpMethodBase method) throws ApiException {
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
				LOG.debug(method.getResponseBodyAsString());
			}
			final JsonObject responseObject = ResponseUtils.responseToObject(method.getResponseBodyAsStream());
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

	protected ContentItem invokeMethodGetContentItemApiDetailsResponse(final HttpMethodBase method, final boolean isFolder) throws ApiException, IOException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				if (isFolder) {
					return ContentItem.fromApiFolderDetailsResponse(this.method.getResponseBodyAsStream());
				}
				else {
					return ContentItem.fromApiFileDetailsResponse(this.method.getResponseBodyAsStream());
				}
			}
		};
		return (ContentItem) handler.invokeApi();
	}
	
	protected ContentItem invokeMethodGetContentItemApiDetailsResponse(final HttpMethodBase method) throws ApiException, IOException {
		final ApiInvocationHandler handler = new ApiInvocationHandler(method) {
			public Object onSuccess() throws HttpException, IOException {
				return ContentItem.fromApiItemDetailsResponse(this.method.getResponseBodyAsStream());
			}
		};
		return (ContentItem) handler.invokeApi();
	}
}
