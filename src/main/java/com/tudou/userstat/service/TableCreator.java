package com.tudou.userstat.service;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.userstat.dao.StatDAO;
import com.tudou.utils.lang.DateUtil;

/**
 * 定时创建数据库表
 * 
 * @author clin
 * 
 */
@Service
public class TableCreator {

	private static final Logger logger = Logger.getLogger(TableCreator.class);

	@Resource
	StatDAO statDAO;
	
	public static void main(String[] args) {
		System.out.println(ITEM_COUNT_SQL);
		System.out.println(COMMENT_COUNT_SQL);
		System.out.println(SUB_COUNT_SQL);
		System.out.println(DIG_COUNT_SQL);
	}

	public static final String ITEM_COUNT_SQL = "CREATE TABLE `td_stat_itemcount_$month$` "
			+ "(  `uid` int(11) NOT NULL,  `day1` int(10) unsigned DEFAULT '0',  "
			+ "`day2` int(10) unsigned DEFAULT '0',  `day3` int(10) unsigned DEFAULT '0', "
			+ " `day4` int(10) unsigned DEFAULT '0',  `day5` int(10) unsigned DEFAULT '0',  "
			+ "`day6` int(10) unsigned DEFAULT '0',  `day7` int(10) unsigned DEFAULT '0',  "
			+ "`day8` int(10) unsigned DEFAULT '0',  `day9` int(10) unsigned DEFAULT '0',  "
			+ "`day10` int(10) unsigned DEFAULT '0',  `day11` int(10) unsigned DEFAULT '0',  "
			+ "`day12` int(10) unsigned DEFAULT '0',  `day13` int(10) unsigned DEFAULT '0',  "
			+ "`day14` int(10) unsigned DEFAULT '0',  `day15` int(10) unsigned DEFAULT '0',  "
			+ "`day16` int(10) unsigned DEFAULT '0',  `day17` int(10) unsigned DEFAULT '0',  "
			+ "`day18` int(10) unsigned DEFAULT '0',  `day19` int(10) unsigned DEFAULT '0',  "
			+ "`day20` int(10) unsigned DEFAULT '0',  `day21` int(10) unsigned DEFAULT '0',  "
			+ "`day22` int(10) unsigned DEFAULT '0',  `day23` int(10) unsigned DEFAULT '0',  "
			+ "`day24` int(10) unsigned DEFAULT '0',  `day25` int(10) unsigned DEFAULT '0',  "
			+ "`day26` int(10) unsigned DEFAULT '0',  `day27` int(10) unsigned DEFAULT '0',  "
			+ "`day28` int(10) unsigned DEFAULT '0',  `day29` int(10) unsigned DEFAULT '0',  "
			+ "`day30` int(10) unsigned DEFAULT '0',  `day31` int(10) unsigned DEFAULT '0',  "
			+ "`totalCount` int(10) unsigned DEFAULT '0',  PRIMARY KEY (`uid`)) ENGINE=innodb DEFAULT CHARSET=utf8 COLLATE=utf8_bin";

