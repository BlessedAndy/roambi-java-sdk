/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.FolderUidValidator.expectAnyFolder;
import static com.mellmo.roambi.cli.client.FolderUidValidator.isAccountFolder;
import static com.mellmo.roambi.cli.client.FolderUidValidator.isPersonalFolderOwner;
import static com.mellmo.roambi.cli.client.RoambiClientUtil.addPermission;
import static com.mellmo.roambi.cli.client.RoambiClientUtil.findFile;
import static com.mellmo.roambi.cli.client.RoambiClientUtil.getContentItem;
import static com.mellmo.roambi.cli.client.RoambiClientUtil.getContentItemUid;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.User;

/**
 * 
 * @author charles
 *
 */
public abstract class RefreshDocumentCommandBase extends CommandBase {
	protected static final Logger LOG = Logger.getLogger(RefreshDocumentCommandBase.class);
	
	@Parameter(names="--template", description = "template rbi")
	String template;
	@Parameter(names="--folder", description="remote folder destination")
	String destinationFolder;
	@Parameter(names="--title", description="title of the new document")
	String title;
	@Parameter(names="--permission", description="set permissions for new document", variableArity = true, required=false)
	protected List<String> permissionIds;
	boolean overwrite=true;

	protected abstract String getFilelog();
	protected abstract ApiJob clientExecute(final RoambiApiClient client, final ContentItem template, final ContentItem folder)  throws Exception;
	
	@Override public void execute(final RoambiApiClient client) throws Exception {
		LOG.info("executing: " + getName());
		LOG.info(getFilelog());
		LOG.info("template: " + template);
		LOG.info("destinationFolder: " + destinationFolder);
		LOG.info("title: " + title);
		LOG.info("overwrite: " + overwrite);
		if (permissionIds !=null) {
			LOG.info("permission:" + permissionIds.toString());
		}
		
		final User user = client.currentUser();
		final String folderUid = getContentItemUid(expectAnyFolder(destinationFolder), client);
		publish(client, folderUid);
		if (isAccountFolder(folderUid)) 					addPermissions(client, folderUid);
		else if (isPersonalFolderOwner(folderUid, user))	addPermissions(client, user.getUid());
		else {
			LOG.warn("Destination folder is only accessible by personal folder's owner. Skip verify and add permission published file.");
		}
	}
	
	private void addPermissions(final RoambiApiClient client, final String folderUid) throws ApiException, IOException {
		final ContentItem newItem = findFile(folderUid, new ContentItem("", title), client);
		if (permissionIds != null && newItem != null) {
			addPermission(newItem, permissionIds, client);
		}
	}
	
	private void publish(final RoambiApiClient client, final String folderUid) throws Exception {
		ApiJob job = clientExecute(client, getContentItem(template, client), getContentItem(folderUid));
		int tries = 0;
		while(job.getStatus()==ApiJob.JobStatus.PROCESSING) {
			Thread.sleep(job.getRetryAfter() * 1000);
			LOG.debug("checking job...");
			job = client.getJob(job.getUid());
			if (++tries > maxTries) {
				throw new Exception("Reached max tries.  Job aborted.");
			}
		}
		if (job.getException() != null && !"".equals(job.getException())) {
			throw new Exception(job.getException());
		}
	}
}
