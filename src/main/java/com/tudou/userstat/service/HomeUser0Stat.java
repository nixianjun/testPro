package com.tudou.userstat.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.usersrv.client.UserServiceClient;
import com.tudou.usersrv.model.User;
import com.tudou.userstat.dao.StatDAO;
import com.tudou.userstat.dao.UserDAO;
import com.tudou.userstat.model.HomeUserStat;
import com.tudou.utils.client.HTTPLongClient;

/**
 * 个人主页0.0用户的相关统计
 * 
 * @author clin
 * 
 */
@Service
public class HomeUser0Stat {

	private static final Logger logger = Logger.getLogger("totaldata");

	@Resource
	UserDAO userDAO;

	@Resource
	StatDAO statDAO;

	@Resource
	UserServiceClient userServiceClient;

	static Comparator<HomeUserStat> citem = new Comparator<HomeUserStat>() {
		@Override
		public int compare(HomeUserStat o1, HomeUserStat o2) {
			return o2.items - o1.items;
		}
	};

	static Comparator<HomeUserStat> cpl = new Comparator<HomeUserStat>() {
		@Override
		public int compare(HomeUserStat o1, HomeUserStat o2) {
			return o2.playlists - o1.playlists;
		}
	};

	static Comparator<HomeUserStat> cfans = new Comparator<HomeUserStat>() {
		@Override
		public int compare(HomeUserStat o1, HomeUserStat o2) {
			return o2.fans - o1.fans;
		}
	};

	public void runAllOldHomeUsers() {
		final List<Integer> oldUids = userDAO.getOldHomeUsers();
		logger.info("get old uids " + oldUids.size());

		ExecutorService es = Executors.newFixedThreadPool(20);

		for (int i = 0; i < 20; i++) {
			final int index = i;

			es.execute(new Runnable() {
				public void run() {
					int size = oldUids.size();
					for (int j = 0; j < size; j++) {
						if (j % 20 == index) {
							int uid = oldUids.get(j);
							User user = userServiceClient.getUserById(uid);

							if (user == null) {
								continue;
							}

							String url = null;

							boolean isV = false;

							if (user.getVerifyType() > 0) {
								url = "http://homepage.jj.tudou.com/home/"
										+ user.getUsername();
								isV = true;
							} else {
								url = "http://homepage.jj.tudou.com/home/"
										+ user.getUsername();
							}

							String result = "";
							try {
								result = HTTPLongClient.getUrlContent(url,
										false, null);
							} catch (Exception e) {
								result = "";
							}

							logger.info("thread " + index + " uid " + uid
									+ " url " + url + " result " + isV + " "
									+ result.length());
						}
					}
				}
			});
		}

	}

	public static void main(String[] args) {
		Pattern p = Pattern.compile("([a-zA-Z0-9_-]+)");

		Matcher m = p.matcher("zl.sz");

		if (m.matches()) {
			System.out.println(m.group(1));
		}
	}

	/**
	 * 统计降级的用户
	 */
	public void statDownUsers() {
		List<Integer> uids = userDAO.getDownHomeUsers();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		Date t = cal.getTime();

		List<Integer> loginUids = new ArrayList<Integer>();

		for (Integer userId : uids) {
			Date time = userServiceClient.getUserLastLoginTime(userId);
			if (time != null && time.after(t)) {
				loginUids.add(userId);
			}
		}

		logger.info("stat login uids " + loginUids.size());
		List<HomeUserStat> users = statDAO.selectUserStatByUids(loginUids);

		Iterator<HomeUserStat> it = users.iterator();

		while (it.hasNext()) {
			HomeUserStat user = it.next();

			if (user.fans < 100) {
				it.remove();
			}
		}

		Collections.sort(users, citem);
		logger.info("====================");

		for (HomeUserStat user : users) {
			if (user.items == 0) {
				break;
			}

			logger.info("uid " + user.uid + " item " + user.items);
		}
		logger.info("====================");
		logger.info("====================");
		Collections.sort(users, cpl);
		for (HomeUserStat user : users) {
			if (user.playlists == 0) {
				break;
			}

			logger.info("uid " + user.uid + " playlist " + user.playlists);
		}
		logger.info("====================");
		logger.info("====================");
		Collections.sort(users, cfans);
		for (HomeUserStat user : users) {
			if (user.fans == 0) {
				break;
			}

			logger.info("uid " + user.uid + " fans " + user.fans);
		}
		logger.info("====================");
	}

	public void stat() {
		logger.info("start stat home user");
		// TODO 获取所有1.0及以上用户
		Set<Integer> newUids = userDAO.getNewHomeUsers();
		logger.info("get new uids " + newUids.size());

		// TODO 获取所有0.0用户
		List<Integer> oldUids = userDAO.getOldHomeUsers();
		logger.info("get old uids " + oldUids.size());

		// TODO 比较获取0.0的用户列表
		List<Integer> noUpdateUids = new ArrayList<Integer>();

		for (Integer uid : oldUids) {
			if (!newUids.contains(uid)) {
				noUpdateUids.add(uid);
			}
		}

		logger.error("not update uids " + noUpdateUids.size());

		// for (Integer uid : noUpdateUids) {
		// User user = userServiceClient.getUserById(uid);
		//
		// if (user == null) {
		// continue;
		// }
		//
		// String url = null;
		// if (user.getVerifyType() > 0) {
		// url = "http://vtest.tudou.com/_" + user.getUserId();
		// } else {
		// url = "http://wwwtest.tudou.com/home/_" + user.getUserId();
		// }
		//
		// logger.info("url is ::" + url);
		// }

		// gc
		newUids = null;
		oldUids = null;
		System.gc();

		List<HomeUserStat> users = new ArrayList<HomeUserStat>();

		// TODO 查询用户的上传视频数
		// TODO 查询用户的
		int size = noUpdateUids.size();
		for (int i = 0; i < size; i += 5000) {
			int end = i + 5000;
			if (end > size) {
				end = size;
			}

			List<HomeUserStat> list = statDAO.selectUserStatByUids(noUpdateUids
					.subList(i, end));
			users.addAll(list);
		}

		logger.info("not update usres objects " + users.size());

		Collections.sort(users, citem);
		logger.info("====================");

		for (HomeUserStat user : users) {
			if (user.items == 0) {
				break;
			}

			logger.info("uid " + user.uid + " item " + user.items);
		}
		logger.info("====================");
		logger.info("====================");
		Collections.sort(users, cpl);
		for (HomeUserStat user : users) {
			if (user.playlists == 0) {
				break;
			}

			logger.info("uid " + user.uid + " playlist " + user.playlists);
		}
		logger.info("====================");
		logger.info("====================");
		Collections.sort(users, cfans);
		for (HomeUserStat user : users) {
			if (user.fans == 0) {
				break;
			}

			logger.info("uid " + user.uid + " fans " + user.fans);
		}
		logger.info("====================");
	}
}
