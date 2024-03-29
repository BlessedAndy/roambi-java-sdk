/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameters;
import com.mellmo.roambi.api.RoambiApiClient;


/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 12/9/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=", commandDescription = "Bootstrap a the client .properties file")
public class ConfigurePropertiesCommand extends ConfigureCommand {

    private final String commandName = "configure";
    private static Logger logger = LoggerFactory.getLogger(ConfigurePropertiesCommand.class);

    private String propFile=null;

    @Override
    public void setPropertiesPath(String path) {
        propFile = path;

        try {
            initDestinationFile(propFile);
        }
        catch(IOException e) {
            // ignore
        }
    }

    @Override
    public String getName() {
        return commandName;
    }

    public String getUserInput(String prompt, Scanner userinput)  {
        System.out.print(prompt);
        String input = userinput.next( );
        return input;
    }

    private static void initDestinationFile(String destPath) throws IOException {
        File destFile = new File(destPath);
        if (destFile.exists()) {
            System.out.println("Cleaning up existing configuration file @ '" + destPath + "'");
            destFile.delete();
        }

        destFile.createNewFile();
    }


    @Override
    public void execute(RoambiApiClient client) throws Exception {
        if (propFile == null) {
            throw new RuntimeException("properties file is not specified");
        }

        initDestinationFile(propFile);

        Properties newProps = new Properties();        
        File file = new File(propFile);

        Scanner userInput = new Scanner(System.in);

        String username = getUserInput("Enter username: ", userInput);
        String password = getUserInput("Enter password: ", userInput);
        String url = getUserInput("Enter api server url: ", userInput);
        String key = getUserInput("Enter consumer key: ", userInput);
        String secret = getUserInput("Enter consumer secret: ", userInput);
        String redirectUri = getUserInput("Enter redirect uri: ", userInput);

        newProps.put("username", username);
        newProps.put("password", password);
        newProps.put("server.url", url);
        newProps.put("consumer.key", key);
        newProps.put("consumer.secret", secret);
        newProps.put("redirect.uri", redirectUri);

        OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file));
        try {
            newProps.store(outputStream, "Generated by Roambi API CLI");

            outputStream.write("# uncomment and edit the following settings if you are running behind a proxy\n");
            outputStream.write("# proxyHost=\n");
            outputStream.write("# proxyPort=\n");
        } finally {
            outputStream.close();
        }

    }
}
