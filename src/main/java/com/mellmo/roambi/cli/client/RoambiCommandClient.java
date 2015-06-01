/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.cli.commands.CommandBase;
import com.mellmo.roambi.cli.commands.ConfigureCommand;
import com.mellmo.roambi.cli.commands.ShowVersionCommand;

/**
 * Created with IntelliJ IDEA.
 * User: pcheng
 * Date: 9/15/14
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoambiCommandClient {
    private static final Logger logger = LoggerFactory.getLogger(RoambiCommandClient.class);


    //commands
    private Map<String, CommandBase> commands = new TreeMap<String,CommandBase>();

    private JCommander jct;

    public static interface ClientConfiguration {
        String getPropertiesFile();
        RoambiApiClient getClient();
    }

    private ClientConfiguration configuration;

    protected RoambiCommandClient() {
        jct = new JCommander(this);
        registerCommands();
    }
    public RoambiCommandClient(ClientConfiguration configuration) {
        this();
        this.setConfiguration(configuration);
    }

    public void setConfiguration(ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    //enumerates and adds all subclasses of CommandBase found in com.mellmo.roambi.cli.commands
    private void registerCommands() {

        Reflections reflections = new Reflections("com.mellmo.roambi.cli.commands");

        Set<Class<? extends CommandBase>> subTypes = reflections.getSubTypesOf(CommandBase.class);

        Iterator<Class <?extends CommandBase>> itr = subTypes.iterator();
        while(itr.hasNext()) {
            Class<?extends CommandBase> clazz = itr.next();
            logger.trace("registering command class: " + clazz.getName());

            try {
                if(!Modifier.isAbstract(clazz.getModifiers())) {
                    CommandBase obj = (CommandBase) clazz.newInstance();
                    commands.put(obj.getName(), obj);
                }
            } catch (IllegalAccessException e) {
                logger.error("could not add command: " + clazz.getName()+". skipping.");
            } catch (InstantiationException e) {
                logger.error("could not add command: " + clazz.getName()+". skipping.");
            }
        }

        // do another iteration, so that commands are added to jct
        // in ascending order
        for( Map.Entry<String, CommandBase> entry : commands.entrySet()) {
            jct.addCommand(entry.getKey(), entry.getValue());
        }

    }

    protected String parse(String [] args) {
        jct.parse(args);

        return jct.getParsedCommand();
    }

    protected void doExecute(String cmd) throws Exception {

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
                    ((ConfigureCommand)cb).setPropertiesPath(configuration.getPropertiesFile());
                    cb.execute(null);
                } else {
                    cb.execute(configuration.getClient());
                }
                return;
            }
        }
        jct.usage();
    }

    public void execute(String [] args) throws Exception {
        String cmd = parse(args);
        doExecute(cmd);
    }

}
