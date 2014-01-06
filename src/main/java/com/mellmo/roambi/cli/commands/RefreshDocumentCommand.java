/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.cli.client.RoambiClientUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/12/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=", commandDescription = "Refresh a Roambi document")
public class RefreshDocumentCommand extends CommandBase{
    private static Logger logger = Logger.getLogger(RefreshDocumentCommand.class);
    private final String commandName = "refresh";

    @Parameter(names="--source", description="remote source file")
    String sourceFile;
    @Parameter(names="--template", description = "template rbi")
    String template;
    @Parameter(names="--folder", description="remote folder destination")
    String destinationFolder;
    @Parameter(names="--title", description="title of the new document")
    String title;
    //@Parameter(names="--overwrite", description = "overwrite existing")
    boolean overwrite=true;
    @Parameter(names="--permission", description="set permissions for new document", variableArity = true, required=false)
    private List<String> permissionIds;

    @Override
    public String getName() {
        return commandName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        logger.info("executing: " + commandName);
        logger.info("sourceFile: " + sourceFile);
        logger.info("template: " + template);
        logger.info("destinationFolder: " + destinationFolder);
        logger.info("title: " + title);
        logger.info("overwrite: " + overwrite);
        if(permissionIds !=null) {
            logger.info("permission:" + permissionIds.toString());
        }

        client.currentUser();
        String destination_rbi_name = title;
        ContentItem destinationItem =  RoambiClientUtil.getContentItem(destinationFolder, client);
        ApiJob job = client.createAnalyticsFile(RoambiClientUtil.getContentItem(sourceFile, client), RoambiClientUtil.getContentItem(template, client),
                destinationItem, destination_rbi_name, overwrite);

        int tries = 0;
        while(job.getStatus()==ApiJob.JobStatus.PROCESSING) {
            Thread.sleep(job.getRetryAfter() * 1000);
            logger.debug("checking job...");
            job = client.getJob(job.getUid());

            if(++tries > maxTries) {
                throw new Exception("Reached max tries.  Job aborted.");
            }
        }

        ContentItem newItem = RoambiClientUtil.findFile(destinationItem.getUid(), new ContentItem("", title), client );
        if(permissionIds != null && newItem != null) {
            RoambiClientUtil.addPermission(newItem, permissionIds,client);
        }
    }
}
