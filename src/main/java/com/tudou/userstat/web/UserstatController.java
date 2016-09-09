package com.tudou.userstat.web;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.tudou.label.client.constants.AnchorScenario;
import com.tudou.segmenter.utils.IKWordUtil;
import com.tudou.userstat.dao.UserTimeLineDAO;
import com.tudou.userstat.model.UserTimeLine;
import com.tudou.userstat.service.UserTimeLineService;
import com.tudou.userstat.tool.ResponseUtils;
import com.tudou.userstat.tool.UserTimeLineServiceUtils;

/**
 * 对外接口
 * 
 * @author JSAON NI 2014-12-8
 * 
 */
@RequestMapping("/userstatController")
@Controller
public class UserstatController {
	private static final Logger logger = Logger
			.getLogger(UserstatController.class);
	@Resource
	UserTimeLineService userTimeLineService;
	@Resource
	UserTimeLineDAO userTimeLineDAO;
	@Resource
	private MemcachedClient userstatMemClient;

	@RequestMapping("isLive")
	public void isLive(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.getWriter().write("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @ResponseBody
	@RequestMapping("getUserTimeLineData")
	public void getUserTimeLineData(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			if (StringUtils.isEmpty(request
					.getParameter("userId"))) {
				ResponseUtils.writeParamErrorResponse(request, response,
						"参数userId出错");
				return;
			}
			String userId = StringUtils.trimToNull(request
					.getParameter("userId"));
			Integer uid = Integer.valueOf(userId);
			List<UserTimeLine> list = userTimeLineService
					.getUserTimeLineData(uid);
			response.setCharacterEncoding("UTF-8");
			ResponseUtils.writeSuccessReponse(request, response,
					UserTimeLineServiceUtils.userTimeLines2JSON(list));
		} catch (Exception e) {
			logger.error("", e);
			ResponseUtils
					.writeServiceErrorResponse(request, response, "服务器端繁忙");
		}
	}

	/**
	 * 通过userId批量更新时光机项目缓存
	 * @param request
	 * @param response
	 * @author JASON NI  2014-12-12
	 */
	@RequestMapping("updateUserTimeLineMemcahedKeyByUserIds")
	public void updateUserTimeLineMemcahedKeyByUserIds(
			HttpServletRequest request, HttpServletResponse response) {
		try {
			String userIds = request.getParameter("userIds");
			if (StringUtils.isEmpty(userIds)) {
				ResponseUtils.writeParamErrorResponse(request, response,
						"参数userIds出错");
				return;
			}
			String[] list = userIds.split(",");
			if(list.length>1000){
				ResponseUtils.writeParamErrorResponse(request, response,
						"参数userIds出错:参数中id不能超过1000");
				return;
			}
			Integer result = 0;
			result = userTimeLineService
					.updateUserTimeLineMemcahedKeyByUserIds(list);
			JSONObject json=new JSONObject();
			json.put("result", result);
			ResponseUtils.writeSuccessReponse(request, response, json);
		} catch (Exception e) {
			logger.error("", e);
			ResponseUtils
					.writeServiceErrorResponse(request, response, "服务器端繁忙");
		}

	}
	
	@RequestMapping("encrypt")
	public void usertimeline(HttpServletRequest request,
			HttpServletResponse response,
 			 Integer id) {
		ResponseUtils.writeSuccessReponse(request, response, com.tudou.util.encry.IdEncrypter.encrypt(id));
	}
	@RequestMapping("decrypt")
	public void decrypt(HttpServletRequest request,
			HttpServletResponse response,
 			String  code) {
		ResponseUtils.writeSuccessReponse(request, response, com.tudou.util.encry.IdEncrypter.decrypt(code));
	}
	
	@RequestMapping("test")
	public void test(HttpServletRequest request,
			HttpServletResponse response){
		String defaultPattern = "[tag type='tid' valueId='vid']tagName[/tag]";
		String result = IKWordUtil.anchorWordMark("测试下花千骨的", 10086, 1, AnchorScenario.PL,defaultPattern);
		ResponseUtils.writeSuccessReponse(request, response, result);
	}
}
