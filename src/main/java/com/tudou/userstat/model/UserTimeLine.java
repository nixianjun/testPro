package com.tudou.userstat.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户时光机
 * @author JSAON NI 2014-12-5
 *
 */
public class UserTimeLine implements Serializable {

	/**
	 * �û�id
	 */
	private Integer userID;
	/**
	 * ��������
	 */
	private Integer type;
	/**
	 * �������ID :�ϴ���ĿID�������û�ID�������û�ID�����۽�ĿID
	 */
	private Integer objectID;   
	/**
	 * text ����������ݣ�������⣬�������ݣ���������
	 */
	private String objectContent; 
	/**
	 * dateTime ����ʱ��
	 */
	private Date occurTime; 
	/**
	 * Text ��������
	 */
	private String addText;
	/**
	 * dateTime ���ʱ�� ��
	 */
	private Date addTime;
	/**
	 * @return the userID
	 */
	public Integer getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * @return the objectID
	 */
	public Integer getObjectID() {
		return objectID;
	}
	/**
	 * @param objectID the objectID to set
	 */
	public void setObjectID(Integer objectID) {
		this.objectID = objectID;
	}
	/**
	 * @return the objectContent
	 */
	public String getObjectContent() {
		return objectContent;
	}
	/**
	 * @param objectContent the objectContent to set
	 */
	public void setObjectContent(String objectContent) {
		this.objectContent = objectContent;
	}
	/**
	 * @return the occurTime
	 */
	public Date getOccurTime() {
		return occurTime;
	}
	/**
	 * @param occurTime the occurTime to set
	 */
	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}
	/**
	 * @return the addText
	 */
	public String getAddText() {
		return addText;
	}
	/**
	 * @param addText the addText to set
	 */
	public void setAddText(String addText) {
		this.addText = addText;
	}
	/**
	 * @return the addTime
	 */
	public Date getAddTime() {
		return addTime;
	}
	/**
	 * @param addTime the addTime to set
	 */
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String logtoString() {
		return "UserTimeLine [userID=" + userID + ", type=" + type
				+ ", objectID=" + objectID + ", objectContent=" + objectContent
				+ ", occurTime=" + occurTime + ", addText=" + addText
				+ ", addTime=" + addTime + "]";
	} 

	
}
