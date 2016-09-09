package com.tudou.userstat.service.mapping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.dao.ItemChangeHisDAO;
import com.tudou.userstat.model.MsgContain;
import com.tudou.userstat.model.TDMsg;

/**
 * 数据映射的基础类
 * 
 * @author clin
 * 
 */
public abstract class AbstractMapping {
	
	@Resource
	protected ItemChangeHisDAO itemChangeHisDAO;

	private static final Logger logger = Logger
			.getLogger(AbstractMapping.class);

	public static final Comparator<TDMsg> comp = new Comparator<TDMsg>() {
		@Override
		public int compare(TDMsg o1, TDMsg o2) {
			return o2.getId() - o1.getId();
		}
	};

	/**
	 * 加载最近的数据
	 * 
	 * @param date
	 * @param loadMinute
	 * @param maxCount
	 */
	public void loadChangeData(MsgType type, Date date, int loadMinute,
			int maxCount) {
		// TODO 视频消息可能需要延迟几秒进行处理
		logger.info("start load change data :: " + type);

		List<TDMsg> result = new ArrayList<TDMsg>();

		int start = 0;
		int limit = 1000;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -loadMinute);
		Date endTime = cal.getTime();

		// 是否已经结束
		boolean end = false;

		// 是否去重
		boolean reDedup = false;

		if (maxCount < 100000) {
			reDedup = true;
		}

		Set<Integer> midSet = new HashSet<Integer>();

		while (!end) {
			List<TDMsg> list = null;

			for (int i = 0; i < 2; i++) {
				// 如果出现数据库错误,那么进行多次重试
				try {
					list = selectDataFromDB(date, start, limit);
					break;
				} catch (Exception e) {
					logger.error("get change his data error " + type, e);
				}
			}

			if (list == null || list.size() == 0) {
				break;
			}

			logger.info("load data " + type + " " + list.size());

			if (reDedup) {
				// 进行去重,防止重复
				Iterator<TDMsg> it = list.iterator();

				while (it.hasNext()) {
					TDMsg msg = it.next();

					if (!midSet.contains(msg.getId())) {
						midSet.add(msg.getId());
						result.add(msg);
					}
				}
			} else {
				result.addAll(list);
			}

			// 如果结果的最大数量大于给定的最大数量那么放弃继续加重
			if (result.size() > maxCount) {
				break;
			}

			// 从数据库读取数据,保证有最近5分钟的数据
			for (TDMsg msg : list) {
				Date msgDate = msg.getAdd_time();
				if (msgDate != null && msgDate.before(endTime)) {
					// 如果数据已经包含5分钟的完整数据,那么不再继续获取
					end = true;
					break;
				}
			}

			// 如果数量不够,那么偏移量增加
			start += limit;
		}

		// 根据消息的id,从大到小,进行排序
		Collections.sort(result, comp);

		// 将消息放入容器中
		MsgContain.putMsgList(type, date, result);
	}

	/**
	 * 从数据库读取数据
	 * 
	 * @param date
	 * @param start
	 * @param limit
	 * @return
	 */
	public abstract List<TDMsg> selectDataFromDB(Date date, int start, int limit);

}
