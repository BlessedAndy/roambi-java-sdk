/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.hidden.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.User;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import com.mellmo.roambi.cli.commands.CommandBase;

@Parameters(separators = "=", commandDescription = "Update a user")
public class UserUpdateCommand extends CommandBase{
    private static Logger logger = LoggerFactory.getLogger(UserUpdateCommand.class);
    private final String commandName = "userupdate";

    @Parameter(names="--id", description="user id")
    public String userId;

    @Parameter(names="--enabled", description="enable or disable user", required=false)
    public boolean enabled;

    @Parameter(names="--role", description="new users role [viewer|publisher|administrator]", required=false)
    public String roleId;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        User user = client.updateUser(RoambiClientUtil.getUserId(userId, client), RoambiClientUtil.validateRole(roleId), enabled);
        logger.info(user.toJSON().toString());
    }
}
