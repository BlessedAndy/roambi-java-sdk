/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.utils;

import org.apache.commons.httpclient.NameValuePair;

import com.google.gson.JsonObject;

public class JsonUtils {

	public static String createJsonFromParameters(NameValuePair...params) {
		JsonObject jsonObject = new JsonObject();
		for (NameValuePair pair : params) {
			jsonObject.addProperty(pair.getName(), pair.getValue());
		}
		return jsonObject.toString();
	}

}
