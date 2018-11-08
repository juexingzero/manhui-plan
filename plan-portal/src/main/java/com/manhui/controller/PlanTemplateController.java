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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Office;
import com.manhui.model.Plan;
import com.manhui.model.PlanAssess;
import com.manhui.model.PlanTemplate;
import com.manhui.model.PlanTemplateModel;
import com.manhui.model.TemplateType;
import com.manhui.model.User;
import com.manhui.service.PlanAssessService;
import com.manhui.service.PlanService;
import com.manhui.service.PlanTemplateService;
import com.manhui.service.TemplateTypeService;
import com.manhui.util.DateUtil;
import com.manhui.util.PlanResourcesUtil;

import net.minidev.json.JSONObject;

/**
 * @ClassName: PlanTemplateController
 * @Description: 计划模板库控制器 
 * @author jiangxiaosong
 * @date: 2017年9月29日14:20:02
 */
@Controller
public class PlanTemplateController {
	
	//引入计划模板映射
	@Autowired
	private PlanTemplateService planTemplateService;
	
	//引入模板分类类型
	@Autowired
	private TemplateTypeService templateTypeService;
	
	//引入计划分类类型
	@Autowired
	private PlanService planService;
	
	//引入考核类型
	@Autowired
	private PlanAssessService planAssessService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	 }
	
	
	/**
	 * 跳转到计划模板编制
	 * @param model
	 * @return
	 */
	@GetMapping("toPlanTemplate")
	public String toPlanTemplate(Model model){
		return "pages/planTemplate/change_template";
	}

	/**
	 * 查询模板库
	 * @param pt
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param model
	 * @return
	 */
	@GetMapping("/toFindTemplate_{pageCurrent}_{pageSize}_{pageCount}")
	public String toFindTemplate(Model model,PlanTemplate pt,@PathVariable Integer pageCurrent,
								@PathVariable Integer pageSize,@PathVariable Integer pageCount,String flag) {
		//转换模糊查询
		String templateName = pt.getTemplateName();
		if(templateName != null){
			templateName = "%"+templateName+"%";
			pt.setTemplateName(templateName);
		}
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planTemplateService.count(pt);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		pt.setStart((pageCurrent - 1)*pageSize);
		pt.setEnd(pageSize);
		List<PlanTemplate> planTemplateList = planTemplateService.findAllPlanTemplate(pt);
		// 查询模板分类类型
		TemplateType templateType = new TemplateType();
		templateType.setStart(0);
		templateType.setEnd(10);
		List<TemplateType> ttList = templateTypeService.findAllTemplateType(templateType);
		Map<Integer, String> ttmap = new HashMap<>();
		for (TemplateType tt : ttList) {
			ttmap.put(tt.getTtId(), tt.getTypeName());
		}

		for (PlanTemplate ptt : planTemplateList) {
			String pptName = ttmap.get(ptt.getTypeId());
			ptt.setTypeName(pptName);
		}

		for (PlanTemplate ptt : planTemplateList) {
			String pptName = "";
			if(ptt.getPlanType() == 1){
				pptName = "项目类工作计划";
			}else if(ptt.getPlanType() == 2){
				pptName = "日常类工作计划";
			}else if(ptt.getPlanType() == 3){
				pptName = "临时类工作计划";
			}
			ptt.setPlanTypeName(pptName);
		}
		//删除成功
		if(flag != null && "delete".equals(flag)){
			flag = "删除成功！";
		}else if(flag != null && "edit".equals(flag)){
			flag = "保存成功！";
		}
			
		//将下拉框内容传入页面
		model.addAttribute("planTemplateList",planTemplateList);
		model.addAttribute("ttList", ttList);
		model.addAttribute("rows", rows);
		model.addAttribute("pageCurrent", pageCurrent);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("pageCount", pageCount);
		model.addAttribute("flag",flag);
		return "pages/planTemplate/plantemplate";
	}

	/**
	 * 生成计划功能
	 * @param model
	 * @param pt
	 * @return
	 */
	@GetMapping("/createPlan")
	public String loginPost(Model model,PlanTemplate pt,String startTime) {
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		Date date = new Date();
		
		PlanTemplate planTemplate = planTemplateService.findPlanTemplateById(pt);
		model.addAttribute("pt",planTemplate);
		//TODO获取当前模板对应计划
		Plan plan = planService.findById (planTemplate.getPlanId());
		Integer parentPlan = plan.getPlanId();
		//生成新的计划
		int max = planService.findMaxPlanId();
		Integer newPlanId = max + 1;
		plan.setPlanId(newPlanId);
        //通过计划开始时间和工期计算结束时间
        if(startTime != null && !"".equals(startTime)){
            plan.setStartTime(startTime);
            plan = DateUtil.getPlanTime(plan,startTime);
        }
        //校验负责部门以及负责人是否有效
        plan = checkCharge(plan);
        plan.setStatus(null);
        plan.setDecomStatus(null);
        plan.setCreateTime(date);
        plan.setCreatePerson(user.getUserName());
		planService.savePlan(plan);
		
		//TODO查询子计划详细
		List<Plan> planList = planService.findSubPlans(parentPlan);
		
		//生成新的计划的子计划
		if(planList != null && planList.size() > 0){
			for(Plan plans : planList){
				plans.setParentId(newPlanId);
				Integer subId = plans.getPlanId();
				List<Plan> subPlan = planService.findSubPlans(subId);
				int subMax = planService.findMaxPlanId();
				plans.setPlanId(subMax+1);
	            //通过时间偏移量计算子计划开始时间和结束时间
	            if(startTime != null && !"".equals(startTime))
	                plans = DateUtil.getPlanTime(plans,startTime);
	            //校验负责部门以及负责人是否有效
	            plans = checkCharge(plans);
	            plans.setStatus(null);
	            plans.setDecomStatus(null);
	            plans.setCreateTime(date);
	            plans.setCreatePerson(user.getUserName());
				planService.savePlan(plans);
				
				if(subPlan != null && subPlan.size() > 0){
					for(Plan subPlans : subPlan){
						subPlans.setParentId(subMax+1);
						Integer subSubId = subPlans.getPlanId();
					 	List<Plan> subSubPlan = planService.findSubPlans(subSubId);
						int subSubMax = planService.findMaxPlanId();
						subPlans.setPlanId(subSubMax+1);
	                    //通过时间偏移量计算子计划开始时间和结束时间
	                    if(startTime != null && !"".equals(startTime))
	                        subPlans = DateUtil.getPlanTime(subPlans,startTime);
	                    //校验负责部门以及负责人是否有效
	                    subPlans = checkCharge(subPlans);
	                    subPlans.setStatus(null);
	                    subPlans.setDecomStatus(null);
	                    subPlans.setCreateTime(date);
	                    subPlans.setCreatePerson(user.getUserName());
						planService.savePlan(subPlans);
						
						if(subSubPlan != null && subSubPlan.size() > 0){
							for(Plan subSubPlans : subSubPlan){
								subSubPlans.setParentId(subSubMax+1);
								Integer subSubSubId = subSubPlans.getPlanId();
	                            List<Plan> subSubSubPlan = planService.findSubPlans(subSubSubId);
								int subSubSubMax = planService.findMaxPlanId();
								subSubPlans.setPlanId(subSubSubMax+1);
	                            //通过时间偏移量计算子计划开始时间和结束时间
	                            if(startTime != null && !"".equals(startTime))
	                                subSubPlans = DateUtil.getPlanTime(subSubPlans,startTime);
	                            //校验负责部门以及负责人是否有效
	                            subSubPlans = checkCharge(subSubPlans);
	                            subSubPlans.setStatus(null);
	                            subSubPlans.setDecomStatus(null);
	                            subSubPlans.setCreateTime(date);
	                            subSubPlans.setCreatePerson(user.getUserName());
								planService.savePlan(subSubPlans);
								
	                            if(subSubSubPlan != null && subSubSubPlan.size() > 0){
	                                for (Plan subSubSubPlans : subSubSubPlan) {
	                                    subSubSubPlans.setParentId(subSubSubMax+1);
	                                    int subSubSubSubMax = planService.findMaxPlanId();
	                                    subSubSubPlans.setPlanId(subSubSubSubMax+1);
	                                    //通过时间偏移量计算子计划开始时间和结束时间
	                                    if(startTime != null && !"".equals(startTime))
	                                        subSubSubPlans = DateUtil.getPlanTime(subSubSubPlans,startTime);
	                                    //校验负责部门以及负责人是否有效
	                                    subSubSubPlans = checkCharge(subSubSubPlans);
	                                    subSubSubPlans.setStatus(null);
	                                    subSubSubPlans.setDecomStatus(null);
	                                    subSubSubPlans.setCreateTime(date);
	                                    subSubSubPlans.setCreatePerson(user.getUserName());
	                                    planService.savePlan(subSubSubPlans);
	                                    subSubSubPlans.setSubPlans(planService.findSubPlans(subSubSubSubMax+1));
	                                }
	                                subSubPlans.setSubPlans(planService.findSubPlans(subSubMax+1));
	                            }
							}
							subPlans.setSubPlans(planService.findSubPlans(subSubMax+1));
						}
					}
					plans.setSubPlans(planService.findSubPlans(subMax+1));
				}
			}
			plan.setSubPlans(planList);
		}
		String channel = "";
		if(startTime != null && !"".equals(startTime)){
			//模板新增渠道
			channel = "add_model";
		}else{
			channel = "add_plan";
		}
		model.addAttribute("plan",plan);
		model.addAttribute("channel", channel);
		return "pages/planCompile/create_plan";
	}

	/**
	 * @description: 校验负责部门和负责人是否有效
	 * @param plan
	 * @param 
	 * @return 
	 * @throws 
	 */
	@SuppressWarnings("unchecked")
	private Plan checkCharge(Plan plan) {
		List<User> UList = (List<User>) CacheManager.getCacheInfo("allUser").getValue();
		List<Office> officeList = (List<Office>) CacheManager.getCacheInfo("officeList").getValue();
		List<String> userNames = new ArrayList<String>();
		List<String> officeNames = new ArrayList<String>();
		for (User user : UList) {
			userNames.add(user.getName());
		}
		for (Office office : officeList) {
			officeNames.add(office.getName());
		}
		if(plan.getBlamePerson() != null && !"".equals(plan.getBlamePerson())){
			if(!userNames.contains(plan.getBlamePerson())){
				plan.setBlamePerson("历史负责人不存在或已离职，请重新指定！");
			}
		}
		if(plan.getBlameSection() != null && !"".equals(plan.getBlameSection())){
			String blameSection = plan.getBlameSection();
			if(blameSection.contains("-")){
				blameSection = blameSection.substring(blameSection.lastIndexOf("-"), blameSection.length());
				blameSection = blameSection.substring(1, blameSection.length());
			}
			if(!officeNames.contains(blameSection)){
				plan.setBlameSection("历史负责部门不存在，请重新指定！");
			}
		}
		return plan;
	}


	/**
	 * 查看详细画面
	 * @param model
	 * @param pt
	 * @return
	 */
	@GetMapping("/showDetail")
	public String register(Model model,PlanTemplate pt) {
		
		PlanTemplate planTemplate = planTemplateService.findPlanTemplateById(pt);
		// 查询模板分类类型
		TemplateType templateType = new TemplateType();
		templateType.setStart(0);
		templateType.setEnd(10);
		List<TemplateType> ttList = templateTypeService.findAllTemplateType(templateType);
		Map<Integer, String> ttmap = new HashMap<>();
		for (TemplateType tt : ttList) {
			ttmap.put(tt.getTtId(), tt.getTypeName());
		}

		String pptName = ttmap.get(planTemplate.getTypeId());
		planTemplate.setTypeName(pptName);

		//TODO模板ID查询计划详细
		Plan plan = planService.findById (planTemplate.getPlanId());
		if(plan != null){
			//TODO查询子计划详细
			Integer parentPlan = plan.getPlanId();
			List<Plan> subPlans = planService.findSubPlans(parentPlan);
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
			plan.setSubPlans(subPlans);
		}else{
			plan = new Plan();
		}
		List<PlanAssess> pa = planAssessService.selectByPlanId(plan.getPlanId());
		model.addAttribute("paList", pa);
		model.addAttribute("plan",plan);
		model.addAttribute("pt",planTemplate);
		return "pages/planTemplate/detailtemplate";
	}
	

	/**
	 * 编辑画面
	 * @param model
	 * @param pt
	 * @param erroFlag
	 * @return
	 */
	@GetMapping("/planEdit")
	public String toPlanEdit(Model model ,String templateId,String actionflag) {
		PlanTemplate pt = new PlanTemplate();
		pt.setTemplateId(Integer.parseInt(templateId));
		PlanTemplate planTemplate = planTemplateService.findPlanTemplateById(pt);
		// 查询模板分类类型
		TemplateType templateType = new TemplateType();
		templateType.setStart(0);
		templateType.setEnd(10);
		List<TemplateType> ttList = templateTypeService.findAllTemplateType(templateType);

		//TODO模板ID查询计划
		Plan plan = planService.findById (planTemplate.getPlanId());
		//TODO查询子计划详细
		Integer parentPlan = plan.getPlanId();
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		if(subPlans.size() > 0 && subPlans != null){
			for (Plan p : subPlans) {
				List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
				if(subSubPlans != null && subSubPlans.size() > 0){
					p.setSubPlans(subSubPlans);
					p.setSubSum(subSubPlans.size());
					for (Plan pp : subSubPlans) {
						List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
						if(subSubSubPlans != null && subSubSubPlans.size() > 0){
							pp.setSubPlans(subSubSubPlans);
							pp.setSubSum(subSubSubPlans.size());
							for(Plan ppp : subSubSubPlans){
								List<Plan> subSubSubSubPlans = planService.findSubPlans(ppp.getPlanId());
								if(subSubSubSubPlans != null && subSubSubSubPlans.size() > 0){
									ppp.setSubPlans(subSubSubSubPlans);
									ppp.setSubSum(subSubSubSubPlans.size());
								}
							}
						}
					}
				}
			}
			plan.setSubPlans(subPlans);
		}
		if("delete".equals(actionflag)){
			actionflag = "删除成功";
		}
		List<PlanAssess> pa = planAssessService.selectByPlanId(plan.getPlanId());
		model.addAttribute("actionflag", actionflag);
		model.addAttribute("plan",plan);
		model.addAttribute("pt",planTemplate);
		model.addAttribute("ttList",ttList);
		model.addAttribute("paList", pa);
		return "pages/planTemplate/edittemplate";
	}
	
	
	/**
	 * 跳转到子计划编辑页面
	 * @param model
	 * @param subId
	 * @param planLocation
	 * @return
	 */
	@GetMapping("/editSubPlan")
	public String editSubPlan(Model model,String subId,String templateId,String planLocation){
		
		//编辑模式，查询子计划信息并返回
		Plan subPlan = planService.findById(Integer.parseInt(subId));
		List<PlanAssess> palist = planAssessService.selectByPlanId(subPlan.getPlanId());
		model.addAttribute("flag", "edit");
		model.addAttribute("subPlan", subPlan);
		model.addAttribute("currentHandlePlan", subPlan.getPlanName());
		model.addAttribute("templateId",templateId);
		model.addAttribute("planLocation", planLocation);
		model.addAttribute("paList", palist);
		return "pages/planTemplate/editsubplan";
	}
	
	
	/**
	 * 跳转到子计划新增页面
	 * @param model
	 * @param subId
	 * @param templateId
	 * @param planLocation
	 * @return
	 *//*
	@GetMapping("/addSubPlan")
	public String addSubPlan(Model model,String subId,String templateId,String planLocation){

		//新增模式
		Plan plan = planService.findById(Integer.parseInt(subId));
		Plan subPlan = new Plan();
		subPlan.setPlanType(plan.getPlanType());
		if("sub".equals(planLocation)){
			subPlan.setParentId(plan.getPlanId());
		} else {
			subPlan.setParentId(plan.getParentId());
		}
		model.addAttribute("subPlan", subPlan);
		model.addAttribute("flag", "add");
		model.addAttribute("currentHandlePlanId", plan.getPlanId());
		model.addAttribute("currentHandlePlanName", plan.getPlanName());
		model.addAttribute("templateId",templateId);
		model.addAttribute("planLocation", planLocation);
		return "pages/planTemplate/editsubplan";
	}*/
	
	
	
	/**
	 * 删除子计划(临时构思)
	 * @param model
	 * @param id
	 * @return
	 */
	@GetMapping("/deleteSubPlan")
	@ResponseBody
	public String deleteSubPlan(Model model,String id){
		//查询下属还有子计划，如果有则提醒不能删除
		List<Plan> subPlanList = planService.findSubPlans(Integer.parseInt(id));
		if(subPlanList != null && subPlanList.size()>0){
			return "true";
		}else {
			planService.deletePlanById(Integer.parseInt(id));
		}
		
		return "false";
	}
	
	
	/**
	 * 保存模板编辑
	 * @param model
	 * @param ptm
	 * @param Plan
	 * @param httpSession
	 * @param templateId
	 * @return
	 */
	@PostMapping("/planEditSave")
	public String planEdit(Model model,PlanTemplateModel ptm,BindingResult br,HttpSession httpSession,String flag) {
		//解析传递过来的值以及判断是否是子计划
		if(ptm != null){
			//传递过来的是模板及计划的分解
			PlanTemplate pt = new PlanTemplate();
			pt.setTemplateId(ptm.getTemplateId());
			PlanTemplate plantemplate = planTemplateService.findPlanTemplateById(pt);
			//TODO保存模板
			plantemplate.setTemplateName(ptm.getTemplateName());
			plantemplate.setTemplateExplain(ptm.getTemplateExplain());
			plantemplate.setTypeId(ptm.getTypeId());
			plantemplate.setPlanType(ptm.getPlanType());
			planTemplateService.update(plantemplate);
			//TODO保存计划
			Plan plan = planService.findById(ptm.getPlanId());
			plan.setPlanName(ptm.getPlanName());
			plan.setPlanDescribe(ptm.getPlanDescribe());
			plan.setBlameSection(ptm.getBlameSection());
			plan.setBlamePerson(ptm.getBlamePerson());
			plan.setStartTime(ptm.getStartTime());
			plan.setEndTime(ptm.getEndTime());
			if(ptm.getSpecialType() != null && ptm.getDateRequire() != null){
				plan.setSpecialType(ptm.getSpecialType());
				plan.setDateRequire(ptm.getDateRequire());
			}
            plan.setImportance(ptm.getImportance());
			if(ptm.getCustomer() != null){
				plan.setCustomer(ptm.getCustomer());
			}
			plan.setAuditProcess(ptm.getAuditProcess());
			if(ptm.getAllowHolidays() != null){
				plan.setAllowHolidays(ptm.getAllowHolidays());
			}
			plan.setAccreditPerson(ptm.getAccreditPerson());
			if(ptm.getLoopMode() != null){
				plan.setLoopMode(ptm.getLoopMode());
			}
			if(ptm.getLoopRate() != null){
				plan.setLoopRate(ptm.getLoopRate());
			}
			if(ptm.getPdDeadline() != null){
				plan.setPdDeadline(ptm.getPdDeadline());
			}
			if(ptm.getPapDeadline() != null){
				plan.setPapDeadline(ptm.getPapDeadline());
			}
			if(ptm.getNdDeadline() != null){
				plan.setNdDeadline(ptm.getNdDeadline());
			}
			if(ptm.getNapDeadline() != null){
				plan.setNapDeadline(ptm.getNapDeadline());
			}
			planService.updatePlan(plan);
			
			//TODO 保存考核方式
			List<PlanAssess> palist = planAssessService.selectByPlanId(ptm.getPlanId());
			if(palist != null && palist.size() > 0){
				if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
					PlanAssess pa = new PlanAssess();
					pa.setId(palist.get(0).getId());
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
					pa.setCheckManner(ptm.getCheckManner());
					if(ptm.getCheckManner() == 1){
						pa.setSingleAmount(ptm.getSingleAmount());
					}else if(ptm.getCheckManner() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDays());
						pa.setCumulativeAmount(ptm.getCumulativeAmount());
					}
					pa.setStatus(1);
					planAssessService.updateByPlanAssessId(pa);
				}
				if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
					PlanAssess pa = new PlanAssess();
					pa.setId(palist.get(1).getId());
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(ptm.getCheckItems());
					pa.setCheckManner(ptm.getCheckManners());
					if(ptm.getCheckManners() == 1){
						pa.setSingleAmount(ptm.getSingleAmounts());
					}else if(ptm.getCheckManners() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDayss());
						pa.setCumulativeAmount(ptm.getCumulativeAmounts());
					}
					pa.setStatus(1);
					planAssessService.updateByPlanAssessId(pa);
				}
				
				//搜索不到考核信息
				if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
					PlanAssess pa = new PlanAssess();
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
					pa.setCheckManner(ptm.getCheckManner());
					if(ptm.getCheckManner() == 1){
						pa.setSingleAmount(ptm.getSingleAmount());
					}else if(ptm.getCheckManner() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDays());
						pa.setCumulativeAmount(ptm.getCumulativeAmount());
					}
					pa.setStatus(1);
					planAssessService.insertPlanAssess(pa);
				}
				if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
					PlanAssess pa = new PlanAssess();
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(ptm.getCheckItems());
					pa.setCheckManner(ptm.getCheckManners());
					if(ptm.getCheckManners() == 1){
						pa.setSingleAmount(ptm.getSingleAmounts());
					}else if(ptm.getCheckManners() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDayss());
						pa.setCumulativeAmount(ptm.getCumulativeAmounts());
					}
					pa.setStatus(1);
					planAssessService.insertPlanAssess(pa);
				}
			}
				
			
			if("add".equals(flag)){
				return "redirect:toFindTemplate_0_0_0?flag=edit";
			}
			return "redirect:toFindTemplate_0_0_0?flag=edit";
		}
		
		return "error";
	}
	
	
	
	/**
	 * 保存模板子计划
	 * @param model
	 * @param subPlan
	 * @param httpSession
	 * @param templateId
	 * @param planId
	 * @param currentHandlePlan
	 * @param planLocation
	 * @return
	 */
	@PostMapping("/subPlanEdit")
	public String subPlanEdit(Model model,PlanTemplateModel ptm,HttpSession httpSession,String templateId,String planId, 
			String currentHandlePlanId, String planLocation,String planTypes,String parentIds){
		
		Plan subPlan = new Plan();
		subPlan.setPlanId(ptm.getPlanId());
		subPlan.setPlanName(ptm.getPlanName());
		if(ptm.getPlanDescribe() != null && !"".equals(ptm.getPlanDescribe())){
			subPlan.setPlanDescribe(ptm.getPlanDescribe());
		}
		subPlan.setBlamePerson(ptm.getBlamePerson());
		subPlan.setBlameSection(ptm.getBlameSection());
		subPlan.setBlameOrganization(ptm.getBlameOrganization());
		if(ptm.getLoopMode() != null){
			subPlan.setLoopMode(ptm.getLoopMode());
		}
		if(ptm.getLoopRate() != null){
			subPlan.setLoopRate(ptm.getLoopRate());
		}
		subPlan.setFinishStandard(ptm.getFinishStandard());
		//验证方式
		if(ptm.getValidateMode() != null && !"".equals(ptm.getValidateMode())){
			if(ptm.getExtractItem() != null && !"".equals(ptm.getExtractItem())){
				subPlan.setValidateMode("1,2");
				subPlan.setExtractItem(ptm.getExtractItem());
			}else{
				subPlan.setValidateMode("1");
			}
		}else{
			if(ptm.getExtractItem() != null && !"".equals(ptm.getExtractItem())){
				subPlan.setValidateMode("2");
				subPlan.setExtractItem(ptm.getExtractItem());
			}
		}

		//TODO保存子计划
		if(subPlan.getPlanId() != null){
			//编辑子计划
			planService.updatePlan(subPlan);
		}else{
			//保存子计划
			User user = (User)httpSession.getAttribute("user");
			Date createDate = new Date();
			if("sub".equals(planLocation) || "".equals(planLocation)){
				List<Plan> subPlans = planService.findSubPlans(Integer.parseInt(parentIds));
				subPlan.setPlanOrder(subPlans.size() + 1);
				subPlan.setParentId(Integer.parseInt(parentIds));
				subPlan.setPlanType(Integer.parseInt(planTypes));
			} else {
				Plan currentPlan = planService.findById(Integer.parseInt(currentHandlePlanId));
				Map<String,Object> criteria = new HashMap<String, Object>();
				if("before".equals(planLocation)){
					subPlan.setPlanOrder(currentPlan.getPlanOrder());
					subPlan.setParentId(currentPlan.getParentId());
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
					subPlan.setPlanOrder(currentPlan.getPlanOrder() == null ? 0 : currentPlan.getPlanOrder() + 1);
					subPlan.setParentId(currentPlan.getParentId());
					criteria.put("planOrder", currentPlan.getPlanOrder() == null ? 0 : currentPlan.getPlanOrder() + 1);
					criteria.put("parentId", currentPlan.getParentId());
					List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
					if(plans.size() > 0)
						for (Plan plan : plans) {
							plan.setPlanOrder(plan.getPlanOrder() + 1);
							planService.updatePlan(plan);
						}
				}
				subPlan.setPlanType(currentPlan.getPlanType());
			}
			if(user != null){
				subPlan.setCreatePerson(user.getUserName());
			}
			subPlan.setCreateTime(createDate);
			subPlan.setStatus(0);
			planService.savePlan(subPlan);
		}
		
		//保存考核设置
		List<PlanAssess> palist = planAssessService.selectByPlanId(subPlan.getPlanId());
		if(palist != null && palist.size() > 0){
			if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
				PlanAssess pa = new PlanAssess();
				pa.setId(palist.get(0).getId());
				pa.setPlanId(ptm.getPlanId());
				pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
				pa.setCheckManner(ptm.getCheckManner());
				if(ptm.getCheckManner() == 1){
					pa.setSingleAmount(ptm.getSingleAmount());
				}else if(ptm.getCheckManner() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDays());
					pa.setCumulativeAmount(ptm.getCumulativeAmount());
				}
				pa.setStatus(1);
				planAssessService.updateByPlanAssessId(pa);
			}
			if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
				PlanAssess pa = new PlanAssess();
				pa.setId(palist.get(1).getId());
				pa.setPlanId(ptm.getPlanId());
				pa.setCheckItem(ptm.getCheckItems());
				pa.setCheckManner(ptm.getCheckManners());
				if(ptm.getCheckManners() == 1){
					pa.setSingleAmount(ptm.getSingleAmounts());
				}else if(ptm.getCheckManners() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDayss());
					pa.setCumulativeAmount(ptm.getCumulativeAmounts());
				}
				pa.setStatus(1);
				planAssessService.updateByPlanAssessId(pa);
			}
		}else{
			//未找到对应的考核数据
			if(subPlan.getPlanId() != null && !"".equals(subPlan.getPlanId())){
				
			}else{
				subPlan = planService.findByPlan(subPlan);
			}
			if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
				PlanAssess pa = new PlanAssess();
				pa.setPlanId(subPlan.getPlanId());
				pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
				pa.setCheckManner(ptm.getCheckManner());
				if(ptm.getCheckManner() == 1){
					pa.setSingleAmount(ptm.getSingleAmount());
				}else if(ptm.getCheckManner() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDays());
					pa.setCumulativeAmount(ptm.getCumulativeAmount());
				}
				pa.setStatus(1);
				planAssessService.insertPlanAssess(pa);
			}
			if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
				PlanAssess pa = new PlanAssess();
				pa.setPlanId(subPlan.getPlanId());
				pa.setCheckItem(ptm.getCheckItems());
				pa.setCheckManner(ptm.getCheckManners());
				if(ptm.getCheckManners() == 1){
					pa.setSingleAmount(ptm.getSingleAmounts());
				}else if(ptm.getCheckManners() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDayss());
					pa.setCumulativeAmount(ptm.getCumulativeAmounts());
				}
				pa.setStatus(1);
				planAssessService.insertPlanAssess(pa);
			}
		}
		
		return "redirect:planEdit?templateId="+templateId;
	}
	
	
	
	/**
	 * 删除模板
	 * @param model
	 * @param pt
	 * @return
	 */
	@GetMapping("/templateDel")
	public String templateDel(Model model,Integer id) {
		PlanTemplate pt = new PlanTemplate();
		pt.setTemplateId(id);
		pt = planTemplateService.findPlanTemplateById(pt);
		planTemplateService.delete(id);
		planService.deletePlanById(pt.getPlanId());
		return "redirect:toFindTemplate_0_0_0?flag=delete";
	}
	
	
	/**
	 * 
	 * @param model
	 * @param PlanId
	 * @param templateId
	 * @return
	 */
	@GetMapping("/changeOrder")
	public String changeOrder(Model model ,String planId,String templateId){
		//获取总计划以及下属子计划
		PlanTemplate planTemplate = new PlanTemplate();
		planTemplate.setTemplateId(Integer.parseInt(templateId));
		PlanTemplate pt = planTemplateService.findPlanTemplateById(planTemplate);
		Integer planIds = pt.getPlanId();
		Plan plan = planService.findById(planIds);
		// 查询子计划详细
		Integer parentPlan = plan.getPlanId();
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		if (subPlans.size() > 0 && subPlans != null) {
			for (Plan p : subPlans) {
				List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
				if (subSubPlans.size() > 0)
					p.setSubPlans(subSubPlans);
				for (Plan pp : subSubPlans) {
					List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
					if (subSubSubPlans.size() > 0)
						pp.setSubPlans(subSubSubPlans);
				}
			}
			plan.setSubPlans(subPlans);
		}
		//查询当前选中计划
		Plan selectPlan = planService.findById(Integer.parseInt(planId));
		//查询当前选中计划的平级计划
		Integer parentId = selectPlan.getParentId();
		List<Plan> subPlan = planService.findSubPlans(parentId);
		//查询当前选中计划的父级计划
		Plan parentPlans = planService.findById(parentId);
		Integer parentPlanId = parentPlans.getParentId();
		if(parentPlanId == null || parentPlanId == 0){
			//已经是顶层父计划
			model.addAttribute("parentPlan", subPlan);
		}else{
			//查询父计划
			List<Plan> parentPlanList = planService.findSubPlans(parentPlanId);
			model.addAttribute("parentPlan", parentPlanList);
		}
		model.addAttribute("allPlan", plan);
		model.addAttribute("selectPlan", selectPlan);
		model.addAttribute("templateId", templateId);
		model.addAttribute("subPlan", subPlan);
		return "pages/planTemplate/change_order";
	}
	
	
	/**
	 * 改变子计划顺序
	 * (重新选择上级计划/调至最顶层)
	 * @param model
	 * @param parentId
	 * @param planId
	 */
	@PostMapping("/change_plan")
	@ResponseBody  
	public Plan changePlan(Model model,String parentId,String planIds){
		
		Integer planId = 0;
		if(planIds != null && !"".equals(planIds)){
			planId = Integer.parseInt(planIds);
		}
		Integer parentid = 0;
		if(parentId != null && !"".equals(parentId)){
			parentid = Integer.parseInt(parentId);
		}
		
		Plan plan = planService.findById(planId);
		Integer parentsId = 0;
		if(parentid != 0){
			//更换父计划
			if(plan != null){
				plan.setParentId(parentid);
				planService.updatePlan(plan);
				parentsId = parentid;
			}
		}else{
			//更换到顶级父计划的子计划
			if(plan != null){
				Integer planParentId = plan.getParentId();
				if(planParentId != null){
					Plan planParent = planService.findById(planParentId);
					Integer planParentParentId = planParent.getParentId();
					if(planParentParentId != null){
						Plan planParentParent = planService.findById(planParentParentId);
						Integer planParentParentParentId = planParentParent.getParentId();
						if(planParentParentParentId != null){
							Plan planParentParentParent = planService.findById(planParentParentParentId);
							Integer planParentParentParentParentId = planParentParentParent.getParentId();
							if(planParentParentParentParentId != null){
								//应该已经到最顶层了
							}else{
								plan.setParentId(planParentParentParentId);
								planService.updatePlan(plan);
								parentsId = planParentParentParentId;
							}
						}else{
							plan.setParentId(planParentParentId);
							planService.updatePlan(plan);
							parentsId = planParentParentId;
						}
					}else{
						plan.setParentId(planParentId);
						planService.updatePlan(plan);
						parentsId = planParentId;
					}
				}
			}
		}
		
		//查到最上级父计划id查询子计划
		Plan plans = planService.findById(parentsId);
		//查询出调整后的结果
		List<Plan> subPlans = planService.findSubPlans(plans.getPlanId());
		if (subPlans.size() > 0 && subPlans != null) {
			for (Plan p : subPlans) {
				List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
				if (subSubPlans.size() > 0)
					p.setSubPlans(subSubPlans);
				for (Plan pp : subSubPlans) {
					List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
					if (subSubSubPlans.size() > 0)
						pp.setSubPlans(subSubSubPlans);
					for(Plan spp : subSubSubPlans){
						List<Plan> subSubSubSubPlans = planService.findSubPlans(spp.getPlanId());
						if(subSubSubSubPlans.size() > 0)
							spp.setSubPlans(subSubSubSubPlans);
					}
				}
			}
			plans.setSubPlans(subPlans);
		}
		return plans;
	}
	
	
	/**
	 * 改变子计划顺序
	 * (选定位置之前/选定位置之后)
	 * @param model
	 * @param position   位置
	 * @param planIds    选择到的计划id
	 * @param planId     本计划id
	 * @return
	 */
	@PostMapping("/change_order")
	@ResponseBody  
	public Plan changePosition(Model model,String position,String planIds,String planId){
		Integer planid = 0;
		if(planIds != null && !"".equals(planIds)){
			planid = Integer.parseInt(planIds);
		}
		Integer selectPlanId = 0;
		if(planId != null && !"".equals(planId)){
			selectPlanId = Integer.parseInt(planId);
		}
		String strPosition = "";
		if(position != null && !"".equals(position)){
			strPosition = position;
		}
		
		//选择位置的计划
		Plan plan = planService.findById(planid);
		//当前的计划
		Plan selectPlan = planService.findById(selectPlanId);
		
		Integer parentId = 0;
		
		Integer planOrder = plan.getPlanOrder();
		Integer selectPlanOrder = selectPlan.getPlanOrder();
		
		if("beforeCheck".equals(strPosition)){
			//在选中位置之前
			int index = planOrder;
			planOrder = selectPlanOrder;
			selectPlanOrder = index;
			plan.setPlanOrder(planOrder);
			selectPlan.setPlanOrder(selectPlanOrder);
			planService.updatePlan(plan);
			planService.updatePlan(selectPlan);
			parentId = plan.getParentId();
		}else if("afterCheck".equals(strPosition)){
			//在选中位置之后
			int index = selectPlanOrder;
			selectPlanOrder = planOrder;
			planOrder = index;
			plan.setPlanOrder(planOrder);
			selectPlan.setPlanOrder(selectPlanOrder);
			planService.updatePlan(plan);
			planService.updatePlan(selectPlan);
			parentId = plan.getParentId();
		}
		
		// 查到最上级父计划id查询子计划
		Plan plans = planService.findById(parentId);
		// 查询出调整后的结果
		List<Plan> subPlans = planService.findSubPlans(plans.getPlanId());
		if (subPlans.size() > 0 && subPlans != null) {
			for (Plan p : subPlans) {
				List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
				if (subSubPlans.size() > 0)
					p.setSubPlans(subSubPlans);
				for (Plan pp : subSubPlans) {
					List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
					if (subSubSubPlans.size() > 0)
						pp.setSubPlans(subSubSubPlans);
					for (Plan spp : subSubSubPlans) {
						List<Plan> subSubSubSubPlans = planService.findSubPlans(spp.getPlanId());
						if (subSubSubSubPlans.size() > 0)
							spp.setSubPlans(subSubSubSubPlans);
					}
				}
			}
			plans.setSubPlans(subPlans);
		}
		
		return plans;
	}
	
	
