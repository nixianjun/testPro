package com.tudou.userstat.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tudou.playlist.client.PlaylistServiceClient;
import com.tudou.subscription.client.SubscriptionClient;
import com.tudou.userstat.dao.StatDAO;
import com.tudou.userstat.dao.UserDAO;
import com.tudou.userstat.model.HomeUserStat;
import com.tudou.userstat.model.UserFullInfo;
import com.tudou.userstat.model.UserFullInfoResult;
import com.tudou.userstat.model.UserMember;

/**
 * 用户信息收集类(全量用户信息的读取,并写入文件系统) 给土豆找人用
 * 
 * @author clin
 * 
 */
@Service
public class UserInfoCollector {

	private static final Logger logger = Logger
			.getLogger(UserInfoCollector.class);

	private static byte[] 回车 = null;
	static {
		try {
			回车 = "\r\n".getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
	}
	@Resource
	UserDAO userDAO;

	@Resource
	UserStatService userStatService;

	@Resource
	SubscriptionClient subscriptionClient;

	@Resource
	PlaylistServiceClient playlistServiceClient;

	@Resource
	StatDAO statDAO;

	/**
	 * 读取用户的认证信息,从ums库中
	 * 
	 * @return
	 */
	public Map<Integer, Integer> readVerifyInfo() {
		Map<Integer, Integer> users = new HashMap<Integer, Integer>();

		List<Integer> list = userDAO.getVerifyPerson();

		for (Integer uid : list) {
			users.put(uid, 1);
		}

		list = userDAO.getVerifyOrg();

		for (Integer uid : list) {
			users.put(uid, 2);
		}

		return users;
	}

	/**
	 * 读取会员用户的信息,从主库
	 * 
	 * @return
	 */
	public Map<Integer, Date> readMembers() {
		List<UserMember> list = userDAO.getMemberUser();

		Map<Integer, Date> users = new HashMap<Integer, Date>();

		for (UserMember user : list) {
			users.put(user.getUserId(), user.getMemberExpiry());
		}

		return users;
	}

	/**
	 * 跑用户全量数据
	 * 
	 * @throws IOException
	 */
	public void collectAllUser() throws IOException {
		Map<Integer, Date> members = null;
		Map<Integer, Integer> verifies = null;
		{
			// 预先读取的数据
			// 从主库读取会员信息
			members = readMembers();

			logger.info("read member size " + members.size());

			// 从ums库读取认证信息
			verifies = readVerifyInfo();
			logger.info("read verifies size " + verifies.size());
		}

		// TODO 分批从数据库中获取数据,并写入文件中
		int start = 0;
		int size = 5000;

		// int count = 0;
		int fileIndex = 0;

		String fileName = "/home/app_admin/fulluser/user" + fileIndex + ".data";

		long now = System.currentTimeMillis();

		FileOutputStream out = new FileOutputStream(fileName);

		while (true) {
			UserFullInfoResult result = collectUser(start, size, members,
					verifies, now);

			List<UserFullInfo> users = result.infos;

			if (result.maxUid == 0) {
				break;
			}

			if (users != null) {
				logger.info("collect user " + start + " " + users.size());
			}

			start = result.maxUid;

			if (users != null) {
				if (users.size() > 0) {
					// if (count >= 2000000) {
					// // 修改文件输出流
					// out.close();
					// // 计数器清空
					// count = 0;
					// // 文件计数器增加1
					// fileIndex++;
					//
					// out = new FileOutputStream(
					// "/home/app_admin/fulluser/test" + fileIndex
					// + ".data");
					// }

					saveUsers(users, out);
					// count += users.size();
				}
			}
		}

		out.close();

	}

	/**
	 * 收集用户信息
	 * 
	 * @param start
	 * @param end
	 * @param members
	 * @param verifies
	 * @return
	 */
	public UserFullInfoResult collectUser(int start, int size,
			Map<Integer, Date> members, Map<Integer, Integer> verifies, long now) {

		UserFullInfoResult result = new UserFullInfoResult();

		// 从统计表获取用户视频数
		List<HomeUserStat> userItemCounts = userStatService.getUserItemCounts(
				start, size);

		if (userItemCounts == null || userItemCounts.size() == 0) {
			return result;
		}

		logger.info("collect user get from db " + userItemCounts.size());

		// 如果用户视频数大于0
		List<Integer> uids = new ArrayList<Integer>();

		Iterator<HomeUserStat> it = userItemCounts.iterator();

		while (it.hasNext()) {
			HomeUserStat u = it.next();
			if (u.uid > result.maxUid) {
				result.maxUid = u.uid;
			}

			if (u.items > 0 || u.playlists > 0) {
				uids.add(u.uid);
			} else {
				it.remove();
			}
		}

		if (uids == null || uids.size() == 0) {
			return result;
		}

		logger.info("collect user get from db uid size:: " + uids.size());

		// 从小到大排序
		Collections.sort(uids);

		// 统计订阅数,粉丝数
		Map<Integer, Integer> fansNums = userDAO.getFansNum(uids);
		Map<Integer, Integer> subNums = userDAO.getSubNum(uids);

		if (fansNums != null && subNums != null) {
			logger.info("collect user get from db fans size:: "
					+ fansNums.size() + " " + subNums.size());
		}

		Map<Integer, Long> itemViews = statDAO.selectUserItemViewCounts(uids);

		List<UserFullInfo> users = new ArrayList<UserFullInfo>();

		for (HomeUserStat u : userItemCounts) {
			int uid = u.uid;
			UserFullInfo info = new UserFullInfo();
			info.setUserID(uid);
			// 视频数
			info.setItemNum(u.items);
			// 粉丝数
			Integer fans = fansNums.get(uid);
			if (fans == null) {
				fans = 0;
			}
			info.setSubedNum(fans);
			// 订阅数
			Integer subs = subNums.get(uid);
			if (subs == null) {
				subs = 0;
			}
			info.setSubNum(subs);
			// 豆单数
			info.setPlaylistNum(u.playlists);
			// 视频播放数
			Long itemViewCount = itemViews.get(uid);
			if (itemViewCount == null) {
				itemViewCount = 0l;
			}
			info.setItemViewCount(itemViewCount);

			// 是否会员
			Date date = members.get(uid);
			if (date != null && date.getTime() > now) {
				info.setMember(true);
			}

			// 认证信息
			info.setVerifyType(verifies.get(uid));

			users.add(info);
		}

		result.infos = users;
		return result;
	}

	/**
	 * 将用户信息写入到本地文件中
	 * 
	 * @param users
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public void saveUsers(List<UserFullInfo> users, FileOutputStream out)
			throws IOException {
		JSONObject json = new JSONObject();

		for (UserFullInfo user : users) {
			// 组装内容
			json.put("id", user.getUserID());
			json.put("itemCount", user.getItemNum());
			json.put("subedCount", user.getSubedNum());
			json.put("subCount", user.getSubNum());
			json.put("userType", user.getVerifyType());
			json.put("playlistCount", user.getPlaylistNum());
			json.put("isMember", user.isMember());
			json.put("playtimes", user.getItemViewCount());

			// 写入文件
			out.write(json.toJSONString().getBytes("UTF-8"));
			// 写入换行符
			out.write(回车);
		}

		logger.info("save user into file "
				+ users.get(users.size() - 1).getUserID());

		// 同步到文件中
		out.flush();
	}

}
