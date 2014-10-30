/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.ContentItem;
import com.mellmo.roambi.cli.client.RoambiClientUtil;

/**
 * Created with IntelliJ IDEA.
 * User: pcheng
 * Date: 9/16/14
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Parameters(separators = "=", commandDescription = "List folder content")
public class ListCommand extends CommandBase {
    private final String commandName = "ls";

    @Parameter(names = "--folder", description="parent folder", required=true)
    private String parentFolder = null;

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {

        client.currentUser();
        List<ContentItem> items = RoambiClientUtil.getPortalContents(parentFolder, client);

        System.out.println("Listing: " + parentFolder);
        for (ContentItem item: items) {
            System.out.println(
                    String.format( "%1$s\t%2$11s\t%3$s", item.getUid(), item.getType(), item.getName()));

        }
    }

}
