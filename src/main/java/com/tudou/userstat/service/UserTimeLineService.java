package com.tudou.userstat.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.tudou.userstat.constant.Constants;
import com.tudou.userstat.dao.UserTimeLineDAO;
import com.tudou.userstat.model.UserTimeLine;

/**
 * 用户时光机
 * 
 * @author JSAON NI 2014-12-8
 * 
 */
@Service
public class UserTimeLineService {
	private static final Logger logger = Logger
			.getLogger(UserTimeLineService.class);
	@Resource
	UserTimeLineDAO userTimeLineDAO;
	@Resource
	private MemcachedClient userstatMemClient;

	public List<UserTimeLine> getUserTimeLineData(Integer userId) {
		String key = Constants.User_Time_Line_KEY + userId;
		try {
			Object cache =   userstatMemClient
					.get(key);
			logger.info("---------read  cache:" + cache);
			if (null!=cache) {
				logger.info("cached:" + cache);
				return JSONArray.parseArray(cache+"", UserTimeLine.class);
			}
		} catch (TimeoutException e) {
			logger.error("1xmemcached timeout exception", e);
		} catch (InterruptedException e) {
			logger.error("1xmemcached interrupted exception", e);
		} catch (MemcachedException e) {
			logger.error("1xmemcached memcached exception", e);
		}
		List<UserTimeLine> result = userTimeLineDAO.getUserTimeLineList(userId);
		try {
			if(null==result||result.isEmpty()){
				return Collections.EMPTY_LIST;
			}
			userstatMemClient.set(key,
					com.tudou.userstat.constant.Constants.SECONDS_PER_WEEK,
					JSONArray.toJSONString(result));
			logger.info("key:"+key+" value:"+userstatMemClient.get(key));
			return result;
		} catch (TimeoutException e) {
			logger.error("2xmemcached timeout exception", e);
		} catch (InterruptedException e) {
			logger.error("2xmemcached interrupted exception", e);
		} catch (MemcachedException e) {
			logger.error("xmemcached memcached exception", e);
		}
		return null;

	}

	public Integer updateUserTimeLineMemcahedKeyByUserIds(String[] list) {
		Integer r=0;
 		if(null==list){
			return 0;
		}
		for (String userId : list) {
			if(!StringUtils.isNumeric(userId)){
				continue;
			}
			String key = Constants.User_Time_Line_KEY + userId;
			List<UserTimeLine> result = userTimeLineDAO.getUserTimeLineList(Integer.valueOf(userId));
			try {
 				userstatMemClient.set(key,
						com.tudou.userstat.constant.Constants.SECONDS_PER_WEEK,
						JSONArray.toJSONString(result));
				logger.info("重置key："+key+":"+userstatMemClient.get(key));
				r++;
  			} catch (TimeoutException e) {
				logger.error("2xmemcached timeout exception", e);
			} catch (InterruptedException e) {
				logger.error("2xmemcached interrupted exception", e);
			} catch (MemcachedException e) {
				logger.error("xmemcached memcached exception", e);
			}
		}
		return r;
	}
	public static void main(String[] args) {
		System.out.println(StringUtils.isNumeric("1212"));
	}
}
