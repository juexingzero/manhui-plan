package com.manhui.controller.jurisdictionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Office;
import com.manhui.model.User;
import com.manhui.model.jurisdictionManager.JurisdictionManager;
import com.manhui.service.jurisdictionManager.JurisdictionManagerService;


/**
 * @ClassName: JurisdictionManagerController
 * @description: 系统设置-权限管理控制器
 * @author:	Jiangxiaosong
 * @date Create in 上午11:29:53 2017年12月21日
 * @version: v1.0.0
 * @modify By: Hejiayan 下午14:22:53 2017年12月22日
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class JurisdictionManagerController {
	
	
	//引入计划分类类型
	@Autowired
	private JurisdictionManagerService jurisdictionManagerService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	 }
	
	/**
	 * 跳转到权限管理
	 * @param model
	 * @return
	 */
	@GetMapping("to_change_jurisdiction")
	public String toChangeJurisdiction(Model model){
		return "pages/jurisdictionManager/change_jurisdiction";
	}
	
	
	/**
	 * 获取基础层级
	 * @param model
	 * @return
	 */
	@GetMapping("get_base_hierarchy")
	public String getBaseHierarchy(Model model){
		
		JurisdictionManager jm = jurisdictionManagerService.findJurisdictionManager();
		if(jm != null)
			model.addAttribute("jurisdiction", jm);
		
		return "pages/jurisdictionManager/base_hierarchy";
	}
	
	
	/**
	 * 保存基础层级
	 * @param jm
	 * @return
	 */
	//@ResponseBody
	@RequestMapping("/save_base_hierarchy")
	public ModelAndView saveBaseHierarchy(JurisdictionManager jm){
		
		ModelAndView mv = new ModelAndView("pages/jurisdictionManager/base_hierarchy");
		//保存数据
		jurisdictionManagerService.updateJurisdictionManager(jm);
		//查询数据
		JurisdictionManager jurisdictionManager = jurisdictionManagerService.findJurisdictionManager();
		
		if(jurisdictionManager != null)
			mv.addObject("jurisdiction", jurisdictionManager);
		
		return mv;
		//return jurisdictionManager;
	}
	
	
	/**
	 * 获取岗位特设
	 * @param model
	 * @return
	 */
	@GetMapping("get_post_special")
	public String getPostSpecial(Model model){
		
		JurisdictionManager jm = jurisdictionManagerService.findJurisdictionManager();
		model.addAttribute("jurisdiction", jm);
		
		return "pages/jurisdictionManager/post_special";
	}
	
	
	/**
	 * 
	 * @param model
	 * @param jm
	 * @return
	 */
	@ResponseBody
	@PostMapping("save_post_special")
	public JurisdictionManager savePostSpecial(Model model,JurisdictionManager jm){
		//保存数据
		jurisdictionManagerService.updateJurisdictionManager(jm);
		//查询数据
		JurisdictionManager jurisdictionManager = jurisdictionManagerService.findJurisdictionManager();
		
		return jurisdictionManager;
	}
	
	/**
	 * @description: 选择特设岗位
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/chose_jurisdictionManager")
	public ModelAndView choseSpecialPost(String flag){
		ModelAndView mv = new ModelAndView("/pages/jurisdictionManager/chose_post");
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOffice").getValue();
		mv.addObject("CQList", CQList);
		mv.addObject("flag", flag);
		return mv;
	}
}
