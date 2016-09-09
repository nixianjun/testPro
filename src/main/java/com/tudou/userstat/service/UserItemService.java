package com.tudou.userstat.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tudou.itemviewsrv.client.ItemAPIClient;
import com.tudou.itemviewsrv.model.Item;
import com.tudou.userstat.cache.UserItemLocalCache;
import com.tudou.userstat.dao.ItemDAO;

@Service
public class UserItemService {

	/**
	 * 进行缓存的临界值判断
	 */
	public int CACHE_SIZE = 5000;

	@Resource
	ItemDAO itemDAO;

	@Resource
	ItemAPIClient itemAPIClient;

	public List<Integer> getUserItemIds(int uid) {
		List<Integer> list = UserItemLocalCache.get(uid);

		if (list != null) {
			return list;
		}

		list = itemDAO.getUserItemIds(uid);

		if (list.size() > CACHE_SIZE) {
			UserItemLocalCache.put(uid, list);
		}

		return list;
	}

	/**
	 * 统计用户的有效视频数
	 * 
	 * @param uid
	 * @return
	 */
	public int getUserLegalVideoCount(int uid) {
		// TODO 获取视频id列表
		List<Integer> list = this.getUserItemIds(uid);

		if (list == null || list.size() == 0) {
			return 0;
		}

		if (list.size() > 5000) {
			// TODO 如果视频总数大于5000,那么不进行过滤操作
			return list.size();
		}

		// TODO 获取视频对象
		List<Item> items = itemAPIClient.getItemsByIds(list);

		if (items == null) {
			return 0;
		}

		int count = 0;

		for (Item item : items) {
			int isChecked = item.getIsChecked();
			if (isChecked <= 0 || isChecked == 82 || item.isAllShield()
					|| item.isCpDelete() || item.isBanByChecked()
					|| item.isAreaShield()) {
				// 安检值小于等于零， 为未上线视频
			} else {
				count++;
			}
		}
		// 过滤非法视频
		// 返回视频数
		return count;
	}
}
