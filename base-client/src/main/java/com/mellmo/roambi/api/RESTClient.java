/**
 * This sample code and information are provided "as is" without warranty of any kind, either expressed or implied, including
 * but not limited to the implied warranties of merchantability and/or fitness for a particular purpose.
 */
package com.mellmo.roambi.api;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.length;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.mellmo.roambi.api.exceptions.ApiException;
import com.mellmo.roambi.api.http.FilePartDescriptor;
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
	protected static final String AUTHORIZATION = "Authorization";
	protected static final String BEARER = "Bearer ";
	protected static final String USER_AGENT = "User-Agent";
	protected static final Logger LOG = LoggerFactory.getLogger(RESTClient.class);
	protected static final String TEXT_JSON = "text/json";
	protected static final String UTF_8 = "utf-8";
	protected static final String USER_AGENT_VALUE = getUserAgent();
	
	public static void setHeaders(final HttpMethodBase method, final String accessToken, final String pluginVersion) {
		setAuthorizationHeader(method, accessToken);
		setUserAgentHeader(method, pluginVersion);
	}
	
	public static void setUserAgentHeader(final HttpMethodBase method, final String pluginVersion) {
		method.setRequestHeader(USER_AGENT, isNotBlank(pluginVersion) ? String.format("%s %s", USER_AGENT_VALUE, pluginVersion) : USER_AGENT_VALUE);
		if (LOG.isDebugEnabled())	LOG.debug(" User-Agent header: " + method.getRequestHeader(USER_AGENT));
	}
	
	public static void setAuthorizationHeader(final HttpMethodBase method, final String accessToken) {
		method.setRequestHeader(AUTHORIZATION, BEARER + accessToken);
	}
	
	public static DeleteMethod buildDeleteMethod(final String accessToken, final String pluginVersion, final String url) {
		final DeleteMethod method = new DeleteMethod(url);
		setHeaders(method, accessToken, pluginVersion);
		return method;
	}
	
	public static GetMethod buildGetMethod(final String accessToken, final String pluginVersion, final String url, final NameValuePair... params) {
		final GetMethod method = new GetMethod(url);
		setHeaders(method, accessToken, pluginVersion);
		if (params.length > 0) {
			method.setQueryString(params);
		}
		return method;
	}
	
	public static PutMethod buildPutMethod(final String accessToken, final String pluginVersion, final String url, final NameValuePair... params) {
		return buildPutMethod(accessToken, pluginVersion, url, FORM_URL_ENCODED, params);
	}

	public static PutMethod buildPutMethod(final String accessToken, final String pluginVersion, final String url, final String contentType, final NameValuePair... params) {
		final PutMethod method = new PutMethod(url);
		setHeaders(method, accessToken, pluginVersion);
		final RequestEntity requestEntity = getStringRequestEntity(contentType, params);
		method.setRequestEntity(requestEntity);
		return method;
	}
	
    /**
     * Return the MIME Type based on the specific file name.
     * @param file the file name
     * @return the file's MIME Type
     */
	protected String contentTypeForFile(File file) {
		if (file.getName().endsWith(".xls")) {
			return "application/excel";
		}
		else if (file.getName().endsWith(".xlsx")) {
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		}
		else if (file.getName().endsWith(".csv")) {
			return "text/csv";
		}
		else {
			return new MimetypesFileTypeMap().getContentType(file);
		}
	}
	
	public static NameValuePair[] removeNull(final NameValuePair... params) {
		final Collection<NameValuePair> result = CollectionUtils.select(Arrays.asList(params), PredicateUtils.notNullPredicate());
		return result.toArray(new NameValuePair[result.size()]);
	}
	
	public static NameValuePair required(final String name, final boolean value) {
		return new NameValuePair(name, value ? "true" : "false");
	}
	
	public static NameValuePair required(final String name, final IBaseModel model) {
		checkArgument(model != null, name + " cannot be null");
		return required(name, model.getUid());
	}
	
	public static NameValuePair required(final String name, final String value) {
		checkArgument(!Strings.isNullOrEmpty(value), name + " cannot be null");
		return new NameValuePair(name, value);
	}
	
	public static String[] required(final String name, final String... values) {
		final Collection<String> notBlankValues = CollectionUtils.select(Arrays.asList(ArrayUtils.nullToEmpty(values)), new Predicate<String>() {
			@Override
			public boolean evaluate(String value) {
				return isNotBlank(value);
			}
		});
		checkArgument(!notBlankValues.isEmpty(), name + " cannot be empty");
		return notBlankValues.toArray(new String[notBlankValues.size()]);
	}
	
	protected static RequestEntity getStringRequestEntity(final String contentType, final String entityContent) {
		try {
			return new StringRequestEntity(entityContent, contentType, UTF_8);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Unable to set request body (encoding not supported): " + e.getLocalizedMessage());
			return null;
		}
	}
	
	protected static NameValuePair optional(final String name, final IBaseModel model) {
		return model == null ? null : optional(name, model.getUid());
	}
	
	protected static NameValuePair optional(final String name, final String value) {
		return value == null ? null : new NameValuePair(name, value);
	}
	
	protected Part toPart(final NameValuePair param) {
		return new StringPart(param.getName(), param.getValue(), UTF_8);
	}
	
	protected Part toPart(final String name, final File file) throws FileNotFoundException {
		checkArgument((file != null && file.exists()), name + " does not exist.");

        String contentType = contentTypeForFile(file);
        String charset = null;

        FilePart filePart = new FilePart(name, file);
        // do this outside the constructor
        // the setter accepts null, but the constructor defaults to DEFAULT_CHARSET when we pass null
        filePart.setContentType(contentType);
        filePart.setCharSet(charset);
        return filePart;
	}

	protected static Part toPart(final String name, final NameValuePair... params) {
		return new StringPart(name, JsonUtils.createJsonFromParameters(params), UTF_8);
	}
	
	private static PostMethod buildPostMethod(final String accessToken, final String pluginVersion, final String url) {
		final PostMethod method = new PostMethod(url);
		setHeaders(method, accessToken, pluginVersion);
		return method;
	}
	
	private static PostMethod buildPostMethod(final String accessToken, final String pluginVersion, final String url, final RequestEntity requestEntity) {
		final PostMethod method = buildPostMethod(accessToken, pluginVersion, url);
		method.setRequestEntity(requestEntity);
		return method;
	}
	
	private static RequestEntity getStringRequestEntity(final String contentType, final NameValuePair... params) {
		final String entityContent = isJson(contentType) ? JsonUtils.createJsonFromParameters(params) : EncodingUtil.formUrlEncode(params, UTF_8);
		return getStringRequestEntity(contentType, entityContent);
	}
	
	private static RequestEntity getStringRequestEntity(final String contentType, final String name, final String... values) {
		final String entityContent = isJson(contentType) ? JsonUtils.toJson(name, values) : EncodingUtil.formUrlEncode(toNameValuePair(name, values), UTF_8);
		return getStringRequestEntity(contentType, entityContent);
	}
	
	private static String getUserAgent() {
		String name = "RoambiJavaSDK";
		String version = "version";
		String build = "build";
		try {
			final File file = new File(RESTClient.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (file.isFile()) {
				final JarFile jar = new JarFile(file);
				final Manifest manifest = jar.getManifest();
				if (manifest != null) {
					final Attributes attributes = manifest.getMainAttributes();
					final String appName = attributes.getValue("app_name");
					if (isNotBlank(appName))	name = appName;
					final String implementationVersion = attributes.getValue("Implementation-Version");
					if (isNotBlank(implementationVersion))	version = implementationVersion;
					final String scmCommit = attributes.getValue("scm_commit");
					if (isNotBlank(scmCommit))	build = length(scmCommit) > 9 ? left(scmCommit, 10) : scmCommit;
				}
				else {
					LOG.warn("Failed to get build info from jar file. manifest is null.");
				}
			}
		} catch (Exception e) {
			LOG.warn("Failed to get build info from jar file. " + e.getMessage());
		}
		return String.format("%s/%s-%s", name, version, build);
	}
	
	private static boolean isJson(final String contentType) {
		return TEXT_JSON.equalsIgnoreCase(contentType) || APPLICATION_JSON.equalsIgnoreCase(contentType);
	}
	
	private static NameValuePair[] toNameValuePair(final String name, final String... values) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String value:values) {
			params.add(new NameValuePair(name + "[]", value));
		}
		return params.toArray(new NameValuePair[params.size()]);
	}
	
	public DeleteMethod buildDeleteMethod(final String url) throws ApiException {
		return buildDeleteMethod(getAccessToken(), getPluginVersion(), url);
	}
	
	protected GetMethod buildGetMethod(final String url, final NameValuePair... params) throws ApiException {
		return buildGetMethod(getAccessToken(), getPluginVersion(), url, params);
	}
	
	protected PostMethod buildPostMethod(final String url, final NameValuePair... params) throws ApiException {
		return buildPostMethod(url, FORM_URL_ENCODED, params);
	}
	
	public PostMethod buildFileUploadMethod(final String url, Map<String, Object> params) throws ApiException, FileNotFoundException {
		final PostMethod method = buildPostMethod(getAccessToken(), getPluginVersion(), url);

		List<Part> parts = new ArrayList<Part>();
		Part part = null;
		for (Entry<String, Object> param : params.entrySet()) {
			if (param.getValue().getClass() == File.class) {
				part = toPart(param.getKey(), (File) param.getValue());
			}
			else if (param.getValue().getClass() == FilePartDescriptor.class) {
				FilePartDescriptor descriptor = (FilePartDescriptor) param.getValue();
				part = new FilePart(param.getKey(), descriptor.getFileName(), descriptor.getFile());
			}
			else {
				part = new StringPart(param.getKey(), param.getValue().toString(), UTF_8);
			}
			parts.add(part);
		}
		
		final MultipartRequestEntity multipartEntity = new MultipartRequestEntity(parts.toArray(new Part[params.size()]), method.getParams());
		method.setRequestEntity(multipartEntity);
		
		return method;
	}
	
	protected PostMethod buildPostMethod(final String url, final Part... parts) throws ApiException {
		final PostMethod method = buildPostMethod(getAccessToken(), getPluginVersion(), url);
		final MultipartRequestEntity multipartEntity = new MultipartRequestEntity(parts, method.getParams());
		method.setRequestEntity(multipartEntity);
		return method;
	}
	
	protected PostMethod buildPostMethod(final String url, final RequestEntity entity) throws ApiException {
		return buildPostMethod(getAccessToken(), getPluginVersion(), url, entity);
	}
	
	protected PostMethod buildPostMethod(final String url, final String contentType, final NameValuePair... params) throws ApiException {
		return buildPostMethod(getAccessToken(), getPluginVersion(), url, getStringRequestEntity(contentType, params));
	}
	
	protected PostMethod buildPostMethod(final String url, final String name, final String... values) throws ApiException {
		return buildPostMethod(url, getStringRequestEntity(APPLICATION_JSON, name, required(name, values)));
	}
	
	protected PutMethod buildPutMethod(final String url, final NameValuePair... params) throws ApiException {
		return buildPutMethod(getAccessToken(), getPluginVersion(), url, FORM_URL_ENCODED, params);
	}

	protected abstract String getAccessToken() throws ApiException;
	
	protected abstract String getPluginVersion();
}
