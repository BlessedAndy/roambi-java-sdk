/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.hidden.commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.Group;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import com.mellmo.roambi.cli.commands.CommandBase;

@Parameters(separators = "=", commandDescription = "Remove user from group(s)")
public class GroupRemoveCommand extends CommandBase {
    private static Logger logger = LoggerFactory.getLogger(UserUpdateCommand.class);
    private final String commandName = "groupremove";

    @Parameter(names="--id", description="groupId if removing from a single group.", required=false)
    private String groupId;

    @Parameter(names="--users", variableArity = true, description = "user ids")
    private List<String> users;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {

        List<String> userIds = RoambiClientUtil.getUserIds(users, client);

        if(groupId != null) {
            Group group = client.removeGroupUsers(RoambiClientUtil.getGroupId(groupId, client), userIds.toArray(new String[0]));
            logger.info(group.toJSON().toString());
        } else {
            for(String s:userIds) {
                client.removeUserFromAllGroups(s);
            }
        }
    }
}
