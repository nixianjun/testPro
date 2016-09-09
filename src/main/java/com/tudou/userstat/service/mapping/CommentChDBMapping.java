package com.tudou.userstat.service.mapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tudou.digservicenew.client.CommentClient;
import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.TDMsg;

/**
 * 评论数的数据库映射
 * 
 * @author clin
 * 
 */
@Service
public class CommentChDBMapping extends AbstractMapping {

	@Resource
	CommentClient commentClient;

	@Override
	public List<TDMsg> selectDataFromDB(Date date, int start, int limit) {
		return itemChangeHisDAO.selectLastCommentChanges(date, start, limit);
	}

	/**
	 * 加载最新的评论数据
	 */
	public void loadCommentChange() {
		super.loadChangeData(MsgType.comment, Calendar.getInstance().getTime(),
				10, 20000);
	}
}
