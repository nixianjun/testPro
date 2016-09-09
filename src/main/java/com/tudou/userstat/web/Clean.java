package com.tudou.userstat.web;

 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.tudou.userstat.tool.HttpClientHelper;
import com.tudou.userstat.tool.ResponseUtils;

public class Clean {
	private static Logger logger = Logger.getLogger(Clean.class);

	public static List<String> list=new ArrayList<String>();

	static{
		list=readFileByLines("/home/anchorword/tudou_member.txt");
		logger.info("文件行数list:"+list.size());
	}
	
	 /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName) {
    	List<String> result=new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
        	logger.info("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	result.add(tempString);
            	 logger.info("line："+line+" tempString"+tempString);
                line++;
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }
    
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName,int min,int max) {
    	List<String> result=new ArrayList<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
            	if(line<min){
            		continue;
            	}
            	if(line>=min&&line<=max){
            		result.add(tempString);
            	}
            	if(line>max){
            		break;
            	}
                line++;
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }
   
	public    void clean( int begin,int end){
		try{
		List<String> list=Clean.list;
		Long b=System.currentTimeMillis();
		String url="http://www.tudou.com/uis/clearUserCache.action";
		Map<String,String> param=new HashMap<String,String>();
		
		for (int i = begin; i <=end; i++) {
			String uid=list.get(i);
			param.put("uid", uid+"");
			param.put("app", "clean");
			try{
				String result=HttpClientHelper.getUriContentUsingPost(url, param, "utf-8");
				JSONObject js= JSONObject.parseObject(result);
				if(!js.getString("code").equals("0")){
					logger.error("[error_clean_uid]:"+uid+"[clean_result]"+result);
				}else{
					logger.info("[success_clean_uid]:"+uid+"[clean_result]"+result);
				}
				Thread.sleep(5);
			}catch(Exception e){
				logger.error("uid:"+uid,e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		 
		Long e=System.currentTimeMillis();
		logger.info("##clean_success##"+begin+" "+end+"[耗时：]"+(e-b)+"ms");
	 
		}catch(Exception e){
			logger.error(e);
			logger.error("##clean_error##"+begin+" "+end);
		}
	}
}
