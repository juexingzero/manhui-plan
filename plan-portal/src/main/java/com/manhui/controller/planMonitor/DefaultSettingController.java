package com.manhui.controller.planMonitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Office;
import com.manhui.model.OrganizationSupervisor;
import com.manhui.model.User;
import com.manhui.service.OrganizationSupervisorService;

/**
 * @ClassName: DefaultSettingController
 * @description: 计划监察-默认设置控制器
 * @author:	HeJiayan
 * @date Create in 下午9:29:53 2017年12月11日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class DefaultSettingController {
	@Autowired
	private OrganizationSupervisorService osService;
	
	/**
	 * @description: 计划监察默认设置页面跳转
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/monitor_default_setting")
	public ModelAndView monitorDefaultSetting(String flag){
		ModelAndView mv = new ModelAndView("pages/planMonitor/defaultSetting/monitor_default_setting");
		List<OrganizationSupervisor> osList = osService.findTopOs();
		if(osList.size() > 0){
			for (OrganizationSupervisor os : osList) {
				List<OrganizationSupervisor> subOs = osService.findSubsByParentId(os.getId());
				if(subOs.size() > 0)
					os.setSubos(subOs);
			}
		}
		if(flag != null && "save".equals(flag))
			flag = "分配成功！";
		mv.addObject("flag", flag);
		mv.addObject("osList", osList);
		return mv;
	}
	
	/**
	 * @description: 分配监察人页面跳转
	 * @param osId
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/allot_supervisor")
	public ModelAndView allotSupervisor(String osId){
		ModelAndView mv = new ModelAndView("pages/planMonitor/defaultSetting/allot_supervisor");
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOfficeAndEmp").getValue();
		mv.addObject("CQList", CQList);
		mv.addObject("osId", osId);
		return mv;
	}
	
	/**
	 * @description: 保存分配结果
	 * @param os
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/save_allot_supervisor")
	public String saveAllotSupervisor(OrganizationSupervisor os){
		if(os.getId() != null && !"".equals(os.getId())){
			osService.updateByPrimaryKeySelective(os);
		}
		return "redirect:/monitor_default_setting?flag=save";
	}
}
