package com.tudou.userstat.web;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tudou.utils.web.RequestUtil;

public class HttpTest {

	
	
	public static void main(String[] args) throws URISyntaxException {
		/* String url ="http://10.111.188.53/messagepool/messagecore/receive_msg?topic=youku.dupload.videoinfo.sync&msgid=463625&count=5"	;
		 String str= com.tudou.userstat.tool.HttpClientHelper.getUriContentUsingGet(url, null);
		 System.out.println(str);
		 JSONObject js=JSONObject.parseObject(str);
		 JSONArray  msgs=   (JSONArray) js.get("msgs");
		 System.out.println(msgs); */
		 String url ="http://10.111.188.53/messagepool/messagecore/receive_msg?";
		 Map<String, String> param=new HashMap<String, String>();
 		/* param.put("topic", "youku.dupload.videoinfo.sync");
		 param.put("msgid", "463625");*/
		 param.put("topic", "copyright.production.video.change");
		 param.put("msgid", "1");
		 param.put("count", "5");
		getMsgsByurl(url,param);
	}
	public static JSONArray getMsgsByurl(String url,Map param) throws URISyntaxException {
		
		 String str= com.tudou.userstat.tool.HttpClientHelper.getUriContentUsingGet(url, param);
		 System.out.println(str);
		 JSONObject js=JSONObject.parseObject(str);
		 JSONArray  msgs=   (JSONArray) js.get("msgs");
		 System.out.println(msgs); 
		 return msgs;
	}
	
