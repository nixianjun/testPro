package com.tudou.userstat.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.tudou.userstat.model.UserFullInfo;
import com.tudou.userstat.model.UserMember;

/**
 * 用户信息数据库类
 * 
 * @author clin
 * 
 */
@Repository
public class UserDAO extends AbstractDAO {

	private static final Logger logger = Logger.getLogger(UserDAO.class);

	/**
	 * 获取土豆网会员用户
	 * 
	 * @return
	 */
	public List<UserMember> getMemberUser() {
		return this.tudouSqlSession.selectList("UserDAO.getMemberUser");
	}

	/**
	 * 获取个人认证用户列表
	 * 
	 * @return
	 */
	public List<Integer> getVerifyPerson() {
		return this.umsSqlSession.selectList("UserDAO.getVerifyPerson");
	}

	/**
	 * 获取结构认证用户列表
	 * 
	 * @return
	 */
	public List<Integer> getVerifyOrg() {
		return this.umsSqlSession.selectList("UserDAO.getVerifyOrg");
	}

	/**
	 * 根据用户id获取用户信息
	 * 
	 * @param uids
	 * @return
	 */
	public List<UserFullInfo> getUserInfos(int start, int size) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("start", start);
		params.put("size", size);

		return this.tudouSqlSession.selectList("UserDAO.getUserInfos", params);
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> getNewHomeUsers() {
		final String sql = "select uid,id from td_homepage_user where id>? limit ?";

		Set<Integer> uids = new HashSet<Integer>(50000000);

		int start = 0;
		final int limit = 5000;

		while (true) {
			// TODO
			Map<String, Object> result = this.homeJdbcTemplate.query(sql,
					new Object[] { start, limit },
					new ResultSetExtractor<Map<String, Object>>() {
						@Override
						public Map<String, Object> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							Map<String, Object> result = new HashMap<String, Object>();

							List<Integer> uids = new ArrayList<Integer>();

							while (rs.next()) {
								int uid = rs.getInt("uid");
								int id = rs.getInt("id");
								uids.add(uid);

								result.put("maxId", id);
							}

							result.put("uids", uids);

							return result;
						}
					});

			List<Integer> list = (List<Integer>) result.get("uids");

			if (list == null || list.size() == 0) {
				break;
			}

			start = (Integer) result.get("maxId");
			uids.addAll(list);

			logger.info("get new home user size::" + list.size() + " start:: "
					+ start + " total:: " + uids.size());
		}

		return uids;
	}

	/**
	 * 获取主动降级的个人主页用户列表
	 * 
	 * @return
	 */
	public List<Integer> getDownHomeUsers() {
		final String sql = "select uid from td_homepage_update where `status`=1";

		return this.homeJdbcTemplate.query(sql,
				new ResultSetExtractor<List<Integer>>() {

					@Override
					public List<Integer> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<Integer> uids = new ArrayList<Integer>();

						while (rs.next()) {
							uids.add(rs.getInt("uid"));
						}
						return uids;
					}
				});
	}

	/**
	 * 获取老个人主页的用户id
	 * 
	 * @return
	 */
	public List<Integer> getOldHomeUsers() {
		final String sql = "select userId from td_user_home_style where userId> ? limit ?";

		List<Integer> uids = new ArrayList<Integer>();

		int start = 0;
		final int limit = 5000;

		while (true) {
			List<Integer> list = this.tudouJdbcTemplate.query(sql,
					new Object[] { start, limit },
					new ResultSetExtractor<List<Integer>>() {
						@Override
						public List<Integer> extractData(ResultSet rs)
								throws SQLException, DataAccessException {

							List<Integer> result = new ArrayList<Integer>();
							while (rs.next()) {
								int uid = rs.getInt("userId");
								result.add(uid);
							}

							return result;
						}
					});

			if (list == null || list.size() == 0) {
				break;
			}

			start = list.get(list.size() - 1);
			uids.addAll(list);

			logger.info("get old home user size::" + list.size() + " start:: "
					+ start + " total:: " + uids.size());
		}

		return uids;
	}

	/**
	 * 批量获取粉丝数
	 * 
	 * @param uids
	 * @return
	 */
	public Map<Integer, Integer> getFansNum(List<Integer> uids) {
		String sql = "select subid, size from td_sub_channel_subedsize where subid in ("
				+ StringUtils.join(uids, ",") + ")";
		return this.subJdbcTemplate.query(sql,
				new ResultSetExtractor<Map<Integer, Integer>>() {

					@Override
					public Map<Integer, Integer> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<Integer, Integer> result = new HashMap<Integer, Integer>();

						while (rs.next()) {
							result.put(rs.getInt("subid"), rs.getInt("size"));
						}

						return result;
					}
				});
	}

	/**
	 * 批量获取订阅数
	 * 
	 * @param uids
	 * @return
	 */
	public Map<Integer, Integer> getSubNum(List<Integer> uids) {
		String sql = "select uid, size from td_sub_channel_subsize where uid in ("
				+ StringUtils.join(uids, ",") + ")";
		return this.subJdbcTemplate.query(sql,
				new ResultSetExtractor<Map<Integer, Integer>>() {

					@Override
					public Map<Integer, Integer> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<Integer, Integer> result = new HashMap<Integer, Integer>();

						while (rs.next()) {
							result.put(rs.getInt("uid"), rs.getInt("size"));
						}

						return result;
					}
				});
	}
}
