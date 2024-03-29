/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.RoambiClientUtil.addPermission;
import static com.mellmo.roambi.cli.client.RoambiClientUtil.findFile;
import static com.mellmo.roambi.cli.client.RoambiClientUtil.toContentItem;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.RoambiFilePermission;

/**
 * 
 * @author charles
 *
 */
public abstract class RefreshDocumentCommandBase extends CommandBase {
	protected static final Logger LOG = LoggerFactory.getLogger(RefreshDocumentCommandBase.class);
	
	@Parameter(names="--template", description = "template rbi")
	String template;
	@Parameter(names="--folder", description="remote folder destination")
	String destinationFolder;
	@Parameter(names="--title", description="title of the new document")
	String title;
	@Deprecated @Parameter(names="--permission", description="(Deprecated) set permissions for new document", variableArity = true, required=false)
	protected List<String> permissionIds;
	@Parameter(names="--users", description="set users permissions for new document", variableArity=true, required=false)
	protected List<String> users;
	@Parameter(names="--groups", description="set groups permissions for new document", variableArity=true, required=false)
	protected List<String> groups;
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
			LOG.warn("permission parameter is deprecated, use users and groups parameters instead.");
		}
		if (users != null)	LOG.info("users:" + users.toString());
		if (groups != null)	LOG.info("groups:" + groups.toString());
		
		client.currentUser();
		publish(client, destinationFolder);
		addPermissions(client, destinationFolder);
	}
	
	private void addPermissions(final RoambiApiClient client, final String folderUid) throws ApiException, IOException {
		final ContentItem newItem = findFile(folderUid, new ContentItem("", title), client);
		if (newItem != null) {
			if (isNotEmpty(users) || isNotEmpty(groups)) {
				client.addPermission(newItem, groups, users, RoambiFilePermission.WRITE);
			}
			else if (permissionIds != null) {
				addPermission(newItem, permissionIds, client);
			}
		}
	}
	
	private void publish(final RoambiApiClient client, final String folderUid) throws Exception {
		ApiJob job = clientExecute(client, toContentItem(template), toContentItem(folderUid));
		int tries = 0;
		while(job.getStatus()==ApiJob.JobStatus.PROCESSING) {
			Thread.sleep(job.getRetryAfter() * 1000 * (tries / 10 + 1));
			LOG.info("checking job... tries: " + (tries + 1));
			job = client.getJob(job.getUid());
			if (++tries > maxTries) {
				throw new Exception("Aborting job.  Maximum retries reached for job");
			}
		}
		if (isNotBlank(job.getException())) {
			throw new Exception(job.getException());
		}
	}	
}
