package com.tudou.userstat.service.mapping;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tudou.userstat.constant.MsgType;
import com.tudou.userstat.model.TDMsg;

@Service
public class PlaylistDBMapping extends AbstractMapping {

	@Override
	public List<TDMsg> selectDataFromDB(Date date, int start, int limit) {
		return itemChangeHisDAO.selectLastPlaylistChanges(date, start, limit);
	}

	/**
	 * 加载最新的视频更新列表
	 * 
	 * @return
	 */
	public void loadPlaylistChange() {
		super.loadChangeData(MsgType.playlist,
				Calendar.getInstance().getTime(), 5, 1000);
	}
}
