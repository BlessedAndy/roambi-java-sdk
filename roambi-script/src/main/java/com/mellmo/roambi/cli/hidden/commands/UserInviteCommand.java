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
import com.mellmo.roambi.api.model.Role;
import com.mellmo.roambi.api.model.User;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import com.mellmo.roambi.cli.commands.CommandBase;

@Parameters(separators = "=", commandDescription = "Invite a user")
public class UserInviteCommand extends CommandBase{
    private static Logger logger = LoggerFactory.getLogger(UserInviteCommand.class);
    private final String commandName = "userinvite";
    /*primary email, given name, family name, role*/

    @Parameter(names="--email", description="new user's email")
    public String email;

    @Parameter(names="--givenName", description="new user's given name")
    public String givenName;

    @Parameter(names="--familyName", description="new user's family name")
    public String familyName;

    @Parameter(names="--role", description="new users role [viewer|publisher|administrator]", required=false)
    public String roleId;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        Role role = RoambiClientUtil.validateRole(roleId);
        User user = client.inviteUser(email, givenName, familyName, role);
        logger.info(user.toJSON().toString());
    }
}
