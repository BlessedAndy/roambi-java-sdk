/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.RoambiClientUtil.toContentItem;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.api.model.RoambiFilePermission;
import com.mellmo.roambi.cli.client.RoambiClientUtil;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/11/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=", commandDescription = "Upload and create a file in the Roambi Repository")
public class CreateSourceCommand extends CommandBase {
    private static Logger logger = LoggerFactory.getLogger(CreateSourceCommand.class);
    private final String commandName = "upload";

    @Parameter(names="--file", description="locale file you with to upload")
    private String newFile;

    @Parameter(names="--folder", description="remote folder destination")
    private String parentFolder;

    @Parameter(names="--title", description="title of the new file")
    private String title;

    @Deprecated @Parameter(names="--permission", description="(Deprecated) set permissions for new file", variableArity = true, required=false)
    private List<String> permissionIds;
    
	@Parameter(names="--users", description="set users permissions for new file", variableArity=true, required=false)
	protected List<String> users;
	@Parameter(names="--groups", description="set groups permissions for new file", variableArity=true, required=false)
	protected List<String> groups;

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
        if (title == null) {
            String []parts = newFile.split(File.separator);
            title = parts[parts.length-1];
        }
        logger.info("title: " + title);
        if(permissionIds !=null) {
            logger.info("permission:" + permissionIds.toString());
            logger.warn("permission parameter is deprecated, use users and groups parameters instead.");
        }
		if (users != null)	logger.info("users:" + users.toString());
		if (groups != null)	logger.info("groups:" + groups.toString());

        final File sourceFile = new File(newFile);
        client.currentUser();
        final ContentItem newItem = client.createFile(toContentItem(parentFolder), title, sourceFile);
		if (newItem != null) {
			if (isNotEmpty(users) || isNotEmpty(groups)) {
				client.addPermission(newItem, groups, users, RoambiFilePermission.WRITE);
			}
			else if (permissionIds != null) {
				RoambiClientUtil.addPermission(newItem, permissionIds, client);
			}
		}
    }
}
