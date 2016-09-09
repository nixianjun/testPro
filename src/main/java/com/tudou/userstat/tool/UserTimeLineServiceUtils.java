package com.tudou.userstat.tool;


import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.tudou.userstat.constant.Constants;
import com.tudou.userstat.model.UserTimeLine;

public class UserTimeLineServiceUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(UserTimeLineServiceUtils.class);
	 private static Schema<UserTimeLine> userSchema = RuntimeSchema.getSchema(UserTimeLine.class);
	
	public static JSONArray userTimeLines2JSON(List<UserTimeLine> utls) {
		JSONArray results = new JSONArray();
		for (UserTimeLine utl : utls) {
			results.add(userTimeLines2JSON(utl));
		}
		return results;
	}
	
	/**
	 * @return the userSchema
	 */
	public static Schema<UserTimeLine> getUserSchema() {
		return userSchema;
	}
	/**
	 * 
	 * @param utl
	 * @return
	 * @author JASON NI  2014-12-17
	 */
	public static JSONObject userTimeLines2JSON(UserTimeLine utl) {
        if(utl == null) return null;
		JSONObject result = new JSONObject();
		result.put("userID",utl.getUserID());
		result.put("type", utl.getType());
		result.put("objectID",utl.getObjectID());
		result.put("objectContent",utl.getObjectContent());
		String tmpDate = null;
		if (utl.getOccurTime()!= null) {
			tmpDate = DateFormatUtils.format(utl.getOccurTime(), Constants.DATE_STYLE);
		}
		String tmpaddTime = null;
		if (utl.getAddTime() != null) {
			tmpaddTime = DateFormatUtils.format(utl.getAddTime(), Constants.DATE_STYLE);
		}
		result.put("occurTime", tmpDate);
		result.put("addTime", tmpaddTime);
		result.put("addText", utl.getAddText());
		return result;
	}
	
    public static byte [] userTimeLineToByteArray(UserTimeLine utl) {
        return ProtostuffIOUtil.toByteArray(utl, getUserSchema(), LinkedBuffer.allocate(512));
    }
    public static UserTimeLine mergeUserFromCache(Object cache) {
        if(cache == null) return null;
        UserTimeLine result = new UserTimeLine();
        ProtobufIOUtil.mergeFrom((byte[])cache, result, getUserSchema());
        return result;
    }

}
