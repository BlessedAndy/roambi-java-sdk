/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.model.ApiJob;
import com.mellmo.roambi.api.model.ContentItem;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/12/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=", commandDescription = "Refresh a Roambi document based on data in a local file")
public class RefreshDocumentWithFileCommand extends RefreshDocumentCommandBase {
	
	private static final String commandName = "publish-with-file";
	
	@Parameter(names="--file", description="local source file", required = true) 
	String localFilePath;

	@Override public String getName() {
		return commandName;
	}

	@Override protected String getFilelog() {
		return "localFilePath: " + localFilePath;
	}

	@Override protected ApiJob clientExecute(final RoambiApiClient client, final ContentItem template, final ContentItem folder) throws Exception {
		return client.createAnalyticsFile(new File(localFilePath), template, folder, title, overwrite);
	}
}
