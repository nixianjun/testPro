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

import com.tudou.digservicenew.client.DigBuryClient;
import com.tudou.userstat.constant.Constants;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.MsgContain;
import com.tudou.userstat.model.TDMsg;
import com.tudou.userstat.service.UserItemService;
import com.tudou.userstat.service.analyze.Analyzer;
import com.tudou.utils.lang.DateUtil;

/**
 * 用户视频被挖的数据统计
 * 
 * @author clin
 * 
 */
@Service
public class UserDigCountStat extends AbstractStat {

	private static final Logger totaldataLogger = Logger.getLogger("totaldata");

	private static final Logger logger = Logger
			.getLogger(UserDigCountStat.class);

	@Resource
	DigBuryClient digBuryClient;

	@Resource
	private Analyzer analyzer;

	@Resource
	UserItemService userItemService;

	volatile private boolean running;

	@Override
	public void statistics(Date date) {
		try {
			synchronized (this) {
				if (running) {
					return;
				}

				running = true;
			}

			logger.info("start stat user dig count "
					+ DateUtil.dateToString(date, "yyyyMMdd"));

			long start = System.currentTimeMillis();

			// 从容器中获取消息列表
			List<TDMsg> list = MsgContain.getMsgList(MsgType.dig, date);

			// 处理解析消息内容
			analyzer.analyzer(list, MsgType.dig);

			if (list == null || list.size() == 0) {
				return;
			}

			// 查看是否有更新
			int lastId = this.getLastStatId(STAT_KEY_DIGCOUNT, date);

			TDMsg lastMsg = list.get(0);

			if (lastMsg.getId() <= lastId) {
				// 没有更新的消息
				return;
			}

			// 获取发生变化的用户id
			Map<Integer, Integer> uids = new HashMap<Integer, Integer>();
			for (TDMsg msg : list) {
				if (msg.getId() < lastId) {
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

			String month = DateUtil.dateToString(date, "yyyyMM");
			String tableName = "td_stat_digcount_" + month;

			// 计算视频被评论的数量
			for (Entry<Integer, Integer> user : uids.entrySet()) {
				// 计算总数
				this.statisticsTotal(user.getKey());
				// 计算每天的数
				this.statisticsDaily(user.getKey(), tableName, day,
						user.getValue());
			}

			logger.info("finish stat user dig count "
					+ DateUtil.dateToString(date, "yyyyMMdd") + " "
					+ (System.currentTimeMillis() - start));

			this.setLastStatId(STAT_KEY_DIGCOUNT, date, lastMsg.getId());
		} finally {
			running = false;
		}
	}

	/**
	 * 统计每日的数据
	 * 
	 * @param uid
	 * @param tableName
	 * @param day
	 * @param count
	 */
	private void statisticsDaily(Integer uid, String tableName, int day,
			Integer count) {
		// 直接根据更新列表的数据进行累加
		statDAO.insertOrAddDailyStat(uid, tableName, day, count);
	}

	/**
	 * 统计用户总的挖数
	 * 
	 * @param key
	 */
	private int statisticsTotal(Integer uid) {
		// 获取用户的所有视频列表
		long step1 = System.currentTimeMillis();

		List<Integer> itemIds = userItemService.getUserItemIds(uid);

		long step2 = System.currentTimeMillis();

		if (itemIds == null || itemIds.size() == 0) {
			return 0;
		}

		// 从客户端获取每个视频的被评论数
		int total = 0;
		int size = itemIds.size();

		for (int i = 20, j = 0; j < size; j = i, i += 20) {
			if (i >= size) {
				i = size;
			}

			Map<Integer, Integer> digs;
			try {
				digs = digBuryClient.getMultiDigCount(itemIds.subList(j, i));

				// comments =
				// commentClient.getMultiCommentCount(itemIds.subList(
				// j, i));

				for (Entry<Integer, Integer> e : digs.entrySet()) {
					total += e.getValue();
				}
			} catch (Exception e1) {
				logger.error("", e1);
			}

		}

		logger.info("add dig count " + uid + " " + total);

		long step3 = System.currentTimeMillis();

		// 更新数据库
		if (total > 0) {
			statDAO.insertOrUpdateStat(uid, "dig", total);
		}

		long step4 = System.currentTimeMillis();

		logger.info("stat user dig count " + uid + " " + (step2 - step1) + " "
				+ (step3 - step2) + " " + (step4 - step3));

		return total;
	}

	public void statAll() {
		for (int i = 1; i < 121782323; i++) {
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
