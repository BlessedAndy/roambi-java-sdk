/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import org.apache.log4j.Logger;

public class UserInfoCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UserInfoCommand.class);
    private final String commandName = "userinfo";

    @Parameter(names="--id", description="user id")
    private String userId;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        //TODO:
    }
}
