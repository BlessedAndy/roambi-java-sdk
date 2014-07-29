/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.hidden.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.cli.commands.CommandBase;

import org.apache.log4j.Logger;

@Parameters(separators = "=", commandDescription = "Create a new group")
public class GroupCreateCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UserUpdateCommand.class);
    private final String commandName = "groupcreate";

    @Parameter(names="--name", description="new group name")
    private String groupName;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
       Group group = client.createGroup(groupName);
       logger.info(group.toJSON().toString());
    }
}
