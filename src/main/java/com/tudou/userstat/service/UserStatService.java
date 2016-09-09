package com.tudou.userstat.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tudou.userstat.dao.StatDAO;
import com.tudou.userstat.model.HomeUserStat;
import com.tudou.userstat.model.UserStat;

/**
 * 用户统计的服务类
 * 
 * @author clin
 * 
 */
@Service
public class UserStatService {

	@Resource
	StatDAO statDAO;

	/**
	 * 获取用户总的统计数据
	 * 
	 * @param uid
	 * @return
	 */
	public Object getUserTotalStat(int uid) {
		return statDAO.selectUserTotal(uid);
	}

	/**
	 * 获取用户总统计数
	 * 
	 * @param uid
	 * @return
	 */
	public UserStat getUserTotalStatByUid(int uid) {
		return statDAO.selectUserTotalReturnUserStat(uid);
	}

	/**
	 * 批量获取用户的视频数
	 * 
	 * @param uids
	 * @return
	 */
	public List<HomeUserStat> getUserItemCounts(int startUid, int size) {
		return statDAO.selectUserItemCounts(startUid, size);
	}

}
