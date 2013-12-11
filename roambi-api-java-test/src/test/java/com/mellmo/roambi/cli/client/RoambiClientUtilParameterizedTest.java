/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.cli.client;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: ryancamoras
 * Date: 6/13/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(Parameterized.class)
public class RoambiClientUtilParameterizedTest {

    private String value;
    private boolean expected;

    public RoambiClientUtilParameterizedTest(String value, boolean expected) {
        this.value = value;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> generateData()
    {
        return Arrays.asList(new Object[][]{
                {"51ba35e4e4b0b7be452482b9", true},
                {"/path/to/my/file", false},
                {"path/to/my/file", false},
                {"/supercalifragalisticexp", false}
        });
    }

    @Test
    public void testIsUID() {
        Assert.assertEquals(RoambiClientUtil.isUIDValue(value), expected);
    }
}