/*	 *//**
     * @description: 根据传进来Plan对象的blameSection字段值分解成负责部门以及负责机构
     * @param @param plan
     * @param @return
     * @return 
     * @throws
     *//*
    public Plan getBlameSectionAndOrganization(Plan plan){
        if(plan.getBlameSection() != null && !"".equals(plan.getBlameSection())){
            String[] sections = plan.getBlameSection().split("-");
            if(sections.length > 0){
                plan.setBlameOrganization(sections[0]);
                String blameSection = "";
                for (int i = 1; i < sections.length; i++) {
                    blameSection += sections[i]+"-";
                }
                blameSection = blameSection.substring(0, blameSection.length() - 1);
                plan.setBlameSection(blameSection);
            }
        }
        return plan;
    }*/
    
    
    /**
	 * 查询过审主计划
	 * @param model
	 * @param plan
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @return
	 */
    @SuppressWarnings("unchecked")
	@GetMapping("/toFindApprovedPlan")
	public String toFindApprovedPlan(Model model,Plan plan){
		//转模糊查询
		String planName=plan.getPlanName();
		if(planName!=null && !"".equals(planName)){
			planName="%"+planName+"%";
			plan.setPlanName(planName);
		}
		
		plan.setStart(0);
		plan.setEnd(50);
		
		List<Office> officeList = new ArrayList<>();
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOffice").getValue();
		for(Office office : CQList){
			officeList.add(office);
			List<Office> subOffices = office.getSubOffices();
			if(subOffices != null && subOffices.size() > 0){
				for(Office subOffice : subOffices){
					officeList.add(subOffice);
					List<Office> subSubOffices = subOffice.getSubOffices();
					if(subSubOffices != null && subSubOffices.size() > 0){
						for(Office subSubOffice : subSubOffices){
							officeList.add(subSubOffice);
						}
					}
				}
			}
		}
		
		model.addAttribute("CQList", officeList);
		
		List<Plan> pList=this.planService.findApprovedPlanExceptInterimPlan(plan);
		model.addAttribute("pList",pList);
		return "pages/planTemplate/choose_plan";
	}
    
    
    /**
	 * 验证是否已经生成模板
	 * @return
	 */
	@GetMapping("/templateVerify")
	public @ResponseBody String templateVerify(String id){
		Plan plan = this.planService.findById(Integer.parseInt(id));
		List<PlanTemplate> list = this.planTemplateService.findPlanTemplate();
		String msg="true";
		
		for(PlanTemplate ptt : list){
			if(ptt.getPlanId() != null){
				if(ptt.getPlanId().equals(plan.getPlanId())){
					msg=ptt.getTemplateName();
					return msg;
				}
			}
		}
		return msg;
	}
    
    
	/**
	 * 编辑模板信息
	 * @param planId
	 * @param model
	 * @param session
	 * @param erroFlag
	 * @return
	 * @throws ParseException
	 */
    @GetMapping("/editTemplateInfo")
    public String editTemplateInfo(String planId,Model model,HttpSession session) throws ParseException{
    	
    	User user = (User) session.getAttribute("user");
    	//接收到选择的计划
    	if(planId != null && !"".equals(planId)){
    		
    		//查询选择计划主计划信息
    		Plan mainPlan = planService.findById(Integer.parseInt(planId));
    		
    		//查询主计划下子计划（分解的子计划）
    		List<Plan> planList = planService.findSubPlans(mainPlan.getPlanId());
    		
    		//生成新的主计划
    		int newPlanId = planService.findMaxPlanId()+1;
    		mainPlan.setPlanId(newPlanId);
    		mainPlan.setStatus(0);
			String planStartTime = mainPlan.getStartTime();
			mainPlan.setStartTime(planStartTime);
			if(user != null){
				mainPlan.setCreatePerson(user.getUserName());
			}
			mainPlan.setCreateTime(new Date());
			planService.savePlan(mainPlan);
			
			//生成新的考核信息
			PlanAssess planAssess = new PlanAssess();
			int newId = planAssessService.findMaxId() + 1;
			planAssess.setId(newId);
			planAssess.setPlanId(newPlanId);
			planAssess.setCheckItem(1);
			planAssess.setStatus(1);
			planAssessService.insertPlanAssess(planAssess);
			
			PlanAssess planAssessc = new PlanAssess();
			int newIdc = planAssessService.findMaxId() + 2;
			planAssessc.setId(newIdc);
			planAssessc.setPlanId(newPlanId);
			planAssessc.setCheckItem(2);
			planAssessc.setStatus(1);
			planAssessService.insertPlanAssess(planAssessc);
    		
    		//先生成新的模板信息
    		PlanTemplate planTemplate = new PlanTemplate();
    		int newTemplateId = planTemplateService.maxTemplateId()+1;
    		planTemplate.setTemplateId(newTemplateId);
    		planTemplate.setPlanId(newPlanId);
    		planTemplate.setPlanType(mainPlan.getPlanType());
    		planTemplate.setCreateDate(new Date());
    		if(user != null ){
    			planTemplate.setCreateName(user.getUserName());
    		}
    		planTemplate.setState(1);
    		planTemplateService.insetTemplate(planTemplate);
    		
    		//生成新的计划的子计划
			if(planList != null && planList.size() > 0){
				for(Plan plans : planList){
					plans.setParentId(newPlanId);
					Integer subId = plans.getPlanId();
					List<Plan> subPlan = planService.findSubPlans(subId);
					int subMax = planService.findMaxPlanId();
					plans.setPlanId(subMax+1);
		            //计算工期
					if(plans.getPlanType() != 2){
						timeLimitAndOffset(planStartTime, plans);
					}
					planService.savePlan(plans);
					
						if (subPlan != null && subPlan.size() > 0) {
							for (Plan subPlans : subPlan) {
								subPlans.setParentId(subMax + 1);
								Integer subSubId = subPlans.getPlanId();
								List<Plan> subSubPlan = planService.findSubPlans(subSubId);
								int subSubMax = planService.findMaxPlanId();
								subPlans.setPlanId(subSubMax + 1);
								// 计算工期
								if (subPlans.getPlanType() != 2) {
									timeLimitAndOffset(planStartTime, subPlans);
								}
								planService.savePlan(subPlans);

									if (subSubPlan != null && subSubPlan.size() > 0) {
										for (Plan subSubPlans : subSubPlan) {
											subSubPlans.setParentId(subSubMax + 1);
											Integer subSubSubId = subSubPlans.getPlanId();
											List<Plan> subSubSubPlan = planService.findSubPlans(subSubSubId);
											int subSubSubMax = planService.findMaxPlanId();
											subSubPlans.setPlanId(subSubSubMax + 1);
											// 计算工期
											if (subSubPlans.getPlanType() != 2) {
												timeLimitAndOffset(planStartTime, subSubPlans);
											}
												planService.savePlan(subSubPlans);

												if (subSubSubPlan != null && subSubSubPlan.size() > 0) {
													for (Plan subSubSubPlans : subSubSubPlan) {
														subSubSubPlans.setParentId(subSubSubMax + 1);
														int subSubSubSubMax = planService.findMaxPlanId();
														subSubSubPlans.setPlanId(subSubSubSubMax + 1);
														// 计算工期
														if (subSubSubPlans.getPlanType() != 2) {
															timeLimitAndOffset(planStartTime, subSubSubPlans);
														}
															planService.savePlan(subSubSubPlans);
															List<Plan> subSubSubSubPlans = planService.findSubPlans(subSubSubSubMax + 1);
															subSubSubPlans.setSubPlans(subSubSubSubPlans);
															subSubSubPlans.setSubSum(subSubSubSubPlans.size());
														
													}
													subSubPlans.setSubPlans(subSubSubPlan);
													subPlans.setSubSum(subSubSubPlan.size());
												}
												
											
										}
										subPlans.setSubPlans(subSubPlan);
										subPlans.setSubSum(subSubPlan.size());
									}
								
								
							}
							plans.setSubPlans(subPlan);
							plans.setSubSum(subPlan.size());
						}
				
				}
				mainPlan.setSubPlans(planList);
			}
    		model.addAttribute("pt",planTemplate);
    		model.addAttribute("plan",mainPlan);
    		List<TemplateType> templateTypes = templateTypeService.findAllTemplateTypeNoPage();
    		model.addAttribute("ttList", templateTypes);
    		List<PlanAssess> planAssessList = planAssessService.selectByPlanId(mainPlan.getPlanId());
    		model.addAttribute("paList", planAssessList);
    		return "pages/planTemplate/edit_template_info";
    	}
    	return "error";
    }
    
    
    /**
	 * 计算工期和偏移量
	 * @param planStartTime
	 * @param plans
	 * @throws ParseException
	 */
	private void timeLimitAndOffset(String planStartTime, Plan plans) throws ParseException {
		String startTime = plans.getStartTime();
		String endTime = plans.getEndTime();
		
		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
			Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
			plans.setTimeLimit(timeLimit);
			//计算偏移量
			if(planStartTime != null && !"".equals(planStartTime)){
				Integer timeOffset=DateUtil.daysBetween(planStartTime, startTime);
				plans.setTimeOffset(timeOffset);
			}
		}
	}
	
	
	/**
	 * 取消生成模板
	 * @param  request
	 * @param  planId
	 * @param  templateId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancelAdd")
	public String cancelAdd(HttpServletRequest request, String planId, String templateId){
		if(templateId != null && !"".equals(templateId)){
			//消除模板记录
			planTemplateService.delete(Integer.parseInt(templateId));
		}
		
		if(planId != null && !"".equals(planId) && planId != "null" && !"null".equals(planId)){
			//消除考核
			planAssessService.deleteByPlanId(Integer.parseInt(planId));
			
			//消除计划记录
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
										}
									}
									planService.deletePlanById(ppp.getPlanId());
								}
							}
							planService.deletePlanById(pp.getPlanId());
						}
					}
					planService.deletePlanById(p.getPlanId());	
				}
			}
			planService.deletePlanById(plan.getPlanId());
		}
		return "redirect:toFindApprovedPlan";
		
	}
	
	/**
	 * 跳转新增模板子计划编辑
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@GetMapping("/choseLocation")	
	public String choseLocation(String planId, Model model,String type,String templateId,String planLocation,String flag){
		Plan plan = new Plan();
		List<PlanAssess> palist = new ArrayList();
		if("edit".equals(flag)){
			plan = planService.findById(Integer.parseInt(planId));
			model.addAttribute("currentHandlePlanId", plan.getPlanId());
			model.addAttribute("currentHandlePlanName", plan.getPlanName());
			palist = planAssessService.selectByPlanId(plan.getPlanId());
		}else if("add".equals(flag)){
			Plan mainPlan = planService.findById(Integer.parseInt(planId));
			model.addAttribute("currentHandlePlanId", mainPlan.getPlanId());
			model.addAttribute("currentHandlePlanName", mainPlan.getPlanName());
			model.addAttribute("planTypes", mainPlan.getPlanType());
			if("sub".equals(planLocation)){
				model.addAttribute("parentIds",mainPlan.getPlanId());
			} else {
				model.addAttribute("parentIds",mainPlan.getParentId());
			}
		}
		model.addAttribute("paList", palist);
		model.addAttribute("type", type);
		model.addAttribute("subPlan", plan);
		model.addAttribute("planLocation", planLocation);
		model.addAttribute("templateId",templateId);
		
		if("add".equals(type) || "edit".equals(type)){
			return "pages/planTemplate/choselocation";
		}else{
			return "pages/planTemplate/editsubplan";
		}
	}
	
	
	/**
	 * 保存新增模板子计划
	 * @param model
	 * @param subPlan
	 * @param httpSession
	 * @param templateId
	 * @param planId
	 * @param currentHandlePlan
	 * @param planLocation
	 * @return
	 */
	@PostMapping("/addSubPlanSave")
	public String addSubPlanSave(Model model,PlanTemplateModel ptm,HttpSession httpSession,String currentHandlePlanId, String planLocation, 
								String templateId,String planTypes,String parentIds){
		
		Plan subPlan = new Plan();
		subPlan.setPlanId(ptm.getPlanId());
		subPlan.setPlanName(ptm.getPlanName());
		if(ptm.getPlanDescribe() != null && !"".equals(ptm.getPlanDescribe())){
			subPlan.setPlanDescribe(ptm.getPlanDescribe());
		}
		subPlan.setBlamePerson(ptm.getBlamePerson());
		subPlan.setBlameSection(ptm.getBlameSection());
		subPlan.setBlameOrganization(ptm.getBlameOrganization());
		if(ptm.getLoopMode() != null){
			subPlan.setLoopMode(ptm.getLoopMode());
		}
		if(ptm.getLoopRate() != null){
			subPlan.setLoopRate(ptm.getLoopRate());
		}
		subPlan.setFinishStandard(ptm.getFinishStandard());
		//验证方式
		if(ptm.getValidateMode() != null && !"".equals(ptm.getValidateMode())){
			if(ptm.getExtractItem() != null && !"".equals(ptm.getExtractItem())){
				subPlan.setValidateMode("1,2");
				subPlan.setExtractItem(ptm.getExtractItem());
			}else{
				subPlan.setValidateMode("1");
			}
		}else{
			if(ptm.getExtractItem() != null && !"".equals(ptm.getExtractItem())){
				subPlan.setValidateMode("2");
				subPlan.setExtractItem(ptm.getExtractItem());
			}
		}
		
		//TODO保存子计划
		if(subPlan.getPlanId() != null){
			//编辑子计划
			planService.updatePlan(subPlan);
		}else{
			//保存子计划
			User user = (User)httpSession.getAttribute("user");
			Date createDate = new Date();
			
			if("sub".equals(planLocation) || "".equals(planLocation)){
				List<Plan> subPlans = planService.findSubPlans(Integer.parseInt(parentIds));
				subPlan.setPlanOrder(subPlans.size() + 1);
				subPlan.setPlanType(Integer.parseInt(planTypes));
				subPlan.setParentId(Integer.parseInt(parentIds));
			} else {
				Plan currentPlan = planService.findById(Integer.parseInt(currentHandlePlanId));
				Map<String, Object> criteria = new HashMap<String, Object>();
				if ("before".equals(planLocation)) {
					subPlan.setPlanOrder(currentPlan.getPlanOrder());
					subPlan.setParentId(currentPlan.getParentId());
					criteria.put("planOrder", currentPlan.getPlanOrder());
					criteria.put("parentId", currentPlan.getParentId());
					List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
					if (plans.size() > 0)
						for (Plan plan : plans) {
							plan.setPlanOrder(plan.getPlanOrder() + 1);
							planService.updatePlan(plan);
						}
				}
				if ("after".equals(planLocation)) {
					subPlan.setPlanOrder(currentPlan.getPlanOrder() == null ? 0 : currentPlan.getPlanOrder() + 1);
					subPlan.setParentId(currentPlan.getParentId());
					criteria.put("planOrder", currentPlan.getPlanOrder() == null ? 0 : currentPlan.getPlanOrder() + 1);
					criteria.put("parentId", currentPlan.getParentId());
					List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
					if (plans.size() > 0)
						for (Plan plan : plans) {
							plan.setPlanOrder(plan.getPlanOrder() + 1);
							planService.updatePlan(plan);
						}
				}
				subPlan.setPlanType(currentPlan.getPlanType());
			}

			if (user != null) {
				subPlan.setCreatePerson(user.getUserName());
			}
			subPlan.setCreateTime(createDate);
			subPlan.setStatus(0);
			planService.savePlan(subPlan);
		}
		
		//保存考核设置
		List<PlanAssess> palist = planAssessService.selectByPlanId(subPlan.getPlanId());
		if(palist != null && palist.size() > 0){
			if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
				PlanAssess pa = new PlanAssess();
				pa.setId(palist.get(0).getId());
				pa.setPlanId(ptm.getPlanId());
				pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
				pa.setCheckManner(ptm.getCheckManner());
				if(ptm.getCheckManner() == 1){
					pa.setSingleAmount(ptm.getSingleAmount());
				}else if(ptm.getCheckManner() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDays());
					pa.setCumulativeAmount(ptm.getCumulativeAmount());
				}
				pa.setStatus(1);
				planAssessService.updateByPlanAssessId(pa);
			}
			if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
				PlanAssess pa = new PlanAssess();
				pa.setId(palist.get(1).getId());
				pa.setPlanId(ptm.getPlanId());
				pa.setCheckItem(ptm.getCheckItems());
				pa.setCheckManner(ptm.getCheckManners());
				if(ptm.getCheckManners() == 1){
					pa.setSingleAmount(ptm.getSingleAmounts());
				}else if(ptm.getCheckManners() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDayss());
					pa.setCumulativeAmount(ptm.getCumulativeAmounts());
				}
				pa.setStatus(1);
				planAssessService.updateByPlanAssessId(pa);
			}
		}else{
			//未找到对应的考核数据
			if(subPlan.getPlanId() != null && !"".equals(subPlan.getPlanId())){
				
			}else{
				subPlan = planService.findByPlan(subPlan);
			}
			if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
				PlanAssess pa = new PlanAssess();
				pa.setPlanId(subPlan.getPlanId());
				pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
				pa.setCheckManner(ptm.getCheckManner());
				if(ptm.getCheckManner() == 1){
					pa.setSingleAmount(ptm.getSingleAmount());
				}else if(ptm.getCheckManner() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDays());
					pa.setCumulativeAmount(ptm.getCumulativeAmount());
				}
				pa.setStatus(1);
				planAssessService.insertPlanAssess(pa);
			}
			if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
				PlanAssess pa = new PlanAssess();
				pa.setPlanId(subPlan.getPlanId());
				pa.setCheckItem(ptm.getCheckItems());
				pa.setCheckManner(ptm.getCheckManners());
				if(ptm.getCheckManners() == 1){
					pa.setSingleAmount(ptm.getSingleAmounts());
				}else if(ptm.getCheckManners() == 2){
					pa.setCumulativeDays(ptm.getCumulativeDayss());
					pa.setCumulativeAmount(ptm.getCumulativeAmounts());
				}
				pa.setStatus(1);
				planAssessService.insertPlanAssess(pa);
			}
		}
		return "redirect:planEdit?templateId="+templateId;
	}
	
	
	/**
	 * 新增计划跳转编辑画面
	 * @param model
	 * @param pt
	 * @param erroFlag
	 * @return
	 */
	@GetMapping("/AddPlanTemplateEdit")
	public String AddPlanTemplateEdit(Model model,String templateId,String actionflag) {
		PlanTemplate pt = new PlanTemplate();
		pt.setTemplateId(Integer.parseInt(templateId));
		PlanTemplate planTemplate = planTemplateService.findPlanTemplateById(pt);
		// 查询模板分类类型
		TemplateType templateType = new TemplateType();
		templateType.setStart(0);
		templateType.setEnd(10);
		List<TemplateType> ttList = templateTypeService.findAllTemplateType(templateType);

		//TODO模板ID查询计划
		Plan plan = planService.findById (planTemplate.getPlanId());
		//TODO查询子计划详细
		Integer parentPlan = plan.getPlanId();
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		if(subPlans.size() > 0 && subPlans != null){
			for (Plan p : subPlans) {
				List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
				if(subSubPlans.size() > 0){
					for (Plan pp : subSubPlans) {
						List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
						if(subSubSubPlans.size() > 0){
							pp.setSubPlans(subSubSubPlans);
							for(Plan ppp : subSubSubPlans){
								List<Plan> subSubSubSubPlans = planService.findSubPlans(ppp.getPlanId());
								if(subSubSubSubPlans != null && subSubSubSubPlans.size() > 0){
									ppp.setSubPlans(subSubSubSubPlans);
								}
							}
							pp.setSubPlans(subSubSubPlans);
						}
					}
					p.setSubPlans(subSubPlans);
				}
			}
			plan.setSubPlans(subPlans);
		}
		if("delete".equals(actionflag)){
			actionflag = "删除成功";
		}
		List<PlanAssess> paList = planAssessService.selectByPlanId(plan.getPlanId());
		model.addAttribute("actionflag", actionflag);
		model.addAttribute("plan",plan);
		model.addAttribute("pt",planTemplate);
		model.addAttribute("ttList",ttList);
		model.addAttribute("paList",paList);
		return "pages/planTemplate/edit_template_info";
	}
	
	
	/**
	 * 更新模板任务编辑(弹窗专用)
	 * @param model
	 * @param ptm
	 * @param Plan
	 * @param httpSession
	 * @param templateId
	 * @return
	 */
	@PostMapping("/addSubPlanUpdate")
	@ResponseBody
	public String addSubPlanUpdate(Model model,PlanTemplateModel ptm,BindingResult br,HttpSession httpSession) {
		//解析传递过来的值以及判断是否是子计划
		if(ptm != null){
			//传递过来的是模板及计划的分解
			PlanTemplate pt = new PlanTemplate();
			pt.setTemplateId(ptm.getTemplateId());
			PlanTemplate plantemplate = planTemplateService.findPlanTemplateById(pt);
			//TODO保存模板
			plantemplate.setTemplateName(ptm.getTemplateName());
			plantemplate.setTemplateExplain(ptm.getTemplateExplain());
			plantemplate.setTypeId(ptm.getTypeId());
			plantemplate.setPlanType(ptm.getPlanType());
			planTemplateService.update(plantemplate);
			//TODO保存计划
			Plan plan = planService.findById(ptm.getPlanId());
			plan.setPlanName(ptm.getPlanName());
			plan.setPlanDescribe(ptm.getPlanDescribe());
			plan.setBlameSection(ptm.getBlameSection());
			plan.setBlamePerson(ptm.getBlamePerson());
			if(ptm.getSpecialType() != null && ptm.getDateRequire() != null){
				plan.setSpecialType(ptm.getSpecialType());
				plan.setDateRequire(ptm.getDateRequire());
			}
            plan.setImportance(ptm.getImportance());
			if(ptm.getCustomer() != null){
				plan.setCustomer(ptm.getCustomer());
			}
			plan.setAuditProcess(ptm.getAuditProcess());
			if(ptm.getAllowHolidays() != null){
				plan.setAllowHolidays(ptm.getAllowHolidays());
			}
			plan.setAccreditPerson(ptm.getAccreditPerson());
			if(ptm.getLoopMode() != null){
				plan.setLoopMode(ptm.getLoopMode());
			}
			if(ptm.getLoopRate() != null){
				plan.setLoopRate(ptm.getLoopRate());
			}
			planService.updatePlan(plan);
			
			//TODO 保存考核方式
			List<PlanAssess> palist = planAssessService.selectByPlanId(ptm.getPlanId());
			if(palist != null && palist.size() > 0){
				if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
					PlanAssess pa = new PlanAssess();
					pa.setId(palist.get(0).getId());
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
					pa.setCheckManner(ptm.getCheckManner());
					if(ptm.getCheckManner() == 1){
						pa.setSingleAmount(ptm.getSingleAmount());
					}else if(ptm.getCheckManner() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDays());
						pa.setCumulativeAmount(ptm.getCumulativeAmount());
					}
					pa.setStatus(1);
					planAssessService.updateByPlanAssessId(pa);
				}
				if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
					PlanAssess pa = new PlanAssess();
					pa.setId(palist.get(1).getId());
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(ptm.getCheckItems());
					pa.setCheckManner(ptm.getCheckManners());
					if(ptm.getCheckManners() == 1){
						pa.setSingleAmount(ptm.getSingleAmounts());
					}else if(ptm.getCheckManners() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDayss());
						pa.setCumulativeAmount(ptm.getCumulativeAmounts());
					}
					pa.setStatus(1);
					planAssessService.updateByPlanAssessId(pa);
				}
				//未找到对应的考核数据
				if(!"0".equals(ptm.getCheckItem()) && ptm.getCheckItem() != null){
					PlanAssess pa = new PlanAssess();
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(Integer.parseInt(ptm.getCheckItem()));
					pa.setCheckManner(ptm.getCheckManner());
					if(ptm.getCheckManner() == 1){
						pa.setSingleAmount(ptm.getSingleAmount());
					}else if(ptm.getCheckManner() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDays());
						pa.setCumulativeAmount(ptm.getCumulativeAmount());
					}
					pa.setStatus(1);
					planAssessService.insertPlanAssess(pa);
				}
				if(!"0".equals(ptm.getCheckItems()) && ptm.getCheckItems() != null){
					PlanAssess pa = new PlanAssess();
					pa.setPlanId(ptm.getPlanId());
					pa.setCheckItem(ptm.getCheckItems());
					pa.setCheckManner(ptm.getCheckManners());
					if(ptm.getCheckManners() == 1){
						pa.setSingleAmount(ptm.getSingleAmounts());
					}else if(ptm.getCheckManners() == 2){
						pa.setCumulativeDays(ptm.getCumulativeDayss());
						pa.setCumulativeAmount(ptm.getCumulativeAmounts());
					}
					pa.setStatus(1);
					planAssessService.insertPlanAssess(pa);
				}
			}
			
			return "true";
		}
		
		return "false";
	}
	
	
	/**
	 * @description: 选择子计划新增位置（之前已有子计划）
	 * @param  request
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/choseAddLocation")
	public ModelAndView choseAddLocation(HttpServletRequest request, String planId, String type, String templateId,String flag){
		ModelAndView mv = new ModelAndView("pages/planTemplate/chose_add_location");
		Plan plan = planService.findById(Integer.parseInt(planId));
		if(type != null && !"".equals(type))
		mv.addObject("type", type);
		mv.addObject("flag",flag);
		mv.addObject("plan", plan);
		mv.addObject("templateId",templateId);
		return mv;
	}
	
	
	/**
	 * 系统提取获取
	 * @param model
	 * @return
	 */
	@RequestMapping("/getSystemextra")
	public String getSystemextra(Model model){
		List<Map> SystemextraJson = PlanResourcesUtil.findSystemextra();
		List<String> interviewInfo = new ArrayList<>();
		List<String> resumeInfoList = new ArrayList<>();
		List<String> wantedInfoList = new ArrayList<>();
		List<String> kaoqinList = new ArrayList<>();
		for(Map json : SystemextraJson){
			//面试邀请
			JSONObject objs = ((JSONObject)json.get("interviewInfo"));
			interviewInfo.add(String.valueOf(objs.get("commentAndtoWhiteCount")));
			interviewInfo.add(String.valueOf(objs.get("interviewTotal")));
			interviewInfo.add(String.valueOf(objs.get("commentAndnotsuitCount")));
			interviewInfo.add(String.valueOf(objs.get("waitInterviewCount")));
			interviewInfo.add(String.valueOf(objs.get("waitCommentCount")));
			interviewInfo.add(String.valueOf(objs.get("commentAndnotpassCount")));
			interviewInfo.add(String.valueOf(objs.get("commentAndreInterviewCount")));
			interviewInfo.add(String.valueOf(objs.get("commentAndtoBlackCount")));
			interviewInfo.add(String.valueOf(objs.get("passInterviewCount")));
			
			//简历信息
			JSONObject resumeInfo = ((JSONObject)json.get("resumeInfo"));
			resumeInfoList.add(String.valueOf(resumeInfo.get("notPassCount")));
			resumeInfoList.add(String.valueOf(resumeInfo.get("dealTotal")));
			resumeInfoList.add(String.valueOf(resumeInfo.get("toWhiteCount")));
			resumeInfoList.add(String.valueOf(resumeInfo.get("toBlackCount")));
			resumeInfoList.add(String.valueOf(resumeInfo.get("interviewCount")));
			
			//需求信息
			JSONObject wantedInfo = ((JSONObject)json.get("wantedInfo"));
			String wanted = String.valueOf(wantedInfo);
			wanted = wanted.substring(2, wanted.length()-1);
			wanted = wanted.replace("[]", "");
			wantedInfoList.add(wanted);
			
			//考勤信息
			JSONObject kaoqin = ((JSONObject)json.get("kaoqin"));
			kaoqinList.add(String.valueOf(kaoqin.get("isOnTime")));
		}
		model.addAttribute("interviewInfo", interviewInfo);
		model.addAttribute("resumeInfo", resumeInfoList);
		model.addAttribute("wantedInfo", wantedInfoList);
		model.addAttribute("kaoqin", kaoqinList);
		return "pages/planTemplate/systemextra";
	}
    
}
