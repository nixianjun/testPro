package com.tudou.userstat.service.statistics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.playlist.client.PlaylistServiceClient;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.MsgContain;
import com.tudou.userstat.model.TDMsg;
import com.tudou.utils.lang.DateUtil;

@Service
public class UserPlaylistCountStat extends AbstractStat {

	private static final Logger totaldataLogger = Logger.getLogger("totaldata");

	private static final Logger logger = Logger
			.getLogger(UserPlaylistCountStat.class);

	@Resource
	PlaylistServiceClient playlistServiceClient;

	@Override
	public void statistics(Date date) {
		logger.info("stat playlist start");

		// 从容器中获取消息列表
		List<TDMsg> list = MsgContain.getMsgList(MsgType.playlist, date);

		if (list == null || list.size() == 0) {
			return;
		}

		logger.info("stat playlist get list size " + list.size());

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

			Integer count = uids.get(msg.getUid());

			if (count == null) {
				uids.put(msg.getUid(), 1);
			} else {
				uids.put(msg.getUid(), count + 1);
			}
		}

		// 计算当天的上传视频数
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_MONTH);

		// 组装表名
		String month = DateUtil.dateToString(date, "yyyyMM");
		String tableName = "td_stat_playlistcount_" + month;

		logger.info("stat user playlist counts ::: " + uids);

		for (Entry<Integer, Integer> e : uids.entrySet()) {
			statisticsTotal(e.getKey());
			statisticsDaily(e.getKey(), tableName, day, e.getValue());
		}

		// 设置当前处理的最后一个id
		this.setLastStatId(STAT_KEY_PLAYLISTCOUNT, date, lastMsg.getId());

	}

	private void statisticsDaily(Integer key, String tableName, int day,
			Integer value) {
		// TODO Auto-generated method stub

	}

	private int statisticsTotal(Integer uid) {
		int count = playlistServiceClient.getPlaylistCountByUserId(uid);

		if (count > 0) {
			statDAO.insertOrUpdateStat(uid, COLUMN_PLAYLIST, count);
		}

		return count;
	}

	public void statAll() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"/home/app_admin/td_playlist_uid.txt"));

			while (true) {
				try {
					String str = br.readLine();
					if (str == null) {
						break;
					}

					Integer i = Integer.parseInt(str);
					if (i < 13842430) {
						continue;
					}

					int count = this.statisticsTotal(i);
					totaldataLogger.info("user playlist uid:: " + i
							+ " count:: " + count);
				} catch (Exception e) {
					logger.error("", e);
				}
			}

			br.close();
		} catch (Exception e) {
			logger.error("", e);
		}

		// for (int i = 571042; i < 121782323; i++) {
		// int count = this.statisticsTotal(i);
		// totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
		// + count);
		// }
		//
		// for (int i = 314261220; i < 360000000; i++) {
		// int count = this.statisticsTotal(i);
		// totaldataLogger.info("totaldataLogger uid:: " + i + " count:: "
		// + count);
		// }
	}
}
