/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import com.beust.jcommander.Parameter;
import com.mellmo.roambi.api.RoambiApiClient;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/12/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CommandBase {
    //so we can have per command help
    @Parameter(names="--help", hidden=true)
    protected boolean help;

    @Parameter(names="--maxTries", hidden=true)
    protected int maxTries = 50;
    
    @Parameter(names="--plugin", hidden=true, required=false)
    protected String pluginVersion = null;

    public boolean getHelp() {
        return help;
    }
    
    public String getPluginVersion() {
    	return this.pluginVersion;
    }
    
    // abstract functions
    public abstract String getName();
    public abstract void execute(RoambiApiClient client) throws Exception;
}
