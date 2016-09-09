package com.tudou.userstat.service.analyze;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.itemviewsrv.client.ItemAPIClient;
import com.tudou.itemviewsrv.model.Item;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.TDMsg;

/**
 * 对外部消息进行分析和数据填充的类
 * 
 * @author clin
 * 
 */
@Service
public class Analyzer {

	private static final Logger logger = Logger.getLogger(Analyzer.class);

	@Resource
	ItemAPIClient itemAPIClient;

	/**
	 * 对消息进行分析,填充消息的详细信息
	 * 
	 * @param msgList
	 * @return
	 */
	public void analyzer(List<TDMsg> msgList, MsgType type) {
		if (msgList == null) {
			return;
		}

		// TODO 对详细类型进行分类
		// TODO 对消息内容进行分析,填充数据
		if (MsgType.item.equals(type)) {
			analyzer视频消息(msgList);
		} else if (MsgType.comment.equals(type) || MsgType.dig.equals(type)) {
			analyzer评论消息(msgList);
		}
	}

	private void analyzer评论消息(List<TDMsg> msgList) {
		// 批量获取视频信息,并填充用户id
		Set<Integer> itemIds = new HashSet<Integer>();

		for (TDMsg msg : msgList) {
			if (msg.getItem_id() > 0) {
				itemIds.add(msg.getItem_id());
			}
		}

		if (itemIds.size() > 0) {
			logger.info("get item from item api " + itemIds.size());
			Map<Integer, Item> map = itemAPIClient.getItemsMapByIds(itemIds);

			for (TDMsg msg : msgList) {
				int itemId = msg.getItem_id();

				Item item = map.get(itemId);

				if (item != null) {
					msg.setUid(item.getOwnerID());
				}
			}
		}

	}

	/**
	 * 处理视频的消息
	 * 
	 * @param msgList
	 */
	public void analyzer视频消息(List<TDMsg> msgList) {
		// TODO 分析消息的类型,属于添加视频,修改视频或者是删除视频
		// TODO 获取并填充ownerid

		// 批量获取视频信息,并填充用户id
		Set<Integer> itemIds = new HashSet<Integer>();

		for (TDMsg msg : msgList) {
			if (msg.getItem_id() > 0) {
				itemIds.add(msg.getItem_id());
			}
		}

		if (itemIds.size() > 0) {
			logger.info("get item from item api " + itemIds.size());
			Map<Integer, Item> map = itemAPIClient.getItemsMapByIds(itemIds);

			for (TDMsg msg : msgList) {
				int itemId = msg.getItem_id();

				Item item = map.get(itemId);

				if (item != null) {
					msg.setUid(item.getOwnerID());
				}
			}
		}
	}

}
