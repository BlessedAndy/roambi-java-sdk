package com.mellmo.roambi.cli;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: pcheng
 * Date: 9/16/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineTokenizerTest {

    protected ArrayList<String> tokenize(String line) {
        ArrayList<String> result = new ArrayList<String>();

        LineTokenizer tokenizer = new LineTokenizer(line);
        while (tokenizer.hasNext()) {
            result.add(tokenizer.next());
        }
        return result;
    }

    @Test
    public void testTokenizeOne() {

        ArrayList<String> args = tokenize("one");

        assertEquals("one", args.get(0));
    }

    @Test
    public void testTokenizeThree() {

        ArrayList<String> args = tokenize("one two three");

        assertEquals("one", args.get(0));
        assertEquals("two", args.get(1));
        assertEquals("three", args.get(2));
    }

    @Test
    public void testLeadingSpace() {

        ArrayList<String> args = tokenize("     one two three");

        assertEquals("one", args.get(0));
        assertEquals("two", args.get(1));
        assertEquals("three", args.get(2));
    }

    @Test
    public void testTrailingSpace() {

        ArrayList<String> args = tokenize("one two three     ");

        assertEquals("one", args.get(0));
        assertEquals("two", args.get(1));
        assertEquals("three", args.get(2));
    }

    @Test
    public void testLeadingTrailingSpace() {

        ArrayList<String> args = tokenize("    one two three     ");

        assertEquals("one", args.get(0));
        assertEquals("two", args.get(1));
        assertEquals("three", args.get(2));
    }

    @Test
    public void testLeadingTrailingSpaceAndQuotes() {

        ArrayList<String> args = tokenize("    \"one\" two three     ");

        assertEquals("one", args.get(0));
        assertEquals("two", args.get(1));
        assertEquals("three", args.get(2));
    }

    @Test
    public void testLeadingTrailingSpaceAndQuotedSpace() {

        ArrayList<String> args = tokenize("    \"one two\" three     ");

        assertEquals("one two", args.get(0));
        assertEquals("three", args.get(1));
    }

    @Test
    public void testStartComment() {

        ArrayList<String> args = tokenize("#    \"one two\" three     ");

        assertEquals(0, args.size());
    }

    @Test
    public void testLeadingSpaceStartComment() {

        ArrayList<String> args = tokenize("    #    \"one two\" three     ");

        assertEquals(0, args.size());
    }

    @Test
    public void testComment() {

        ArrayList<String> args = tokenize("    \"one two\" # three     ");

        assertEquals(1, args.size());
        assertEquals("one two", args.get(0));

    }
}
