package com.manhui.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.User;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @ClassName: SendRequest
 * @description: 发送服务器请求
 * @author:	HeJiayan
 * @date Create in 上午11:12:49 2017年10月31日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@SuppressWarnings("deprecation")
public abstract class SendRequest {
	public static Logger logger = Logger.getLogger(SendRequest.class);
	
	public static String powerURL = "http://114.242.165.90:15982/mauth/a";
	
	public static String workflowURL = "http://114.242.165.90:15986/workflow/a";
	
	//暂时调取系统提取地址
	public static String systemextraURL = "http://192.168.0.52:8088";

	/**
	 * @description: 发起流程审批申请
	 * @param procDefKey
	 * @param 
	 * @return 
	 * @throws
	 */
    public static boolean sendApprovalRequest(String procDefKey) {
    	boolean flag = false;
    	if(procDefKey != null && !"".equals(procDefKey)){
    		User user = (User) CacheManager.getCacheInfo("user").getValue();
        	JSONObject json = new JSONObject();
    		json.put("user_id", user.getUserId());
    		json.put("procDefKey", procDefKey);
    		
    		String result = post(json, "/act/taskApi /start", "workflow");
    		JSONObject resultJson = (JSONObject) JSONValue.parse(result); 
    		Integer respCode = (Integer) resultJson.get("respCode");
    		if(respCode == 1){
    			flag = true;
    		}else{
    			flag = false;
    		}
    	}else{
    		flag = false;
    	}
		
		return flag;
    }

    /**
     * @description: 发出请求
     * @param json
     * @param url
     * @param flag
     * @param
     * @return 
     * @throws
     */
	public static String post(JSONObject json, String url, String flag) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = null;
        if(flag.equals("power")){
        	post = new HttpPost(powerURL + url);     //权限服务器
        }else if(flag.equals("workflow")){ 
        	post = new HttpPost(workflowURL + url);  //工作流服务器
        }
        
        post.setHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Basic YWRtaW46");
        String result = "";
        
        try {

            StringEntity s = new StringEntity(JSONObject.toJSONString(json), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            post.setEntity(s);

            // 发送请求
            HttpResponse httpResponse = client.execute(post);

            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                strber.append(line + "\n");
            inStream.close();

            result = strber.toString();
            
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                logger.info("请求服务器成功，做相应处理");
            } else {
                logger.info("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常:"+e.getMessage());
            throw new RuntimeException(e);
        }

        return result;
    }
	
	
	/**
	 * 临时调取人力招聘等信息
	 * @param json
	 * @param url
	 * @return
	 */
	public static String findSystemextra(JSONObject json, String url){
		HttpClient client = new DefaultHttpClient();
        HttpPost post = null;
        post = new HttpPost(systemextraURL + url);     //权限服务器
        
        post.setHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Basic YWRtaW46");
        String result = "";
        
        try {

            StringEntity s = new StringEntity(JSONObject.toJSONString(json), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            post.setEntity(s);

            // 发送请求
            HttpResponse httpResponse = client.execute(post);

            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                strber.append(line + "\n");
            inStream.close();

            result = strber.toString();
            
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                logger.info("请求服务器成功，做相应处理");
            } else {
                logger.info("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常:"+e.getMessage());
            throw new RuntimeException(e);
        }

        return result;
	}

}
