package com.tudou.userstat.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.tudou.userstat.model.HomeUserStat;
import com.tudou.userstat.model.UserStat;

/**
 * 统计的数据库操作类
 * 
 * @author clin
 * 
 */
@Repository
public class StatDAO extends AbstractDAO {

	/**
	 * 加入一条新的数据或使用当前值覆盖用户的统计值
	 * 
	 * @param uid
	 * @param key
	 * @param value
	 */
	public void insertOrUpdateStat(int uid, String key, Object value) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("uid", uid);
		params.put("key", key);
		params.put("value", value);

		this.writeSqlSession.insert("StatDAO.insertOrUpdateStat", params);
	}

	/**
	 * 加入一条新的数据或使用当前值覆盖用户的统计值
	 * 
	 * @param uid
	 * @param key
	 * @param value
	 */
	public void updateStat(int uid, String key, Object value) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("uid", uid);
		params.put("key", key);
		params.put("value", value);

		this.writeSqlSession.insert("StatDAO.updateStat", params);
	}

	/**
	 * 每天的执行数量,加入一条新的数据或使用当前值覆盖统计值
	 * 
	 * @param uid
	 * @param tableName
	 * @param date
	 * @param data
	 */
	public void insertOrUpdateDailyStat(int uid, String tableName, int day,
			int value) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tableName", tableName);
		params.put("uid", uid);
		params.put("day", day);
		params.put("value", value);

		this.writeSqlSession.insert("StatDAO.insertOrUpdateDailyStat", params);
	}

	/**
	 * 新增或者在当前值的基础上增加指定的值
	 * 
	 * @param uid
	 * @param tableName
	 * @param day
	 * @param value
	 */
	public void insertOrAddDailyStat(int uid, String tableName, int day,
			int value) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tableName", tableName);
		params.put("uid", uid);
		params.put("day", day);
		params.put("value", value);

		this.writeSqlSession.insert("StatDAO.insertOrAddDailyStat", params);
	}

	/**
	 * 执行创建数据库的脚本
	 * 
	 * @param sql
	 */
	public void createTable(String sql) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sql", sql);
		this.writeSqlSession.insert("StatDAO.createTable", params);
	}

	/**
	 * 更新最后执行id
	 * 
	 * @param msgType
	 * @param date
	 * @param lastId
	 */
	public void insertOrUpdateLastStatId(String msgType, Date date, int lastId) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		date = cal.getTime();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("msgType", msgType);
		params.put("date", date);
		params.put("lastId", lastId);

		this.writeSqlSession.insert("StatDAO.insertOrUpdateLastStatId", params);
	}

	/**
	 * 查询最后更新id
	 * 
	 * @param msgType
	 * @param date
	 * @return
	 */
	public int selectLastStatId(String msgType, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		date = cal.getTime();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("msgType", msgType);
		params.put("date", date);

		Integer count = (Integer) this.writeSqlSession.selectOne(
				"StatDAO.selectLastStatId", params);

		if (count == null) {
			return 0;
		}

		return count;
	}

	/**
	 * 获取用户的总统计数
	 * 
	 * @param uid
	 * @return
	 */
	public Object selectUserTotal(int uid) {
		// TODO
		return this.writeSqlSession.selectOne("StatDAO.selectUserTotal", uid);
	}

	public UserStat selectUserTotalReturnUserStat(int uid) {
		return this.writeSqlSession.selectOne(
				"StatDAO.selectUserTotalReturnUserStat", uid);
	}

	/**
	 * 查询指定用户列表的视频播放数
	 * 
	 * @param uids
	 * @return
	 */
	public Map<Integer, Long> selectUserItemViewCounts(List<Integer> uids) {
		String sql = "select userId, viewCount from td_userstat_itemview where userId in ("
				+ StringUtils.join(uids, ",") + ")";

		Map<Integer, Long> stats = this.writeJdbcTemplate.query(sql,
				new ResultSetExtractor<Map<Integer, Long>>() {

					@Override
					public Map<Integer, Long> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<Integer, Long> itemViewCounts = new HashMap<Integer, Long>();

						while (rs.next()) {
							int userId = rs.getInt("userId");
							long viewCount = rs.getLong("viewCount");

							itemViewCounts.put(userId, viewCount);
						}

						return itemViewCounts;
					}
				});

		return stats;
	}

	/**
	 * 批量获取用户的相关数据
	 * 
	 * @param uids
	 * @return
	 */
	public List<HomeUserStat> selectUserStatByUids(List<Integer> uids) {
		String sql = "select uid, itemCount, sub,date1 from td_userstat_total where uid in ("
				+ StringUtils.join(uids, ",") + ")";

		List<HomeUserStat> stats = this.writeJdbcTemplate.query(sql,
				new ResultSetExtractor<List<HomeUserStat>>() {

					@Override
					public List<HomeUserStat> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<HomeUserStat> stats = new ArrayList<HomeUserStat>();
						while (rs.next()) {
							int items = rs.getInt("itemCount");
							int fans = rs.getInt("sub");
							int playlists = rs.getInt("date1");

							HomeUserStat stat = new HomeUserStat();
							stat.uid = rs.getInt("uid");
							stat.items = items;
							stat.playlists = playlists;
							stat.fans = fans;

							stats.add(stat);
						}

						return stats;
					}
				});

		return stats;
	}

	/**
	 * 查询用户视频数
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<HomeUserStat> selectUserItemCounts(int start, int size) {
		String sql = "select uid,itemCount,date1 from td_userstat_total where uid > "
				+ start + " limit " + size;

		return this.writeJdbcTemplate.query(sql,
				new ResultSetExtractor<List<HomeUserStat>>() {

					@Override
					public List<HomeUserStat> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<HomeUserStat> stats = new ArrayList<HomeUserStat>();

						while (rs.next()) {
							HomeUserStat stat = new HomeUserStat();
							stat.uid = rs.getInt("uid");
							stat.items = rs.getInt("itemCount");
							stat.playlists = rs.getInt("date1");
							stats.add(stat);
						}

						return stats;
					}
				});
	}
}
