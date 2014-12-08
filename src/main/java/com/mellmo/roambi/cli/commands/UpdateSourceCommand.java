/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import static com.mellmo.roambi.cli.client.RoambiClientUtil.toContentItem;

import java.io.File;

import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/11/13
 * Time: 5:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Parameters(separators = "=", commandDescription = "Upload and update a file in the Roambi Repository")
public class UpdateSourceCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(UpdateSourceCommand.class);
    private final String commandName = "update";

    @Parameter(names="--file", description="locale file you with to upload")
    private String newFile;

    @Parameter(names="--target", description="target file uid")
    private String remoteUid;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        logger.info("executing: " + commandName);
        logger.info("file: " + newFile);
        logger.info("remoteUid: " + remoteUid);

        final File sourceFile = new File(newFile);
        client.currentUser();
        client.updateFileData(toContentItem(remoteUid), sourceFile);
    }
}