	@Test
	public void testy1(){
		Map<String, String> headersMap = new HashMap<String, String>();
		headersMap.put("X-Forwarded-For", "103.24.228.180");
		headersMap.put("clientIp", "103.24.228.183");
		System.out.println(com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				"http://messagetest.tudou.com/addMessageApi.html?content=jason.ni&userId=45&senderId=45&ip=103.24.228.181&app=openApi&key=1qazXSW@3edc",
				true, 
				Collections.EMPTY_MAP,
				"utf-8",
				headersMap));
	}
	
	@Test
	public void testNeiwang(){
		System.out.println(com.tudou.utils.web.RequestUtil.isFromInside("10.100.27.57"));
		Map<String, String> headersMap = new HashMap<String, String>();
		/*System.out.println(com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				"http://messagetest.tudou.com/addMessage.html?content=qwertyuioplkjhgbnmjhyuiokjujuseqwwerhjioookjjjssqqqjjjvvvseee&userId=45&senderId=11925647",
				true, 
				Collections.EMPTY_MAP,
				"utf-8",
				headersMap));		*/
		String url="http://wordfilter-int-vip.hgh.tudou.com/filter/wordfilter.do";
		Map<String, String>   map=new HashMap();
		map.put("app", 37+"");//app=123&ip=192.168.3.1&uid=123
		map.put("ip", "192.168.3.1");
		map.put("uid", 45+"");
		map.put("txt", "qwertyuioplkjhgbnmjhyuiokjujuseqwwerhjioookjjjssqqqjjjvvvseee");
		
		System.out.println(com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
url,				false, 
map,
				"utf-8",
				headersMap));		
		
		Set<String> IPS = new HashSet<String>();
		RequestUtil.addIpSplit(IPS, "113.108.198.210/21");
		System.out.println(IPS);
 
	}
	
	@Test
	public void testpersion(){
		   String url = "http://10.103.88.52/person.person";
		   Map<String,String> map=new HashMap<String, String>();
		   map.put("q", "personid:" + 1);
		   map.put("fd", "personname personalias gender nationality occupation bloodtype birthday height persondesc "
                                   + "thumburl deathday homeplace state");
		 String s=   com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				   url,				false, 
				   map,
				   				"utf-8",
				   				Collections.EMPTY_MAP);
		 System.out.println(s);
	}
	
	@Test
	public void testshow(){
		    int showid=143647;
		    this.getDouBanPingFeng(showid);
	}
	
	public static JSONObject getDouBanPingFeng(int showid){
		JSONObject result=new JSONObject();
		String url = "http://10.103.88.52/show.show?";
		   Map<String,String> map=new HashMap<String, String>();
		   map.put("q", "showid:" + showid);
		   map.put("fd", "avg_rating rating");
		 String s=   com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				   url,				false, 
				   map,
				   				"utf-8",
				   				Collections.EMPTY_MAP);
		 JSONObject js=JSONObject.parseObject(s);
		 JSONArray ja= (JSONArray) js.get("results");
		 JSONObject i=(JSONObject) ja.get(0);
		 String avg_rating= i.getString("avg_rating");
		 result.put("avg_rating", avg_rating);
		 JSONObject rating= (JSONObject) i.get("rating");
		 result.put("rating", rating);
		 System.out.println(result);
		 return result;
	}
	
	@Test
	public void testshowgetPersonByPersonid(){
		    int personid=10002;
		    this.getPersonByPersonid(personid);
	}
	public static JSONObject getPersonByPersonid(int personid){
		JSONObject result=new JSONObject();
		String url = "http://10.103.88.52/person.person?";
		   Map<String,String> map=new HashMap<String, String>();
		   map.put("q", "personid:" + personid);
		   map.put("fd", "personname thumburl persondesc birthday homeplace nationality youku_userid tudou_personid");
		 String s=   com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				   url,				false, 
				   map,
				   				"utf-8",
				   				Collections.EMPTY_MAP);
		 JSONObject js=JSONObject.parseObject(s);
		 JSONArray ja= (JSONArray) js.get("results");
		 JSONObject i=(JSONObject) ja.get(0);
		 String personname= i.getString("personname");
		 result.put("name", personname);
		 String thumburl= i.getString("thumburl");
		 result.put("picUrl", thumburl);
		 String persondesc= i.getString("persondesc");
		 result.put("desc", persondesc);
		 String birthday= i.getString("birthday");
		 result.put("birthday", birthday);
		 String youku_userid= i.getString("youku_userid");
		 result.put("youku_userid", youku_userid);
		 String homeplace= i.getString("homeplace");
		 result.put("homeplace", homeplace);
		 System.out.println(result+"\n"+i);
		 return result;
	}
	@Test
	public void testgetAlbumByShowid(){
		    int showid=143647;
		    this.getAlbumByShowid(showid);
	}
	public static JSONObject getAlbumByShowid(int showid){
		JSONObject result=new JSONObject();
		String url = "http://10.103.88.52/video.show?";
		   Map<String,String> map=new HashMap<String, String>();
		   map.put("q", "showid:" + showid);
		   map.put("fd", "pk_odshow showname showdesc deschead firstepisode_videourl show_thumburl show_vthumburl show_bannerurl episode_total area releaseage showcategory");
		 String s=   com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				   url,				false, 
				   map,
				   				"utf-8",
				   				Collections.EMPTY_MAP);
		 System.out.println(s);
		 JSONObject js=JSONObject.parseObject(s);
		 JSONArray ja= (JSONArray) js.get("results");
		 JSONObject i=(JSONObject) ja.get(0);
		 String mmm= i.toJSONString().replace("\":", "\":\n");
		 System.out.println( "\n"+mmm);
		 return result;
	}
	
	@Test
	public void testgetVideoByVideoid(){
		    int videoid=31425565 ;
		    this.getVideoByVideoid(videoid);
	}
	
	public static JSONObject getVideoByVideoid(int videoid){
		JSONObject result=new JSONObject();
		String url = "http://10.103.88.52/video.video?";
		   Map<String,String> map=new HashMap<String, String>();
		   map.put("q", "videoid:" + videoid);
		   map.put("fd", "title thumburl thumburl_v2 deschead firstepisode_videourl show_thumburl show_vthumburl show_bannerurl episode_total area releaseage showcategory");
		 String s=   com.tudou.userstat.tool.HttpClientHelper.getUriRequestContent(
				   url,				false, 
				   map,
				   				"utf-8",
				   				Collections.EMPTY_MAP);
		 JSONObject js=JSONObject.parseObject(s);
		 JSONArray ja= (JSONArray) js.get("results");
		 JSONObject i=(JSONObject) ja.get(0);
		 String mmm= i.toJSONString().replace("\":", "\":\n");
		 System.out.println( "\n"+mmm);
		 return result;
	}
	
	 
}
