package com.mellmo.roambi.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.model.IBaseModel;
import com.mellmo.roambi.api.utils.JsonUtils;

/**
 * 
 * @author charles
 *
 */
public abstract class RESTClient {
	
	public static final String APPLICATION_JSON = "application/json";
	public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	protected static final String BEARER = "Bearer ";
	protected static final String AUTHORIZATION = "Authorization";
	protected static final String TEXT_JSON = "text/json";
	protected static final String UTF_8 = "utf-8";
	protected static final Logger LOG = Logger.getLogger(RESTClient.class);
	
	protected abstract String getAccessToken() throws ApiException;
	
	protected GetMethod buildGetMethod(final String url) throws ApiException {
		return buildGetMethod(getAccessToken(), url);
	}
	
	protected DeleteMethod buildDeleteMethod(final String url) throws ApiException {
		return buildDeleteMethod(getAccessToken(), url);
	}
	
	protected PutMethod buildPutMethod(final String url, final NameValuePair... params) throws ApiException {
		return buildPutMethod(getAccessToken(), url, FORM_URL_ENCODED, params);
	}
	
	protected PostMethod buildPostMethod(final String url, final NameValuePair... params) throws ApiException {
		return buildPostMethod(getAccessToken(), url, params);
	}
	
	public static GetMethod buildGetMethod(final String accessToken, final String url) {
		final GetMethod method = new GetMethod(url);
		addAuthorizationHeader(method, accessToken);
		return method;
	}
	
	public static DeleteMethod buildDeleteMethod(final String accessToken, final String url) {
		final DeleteMethod method = new DeleteMethod(url);
		addAuthorizationHeader(method, accessToken);
		return method;
	}

	public static PutMethod buildPutMethod(final String accessToken, final String url, final NameValuePair... params) {
		return buildPutMethod(accessToken, url, FORM_URL_ENCODED, params);
	}
	
	public static PutMethod buildPutMethod(final String accessToken, final String url, final String contentType, final NameValuePair... params) {
		final PutMethod method = new PutMethod(url);
		addAuthorizationHeader(method, accessToken);
		final RequestEntity requestEntity = getStringRequestEntity(contentType, params);
		method.setRequestEntity(requestEntity);
		return method;
	}
	
	public static PostMethod buildPostMethod(final String accessToken, final String url, final NameValuePair... params) {
		final PostMethod method = new PostMethod(url);
		addAuthorizationHeader(method, accessToken);
		method.setRequestBody(params);
		return method;
	}
	
	public static PostMethod buildPostMethod(final String accessToken, final String url, final String name, final String... values) {
		return buildPostMethod(accessToken, url, toNameValuePair(name, values));
	}
	
	public static PostMethod buildPostMethod(final String accessToken, final String url, final String contentType, final String name, final String... values) {
		final PostMethod method = new PostMethod(url);
		addAuthorizationHeader(method, accessToken);
		final String entityContent = getStringContent(contentType, name, values);
		final RequestEntity requestEntity = getStringRequestEntity(contentType, entityContent);
		method.setRequestEntity(requestEntity);
		return method;
	}
	
	public static void addAuthorizationHeader(final HttpMethodBase method, final String accessToken) {
		method.addRequestHeader(AUTHORIZATION, BEARER + accessToken);
	}
	
	public static NameValuePair toParam(final String name, final String value) {
		checkArgument(!Strings.isNullOrEmpty(value), name + " cannot be null");
		return new NameValuePair(name, value);
	}
	
	public static NameValuePair toParam(final String name, final IBaseModel model) {
		checkArgument(model != null, name + " cannot be null");
		checkArgument(!Strings.isNullOrEmpty(model.getUid()), name + " cannot be null");
		return new NameValuePair(name, model.getUid());
	}
	
	public static NameValuePair toParam(final String name, final boolean value) {
		return new NameValuePair(name, value ? "true" : "false");
	}
	
	private static String getStringContent(final String contentType, final String name, final String... values) {
		return isJson(contentType) ? JsonUtils.toJson(name, values) : EncodingUtil.formUrlEncode(toNameValuePair(name, values), UTF_8);
	}
	
	private static boolean isJson(final String contentType) {
		return TEXT_JSON.equalsIgnoreCase(contentType) || APPLICATION_JSON.equalsIgnoreCase(contentType);
	}
	
	private static RequestEntity getStringRequestEntity(final String contentType, final NameValuePair... params) {
		final String entityContent = isJson(contentType) ? JsonUtils.createJsonFromParameters(params) : EncodingUtil.formUrlEncode(params, UTF_8);
		return getStringRequestEntity(contentType, entityContent);
	}
	
	private static RequestEntity getStringRequestEntity(final String contentType, final String entityContent) {
		try {
			return new StringRequestEntity(entityContent, contentType, UTF_8);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Unable to set request body (encoding not supported): " + e.getLocalizedMessage());
			return null;
		}
	}
	
	private static NameValuePair[] toNameValuePair(final String name, final String... values) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String value:values) {
			params.add(new NameValuePair(name + "[]", value));
		}
		return params.toArray(new NameValuePair[params.size()]);
	}
}
