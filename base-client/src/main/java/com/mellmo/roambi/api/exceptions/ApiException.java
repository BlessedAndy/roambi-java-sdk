/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.exceptions;

import java.io.InputStream;

import com.google.common.io.Closeables;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class ApiException extends Exception {
	
	private int status;
	private String code;
	private String message;

	public ApiException(int status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		if (message == null) {
			return "CODE: " + code + "; STATUS: " + status;
		}
		else {
			return message;
		}
	}

	public static ApiException fromApiResponse(final int statusCode, final InputStream stream) {
		if (stream == null) {
			return new ApiException(statusCode, null, null);
		}
		else {
			try {
				JsonObject error = ResponseUtils.responseToObject(stream);
				String desc = error.get("error_description").isJsonNull() ? null : error.get("error_description").getAsString();
				return new ApiException(statusCode, error.get("error").getAsString(), desc);
			} catch (Exception ex) {
				return new ApiException(statusCode, "Unknown Error", null);
			} finally {
				Closeables.closeQuietly(stream);
			}
		}
	}
}