	public static final String COMMENT_COUNT_SQL = "CREATE TABLE `td_stat_commentcount_$month$` "
			+ "(  `uid` int(11) NOT NULL,  `day1` int(10) unsigned DEFAULT '0',  "
			+ "`day2` int(10) unsigned DEFAULT '0',  `day3` int(10) unsigned DEFAULT '0', "
			+ " `day4` int(10) unsigned DEFAULT '0',  `day5` int(10) unsigned DEFAULT '0',  "
			+ "`day6` int(10) unsigned DEFAULT '0',  `day7` int(10) unsigned DEFAULT '0',  "
			+ "`day8` int(10) unsigned DEFAULT '0',  `day9` int(10) unsigned DEFAULT '0',  "
			+ "`day10` int(10) unsigned DEFAULT '0',  `day11` int(10) unsigned DEFAULT '0',  "
			+ "`day12` int(10) unsigned DEFAULT '0',  `day13` int(10) unsigned DEFAULT '0',  "
			+ "`day14` int(10) unsigned DEFAULT '0',  `day15` int(10) unsigned DEFAULT '0',  "
			+ "`day16` int(10) unsigned DEFAULT '0',  `day17` int(10) unsigned DEFAULT '0',  "
			+ "`day18` int(10) unsigned DEFAULT '0',  `day19` int(10) unsigned DEFAULT '0',  "
			+ "`day20` int(10) unsigned DEFAULT '0',  `day21` int(10) unsigned DEFAULT '0',  "
			+ "`day22` int(10) unsigned DEFAULT '0',  `day23` int(10) unsigned DEFAULT '0',  "
			+ "`day24` int(10) unsigned DEFAULT '0',  `day25` int(10) unsigned DEFAULT '0',  "
			+ "`day26` int(10) unsigned DEFAULT '0',  `day27` int(10) unsigned DEFAULT '0',  "
			+ "`day28` int(10) unsigned DEFAULT '0',  `day29` int(10) unsigned DEFAULT '0',  "
			+ "`day30` int(10) unsigned DEFAULT '0',  `day31` int(10) unsigned DEFAULT '0',  "
			+ "`totalCount` int(10) unsigned DEFAULT '0',  PRIMARY KEY (`uid`)) ENGINE=innodb DEFAULT CHARSET=utf8 COLLATE=utf8_bin";

	public static final String SUB_COUNT_SQL = "CREATE TABLE `td_stat_subcount_$month$` "
			+ "(  `uid` int(11) NOT NULL,  `day1` int(10) unsigned DEFAULT '0',  "
			+ "`day2` int(10) unsigned DEFAULT '0',  `day3` int(10) unsigned DEFAULT '0', "
			+ " `day4` int(10) unsigned DEFAULT '0',  `day5` int(10) unsigned DEFAULT '0',  "
			+ "`day6` int(10) unsigned DEFAULT '0',  `day7` int(10) unsigned DEFAULT '0',  "
			+ "`day8` int(10) unsigned DEFAULT '0',  `day9` int(10) unsigned DEFAULT '0',  "
			+ "`day10` int(10) unsigned DEFAULT '0',  `day11` int(10) unsigned DEFAULT '0',  "
			+ "`day12` int(10) unsigned DEFAULT '0',  `day13` int(10) unsigned DEFAULT '0',  "
			+ "`day14` int(10) unsigned DEFAULT '0',  `day15` int(10) unsigned DEFAULT '0',  "
			+ "`day16` int(10) unsigned DEFAULT '0',  `day17` int(10) unsigned DEFAULT '0',  "
			+ "`day18` int(10) unsigned DEFAULT '0',  `day19` int(10) unsigned DEFAULT '0',  "
			+ "`day20` int(10) unsigned DEFAULT '0',  `day21` int(10) unsigned DEFAULT '0',  "
			+ "`day22` int(10) unsigned DEFAULT '0',  `day23` int(10) unsigned DEFAULT '0',  "
			+ "`day24` int(10) unsigned DEFAULT '0',  `day25` int(10) unsigned DEFAULT '0',  "
			+ "`day26` int(10) unsigned DEFAULT '0',  `day27` int(10) unsigned DEFAULT '0',  "
			+ "`day28` int(10) unsigned DEFAULT '0',  `day29` int(10) unsigned DEFAULT '0',  "
			+ "`day30` int(10) unsigned DEFAULT '0',  `day31` int(10) unsigned DEFAULT '0',  "
			+ "`totalCount` int(10) unsigned DEFAULT '0',  PRIMARY KEY (`uid`)) ENGINE=innodb DEFAULT CHARSET=utf8 COLLATE=utf8_bin";

