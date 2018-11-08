package com.manhui.controller.planMonitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Office;
import com.manhui.model.Plan;
import com.manhui.model.User;
import com.manhui.service.PlanService;

/**
 * @ClassName: MonitorController
 * @description: 计划监察-监察分配控制器
 * @author:	HeJiayan
 * @date Create in 下午5:31:48 2017年12月7日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class MonitorController {
	@Autowired
    private PlanService planService;
	
	/**
	 * @description: 监察分配页面跳转
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/monitor_assignment")
	public ModelAndView monitorAssignment(){
		ModelAndView mv = new ModelAndView("pages/planMonitor/monitorAssignment/monitor_assignment");
		return mv;
	}
	
	/**
	 * @description: 分页查询已过审的所有主计划
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param plan
	 * @param flag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/find_monitor_assignment_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView findMonitorAssignment(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Plan plan, String flag){
		ModelAndView mv = new ModelAndView("pages/planMonitor/monitorAssignment/monitor_assignment_list");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		String planName = plan.getPlanName();
		if(planName != null && !"".equals(planName))
			plan.setPlanName("%"+planName+"%");
		
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planService.countApprovedPlan(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		
		//条件查询所有协作申请列表
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> planList = planService.findApprovedPlan(plan);
		
		if(planList != null && planList.size() > 0)
			mv.addObject("planList", planList);
		if(flag != null && "save".equals(flag)){
			flag = "重新分配成功！";
			mv.addObject("flag", flag);
		}
		
		plan.setPlanName(planName);
		mv.addObject("plan", plan);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 查看计划详情或者重新分配监察人
	 * @param planId
	 * @param flag
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/reassignment_or_check_plan")
	public ModelAndView reassignmentOrCheckPlan(String planId, String flag){
		ModelAndView mv = new ModelAndView("pages/planMonitor/monitorAssignment/monitor_plan_detail");
		Plan plan = new Plan();
		if(planId != null && !"".equals(planId)){
			plan = planService.findById(Integer.parseInt(planId));
			if(plan.getParentId() != null && !"".equals(plan.getParentId())){
				plan = planService.findById(plan.getParentId());
				if(plan.getParentId() != null && !"".equals(plan.getParentId())){
					plan = planService.findById(plan.getParentId());
					if(plan.getParentId() != null && !"".equals(plan.getParentId())){
						plan = planService.findById(plan.getParentId());
					}
				}
			}
			List<Plan> subPlans = planService.findSubPlans(plan.getPlanId());
			if(subPlans.size() > 0){
				plan.setSubPlans(subPlans);
				plan.setSubSum(subPlans.size());
				for (Plan p : subPlans) {
					List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
					if(subSubPlans.size() > 0){
						p.setSubPlans(subSubPlans);
						p.setSubSum(subSubPlans.size());
						for (Plan pp : subSubPlans) {
							List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
							if(subSubSubPlans.size() > 0){
								pp.setSubPlans(subSubSubPlans);
								pp.setSubSum(subSubSubPlans.size());
								for (Plan ppp : subSubSubPlans) {
									List<Plan> subSubSubSubPlans = planService.findSubPlans(ppp.getPlanId());
									if(subSubSubSubPlans.size() > 0){
										ppp.setSubPlans(subSubSubSubPlans);
										ppp.setSubSum(subSubSubSubPlans.size());
									}
								}
							}
						}
					}
				}
			}
		}
		
		if(flag != null && !"".equals(flag))
			mv.addObject("flag", flag);
		mv.addObject("plan", plan);
		return mv;
	}
	
	/**
	 * @description: 选择监察人
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/chose_supervisor")
	public ModelAndView choseSupervisor(){
		ModelAndView mv = new ModelAndView("pages/planMonitor/monitorAssignment/chose_supervisor");
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOfficeAndEmp").getValue();
		mv.addObject("CQList", CQList);
		return mv;
	}
	
	/**
	 * @description: 保存监察人
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/save_supervisor")
	public String saveSupervisor(Plan plan){
		planService.updatePlan(plan);
		Integer planType = plan.getPlanType();
		return "redirect:/find_monitor_assignment_0_10_0?planType="+planType+"&&flag=save";
	}
}
