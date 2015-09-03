/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.RoambiClientUtil.toContentItem;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.RoambiFilePermission;
import com.mellmo.roambi.cli.client.RoambiClientUtil;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 7/30/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=", commandDescription = "Create a folder in the Roambi Repository")
public class CreateFolderCommand extends CommandBase {
    private static Logger logger = LoggerFactory.getLogger(CreateFolderCommand.class);
    private final String commandName = "mkdir";

    @Parameter(names="--folder", description="parent folder", required=false)
    private String parentFolder=null;

    @Parameter(names="--title", description="title of the new folder")
    private String title;

    @Deprecated @Parameter(names="--permission", description="(Deprecated) set permissions for folder", variableArity = true, required=false)
    private List<String> permissionIds;
    
    @Parameter(names="--users", description="set users permissions for folder", variableArity=true, required=false)
	protected List<String> users;
	@Parameter(names="--groups", description="set groups permissions for folder", variableArity=true, required=false)
	protected List<String> groups;

    @Parameter(names="--ignoreFailure", description = "Do not report error when failed.", required = false)
    private boolean ignoreFailure;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        logger.info("executing: " + commandName);
        logger.info("folder: " + parentFolder);
        logger.info("title: " + title);
        if(permissionIds !=null) {
            logger.info("permission:" + permissionIds.toString());
            logger.warn("permission parameter is deprecated, use users and groups parameters instead.");
        }
		if (users != null)	logger.info("users:" + users.toString());
		if (groups != null)	logger.info("groups:" + groups.toString());

        client.currentUser();

        try {
        	final ContentItem newFolder = client.createFolder(toContentItem(parentFolder), title);
        	if (newFolder != null) {
				if (isNotEmpty(users) || isNotEmpty(groups)) {
					client.addPermission(newFolder, groups, users, RoambiFilePermission.WRITE);
				}
				else if (permissionIds != null) {
					RoambiClientUtil.addPermission(newFolder, permissionIds, client);
				}
			}
        } catch(ApiException e) {
            if (ignoreFailure) {
                logger.warn(e.getLocalizedMessage());
            } else {
                throw e;
            }
        }
    }


}
