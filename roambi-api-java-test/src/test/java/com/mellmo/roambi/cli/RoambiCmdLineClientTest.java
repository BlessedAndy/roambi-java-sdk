/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/12/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */


@Ignore
public class RoambiCmdLineClientTest {

    String propsLocation;
    String sourceLocation;

    @Before
    public void setUp() {
        URL url = RoambiCmdLineClientTest.class.getResource("/gerrit.properties");
        if(url != null) {
            propsLocation = url.getPath();
        }

        url =  RoambiCmdLineClientTest.class.getResource("/simple.xls");
        if(url != null){
            sourceLocation = url.getPath();
        }
    }


    @Test
    public void testCreateSourceCommand() throws Exception, IllegalAccessException {
        String newTitle = "create_test_" + Calendar.getInstance().getTime().toString().replace(" ","_");
        String args [ ] = {"-props="+propsLocation, "create", "--file="+sourceLocation, "--folder=518c22aae4b0acb32dfabe48", "--title="+newTitle};
        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);

        cmd.execute();
    }

    @Test
    public void testCreateSourceCommandPath() throws Exception, IllegalAccessException {
        String newTitle = "create_test_path_" + Calendar.getInstance().getTime().toString().replace(" ","_");
        String args [ ] = {"-props="+propsLocation, "create", "--file="+sourceLocation, "--folder=/Source", "--title="+newTitle};
        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);

        cmd.execute();
    }


    @Test
    public void testRefreshDocumentCommand() throws Exception, IllegalAccessException {
        String directory="518c22aae4b0acb32dfabe48";
        String template="518c2c78e4b081a95e1cb7bc";
        String sourceFile="518c26e0e4b0070c8f380779";
        String title="refresh_test_"+ Calendar.getInstance().getTime().toString().replace(" ","_");;

        String args [ ] = {"-props="+propsLocation, "refresh", "--source="+sourceFile, "--template="+template, "--folder="+directory, "--title="+title};// "--overwrite=true"};
        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);

        cmd.execute();
    }

    @Test
    public void testUpdateSourceCommand() throws Exception, IllegalAccessException {
        String sourceFile="518c26e0e4b0070c8f380779";
        String args [ ] = {"-props="+propsLocation, "update", "--file="+sourceLocation, "--target="+sourceFile};
        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);

        cmd.execute();
    }

    @Test
    public void testAddPermissions() throws Exception {
        String sourceFile="518c26a2e4b0070c8f380691x";
        String args [] =  {"-props="+propsLocation, "addPermission", "--target="+sourceFile, "--userIds=17feaa6c-0610-48fb-a798-e0ca01d19ff4", "4a6f2bd9-04a3-4548-835f-4175555957ec", "--groupIds=5192bb09e4b05b4768a0fbd7", "5192bb04e4b05b4768a0fbcc"};

        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);
        cmd.execute();

        String args2 [] =  {"-props="+propsLocation, "removePermission", "--target="+sourceFile, "--userIds=17feaa6c-0610-48fb-a798-e0ca01d19ff4", "4a6f2bd9-04a3-4548-835f-4175555957ec", "--groupIds=5192bb09e4b05b4768a0fbd7", "5192bb04e4b05b4768a0fbcc"};
        RoambiCmdLineClient cmd2 = new RoambiCmdLineClient(args2);
        cmd2.execute();
    }

    @Test
    public void testCreateFolder() throws Exception {
        String args [] =  {"-props="+propsLocation, "mkdir", "--title=testcliFolder"};
        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);
        cmd.execute();

        //test delete
        String args2 [] =  {"-props="+propsLocation, "rmdir", "--folder=/testcliFolder"};
        RoambiCmdLineClient cmd2 = new RoambiCmdLineClient(args2);
        cmd2.execute();
    }

    @Test
    public void testGerrit() throws Exception, IllegalAccessException {
        String sourceFile="528a6358e4b0d0b666791fed";
        String args [ ] = {"-props="+propsLocation, "update", "--file="+sourceLocation, "--target="+sourceFile};
        RoambiCmdLineClient cmd = new RoambiCmdLineClient(args);

        cmd.execute();
    }
}
