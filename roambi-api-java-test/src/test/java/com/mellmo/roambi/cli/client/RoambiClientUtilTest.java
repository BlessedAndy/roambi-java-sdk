/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import com.mellmo.roambi.api.RoambiApiClient;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.ContentItem;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/13/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */


@Ignore
public class RoambiClientUtilTest {

    RoambiClientWrapper roambiClientWrapper;

    @Before
    public void setUp() {

        URL url = RoambiClientUtilTest.class.getResource("/roambi-api-cli.properties");
        if(url != null) {
            String propsLocation = url.getPath();
            roambiClientWrapper = new RoambiClientWrapper(propsLocation);
        }
    }

    @Test
    public void testGetContentItemFromPath() throws Exception {

        String foobar = "51ba35e4e4b0b7be452482b9";
        RoambiApiClient client = roambiClientWrapper.getClient();
        client.currentUser();

        ContentItem contentItem = RoambiClientUtil.getContentItem("/Source/don't_delete.xlsx", client);

        Assert.assertEquals(contentItem.getUid(), foobar);

    }
}

