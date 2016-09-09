package com.tudou.userstat.model;

import java.util.Date;

/**
 * 消息类
 * 
 * @author clin
 * 
 */
public class TDMsg {
	/**
	 * 消息的类型
	 */
	private int type;

	/**
	 * 消息的id
	 */
	private int id;

	/**
	 * 视频id
	 */
	private int item_id;

	/**
	 * 类型
	 */
	private int change_type;

	/**
	 * 操作类型
	 */
	private int oper_type;

	public int getOper_type() {
		return oper_type;
	}

	public void setOper_type(int oper_type) {
		this.oper_type = oper_type;
	}

	/**
	 * 时间点
	 */
	private Date add_time;

	/**
	 * 视频对应的用户id
	 */
	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public int getChange_type() {
		return change_type;
	}

	public void setChange_type(int change_type) {
		this.change_type = change_type;
	}

	public Date getAdd_time() {
		return add_time;
	}

	public void setAdd_time(Date add_time) {
		this.add_time = add_time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
