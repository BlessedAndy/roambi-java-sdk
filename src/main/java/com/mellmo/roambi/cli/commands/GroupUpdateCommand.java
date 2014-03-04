/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.Group;
import org.apache.log4j.Logger;


public class GroupUpdateCommand extends CommandBase{
    private static Logger logger = Logger.getLogger(GroupUpdateCommand.class);
    private final String commandName = "groupUpdate";

    @Parameter(names="--id", description="group id")
    private String groupId;

    @Parameter(names="--name", description="group name", required=false)
    public String name;

    @Parameter(names="--description", description="group description", required=false)
    public String description;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        Group group = client.setGroupInfo(groupId, name, description);
        logger.info(group.toJSON().toString());
    }
}
