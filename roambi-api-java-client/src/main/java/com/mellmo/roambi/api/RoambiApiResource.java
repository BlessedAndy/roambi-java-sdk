/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import org.apache.log4j.Logger;


public enum RoambiApiResource {

	AUTHORIZE {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, "authorize");
		}
	},
	ADD_PERMISSION {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, params[0], PERMISSIONS);
		}
	},
	ADD_FOLDER_PERMISSION {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String... params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FOLDERS, params[0], PERMISSIONS);
		}
	},
	CREATE_FOLDER {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String... params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FOLDERS);
		}
	},
	@Deprecated
	CREATE_ROOT_FOLDER {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String... params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FOLDERS);
		}
	},
	CREATE_ANALYTICS_FILE {  //same as publish?
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, ANALYTICS);
		}
	},
	CREATE_FILE {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES);
		}
	},
	DELETE_FILE {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiFilesUrl(baseUrl, apiVersion, accountUid, params[0]);
		}
	},
	FOLDERS_UID {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FOLDERS, params[0]);
		}
	},
	FILE_INFO {
        public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
        	return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, params[0], INFO);
        }
    },
    DOWNLOAD_FILE {
        public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
        	return buildApiFilesUrl(baseUrl, apiVersion, accountUid, params[0]);
        }
    },
	FOLDER_INFO {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FOLDERS, params[0], INFO);
		}
	},
	ITEM_INFO {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, INFO);
		}
	},
    GET_JOB {
        public String url(String baseUrl, int apiVersion, String jobUid, String...params) {
        	return buildApiUrl(baseUrl, apiVersion, "jobs", jobUid);
        }
    },
	LIST_GROUPS {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS);
		}
	},
	GROUPS_UID {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS, params[0]);
		}
	},
	GROUPS_UID_INFO {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS, params[0], INFO);
		}
	},
	GROUPS_UID_USERS {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS, params[0], USERS);
		}
	},
	GROUPS_UID_USERS_REMOVE {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS, params[0], USERS, REMOVE);
		}
	},
	GROUPS_UID_USERS_UID {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS, params[0], USERS, params[1]);
		}
	},
	GROUPS_USERS_UID {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, GROUPS, USERS, params[0]);
		}
	},
	LIST_PORTALS {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, PORTALS);
		}
	},
	LIST_USERS {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, USERS);
		}
	},
	USERS_UID {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, USERS, params[0]);
		}
	},
	USERS_SEARCH {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, USERS, SEARCH);
		}
	},
	USERS_UID_GROUPS_UID {
		public String url(final String baseUrl, final int apiVersion, final String accountUid, final String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, USERS, params[0], GROUPS, params[1]);
		}
	},
	PORTAL_CONTENTS {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, PORTALS,  params[0], "contents");
		}
	},
	@Deprecated
	PUBLISH_FILE {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, ANALYTICS);
		}
	},
	REMOVE_PERMISSION {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, params[0], PERMISSIONS, REMOVE);
		}
	},
	REMOVE_FOLDER_PERMISSION {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FOLDERS, params[0], PERMISSIONS, REMOVE);
		}
	},
	TOKEN {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, "token");
		}
	},
	UPDATE_FILE {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, PORTALS, params[0], FILES, params[1]);
		}
	},
	UPDATE_FILE_DATA {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, params[0], "data");
		}
	},
	USER_RESOURCES {
		public String url(String baseUrl, int apiVersion, String accountUid, String...params) {
			return buildApiUrl(baseUrl, apiVersion, "user", "resources");
		}
	},
	;

	protected static final String SEARCH = "search";
	protected static final String GROUPS = "groups";
	protected static final String REMOVE = "remove";
	protected static final String USERS = "users";
	protected static final String ANALYTICS = "analytics";
	protected static final String PORTALS = "portals";
	protected static final String INFO = "info";
	protected static final String PERMISSIONS = "permissions";
	protected static final String FOLDERS = "folders";
	protected static final String FILES = "files";
	protected static final String ACCOUNTS = "accounts";

	private static final Logger LOG = Logger.getLogger(RoambiApiResource.class);

	public abstract String url(String baseUrl, int apiVersion, String accountUid, String...params);
	
	private static String normalizeServiceUrl(String serviceUrl) {
		if (serviceUrl.endsWith("/")) {
			return serviceUrl;
		}
		else {
			return serviceUrl + "/";
		}
	}
	
	private static String buildApiFilesUrl(final String baseUrl, final int apiVersion, final String accountUid, final String file_uid) {
		return buildApiUrl(baseUrl, apiVersion, ACCOUNTS, accountUid, FILES, file_uid);
	}
	
	private static String buildApiUrl(final String baseUrl, final int apiVersion, final String... paths) {
		final StringBuilder builder = new StringBuilder(normalizeServiceUrl(baseUrl)).append(apiVersion).append('/');
		for (String path:paths) {
			builder.append(path).append('/');
		}
		if (builder.length() > 0) {
			builder.setLength(builder.length() - 1);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Built API url:" + builder.toString());
		}
		return builder.toString();
	}
}
