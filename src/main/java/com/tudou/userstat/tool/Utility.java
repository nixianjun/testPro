package com.tudou.userstat.tool;


public class Utility {
	/**
	 * 去除特殊字符
	 * 
	 * @param content
	 * @return
	 */
	public static String htmlSpecialReplace(String content) {
		if (content == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		char[] chars = content.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				default:
					sb.append(chars[i]);
					break;
			}
		}

		return sb.toString();
	}

	public static String getChineseSex(String dbSex) {
		if ("F".equals(dbSex)) {
			return "女";
		} else if ("M".equals(dbSex)) {
			return "男";
		} else {
			return "保密";
		}
	}

	/**
	 * 获取用户主页地址
	 * 
	 * @param userId
	 * @param username
	 * @param isVuser
	 * @return
	 */
	public static String getUserHomeUrl(int userId, String username, boolean isVuser) {
		if (isVuser) {
			return "http://v.tudou.com/" + username + "/";
		} else {
			return "http://www.tudou.com/home/" + username + "/";
		}
	}
}
