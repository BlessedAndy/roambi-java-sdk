/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.cli.client.RoambiClientWrapper;
import com.mellmo.roambi.cli.client.RoambiCommandClient;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/11/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=")
public class RoambiCmdLineClient  extends RoambiCommandClient implements RoambiCommandClient.ClientConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(RoambiCmdLineClient.class);

    private RoambiClientWrapper clientWrapper;

    //top-level options
    @Parameter (names={"-props", "--props"}, description = "Property file location. If not specified, default to roambi-api-cli.properties")
    private String propertiesFile = "roambi-api-cli.properties";

    @Parameter(names = {"--help", "-h"}, description = "Shows help", help = true)
    private boolean help;

    @Parameter(names = "--verbose", description = "Verbose mode")
    private boolean verbose;

    @Parameter(names = {"--file", "-f"}, description = "Script File", converter = FileConverter.class)
    private File scriptFile;

    @Parameter(names = {"--continue-on-failure", "-C"}, description = "Continue the rest of the script file on failure.")
    private boolean continueOnFailure;


    private int successCount = 0;
    private int totalCount = 0;

    public RoambiCmdLineClient () {
        super();
        setConfiguration(this);
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return totalCount - successCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    protected void doExecute(String cmd) throws Exception {
        if (verbose) {
            //LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
        	// this ties us directly to the logback implementation...
        	ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        	root.setLevel(Level.INFO);
        }
        if (cmd == null) {
            if (scriptFile != null) {
                executeScriptFile();
                return;
            }
        }

        try {
            totalCount++;
            super.doExecute(cmd);
            successCount++;
        } catch (Exception e) {
            throw e;
        }
    }

    private void executeScriptFile() throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile), "UTF-8"));

        executeReader(reader);
    }

    private void executeReader(Reader reader) throws Exception {

        Scanner lineScanner = new Scanner(reader);

        while (lineScanner.hasNext()) {

            String line = lineScanner.nextLine();

            LineTokenizer tokenizer = new LineTokenizer(line);

            ArrayList<String> argList = new ArrayList<String>();
            while( tokenizer.hasNext()) {
                argList.add(tokenizer.next());
            }
            if (! argList.isEmpty()) {
                String [] args = argList.toArray(new String[]{});
                try {
                    totalCount++;
                    RoambiCommandClient client = new RoambiCommandClient(this);
                    client.execute(args);
                    successCount++;
                } catch(Exception e) {
                    LOG.error("Failed when executing:");
                    LOG.error(args.toString());
                    if (continueOnFailure) {
                        LOG.error("Failed", e);
                    } else {
                        // don't have to log because the caller is logging already.
                        throw e;
                    }
                }
            }
        }
    }

    // ClientConfiguration
    public String getPropertiesFile() {
        return propertiesFile;
    }

    public RoambiApiClient getClient() {
        if (clientWrapper == null) {
            if (getPropertiesFile() == null) {
                throw new RuntimeException("Property file was not specified.");
            }
            File file = new File(getPropertiesFile());
            if (! file.exists()) {
                throw new RuntimeException(String.format("Property file '%1$s' does not exist.", getPropertiesFile()));
            }
            clientWrapper = new RoambiClientWrapper(getPropertiesFile());
        }
        RoambiApiClient client = clientWrapper.getClient();
        if (client == null) {
            throw new RuntimeException("Unable to create Roambi API client.");
        }
        return client;
    }

    public static void main (String[] args) {
        int exitCode = 0;
        RoambiCmdLineClient cmd = new RoambiCmdLineClient();
        try {
            cmd.execute(args);
            LOG.info("Finished.");
        } catch (Exception e) {
            LOG.error("Failed. " + e.getLocalizedMessage(), e);
            exitCode = 1;
        }
        LOG.info(String.format("Run: %1$d. Failure: %2$d. ",cmd.getTotalCount(),  cmd.getFailureCount()));
        System.exit(exitCode);
    }
}
