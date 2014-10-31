/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.RoambiClientUtil.getContentItemUid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;

/**
 * 
 * @author charles
 *
 */
@Parameters(separators = "=", commandDescription = "Set sync of folder(s) in Roambi Repository")
public class SetFolderSyncCommand extends CommandBase {
	
	private static Logger LOG = Logger.getLogger(SetFolderSyncCommand.class);
	private final String commandName = "sync_dir";
	
	@Parameter(names = {"-s", "--sync"}, description = "Enable sync for the folder")
	private boolean sync = false;
	
	@Parameter(names = "--folders", description = "folders to be updated", variableArity = true, required = true)
	private List<String> folders;
	
	@Override
	public String getName() {
		return commandName;
	}
	
	@Override
	public void execute(RoambiApiClient client) throws Exception {
		LOG.info("executing: " + commandName);
		LOG.info("folder: " + folders);
		LOG.info("sync: " + sync);
		client.currentUser();
		flagFoldersSync(client);		
	}
	
	private void flagFoldersSync(final RoambiApiClient client) throws Exception {
		final Set<String> folderUids = getUniqueFolderUids(client);
		final String action = sync ? "Enable" : "Disable";
		for (String folderUid:folderUids) {
			try {
				LOG.info(String.format("%s sync for folder: '%s'", action, folderUid));
				client.setFolderSync(folderUid, sync);
			} catch (Exception e) {
				LOG.error(String.format("%s sync for folder: '%s' failed. %s", action, folderUid, e.getMessage()));
				throw e;
			}
		}
	}
	
	private Set<String> getUniqueFolderUids(final RoambiApiClient client) throws Exception {
		final Set<String> folderUids = new HashSet<String>();
		final Set<String> uniqueFolders = new HashSet<String>(this.folders);
		for (String folder:uniqueFolders) {
			if (!folderUids.contains(folder)) {
				try {
					folderUids.add(getContentItemUid(folder, client));
				} catch (Exception e) {
					LOG.error(String.format("Failed to get folder_uid of path: '%s'. %s", folder, e.getMessage()));
					throw e;
				}
			}
		}
		return folderUids;
	}

	protected boolean isSync() {
		return this.sync;
	}
	
	protected List<String> getFolders() {
		return this.folders;
	}
}