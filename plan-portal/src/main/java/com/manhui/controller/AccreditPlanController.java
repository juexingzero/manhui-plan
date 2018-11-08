package com.manhui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.manhui.model.Plan;
import com.manhui.service.PlanService;
import com.manhui.util.PageUtil;



/**
 * @ClassName: AccreditPlanController
 * @description:授权查看的计划控制层
 * @author:	RanMaoKun
 * @date Create in 2017年10月24日10:58:45
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class AccreditPlanController {
	
	
	
	//引入计划类类型
	@Autowired
	private PlanService planService;
	
	/**
	 * 分页显示授权查看主计划
	 * @param model
	 * @param plan
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @return
	 */
	@GetMapping("/toFindAccreditPlan_{pageCurrent}_{pageSize}_{pageCount}")
	public String toFindMainPlan(Model model,Plan plan,@PathVariable Integer pageCurrent,
	@PathVariable Integer pageSize,@PathVariable Integer pageCount){
		//需得到登录用户id 添加到plan对象授权查看人
		//转模糊查询
		String planName=plan.getPlanName();
		if(planName!=null){
			planName="%"+planName+"%";
			plan.setPlanName(planName);
		}
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows=this.planService.count(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> pList=this.planService.findPlan(plan);
		
		model.addAttribute("pList",pList);
		
		String pageHTML=PageUtil.getPageContent("toAccreditPlan_{pageCurrent}_{pageSize}_{pageCount}?"
				+ "planName="+plan.getPlanName(),
				pageCurrent, pageSize, pageCount);
		model.addAttribute("pageHTML",pageHTML);
		return "pages/planAccredit/accreditplan";
	}
	
	@RequestMapping("/plandetails")
	public String planDetails(String planId,Model model){
		Plan plan=this.planService.findById(Integer.parseInt(planId));
		// 查询子计划详细
		Integer parentPlan = plan.getPlanId();
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		if (subPlans.size() > 0 && subPlans != null) {
				for (Plan p : subPlans) {
					List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
					if (subSubPlans.size() > 0&& subSubPlans != null)
						p.setSubPlans(subSubPlans);
					for (Plan pp : subSubPlans) {
						List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
						if (subSubSubPlans.size() > 0&& subSubSubPlans != null)
							pp.setSubPlans(subSubSubPlans);
							for(Plan ppp:subSubSubPlans){
								List<Plan> subSubSubSubPlans = planService.findSubPlans(ppp.getPlanId());
								if(subSubSubSubPlans.size()>0&& subSubSubSubPlans != null)
									ppp.setSubPlans(subSubSubSubPlans);
							}
						}
					}
				plan.setSubPlans(subPlans);
			}
		model.addAttribute("plan",plan);
		return "pages/planAccredit/planaccreditdetails";
		
	}
}
