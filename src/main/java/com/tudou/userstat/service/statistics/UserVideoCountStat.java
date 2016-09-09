package com.tudou.userstat.service.statistics;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.userstat.cache.UserItemLocalCache;
import com.tudou.userstat.constant.Constants;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.constant.StatKeys;
import com.tudou.userstat.dao.ItemDAO;
import com.tudou.userstat.dao.StatDAO;
import com.tudou.userstat.model.HomeUserStat;
import com.tudou.userstat.model.MsgContain;
import com.tudou.userstat.model.TDMsg;
import com.tudou.userstat.service.UserItemService;
import com.tudou.userstat.service.analyze.Analyzer;
import com.tudou.utils.lang.DateUtil;

/**
 * 用户上传视频数的统计
 * 
 * @author clin
 * 
 */
@Service
public class UserVideoCountStat extends AbstractStat {

	private static final Logger logger = Logger
			.getLogger(UserVideoCountStat.class);

	private static final Logger totaldataLogger = Logger.getLogger("totaldata");

	@Resource
	UserItemService userItemService;

	@Resource
	Analyzer analyzer;

	@Resource
	ItemDAO itemDAO;

	@Resource
	StatDAO statDAO;

	public void statistics(Date date) {
		logger.info("start stat user item count "
				+ DateUtil.dateToString(date, "yyyyMMdd"));

		// 从容器中获取消息列表
		List<TDMsg> list = MsgContain.getMsgList(MsgType.item, date);

		// 处理解析消息内容
		analyzer.analyzer(list, MsgType.item);

		if (list == null || list.size() == 0) {
			return;
		}

		logger.info("start stat user item count list::" + list.size());

		// 查看是否有更新
		int lastId = this.getLastStatId(STAT_KEY_ITEMCOUNT, date);
		TDMsg lastMsg = list.get(0);

		if (lastMsg.getId() <= lastId) {
			// 没有更新的消息
			return;
		} else {
			lastId = lastMsg.getId();
		}

		// 获取发生变化的用户id
		Set<Integer> uids = new HashSet<Integer>();
		for (TDMsg msg : list) {
			// 只获取新增视频
			if (msg.getOper_type() == Constants.OPER_TYPE_ADD
					&& msg.getUid() > 0) {
				uids.add(msg.getUid());
			}
		}

		// 计算中上传视频数
		for (Integer uid : uids) {
			// TODO 删除用户在本地的视频列表缓存
			UserItemLocalCache.remove(uid);
			this.statisticsTotal(uid);
		}

		// 计算当天的上传视频数
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		cal.add(Calendar.DATE, 1);
		Date endDate = cal.getTime();

		// 组装表名
		String month = DateUtil.dateToString(date, "yyyyMM");
		String tableName = "td_stat_itemcount_" + month;

		String startDateStr = DateUtil.dateToString(date, "yyyy-MM-dd");
		String endDateStr = DateUtil.dateToString(endDate, "yyyy-MM-dd");

		for (Integer uid : uids) {
			statisticsDaily(uid, startDateStr, endDateStr, tableName, day);
		}

		// 设置当前处理的最后一个id
		this.setLastStatId(STAT_KEY_ITEMCOUNT, date, lastId);
	}

	/**
	 * 计算用户指定日期上传的视频数
	 * 
	 * @param uid
	 */
	public void statisticsDaily(int uid, String startDate, String endDate,
			String tableName, int day) {
		// 获取指定日期用户的视频数
		int count = this.itemDAO.getItemCountByUidDate(uid, startDate, endDate);

		if (count > 0) {
			// 将数据存入日期表
			this.statDAO.insertOrUpdateDailyStat(uid, tableName, day, count);
		}
	}

	/**
	 * 计算用户的总视频数量
	 * 
	 * @param uid
	 */
	public int statisticsTotal(int uid) {
		if (uid <= 0) {
			return 0;
		}

		// 从item表中查询到用户视频数
		for (int i = 0; i < 2; i++) {
			// 如果出现错误,那么进行若干次重试
			try {
				int count = this.getItemCountByUid(uid);
				logger.info("stat user item count " + uid + " count " + count);
				// 更新到统计表中
				if (count > 0) {
					statDAO.insertOrUpdateStat(uid, StatKeys.ITEMCOUNT, count);
				} else {
					// 将视频数清空为0
					statDAO.updateStat(uid, StatKeys.ITEMCOUNT, 0);
				}
				return count;
			} catch (Exception e) {
				logger.error("update user video count error " + uid, e);
			}
		}

		return 0;
	}

	/**
	 * 根据用户id获取用户视频数量
	 * 
	 * @param uid
	 * @return
	 */
	private int getItemCountByUid(int uid) {
		int count = itemDAO.getItemCountByUserId(uid);

		if (count < 5000) {
			count = userItemService.getUserLegalVideoCount(uid);
		}

		return count;
		// // 设置查询条件
		// Criteria cra = s3ServiceClient.createCriteria(AppMode.UGC)
		// .giveSecurityCheck(SecurityCheckTypeMode.可播).openFieldCheck(false).closeAutoItemScore();
		// // 结果集
		// cra.setFirstResult(0).setMaxResults(1);
		// cra.addFieldList("itemId");
		//
		// // 约束条件
		// cra.add(Restrictions.eq("allShield", "0"));
		// cra.add(Restrictions.eq("ownerId", String.valueOf(uid)));
		//
		// JSONObject jsonResult = JSON.parseObject(cra.toJson());
		//
		// int count = jsonResult.getIntValue("numFound");
		// return count;
	}

	/**
	 * 统计所有用户的视频数
	 * 
	 * @return
	 */
	public void statAll() {
		// TODO
		// for (int i = 86723820; i < 121782323; i++) {
		// int count = this.statisticsTotal(i);
		// totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
		// + count);
		// }

		for (int i = 314261220; i < 360000000; i++) {
			int count = this.statisticsTotal(i);
			totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
					+ count);
		}
	}

	public List<HomeUserStat> collectUser(int start, int size) {
		// 从统计表获取用户视频数
		return statDAO.selectUserItemCounts(start, size);
	}

	public void collectAllUser() {
		int index = 0;
		while (true) {
			List<HomeUserStat> userMap = this.collectUser(index, 5000);

			for (HomeUserStat e : userMap) {
				int uid = e.uid;

				if (uid > index) {
					index = uid;
				}

				int count = e.items;
				if (count > 0) {
					// 获取用户视频数
					count = this.statisticsTotal(uid);

					totaldataLogger.info("totaldataLogger uid:: " + uid
							+ " count:: " + count);
				}
			}
		}
	}
}
