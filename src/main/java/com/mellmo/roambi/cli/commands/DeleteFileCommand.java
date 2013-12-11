/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 9/27/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteFileCommand extends CommandBase {
    private static Logger logger = Logger.getLogger(DeleteFileCommand.class);
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

        ContentItem item = RoambiClientUtil.getContentItem(file, client);
        client.deleteFile(item.getUid());
    }
}
