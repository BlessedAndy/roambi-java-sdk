/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.Group;
import org.apache.log4j.Logger;

public class GroupInfoCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UserUpdateCommand.class);
    private final String commandName = "groupInfo";

    @Parameter(names="--id", description="group id")
    private String groupId;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        Group group = client.getGroupInfo(groupId);
        logger.info(group.toJSON().toString());
    }
}
