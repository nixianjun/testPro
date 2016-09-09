package com.tudou.userstat.model;

import com.tudou.util.encry.IdEncrypter;

/**
 * 用户信息对象
 * 
 * @author clin
 * 
 */
public class UserFullInfo {
	/**
	 * 用户id
	 */
	private int userID;

	/**
	 * 用户上传视频数
	 */
	private int itemNum;

	/**
	 * 用户粉丝数
	 */
	private int subedNum;

	/**
	 * 用户订阅数
	 */
	private int subNum;

	/**
	 * 豆单数
	 */
	private int playlistNum;

	/**
	 * 是否会员
	 */
	private boolean isMember;

	/**
	 * 认证类型
	 */
	private int verifyType;

	/**
	 * 用户视频播放总数
	 */
	private long itemViewCount;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUidcode() {
		return IdEncrypter.encrypt(userID);
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

	public int getSubedNum() {
		return subedNum;
	}

	public void setSubedNum(Integer subedNum) {
		if (subedNum != null) {
			this.subedNum = subedNum;
		}
	}

	public int getSubNum() {
		return subNum;
	}

	public void setSubNum(Integer subNum) {
		if (subNum != null) {
			this.subNum = subNum;
		}
	}

	public int getPlaylistNum() {
		return playlistNum;
	}

	public void setPlaylistNum(int playlistNum) {
		this.playlistNum = playlistNum;
	}

	public boolean isMember() {
		return isMember;
	}

	public void setMember(Boolean isMember) {
		if (isMember != null) {
			this.isMember = isMember;
		}
	}

	public int getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(Integer verifyType) {
		if (verifyType != null) {
			this.verifyType = verifyType;
		}
	}

	public long getItemViewCount() {
		return itemViewCount;
	}

	public void setItemViewCount(long itemViewCount) {
		this.itemViewCount = itemViewCount;
	}

}
