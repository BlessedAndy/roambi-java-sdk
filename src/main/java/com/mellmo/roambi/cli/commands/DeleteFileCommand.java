/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 9/27/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Parameters(separators = "=", commandDescription = "Delete a file in the Roambi Repository")
public class DeleteFileCommand extends CommandBase {
    private static Logger logger = LoggerFactory.getLogger(DeleteFileCommand.class);
    private final String commandName = "delete";

    @Parameter(names="--file", description="file to be deleted")
    private String file;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        logger.info("executing: " + commandName);
        logger.info("file: " + file);
        client.currentUser();
        client.deleteFile(file);
    }
}
