package com.tudou.userstat.service.mapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.TDMsg;

/**
 * 订阅数据的映射
 * 
 * @author clin
 * 
 */
@Service
public class SubDBMapping extends AbstractMapping {

	@Override
	public List<TDMsg> selectDataFromDB(Date date, int start, int limit) {
		return itemChangeHisDAO.selectLastSubChanges(date, start, limit);
	}

	/**
	 * 加载最新的视频更新列表
	 * 
	 * @return
	 */
	public void loadSubChange() {
		super.loadChangeData(MsgType.sub, Calendar.getInstance().getTime(), 5,
				10000);
	}
}
