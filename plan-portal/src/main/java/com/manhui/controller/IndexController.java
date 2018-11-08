package com.manhui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.Cache;
import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Menu;
import com.manhui.model.User;
import com.manhui.util.PlanResourcesUtil;
import com.manhui.util.ResultParameterAnalysis;
import com.manhui.util.SendRequest;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @ClassName: IndexController.java
 * @description:   首页相关控制器
 * @author: HeJiayan
 * @date Create in 14:33 2017/9/29
 * @version: v1.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class IndexController {

	private static String flag = "power";
	
    /**
     * 登录跳转
     *
     * @param 
     * @return
     */
    @RequestMapping("/login")
    public ModelAndView loginGet(String flag) {
    	ModelAndView mv = new ModelAndView("login");
    	if(flag != null && flag.equals("timeout")){
    		mv.addObject("error", "登陆超时，请重新登陆！");  
    	}
    	return mv;
    }

    /**
     * 登录
     *
     * @param user
     * @param httpSession
     * @return
     */
    @RequestMapping("/admin/login")
    public ModelAndView loginPost(String userName, String passWord, HttpSession session) {
    	ModelAndView mv = new ModelAndView();
    	User user = new User();
    	user.setUserName(userName);
    	user.setPassWord(passWord);
    	
    	JSONObject json = new JSONObject();
		json.put("username", user.getUserName());
		json.put("password", user.getPassWord());
	/*	json.put("app_id", "1576761b1485dad5");
		json.put("app_key", "0fcb946357c3ed7d");*/
		session.setAttribute("user", user);
		
		String result = SendRequest.post(json, "/sys/out/login_web", flag);
		JSONObject resultJson = (JSONObject) JSONValue.parse(result); 
		Integer respCode = (Integer) resultJson.get("respCode");
		if(respCode == 1){
			user = ResultParameterAnalysis.analysisResult(resultJson,user);
			session.setAttribute("user", user);
			
			//通过用户的一些信息查询机构部门以及公司员工，并将所查询结果存入缓存
			new PlanResourcesUtil(user);
			
			//查询用户菜单信息
			List<Menu> menus = getUserMenus(user);
			
            mv.setViewName("index");
            mv.addObject("menus", menus);
            mv.addObject("user", user);
            return mv;
		}else if(respCode == -1){
			mv.setViewName("login");
            mv.addObject("error", "服务器错误！");  
            return mv;
		}else if(respCode == -4){
			mv.setViewName("login");
            mv.addObject("error", "用户被禁用！");  
            return mv;
		}else{
			mv.setViewName("login");
            mv.addObject("error", "用户名或密码错误，请重新登录！");  
            return mv;
		}
    }

	/**
     * 仪表板页面
     *
     * @param 
     * @return
     */
    @RequestMapping("/dashboard")
    public String dashboard() {
    	return "dashboard";
    }

    /**
     * 登出
     * @param httpSession
     * @return
     */
    @RequestMapping("/admin/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        CacheManager.clearAll();
        return "login";
    }
    
    /**
	 * @description: 登陆成功后，查询用户对应菜单信息
	 * @param paramMap
	 * @return 
	 * @throws 
	 */
	private List<Menu> getUserMenus(User user) {
		JSONObject json = new JSONObject();
		json.put("user_id", user.getUserId());
		json.put("app_id", user.getAppId());
		json.put("app_key", user.getAppKey());
		json.put("token", user.getToken());
    	
    	String url = "/sys/out/userinfo";
		String result = SendRequest.post(json, url, flag);
		JSONObject resultJson = (JSONObject) JSONValue.parse(result);
		JSONArray roleList = (JSONArray) ((JSONObject) resultJson.get("data")).get("roleList");
		JSONArray menuList = (JSONArray) ((JSONObject)roleList.get(0)).get("menuList");

		List<Menu> menus = new ArrayList<Menu>();
		if(menuList != null){
			for (Object object : menuList) {
				JSONObject obj = (JSONObject) object;
				Menu menu = new Menu();
				menu.setMenuId((String) obj.get("id"));
				menu.setName((String) obj.get("name"));
				menu.setUrl((String) obj.get("href"));
				menu.setParentId((String) obj.get("parentId"));
				menu.setParentIds((String) obj.get("parentIds"));
				menu.setSort((Integer) obj.get("sort"));
				menu.setApplicationId((String) obj.get("applicationid"));
				if(!"1".equals(menu.getParentId()) && menu.getApplicationId().equals("80f615d6629d49f2bf0f688d36dac85b"))
					menus.add(menu);
			}
		}
		
		List<Menu> newMenus = new ArrayList<Menu>();
		for (Menu menu : menus) {
			String parentIds = menu.getParentIds();
			String[] idArr = parentIds.split(",");
			if(idArr.length == 3){
				List<Menu> subMenus = new ArrayList<Menu>();
				for (Menu subMenu : menus) {
					if(subMenu.getParentId().equals(menu.getMenuId()))
						subMenus.add(subMenu);
				}
				//子菜单排序
				Collections.sort(subMenus, new Comparator<Menu>(){

					@Override
					public int compare(Menu o1, Menu o2) {
						if(o1.getSort()>o2.getSort()){
		                    return 1;
		                }
		                if(o1.getSort()==o2.getSort()){
		                    return 0;
		                }
						return -1;
					}
					
				});
				menu.setSubmenus(subMenus);
				newMenus.add(menu);
			}
		}
		//父菜单排序
		Collections.sort(newMenus, new Comparator<Menu>(){

			@Override
			public int compare(Menu o1, Menu o2) {
				if(o1.getSort()>o2.getSort()){
                    return 1;
                }
                if(o1.getSort()==o2.getSort()){
                    return 0;
                }
				return -1;
			}
			
		});
		return newMenus;
	}
}
