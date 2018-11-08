package com.manhui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.model.Menu;
import com.manhui.model.Plan;
import com.manhui.service.PlanService;

/**
 * @ClassName: PlanReSortController
 * @description: 计划排序控制器
 * @author:	HeJiayan
 * @date Create in 上午10:18:52 2017年12月18日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanReSortController {
	@Autowired
    private PlanService planService;
	
	/**
	 * @description: 调序页面跳转
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/re_sort_plan")
	public ModelAndView reSortPlan(String planId, String editFlag, String flag){
		ModelAndView mv = new ModelAndView("pages/sortPlan/re_sort_plan");
		Plan plan = planService.findById(Integer.parseInt(planId));
		List<Plan> planList = planService.findSubPlans(plan.getParentId());
		List<Plan> sortList = new ArrayList<Plan>();
		for (Plan p : planList) {
			if(p.getPlanId() != Integer.parseInt(planId))
				sortList.add(p);
		}
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		if(flag != null && !"".equals(flag))
			mv.addObject("flag", flag);
		mv.addObject("planList", planList);
		mv.addObject("sortList", sortList);
		mv.addObject("planId", planId);
		return mv;
	}
	
	/**
	 * @description: 异步排序，实现预览
	 * @param  aimPlanId
	 * @param  planId
	 * @param  location
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/set_plan_sort")
	@ResponseBody
	public List<Plan> setPlanSort(String aimPlanId, String planId, String location){
		List<Plan> planList = getSortPlanList(aimPlanId, planId, location);
		return planList;
	}
	
	/**
	 * @description: 保存计划排序
	 * @param aimPlanId
	 * @param planId
	 * @param location
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/save_plan_sort")
	public ModelAndView savePlanSort(String aimPlanId, String planId, String location, String editFlag, String flag){
		ModelAndView mv = new ModelAndView();
		List<Plan> planList = getSortPlanList(aimPlanId, planId, location);
		for (Plan plan : planList) {
			planService.updatePlan(plan);
		}
		
		if(editFlag != null && !"".equals(editFlag)){
			Plan plan = getAllPlans(Integer.parseInt(planId));
			mv.addObject("plan", plan);
			mv.addObject("editFlag", editFlag);
			mv.setViewName("pages/planCompile/create_plan");
		}
		if(flag != null && !"".equals(flag)){
			Plan plan = planService.findById(planService.findById(Integer.parseInt(planId)).getParentId());
			List<Plan> subPlans = planService.findSubPlans(plan.getPlanId());
			plan.setSubPlans(subPlans);
			
			if(plan.getParentId() == null || "".equals(plan.getParentId())){
	    		mv.setViewName("pages/planDecompose/decompose_main");
	    	}else{
	    		mv.setViewName("pages/planDecompose/decompose_sub");
	    		Plan newPlan = getTopPlan(plan.getPlanId());
	    		mv.addObject("loopMode", newPlan.getLoopMode());
	    	}
			mv.addObject("plan", plan);
		}
		return mv;
	}
	
	/**
	 * @description: 取消排序
	 * @param  planId
	 * @param  editFlag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_sort")
	public ModelAndView cancelSort(String planId, String editFlag, String flag){
		ModelAndView mv = new ModelAndView();
		if(editFlag != null && !"".equals(editFlag)){
			Plan plan = getAllPlans(Integer.parseInt(planId));
			mv.addObject("plan", plan);
			mv.addObject("editFlag", editFlag);
			mv.setViewName("pages/planCompile/create_plan");
		}
		
		if(flag != null && !"".equals(flag)){
			Plan plan = planService.findById(planService.findById(Integer.parseInt(planId)).getParentId());
			List<Plan> subPlans = planService.findSubPlans(plan.getPlanId());
			plan.setSubPlans(subPlans);
			
			if(plan.getParentId() == null || "".equals(plan.getParentId())){
	    		mv.setViewName("pages/planDecompose/decompose_main");
	    	}else{
	    		mv.setViewName("pages/planDecompose/decompose_sub");
	    		Plan newPlan = getTopPlan(plan.getPlanId());
	    		mv.addObject("loopMode", newPlan.getLoopMode());
	    	}
			mv.addObject("plan", plan);
		}
		return mv;
	}
	
	/**
	 * @description: 进行计划预排序
	 * @param  aimPlanId
	 * @param  planId
	 * @param  location
	 * @param 
	 * @return 
	 * @throws 
	 */
	private List<Plan> getSortPlanList(String aimPlanId, String planId, String location) {
		Plan aimPlan = planService.findById(Integer.parseInt(aimPlanId));
		Plan plan = planService.findById(Integer.parseInt(planId));
		List<Plan> planList = planService.findSubPlans(plan.getParentId());
		
		if("before".equals(location)){
			for (Plan p : planList) {
				if(p.getPlanOrder() >= plan.getPlanOrder())
					p.setPlanOrder(p.getPlanOrder() + 1);
			}
			
			for (Plan p : planList) {
				if(p.getPlanId() == aimPlan.getPlanId() || p.getPlanId().equals(aimPlan.getPlanId()))
					p.setPlanOrder(plan.getPlanOrder());
			}
		}else if("after".equals(location)){
			for (Plan p : planList) {
				if(p.getPlanOrder() >= (plan.getPlanOrder()+1))
					p.setPlanOrder(p.getPlanOrder() + 1);
			}
			
			for (Plan p : planList) {
				if(p.getPlanId() == aimPlan.getPlanId() || p.getPlanId().equals(aimPlan.getPlanId()))
					p.setPlanOrder(plan.getPlanOrder() + 1);
			}
		}
		//计划排序
		Collections.sort(planList, new Comparator<Plan>(){
			@Override
			public int compare(Plan o1, Plan o2) {
				if(o1.getPlanOrder()>o2.getPlanOrder()){
                    return 1;
                }
                if(o1.getPlanOrder()==o2.getPlanOrder()){
                    return 0;
                }
				return -1;
			}
		});
		for (int i = 0; i < planList.size(); i++) {
			planList.get(i).setPlanOrder(i+1);
		}
		return planList;
	}
	
	/**
	 * @description: 根据ID查询计划及其所有子父级计划
	 * @param @return
	 * @return 
	 * @throws
	 */
	public Plan getAllPlans(Integer planId){
		Plan plan = planService.findById(planId);
		if(plan.getParentId() != null && !"".equals(plan.getParentId())){
			plan = planService.findById(plan.getParentId());
			if(plan.getParentId() != null && !"".equals(plan.getParentId())){
				plan = planService.findById(plan.getParentId());
				if(plan.getParentId() != null && !"".equals(plan.getParentId())){
					plan = planService.findById(plan.getParentId());
					if(plan.getParentId() != null && !"".equals(plan.getParentId())){
						plan = planService.findById(plan.getParentId());
					}
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
		return plan;
	}
	
	/**
	 * @description: 根据ID查询最顶层父级计划
	 * @param @return
	 * @return 
	 * @throws
	 */
	public Plan getTopPlan(Integer planId){
		Plan plan = planService.findById(planId);
		if(plan.getParentId() != null && !"".equals(plan.getParentId())){
			plan = planService.findById(plan.getParentId());
			if(plan.getParentId() != null && !"".equals(plan.getParentId())){
				plan = planService.findById(plan.getParentId());
				if(plan.getParentId() != null && !"".equals(plan.getParentId())){
					plan = planService.findById(plan.getParentId());
				}
			}
		}
		return plan;
	}
}
