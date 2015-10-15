/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.exceptions;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import com.google.common.io.Closeables;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class ApiException extends Exception {
	
	protected static final Logger LOG = LoggerFactory.getLogger(ApiException.class);
	private static final long serialVersionUID = 1L;
	
	private int status;
	private String code;
	private String message;

	public ApiException(int status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
		this.message = message;
	}

	/**
	 * The HTTP Status Code returned by the server
	 * @return the HTTP status code
	 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html</a>
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Roambi API error code returned by the server 
	 * @return the Roambi API error code
	 */
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

	public static ApiException fromApiResponse(final int statusCode, final InputStream stream) throws IOException {
		if (stream == null) {
			return new ApiException(statusCode, null, null);
		}
		else {
			boolean threw = true;
			JsonObject error = null;
			try {
				error = ResponseUtils.responseToObject(stream);
				String desc = error.get("error_description").isJsonNull() ? null : error.get("error_description").getAsString();
				ApiException ex = new ApiException(statusCode, error.get("error").getAsString(), desc);
				threw = false;
				return ex;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				if (error != null) {
					LOG.debug("Error json: " + error.toString());
				}
				return new ApiException(statusCode, "Unknown Error", null);
			} finally {
				Closeables.close(stream, threw);
			}
		}
	}
}
