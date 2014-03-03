/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import org.apache.log4j.Logger;

import java.util.List;

public class GroupRemoveCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UserUpdateCommand.class);
    private final String commandName = "groupRemove";

    @Parameter(names="--id", description="groupId if removing from a single group.", required=false)
    private String groupId;

    @Parameter(names="--users", variableArity = true, description = "user ids") //maybe use check allow email
    private List<String> users;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        //TODO:
    }
}
