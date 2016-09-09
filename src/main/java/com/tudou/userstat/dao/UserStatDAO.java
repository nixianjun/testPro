package com.tudou.userstat.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.tudou.utils.lang.DateUtil;

/**
 * 用户频道相关的统计
 * 
 * @author clin
 * 
 */
@Repository
public class UserStatDAO extends AbstractDAO {

	/**
	 * 获取每天播客被订阅的总数
	 * 
	 * @return
	 */
	public int getUserSubTotalCount(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		Date endTime = cal.getTime();

		String sql = "SELECT count(*) c FROM subscription.td_sub_channel where createtime>='"
				+ DateUtil.dateToString(date, "yyyy-MM-dd")
				+ "' and createtime<'"
				+ DateUtil.dateToString(endTime, "yyyy-MM-dd") + "';";

		Integer query = this.subJdbcTemplate.query(sql,
				new ResultSetExtractor<Integer>() {

					@Override
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getInt("c");
						}

						return 0;
					}
				});

		if (query == null) {
			query = 0;
		}

		return query;
	}

	/**
	 * 获取剧集订阅数
	 * 
	 * @return
	 */
	public int getAlbumSubTotalCount(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		Date endTime = cal.getTime();

		String sql = "SELECT count(*) c FROM subscription.td_sub_album where createtime>='"
				+ DateUtil.dateToString(date, "yyyy-MM-dd")
				+ "' and createtime<'"
				+ DateUtil.dateToString(endTime, "yyyy-MM-dd") + "';";

		Integer query = this.subJdbcTemplate.query(sql,
				new ResultSetExtractor<Integer>() {

					@Override
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getInt("c");
						}

						return 0;
					}
				});

		if (query == null) {
			query = 0;
		}

		return query;
	}

	/**
	 * 获取指定日期订阅数最多的播客列表
	 * 
	 * @param date
	 * @return
	 */
	public LinkedHashMap<Integer, Integer> getTopSubUsers(Date date, int count) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		date = cal.getTime();

		String month = DateUtil.dateToString(date, "yyyyMM");
		final String day = "day" + cal.get(Calendar.DAY_OF_MONTH);

		String sql = "SELECT uid," + day + " FROM userstat.td_stat_subcount_"
				+ month + " order by " + day + " desc limit 50;";

		LinkedHashMap<Integer, Integer> users = this.writeJdbcTemplate.query(
				sql, new ResultSetExtractor<LinkedHashMap<Integer, Integer>>() {

					@Override
					public LinkedHashMap<Integer, Integer> extractData(
							ResultSet rs) throws SQLException,
							DataAccessException {
						LinkedHashMap<Integer, Integer> users = new LinkedHashMap<Integer, Integer>();

						while (rs.next()) {
							int uid = rs.getInt("uid");
							int count = rs.getInt(day);

							users.put(uid, count);
						}

						return users;
					}
				});

		return users;
	}

}
