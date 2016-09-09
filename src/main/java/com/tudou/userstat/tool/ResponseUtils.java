package com.tudou.userstat.tool;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tudou.utils.web.WebTool;

public class ResponseUtils {

	static final Logger logger = Logger.getLogger(ResponseUtils.class);

	public static String DEFAULT_ENCODING = "UTF-8";

	public static String DEFAULT_JSONP = "jsoncallback";

	private static final String RESPONSE_CODE = "code";
	private static final String RESPONSE_MSG = "msg";
	private static final String RESPONSE_DATA = "data";

	public static final int RESPONSE_CODE_SUCCESS = 0;

	/** 参数错误 */
	public static final int RESPONSE_CODE_PARAMETER_ERROR = 1000;

	/** 应用业务逻辑错误 */
	public static final int RESPONSE_CODE_APPLICATION_ERROR = 2000;

	/** 服务系统错误 */
	public static final int RESPONSE_CODE_SERVICE_ERROR = 3000;

	/**
	 * 以秒为单位
	 */
	public static final int CACHE_5_MINUTE = 5 * 60;

	/**
	 * 设置content-type header以及响应编码,编码从response中的getCharacterEncoding获取编码，如果此编码为空则设置为默认编码（默认为UTF-8）
	 * 
	 * @param response http响应
	 */
	public static void setContentTypeHeader(HttpServletResponse response) {
		String encoding = response.getCharacterEncoding();
		if (StringUtils.isBlank(encoding)) {
			encoding = DEFAULT_ENCODING;
			response.setCharacterEncoding(encoding);
		}
		response.setContentType("text/html;charset=" + encoding);
	}

	/**
	 * 设置不缓存响应头
	 * 
	 * @param response http响应
	 */
	public static void setNoCacheHeader(HttpServletResponse response) {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache,no-store");
		response.setDateHeader("Expires", 0);
	}

	/**
	 * 设置响应的内容类型及编码，同时设置不缓存的头信息
	 * 
	 * @param response
	 */
	public static void setHtmlContentNoCacheHeader(HttpServletResponse response) {
		setContentTypeHeader(response);
		setNoCacheHeader(response);
	}

	/**
	 * 设置缓存响应头
	 * 
	 * @param response
	 * @param seconds 大于0的缓存时间，单位：秒
	 */
	public static void setCacheHeader(HttpServletResponse response, int seconds) {
		if (seconds <= 0) {
			return;
		}
		response.setHeader("Cache-Control", "max-age=" + seconds);
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
		response.setDateHeader("Expires", System.currentTimeMillis() + seconds * 1000);
	}

	public static void setHtmlContentCacheHeader(HttpServletResponse response, int seconds) {
		setContentTypeHeader(response);
		setCacheHeader(response, seconds);
	}

	/**
	 * 向客户端写响应内容
	 * 
	 * @param response http响应
	 * @param content 需要写到浏览器的响应内容
	 */
	public static void writeResponse(HttpServletRequest request, HttpServletResponse response, String content) {
		Writer writer = null;
		try {
			String jsoncallback = request.getParameter(DEFAULT_JSONP);
			if (!WebTool.checkCallbackName(jsoncallback, response)) {
				return;
			}
			setContentTypeHeader(response);
			writer = response.getWriter();
			if (StringUtils.isBlank(jsoncallback)) {
				writer.write(content);
			} else {
				writer.write(jsoncallback + "(" + content + ")");
			}
		} catch (IOException e) {
			logger.error("write response error", e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void writeResponse(HttpServletRequest request, HttpServletResponse response, int code, String msg,
			Object data) {
		JSONObject result = new JSONObject();
		result.put(RESPONSE_CODE, code);
		result.put(RESPONSE_MSG, msg);
		result.put(RESPONSE_DATA, JSON.toJSON(data));
		writeResponse(request, response, JSON.toJSONString(result, SerializerFeature.BrowserCompatible));
	}

	/**
	 * 向客户端写正确响应，响应编码采用response中的编码或默认编码，并自动处理JSONP参数
	 * 
	 * @param request
	 * @param response
	 * @param data
	 * @see com.tudou.uis.tool.ResponseUtils.DEFAULT_JSONP
	 * @see com.tudou.uis.tool.ResponseUtils.DEFAULT_ENCODING
	 */
	public static void writeSuccessReponse(HttpServletRequest request, HttpServletResponse response, Object data) {
		writeResponse(request, response, RESPONSE_CODE_SUCCESS, "success", data);
	}

	/**
	 * 向客户端写失败响应，响应编码采用response中的编码或默认编码，并自动处理JSONP参数
	 * 
	 * @param request
	 * @param response
	 * @param msg
	 * @see com.tudou.uis.tool.ResponseUtils.DEFAULT_JSONP
	 */
	public static void writeParamErrorResponse(HttpServletRequest request, HttpServletResponse response, String msg) {
		setNoCacheHeader(response);
		writeResponse(request, response, RESPONSE_CODE_PARAMETER_ERROR, msg, null);
	}

	public static void writeAppErrorResponse(HttpServletRequest request, HttpServletResponse response, String msg) {
		setNoCacheHeader(response);
		writeResponse(request, response, RESPONSE_CODE_APPLICATION_ERROR, msg, null);
	}

	public static void writeServiceErrorResponse(HttpServletRequest request, HttpServletResponse response, String msg) {
		setNoCacheHeader(response);
		writeResponse(request, response, RESPONSE_CODE_SERVICE_ERROR, msg, null);
	}

	public static String getDefaultJsonp() {
		return DEFAULT_JSONP;
	}

	/**
	 * 设置默认的JSONP的回调函数参数名
	 * 
	 * @param jsoncallback
	 */
	public static void setDefaultJsonp(String jsoncallback) {
		DEFAULT_JSONP = jsoncallback;
	}

}
