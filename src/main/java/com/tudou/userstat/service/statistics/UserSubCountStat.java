package com.tudou.userstat.service.statistics;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.subscription.client.SubscriptionClient;
import com.tudou.subscription.model.SourceType;
import com.tudou.userstat.constant.Constants;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.dao.StatDAO;
import com.tudou.userstat.model.MsgContain;
import com.tudou.userstat.model.TDMsg;
import com.tudou.userstat.service.analyze.Analyzer;
import com.tudou.utils.lang.DateUtil;

/**
 * 用户订阅数的统计
 * 
 * @author clin
 * 
 */
@Service
public class UserSubCountStat extends AbstractStat {

	private static final Logger totaldataLogger = Logger.getLogger("totaldata");

	@Resource
	SubscriptionClient subscriptionClient;

	@Resource
	StatDAO statDAO;

	private static final Logger logger = Logger
			.getLogger(UserSubCountStat.class);

	@Resource
	Analyzer analyzer;

	@Override
	public void statistics(Date date) {
		// 从容器中获取消息列表
		List<TDMsg> list = MsgContain.getMsgList(MsgType.sub, date);

		if (list == null || list.size() == 0) {
			return;
		}

		// 查看是否有更新
		int lastId = this.getLastStatId(STAT_KEY_SUBCOUNT, date);
		TDMsg lastMsg = list.get(0);

		if (lastMsg.getId() <= lastId) {
			// 没有更新的消息
			return;
		}

		// 获取发生变化的用户id
		Map<Integer, Integer> uids = new HashMap<Integer, Integer>();
		for (TDMsg msg : list) {
			if (msg.getId() <= lastId) {
				continue;
			}

			if (msg.getOper_type() == Constants.OPER_TYPE_ADD
					&& msg.getUid() > 0) {
				Integer count = uids.get(msg.getUid());

				if (count == null) {
					uids.put(msg.getUid(), 1);
				} else {
					uids.put(msg.getUid(), count + 1);
				}
			}
		}

		// 计算当天的上传视频数
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// 组装表名
		String month = DateUtil.dateToString(date, "yyyyMM");
		String tableName = "td_stat_subcount_" + month;

		logger.info("stat user sub counts ::: " + uids);

		for (Entry<Integer, Integer> e : uids.entrySet()) {
			statisticsTotal(e.getKey());
			statisticsDaily(e.getKey(), tableName, day, e.getValue());
		}

		// 设置当前处理的最后一个id
		this.setLastStatId(STAT_KEY_SUBCOUNT, date, lastMsg.getId());
	}

	/**
	 * 统计每天的数据
	 * 
	 * @param uid
	 * @param startDateStr
	 * @param endDateStr
	 * @param tableName
	 * @param day
	 */
	private void statisticsDaily(int uid, String tableName, int day, int count) {
		statDAO.insertOrAddDailyStat(uid, tableName, day, count);
	}

	/**
	 * 统计总数
	 * 
	 * @param uid
	 */
	private int statisticsTotal(Integer uid) {
		// 通过客户端获取粉丝数
		int count = subscriptionClient.subedSize(uid, SourceType.CHANNEL);

		if (count > 0) {
			// 更新数据库
			statDAO.insertOrUpdateStat(uid, "sub", count);
		}

		return count;
	}

	public void statAll() {
		// TODO
		for (int i = 9027568; i < 121782323; i++) {
			int count = this.statisticsTotal(i);
			totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
					+ count);
		}

		for (int i = 314261220; i < 360000000; i++) {
			int count = this.statisticsTotal(i);
			totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
					+ count);
		}
	}
}
