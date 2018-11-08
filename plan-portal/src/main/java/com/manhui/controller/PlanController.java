package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.OrganizationSupervisor;
import com.manhui.model.Plan;
import com.manhui.model.PlanAssess;
import com.manhui.model.PlanTemplate;
import com.manhui.model.PlanTemplateModel;
import com.manhui.model.TemplateType;
import com.manhui.model.User;
import com.manhui.service.OrganizationSupervisorService;
import com.manhui.service.PlanAssessService;
import com.manhui.service.PlanService;
import com.manhui.service.PlanTemplateService;
import com.manhui.service.TemplateTypeService;
import com.manhui.util.DateUtil;
import com.manhui.util.SendRequest;

/**
 * @ClassName: PlanController
 * @description: 计划管理控制器
 * @author:	HeJiayan
 * @date Create in 上午11:22:25 2017年9月30日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanController {
	
	@Autowired
    private PlanService planService;
	@Autowired
	private PlanTemplateService planTemplateService;
	@Autowired
	private TemplateTypeService templateTypeService;
	@Autowired
	private OrganizationSupervisorService osService;
	@Autowired
	private PlanAssessService planAssessService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * @description: 选择新增方式
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/add_plans")
	public String addPlans(){
		return "pages/planCompile/choose_add_manner";
	}
	
	/**
	 * @description: 选择计划类型
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/choose_plan_type")
	public String choosePlanType(){
		return "pages/planCompile/choose_plan_type";
	}
	
	/**
	 * @description: 查询计划模板
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/choose_plan_template_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView choosePlanTemplate(PlanTemplate pt, @PathVariable Integer pageCurrent, @PathVariable Integer pageSize,
			@PathVariable Integer pageCount){
		ModelAndView mv = new ModelAndView("pages/planCompile/choose_plan_template");
		
		//转换模糊查询
		String templateName = pt.getTemplateName();
		if(templateName != null){
			templateName = "%"+templateName+"%";
			pt.setTemplateName(templateName);
		}
		//判断
		if(pageSize == 0) pageSize = 8;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planTemplateService.count(pt);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		pt.setStart((pageCurrent - 1)*pageSize);
		pt.setEnd(pageSize);
		List<PlanTemplate> planTemplateList = planTemplateService.findAllPlanTemplate(pt);
		
		//查询模板分类
		TemplateType templateType = new TemplateType();
		templateType.setStart(0);
		templateType.setEnd(100);
		List<TemplateType> types = templateTypeService.findAllTemplateType(templateType);
		for (PlanTemplate planTemplate : planTemplateList) {
			for (TemplateType type : types) {
				if(planTemplate.getTypeId() == type.getTtId())
					planTemplate.setTypeName(type.getTypeName());
			}
		}
		
		mv.addObject("types", types);
		mv.addObject("planTemplateList",planTemplateList);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 新增计划页面跳转
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/create_plan")
	public ModelAndView createPlan(String planType){
		ModelAndView mv = new ModelAndView("pages/planCompile/create_plan");
		Plan plan = new Plan();
		plan.setPlanType(Integer.parseInt(planType));
		mv.addObject("plan", plan);
		return mv;
	}
	
	/**
	 
	 * @description: 保存计划
	 * @param  request
	 * @param  plan
	 * @param  planId
	 * @param  flag
	 * @return 
	 * @throws ParseException 
	 */
	@RequestMapping("/save_plan")
	public ModelAndView savePlan(HttpServletRequest request, Plan plan, PlanAssess planAssess, PlanTemplateModel planTemplateModel, String planId, String flag, 
			String planStartTime, String editFlag) throws ParseException {
		ModelAndView mv = new ModelAndView();
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		OrganizationSupervisor os = osService.selectByPrimaryKey(user.getCompanyId());
		
		if(planId != null && !"".equals(planId)){
			if(planStartTime != null && !"".equals(planStartTime)){
				plan.setStartTime(planStartTime);
			}
			//计算工期
			String startTime = plan.getStartTime();
			String endTime = plan.getEndTime();
			if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
				Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
				plan.setTimeLimit(timeLimit);
			}
			plan.setSupervisor(os.getSupervisor());
			plan.setSupervisorId(os.getSupervisorId());
			plan.setSupervisorCompany(os.getSupervisorCompany());
			plan.setSupervisorOffice(os.getSupervisorOffice());
			planService.updatePlan(plan);
			//保存计划考核信息
			saveAndUpdatePlanAssess(planAssess,planTemplateModel,plan.getPlanId());
			
			//判断是否新增子计划，如果是则跳转至子计划新增页面(之前没有子计划)
			if("addSub".equals(flag)){
				mv.setViewName("pages/planCompile/add_subPlan");
				Plan subPlan = new Plan();
				subPlan.setParentId(Integer.parseInt(planId));
				subPlan.setPlanType(plan.getPlanType());
				
				if(editFlag != null && !"".equals(editFlag))
					mv.addObject("editFlag", editFlag);
				mv.addObject("subPlan", subPlan);
				return mv;
			}
		} else {
			Date createTime = new Date();
			plan.setCreateTime(createTime);
			if(user != null){
				plan.setCreatePerson(user.getUserName());
			}
			if(planStartTime != null && !"".equals(planStartTime)){
				plan.setStartTime(planStartTime);
			}
			//计算工期
			String startTime = plan.getStartTime();
			String endTime = plan.getEndTime();
			if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
				Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
				plan.setTimeLimit(timeLimit);
			}
			
			plan.setSupervisor(os.getSupervisor());
			plan.setSupervisorId(os.getSupervisorId());
			plan.setSupervisorCompany(os.getSupervisorCompany());
			plan.setSupervisorOffice(os.getSupervisorOffice());
			planService.savePlan(plan);
			plan = planService.findByPlan(plan);
			//保存计划考核信息
			saveAndUpdatePlanAssess(planAssess,planTemplateModel,plan.getPlanId());
			
			//判断是否新增子计划，如果是则跳转至子计划新增页面(之前没有子计划)
			if("addSub".equals(flag)){
				mv.setViewName("pages/planCompile/add_subPlan");
				plan = planService.findByPlan(plan);
				Plan subPlan = new Plan();
				subPlan.setParentId(plan.getPlanId());
				subPlan.setPlanType(plan.getPlanType());
				
				mv.addObject("subPlan", subPlan);
				return mv;
			}
		}
		
		String success = "保存草稿成功！";
		//判断计划操作
		if("release".equals(flag)){
			//发布任务
			plan.setStatus(100);
			plan.setDecomStatus(2);
			planService.updatePlan(plan);
			List<Plan> subList = planService.findSubPlans(plan.getPlanId());
			for (Plan p : subList) {
				p.setDecomStatus(3);
				planService.updatePlan(p);
			}
			success = "发布成功！";
		}else if("save".equals(flag)){
			//保存草稿
			plan.setStatus(0);
			planService.updatePlan(plan);
		}else if("submit".equals(flag)){
			//提交审批
			plan.setStatus(2);
			planService.updatePlan(plan);
			success = "提交审批成功！";
		}
		
		CacheManager.clearOnly("importMap");
		
		mv.addObject("success", success);
		if(editFlag != null && !"".equals(editFlag)){
			mv.setViewName("pages/draftBox/draft_box_list");
	    	Plan paramPlan = new Plan();
	    	paramPlan.setCreatePerson(user.getUserName());
	    	paramPlan.setPlanType(plan.getPlanType());
			//分页判断
			int pageSize = 10;
			int pageCurrent = 1;
			int rows = planService.getDraftsCount(paramPlan);
			int pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
			paramPlan.setStart((pageCurrent - 1)*pageSize);
			paramPlan.setEnd(pageSize);
			List<Plan> drafts = planService.findDrafts(paramPlan);
	    	
			if(drafts != null && drafts.size() > 0)
				mv.addObject("drafts", drafts);
			mv.addObject("planType", plan.getPlanType());
			mv.addObject("pageCurrent",pageCurrent);
			mv.addObject("pageSize",pageSize);
			mv.addObject("pageCount",pageCount);
			mv.addObject("rows",rows);
			mv.addObject("flag", success);
		}else{
			mv.setViewName("pages/planCompile/choose_add_manner");
		}
		return mv;
	}
	
	/**
	 * @description: 保存子计划
	 * @param  request
	 * @param  subPlan
	 * @param  planId
	 * @param  session
	 * @param 
	 * @throws ParseException 
	 */
	@RequestMapping("/save_subPlan")
	public ModelAndView saveSubPlan(HttpServletRequest request, Plan subPlan, PlanAssess planAssess, PlanTemplateModel planTemplateModel, String planId, String currentHandlePlanId, 
			String planLocation, String penaltyAmount_day, String editFlag, HttpSession session) throws ParseException{
		ModelAndView mv = new ModelAndView("pages/planCompile/create_plan");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		
		if(subPlan.getPlanType() == 2){
			if(subPlan.getValidateMode() != null && !"".equals(subPlan.getValidateMode())){
				if(subPlan.getExtractItem() != null && !"".equals(subPlan.getExtractItem()))
					subPlan.setValidateMode("1,2");
			}else{
				if(subPlan.getExtractItem() != null && !"".equals(subPlan.getExtractItem()))
					subPlan.setValidateMode("2");
			}
		}
		if(planId != null && !"".equals(planId)){
			if(subPlan.getPlanType() != 2){
				//计算工期
				String startTime = subPlan.getStartTime();
				String endTime = subPlan.getEndTime();
				if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
					Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
					subPlan.setTimeLimit(timeLimit);
				}
			}
			planService.updatePlan(subPlan);
			//保存计划考核信息
			saveAndUpdatePlanAssess(planAssess,planTemplateModel,subPlan.getPlanId());
		} else {
			Date createTime = new Date();
			subPlan.setCreateTime(createTime);
			if(user != null){
				subPlan.setCreatePerson(user.getUserName());
			}
			List<Plan> subPlans = planService.findSubPlans(subPlan.getParentId());
			
			//根据添加位置设置新增子计划的 planOrder
			if("sub".equals(planLocation) || "".equals(planLocation)){
				subPlan.setPlanOrder(subPlans.size() + 1);
			} else {
				Plan currentPlan = planService.findById(Integer.parseInt(currentHandlePlanId));
				Map<String,Object> criteria = new HashMap<String, Object>();
				if("before".equals(planLocation)){
					subPlan.setPlanOrder(currentPlan.getPlanOrder());
					criteria.put("planOrder", currentPlan.getPlanOrder());
					criteria.put("parentId", currentPlan.getParentId());
					List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
					if(plans.size() > 0)
						for (Plan plan : plans) {
							plan.setPlanOrder(plan.getPlanOrder() + 1);
							planService.updatePlan(plan);
						}
				}
				if("after".equals(planLocation)){
					subPlan.setPlanOrder(currentPlan.getPlanOrder() + 1);
					criteria.put("planOrder", currentPlan.getPlanOrder() + 1);
					criteria.put("parentId", currentPlan.getParentId());
					List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
					if(plans.size() > 0)
						for (Plan plan : plans) {
							plan.setPlanOrder(plan.getPlanOrder() + 1);
							planService.updatePlan(plan);
						}
				}
			}
			
			if(subPlan.getPlanType() != 2){
				//计算工期
				String startTime = subPlan.getStartTime();
				String endTime = subPlan.getEndTime();
				if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
					Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
					subPlan.setTimeLimit(timeLimit);
				}
			}
			
			planService.savePlan(subPlan);
			subPlan = planService.findByPlan(subPlan);
			//保存计划考核信息
			saveAndUpdatePlanAssess(planAssess,planTemplateModel,subPlan.getPlanId());
		}
		
		Plan plan = getAllPlans(subPlan.getParentId());
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("plan", plan);
		return mv;
	}
	
	/**
	 * @description: 选择子计划新增位置（之前已有子计划）
	 * @param  request
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/chose_add_location")
	public ModelAndView choseAddLocation(HttpServletRequest request, String planId, String editFlag, String subFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/chose_add_location");
		Plan plan = planService.findById(Integer.parseInt(planId));
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		if(subFlag != null && !"".equals(subFlag))
			mv.addObject("subFlag", subFlag);
		mv.addObject("plan", plan);
		return mv;
	}
	
	/**
	 * @description: 添加子计划（之前已有其他子计划）
	 * @param  request
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/add_subPlan")
	public ModelAndView addSubPlan(HttpServletRequest request, String planId, String planLocation, String editFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/add_subPlan");
		Plan plan = planService.findById(Integer.parseInt(planId));
		Plan subPlan = new Plan();
		subPlan.setPlanType(plan.getPlanType());
		if("sub".equals(planLocation)){
			subPlan.setParentId(plan.getPlanId());
		} else {
			subPlan.setParentId(plan.getParentId());
		}
			
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("subPlan", subPlan);
		mv.addObject("planLocation", planLocation);
		mv.addObject("currentHandlePlanId", plan.getPlanId());
		mv.addObject("currentHandlePlanName", plan.getPlanName());
		Plan topPlan = getTopPlan(plan.getPlanId());
		mv.addObject("loopMode", topPlan.getLoopMode());
		mv.addObject("loopRate", topPlan.getLoopRate());
		return mv;
	}
	
	/**
	 * @description: 取消新增计划
	 * @param  request
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_add")
	public String cancelAdd(HttpServletRequest request, String planId, String channel){
		if(planId != null && !"".equals(planId) && planId != "null" && !"null".equals(planId)){
			Plan plan = planService.findById(Integer.parseInt(planId));
			List<Plan> plans = planService.findSubPlans(plan.getPlanId());
			if(plans.size() > 0){
				for (Plan p : plans) {
					List<Plan> subPlans = planService.findSubPlans(p.getPlanId());
					if(subPlans.size() > 0){
						for (Plan pp : subPlans) {
							List<Plan> subSubPlans = planService.findSubPlans(pp.getPlanId());
							if(subSubPlans.size() > 0){
								for (Plan ppp : subSubPlans) {
									List<Plan> subSubSubPlans = planService.findSubPlans(ppp.getPlanId());
									if(subSubSubPlans.size() > 0) {
										for (Plan pppp : subSubSubPlans) {
											planService.deletePlanById(pppp.getPlanId());
											planAssessService.deleteByPlanId(pppp.getPlanId());
										}
									}
									planService.deletePlanById(ppp.getPlanId());
									planAssessService.deleteByPlanId(ppp.getPlanId());
								}
							}
							planService.deletePlanById(pp.getPlanId());
							planAssessService.deleteByPlanId(pp.getPlanId());
						}
					}
					planService.deletePlanById(p.getPlanId());
					planAssessService.deleteByPlanId(p.getPlanId());
				}
			}
			planService.deletePlanById(plan.getPlanId());
			planAssessService.deleteByPlanId(plan.getPlanId());
			
			CacheManager.clearOnly("importMap");
		}
		
		if("add_plan".equals(channel)){
			return "redirect:toFindTemplate_0_0_0";
		}
		return "redirect:add_plans";
	}
	
	/**
	 * @description: 取消添加子计划
	 * @param  request
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_addSub")
	public ModelAndView cancelAddSub(HttpServletRequest request, String parentId, String editFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/create_plan");
		Plan plan = getAllPlans(Integer.parseInt(parentId));
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("plan", plan);
		return mv;
	}
	
	/**
	 * @description: 编辑子计划页面跳转
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@RequestMapping("/edit_plan")
	public ModelAndView editPlan(HttpServletRequest request, String planId, String editFlag){
		ModelAndView mv = new ModelAndView();
		Plan plan = planService.findById(Integer.parseInt(planId));
		if(plan.getParentId() != null && !"".equals(plan.getParentId())){
			Plan subPlan = plan;
			List<PlanAssess> pass = planAssessService.selectByPlanId(subPlan.getPlanId());
			if(pass.size() > 0){
				subPlan.setPass(pass);
				if(pass.size() == 1 && pass.get(0).getCheckItem() == 1){
					subPlan.setCheckItem("1");
				}else if(pass.size() == 1 && pass.get(0).getCheckItem() == 2){
					subPlan.setCheckItem("2");
				}else if(pass.size() == 2){
					subPlan.setCheckItem("1,2");
				}
			}else{
				subPlan.setCheckItem(null);
			}
			Plan parentPlan = planService.findById(plan.getParentId());
			mv.addObject("subPlan", subPlan);
			mv.addObject("parentPlanName", parentPlan.getPlanName());
			mv.addObject("loopMode", parentPlan.getLoopMode());
			mv.addObject("loopRate", parentPlan.getLoopRate());
			if(editFlag != null && !"".equals(editFlag))
				mv.addObject("editFlag", editFlag);
			mv.setViewName("pages/planCompile/add_subPlan");
		}else{
			plan = getAllPlans(plan.getPlanId());
			mv.addObject("plan", plan);
			if(editFlag != null && !"".equals(editFlag))
				mv.addObject("editFlag", editFlag);
			mv.setViewName("pages/planCompile/create_plan");
		}
		return mv;
	}
	
	/**
	 * @description: 修改计划时间
	 * @param request
	 * @param planId
	 * @param startTime
	 * @param startTimeDaily
	 * @param endTime
	 * @param endTimeDaily
	 * @return 
	 * @throws ParseException
	 */
	@RequestMapping("/update_time")
	public ModelAndView updateTime(HttpServletRequest request, String planId, String editFlag,
			String startTime, String startTimeDaily, String endTime, String endTimeDaily) throws ParseException{
		ModelAndView mv = new ModelAndView("pages/planCompile/create_plan");
		String flag = "修改时间成功！";
		Plan plan = planService.findById(Integer.parseInt(planId));
		Plan upPlan = new Plan();
		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)
				&& planId != null && !"".equals(planId)){
			upPlan.setPlanId(Integer.parseInt(planId));
			upPlan.setStartTime(startTime);
			upPlan.setEndTime(endTime);
			if(plan.getPlanType() == 2){
				if(startTimeDaily != null && !"".equals(startTimeDaily)
						&& endTimeDaily != null && !"".equals(endTimeDaily)){
					upPlan.setStartTimeDaily(startTimeDaily);
					upPlan.setEndTimeDaily(endTimeDaily);
				}else{
					flag = "修改时间失败！";
				}
			}
			
			if(plan.getPlanType() != 2){
				//计算工期
				if(upPlan.getStartTime() != null && !"".equals(upPlan.getStartTime()) 
						&& upPlan.getEndTime() != null && !"".equals(upPlan.getEndTime())){
					Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
					if(timeLimit > 0){
						upPlan.setTimeLimit(timeLimit);
					}else{
						flag = "修改时间失败！请确认开始时间是否早于结束时间。";
					}
				}
			}
			planService.updatePlan(upPlan);
		}else{
			flag = "修改时间失败！";
		}
		
		Plan newPlan = getAllPlans(Integer.parseInt(planId));
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("plan", newPlan);
		mv.addObject("flag", flag);
		return mv;
	}
	
	/**
	 * @description: 删除计划
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@RequestMapping("/delete_subPlan")
	public ModelAndView geleteSubPlan(HttpServletRequest request, String planId, String editFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/create_plan");
		//查询下属还有子计划，如果有则提醒不能删除
		Plan subPlan = planService.findById(Integer.parseInt(planId));
		List<Plan> subPlanList = planService.findSubPlans(subPlan.getPlanId());
		String flag = "删除成功！";
		if(subPlanList != null && subPlanList.size()>0){
			flag = "该计划存在子计划，删除失败！";
		}else {
			planService.deletePlanById(Integer.parseInt(planId));
			planAssessService.deleteByPlanId(Integer.parseInt(planId));
		}
		
		Plan plan = getAllPlans(subPlan.getParentId());
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("flag", flag);
		mv.addObject("plan", plan);
		return mv;
	}
	
	/**
	 * @description: 导入子计划时先进行主计划保存
	 * @param @param request
	 * @param @param plan
	 * @return 
	 * @throws ParseException 
	 */
	@RequestMapping("/save_mainPlan")
	@ResponseBody
	public Object saveMainPlan(HttpServletRequest request, Plan plan, String planId, String planStartTime, String editFlag, HttpSession session) throws ParseException{
		User user = (User) session.getAttribute("user");
		if(planId != null && !"".equals(planId)){
			if(planStartTime != null && !"".equals(planStartTime)){
				plan.setStartTime(planStartTime);
			}
			//计算工期
			String startTime = plan.getStartTime();
			String endTime = plan.getEndTime();
			if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
				Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
				plan.setTimeLimit(timeLimit);
			}
			planService.updatePlan(plan);
		} else {
			Date createTime = new Date();
			plan.setCreateTime(createTime);
			if(user != null){
				plan.setCreatePerson(user.getUserName());
			}
			if(planStartTime != null && !"".equals(planStartTime)){
				plan.setStartTime(planStartTime);
			}
			//计算工期
			String startTime = plan.getStartTime();
			String endTime = plan.getEndTime();
			if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
				Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
				plan.setTimeLimit(timeLimit);
			}
			planService.savePlan(plan);
				
			//获取当前新增计划的主键
			plan = planService.findByPlan(plan);
			planId = String.valueOf(plan.getPlanId());
		}
		if(editFlag != null && !"".equals(editFlag)){
			return planId+"-"+editFlag;
		}else{
			return planId;
		}
	}
	
	/**
	 * @description: 选择需要上传子计划的文档
	 * @param @param request
	 * @param @param planType
	 * @param @param loopMode
	 * @return 
	 * @throws
	 */
	@RequestMapping("/chose_file")
	public ModelAndView choseFile(HttpServletRequest request, String planId, String editFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/chose_file");
		if(planId != null && !"".equals(planId)){
			Plan plan = planService.findById(Integer.parseInt(planId));
			mv.addObject("planId", plan.getPlanId());
		}
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		return mv;
	}
	
	/**
	 * @description: 导入模板计划后重新加载数据
	 * @param request
	 * @param planId
	 * @param session
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/reload_data")
	public ModelAndView reloadData(HttpServletRequest request, String planId, String editFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/create_plan");
		Plan plan = new Plan();
		//错误提示
		String errorCue = null;
		
		//查询出导入的所有计划，返回页面
		if(planId != null && !"".equals(planId)){
			plan = getAllPlans(Integer.parseInt(planId));
		}
		
		//设置提示信息
		Map<String, Object> importMap = (Map<String, Object>) CacheManager.getCacheInfo("importMap").getValue();
		if(importMap != null){
			Integer failure = (Integer) importMap.get("failure");
			if(failure != 0 && !"".equals(failure)){
				errorCue = "文档总共导入"+importMap.get("total")+"条子计划信息，其中"+importMap.get("success")+"条导入成功，"+importMap.get("failure")+"条未成功，";
			}else{
				errorCue = "文档总共导入"+importMap.get("total")+"条子计划信息，其中"+importMap.get("success")+"条导入成功，"+importMap.get("failure")+"条未成功";
			}
			mv.addObject("failure", failure);
		}else{
			errorCue = "导入失败，请重新导入！";
		}
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("plan", plan);
		mv.addObject("errorCue", errorCue);
		return mv;
	}
	
	/**
	 * @description: 查看导入计划的错误信息页面跳转
	 * @param request
	 * @param planId
	 * @param session
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/check_errorDetail")
	public ModelAndView checkErrorDetail(HttpServletRequest request, String planId, String editFlag){
		ModelAndView mv = new ModelAndView("pages/planCompile/error_detail");
		Map<String, Object> importMap = (Map<String, Object>) CacheManager.getCacheInfo("importMap").getValue();
		List<Plan> importList = new ArrayList<Plan>();
		if(importMap.size() > 0 && importMap != null){
			List<Plan> planList = (List<Plan>) importMap.get("importList_"+planId);
			importList = planList;
		}
		
		if(importList.size() > 0){
			for (Plan plan : importList) {
				if(plan.getErrorMsg() != null && !"".equals(plan.getErrorMsg()) 
						&& !plan.getErrorMsg().contains("。") && !plan.getErrorMsg().contains("；<br/>")){
					String[] errors = plan.getErrorMsg().split(";");
					String errorMsg = "";
					for (int i = 1; i < errors.length; i++) {
						errorMsg += i + "、" + errors[i] + "；<br/>";
					}
					errorMsg = errorMsg.substring(0, errorMsg.lastIndexOf("；<br/>"));
					errorMsg = errorMsg + "。";
					plan.setErrorMsg(errorMsg);
				}
				
				String blamePerson = null;
				String number = null;
				String personAndNumber = plan.getBlamePerson();
				if(personAndNumber != null && !"".equals(personAndNumber)){
					if(personAndNumber.contains(",")){
						String[] arry = personAndNumber.split(",");
						blamePerson = arry[0];
						number = arry[1];
					}else{
						blamePerson = personAndNumber;
					}
				}
				plan.setBlamePerson(blamePerson);
				plan.setCustomer(number);
			}
		}
		
		Plan plan = planService.findById(Integer.parseInt(planId));
		
		if(editFlag != null && !"".equals(editFlag))
			mv.addObject("editFlag", editFlag);
		mv.addObject("importList", importList);
		mv.addObject("planId", planId);
		mv.addObject("planType", plan.getPlanType());
		mv.addObject("loopMode", plan.getLoopMode());
		return mv;
	}
	
	/**
	 * @description: 计划草稿箱页面跳转
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/draft_box")
	public ModelAndView draftBox(){
		ModelAndView mv = new ModelAndView("pages/draftBox/draft_box");
		return mv;
	}
	
	/**
	 * @description: 查询对应创建人的草稿计划
	 * @param plan
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/find_drafts_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView findDrafts(Plan plan, @PathVariable Integer pageCurrent, @PathVariable Integer pageSize,
			@PathVariable Integer pageCount, String flag){
		ModelAndView mv = new ModelAndView("pages/draftBox/draft_box_list");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
    	plan.setCreatePerson(user.getUserName());
    	
    	//转换模糊查询
		String planName = plan.getPlanName();
		if(planName != null && !"".equals(planName)){
			
			planName = "%"+planName+"%";
			plan.setPlanName(planName);
		}
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planService.getDraftsCount(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> drafts = planService.findDrafts(plan);
    	
		if(drafts != null && drafts.size() > 0)
			mv.addObject("drafts", drafts);
		if(flag != null && "delete".equals(flag))
			flag = "删除成功！";
		mv.addObject("flag", flag);
		mv.addObject("planType", plan.getPlanType());
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 删除草稿计划
	 * @param request
	 * @param planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/del_draft")
	public String delDraft(HttpServletRequest request, String planId){
		Plan plan = planService.findById(Integer.parseInt(planId));
		List<Plan> planList = planService.findSubPlans(plan.getPlanId());
		if(planList.size() > 0){
			for (Plan p : planList) {
				List<Plan> subPlans = planService.findSubPlans(p.getPlanId());
				if(subPlans.size() > 0){
					for (Plan pp : subPlans) {
						List<Plan> subSubPlans = planService.findSubPlans(pp.getPlanId());
						if(subSubPlans.size() > 0){
							for (Plan ppp : subSubPlans) {
								List<Plan> subSubSubPlans = planService.findSubPlans(ppp.getPlanId());
								if(subSubSubPlans.size() > 0){
									for (Plan pppp : subSubSubPlans) {
										planService.deletePlanById(pppp.getPlanId());
										planAssessService.deleteByPlanId(pppp.getPlanId());
									}
								}
								planService.deletePlanById(ppp.getPlanId());
								planAssessService.deleteByPlanId(ppp.getPlanId());
							}
						}
						planService.deletePlanById(pp.getPlanId());
						planAssessService.deleteByPlanId(pp.getPlanId());
					}
				}
				planService.deletePlanById(p.getPlanId());
				planAssessService.deleteByPlanId(p.getPlanId());
			}
		}
		planService.deletePlanById(Integer.parseInt(planId));
		planAssessService.deleteByPlanId(Integer.parseInt(planId));
		return "redirect:/find_drafts_0_10_0?flag=delete&&planType="+plan.getPlanType();
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
		List<PlanAssess> pass = planAssessService.selectByPlanId(plan.getPlanId());
		if(pass.size() > 0){
			plan.setPass(pass);
			if(pass.size() == 1 && pass.get(0).getCheckItem() == 1){
				plan.setCheckItem("1");
			}else if(pass.size() == 1 && pass.get(0).getCheckItem() == 2){
				plan.setCheckItem("2");
			}else if(pass.size() == 2){
				plan.setCheckItem("1,2");
			}
		}else{
			plan.setCheckItem(null);
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
	
	/**
	 * @param planTemplateModel 
	 * @description: 保存计划考核信息
	 * @param planAssess
	 * @param planId
	 * @return 
	 * @throws 
	 */
	private void saveAndUpdatePlanAssess(PlanAssess planAssess, PlanTemplateModel planTemplateModel, Integer planId) {
		planAssessService.deleteByPlanId(planId);
		if(planAssess.getCheckItem() != null && !"".equals(planAssess.getCheckItem())){
			planAssess.setPlanId(planId);
			planAssessService.insertPlanAssess(planAssess);
		}
		
		if(planTemplateModel.getCheckItems() != null && !"".equals(planTemplateModel.getCheckItems())){
			planAssess.setCheckItem(planTemplateModel.getCheckItems());
			planAssess.setCheckManner(planTemplateModel.getCheckManners());
			planAssess.setPlanId(planId);
			planAssess.setSingleAmount(planTemplateModel.getSingleAmounts());
			planAssess.setCumulativeDays(planTemplateModel.getCumulativeDayss());
			planAssess.setCumulativeAmount(planTemplateModel.getCumulativeAmounts());
			planAssessService.insertPlanAssess(planAssess);
		}
	}
}

