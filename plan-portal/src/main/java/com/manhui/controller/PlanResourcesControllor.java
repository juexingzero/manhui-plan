package com.manhui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.Cache;
import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Office;
import com.manhui.model.User;
import com.manhui.util.SendRequest;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * @ClassName: PlanResourcesControllor
 * @description: 查询计划资源信息控制器
 * @author:	HeJiayan
 * @date Create in 下午3:10:41 2017年11月1日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanResourcesControllor {
	
	/**
	 * @description: 查询部门机构
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/find_allOffice")
	public ModelAndView findAllOffice(){
		ModelAndView mv = new ModelAndView("pages/planCompile/chose_office");
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOffice").getValue();
		mv.addObject("CQList", CQList);
		return mv;
	}
	
	/**
	 * @description: 查询员工
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/chose_blamePerson")
	public ModelAndView findAllPerson(String isDecompose){
		ModelAndView mv = new ModelAndView("pages/planCompile/chose_blamePerson");
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOfficeAndEmp").getValue();
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		mv.addObject("CQList", CQList);
        return mv;
	}
	
	/**
	 * @description: 查询流程
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/chose_audit")
	public ModelAndView findAllAudit(){
		ModelAndView mv = new ModelAndView("pages/planCompile/chose_audit");
		List<Map> circuitList = (List<Map>) CacheManager.getCacheInfo("allCircuit").getValue();
		mv.addObject("circuitList", circuitList);
		return mv;
	}
}
