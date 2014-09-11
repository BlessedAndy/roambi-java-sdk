/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class ApiJob {
	public enum JobStatus {
		PROCESSING,
		ERROR,
		COMPLETE
		;
		
		private static JobStatus fromString(String value) {
			return JobStatus.valueOf(value.toUpperCase());
		}
	}

	private String uid;
	private float progress = 0.0f;
	private JobStatus status = JobStatus.PROCESSING;
	private String exception = null;
	private int retryAfter = 0;
	private String statusUri = null;
	private Object result = null;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public int getRetryAfter() {
		return retryAfter;
	}

	public void setRetryAfter(int retryAfter) {
		this.retryAfter = retryAfter;
	}

	public String getStatusUri() {
		return statusUri;
	}

	public void setStatusUri(String statusUri) {
		this.statusUri = statusUri;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static ApiJob fromApiResponse(InputStream stream) {
		JsonObject object = ResponseUtils.responseToObject(stream);
		JsonObject props = object.get("job").getAsJsonObject();
		ApiJob job = new ApiJob();
		job.uid = props.get("uid").getAsString();
		job.progress = props.get("progress").getAsFloat();
		job.status = JobStatus.fromString(props.get("status").getAsString());
		JsonElement prop = props.get("exception");
		if (prop.isJsonNull()) {
			job.exception = null;
		}
		else {
			job.exception = props.get("exception").getAsString();
		}
		job.statusUri = props.get("status_uri").getAsString();
		job.retryAfter = props.get("retry_after").getAsInt();

		prop = props.get("results");
		if (prop == null || prop.isJsonNull()) {
			job.result = null;
		}
		else {
			// FIXME parse the results into objects?
			job.result = prop.getAsJsonObject();
		}

		return job;
	}

}
