package com.tudou.userstat.tool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;

public class JsonUtils {
	private static final Logger logger = Logger.getLogger(JsonUtils.class);

	
	@SuppressWarnings("unchecked")
	public static <T> JsonResult<List<T>> parse2Array(String jsonValue, Class<T> dataClazz) {
		return parse(jsonValue, dataClazz);
	}

	@SuppressWarnings("unchecked")
	private static <T> JsonResult<T> parse2Object(String jsonValue, Class<T> dataClazz) {
		return parse(jsonValue, dataClazz);
	}

	@SuppressWarnings("unchecked")
	private static <T> JsonResult parse(String jsonValue, Class<T> dataClazz) {
		try {
			if (StringUtils.isBlank(jsonValue)) {
				JsonResult<T> result = new JsonResult<T>();
				result.setCode(-1);
				result.setMsg("返回值为空");
				return result;
			}
			JsonResult result = JSON.parseObject(jsonValue, JsonResult.class);
			Object data = result.getData();
			if (data == null) {
				return result;
			}
			if (data instanceof JSONArray) {
				JSONArray jobj = (JSONArray) data;

				List<T> list = new ArrayList<T>();
				for (int i = 0; i < jobj.size(); i++) {
					list.add(jobj.getObject(i, dataClazz));
				}
				result.setData(list);
			} else if (data instanceof JSONObject || data.getClass() != dataClazz) {
				result.setData(TypeUtils.castToJavaBean(data, dataClazz));
			}
			return result;
		} catch (Throwable e) {
			logger.error("", e);
		}

		return new JsonResult<T>();
	}

	public static class JsonResult<T> {
		private int code;
		private String msg;
		private T data;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		@Override
		public String toString() {
			return "JsonResult [code=" + code + ", msg=" + msg + ", data=" + data + "]";
		}
	}

}
