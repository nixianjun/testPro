package com.tudou.userstat.service.mapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.TDMsg;

/**
 * 挖数据的数据库同步
 * 
 * @author clin
 * 
 */
@Service
public class DigDBMapping extends AbstractMapping {

	@Override
	public List<TDMsg> selectDataFromDB(Date date, int start, int limit) {
		return itemChangeHisDAO.selectLastDigChanges(date, start, limit);
	}

	/**
	 * 加载最新的视频更新列表
	 * 
	 * @return
	 */
	public void loadDigChange() {
		super.loadChangeData(MsgType.dig, Calendar.getInstance().getTime(), 5,
				1000);
	}
}
