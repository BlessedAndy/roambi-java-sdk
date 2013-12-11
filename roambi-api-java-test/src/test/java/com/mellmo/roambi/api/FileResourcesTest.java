/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.api.model.Portal;
import com.mellmo.roambi.api.model.RoambiFilePermission;
import com.mellmo.roambi.api.model.User;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 5/14/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileResourcesTest extends ApiClientTestBase {

    private static final String API_UPLOADS = "API Uploads";
	private static final String API_UNITTEST_DEST = "api_unittest_dest";
	static int MAX_RETRY = 20;
    static String FILE_READ="read";
    static String FILE_WRITE="write";

	@Test
	public void testDownloadSourceFile() throws ApiException, IOException {
        final String fileUid = getSaveSourceFileUID();
        final ContentItem contentItem = client.getFileInfo(fileUid);
        assertNotNull(contentItem);
        InputStream inputStream = null;
		File tmpFile = null;
		try {
			inputStream = client.downloadFile(fileUid);
			assertNotNull(inputStream);
			tmpFile = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            ByteStreams.copy(inputStream, Files.newOutputStreamSupplier(tmpFile));
			inputStream.close();
			assertEquals(tmpFile.length(), contentItem.getSize());
		} finally {
			Closeables.closeQuietly(inputStream);
			if (tmpFile != null && tmpFile.exists()) {
				tmpFile.delete();
			}
		}
	}

	@Test(expected=ApiException.class)
	public void testDownloadAnalyticsFile() throws ApiException, IOException {
        final String fileUid = getSaveTemplateUID();
        client.downloadFile(fileUid);
	}

	@Test(expected=ApiException.class)	// test that delete file end point cannot be used to delete folder
	public void testDeleteFileOnFolder() throws ApiException {
		final List<Portal> portals = client.getPortals();
		assertTrue(portals.size() > 0);
		final String portalUid = portals.get(0).getUid();
		
		final List<ContentItem> items = client.getPortalContents(portalUid, null);
		assertTrue(items.size() > 0);
		final String folderUid = items.get(0).getUid();
		client.deleteFile(folderUid);
	}

    @Ignore //needs delete endpoint
	@Test
	public void testUploadAndDeleteFile() throws ApiException, IOException {

        //find a parent folder to upload to
        ContentItem folderItem = getFirstWriteableFolder();
		assertNotNull("Unable to find a folder with write permission.", folderItem);

        //upload file
		final String destinationFileName = getTimeStampedName("test", "xlsx");
		final URL url = getClass().getResource("/trends.xlsx");
		final File sourceFile = new File(url.getPath());
		final ContentItem item = client.createFile(new ContentItem(folderItem.getUid(), folderItem.getName()), destinationFileName, sourceFile);
		assertNotNull(item);
		assertNotNull(item.getUid());

		try {
			client.deleteFile(item.getUid());
		} catch (ApiException e) {
			log.error("Failed to delete resource file " + destinationFileName + " in " + folderItem.getName() + " folder.");
			throw e;
		}
	}
	
    @Test
    public void testGetFileInfo() throws ApiException {
        String file_uid= getPermissionsFileUID();
        ContentItem item = client.getFileInfo(file_uid);

        Assert.assertEquals("Incorrect file name", getInfoFileName(), (item.getName()));
        Assert.assertEquals("Incorrect type", getInfoFileType(), (item.getType()));
        Assert.assertEquals("Incorrect size", getInfoFileSize(), item.getSize());
    }

    @Test  //rename
    public void testPutFileInfo() throws ApiException {
        String file_uid=getInfoFileUID();//"5191874be4b0e708b1bfdd88";
        String directory_uid = getInfoDirectoryUID();//"518c2272e4b0acb32dfabe2b";

        ContentItem item = client.getFileInfo(file_uid);

        String newName = java.util.UUID.randomUUID().toString();
        item.setName(newName);
        client.setFileInfo(file_uid, item);
	    Assert.assertNotNull("Rename failed", findFile(directory_uid, new ContentItem("", newName)));
	}
    
    // should enable this test once business-test has the latest code
    @Ignore
	@Test
	public void testCreateAnalyticsFilesUsingMultipartPost() throws Exception {
		final String directory_uid = getDirectoryUID();
		final File sourceFile = new File(getClass().getResource("/trends.xlsx").getPath());
		ApiJob job = client.createAnalyticsFile(sourceFile, new ContentItem(getSaveTemplateUID(), ""), new ContentItem(directory_uid, API_UPLOADS), API_UNITTEST_DEST, true);
		checkCreateAnalyticsFileJobStatus(directory_uid, job);
	}

	protected void checkCreateAnalyticsFileJobStatus(final String directory_uid, ApiJob job) throws InterruptedException, ApiException, Exception {
		int tries = 0;
		while (job.getStatus()==ApiJob.JobStatus.PROCESSING) {
			Thread.sleep(job.getRetryAfter() * 1000);
			System.out.println("checking job...");
			job = client.getJob(job.getUid());
			if (++tries > MAX_RETRY) {
				throw new Exception("Aborting job.  Maximum retries reached for job");
			}
		}
		System.out.println("Job Done.");
		assertNull("Job returns with exception.", job.getException());
		assertEquals("Job status is not complete.", ApiJob.JobStatus.COMPLETE, job.getStatus());
		ContentItem found = findFile(directory_uid, new ContentItem("", API_UNITTEST_DEST));
		Assert.assertNotNull("File not found in destination directory", found);
	}
	
	@Test(expected=ApiException.class)
	public void testReplaceTemplateAnalyticsFile() throws ApiException {
		try {
			client.createAnalyticsFile(	new ContentItem(getSaveSourceFileUID(), ""), new ContentItem(getSaveTemplateUID(), ""),
										new ContentItem("518c229ce4b0acb32dfabe40", "RBI"), "foobar", true);	// Library/RBI/foobar
			fail("Expecting ApiException");
		} catch (ApiException e) {
			assertEquals("content_not_overwritable", e.getCode());
			assertEquals(400, e.getStatus());
			throw e;
		}		
	}
	
	@Test(expected=ApiException.class)
	public void testReplaceExistingAnalyticsFileOverwriteIsFalse() throws ApiException {
		try {
			client.createAnalyticsFile(	new ContentItem(getSaveSourceFileUID(), ""), new ContentItem(getSaveTemplateUID(), ""),
										new ContentItem(getDirectoryUID(), API_UPLOADS), API_UNITTEST_DEST, false);
			fail("Expecting ApiException");
		} catch (ApiException e) {
			assertEquals("content_exists", e.getCode());
			assertEquals(400, e.getStatus());
			throw e;
		}
	}
	
	
    @Test   //YETI-6713
    public void testReplaceExistingAnalyticsFileOverwriteIsTrue() throws Exception {
        String directory_uid = getDirectoryUID();
        ApiJob job = client.createAnalyticsFile(new ContentItem(getSaveSourceFileUID(), ""), new ContentItem(getSaveTemplateUID(), ""),
        										new ContentItem(directory_uid, API_UPLOADS), API_UNITTEST_DEST, true);
        checkCreateAnalyticsFileJobStatus(directory_uid, job);
    }

    @Test      //YETI-6716
    public void testModifyPermissions() throws ApiException {

        String file_uid= getPermissionsFileUID();// "518c26a2e4b0070c8f380691";
        String user_uid= getPermissionsUserUID();//"17feaa6c-0610-48fb-a798-e0ca01d19ff4";

        User user = new User();
        user.setUid(user_uid); //the Publisher user

        //make sure file is there
        ContentItem item = client.getFileInfo(file_uid);
        Assert.assertNotNull("File not found", item);

        //test add read permission
        ContentItem resultItem = client.addPermission(item, user, RoambiFilePermission.READ);
        Assert.assertEquals(FILE_READ, resultItem.getPermissions().getUserAccess(user.getUid()));

        //test remove read permission
        resultItem = client.removePermission(item, user);
        Assert.assertNull(resultItem.getPermissions().getUserAccess(user.getUid()));

        //test add write permission
        resultItem = client.addPermission(item, user, RoambiFilePermission.WRITE);
        Assert.assertEquals(FILE_WRITE,resultItem.getPermissions().getUserAccess(user.getUid()));

        //test remove write permission
        resultItem = client.removePermission(item, user);
        Assert.assertNull(resultItem.getPermissions().getUserAccess(user.getUid()));

    }

    @Test
    public void testGroupPermission() throws ApiException {
        String group_uid = getPermissionsGroupUID();
        String file_uid= getPermissionsFileUID();
        Group group = new Group();
        group.setUid(group_uid); //a group uid

        //make sure file is there
        ContentItem item = client.getFileInfo(file_uid);
        Assert.assertNotNull("File not found", item);


        //test add read permission
        ContentItem resultItem = client.addPermission(item, group, RoambiFilePermission.READ);
        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getGroupAccess(group.getUid()));

        //test remove read permission
        resultItem = client.removePermission(item, group);
        Assert.assertNull(resultItem.getPermissions().getGroupAccess(group.getUid()));

        //test add write permission
        resultItem = client.addPermission(item, group, RoambiFilePermission.WRITE);
        Assert.assertEquals(FILE_WRITE,resultItem.getPermissions().getGroupAccess(group.getUid()));

        //test remove write permission
        resultItem = client.removePermission(item, group);
        Assert.assertNull(resultItem.getPermissions().getUserAccess(group.getUid()));
    }

    @Test //YETI-6740
    public void testUpload() throws ApiException, IOException {
        String directory_uid = getDirectoryUID();
        String directory_name = "";
        final String destinationFileName = getTimeStampedName("trends", "xlsx");

        File sourceFile=null;
        ContentItem found = null;
        try {
            URL url = getClass().getResource("/trends.xlsx");
            sourceFile = new File(url.getPath());
            ContentItem result = client.createFile(new ContentItem(directory_uid, directory_name), destinationFileName, sourceFile);
            client.addPermission(result, currentUser, RoambiFilePermission.READ);

            //check to see if file is there
            found = findFile(directory_uid, new ContentItem("", destinationFileName));

            Assert.assertNotNull("File not found in destination directory", found);
        } catch(IOException e) {
            System.out.println("file not found");
            throw e;
        } catch(ApiException e){
            throw e;
        } finally {
        	if (found != null) {
				client.deleteFile(found.getUid());
				found = findFile(directory_uid, new ContentItem("", destinationFileName));
				Assert.assertNull("File still found in destination directory", found);
        	}
        }
    }


    @Test
    public void testPermissions() throws Exception {
        String group_uid = getPermissionsGroupUID();
        String group2_uid = getPermissionsGroup2UID();
        String user_uid= getPermissionsUserUID();
        String user2_uid = getPermissionsUser2UID();
        String file_uid= getPermissionsFileUID();

        List<String> groups = new ArrayList<String>();
        groups.add(group_uid);
        groups.add(group2_uid);

        List<String> users = new ArrayList<String>();
        users.add(user_uid);
        users.add(user2_uid);


        //make sure file is there
        ContentItem item = client.getFileInfo(file_uid);
        Assert.assertNotNull("File not found", item);
        ContentItem resultItem = null;
        try {
//        	// getFileInfo right now doesn't return permissions
//        	if (item.getPermissions() != null) {
//        		assertNull(item.getPermissions().getGroupAccess(group_uid));
//        		assertNull(item.getPermissions().getGroupAccess(group2_uid));
//            	assertNull(item.getPermissions().getUserAccess(user_uid));
//            	assertNull(item.getPermissions().getUserAccess(user2_uid));
//        	}
        	
	        resultItem = client.addPermission(item, groups, users, RoambiFilePermission.READ);
	
	        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getGroupAccess(group_uid));
	        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getGroupAccess(group2_uid));
	        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getUserAccess(user_uid));
	        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getUserAccess(user2_uid));
	
	        client.addPermission(item, new User(user2_uid), RoambiFilePermission.WRITE);
	        resultItem = client.addPermission(item, new Group(group2_uid, ""), RoambiFilePermission.WRITE);
	
	        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getGroupAccess(group_uid));
	        Assert.assertEquals(FILE_WRITE,resultItem.getPermissions().getGroupAccess(group2_uid));
	        Assert.assertEquals(FILE_READ,resultItem.getPermissions().getUserAccess(user_uid));
	        Assert.assertEquals(FILE_WRITE,resultItem.getPermissions().getUserAccess(user2_uid));
        } finally {
        	resultItem = client.removePermission(item, groups, users);
	        Assert.assertNull(resultItem.getPermissions().getGroupAccess(group_uid));
	        assertNull(resultItem.getPermissions().getGroupAccess(group2_uid));
        	assertNull(resultItem.getPermissions().getUserAccess(user_uid));
        	assertNull(resultItem.getPermissions().getUserAccess(user2_uid));
        }
	}
	
	@Test
	public void testUpdateFileData() throws Exception {
		final String destinationFileName = getTimeStampedName("trends", "xlsx");
		ContentItem targetFile = null;
		try {
			File sourceFile = new File(getClass().getResource("/trends.xlsx").getPath());
			targetFile = client.createFile(new ContentItem(getDirectoryUID(), ""), destinationFileName, sourceFile);
			assertEquals(sourceFile.length(), targetFile.getSize());
			client.addPermission(targetFile, currentUser, RoambiFilePermission.READ);
			File newFile = new File(getClass().getResource("/BookTime.xlsx").getPath());
			client.updateFileData(targetFile, newFile);
			final ContentItem found = client.getFileInfo(targetFile.getUid());
			Assert.assertEquals( newFile.length(), found.getSize());
		} finally {
			if (targetFile != null) {
				client.deleteFile(targetFile.getUid());
			}
		}
	}
	
	@Test
	public void testUpdateFileDataInputStream() throws Exception {
		final String destinationFileName = getTimeStampedName("trends", "xlsx");
		ContentItem targetFile = null;
		try {
			File sourceFile = new File(getClass().getResource("/trends.xlsx").getPath());
			targetFile = client.createFile(new ContentItem(getDirectoryUID(), ""), destinationFileName, sourceFile);
			assertEquals(sourceFile.length(), targetFile.getSize());
			client.addPermission(targetFile, currentUser, RoambiFilePermission.READ);
			File newFile = new File(getClass().getResource("/BookTime.xlsx").getPath());
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(newFile);
				client.updateFileData(targetFile, inputStream, RoambiApiClient.contentTypeForFile(newFile));
				final ContentItem found = client.getFileInfo(targetFile.getUid());
				Assert.assertEquals( newFile.length(), found.getSize());
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		} finally {
			if (targetFile != null) {
				client.deleteFile(targetFile.getUid());
			}
		}
	}
}
