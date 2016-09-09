package com.tudou.userstat.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

/**
 * 视频相关的dao,查询主库的item表
 * 
 * @author clin
 * 
 */
@Repository
public class ItemDAO extends AbstractDAO {

	/**
	 * 获取指定用户的上传视频的数量
	 * 
	 * @param uid
	 * @return
	 */
	public int getItemCountByUserId(int uid) {
		Integer count = (Integer) this.tudouSqlSession.selectOne(
				"ItemDAO.getItemCountByUserId", uid);

		if (count == null) {
			return 0;
		}

		return count;
	}

	/**
	 * 获取用户上传的视频列表
	 * 
	 * @param uid
	 * @return
	 */
	public List<Integer> getUserItemIds(int uid) {
		return this.tudouSqlSession.selectList("ItemDAO.getUserItemIds", uid);
	}

	/**
	 * 获取指定用户指定时间段内上传的视频数量
	 * 
	 * @param uid
	 * @param date
	 * @return
	 */
	public int getItemCountByUidDate(int userId, String startDate,
			String endDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("startDate", startDate);
		params.put("endDate", endDate);

		Integer count = (Integer) this.tudouSqlSession.selectOne(
				"ItemDAO.getItemCountByUidDate", params);

		if (count == null) {
			return 0;
		}

		return count;
	}
}
