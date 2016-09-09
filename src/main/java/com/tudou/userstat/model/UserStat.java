package com.tudou.userstat.model;

/**
 * 用户统计结果类
 * 
 * @author clin
 * 
 */
public class UserStat {
	private int uid;

	private int itemCount;
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
}
