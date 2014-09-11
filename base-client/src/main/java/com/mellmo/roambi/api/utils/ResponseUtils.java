/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpMethodBase;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResponseUtils {

	public static JsonObject responseToObject(String json) {
		JsonParser parser = new JsonParser();
		return (JsonObject) parser.parse(json);
	}

	public static JsonObject responseToObject(InputStream stream) {
		JsonParser parser = new JsonParser();
		return (JsonObject) parser.parse(new InputStreamReader(stream));
	}
	
	public static InputStream getResponseInputStream(final HttpMethodBase method) throws UnsupportedEncodingException, IOException {
		return new FilterInputStream(method.getResponseBodyAsStream()) {
			@Override
			public void close() throws IOException {
				try {
					super.close();
				} finally {
					if (method != null) {
						method.releaseConnection();
					}
				}
			}
		};
	}
}
