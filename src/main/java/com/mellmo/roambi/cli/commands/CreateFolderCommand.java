/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.RoambiClientUtil.toContentItem;

import java.util.List;

import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ContentItem;
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
    private static Logger logger = Logger.getLogger(CreateFolderCommand.class);
    private final String commandName = "mkdir";

    @Parameter(names="--folder", description="parent folder", required=false)
    private String parentFolder=null;

    @Parameter(names="--title", description="title of the new folder")
    private String title;

    @Parameter(names="--permission", description="set permissions for folder", variableArity = true, required=false)
    private List<String> permissionIds;

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
        }

        client.currentUser();

        try {
        	final ContentItem newFolder = client.createFolder(toContentItem(parentFolder), title);
            if(permissionIds != null && newFolder != null) {
                RoambiClientUtil.addPermission(newFolder, permissionIds, client);
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
