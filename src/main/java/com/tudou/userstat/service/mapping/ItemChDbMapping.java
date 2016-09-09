package com.tudou.userstat.service.mapping;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.TDMsg;

/**
 * 将数据库的数据映射到本地内存中
 * 
 * @author clin
 * 
 */
@Service
public class ItemChDbMapping extends AbstractMapping {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(ItemChDbMapping.class);

	public static final Comparator<TDMsg> comp = new Comparator<TDMsg>() {
		@Override
		public int compare(TDMsg o1, TDMsg o2) {
			return o2.getId() - o1.getId();
		}
	};

	/**
	 * 加载最新的视频更新列表
	 * 
	 * @return
	 */
	public void loadItemChange() {
		super.loadChangeData(MsgType.item, Calendar.getInstance().getTime(), 5,
				1000);
	}

	@Override
	public List<TDMsg> selectDataFromDB(Date date, int start, int limit) {
		return itemChangeHisDAO.selectLastItemChanges(date, start, limit);
	}
}
