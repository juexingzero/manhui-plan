package com.manhui.controller;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Plan;
import com.manhui.model.PlanAssess;
import com.manhui.model.PlanTemplateModel;
import com.manhui.model.User;
import com.manhui.service.PlanAssessService;
import com.manhui.service.PlanService;
import com.manhui.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: PlanDecomposeController
 * @description: 计划分解相关控制器
 * @author:	HeJiayan
 * @date Create in 下午3:35:06 2017年11月16日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanDecomposeController {

    @Autowired
    private PlanService planService;
    @Autowired
	private PlanAssessService planAssessService;

    @InitBinder
	public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

    /**
     * @description: 计划分解列表页面跳转
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/plan_decompose")
    public ModelAndView planDecompose(){
    	ModelAndView mv = new ModelAndView("pages/planDecompose/plan_decompose");
		return mv;
    }
    
    /**
     * @description: 分页查询待分解计划
     * @param plan
     * @param pageCurrent
     * @param pageSize
     * @param pageCount
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/find_plans_{pageCurrent}_{pageSize}_{pageCount}")
    public ModelAndView findPlans(Plan plan, @PathVariable Integer pageCurrent, @PathVariable Integer pageSize,
			@PathVariable Integer pageCount){
    	ModelAndView mv = new ModelAndView("pages/planDecompose/plan_decompose_list");
    	User user = (User) CacheManager.getCacheInfo("user").getValue();
    	plan.setBlamePerson(user.getName());
    	
    	//转换模糊查询
		String planName = plan.getPlanName();
		if(planName != null && !"".equals(planName)){
			
			planName = "%"+planName+"%";
			plan.setPlanName(planName);
		}
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planService.getDecomPlancount(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> decomPlans = planService.getDecomPlans(plan);
    	
		if(decomPlans != null && decomPlans.size() > 0){
			mv.addObject("decomPlans", decomPlans);
		}
		
		mv.addObject("planType", plan.getPlanType());
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
    }
    
    /**
     * @description: 分解计划页面跳转
     * @param planId
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/decompose_plan")
    public ModelAndView decomposePlan(String planId){
    	ModelAndView mv = new ModelAndView();
    	Plan plan = new Plan();
    	if(planId != null && !"".equals(planId))
    		plan = planService.findById(Integer.parseInt(planId));
    	
    	List<Plan> plans = planService.findSubPlans(plan.getPlanId());
    	if(plans != null && plans.size() > 0){
    		plan.setSubPlans(plans);
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
    	
    	if(plan.getParentId() == null || "".equals(plan.getParentId())){
    		mv.setViewName("pages/planDecompose/decompose_main");
    	}else{
    		mv.setViewName("pages/planDecompose/decompose_sub");
    	}
    	mv.addObject("plan", plan);
		return mv;
    }
    
    /**
     * @description: 选择新增分解计划的位置
     * @param planId
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/chose_addDecom_location")
    public ModelAndView choseAddDecomLocation(String planId){
    	ModelAndView mv = new ModelAndView("pages/planDecompose/chose_addDecom_location");
    	Plan plan = planService.findById(Integer.parseInt(planId));
		mv.addObject("plan", plan);
		return mv;
    }
    
    /**
     * @description: 添加分解计划
     * @param planId
     * @param
     * @return 
     * @throws
     */
    @RequestMapping("/add_subDecom")
    public ModelAndView addSubDecom(String planId, String planLocation){
    	ModelAndView mv = new ModelAndView("pages/planDecompose/add_subDecom");
    	Plan plan = planService.findById(Integer.parseInt(planId));
    	Plan subPlan = new Plan();
    	if(planLocation != null && !"".equals(planLocation)){
    		subPlan.setParentId(plan.getParentId());
    		mv.addObject("planLocation", planLocation);
    		mv.addObject("currentHandlePlanId", plan.getPlanId());
    		mv.addObject("currentHandlePlanName", plan.getPlanName());
    	}else{
    		subPlan.setParentId(plan.getPlanId());
    	}
    	subPlan.setPlanType(plan.getPlanType());
		mv.addObject("subPlan", subPlan);
    	return mv;
    }
    
    /**
     * @description: 保存分解计划
     * @param request
     * @param subPlan
     * @param planId
     * @param currentHandlePlanId
     * @param planLocation
     * @param 
     * @return 
     * @throws ParseException
     */
    @RequestMapping("/save_subDecom")
	public ModelAndView saveSubDecom(HttpServletRequest request, Plan subPlan, PlanAssess planAssess, PlanTemplateModel planTemplateModel, String planId, String currentHandlePlanId, 
			String planLocation) throws ParseException{
		ModelAndView mv = new ModelAndView();
		String flag = "";
		User user = (User) CacheManager.getCacheInfo("user").getValue();
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
			flag = "保存编辑成功！";
		} else {
			Date createTime = new Date();
			subPlan.setCreateTime(createTime);
			if(user != null){
				subPlan.setCreatePerson(user.getUserName());
			}
			List<Plan> subPlans = planService.findSubPlans(subPlan.getParentId());
			
			//根据添加位置设置新增子计划的 planOrder
			if(planLocation != null && !"".equals(planLocation)){
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
			} else {
				subPlan.setPlanOrder(subPlans.size() + 1);
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
			flag = "新增分解计划成功！";
		}
		
		Plan plan = planService.findById(subPlan.getParentId());
		List<Plan> subList = planService.findSubPlans(plan.getPlanId());
		if(subList != null && subList.size() > 0)
			plan.setSubPlans(subList);
		
		if(plan.getParentId() == null || "".equals(plan.getParentId())){
    		mv.setViewName("pages/planDecompose/decompose_main");
    	}else{
    		mv.setViewName("pages/planDecompose/decompose_sub");
    	}
		mv.addObject("plan", plan);
		mv.addObject("flag", flag);
		return mv;
	}
    
    /**
     * @description: 修改分解计划的时间
     * @param planId
     * @param startTime
     * @param startTimeDaily
     * @param endTime
     * @param endTimeDaily
     * @param 
     * @return 
     * @throws ParseException
     */
    @RequestMapping("/update_decomTime")
    public ModelAndView updateDecomTime(String planId, String startTime, String startTimeDaily, 
    		String endTime, String endTimeDaily) throws ParseException{
    	ModelAndView mv = new ModelAndView();
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
		
		plan = planService.findById(plan.getParentId());
		List<Plan> subList = planService.findSubPlans(plan.getPlanId());
		if(subList != null && subList.size() > 0)
			plan.setSubPlans(subList);
		
		if(plan.getParentId() == null || "".equals(plan.getParentId())){
    		mv.setViewName("pages/planDecompose/decompose_main");
    	}else{
    		mv.setViewName("pages/planDecompose/decompose_sub");
    	}
		mv.addObject("plan", plan);
		mv.addObject("flag", flag);
    	return mv;
    }
    
    /**
	 * @description: 编辑分解计划页面跳转
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@RequestMapping("/edit_subDecom")
	public ModelAndView editSubDecom(String planId){
		ModelAndView mv = new ModelAndView("pages/planDecompose/add_subDecom");
		Plan subPlan = planService.findById(Integer.parseInt(planId));
		Plan parentPlan = planService.findById(subPlan.getParentId());
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
		
		mv.addObject("subPlan", subPlan);
		mv.addObject("parentPlanName", parentPlan.getPlanName());
		Plan plan = getTopPlan(subPlan.getPlanId());
		mv.addObject("loopMode", plan.getLoopMode());
		mv.addObject("loopRate", plan.getLoopRate());
		return mv;
	}
    
    /**
	 * @description: 删除分解计划
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@RequestMapping("/delete_subDecom")
	public ModelAndView deleteSubDecom(String planId){
		ModelAndView mv = new ModelAndView();
		String flag = "删除成功！";
		//删除分解计划及其子计划
		Plan subPlan = planService.findById(Integer.parseInt(planId));
		planService.deletePlanById(subPlan.getPlanId());
		planAssessService.deleteByPlanId(subPlan.getPlanId());
		List<Plan> subPlanList = planService.findSubPlans(subPlan.getPlanId());
		if(subPlanList != null && subPlanList.size()>0){
			for (Plan p : subPlanList) {
				planService.deletePlanById(p.getPlanId());
				planAssessService.deleteByPlanId(p.getPlanId());
				List<Plan> subList = planService.findSubPlans(p.getPlanId());
				if(subList != null && subList.size() > 0){
					for (Plan pp : subList) {
						planService.deletePlanById(pp.getPlanId());
						planAssessService.deleteByPlanId(pp.getPlanId());
						List<Plan> subSubList = planService.findSubPlans(pp.getPlanId());
						if(subSubList != null && subSubList.size() > 0){
							for (Plan ppp : subSubList) {
								planService.deletePlanById(ppp.getPlanId());
								planAssessService.deleteByPlanId(ppp.getPlanId());
							}
						}
					}
				}
			}
		}
		
		Plan plan = planService.findById(subPlan.getParentId());
		List<Plan> list = planService.findSubPlans(plan.getPlanId());
		if(list != null && list.size() > 0)
			plan.setSubPlans(list);
		
		if(plan.getParentId() == null || "".equals(plan.getParentId())){
    		mv.setViewName("pages/planDecompose/decompose_main");
    	}else{
    		mv.setViewName("pages/planDecompose/decompose_sub");
    	}
		mv.addObject("flag", flag);
		mv.addObject("plan", plan);
		return mv;
	}
    
	/**
	 * @description: 保存分解草稿
	 * @param planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/save")
	public ModelAndView saveDecomPlan(String planId, String planType, String flag){
		ModelAndView mv = new ModelAndView("pages/planDecompose/plan_decompose_list");
		String success = null;
		//根据flag标识操作分解计划
		Plan handlePlan = new Plan();
		if("draft".equals(flag)){
			//保存草稿
			handlePlan.setPlanId(Integer.parseInt(planId));
			handlePlan.setDecomStatus(1);
			planService.updatePlan(handlePlan);
			success = "保存草稿成功！";
		}else if("release".equals(flag)){
			//发布任务
			handlePlan.setPlanId(Integer.parseInt(planId));
			handlePlan.setDecomStatus(2);
			planService.updatePlan(handlePlan);
			List<Plan> subList = planService.findSubPlans(handlePlan.getPlanId());
			for (Plan p : subList) {
				p.setDecomStatus(3);
				planService.updatePlan(p);
			}
			success = "发布成功！";
		}else if("submit".equals(flag)){
			//提交审批
			handlePlan.setPlanId(Integer.parseInt(planId));
			handlePlan.setStatus(2);
			handlePlan.setDecomStatus(0);
			planService.updatePlan(handlePlan);
			success = "提交审批成功！";
		}
		
		User user = (User) CacheManager.getCacheInfo("user").getValue();
    	Plan plan = new Plan();
    	plan.setBlamePerson(user.getName());
    	plan.setPlanType(Integer.parseInt(planType));
		//分页查询
		int pageSize = 10;
		int pageCurrent = 1;
		int rows = planService.getDecomPlancount(plan);
		int pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> decomPlans = planService.getDecomPlans(plan);
    	
		if(decomPlans != null && decomPlans.size() > 0){
			mv.addObject("decomPlans", decomPlans);
		}
		
		mv.addObject("planType", planType);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		mv.addObject("success",success);
		return mv;
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
