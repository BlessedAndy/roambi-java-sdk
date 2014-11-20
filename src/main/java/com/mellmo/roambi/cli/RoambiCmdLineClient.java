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

import org.apache.log4j.Logger;

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

    private RoambiClientWrapper clientWrapper;

    //top-level options
    @Parameter (names={"-props", "--props"}, description = "Property file location. If not specified, default to roambi-api-cli.properties")
    private String propertiesFile = "roambi-api-cli.properties";

    @Parameter(names = "--help", description = "Shows help", help = true)
    private boolean help;

    @Parameter(names = "--file", description = "Script File", converter = FileConverter.class)
    private File scriptFile;

    public RoambiCmdLineClient () {
        super();
        setConfiguration(this);
    }

    @Override
    protected void doExecute(String cmd) throws Exception {
        if (cmd == null) {
            if (scriptFile != null) {
                executeScriptFile();
                return;
            }
        }
        super.doExecute(cmd);
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

            ArrayList<String> args = new ArrayList<String>();
            while( tokenizer.hasNext()) {
                args.add(tokenizer.next());
            }
            if (! args.isEmpty()) {
                RoambiCommandClient client = new RoambiCommandClient(this);
                client.execute(args.toArray(new String[]{}));
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
            clientWrapper = new RoambiClientWrapper(propertiesFile);
        }
        return clientWrapper.getClient();
    }

    public static void main (String[] args) {
        try {
            RoambiCmdLineClient cmd = new RoambiCmdLineClient();
            cmd.execute(args);
            Logger.getLogger(RoambiCmdLineClient.class).info("Finished.");
        } catch (Exception e) {
            Logger.getLogger(RoambiCmdLineClient.class).error("Failed. " + e.getLocalizedMessage(), e);
            System.exit(1);
        }
    }
}
