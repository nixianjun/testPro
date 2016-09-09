package com.tudou.userstat.constant;

import org.apache.commons.lang.time.DateUtils;

/**
 * 常量
 * 
 * @author clin
 * 
 */
public class Constants {
	public static final Integer OPER_TYPE_ADD = 1; // 增加
	public static final Integer OPER_TYPE_UPDATE = 2; // 更新
	public static final Integer OPER_TYPE_DELETE = 3;// 删除
	public static final Integer OPER_TYPE_RESTORE = 4;// 恢复
	public static final String DATE_STYLE = "yyyy-MM-dd HH:mm:ss";
	public static final String User_Time_Line_KEY = "user_time_line_";
 	public static final long MILLIS_PER_WEEK = DateUtils.MILLIS_PER_DAY * 7;
	public static final int SECONDS_PER_WEEK = (int) (DateUtils.MILLIS_PER_DAY * 7 / 1000);

}