	public static final String DIG_COUNT_SQL = "CREATE TABLE `td_stat_digcount_$month$` "
			+ "(  `uid` int(11) NOT NULL,  `day1` int(10) unsigned DEFAULT '0',  "
			+ "`day2` int(10) unsigned DEFAULT '0',  `day3` int(10) unsigned DEFAULT '0', "
			+ " `day4` int(10) unsigned DEFAULT '0',  `day5` int(10) unsigned DEFAULT '0',  "
			+ "`day6` int(10) unsigned DEFAULT '0',  `day7` int(10) unsigned DEFAULT '0',  "
			+ "`day8` int(10) unsigned DEFAULT '0',  `day9` int(10) unsigned DEFAULT '0',  "
			+ "`day10` int(10) unsigned DEFAULT '0',  `day11` int(10) unsigned DEFAULT '0',  "
			+ "`day12` int(10) unsigned DEFAULT '0',  `day13` int(10) unsigned DEFAULT '0',  "
			+ "`day14` int(10) unsigned DEFAULT '0',  `day15` int(10) unsigned DEFAULT '0',  "
			+ "`day16` int(10) unsigned DEFAULT '0',  `day17` int(10) unsigned DEFAULT '0',  "
			+ "`day18` int(10) unsigned DEFAULT '0',  `day19` int(10) unsigned DEFAULT '0',  "
			+ "`day20` int(10) unsigned DEFAULT '0',  `day21` int(10) unsigned DEFAULT '0',  "
			+ "`day22` int(10) unsigned DEFAULT '0',  `day23` int(10) unsigned DEFAULT '0',  "
			+ "`day24` int(10) unsigned DEFAULT '0',  `day25` int(10) unsigned DEFAULT '0',  "
			+ "`day26` int(10) unsigned DEFAULT '0',  `day27` int(10) unsigned DEFAULT '0',  "
			+ "`day28` int(10) unsigned DEFAULT '0',  `day29` int(10) unsigned DEFAULT '0',  "
			+ "`day30` int(10) unsigned DEFAULT '0',  `day31` int(10) unsigned DEFAULT '0',  "
			+ "`totalCount` int(10) unsigned DEFAULT '0',  PRIMARY KEY (`uid`)) ENGINE=innodb DEFAULT CHARSET=utf8 COLLATE=utf8_bin";

	/**
	 * 创建统计视频每日上传量的表
	 * 
	 * @param month
	 */
	public void createItemCountDailyTable(String month) {
		String sql = ITEM_COUNT_SQL.replace("$month$", month);
		try {
			statDAO.createTable(sql);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 创建统计视频每日上传量的表
	 * 
	 * @param month
	 */
	public void createCommentCountDailyTable(String month) {
		String sql = COMMENT_COUNT_SQL.replace("$month$", month);
		try {
			statDAO.createTable(sql);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 创建订阅每日统计表
	 * 
	 * @param month
	 */
	public void createSubCountDailyTable(String month) {
		String sql = SUB_COUNT_SQL.replace("$month$", month);
		try {
			statDAO.createTable(sql);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 创建挖数每日统计表
	 * 
	 * @param month
	 */
	public void createDigCountDailyTable(String month) {
		String sql = DIG_COUNT_SQL.replace("$month$", month);
		try {
			statDAO.createTable(sql);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 创建本月的数据库表和下个月的数据库表
	 */
	public void createDailyTable() {
		logger.info("start create daily table==============");
		Calendar cal = Calendar.getInstance();

		Date date = cal.getTime();
		String month = DateUtil.dateToString(date, "yyyyMM");
		createItemCountDailyTable(month);
		createCommentCountDailyTable(month);
		createSubCountDailyTable(month);
		createDigCountDailyTable(month);

		cal.add(Calendar.MONTH, 1);
		date = cal.getTime();
		month = DateUtil.dateToString(date, "yyyyMM");
		createItemCountDailyTable(month);
		createCommentCountDailyTable(month);
		createSubCountDailyTable(month);
		createDigCountDailyTable(month);
	}
}
