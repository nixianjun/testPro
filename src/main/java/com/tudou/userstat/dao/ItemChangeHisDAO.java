package com.tudou.userstat.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.tudou.userstat.model.TDMsg;
import com.tudou.utils.lang.DateUtil;

/**
 * 读取itemchangehis表的内容
 * 
 * @author clin
 * 
 */
@Repository
public class ItemChangeHisDAO extends AbstractDAO {

	/**
	 * 获取指定日期发生的视频更新的消息列表
	 * 
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<TDMsg> selectLastItemChanges(Date date, int start, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", DateUtil.dateToString(date, "yyyyMMdd"));
		params.put("start", start);
		params.put("limit", limit);

		return itemChSqlSession.selectList(
				"ItemChangeHisDAO.selectLastItemChanges", params);
	}

	/**
	 * 获取最近的评论更新数据列表
	 * 
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<TDMsg> selectLastCommentChanges(Date date, int start, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", DateUtil.dateToString(date, "yyyyMMdd"));
		params.put("start", start);
		params.put("limit", limit);

		return itemChSqlSession.selectList(
				"ItemChangeHisDAO.selectLastCommentChanges", params);
	}

	/**
	 * 获取最新的订阅数据
	 * 
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<TDMsg> selectLastSubChanges(Date date, int start, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", DateUtil.dateToString(date, "yyyyMMdd"));
		params.put("start", start);
		params.put("limit", limit);

		return itemChSqlSession.selectList(
				"ItemChangeHisDAO.selectLastSubChanges", params);
	}

	/**
	 * 查询豆单更新
	 * 
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<TDMsg> selectLastPlaylistChanges(Date date, int start, int limit) {
		String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
		String sql = "select user_id, id,msg_type from td_playlistchangehis_"
				+ dateStr + "  order by id desc limit " + start + " ," + limit;
		return itemChJdbcTemplate.query(sql,
				new ResultSetExtractor<List<TDMsg>>() {
					@Override
					public List<TDMsg> extractData(ResultSet rs)
							throws SQLException, DataAccessException {

						List<TDMsg> list = new ArrayList<TDMsg>();

						while (rs.next()) {
							int type = rs.getInt("msg_type");

							if (type == 5 || type == 6 || type == 8) {
								TDMsg msg = new TDMsg();
								msg.setId(rs.getInt("id"));
								msg.setUid(rs.getInt("user_id"));

								list.add(msg);
							}
						}

						return list;
					}
				});
	}

	/**
	 * 获取最新的挖数据
	 * 
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	public List<TDMsg> selectLastDigChanges(Date date, int start, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("date", DateUtil.dateToString(date, "yyyyMMdd"));
		params.put("start", start);
		params.put("limit", limit);

		return itemChSqlSession.selectList(
				"ItemChangeHisDAO.selectLastDigChanges", params);
	}
}
