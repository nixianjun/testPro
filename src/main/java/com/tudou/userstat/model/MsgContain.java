package com.tudou.userstat.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tudou.userstat.constant.MsgType;
import com.tudou.utils.lang.DateUtil;

/**
 * 消息的容器
 * 
 * @author clin
 * 
 */
public class MsgContain {
	public static final Map<MsgType, Map<String, List<TDMsg>>> msgMap = new ConcurrentHashMap<MsgType, Map<String, List<TDMsg>>>();

	/**
	 * 将消息列表放入容器中
	 * 
	 * @param type
	 * @param msgList
	 */
	public static void putMsgList(MsgType type, Date date, List<TDMsg> msgList) {
		Map<String, List<TDMsg>> map = msgMap.get(type);

		if (map == null) {
			map = new ConcurrentHashMap<String, List<TDMsg>>();
			msgMap.put(type, map);
		}

		String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
		map.put(dateStr, msgList);
	}

	/**
	 * 根据消息类型获取消息列表
	 * 
	 * @param type
	 * @return
	 */
	public static List<TDMsg> getMsgList(MsgType type, Date date) {
		Map<String, List<TDMsg>> map = msgMap.get(type);

		if (map != null) {
			return map.get(DateUtil.dateToString(date, "yyyyMMdd"));
		}

		return null;
	}

}
