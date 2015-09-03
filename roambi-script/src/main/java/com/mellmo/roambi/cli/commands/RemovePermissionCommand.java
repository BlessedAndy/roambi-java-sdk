/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/21/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators="=", commandDescription="remove permissions to a file")
public class RemovePermissionCommand extends CommandBase {
    private static Logger logger = LoggerFactory.getLogger(RemovePermissionCommand.class);
    protected String name = "removePermission";

    @Parameter(names="--target", description="target file")
    private String remoteUid;

    @Parameter(names="--groupIds", variableArity = true, description="group names or ids", required=false)
    private List<String> groupIds;

    @Parameter(names="--userIds", variableArity = true, description = "user emails or ids", required=false)
    private List<String> userIds;


    @Override
    public String getName() {
        return name;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {

        if(groupIds == null) {
            groupIds = new ArrayList<String>();
        }
        if(userIds == null) {
            userIds = new ArrayList<String>();
        }
        logger.info("executing: " + name);
        logger.info("target: " + remoteUid);
        logger.info("users: " + userIds.toString());
        logger.info("groups: " + groupIds.toString());

        client.currentUser();
        if (isNotEmpty(userIds) || isNotEmpty(groupIds)) {
        	client.removePermission(client.getItemInfo(remoteUid), groupIds, userIds);
        }
        else {
            throw new Exception("No groups or users specified.");
        }

    }
}