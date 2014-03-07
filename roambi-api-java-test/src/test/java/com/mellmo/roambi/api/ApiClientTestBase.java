package com.mellmo.roambi.api;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.Portal;
import com.mellmo.roambi.api.model.User;

public class ApiClientTestBase implements RoambiApiApplication {

	public static final String API_TEST_ENVIRONMENT = "API_TEST_ENVIRONMENT";
	// default to production environment
	public static final String DEFAULT_API_TEST_ENVIRONMENT = "business-test";
	public static final int API_VERSION = 1;

	protected static transient Properties props = new Properties();
	protected Logger log = Logger.getLogger(ApiClientTestBase.class);
	protected static transient RoambiApiClient client = null;
	protected static transient User currentUser = null;
	
	@BeforeClass
	public static void initializeApiClient() throws IOException, ApiException {
		props.load(ApiClientTestBase.class.getResourceAsStream("/roambi-api-test.properties"));
		client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey(), consumerSecret(), getRedirectURI(), new ApiClientTestBase());
		currentUser = client.currentUser();	// authenticate
		client.setCurrentAccount(getAccount());
	}

//	@Before
//	public void initializeClient() throws IOException, URISyntaxException {
//		props.load(ApiClientTestBase.class.getResourceAsStream("/roambi-api-test.properties"));
//		client = new RoambiApiClient(serverUrl(), API_VERSION, consumerKey(), consumerSecret(), getRedirectURI(), this);
//	}
//
	@Override
	public String getUsername() {
		return props.getProperty(serverEnvironment() + ".username");
	}

	@Override
	public String getPassword() {
		return props.getProperty(serverEnvironment() + ".password");
	}

	protected static String serverUrl() {
		return props.getProperty(serverEnvironment() + ".server.url");
	}
	
	protected static String consumerKey() {
		return props.getProperty(serverEnvironment() + ".consumer.key");
	}
	
	protected static String consumerSecret() {
		return props.getProperty(serverEnvironment() + ".consumer.secret");
	}

    protected static String getAccount() {
        return props.getProperty(serverEnvironment() + ".account");
    }

    protected static String getRedirectURI() {
        return props.getProperty(serverEnvironment() + ".redirect.uri");
    }

    protected String getSaveTemplateUID() {
        return props.getProperty(serverEnvironment() + ".save.template_uid");
    }

    protected String getSaveSourceFileUID() {
        return props.getProperty(serverEnvironment() + ".save.source_file_uid");
    }

    protected String getDirectoryUID() {
        return props.getProperty(serverEnvironment() + ".directory_uid");
    }

    protected String getPermissionsFileUID() {
        return props.getProperty(serverEnvironment() + ".permissions.file_uid");
    }

    protected String getPermissionsUserUID() {
        return props.getProperty(serverEnvironment() + ".permissions.user_uid");
    }
    protected String getPermissionsUser2UID() {
        return props.getProperty(serverEnvironment() + ".permissions.user2_uid");
    }

    protected String getPermissionsGroupUID() {
        return props.getProperty(serverEnvironment() + ".permissions.group_uid");
    }

    protected String getPermissionsGroup2UID() {
        return props.getProperty(serverEnvironment() + ".permissions.group2_uid");
    }

    protected String getInfoFileName() {
        return props.getProperty(serverEnvironment() + ".info.filename");
    }
    protected String getInfoFileType() {
        return props.getProperty(serverEnvironment() + ".info.filetype");
    }
    protected long getInfoFileSize() {
        return Long.parseLong(props.getProperty(serverEnvironment() + ".info.filesize"));
    }
    protected String getInfoFileUID() {
        return props.getProperty(serverEnvironment() + ".info.file_uid");
    }
    protected String getInfoDirectoryUID() {
        return props.getProperty(serverEnvironment() + ".info.directory_uid");
    }

	protected static String serverEnvironment() {
		if (System.getenv(API_TEST_ENVIRONMENT) != null) {
			return System.getenv(API_TEST_ENVIRONMENT);
		}
		else {
			return DEFAULT_API_TEST_ENVIRONMENT;
		}
	}

    protected ContentItem findFile(String directory_uid, ContentItem fileItem) throws ApiException {
        ContentItem foundItem = null;
        List<ContentItem> directoryListing = client.getPortalContents("rfs", directory_uid);
        for(ContentItem content:directoryListing) {
            if( content.getName().equals(fileItem.getName()) || content.getUid().equals(fileItem.getUid()) ) {
                System.out.println("File found in destination directory");
                assertNull("There should only be one item with the same name", foundItem);
                foundItem = content;
            }
        }
        return foundItem;
    }
    
    protected ContentItem getFirstWriteableFolder() throws ApiException {
    	final List<Portal> portals = client.getPortals();
		assertTrue(portals.size() > 0);
		final String portalUid = portals.get(0).getUid();
		return getFirstWriteableFolder(portalUid);
    }
    
    protected ContentItem getFirstWriteableFolder(final String portalUid) throws ApiException {
    	final List<ContentItem> items = client.getPortalContents(portalUid, null);
		assertTrue(items.size() > 0);
		ContentItem folderItem = null;
		for (ContentItem item:items) {
			if ("FOLDER".equals(item.getType()) && !item.getReadOnly()) {
				folderItem = item;
				break;
			}
		}
		return folderItem;
    }

    protected String getTimeStampedName(String rootName, String extension) {
        final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String name = rootName+"-"+formatter.format(new Date());
        if(!extension.isEmpty()) {
            name = name + "." + extension;
        }
        return name;
    }
    
    protected String getRandomEmail() {
    	final StringBuilder builder = new StringBuilder(RandomStringUtils.randomAlphanumeric(10)).append('@').append(RandomStringUtils.randomAlphabetic(6)).append(".com");
    	return builder.toString();
    }
}
