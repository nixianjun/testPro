package com.tudou.userstat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.tudou.userstat.model.UserTimeLine;
import com.tudou.userstat.tool.JsonUtils;
import com.tudou.utils.client.BaseClient;
import com.tudou.utils.client.HTTPLongClient;
import com.tudou.utils.client.MemcachedClientAdapter;

/**
 * 
 * @author JSAON NI 2014-12-9
 * 
 */
public class UserStatClient extends BaseClient {
	private static final Logger logger = Logger.getLogger(UserStatClient.class);
	/**
	 * 必须，usersrv地址
	 */
	private String serverURL;

	/**
	 * 应用程序名(如playlist、channels等)
	 */
	private String app;

	private MemcachedClientAdapter memCachedClient;

	private static final String DEFAULT_ENCODING = "utf8";
	private static final String USERSTAT_ACTION = "/userstatController/";

	
	
	
	//检测心跳
	/** 服务降级的错误数阈值 */
	private static final int DEGRADATION_THRESHOLD = 20;
	/** 服务恢复时的成功数阈值 */
	private static final int RECOVERY_THRESHOLD = 20;
	/** 固定时间间隔内失败数 */
	private AtomicInteger failNum = new AtomicInteger(0);
	/** 请求成功数 */
	private AtomicInteger succNum = new AtomicInteger(0);
	/** 服务是否可用 */
	private volatile boolean available = true;


	public UserStatClient(String confURL) {
		super(confURL);
		this.serverURL = getPropValue("server");
		memCachedClient = getMemCacheClientAdapter("userstat");
		logger.info("memCachedClient="+memCachedClient+"/r/n" +
				"开始检测心跳");
		 
		//检测心跳
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				// 测试时间间隔内错误数超过指定阈值则判定服务不可用
				if (failNum.get() > DEGRADATION_THRESHOLD) {
					available = false;
					failNum.set(0);
					succNum.set(0);
				}
				// 如果服务不可用则进行心跳测试以求在后续时刻可以恢复服务
				if (!available) {
					try {
						if (isServiceAvailable()) {
							succNum.incrementAndGet();
						} else {
							succNum.set(0);
						}
					} catch (Exception e) {
						succNum.set(0);
					}
					if (succNum.get() >= RECOVERY_THRESHOLD) {
						available = true;
					}
				}
				// 服务可用时重置计数
				else {
					failNum.set(0);
					succNum.set(0);
				}
				//logger.info("检测心跳:"+available);
			}

		}, 1000, 500, TimeUnit.MILLISECONDS);
	}
	/**
	 * 服务可用性测试，不对错误数和成功数修改
	 * 
	 * @return
	 */
	private boolean isServiceAvailable() {
		String url = serverURL + USERSTAT_ACTION+"isLive.do";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app", app);
		params.put("javaClient", "true");
  		try {
			HTTPLongClient.getUrlContent(url, false, params, DEFAULT_ENCODING);
			logger.info("****isLive=true");
			return true;
		} catch (Exception e) {
			logger.info("****isLive=false");
			return false;
		}
	}

	@Override
	public boolean safeCheck() {
		Object userMc = this.getMemCacheClientAdapter("userstat");
		if (userMc == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 得到user时光机数据
	 * @author Jason ni 2014.12.5
	 * @param userId 主键
	 * @return User对象
	 */
	public  List<UserTimeLine> getUserTimeLineData(Integer userId) {
		if (!available) {
			logger.info("心跳:"+available+"method:getUserTimeLineData param【userId="+userId+"】 return null");
			return null;
		}
		if (userId < 0) {
			return null;
		}
		try {
			Object cached =   memCachedClient.get(com.tudou.userstat.constant.Constants.User_Time_Line_KEY + userId);
			// 如果cache中有数据,直接返回
			if (cached != null) {
				logger.info("[UserStatClient]query cached:"+cached);
				return JSONArray.parseArray(cached+"", UserTimeLine.class);
			}
			String jsonResult = execHTTPPost(USERSTAT_ACTION+"getUserTimeLineData", new NameValuePair(
					"userId", userId + ""));
			logger.info("[UserStatClient]nocached-jsonResult:"+jsonResult);
			JsonUtils.JsonResult<List<UserTimeLine>> list=JsonUtils.parse2Array(jsonResult, UserTimeLine.class);
			return list.getData();
		} catch (Exception e) {
			failNum.incrementAndGet();
			logger.error("getUserTimeLineData error..+failNum:"+failNum);
			return null;
		}
	}
	/**
	 * 私有，封装http post调用，有时id传的过多，导致url长度非法，只能用post
	 * 
	 * @param pair key->value参数对
	 * @return HTTP GET 执行结果
	 */
	private String execHTTPPost(String actionName, NameValuePair... pair) {
		if (StringUtils.isBlank(app)) {
			throw new IllegalArgumentException("app参数不可为空");
		}

		NameValuePair[] nameValuePairs = new NameValuePair[pair.length + 2];

		for (int i = 0; i < pair.length; i++) {
			nameValuePairs[i] = pair[i];
		}

		nameValuePairs[nameValuePairs.length - 1] = new NameValuePair("javaClient", "true");
		nameValuePairs[nameValuePairs.length - 2] = new NameValuePair("app", app);

		PostMethod httpMethod = (PostMethod) createHttpMethod(serverURL+actionName+".do", DEFAULT_ENCODING, false);
		httpMethod.addParameters(nameValuePairs);
		try {
			HTTPLongClient.getHttpClient().executeMethod(httpMethod);
			if (httpMethod.getStatusCode() == 404) {
				logger.error("####http error 404 actionName=" +serverURL+actionName+".do");
				return "";
			}
			return httpMethod.getResponseBodyAsString();
		} catch (HttpException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			httpMethod.releaseConnection();
		}

		return null;
	}

	/**
	 * 创建HttpMethod对象,支持get和post
	 * 
	 * @param url url
	 * @param charset 字符集
	 * @param isGet 是否是get方式
	 * @return GetMethod
	 */
	private HttpMethod createHttpMethod(String url, String charset, boolean isGet) {
		HttpMethod httpMethod = isGet ? new GetMethod(url) : new PostMethod(url);
		HttpMethodParams arg0 = new HttpMethodParams();
		arg0.setContentCharset(charset);
		arg0.setSoTimeout(3000);
		httpMethod.setParams(arg0);

		httpMethod.setRequestHeader("Connection", "keep-alive");
		httpMethod
				.setRequestHeader("User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12 (.NET CLR3.5.30729)");
		return httpMethod;
	}
	
	
	
	
	/**
	 * @param serverURL the serverURL to set
	 */
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	/**
	 * @param app the app to set
	 */
	public void setApp(String app) {
		this.app = app;
	}
/*	public static void main(String[] args) {
		String[] i="1,2,3,4".split(",");
		List<Integer> s=new ArrayList<Integer>();
		for (String string : i) {
			s.add(Integer.valueOf(string));	}
		Collection<Integer> list=s;
		System.out.println(list.toString());
	}*/

}
