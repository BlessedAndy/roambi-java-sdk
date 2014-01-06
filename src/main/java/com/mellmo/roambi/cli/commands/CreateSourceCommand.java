/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/11/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=", commandDescription = "Upload and create a file in the Roambi Repository")
public class CreateSourceCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(CreateSourceCommand.class);
    private final String commandName = "create";

    @Parameter(names="--file", description="locale file you with to upload")
    private String newFile;

    @Parameter(names="--folder", description="remote folder destination")
    private String parentFolder;

    @Parameter(names="--title", description="title of the new file")
    private String title;

    @Parameter(names="--permission", description="set permissions for new file", variableArity = true, required=false)
    private List<String> permissionIds;

    public CreateSourceCommand(){}

    @Override
    public String getName() {
        return commandName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        logger.info("executing: " + commandName);
        logger.info("file: " + newFile);
        logger.info("folder: " + parentFolder);
        logger.info("title: " + title);
        if(permissionIds !=null) {
            logger.info("permission:" + permissionIds.toString());
        }

        File sourceFile = new File(newFile);
        client.currentUser();
        ContentItem newItem = client.createFile(RoambiClientUtil.getContentItem(parentFolder, client), title, sourceFile);
        if(permissionIds != null && newItem != null) {
            RoambiClientUtil.addPermission(newItem, permissionIds,client);
        }
    }
}
