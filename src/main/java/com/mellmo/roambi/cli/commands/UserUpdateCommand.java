/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import org.apache.log4j.Logger;


public class UserUpdateCommand extends CommandBase{
    private static Logger logger = Logger.getLogger(UserUpdateCommand.class);
    private final String commandName = "userupdate";

    @Parameter(names="--id", description="user id")
    private String userId;

    @Parameter(names="--enabled", description="enable or disable user", required=false)
    public boolean enabled;

    @Parameter(names="--role", description="new users role [stand|publisher|admin]", required=false)
    public String role;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        //TODO:
    }
}
