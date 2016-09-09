package com.tudou.userstat.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.tudou.userstat.model.UserTimeLine;

@Repository
public class UserTimeLineDAO extends AbstractDAO {
	public List<UserTimeLine> getUserTimeLineList(Integer userId) {
		if(userId<=0){
			return null;
		}
		Integer temp=userId;
		Integer temp2=temp%10;
		String table_pre="ums_user_timeline_"+temp2;
		String sql = " SELECT userID, type, objectID, objectContent, occurTime, addText, addTime "+
                " FROM  "+table_pre+"  WHERE userID = ? ";
		List<UserTimeLine> list = userTimeLineJdbcTemplate.query(sql, new Object[]{ userId},
						new BeanPropertyRowMapper<UserTimeLine>(
								UserTimeLine.class));
		//System.out.println(sql);
		return list;
	}
	
	public static void main(String[] args) {
		System.out.println(371%10);
	}
}
