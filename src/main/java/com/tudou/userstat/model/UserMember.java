package com.tudou.userstat.model;

import java.util.Date;

/**
 * 用户会员信息
 * 
 * @author clin
 * 
 */
public class UserMember {
	private int userId;

	private Date memberExpiry;

	private Date updateTime;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getMemberExpiry() {
		return memberExpiry;
	}

	public void setMemberExpiry(Date memberExpiry) {
		this.memberExpiry = memberExpiry;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
