/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.commands;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 12/9/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ConfigureCommand extends CommandBase {
    abstract public void setPropertiesPath(String path);
}
