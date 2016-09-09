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

import com.tudou.digservicenew.client.CommentClient;
import com.tudou.userstat.constant.Constants;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.dao.StatDAO;
import com.tudou.userstat.model.MsgContain;
import com.tudou.userstat.model.TDMsg;
import com.tudou.userstat.service.UserItemService;
import com.tudou.userstat.service.analyze.Analyzer;
import com.tudou.utils.lang.DateUtil;

/**
 * 用户视频被评论数的统计
 * 
 * @author clin
 * 
 */
@Service
public class UserCommentCountStat extends AbstractStat {
	
	private static final Logger totaldataLogger = Logger.getLogger("totaldata");

	private static final Logger logger = Logger
			.getLogger(UserCommentCountStat.class);

	@Resource
	Analyzer analyzer;

	@Resource
	UserItemService userItemService;

	@Resource
	CommentClient commentClient;

	@Resource
	StatDAO statDAO;

	volatile boolean running;

	volatile boolean running2;

	public void statistics(Date date) {
		try {
			synchronized (this) {
				if (running) {
					return;
				}

				running = true;
			}

			logger.info("start stat user comment count "
					+ DateUtil.dateToString(date, "yyyyMMdd"));

			long start = System.currentTimeMillis();

			// 从容器中获取消息列表
			List<TDMsg> list = MsgContain.getMsgList(MsgType.comment, date);

			// 处理解析消息内容
			analyzer.analyzer(list, MsgType.comment);

			if (list == null || list.size() == 0) {
				return;
			}

			// 查看是否有更新
			int lastId = this.getLastStatId(STAT_KEY_COMMENTCOUNT, date);

			logger.info("comment last id " + lastId);

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

				if (msg.getUid() > 0) {
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
			String tableName = "td_stat_commentcount_" + month;

			// 计算视频被评论的数量
			for (Entry<Integer, Integer> user : uids.entrySet()) {
				// 计算总数
				this.statisticsTotal(user.getKey(), 0, false);
				// 计算每天的数
				this.statisticsDaily(user.getKey(), tableName, day,
						user.getValue());
			}

			logger.info("finish stat user comment count "
					+ DateUtil.dateToString(date, "yyyyMMdd") + " "
					+ (System.currentTimeMillis() - start));

			this.setLastStatId(STAT_KEY_COMMENTCOUNT, date, lastMsg.getId());
		} finally {
			running = false;
		}
	}

	public void statisticsSpUser() {
		Calendar cal = Calendar.getInstance();
		this.statisticsSpUser(cal.getTime());
	}

	/**
	 * 统计特殊的用户,一般都是上传视频量比较大的用户
	 * 
	 * @param date
	 */
	public void statisticsSpUser(Date date) {
		try {
			synchronized (this) {
				if (running2) {
					return;
				}

				running2 = true;
			}

			logger.info("start stat sp_+_+ user comment count "
					+ DateUtil.dateToString(date, "yyyyMMdd"));

			long start = System.currentTimeMillis();

			// 从容器中获取消息列表
			List<TDMsg> list = MsgContain.getMsgList(MsgType.comment, date);

			// 处理解析消息内容
			analyzer.analyzer(list, MsgType.comment);

			if (list == null || list.size() == 0) {
				return;
			}

			// 查看是否有更新
			int lastId = this.getLastStatId(STAT_KEY_COMMENTCOUNT_SP, date);

			logger.info("comment last id " + lastId);

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
			String tableName = "td_stat_commentcount_" + month;

			// 计算视频被评论的数量
			for (Entry<Integer, Integer> user : uids.entrySet()) {
				// 计算总数
				this.statisticsTotal(user.getKey(), 0, true);
				// 计算每天的数
				this.statisticsDaily(user.getKey(), tableName, day,
						user.getValue());
			}

			logger.info("finish stat sp_+_+ user comment count "
					+ DateUtil.dateToString(date, "yyyyMMdd") + " "
					+ (System.currentTimeMillis() - start));

			this.setLastStatId(STAT_KEY_COMMENTCOUNT_SP, date, lastMsg.getId());
		} finally {
			running2 = false;
		}
	}

	/**
	 * 统计用户视频被评论的总数
	 * 
	 * @param uid
	 */
	private int statisticsTotal(int uid, int force, boolean spuser) {
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

		if (force == 0) {
			if (!spuser) {
				if (size > userItemService.CACHE_SIZE) {
					// 如果不是特殊用户的统计,那么对于视频数量大于指定值的用户,不进行统计
					return 0;
				}
			} else {
				if (size <= userItemService.CACHE_SIZE) {
					// 如果是普通用户,那么不计算小用户的数据
					return 0;
				}
			}
		}

		for (int i = 20, j = 0; j < size; j = i, i += 20) {
			if (i >= size) {
				i = size;
			}

			Map<Integer, Integer> comments;
			try {
				comments = commentClient.getMultiCommentCount(itemIds.subList(
						j, i));

				for (Entry<Integer, Integer> e : comments.entrySet()) {
					total += e.getValue();
				}
			} catch (Exception e1) {
				logger.error("", e1);
			}

		}

		logger.info("add comment count " + uid + " " + total);

		long step3 = System.currentTimeMillis();

		// 更新数据库
		if (total > 0) {
			statDAO.insertOrUpdateStat(uid, "comment", total);
		}
		
		long step4 = System.currentTimeMillis();

		logger.info("stat user comment count " + uid + " " + (step2 - step1)
				+ " " + (step3 - step2) + " " + (step4 - step3));
		return total;
	}

	/**
	 * 统计用户每天被评论的数量
	 * 
	 * @param uid
	 * @param count
	 */
	private void statisticsDaily(int uid, String tableName, int day, int count) {
		// 直接根据更新列表的数据进行累加
		statDAO.insertOrAddDailyStat(uid, tableName, day, count);
	}
	
	public void statAll() {
		for (int i = 1; i < 121782323; i++) {
			int count = this.statisticsTotal(i, 1, false);
			totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
					+ count);
		}

		for (int i = 314261220; i < 360000000; i++) {
			int count = this.statisticsTotal(i, 1, false);
			totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
					+ count);
		}
	}
}
