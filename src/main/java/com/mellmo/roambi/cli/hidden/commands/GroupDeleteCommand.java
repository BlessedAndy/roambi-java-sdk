/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.hidden.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import com.mellmo.roambi.cli.commands.CommandBase;

import org.apache.log4j.Logger;

@Parameters(separators = "=", commandDescription = "Delete a group")
public class GroupDeleteCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UserUpdateCommand.class);
    private final String commandName = "groupdelete";

    @Parameter(names="--id", description="group id")
    private String groupId;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        client.deleteGroup(RoambiClientUtil.getGroupId(groupId, client));
    }
}
