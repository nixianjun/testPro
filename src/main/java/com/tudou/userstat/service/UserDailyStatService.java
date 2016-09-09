package com.tudou.userstat.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.userstat.dao.UserStatDAO;
import com.tudou.userstat.tool.FileMapper;
import com.tudou.utils.lang.DateUtil;

/**
 * 用户每日统计的类(给马杰用)
 * 
 * @author clin
 * 
 */
@Service
public class UserDailyStatService {

	private static final Logger logger = Logger
			.getLogger(UserDailyStatService.class);

	private static final Logger totaldataLogger = Logger.getLogger("totaldata");

	@Resource
	UserStatDAO userStatDAO;
	
	private static final String ROOTDIR="/home/wwwroot/";
	//private static final String ROOTDIR="/home/userstat/";

	public void statDaily() {
		Calendar cal = Calendar.getInstance();

		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		cal.add(Calendar.DATE, -1);
		statDaily(cal.getTime());

		 //每周4一次的统计
		if (dayOfWeek == 5) {
			// 每个周四执行一次
			statWeek4(new Date());
		}
	}
	
	/**
	 * 每周4一次的统计
	 */
	public  String statWeek4(Date now) {
		totaldataLogger.info("start stat statWeek4");
		Long begin=System.currentTimeMillis();
		// TODO 读取之前每天的数据
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		List<String> users = new LinkedList<String>();
		List<String> albums = new LinkedList<String>();

		cal.add(Calendar.DATE, -8);
		for (int i = 1; i <=7; i++) {
			try {
				cal.add(Calendar.DATE, 1);
				Date date = cal.getTime();
				String dateStr = DateUtil.dateToString(date, "yyyyMMdd");
				String file = ROOTDIR+ dateStr + ".data";
				BufferedReader r = new BufferedReader(new FileReader(file));
				String user = r.readLine();
				String album = r.readLine();

				users.add(user + "\t" + dateStr);
				albums.add(album + "\t" + dateStr);

				r.close();
			} catch (Exception e) {
			   logger.error(e);
			}
		}

		logger.info("get users " + users);
		logger.info("get ablums " + albums);

		// 将所有数据写入到一个文件中
		String file = ROOTDIR + DateUtil.dateToString(now, "yyyyMMdd")
				+ ".week";

		logger.info("write into file " + file);

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));

			for (String string : users) {
				w.write(string);
				w.newLine();
			}

			w.newLine();

			for (String string : albums) {
				w.write(string);
				w.newLine();
			}

			w.close();
		} catch (Exception e) {
			logger.error("", e);
		}
		Long end=System.currentTimeMillis();
		totaldataLogger.info("finish stat statWeek4 cost:"+(end-begin));
		return "users:"+users+"albums:"+albums;
	}

	public String statDaily(Date date) {
		totaldataLogger.info("start stat daily");
		Long begin=System.currentTimeMillis();
		// TODO 统计播客订阅数
		int userSubCount = userStatDAO.getUserSubTotalCount(date);
		// TODO 统计剧集订阅数
		int albumSubCount = userStatDAO.getAlbumSubTotalCount(date);
		// TODO 统计每日播客最多被订阅数
		LinkedHashMap<Integer, Integer> topSubUsers = userStatDAO
				.getTopSubUsers(date, 50);

		// TODO 写入本地文件
		String statContent = userSubCount + "\r\n" + albumSubCount + "\r\n";

		Iterator<Entry<Integer, Integer>> it = topSubUsers.entrySet()
				.iterator();

		while (it.hasNext()) {
			Entry<Integer, Integer> e = it.next();
			statContent += e.getKey() + " " + e.getValue() + "\r\n";
		}

 		String file = ROOTDIR	+ DateUtil.dateToString(date, "yyyyMMdd") + ".data";

		try {
			FileMapper.writeFile(file, statContent.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		Long end=System.currentTimeMillis();
		totaldataLogger.info("finish stat daily cost:"+(end-begin));
		return statContent;
	}
}
