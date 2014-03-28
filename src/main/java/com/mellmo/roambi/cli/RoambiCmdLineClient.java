/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli;


import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.mellmo.roambi.cli.client.RoambiClientWrapper;
import com.mellmo.roambi.cli.commands.CommandBase;
import com.mellmo.roambi.cli.commands.ConfigureCommand;
import com.mellmo.roambi.cli.commands.ShowVersionCommand;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/11/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */

@Parameters(separators = "=")
public class RoambiCmdLineClient {
    private static Logger logger = Logger.getLogger(RoambiCmdLineClient.class);
    private JCommander jct;
    private RoambiClientWrapper clientWrapper;

    //commands
    private Map<String, CommandBase> commands = new HashMap<String,CommandBase>();

    //top-level options
    @Parameter (names="-props", description = "Property file location")
    private String propertiesFile;

    @Parameter(names = "--help", description = "Shows help", help = true)
    private boolean help;


    public RoambiCmdLineClient (String [] args) {
        jct = new JCommander(this);
        registerCommands(args);

        if(propertiesFile != null) {
            clientWrapper = new RoambiClientWrapper(propertiesFile);
        }
    }

    //output to stdout
    public static void message(String txt) {
        System.out.println(txt);
    }

    //enumerates and adds all subclasses of CommandBase found in com.mellmo.roambi.cli.commands
    private void registerCommands(String [] args) {

        Reflections reflections = new Reflections("com.mellmo.roambi.cli.commands");

        Set<Class<? extends CommandBase>> subTypes = reflections.getSubTypesOf(CommandBase.class);

        Iterator <Class <?extends CommandBase>> itr = subTypes.iterator();
        while(itr.hasNext()) {
            Class clazz = itr.next();
            logger.debug("registering command class: " + clazz.getName());

            try {
                if(!Modifier.isAbstract(clazz.getModifiers())) {
                    CommandBase obj = (CommandBase) clazz.newInstance();
                    commands.put(obj.getName(), obj);
                    jct.addCommand(obj.getName(), obj);
                }
            } catch (IllegalAccessException e) {
                logger.error("could not add command: " + clazz.getName()+". skipping.");
            } catch (InstantiationException e) {
                logger.error("could not add command: " + clazz.getName()+". skipping.");
            }
        }

        jct.parse(args);
    }

    public void execute() throws Exception {
        String cmd = jct.getParsedCommand();
        if(cmd != null) {

            CommandBase cb = commands.get(cmd);
            if(cb==null || cb.getHelp()) {
                jct.usage(cb.getName());
            }
            else if (cb instanceof ShowVersionCommand) {
            	((ShowVersionCommand)cb).execute(null);
            	return;
            }
            else {
                //kludge
                if(cb instanceof ConfigureCommand) {
                   ((ConfigureCommand)cb).setPropertiesPath(propertiesFile);
                }
                cb.execute(clientWrapper.getClient());
                return;
            }
        }
        jct.usage();
    }

    public static void main (String[] args) {
        try {
            RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);
            cmd.execute();
            Logger.getLogger(RoambiCmdLineClient.class).info("Finished.");
        } catch (Exception e) {
            Logger.getLogger(RoambiCmdLineClient.class).error("Failed. " + e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
