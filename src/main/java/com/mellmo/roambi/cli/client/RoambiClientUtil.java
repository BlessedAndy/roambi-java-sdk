/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.exceptions.PortalContentNotFoundException;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.ContentResult;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.api.model.PagedList;
import com.mellmo.roambi.api.model.RoambiFilePermission;
import com.mellmo.roambi.api.model.Role;
import com.mellmo.roambi.api.model.User;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/12/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoambiClientUtil {

    private static final String RFS = "rfs";
    private static Logger log = Logger.getLogger(RoambiClientUtil.class);

    //cheesy check to see if we are a UID or a path
    public static boolean isUIDValue(String value){
        if(value == null || value.startsWith("/") || value.length()!=24) {
            return false;
        }
        return true;

	}
	
	public static boolean isValidUUID(final String uuid) {
		if (uuid == null) {
			return false;
		}
		try {
			return UUID.fromString(uuid) != null;
		} catch (IllegalArgumentException ex) {
			log.trace("UUID is invalid");
		}
		return false;
	}
	
	public static String getContentItemUid(final String item, final RoambiApiClient client) throws ApiException, IOException {
		return StringUtils.startsWith(item, "/") ? client.getItemInfoByPath(item).getUid() : item; 
	}
	

    public static ContentItem getContentItem(String item, RoambiApiClient client) throws Exception {
        if(isUIDValue(item)){
            return new ContentItem(item, "");
        }
        else {
            try {
                ContentResult result = getContentAndFolderByPath(RFS, item, client);
                if (result.getContent() != null) {
                    return result.getContent();
                } else {
                    throw new FileNotFoundException("Can't find item: " + item);
                }
            } catch (PortalContentNotFoundException e) {
                log.error("Portal not found.");
                throw e;

            } catch (FileNotFoundException e) {
                log.error("File not found.");
                throw e;
            }
        }
    }

    private static ContentItem getContentItemFromPath(String path, RoambiApiClient client) {
        String uid="", name="";
        return new ContentItem(uid, name);
    }

    public static List<ContentItem> getPortalContents(String item, RoambiApiClient client) throws IOException, PortalContentNotFoundException, ApiException {
        String folderUid;
        if(isUIDValue(item)){
           folderUid = item;
        } else {
            String destinationPath = item;
            ContentResult result = getContentAndFolderByPath(RFS, destinationPath, client);
            if (result.getContent() == null) {
                return null;
            }
            folderUid = result.getContent().getUid();
        }

        return client.getPortalContents(RFS, folderUid);
    }

    private static ContentResult getContentAndFolderByPath(String portalUid, String destinationPath, RoambiApiClient client) throws PortalContentNotFoundException, IOException {
        if(destinationPath.startsWith("/")) {
            destinationPath = destinationPath.replaceFirst("/", "");
        }
        String[] pathElements = destinationPath.split("/");

        ContentResult result = new ContentResult();
        ContentItem folder = null;
        String fileName = pathElements[pathElements.length - 1];

        try {
            for (int i=0; i<pathElements.length - 1; i++) {
                List<ContentItem> contents = client.getPortalContents(portalUid, folder == null ? null : folder.getUid());

                folder = null;
                for (ContentItem item : contents) {
                    if (item.getName().equals(pathElements[i])) {
                        folder = item;
                    }
                }
                if (folder == null) {
                    throw new PortalContentNotFoundException("The specified path '" + destinationPath + "' could not be found on portal '" + portalUid + "'");
                }
            }
        } catch (ApiException apiEx) {
            log.info("ApiException while finding folder: " + apiEx.getMessage());
        }

        if (pathElements.length > 1 && folder == null) {
            throw new PortalContentNotFoundException("The specified parent folder '" + destinationPath + "' could not be found on portal '" + portalUid + "'");
        }
        else {
            ContentItem file = null;
            log.debug("Finding file '" + fileName + "' " + (folder == null ? "" : " in folder '" + folder.getName() + "'"));
            try {
                List<ContentItem> contents = client.getPortalContents(portalUid, folder == null ? null : folder.getUid());
                for (ContentItem item : contents) {
                    if (item.getName().equals(fileName)) {
                        file = item;
                    }
                }
            } catch (ApiException apiEx) {
                log.info("ApiException while finding file '" + fileName + "': " + apiEx.getMessage());
            }

            result.setContent(file);
            result.setParentFolder(folder);
        }

        return result;
    }

    public static String getUserId(String id, RoambiApiClient client) throws ApiException, IOException {
        PagedList<User> pagedList = client.getUsers();
        List<User> userList = pagedList.getResults();

        for(User user:userList) {
            if(id.equals(user.getPrimaryEmail()) || id.equals(user.getUid())) {
                return user.getUid();
            }
        }
        return id;
    }

    public static List<String> getUserIds(List<String> users, RoambiApiClient client) throws ApiException, IOException {
        PagedList<User> pagedList = client.getUsers();
        List<User> userList = pagedList.getResults();
        List<String> results = new ArrayList<String>();

        //if 'all' don't bother parsing the rest of the list
        if(users.contains("all")) {
            for(User u:userList) {
                results.add(u.getUid());
            }
        }
        else {
           //loop through list and get the client id if it matches the username
            for(String userId:users) {
                if(userId.equals("self")) {
                    results.add(client.currentUser().getUid());
                    continue;
                }
                for(User user:userList) {
                     if(userId.equals(user.getPrimaryEmail()) || userId.equals(user.getUid())) {
                         results.add(user.getUid());
                         break;
                     }
                }
            }
        }

        return results;
    }

    public static String getGroupId(String id, RoambiApiClient client) throws ApiException, IOException {
        List<Group> groupList = client.getGroups();

        for(Group group:groupList) {
            if(id.equals(group.getName()) || id.equals(group.getUid())) {
                return group.getUid();
            }
        }
        return id;
    }

    public static List<String> getGroupIds(List<String> groups, RoambiApiClient client) throws ApiException, IOException {
        List<Group> groupList = client.getGroups();
        List<String> results = new ArrayList<String>();
        for(String groupId:groups) {
            for(Group grp:groupList) {
                if(groupId.equals(grp.getName()) || groupId.equals(grp.getUid())) {
                    results.add(grp.getUid());
                }
            }
        }
        return results;
    }

    public static void addPermission(ContentItem newItem, List<String> permissionIds, RoambiApiClient client) throws ApiException, IOException {
        if (permissionIds.isEmpty()) {
            log.info("No user or group specified. Permission not added.");
            return;
        }
        List<String> userIds = RoambiClientUtil.getUserIds(permissionIds, client);
        List<String> groupIds=null;
        if(!permissionIds.contains("all")) {
            groupIds= RoambiClientUtil.getGroupIds(permissionIds, client);
        }

        if (userIds.isEmpty() && groupIds.isEmpty()) {
            log.info("Specified user and group cannot be found. Permission not added.");
            return;
        }
        client.addPermission(newItem, groupIds, userIds, RoambiFilePermission.WRITE);
    }

    public static ContentItem findFile(String directory_uid, ContentItem fileItem, RoambiApiClient client) throws ApiException, IOException {
        ContentItem foundItem = null;
        List<ContentItem> directoryListing = client.getPortalContents(RFS, directory_uid);
        for(ContentItem content:directoryListing) {
            if( content.getName().equals(fileItem.getName()) || content.getUid().equals(fileItem.getUid()) ) {
                foundItem = content;
            }
        }
        return foundItem;
    }

    public static Role validateRole(String roleId) {
        Role role = Role.VIEWER;
        if("publisher".equalsIgnoreCase(roleId)) {
            role = Role.PUBLISHER;
        } else if("administrator".equalsIgnoreCase(roleId)) {
            role = Role.ADMINISTRATOR;
        }
        return role;
    }
}
