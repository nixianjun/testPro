package com.tudou.userstat.tool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.tudou.utils.web.RequestUtil;

public class HttpClientHelper {

	/** HttpClient */
	private static HttpClient httpClient;
	/** 连接超时时间(ms) */
	private static int connectTimeout = 2000;
	/** socket响应超时时间(ms) */
	private static int socketTimeout = 2000;
	/** 每个host最多可以有多少连接 */
	private static int maxConnectionsPerHost = 50;
	/** 所有Host总共连接数 */
	private static int maxTotalConnections = 1000;

	private static List<Header> defaultHeaders;

	static {
		init();
	}

	private static void init() {
		PoolingClientConnectionManager connManager = new PoolingClientConnectionManager();
		connManager.setDefaultMaxPerRoute(maxConnectionsPerHost);
		connManager.setMaxTotal(maxTotalConnections);
		httpClient = new DefaultHttpClient(connManager);
		HttpParams clientParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(clientParams, connectTimeout);
		HttpConnectionParams.setSoTimeout(clientParams, socketTimeout);
 		com.tudou.userstat.tool.HttpClientHelper.addDefaultHeader("x-forwarded-for", "103.24.228.180");
 		com.tudou.userstat.tool.HttpClientHelper.addDefaultHeader("clientIp", "103.24.228.180");
		if (defaultHeaders != null) {
			clientParams.setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders);
		}
	}

	/**
	 * 使用GET方式请求指定URI并以字符串形式获取响应内容，响应的文本内容根据Content-Type头来解码，如果没有此Header则默认使用ISO-8859-1来解码
	 * 
	 * @param url
	 * @param params
	 * @return 响应内容
	 */
	public static String getUriContentUsingGet(String url, Map<String, String> params) {
		return getUriRequestContent(url, true, params, null, null);
	}

	/**
	 * 使用POST方式请求指定URI并返回相应内容
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String getUriContentUsingPost(String url, Map<String, String> params, String charset) {
		return getUriRequestContent(url, false, params, charset, null);
	}

	/**
	 * 以GET或POST方法请求指定URI并以字符串形式获取响应内容，响应的文本内容根据Content-Type头来解码，如果没有此Header则默认使用ISO-8859-1来解码
	 * 
	 * @param url 请求地址
	 * @param useGet 是否使用GET方式
	 * @param params 请求参数
	 * @param charset POST时指定的参数编码，如果为空则默认使用ISO-8859-1编码参数
	 * @return 响应内容，字符串的解码字符集首先从响应头Content-Type头获取，如果没有此Header则默认使用ISO-8859-1来解码
	 */
	public static String getUriRequestContent(String url, boolean useGet, Map<String, String> params, String charset) {
		return getUriRequestContent(url, useGet, params, charset, null);
	}

	public static String getUriRequestContent(String url, boolean useGet, Map<String, String> params, String charset,
			Map<String, String> headersMap) {
		HttpUriRequest request = null;
		try {
			// GET request
			if (useGet) {
				if (params != null && params.size() > 0) {
					URIBuilder builder = new URIBuilder(url);
					for (Map.Entry<String, String> param : params.entrySet()) {
						builder.addParameter(param.getKey(), param.getValue());
					}
					request = new HttpGet(builder.build());
				} else {
					request = new HttpGet(url);
				}
			}
			// POST request
			else {
				request = new HttpPost(url);
				HttpPost post = (HttpPost) request;
				if (params != null && params.size() > 0) {
					List<NameValuePair> nvList = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : params.entrySet()) {
						nvList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
					UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvList, charset);
					post.setEntity(formEntity);
				}
			}
			// add header if exist
			if (headersMap != null && !headersMap.isEmpty()) {
				for (Map.Entry<String, String> header : headersMap.entrySet()) {
					request.addHeader(header.getKey(), header.getValue());
				}
			}
		 Header[] headList=	request.getAllHeaders();
		 for (Header header : headList) {
			System.out.println(header.getName()+"   "+header.getValue());
		  }
		// System.out.println("ip:"+ request.get);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			} else {
				return EntityUtils.toString(entity);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			((HttpRequestBase) request).releaseConnection();
		}
	}

	public static <T> T requestURI(String url, boolean useGet, Map<String, String> params, String charset,
			Map<String, String> headersMap, ResponseHandler<? extends T> responseHandler) {
		HttpUriRequest request = null;
		try {
			// GET request
			if (useGet) {
				if (params != null && params.size() > 0) {
					URIBuilder builder = new URIBuilder(url);
					for (Map.Entry<String, String> param : params.entrySet()) {
						builder.addParameter(param.getKey(), param.getValue());
					}
					request = new HttpGet(builder.build());
				} else {
					request = new HttpGet(url);
				}
			}
			// POST request
			else {
				request = new HttpPost(url);
				HttpPost post = (HttpPost) request;
				if (params != null && params.size() > 0) {
					List<NameValuePair> nvList = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> param : params.entrySet()) {
						nvList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
					}
					UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvList, charset);
					post.setEntity(formEntity);
				}
			}
			// add header if exist
			if (headersMap != null && !headersMap.isEmpty()) {
				for (Map.Entry<String, String> header : headersMap.entrySet()) {
					request.addHeader(header.getKey(), header.getValue());
				}
			}
			T result = httpClient.execute(request, responseHandler);
			return result;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			((HttpRequestBase) request).releaseConnection();
		}
	}

	public static <T> T requestUriUsingGet(String uri, Map<String, String> params, ResponseHandler<? extends T> responseHandler) {
		return requestURI(uri, true, params, null, null, responseHandler);
	}

	/**
	 * 设置连接超时，设置后将对后续所有请求有效
	 * 
	 * @param newConnectTimeout 新的连接超时时间（毫秒），大于0的值将产生连接超时效果，负数或0代表不设置超时
	 */
	public static void setConnectTimeout(int newConnectTimeout) {
		connectTimeout = newConnectTimeout;
		HttpParams clientParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(clientParams, newConnectTimeout);
	}

	/**
	 * 设置响应超时，设置后将对后续所有请求有效
	 * 
	 * @param newSocketTimeout 新的响应超时时间（毫秒），大于0的值将产生响应超时效果，负数或0代表不设置超时
	 */
	public static void setSocketTimeout(int newSocketTimeout) {
		socketTimeout = newSocketTimeout;
		HttpParams clientParams = httpClient.getParams();
		HttpConnectionParams.setSoTimeout(clientParams, newSocketTimeout);
	}

	/**
	 * 设置默认的每个主机最大连接数，必须为正整数
	 * 
	 * @param newMaxConnectionsPerHost
	 */
	public static void setMaxConnectionsPerHost(int newMaxConnectionsPerHost) {
		if (newMaxConnectionsPerHost < 1) {
			throw new IllegalArgumentException("maxConnectionsPerHost argument invalid  " + newMaxConnectionsPerHost);
		}
		PoolingClientConnectionManager connManager = (PoolingClientConnectionManager) httpClient.getConnectionManager();
		connManager.setDefaultMaxPerRoute(newMaxConnectionsPerHost);
		maxConnectionsPerHost = newMaxConnectionsPerHost;
	}

	/**
	 * 设置所有主机连接总数最大值
	 * 
	 * @param newMaxTotalConnections
	 */
	public static void setMaxTotalConnections(int newMaxTotalConnections) {
		if (newMaxTotalConnections < 10) {
			throw new IllegalArgumentException("maxTotalConnections can not be less than 10, parameter is "
					+ newMaxTotalConnections);
		}
		PoolingClientConnectionManager connManager = (PoolingClientConnectionManager) httpClient.getConnectionManager();
		connManager.setMaxTotal(newMaxTotalConnections);
		maxTotalConnections = newMaxTotalConnections;
	}

	/**
	 * 添加客户端请求时默认添加的Header
	 * 
	 * @param name
	 * @param value
	 */
	public static void addDefaultHeader(String name, String value) {
		if (defaultHeaders == null) {
			defaultHeaders = new ArrayList<Header>();
		}
		defaultHeaders.add(new BasicHeader(name, value));
		HttpParams clientParams = httpClient.getParams();
		clientParams.setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders);
	}

	public static void main(String[] args) throws URISyntaxException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("app", "2");
		params.put("uidCode", "6gyNzsOWizE");
		System.out.println(getUriRequestContent("http://www.tudou.com/uis/userInfo.action", true, params, "UTF-8"));
	}
}
