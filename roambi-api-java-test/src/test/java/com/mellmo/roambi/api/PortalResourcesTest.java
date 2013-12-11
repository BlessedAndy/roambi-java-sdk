/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.Portal;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 5/13/13
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PortalResourcesTest extends ApiClientTestBase {


    @Test
    public void testGetPortals() throws ApiException {
        List<Portal> portals = client.getPortals();
        for(Portal p:portals) {
            System.out.println(p.getTitle() + " : " + p.getUid());
        }

    }

    @Ignore //not implemented yet
    @Test //'rename'
    public void testUpdateFileName() throws ApiException {
        //this stuff needs to come from the properties
        String fileUid ="5191874be4b0e708b1bfdd88";
        String directory_uid = getDirectoryUID();
        String portalUid="rfs";
        String title = "renamed";

        ContentItem item = new ContentItem(fileUid, "");
        item = client.updateFileName(item, portalUid, title);

        ContentItem found = findFile(directory_uid, new ContentItem("", title));
        Assert.assertNotNull("File not found in directory", found);

    }

    @Ignore //not implemented yet
    @Test //'move'
    public void testUpdateFileDirectory() throws ApiException {
        //this stuff needs to come from the properties
        String fileUid ="5191874be4b0e708b1bfdd88";
        String portalUid="rfs";
        String directory_uid = getDirectoryUID();//new directory uid

        ContentItem item = new ContentItem(fileUid, "");
        ContentItem dirItem = new ContentItem(getDirectoryUID(), "");
        client.updateFileDirectory(item, portalUid, dirItem);

        ContentItem found = findFile(directory_uid, new ContentItem(fileUid, ""));
        Assert.assertNotNull("File not found in directory", found);
    }
}
