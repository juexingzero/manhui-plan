package com.manhui.util;

import com.manhui.model.User;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @ClassName: ResultParameterAnalysis
 * @description: TODO
 * @author:	HeJiayan
 * @date Create in 下午4:09:41 2017年10月31日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class ResultParameterAnalysis {
	public static User analysisResult(JSONObject resultJson, User user){
		JSONObject userJSON = (JSONObject) ((JSONObject) resultJson.get("data")).get("user");
		String companyId = (String) userJSON.get("company_id");
		String userId = (String) userJSON.get("user_id");
		String name = (String) userJSON.get("user_name");
		String token = (String) ((JSONObject) resultJson.get("data")).get("token");
		
		user.setCompanyId(companyId);
		user.setUserId(userId);
		user.setName(name);
		user.setAppId("1576761b1485dad5");
		user.setAppKey("0fcb946357c3ed7d");
		user.setToken(token);
		
		return user;
	}
}
