package com.mellmo.roambi.cli;

/**
 * Created with IntelliJ IDEA.
 * User: pcheng
 * Date: 9/16/14
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineTokenizer {

    private char commentChar = '#';
    private char quoteChar = '"';
    private String line;
    private String nextToken = null;
    private int currentPos = 0;

    LineTokenizer(String line) {
        if (line == null) {
            throw new NullPointerException();
        }
        this.line = line;

        findNextToken();
    }

    public boolean hasNext() {
        return (nextToken != null);
    }

    public String next() {
        String token = nextToken;

        findNextToken();

        return token;
    }

    private boolean isCommentChar(char ch) {
        return ch == commentChar;
    }

    private boolean isQuote(char ch) {
        return ch == quoteChar;
    }

    private void findNextToken() {
        skipWhitespace();

        if (currentPos >= line.length()) {
            // no more token
            nextToken = null;
            return;
        }
        boolean insideQuotes = false;

        StringBuilder buffer = null;
        for( ;  currentPos < line.length() ; currentPos++) {
            char ch = line.charAt(currentPos);

            if (isQuote(ch)) {
                insideQuotes = ! insideQuotes;
            }

            if (! insideQuotes) {
                if (Character.isWhitespace(ch)) {
                    break;
                }

                if (isCommentChar(ch)) {
                    currentPos = line.length();
                    break;
                }

            }

            if (! isQuote(ch)) {
                if (buffer == null) {
                    buffer = new StringBuilder(line.length());
                }
                buffer.append(ch);
            }
        }
        if (buffer == null) {
            nextToken = null;
        } else {
            nextToken = buffer.toString();
        }
    }

    private void skipWhitespace() {
        while( currentPos < line.length() ) {
            char ch = line.charAt(currentPos);
            if (Character.isWhitespace(ch)) {
                currentPos++;
            } else {
                // currentPos points to a non-whitespace
                return;
            }
        }
    }
}
