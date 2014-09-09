/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.httpclient.NameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.gson.JsonObject;

public class JsonUtils {

	public static String createJsonFromParameters(NameValuePair...params) {
		final JsonObject jsonObject = new JsonObject();
		for (NameValuePair pair : params) {
			jsonObject.addProperty(pair.getName(), pair.getValue());
		}
		return jsonObject.toString();
	}
	
	public static String toJson(final String name, final String...values) {
		final JSONArray jsonArray = new JSONArray();
		for (String value:values) {
			jsonArray.add(value);
		}
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put(name, jsonArray);
		return JSONValue.toJSONString(jsonObject);
	}
	
	public static JsonObject getJson(final JsonObject json, final String name) {
		return json != null && json.has(name) && json.get(name).isJsonObject() ? json.get(name).getAsJsonObject() : null;
	}
	
	public static String getString(final JsonObject json, final String name) {
		return json != null && json.has(name) && json.get(name).isJsonPrimitive() && json.get(name).getAsJsonPrimitive().isString() ? json.get(name).getAsString() : null;
	}
	
	public static boolean hasBoolean(final JsonObject json, final String name) {
		return json != null && json.has(name) && json.get(name).isJsonPrimitive() && json.get(name).getAsJsonPrimitive().isBoolean();
	}
	
	public static Date getDate(final JsonObject json, final String name) {
		final String stringValue = getString(json, name);
		return stringValue != null ? DatatypeConverter.parseDateTime(stringValue).getTime() : null;
	}
	
	public static String printDate(final Date time) {
		if (time != null) {
			final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
			calendar.setTime(time);
			return DatatypeConverter.printDateTime(calendar);
		}
		return null;
	}
}
