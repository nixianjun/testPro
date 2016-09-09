package com.tudou.userstat.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.tudou.digservicenew.client.PtsClient;
import com.tudou.userstat.dao.ItemDAO;
import com.tudou.userstat.service.HomeUser0Stat;
import com.tudou.userstat.service.UserDailyStatService;
import com.tudou.userstat.service.UserInfoCollector;
import com.tudou.userstat.service.UserItemService;
import com.tudou.userstat.service.UserStatService;
import com.tudou.userstat.service.statistics.UserCommentCountStat;
import com.tudou.userstat.service.statistics.UserPlaylistCountStat;
import com.tudou.userstat.service.statistics.UserSubCountStat;
import com.tudou.userstat.service.statistics.UserVideoCountStat;
import com.tudou.userstat.tool.ResponseUtils;
import com.tudou.utils.client.HTTPLongClient;

@RequestMapping("/userstat/data")
@Controller
public class UserStatDataController {

	private static final Logger logger = Logger
			.getLogger(UserStatDataController.class);

	@Resource
	UserStatService userStatService;

	@Resource
	UserVideoCountStat userVideoCountStat;

	@Resource
	UserCommentCountStat userCommentCountStat;

	@Resource
	UserSubCountStat userSubCountStat;

	@Resource
	UserInfoCollector userInfoCollector;

	@Resource
	HomeUser0Stat homeUser0Stat;

	@Resource
	UserPlaylistCountStat userPlaylistCountStat;

	@Resource
	ItemDAO itemDAO;

	@Resource
	PtsClient ptsClient;

	@Resource
	UserItemService userItemService;

	@Resource
	UserDailyStatService userDailyStatService;

	/**
	 * 获取用户的总统计数
	 * 
	 * @param uid
	 */
	@RequestMapping("getTotalData")
	public void getTotalData(int uid, HttpServletRequest request,
			HttpServletResponse response) {
		Object obj = userStatService.getUserTotalStat(uid);

		if (obj != null) {
			logger.info("get user obj " + obj.getClass() + " " + obj);
		}

		ResponseUtils.writeSuccessReponse(request, response, obj);
	}

	@RequestMapping("runAllUser")
	public void runAllUser() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(
					"/home/userstat/totaldata.log"));

			String line = null;
			int index = 0;
			while ((line = r.readLine()) != null) {
				String result = null;
				try {
					result = HTTPLongClient.getUrlContent(line, false, null);
				} catch (Exception e) {
					logger.error("", e);
				}

				if (result == null) {
					result = "";
				}

				logger.info("get http client " + line + " " + result.length()
						+ " index " + index++);
			}

			r.close();
		} catch (Exception e) {
		}
	}

	/**
	 * 测试接口
	 * 
	 * @param uid
	 * @param request
	 * @param response
	 */
	@RequestMapping("test")
	public void test(HttpServletRequest request, HttpServletResponse response) {
		homeUser0Stat.statDownUsers();
	}

	/**
	 * 初始化运行所有的视频数量
	 */
	@RequestMapping("runTotalVC")
	public void runTotalVC() {
		userPlaylistCountStat.statAll();
	}

	@RequestMapping("getUserItemPlaytimes")
	public void getUserItemPlaytimes(int uid, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject obj = new JSONObject();
		List<Integer> itemids = itemDAO.getUserItemIds(uid);

		int total = 0;

		for (Integer itemId : itemids) {
			total += ptsClient.getItemPlayTimes(itemId);
		}

		obj.put("playtimes", total);
		obj.put("items", itemids.size());

		ResponseUtils.writeSuccessReponse(request, response, obj);
	}

	@RequestMapping("collectAllUser")
	public void collectAllUser() throws IOException {
		logger.info("start collect all user");
		try {
			userInfoCollector.collectAllUser();
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("finish collect all user");
	}
	
	
	@RequestMapping("userDailyStatService")
	public void userDailyStatService() throws IOException {
		logger.info("start userDailyStatService");
		try {
			userDailyStatService.statDaily();
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("finish userDailyStatService");
	}

	@RequestMapping("userDailyStatService_statDaily")
	public void userDailyStatService_statDaily(@RequestParam(required = true) String date,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.info("start userDailyStatService_statDaily");
		try {
			 
			Date tdDate=com.tudou.utils.lang.DateUtil.stringToDate(date);
			com.tudou.userstat.tool.ResponseUtils.writeSuccessReponse(request, response, userDailyStatService.statDaily(tdDate));
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("finish userDailyStatService_statDaily");
	}
	
	@RequestMapping("userDailyStatService_statWeek4")
	public void userDailyStatService_statWeek4(@RequestParam(required = true) String date,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.info("start userDailyStatService_statWeek4");
		try {
			Date tdDate=com.tudou.utils.lang.DateUtil.stringToDate(date);
			com.tudou.userstat.tool.ResponseUtils.writeSuccessReponse(request, response, userDailyStatService.statWeek4(tdDate));
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("finish userDailyStatService_statWeek4");
	}
 }
