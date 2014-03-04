/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.User;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import org.apache.log4j.Logger;

@Parameters(separators = "=", commandDescription = "Get user information")
public class UserInfoCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UserInfoCommand.class);
    private final String commandName = "userinfo";

    @Parameter(names="--id", description="user id")
    public String userId;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        User user = client.getUserInfo(RoambiClientUtil.getUserId(userId, client));
        logger.info(user.toJSON().toString());
    }
}
