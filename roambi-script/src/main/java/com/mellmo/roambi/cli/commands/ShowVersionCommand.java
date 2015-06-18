/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import java.io.File;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mellmo.roambi.api.RoambiApiClient;

public class ShowVersionCommand extends CommandBase {
    private static Logger logger = LoggerFactory.getLogger(ShowVersionCommand.class);
    private final String commandName = "version";

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public void execute(RoambiApiClient client) throws Exception {
        JarFile jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        Manifest manifest = jar.getManifest();
        for (Object key : manifest.getMainAttributes().keySet()) {
        	if (key.toString().startsWith("scm") || key.toString().startsWith("build")) {
        		// system out so that we're not dependent on log4j config and will always get the debug info
        		System.out.println("[" + key.toString() + "]\t" + manifest.getMainAttributes().getValue(key.toString()));
        	}
        }
    }

}
