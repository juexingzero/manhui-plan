package com.manhui.controller.planMonitor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Plan;
import com.manhui.model.User;
import com.manhui.service.PlanService;

/**
 * @ClassName: MyMonitorController
 * @description: 计划监察-我的监察控制器
 * @author:	HeJiayan
 * @date Create in 下午5:55:49 2017年12月8日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class MyMonitorController {
	@Autowired
    private PlanService planService;
	
	/**
	 * @description: 我的监察页面跳转
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/my_monitor_plan")
	public ModelAndView myMonitorPlan(){
		ModelAndView mv = new ModelAndView("pages/planMonitor/myMonitor/my_monitor");
		return mv;
	}
	
	/**
	 * @description: 分页查询当前登录用户负责监察的已过审的所有主计划
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param plan
	 * @param flag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/my_monitor_list_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView findMonitorAssignment(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Plan plan){
		ModelAndView mv = new ModelAndView("pages/planMonitor/myMonitor/my_monitor_list");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		String planName = plan.getPlanName();
		if(planName != null && !"".equals(planName))
			plan.setPlanName("%"+planName+"%");
		plan.setSupervisorId(user.getUserId());
		
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
		
		plan.setPlanName(planName);
		mv.addObject("plan", plan);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 查看计划详情
	 * @param planId
	 * @param flag
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/check_mainplan_detail")
	public ModelAndView checkMainplanDetail(String planId){
		ModelAndView mv = new ModelAndView("pages/planMonitor/myMonitor/monitor_plan_detail");
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
		
		mv.addObject("plan", plan);
		return mv;
	}
}
