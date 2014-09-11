/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api.model;

import java.util.List;

import com.google.gson.JsonObject;
import com.mellmo.roambi.api.utils.ResponseUtils;

public class PagedList<T> {
	
	private List<T> results;
	private int totalResults;
	private int resultsCount;
	private int offset;
	private int limit;


	public PagedList() {
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public int getResultsCount() {
		return resultsCount;
	}

	public void setResultsCount(int resultsCount) {
		this.resultsCount = resultsCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void fromApiListResponse(String json) {
		fromApiListResponse(ResponseUtils.responseToObject(json).get("list_data").getAsJsonObject());
	}
	
	public void fromApiListResponse(JsonObject listProps) {
		this.setLimit(listProps.get("limit").getAsInt());
		this.setOffset(listProps.get("offset").getAsInt());
		this.setResultsCount(listProps.get("result_count").getAsInt());
		this.setTotalResults(listProps.get("record_count").getAsInt());
	}
}
