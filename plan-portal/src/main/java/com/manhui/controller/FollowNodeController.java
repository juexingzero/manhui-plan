package com.manhui.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestMapping;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.ActionPlan;
import com.manhui.model.FollowNode;
import com.manhui.model.Office;
import com.manhui.model.Plan;
import com.manhui.model.User;
import com.manhui.service.FollowNodeService;
import com.manhui.service.PlanService;

/**
 * @ClassName: FollowNodeController
 * @description: 重点关注计划控制器
 * @author:	jiangxiaosong	
 * @date Update in 下午9:36:25 2017年11月27日
 * @version: v2.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class FollowNodeController {

/*	@Autowired
	private ActionPlanService actionPlanService;*/
	
	@Autowired
	private FollowNodeService followNodeService;
	
	@Autowired
	private PlanService planService;
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	 }
	
	
	@GetMapping("/change_follow_type")
	public String changeFollowType(Model model){
		return "pages/followNodes/change_follow_type";
	}
	
	
	/**
	 * 我关注的节点展示画面（太过复杂，逻辑确定之后调试）
	 * @param model
	 * @param plan
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/focus_plan_{pageCurrent}_{pageSize}_{pageCount}")
	public String toFindfollowNode(Model model, Plan plan, HttpSession httpSession,String planType,String actionflag,
							@PathVariable Integer pageCurrent,@PathVariable Integer pageSize,@PathVariable Integer pageCount) {

		// 取得当前登录人名称
		User user = (User) CacheManager.getCacheInfo("user").getValue();

		// 转换子计划模糊查询
		String planName = plan.getPlanName();
		if (planName != null) {
			planName = "%" + planName + "%";
			plan.setPlanName(planName);
		}
		
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planService.count(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		
		List<Plan> MyFollowPlanList = new ArrayList<>();
		
		// 查询这个用户关注的计划（用户关注的也有可能是原计划的）
		if (user != null) {
			List<FollowNode> followNodes = followNodeService.findFollowNodeByFollowPerson(user.getName());
			model.addAttribute("followNodes", followNodes);
			model.addAttribute("followPerson", user.getUserName());
			// 用关注的计划查询
			if (followNodes != null && followNodes.size() > 0) {
				for (FollowNode followNode : followNodes) {
					if (followNode.getPlanId() != null || !"".equals(followNode.getPlanId())) {
						
						//查询原计划的情况
						Plan followAcitonPlan = planService.findById(followNode.getPlanId());
						if(followAcitonPlan != null){
							followAcitonPlan.setPlanName(plan.getPlanName());
							followAcitonPlan.setStatus(plan.getStatus());
							followAcitonPlan.setStartTime(plan.getStartTime());
							followAcitonPlan.setEndTime(plan.getEndTime());
							followAcitonPlan.setPlanType(Integer.parseInt(planType));
							followAcitonPlan.setStart(0);
							followAcitonPlan.setEnd(10);
							List<Plan> followNodesActionPlans = planService.findPlansByPlan(followAcitonPlan);
							// 将所有子计划找到父节点/主节点，并且将节点去重且只有当前用户的主计划放入list中
							for (Plan subActionPlan : followNodesActionPlans) {
									// 取得所有子计划的父节点，且判断是否为主计划
									Integer parentActionPlanId = subActionPlan.getParentId();
									Plan parentActionPlan = planService.findById(parentActionPlanId);
									if (parentActionPlan != null) {
										// 还有父计划则不是主计划
										Integer parentParentActionPlanId = parentActionPlan.getParentId();
										Plan parentParentActionPlan = planService.findById(parentParentActionPlanId);
										if (parentParentActionPlan != null) {
											// 还有父计划则不是主计划
											Integer parentParentParentActionPlanId = parentParentActionPlan.getParentId();
											Plan parentParentParentActionPlan = planService.findById(parentParentParentActionPlanId);
											if (parentParentParentActionPlan == null) {
												// 已经是主计划，可以将当前用户的主计划放入展示list中
												// parentParentActionPlan
												Plan planViews = new Plan();
												planViews.setPlanId(parentParentActionPlan.getPlanId());
												planViews.setPlanName(parentParentActionPlan.getPlanName());
												planViews.setStartTime(parentParentActionPlan.getStartTime());
												planViews.setEndTime(parentParentActionPlan.getEndTime());
												planViews.setStatus(parentParentActionPlan.getStatus());
												planViews.setBlameOrganization(parentParentActionPlan.getBlameOrganization());
												planViews.setBlameSection(parentParentActionPlan.getBlameSection());
												planViews.setBlamePerson(parentParentActionPlan.getBlamePerson());
												MyFollowPlanList.add(planViews);
											}
										} else {
											// 已经是主计划，可以将当前用户的主计划放入展示list中
											// parentActionPlan
											Plan planViews = new Plan();
											planViews.setPlanId(parentActionPlan.getPlanId());
											planViews.setPlanName(parentActionPlan.getPlanName());
											planViews.setStartTime(parentActionPlan.getStartTime());
											planViews.setEndTime(parentActionPlan.getEndTime());
											planViews.setStatus(parentActionPlan.getStatus());
											planViews.setBlameOrganization(parentActionPlan.getBlameOrganization());
											planViews.setBlameSection(parentActionPlan.getBlameSection());
											planViews.setBlamePerson(parentActionPlan.getBlamePerson());
											MyFollowPlanList.add(planViews);
										}
									} else {
										// 已经是主计划，可以将当前用户的主计划放入展示list中
										// subActionPlan
										Plan planViews = new Plan();
										planViews.setPlanId(subActionPlan.getPlanId());
										planViews.setPlanName(subActionPlan.getPlanName());
										planViews.setStartTime(subActionPlan.getStartTime());
										planViews.setEndTime(subActionPlan.getEndTime());
										planViews.setStatus(subActionPlan.getStatus());
										planViews.setBlameOrganization(subActionPlan.getBlameOrganization());
										planViews.setBlameSection(subActionPlan.getBlameSection());
										planViews.setBlamePerson(subActionPlan.getBlamePerson());
										MyFollowPlanList.add(planViews);
									}
							}
						}
						
						
						// 将展示list里的主计划进行去重
						for (int i = 0; i < MyFollowPlanList.size(); i++) {
							for (int j = i + 1; j < MyFollowPlanList.size(); j++) {
								if (MyFollowPlanList.get(i).getPlanId().equals(MyFollowPlanList.get(j).getPlanId())) {
									MyFollowPlanList.remove(j);
								}
							}
						}
						
						// 将list循环查询到下面的子节点ID并查询出子计划是待验收状态的计划,将待验收的计划数据加入到一个临时列表中
						for (Plan planView : MyFollowPlanList) {
							// 全部是主计划，查询下属子计划
							List<Plan> subPlans = planService.findSubPlans(planView.getPlanId());
							for (Plan subPlan : subPlans) {
								// 判断是否当前用户关注的，不是则不显示
								if (subPlan.getPlanId().equals(followNode.getPlanId())) {
									// 状态为待验收的子计划才能进行展示,判断子计划下属还有子计划么？
									Integer subActionPlanId = subPlan.getPlanId();
									List<Plan> subSubPlans = planService.findSubPlans(subActionPlanId);
									for (Plan subSubPlan : subSubPlans) {
										if (subSubPlan.getPlanId().equals(followNode.getPlanId())) {
											Integer subSubPlanId = subSubPlan.getPlanId();
											List<Plan> subSubSubPlans = planService.findSubPlans(subSubPlanId);
											if (subSubSubPlans != null && subSubSubPlans.size() > 0) {
												// 子计划下还有子计划
												for (Plan subSubSubPlan : subSubSubPlans) {
													if (subSubSubPlan.getPlanId().equals(followNode.getPlanId())) {
														Integer subSubSubPlanId = subSubSubPlan.getPlanId();
														List<Plan> subSubSubSubPlans = planService.findSubPlans(subSubSubPlanId);
														// 默认为最底层子计划，直接将数据装入展示对象中subSubSubActionPlan
														subSubPlan.setSubPlans(subSubSubPlans);
														subPlan.setSubPlans(subSubPlans);
														planView.setSubPlans(subPlans);
													}
												}
											} 
											// 子计划下无子计划，将数据装入展示对象中subSubActionPlan
											subPlan.setSubPlans(subSubPlans);
											planView.setSubPlans(subPlans);
										}
									}
									// 子计划下无子计划，将数据装入展示对象中subActionPlan
									planView.setSubPlans(subPlans);
								}
							}
						}
					}
				}
			}
			Map<Integer,String> fnMap = new HashMap<>();
			//将被本用户关注的计划制成map
			for(FollowNode fn : followNodes){
				fnMap.put(fn.getPlanId(), fn.getPlanName());
			}
			
			int index = MyFollowPlanList.size();
			for(int i=0;i<index;i++){
				Plan followPlan = MyFollowPlanList.get(i);
				List<Plan> subPlans = followPlan.getSubPlans();
				if(subPlans != null){
					int subIndex = subPlans.size();
					for(int j=0;j<subIndex;j++){
						Plan subPlan = subPlans.get(j);
						if(fnMap.get(subPlan.getPlanId())!=null){
							continue;
						}else{
							subPlans.remove(j);
							j--;
							subIndex--;
						}
					}
				}
			}
		}
		
		if("follow_ok".equals(actionflag)){
			actionflag = "计划关注成功";
		}

		model.addAttribute("actionflag", actionflag);
		/*model.addAttribute("followActionPlan", MyFollowActionPlanList);*/
		model.addAttribute("followPlan", MyFollowPlanList);
		model.addAttribute("rows", rows);
		model.addAttribute("pageCurrent", pageCurrent);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("planType", planType);
		return "pages/followNodes/showfollownode";
	}
	
	
	
	/**
	 * 删除关注的计划
	 * @param model
	 * @param actionPlanId
	 * @param followPerson
	 * @return
	 */
	//删除关注计划
	@GetMapping("deleteFocusPlan")
	public String deleteFocusPlan(Model model,String planId,String followPerson,String type){
		
		//判断传过来的planId是原计划的ID还是执行计划ID
		FollowNode followNode = new FollowNode();
		if("plan".equals(type)){
			//原计划
			followNode.setPlanId(Integer.parseInt(planId));
			followNode.setFollowPerson(followPerson);
			FollowNode planNode = followNodeService.findFollowNode(followNode);
			if(planNode != null){
				followNodeService.deleteFollowNodes(planNode.getId());
			}
		}else if("action_plan".equals(type)){
			//执行计划
			followNode.setActionPlanId(Integer.parseInt(planId));
			followNode.setFollowPerson(followPerson);
			FollowNode planNode = followNodeService.findFollowNode(followNode);
			followNodeService.deleteFollowNodes(planNode.getId());
		}
		
		return "redirect:focus_plan_0_0_0";
	}
	
	
	
	/**
	 * 主计划详细页面跳转
	 * @param model
	 * @param planId
	 * @param type
	 * @return
	 */
	@GetMapping("getMainPlan")
	public String getMainPlan(Model model,String planId,String type){
		
		if(planId != null && "".equals(planId)){
			
			//先判断是原计划还是执行计划
			if("plan".equals(type)){
				//查询原计划表
				Plan orginPlan = planService.findById(Integer.parseInt(planId));
				//查询原计划的子计划
				List<Plan> subPlans = planService.findSubPlans(Integer.parseInt(planId));
				if(subPlans != null && subPlans.size() > 0){
					for(Plan subPlan : subPlans){
						Integer subPlanId = subPlan.getPlanId();
						List<Plan> subSubPlans = planService.findSubPlans(subPlanId);
						if(subSubPlans != null && subSubPlans.size() > 0){
							for(Plan subSubPlan : subSubPlans){
								Integer subSubPlanId = subSubPlan.getPlanId();
								List<Plan> subSubSubPlan = planService.findSubPlans(subSubPlanId);
								if(subSubSubPlan != null && subSubSubPlan.size() > 0){
									subSubPlan.setSubPlans(subSubSubPlan);
								}
							}
							subPlan.setSubPlans(subSubPlans);
						}
					}
					orginPlan.setSubPlans(subPlans);
				}
				model.addAttribute("plan", orginPlan);
			}/*else if("action_plan".equals(type)){
				//查询计划执行表
				ActionPlan mainActionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
				List<ActionPlan> subActionPlans = actionPlanService.findSubActionPlan(mainActionPlan.getActionPlanId());
				if(subActionPlans != null && subActionPlans.size() > 0){
					for(ActionPlan subActionPlan : subActionPlans){
						Integer subActionPlanId = subActionPlan.getActionPlanId();
						List<ActionPlan> subSubActionPlans = actionPlanService.findSubActionPlan(subActionPlanId);
						if(subSubActionPlans != null && subSubActionPlans.size() > 0){
							for(ActionPlan subSubActionPlan :subSubActionPlans){
								Integer subSubActionPlanId = subSubActionPlan.getActionPlanId();
								List<ActionPlan> subSubSubActionPlans = actionPlanService.findSubActionPlan(subSubActionPlanId);
								if(subSubSubActionPlans != null && subSubSubActionPlans.size() > 0){
									subSubActionPlan.setSubPlanList(subSubSubActionPlans);
								}
							}
							subActionPlan.setSubPlanList(subSubActionPlans);
						}
					}
					mainActionPlan.setSubPlanList(subActionPlans);
				}
				model.addAttribute("actionplan", mainActionPlan);
			}*/
		}
		model.addAttribute("plan", new Plan());
		model.addAttribute("actionplan", new ActionPlan());
		model.addAttribute("type", type);
		
		return "";
	}
	
	
	
	//子计划详细页面(完全废弃)
	/**
	 * 子计划详细页面跳转
	 * @param model
	 * @param planId
	 * @return
	 */
	/*public String getSubPlan(Model model,String planId){
		
		//查询当前选择的子计划的详细信息
		ActionPlan subActionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
		//将选中子计划查询出完整列表
		ActionPlan mainActionPlan = new ActionPlan();
		//先查询最高级的子计划
		if(subActionPlan.getActionParentId() != null){
			//选择这条子计划还有父计划
			ActionPlan parentActionPlan = actionPlanService.findActionPlanByPlanId(subActionPlan.getActionParentId());
			if(parentActionPlan != null){
				//子计划还有父计划
				ActionPlan parentParentActionPlan = actionPlanService.findActionPlanByPlanId(parentActionPlan.getActionParentId());
				if(parentParentActionPlan != null){
					//子计划还有父计划(默认这一层没有父计划了)
				}else{
					//没有父计划将从现在这个节点往下查
					mainActionPlan = parentParentActionPlan;
				}
			}else{
				//没有父计划，将从现在这个节点往下查
				mainActionPlan = parentActionPlan;
			}
		}else{
			//没有父计划，将从现在这个节点往下查
			mainActionPlan = subActionPlan;
		}
		
		//取出父计划后想下查询所有的子计划
		Integer mainActionPlanId = mainActionPlan.getActionPlanId();
		List<ActionPlan> subActionPlans = actionPlanService.findSubActionPlan(mainActionPlanId);
		if(subActionPlans != null && subActionPlans.size() > 0){
			for(ActionPlan ActionPlan : subActionPlans){
				List<ActionPlan> subSubActionPlans = actionPlanService.findSubActionPlan(ActionPlan.getActionPlanId());
				if(subSubActionPlans != null && subSubActionPlans.size() > 0){
					for(ActionPlan subSubActionPlan : subSubActionPlans){
						List<ActionPlan> subSubSubActionPlans = actionPlanService.findSubActionPlan(subSubActionPlan.getActionPlanId());
						if(subSubSubActionPlans != null && subSubSubActionPlans.size() > 0){
							subSubActionPlan.setSubPlanList(subSubSubActionPlans);
						}
					}
					ActionPlan.setSubPlanList(subSubActionPlans);
				}
			}
			mainActionPlan.setSubPlanList(subActionPlans);
		}
		
		model.addAttribute("subActionPlan", subActionPlan);
		model.addAttribute("mainActionPlan", mainActionPlan);
		
		//查询相关日志
		Log log = new Log();
		log.setActionPlanId(subActionPlan.getActionPlanId());
		log.setCreatePerson(subActionPlan.getBlamePerson());
		List<Log> logs = logSerivce.findLogByPlanIdAndCreatePerson(log);
		
		model.addAttribute("logs", logs);
		
		return "";
	}*/
	
	
	//跳转到设置关注节点(除开已完成之后的状态以及日常类计划)
	/**
	 * 跳转到设置关注节点
	 * @param model
	 * @return
	 */
	@GetMapping("toSetFollowNodes")
	public String toSetFollowNodes(Model model,String planName){
		
		//转换模糊查询
		String followPlanName = planName;
		if(followPlanName != null){
			followPlanName = "%"+followPlanName+"%";
		}
		
		//先获取当前用户所在的机构(office)
		// 取得当前登录人名称
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		//取得全部人员表
		List<User> UList = (List<User>) CacheManager.getCacheInfo("allUser").getValue();
		
		Map<String,User> userMap = new HashMap<String,User>();
		for(User users : UList){
			userMap.put(users.getUserId(), users);
		}
		
		//找到当前登录用户的office岗位
		User nowUser = userMap.get(user.getUserId());
		Office userOffice = new Office();
		if(nowUser != null){
			userOffice = nowUser.getOffice();
		}
		
		String officeName = userOffice.getName();
		officeName = officeName.substring(0, officeName.length()-1);
		
		List<Plan> mainPlans = new ArrayList<>();
		Map<String,Object> criteria = new HashMap<String, Object>();
		criteria.put("planName", followPlanName);
		criteria.put("blameSection", "%"+officeName+"%");
		//先查询所有原计划(最高权限，董事长级)
		mainPlans = planService.findMainPlans(criteria);
		for(Plan mainPlan : mainPlans){
			if(mainPlan.getStatus() != 5 && mainPlan.getPlanType() != 2){
				if(officeName.indexOf(mainPlan.getBlameSection())!=-1){
					List<Plan> subPlans = planService.findSubPlans(mainPlan.getPlanId());
					if(subPlans != null && subPlans.size() > 0){
						for(Plan subPlan : subPlans){
							if(subPlan.getStatus() != 5 && subPlan.getPlanType() != 2){
								List<Plan> subSubPlans = planService.findSubPlans(subPlan.getPlanId());
								if(subSubPlans != null && subSubPlans.size() > 0){
									for(Plan subSubPlan : subSubPlans){
										if(subPlan.getStatus() != 5 && subPlan.getPlanType() != 2){
											List<Plan> subSubSubPlans = planService.findSubPlans(subSubPlan.getPlanId());
											if(subSubSubPlans != null && subSubSubPlans.size() > 0){
												subSubPlan.setSubPlans(subSubSubPlans);
											}
										}
									}
									subPlan.setSubPlans(subSubPlans);
								}
							}
						}
						mainPlan.setSubPlans(subPlans);
					}
				}
			}
		}
		
		model.addAttribute("mainPlans", mainPlans);
		
		//查询执行计划
		/*List<ActionPlan> mainActionPlans = actionPlanService.findMainActionPlan();
		for(ActionPlan mainActionPlan : mainActionPlans){
			if(mainActionPlan.getState() != 5 && mainActionPlan.getActionPlanType() != 2){
				List<ActionPlan> subActionPlans = actionPlanService.findSubActionPlan(mainActionPlan.getActionPlanId());
				if(subActionPlans != null && subActionPlans.size() > 0){
					for(ActionPlan subActionPlan : subActionPlans){
						List<ActionPlan> subSubActionPlans =  actionPlanService.findSubActionPlan(subActionPlan.getActionPlanId());
						if(subSubActionPlans != null && subSubActionPlans.size() > 0){
							for(ActionPlan subSubSubActionPlan : subSubActionPlans){
								List<ActionPlan> subSubSubActionPlans = actionPlanService.findSubActionPlan(subSubSubActionPlan.getActionPlanId());
								if(subSubSubActionPlans != null && subSubSubActionPlans.size() >0){
									subSubSubActionPlan.setSubPlanList(subSubSubActionPlans);
								}
							}
							subActionPlan.setSubPlanList(subSubActionPlans);
						}
					}
					mainActionPlan.setSubPlanList(subActionPlans);
				}
			}
		}
		
		model.addAttribute("mainActionPlans", mainActionPlans);*/
		
		return "pages/followNodes/setfollownode";
	}
	
	
	
	//设置关注节点
	/**
	 * 设置关注节点
	 * @param model
	 * @param planIds
	 * @return
	 */
	@RequestMapping("setFollowNodes")
	public String setFollowNodes(Model model,String planIds,String actionflag){
		
		//分解planIds里的planId
		String[] selectPlanIds = planIds.split(",");
		//将数组转换为集合
		List<String> selectPlanIdList = java.util.Arrays.asList(selectPlanIds);
		
		// 取得当前登录人名称
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		
		//判别id是原计划还是执行计划
		if(selectPlanIdList != null && selectPlanIdList.size() > 0){
			for(String planId : selectPlanIdList){
				if(!"".equals(planId)){
					Plan plan = planService.findById(Integer.parseInt(planId));
					if(plan != null){
						//去掉已经加入了的
						FollowNode fns = new FollowNode();
						fns.setPlanId(plan.getPlanId());
						fns.setFollowPerson(user.getName());
						if(followNodeService.findFollowNode(fns) != null){
							continue;
						}
						
						//是原计划
						FollowNode followNode = new FollowNode();
						followNode.setPlanId(plan.getPlanId());
						followNode.setPlanName(plan.getPlanName());
						if(user != null){
							followNode.setFollowPerson(user.getName());
						}
						followNode.setStartTime(plan.getStartTime());
						followNode.setEndTime(plan.getEndTime());
						followNodeService.saveFollowNode(followNode);
					}
				}/*else{
					//是执行计划
					ActionPlan actionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
					FollowNode followNode = new FollowNode();
					followNode.setActionPlanId(actionPlan.getActionPlanId());
					followNode.setPlanName(actionPlan.getActionPlanName());
					followNode.setFollowPerson(actionPlan.getBlamePerson());
					followNode.setStartTime(actionPlan.getStartTime());
					followNode.setEndTime(actionPlan.getEndTime());
					followNodeService.saveFollowNode(followNode);
				}*/
			}
		}
		
		return "redirect:focus_plan_0_0_0?planType=1&actionflag="+actionflag;
		
	}
	
}
