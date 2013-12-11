/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import org.junit.Assert;
import org.junit.Test;

import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.RoambiFilePermission;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 7/31/13
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderResourcesTest extends ApiClientTestBase {
    @Test
    public void testCreateAndDeleteFolder() throws ApiException {
        String newFolderName = getTimeStampedName("APICreatedFolder", "");
        ContentItem parentFolder = getFirstWriteableFolder();

        //check that the folder isn't there
        Assert.assertNull(findFile(parentFolder.getUid(), new ContentItem("", newFolderName)));

        //create the folder
        ContentItem newFolderItem = client.createFolder(parentFolder, newFolderName);

        Assert.assertNotNull(findFile(parentFolder.getUid(), new ContentItem("", newFolderName)));

        //delete the folder
        client.deleteFolder(newFolderItem.getUid());

        //check that the folder isn't there
        Assert.assertNull(findFile(parentFolder.getUid(), new ContentItem("", newFolderName)));

    }

    //test overwrite folder fails
    @Test (expected=ApiException.class)
    public void testOverwriteFails() throws ApiException {
        ContentItem parentFolder = getFirstWriteableFolder();

        String newFolderName = getTimeStampedName("APICreatedFolder", "");
        ContentItem newFolderItem = client.createFolder(parentFolder, newFolderName);
        try {
            client.createFolder(parentFolder, newFolderName);
        } finally {
            client.deleteFolder(newFolderItem.getUid());
        }
    }

    //test folder permissions
    @Test
    public void testFolderPermissions() throws ApiException {
        ContentItem parentFolder = getFirstWriteableFolder();
        String newFolderName = getTimeStampedName("APICreatedFolder", "");
        ContentItem newFolderItem = client.createFolder(parentFolder, newFolderName);
        try {
            client.addPermission(newFolderItem, currentUser, RoambiFilePermission.WRITE);
            ContentItem folderInfo = client.getFolderInfo(newFolderItem.getUid());
            Assert.assertEquals("write", folderInfo.getPermissions().getUserAccess(currentUser.getUid()));

            client.addPermission(newFolderItem, currentUser, RoambiFilePermission.READ);
            folderInfo = client.getFolderInfo(newFolderItem.getUid());
            Assert.assertEquals("read", folderInfo.getPermissions().getUserAccess(currentUser.getUid()));

        } finally {
            client.deleteFolder(newFolderItem.getUid());
        }
    }
}
