package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.ActionPlan;
import com.manhui.model.Plan;
import com.manhui.model.PlanAdjust;
import com.manhui.model.PlanAssess;
import com.manhui.model.User;
import com.manhui.service.ActionPlanService;
import com.manhui.service.PlanAdjustService;
import com.manhui.service.PlanAssessService;
import com.manhui.service.PlanService;

/**
 * @ClassName: PlanAdjustController
 * @Description: 调整申请记录控制器 
 * @author WangSheng
 * @date: 2017年10月27日18:43:02
 */
@Controller
public class PlanAdjustController {

	@Autowired
	private PlanAdjustService planAdjustService;
	
	@Autowired
	private ActionPlanService actionPlanService;
	
	@Autowired
	private PlanService planService;
	
	//引入考核类型
	@Autowired
	private PlanAssessService planAssessService;
	
	
	/**
	 * 跳转到计划调整申请记录改变页面
	 * @param model
	 * @return
	 */
	@GetMapping("toPlanAdjustChange")
	public String toPlanAdjustChange(Model model){
		return "pages/planadjust/change_plan_type";
	}

	
	/**
	 * 调整申请记录进行分页显示
	 * @param reminders
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param model
	 * @return
	 */
	@GetMapping("/toFindPlanAdjust_{pageCurrent}_{pageSize}_{pageCount}")
	public String toFindPlanAdjust(Model model,PlanAdjust plan,@PathVariable Integer pageCurrent,String actionflag,
									@PathVariable Integer pageSize,@PathVariable Integer pageCount,String planType){
		
		//转模糊查询(计划)
		String planName=plan.getPlanName();
		if(planName!=null){
			planName="%"+planName+"%";
			plan.setPlanName(planName);
		}
		
		if(planType != null && !"".equals(planType)){
			plan.setPlanType(Integer.parseInt(planType));
		}
		
		
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = this.planAdjustService.count(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		plan.setStart((pageCurrent-1)*pageSize);
		plan.setEnd(pageSize);
		
		List<PlanAdjust> planAdjustList = planAdjustService.findPlanAdjustByPage(plan);
		
		for (PlanAdjust p : planAdjustList) {
			
			if(p.getParentPlanName()==null||p.getParentPlanName()==""){
				p.setParentPlanName("--");
			}
			
			//获取显示用的机构
			Plan plans = planService.findById(p.getPlanId());
			p.setBlameOrganization(plans.getBlameOrganization() == null ? "" : plans.getBlameOrganization());
			p.setBlameSection(plans.getBlameSection() == null ? "" : plans.getBlameSection());
			p.setBlamePerson(plans.getBlamePerson() == null ? "" : plans.getBlamePerson());
			p.setParentId(plans.getParentId());
		}
		
		if("adjust_ok".equals(actionflag)){
			actionflag = "调整申请成功";
		}
		model.addAttribute("actionflag", actionflag);
		model.addAttribute("planAdjustList", planAdjustList);
		model.addAttribute("planType", planType);
		
		return "pages/planadjust/titlehand";
	} 
	
	
	/**
	 * 查看计划调整申请记录详情
	 * @param planadjustid
	 * @param model
	 * @return
	 */
	@RequestMapping("/handDetails")
	public String handDetails(String planId,Model model){
		
		Plan plan = planService.findById(Integer.parseInt(planId));
		
		//获取子计划
		if(plan != null){
			List<Plan> subPlans = planService.findSubPlans(plan.getPlanId());
			if(subPlans != null && subPlans.size() > 0){
				plan.setSubPlans(subPlans);
				for(Plan subPlan : subPlans){
					//查询是否延期
					List<PlanAdjust> subPlanAdjust = planAdjustService.findPlanAdjustByPlanId(subPlan.getPlanId());
					if(subPlanAdjust != null && subPlanAdjust.size() > 0){
						subPlan.setAdjustStatus(subPlanAdjust.get(0).getStatus());
					}
					List<Plan> subSubPlans = planService.findSubPlans(subPlan.getPlanId());
					if(subSubPlans != null && subSubPlans.size()>0){
						subPlan.setSubPlans(subSubPlans);
						for(Plan subSubPlan : subSubPlans){
							//查询是否延期
							List<PlanAdjust> subSubPlanAdjust = planAdjustService.findPlanAdjustByPlanId(subSubPlan.getPlanId());
							if(subSubPlanAdjust != null && subSubPlanAdjust.size()>0){
								subSubPlan.setAdjustStatus(subSubPlanAdjust.get(0).getStatus());
							}
							List<Plan> subSubSubPlans = planService.findSubPlans(subSubPlan.getPlanId());
							if(subSubSubPlans != null && subSubSubPlans.size() > 0){
								subSubPlan.setSubPlans(subSubSubPlans);
								for(Plan subSubSubPlan : subSubSubPlans){
									//查询是否延期
									List<PlanAdjust> subSubSubPlanAdjust = planAdjustService.findPlanAdjustByPlanId(subSubSubPlan.getPlanId());
									if(subSubSubPlanAdjust != null && subSubSubPlanAdjust.size() > 0){
										subSubSubPlan.setAdjustStatus(subSubSubPlanAdjust.get(0).getStatus());
									}
									List<Plan> subSubSubSubPlans = planService.findSubPlans(subSubSubPlan.getPlanId());
									if(subSubSubSubPlans != null && subSubSubSubPlans.size() > 0){
										subSubSubPlan.setSubPlans(subSubSubSubPlans);
									}
								}
							}
						}
					}
				}
			}
		}
		
		model.addAttribute("plan", plan);
		
		return "pages/planadjust/handdetail";
	}
	
	
	/**
	 * 查看主计划计划详情(以前)
	 * @param actionplanid
	 * @param model
	 * @return
	 *//*
	@RequestMapping("toFindActionPlan")
	public String toFindActionPlan(String actionplanid,Model model){
	
		ActionPlan actionPlan=actionPlanService.findActionPlanByPlanId(Integer.parseInt(actionplanid));
		
		//查询子计划
		//第一层
		List<ActionPlan> actionPlanO=actionPlanService.findSubActionPlan(actionPlan.getActionPlanId());
		
		if(actionPlanO.size()>0){
			for (ActionPlan apo : actionPlanO) {
				List<ActionPlan> actionPlanT=actionPlanService.findSubActionPlan(apo.getActionPlanId());
				//第二层
				if(actionPlanT.size()>0){
					apo.setSubPlanList(actionPlanT);
					for (ActionPlan opt : actionPlanT) {
						List<ActionPlan> actionPlanTH=actionPlanService.findSubActionPlan(opt.getActionPlanId());
						//第三层
						if(actionPlanTH.size()>0){
							opt.setSubPlanList(actionPlanTH);
							for (ActionPlan opth : actionPlanTH) {
								List<ActionPlan> actionPlanF=actionPlanService.findSubActionPlan(opth.getActionPlanId());
								//第四层
								if(actionPlanF.size()>0){
									opth.setSubPlanList(actionPlanF);
								}
							}
						}
					}
				}
			}
			actionPlan.setSubPlanList(actionPlanO);
		}
		
		model.addAttribute("actionPlan", actionPlan);
		return "pages/planadjust/plandetails";
	}*/
	
	
	
	/**
	 * 跳转到选择页面
	 * @param model
	 * @return
	 */
	@RequestMapping("select_plan_adjust")
	public String selectPlanAdjust(Model model){
		return "pages/planadjust/chose_adjust";
	}
	
	
	/**
	 * 跳转到申请调整页面
	 * @param model
	 * @param location
	 * @return
	 */
	@RequestMapping("toadjust_plan")
	public String toAdjustPlan(Model model,String planLocation){
		
		if("delay".equals(planLocation)){
			//计划延期
			model.addAttribute("status", "delay");
			return "pages/planadjust/adjust_plan_apply";
		}else if("suspend".equals(planLocation)){
			//计划暂停
			model.addAttribute("status", "suspend");
			return "pages/planadjust/adjust_plan_apply";
		}else if("suspension".equals(planLocation)){
			//计划中止
			model.addAttribute("status", "suspension");
			return "pages/planadjust/adjust_plan_apply";
		}else{
			//计划变更
			model.addAttribute("status", "change");
			User user = (User) CacheManager.getCacheInfo("user").getValue();
			//查询所有要调整的计划
			List<Plan> list = planService.findAdjustPlan(user.getName());
			
			model.addAttribute("list", list);
			return "pages/planadjust/choose_plan";
		}
	}
	
	/**
	 * 打开选择计划
	 * @param model
	 * @param planType
	 * @return
	 */
	@RequestMapping("to_choose_plan")
	public String toChoosePlan(Model model,String planType){
		
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		//查询所有要调整的计划
		List<Plan> list = planService.findAdjustPlan(user.getName());
		
		model.addAttribute("list", list);
		
		return "pages/planadjust/choose_plan";
	}
	
	
	/**
	 * 保存申请
	 * @param model
	 * @param planAdjust
	 * @param location
	 * @return
	 * @throws ParseException 
	 */
	@PostMapping("save_plan_adjust")
	public String savePlanAdjust(Model model,String planId,String location,String planName,String actionflag,
								String postponeTime,String postponeTimeLimit,String adjustReason,String adjustContent) throws ParseException{
		
		
		Date createDate = new Date();
		PlanAdjust save = new PlanAdjust();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
			//查询出计划数据
			if(planId != null && !"".equals(planId)){
				Plan plan = planService.findById(Integer.parseInt(planId));
				save.setPlanId(plan.getPlanId());
				if(postponeTime != null && !"".equals(postponeTime)){
					save.setPostponeTime(sdf.parse(postponeTime));
				}
				if(postponeTimeLimit != null){
					save.setPostponeTimeLimit(Integer.parseInt(postponeTimeLimit));
				}
				save.setAdjustReason(adjustReason);
				save.setAdjustContent(adjustContent);
				save.setPlanName(plan.getPlanName());
				Integer prarentId = plan.getParentId();
				if(prarentId != null && !"".equals(prarentId)){
					Plan prarentPlan = planService.findById(prarentId);
					save.setParentPlanName(prarentPlan.getPlanName());
				}
				if("delay".equals(location)){
					save.setStatus(1);//延期
				}else if("suspend".equals(location)){
					save.setStatus(2);//暂停
				}else if("suspension".equals(location)){
					save.setStatus(3);//终止
				}else if("change".equals(location)){
					save.setStatus(4);//变更
				}
				save.setCreateTime(sdf.format(createDate));
				save.setAdjustType(1);
				save.setStartTime(plan.getStartTime());
				save.setEndTime(plan.getEndTime());
				save.setPlanType(plan.getPlanType());
				planAdjustService.savePlanAdjustId(save);
			}
		
		return "redirect:toFindPlanAdjust_0_0_0?planType=1&actionflag="+actionflag;
	}
	
	/**
	 * 跳转到计划变更
	 * @param model
	 * @param planId
	 * @return
	 */
	@GetMapping("adjustplan_change")
	public String adjustPlanChange(Model model,String planId,String location){
		
		//查询选中计划的信息及子计划信息
		Plan plan = new Plan();
		if(planId != null && !"".equals(planId)){
			plan = planService.findById(Integer.parseInt(planId));
			if(plan != null){
				List<Plan> subPlans = planService.findSubPlans(plan.getPlanId());
				if(subPlans != null && subPlans.size() > 0){
					plan.setSubPlans(subPlans);
					plan.setSubSum(subPlans.size());
					for(Plan subPlan : subPlans){
						List<Plan> subSubPlans = planService.findSubPlans(subPlan.getPlanId());
						if(subSubPlans != null && subSubPlans.size() > 0){
							subPlan.setSubPlans(subSubPlans);
							subPlan.setSubSum(subSubPlans.size());
							for(Plan subSubPlan : subSubPlans){
								List<Plan> subSubSubPlans = planService.findSubPlans(subSubPlan.getPlanId());
								if(subSubSubPlans != null && subSubSubPlans.size()>0){
									subSubPlan.setSubPlans(subSubSubPlans);
									subSubPlan.setSubSum(subSubSubPlans.size());
									for(Plan subSubSubPlan : subSubSubPlans){
										List<Plan> subSubSubSubPlans = planService.findSubPlans(subSubSubPlan.getPlanId());
										if(subSubSubSubPlans != null && subSubSubSubPlans.size() > 0){
											subSubSubPlan.setSubPlans(subSubSubSubPlans);
											subSubSubPlan.setSubSum(subSubSubSubPlans.size());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		List<PlanAssess> pa = planAssessService.selectByPlanId(plan.getPlanId());
		model.addAttribute("plan", plan);
		model.addAttribute("location", location);
		model.addAttribute("paList", pa);
		return "pages/planadjust/change_plan";
	}
	
	
}
