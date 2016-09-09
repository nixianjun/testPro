package com.tudou.userstat.service.statistics;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import com.tudou.userstat.dao.StatDAO;

/**
 * 统计类的虚类
 * 
 * @author clin
 * 
 */
public abstract class AbstractStat {

	public static final String STAT_KEY_ITEMCOUNT = "itemcount";

	public static final String STAT_KEY_COMMENTCOUNT = "commentcount";

	public static final String STAT_KEY_COMMENTCOUNT_SP = "commentcountsp";

	public static final String STAT_KEY_SUBCOUNT = "subcount";

	public static final String STAT_KEY_DIGCOUNT = "digcount";
	
	public static final String STAT_KEY_PLAYLISTCOUNT = "playlistcount";
	
	public static final String COLUMN_PLAYLIST = "date1";

	@Resource
	StatDAO statDAO;

	/**
	 * 设置当前统计的最大的id
	 * 
	 * @param key
	 */
	public void setLastStatId(String key, Date date, int id) {
		statDAO.insertOrUpdateLastStatId(key, date, id);
	}

	/**
	 * 获取指定统计类型最大的id
	 * 
	 * @param key
	 * @return
	 */
	public int getLastStatId(String key, Date date) {
		return statDAO.selectLastStatId(key, date);
	}

	/**
	 * 计算当前的数据
	 */
	public void statistics() {
		Calendar cal = Calendar.getInstance();
		this.statistics(cal.getTime());
	}

	public abstract void statistics(Date date);
}
