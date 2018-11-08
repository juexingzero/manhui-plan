package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.manhui.model.Acceptance;
import com.manhui.model.ActionPlan;
import com.manhui.model.Log;
import com.manhui.model.User;
import com.manhui.service.AcceptanceService;
import com.manhui.service.ActionPlanService;
import com.manhui.service.LogService;

/**
 * @ClassName: AcceptanceController
 * @description: 待验收计划控制器
 * @author:	jiangxiaosong	
 * @date Create in 下午9:36:25 2017年10月26日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class AcceptanceController {

	@Autowired
    private AcceptanceService acceptanceService;
	
	@Autowired
	private ActionPlanService actionPlanService;
	
	@Autowired
	private LogService logSerivce;
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	 }
	
	
	/**
	 * 查询待验证的计划
	 * @param model
	 * @param plan
	 * @param pageCurrent
	 * @param httpSession
	 * @param pageSize
	 * @param pageCount
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/findAccptancePlan_{pageCurrent}_{pageSize}_{pageCount}")
	public String findAccptancePlan(Model model,ActionPlan plan,@PathVariable Integer pageCurrent,HttpSession httpSession,
						             @PathVariable Integer pageSize,@PathVariable Integer pageCount) throws ParseException{
		
		//取得当前登录人名称
		User user = (User)httpSession.getAttribute("user");
		
		// 转换子计划模糊查询
		String planName = plan.getActionPlanName();
		if (planName != null) {
			planName = "%" + planName + "%";
			plan.setActionPlanName(planName);
		}

		// 查询所有待验收状态的子计划
//		if (pageSize == 0)
//			pageSize = 10;
//		if (pageCurrent == 0)
//			pageCurrent = 1;
//		int rows = acceptanceService.findAcceptanceCount(plan);
//		if (pageCount == 0)
//			pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;
//		
//		plan.setStart((pageCurrent - 1) * pageSize);
//		plan.setEnd(pageSize);
//		List<ActionPlan> actionPlans = acceptanceService.findAcceptancePage(plan);

		//先查询所以状态为待验收的子计划
		List<ActionPlan> acceptanceSubPlans = actionPlanService.findAcceptanceSubPlan(plan);
		
		List<ActionPlan> planViewList = new ArrayList<>();
		//将所有子计划找到父节点/主节点，并且将节点去重且只有当前用户的主计划放入展示list中
		for(ActionPlan subActionPlan : acceptanceSubPlans){
			//取得所有子计划的父节点，且判断是否为主计划
			Integer parentActionPlanId = subActionPlan.getActionParentId();
			ActionPlan parentActionPlan = actionPlanService.findActionPlanByPlanId(parentActionPlanId);
			if(parentActionPlan != null){
				//还有父计划则不是主计划
				Integer parentParentActionPlanId = parentActionPlan.getActionParentId();
				ActionPlan parentParentActionPlan = actionPlanService.findActionPlanByPlanId(parentParentActionPlanId);
				if(parentParentActionPlan != null){
					//还有父计划则不是主计划
					Integer parentParentParentActionPlanId = parentParentActionPlan.getActionParentId();
					ActionPlan parentParentParentActionPlan = actionPlanService.findActionPlanByPlanId(parentParentParentActionPlanId);
					if(parentParentParentActionPlan == null){
						//已经是主计划，可以将当前用户的主计划放入展示list中
						//parentParentActionPlan
						ActionPlan planViews = new ActionPlan();
						planViews.setActionPlanId(parentParentActionPlan.getActionPlanId());
						planViews.setActionPlanName(parentParentActionPlan.getActionPlanName());
						planViewList.add(planViews);
					}
				}else{
					//已经是主计划，可以将当前用户的主计划放入展示list中
					//parentActionPlan
					ActionPlan planViews = new ActionPlan();
					planViews.setActionPlanId(parentActionPlan.getActionPlanId());
					planViews.setActionPlanName(parentActionPlan.getActionPlanName());
					planViewList.add(planViews);
				}
			}else{
				//已经是主计划，可以将当前用户的主计划放入展示list中
				//subActionPlan
				ActionPlan planViews = new ActionPlan();
				planViews.setActionPlanId(subActionPlan.getActionPlanId());
				planViews.setActionPlanName(subActionPlan.getActionPlanName());
				planViewList.add(planViews);
			}
		}
		
		//将展示list里的主计划进行去重
		for (int i = 0; i < planViewList.size(); i++) {
			for (int j = i + 1; j < planViewList.size(); j++) {
				if (planViewList.get(i).getActionPlanId() == planViewList.get(j).getActionPlanId()) {
					planViewList.remove(j);
				}
			}
		}
		
		//将list循环查询到下面的子节点ID并查询出子计划是待验收状态的计划,将待验收的计划数据加入到一个临时列表中
		for(ActionPlan planView : planViewList){
			//全部是主计划，查询下属子计划
			List<ActionPlan> subActionPlans = actionPlanService.findSubActionPlan(planView.getActionPlanId());
			for(ActionPlan subActionPlan : subActionPlans){
				if(subActionPlan.getState() == 7){
					//状态为待验收的子计划才能进行展示,判断子计划下属还有子计划么？
					Integer subActionPlanId = subActionPlan.getActionPlanId();
					List<ActionPlan> subSubActionPlans = actionPlanService.findSubActionPlan(subActionPlanId);
					if(subSubActionPlans != null && subSubActionPlans.size() > 0){
						//子计划下有子计划
						for(ActionPlan subSubActionPlan : subSubActionPlans){
							if(subSubActionPlan.getState() == 7){
								Integer subSubActionPlanId = subSubActionPlan.getActionPlanId();
								List<ActionPlan> subSubSubActionPlans = actionPlanService.findSubActionPlan(subSubActionPlanId);
								if(subSubSubActionPlans != null && subSubSubActionPlans.size() > 0){
									//子计划下还有子计划
									for(ActionPlan subSubSubActionPlan : subSubSubActionPlans){
										if(subSubSubActionPlan.getState() == 7){
											Integer subSubSubActionPlanId = subSubSubActionPlan.getActionPlanId();
											List<ActionPlan> subSubSubSubActionPlans = actionPlanService.findSubActionPlan(subSubSubActionPlanId);
											//默认为最底层子计划，直接将数据装入展示对象中subSubSubActionPlan
											planView.setSubPlanList(subSubSubActionPlans);
										}
									}
								}else{
									//子计划下无子计划，将数据装入展示对象中subSubActionPlan
									planView.setSubPlanList(subActionPlans);
									subActionPlan.setSubPlanList(subSubActionPlans);
									subSubActionPlan.setSubPlanList(subSubActionPlans);
								}
							}
						}
					}else{
						//子计划下无子计划，将数据装入展示对象中subActionPlan
						planView.setSubPlanList(subActionPlans);
						subActionPlan.setSubPlanList(subSubActionPlans);
					}
				}
			}
		}
		
		model.addAttribute("planViewList", planViewList);
		
		//return TODO 页面未出来
		return "";
	}
	
	
	
	/**
	 * 跳转到验收页面
	 * @param model
	 * @param actionPlan
	 * @return
	 */
	/*@GetMapping("/toAcceptancePage")
	public String toAcceptancePage(Model model,String actionPlanId,String orginPlanId){
		
		ActionPlan actionPlan = new ActionPlan();
		
		if(actionPlanId != null && !"".equals(actionPlanId)){
			actionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(actionPlanId));
			//查询出当前任务对应完成文件
			PlanFile findTemple = new PlanFile();
			findTemple.setModel("complate_plan");
			findTemple.setModelId(actionPlan.getActionPlanId());
			List<PlanFile> planFiles = planFileService.findPlanFileByModelId(findTemple);
			model.addAttribute("fileList", planFiles);
		}
		
		model.addAttribute("actionPlan", actionPlan);
		model.addAttribute("orginPlanId", orginPlanId);
		//return TODO 页面未出来
		return "";
	}*/
	
	
	/**
	 * 跳转到详细画面
	 * @param model
	 * @param actionPlanId
	 * @return
	 */
	/*@GetMapping("/toAcceptanceDetail")
	public String toAcceptanceDetail(Model model,String actionPlanId,HttpSession session){
		
		User user = (User)session.getAttribute("user");
		//查询出当前计划信息
		ActionPlan actionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(actionPlanId));
		//查询出当前计划子计划信息
		Integer actionPlansId = actionPlan.getActionPlanId();
		List<ActionPlan> actionPlans = actionPlanService.findSubActionPlan(actionPlansId);
		for(ActionPlan ap : actionPlans){
			if(ap.getAdjustType() == 6){
				ap.setIsAdvance(1);
			}else if(ap.getAdjustType() == 7){
				ap.setIsDelay(1);
			}
		}
		//查找完成文件
		PlanFile planFile = new PlanFile();
		planFile.setModel("complate_plan");
		planFile.setModelId(actionPlan.getActionPlanId());
		List<PlanFile> complateFile = planFileService.findPlanFileByModelId(planFile);
		model.addAttribute("complateFile", complateFile);
		
		//查找对应日志数据及文件
		if(user != null){
			Log log = new Log();
			log.setActionPlanId(actionPlansId);
			log.setCreatePerson(user.getUserName());
			List<Log> logList = logSerivce.findLogByPlanIdAndCreatePerson(null);
			model.addAttribute("logList", logList);
			if(logList != null && logList.size() >0){
				Integer logId = 0;
				for(Log logs : logList){
					logId = logs.getLogId();
				}
				PlanFile planFiles = new PlanFile();
				planFiles.setModel("log");
				planFiles.setModelId(logId);
				List<PlanFile> logFiles = planFileService.findPlanFileByModelId(planFiles);
				model.addAttribute("logFiles", logFiles);
			}
		}
		
		//return TODO 页面未出来
		return "";
	}*/
	
	
	//验收功能
	@PostMapping("plsnAcceptance")
	public String plsnAcceptance(String acceptanceResult,String remark,Model model,String actionPlanId,String orginPlanId){
		
		//先保存验收信息
		Acceptance acceptance = new Acceptance();
		acceptance.setAcceptanceId(Integer.parseInt(actionPlanId));
		acceptance.setAcceptanceResult(Integer.parseInt(acceptanceResult));
		acceptance.setRemark(remark);
		acceptanceService.saveAcceptance(acceptance);
		
		//变更当前执行计划状态
		ActionPlan actionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(actionPlanId));
		actionPlan.setState(3);
		actionPlanService.updateActionPlan(actionPlan);
		
		//如果全部子计划都完成了需要变更原计划状态
		ActionPlan OrginActionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(orginPlanId));
		//查询原计划下所有子计划是否全部完成
		List<ActionPlan> subActionPlans = actionPlanService.findSubActionPlan(Integer.parseInt(orginPlanId));
		Integer complateIndex = 0;
		for(ActionPlan subActionPlan : subActionPlans){
			if(subActionPlan.getState() == 3){
				complateIndex = complateIndex +1;
			}
		}
		if(complateIndex == subActionPlans.size()){
			//全部子计划已完成，可以更改原计划状态
			OrginActionPlan.setState(3);
			actionPlanService.updateActionPlan(OrginActionPlan);
		}
		
		return "redirect:findAccptancePlan";
	}
	
}
