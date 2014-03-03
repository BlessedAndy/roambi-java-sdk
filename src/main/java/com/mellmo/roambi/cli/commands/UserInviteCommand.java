/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import org.apache.log4j.Logger;

public class UserInviteCommand extends CommandBase{
    private static Logger logger = Logger.getLogger(UserInviteCommand.class);
    private final String commandName = "userinvite";
    /*primary email, given name, family name, role*/

    @Parameter(names="--email", description="new user's email")
    public String email;

    @Parameter(names="--givenName", description="new user's given name")
    public String givenName;

    @Parameter(names="--familyName", description="new user's family name")
    public String familyName;

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
