package com.manhui.controller.planCooperation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Accessory;
import com.manhui.model.ActionPlan;
import com.manhui.model.Office;
import com.manhui.model.User;
import com.manhui.model.planCooperation.Cooperation;
import com.manhui.model.planCooperation.SkipInfo;
import com.manhui.service.AccessoryService;
import com.manhui.service.ActionPlanService;
import com.manhui.service.planCooperation.CooperationService;
import com.manhui.service.planCooperation.SkipInfoService;
import com.manhui.util.FileUploadUtil;

import net.minidev.json.JSONObject;

/**
 * @ClassName: CooperationController
 * @description: 计划协作相关控制器
 * @author:	HeJiayan
 * @date Create in 下午2:02:23 2017年11月28日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class CooperationController {
	
	@Autowired
    private CooperationService coopService;
	@Autowired
    private ActionPlanService apService;
	@Autowired
    private SkipInfoService skipService;
	@Autowired
    private AccessoryService acceService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * @description: 分页查询协作申请列表
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param coop
	 * @param startTime
	 * @param endTime
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/launch_cooperation_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView launchCooperation(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Cooperation coop, String startTime, String endTime, String flag){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopApply/coop_list");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		Map<String,Object> criteria = new HashMap<String, Object>();
		criteria.put("applicantId", user.getUserId());
		if(coop.getTheme() != null && !"".equals(coop.getTheme()))
			criteria.put("theme", "%"+coop.getTheme()+"%");
		if(coop.getCollaborator() != null && !"".equals(coop.getCollaborator()))
			criteria.put("collaborator", "%"+coop.getCollaborator()+"%");
		if(coop.getExecutor() != null && !"".equals(coop.getExecutor()))
			criteria.put("executor", "%"+coop.getExecutor()+"%");
		criteria.put("status", coop.getStatus());
		criteria.put("startTime", startTime);
		criteria.put("endTime", endTime);
		
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = coopService.countApplyList(criteria);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		
		//条件查询所有协作申请列表
		criteria.put("start", (pageCurrent - 1)*pageSize);
		criteria.put("end", pageSize);
		List<Cooperation> coopList = coopService.findApplyList(criteria);
		
		if(coopList != null && coopList.size() > 0)
			mv.addObject("coopList", coopList);
		if(flag != null && "save".equals(flag)){
			flag = "提交申请成功！";
			mv.addObject("flag", flag);
		}else if(flag != null && "delete".equals(flag)){
			flag = "删除成功！";
			mv.addObject("flag", flag);
		}
		mv.addObject("coop", coop);
		mv.addObject("startTime", startTime);
		mv.addObject("endTime", endTime);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 新增协作申请页面跳转
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/add_coop")
	public ModelAndView addCooperation(){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopApply/add_coop");
		ActionPlan ap = new ActionPlan();
		Cooperation coop = new Cooperation();
		SkipInfo skip = new SkipInfo();
		mv.addObject("ap", ap);
		mv.addObject("coop", coop);
		mv.addObject("skip", skip);
		return mv;
	}
	
	/**
	 * @description: 选择协作人或者执行人
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/chose_collaborator_or_executor")
	public ModelAndView choseCollaboratorOrExecutor(String flag){
		ModelAndView mv = new ModelAndView();
		List<Office> CQList = (List<Office>) CacheManager.getCacheInfo("allOfficeAndEmp").getValue();
		
		if(flag != null && "collaborator".equals(flag)){
			mv.setViewName("pages/planCooperation/coopApply/chose_collaborator");
		}else if(flag != null && "executor".equals(flag)){
			mv.setViewName("pages/planCooperation/coopHandle/chose_executor");
		}
		
		mv.addObject("CQList", CQList);
		return mv;
	}
	
	/**
	 * @description: 选择协作计划
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/chose_plan_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView chosePlan(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			String actionPlanName){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopApply/chose_plan");
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		Map<String,Object> criteria = new HashMap<String, Object>();
		criteria.put("blamePerson", user.getName());
		if(actionPlanName != null && !"".equals(actionPlanName))
			criteria.put("actionPlanName", "%"+actionPlanName+"%");
		
		//分页判断
		if(pageSize == 0) pageSize = 8;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = apService.countExecutingPlans(criteria);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		
		//条件查询对应负责人的所有执行中计划
		criteria.put("start", (pageCurrent - 1)*pageSize);
		criteria.put("end", pageSize);
		List<ActionPlan> apList = apService.findExecutingPlans(criteria);
		
		if(apList != null && apList.size() > 0)
			mv.addObject("apList", apList);
		mv.addObject("actionPlanName",actionPlanName);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 查询所选执行计划
	 * @param apId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/find_action_plan")
	@ResponseBody
	public ActionPlan findActionPlan(String apId){
		ActionPlan ap = new ActionPlan();
		if(apId != null && !"".equals(apId))
			ap = apService.findActionPlanByPlanId(Integer.parseInt(apId));
		return ap;
	}
	
	/**
	 * @description: 保存新增协作申请信息
	 * @param coop
	 * @param coopId
	 * @param 
	 * @return 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/save_cooperation")
	public String saveCooperation(Cooperation coop, String coopId){
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		List<User> UList = (List<User>) CacheManager.getCacheInfo("allUser").getValue();
		List<Office> officeList = (List<Office>) CacheManager.getCacheInfo("officeList").getValue();
		
		Date applyTime = new Date();
		if(coopId != null && !"".equals(coopId)){
			coop = coopService.selectByPrimaryKey(coop.getCoopId());
			coop.setRejectId(null);
			coop.setRejectPerson(null);
			coop.setRejectReason(null);
			coop.setRejectTime(null);
			List<SkipInfo> skipInfos = skipService.findSkipInfosByCoopId(coop.getCoopId());
			if(skipInfos.size() > 0){
				for (SkipInfo skipInfo : skipInfos) {
					skipInfo.setStatus(1);
					skipService.updateByPrimaryKeySelective(skipInfo);
				}
			}
				
			coop.setApplyTime(applyTime);
			coop.setStatus(1);
			coopService.updateByPrimaryKey(coop);
		}else{
			coop.setApplyTime(applyTime);
			coop.setApplicantId(loginUser.getUserId());
			coop.setApplicant(loginUser.getName());
			coop.setApplicantCompany("重庆满惠");
			
			for (User user : UList) {
				if(user.getName().equals(coop.getCollaborator()))
					coop.setCollaboratorPost(user.getPostName());
				if(user.getUserId().equals(loginUser.getUserId()))
					loginUser = user;
			}
			String applicantOffice = loginUser.getOffice().getName();
			for (Office office : officeList) {
				if(office.getId().equals(loginUser.getOffice().getId()) && office.getParentId() != null
						&& !"".equals(office.getParentId()) && !"总经办".equals(office.getName())){
					for (Office o : officeList) {
						if(o.getId().equals(office.getParentId())){
							applicantOffice = o.getName() + "-" + applicantOffice;
							if(o.getParentId() != null && !"".equals(o.getParentId()) && !"总经办".equals(o.getName())){
								for (Office oo : officeList) {
									if(oo.getId().equals(o.getParentId()))
										applicantOffice = oo.getName() + "-" + applicantOffice;
								}
							}
						}
					}
				}
			}
			
			coop.setApplicantOffice(applicantOffice);
			coop.setStatus(1);
			coopService.insertSelective(coop);
		}
		return "redirect:/launch_cooperation_0_10_0?flag=save";
	}
	
	/**
	 * @description: 编辑或者查看协作详情页面跳转
	 * @param coopId
	 * @param flag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/edit_or_check_cooperation")
	public ModelAndView editOrCheckCooperation(String coopId, String flag){
		ModelAndView mv = new ModelAndView();
		Cooperation coop = new Cooperation();
		ActionPlan ap = new ActionPlan();
		SkipInfo skip = new SkipInfo();
		if(coopId != null && !"".equals(coopId)){
			coop = coopService.selectByPrimaryKey(Integer.parseInt(coopId));
			ap = apService.findActionPlanByPlanId(coop.getApId());
			List<SkipInfo> skipInfos = skipService.findSkipInfosByCoopId(coop.getCoopId());
			if(skipInfos.size() > 0){
				coop.setSkipInfos(skipInfos);
				skip = skipInfos.get(skipInfos.size() - 1);
			}
			
			if(coop.getStatus() == 6){
				Accessory acce = new Accessory();
				acce.setModel("cooperation");
				acce.setModelId(coop.getCoopId());
				acce.setStatus(0);
				List<Accessory> acceList = acceService.selectByAccessorySelective(acce);
				
				if(acceList.size() > 0)
					mv.addObject("acceList", acceList);
			}
		}
		
		if("edit".equals(flag)){
			mv.setViewName("pages/planCooperation/coopApply/add_coop");
		}else if("check".equals(flag)){
			mv.setViewName("pages/planCooperation/coopApply/check_detail");
		}
		mv.addObject("coop", coop);
		mv.addObject("ap", ap);
		mv.addObject("skip", skip);
		return mv;
	}
	
	/**
	 * @description: 删除协作记录
	 * @param coopId
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/delete_cooperation")
	public String deleteCooperation(String coopId){
		if(coopId != null && !"".equals(coopId)){
			List<SkipInfo> skipInfos = skipService.findSkipInfosByCoopId(Integer.parseInt(coopId));
			if(skipInfos.size() > 0){
				for (SkipInfo skipInfo : skipInfos) {
					skipService.deleteByPrimaryKey(skipInfo.getSkipId());
				}
			}
			coopService.deleteByPrimaryKey(Integer.parseInt(coopId));
		}
		return "redirect:/launch_cooperation_0_10_0?flag=delete";
	}
	
	/**
	 * @description: 查看计划详情
	 * @param apId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/check_plan_detail")
	public ModelAndView checkPlanDetail(String apId){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopApply/action_plan_detail");
		ActionPlan plan = new ActionPlan();
		if(apId != null && !"".equals(apId)){
			plan = apService.findActionPlanByPlanId(Integer.parseInt(apId));
			if(plan.getActionParentId() != null && !"".equals(plan.getActionParentId())){
				plan = apService.findActionPlanByPlanId(plan.getActionParentId());
				if(plan.getActionParentId() != null && !"".equals(plan.getActionParentId())){
					plan = apService.findActionPlanByPlanId(plan.getActionParentId());
					if(plan.getActionParentId() != null && !"".equals(plan.getActionParentId())){
						plan = apService.findActionPlanByPlanId(plan.getActionParentId());
						if(plan.getActionParentId() != null && !"".equals(plan.getActionParentId())){
							plan = apService.findActionPlanByPlanId(plan.getActionParentId());
						}
					}
				}
			}
			List<ActionPlan> subPlans = apService.findSubActionPlan(plan.getActionPlanId());
			if(subPlans.size() > 0){
				plan.setSubPlanList(subPlans);
				plan.setSubSum(subPlans.size());
				for (ActionPlan p : subPlans) {
					List<ActionPlan> subSubPlans = apService.findSubActionPlan(p.getActionPlanId());
					if(subSubPlans.size() > 0){
						p.setSubPlanList(subSubPlans);
						p.setSubSum(subSubPlans.size());
						for (ActionPlan pp : subSubPlans) {
							List<ActionPlan> subSubSubPlans = apService.findSubActionPlan(pp.getActionPlanId());
							if(subSubSubPlans.size() > 0){
								pp.setSubPlanList(subSubSubPlans);
								pp.setSubSum(subSubSubPlans.size());
								for (ActionPlan ppp : subSubSubPlans) {
									List<ActionPlan> subSubSubSubPlans = apService.findSubActionPlan(ppp.getActionPlanId());
									if(subSubSubSubPlans.size() > 0){
										ppp.setSubPlanList(subSubSubSubPlans);
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
		mv.addObject("locationApId", apId);
		return mv;
	}
	
	/**
	 * @description: 分页查询协作处理列表
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param coop
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cooperation_handle_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView cooperationHandle(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Cooperation coop, String dealFlag, String flag){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopHandle/coop_handle_list");
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		Map<String,Object> criteria = new HashMap<String, Object>();
		criteria.put("collaboratorId", loginUser.getUserId());
		criteria.put("handlerId", loginUser.getUserId());
		if(coop.getTheme() != null && !"".equals(coop.getTheme()))
			criteria.put("theme", "%"+coop.getTheme()+"%");
		if(coop.getApplicant() != null && !"".equals(coop.getApplicant()))
			criteria.put("applicant", "%"+coop.getApplicant()+"%");
		
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = coopService.countHandleList(criteria);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		
		//条件查询所有协作处理列表
		criteria.put("start", (pageCurrent - 1)*pageSize);
		criteria.put("end", pageSize);
		List<Cooperation> coopList = coopService.findHandleList(criteria);
		if(coopList != null && coopList.size() > 0){
			for (Cooperation cooperation : coopList) {
				if(cooperation.getStatus() == 2){
					List<SkipInfo> skipInfos = skipService.findSkipInfosByCoopId(cooperation.getCoopId());
					SkipInfo skip = skipInfos.get(skipInfos.size() - 1);
					if(skip.getHandlerId().equals(loginUser.getUserId())){
						cooperation.setFlag("skipIn");
					}else{
						cooperation.setFlag("skipOut");
					}
				}
			}
			if(dealFlag != null && !"".equals(dealFlag)){
				if(dealFlag.equals("待处理")){
					List<Cooperation> newList = new ArrayList<Cooperation>();
					for (Cooperation cooperation : coopList) {
						if(cooperation.getStatus() == 1 || cooperation.getFlag().equals("skipIn"))
							newList.add(cooperation);
					}
					mv.addObject("coopList", newList);
				}else if(dealFlag.equals("已跳级")){
					List<Cooperation> newList = new ArrayList<Cooperation>();
					for (Cooperation cooperation : coopList) {
						if(cooperation.getFlag() != null && cooperation.getFlag().equals("skipOut"))
							newList.add(cooperation);
					}
					mv.addObject("coopList", newList);
				}
			}else{
				mv.addObject("coopList", coopList);
			}
		}
		
		if(flag != null && "reject".equals(flag)){
			flag = "拒绝成功！";
			mv.addObject("flag", flag);
		}else if(flag != null && "accept".equals(flag)){
			flag = "接受成功！";
			mv.addObject("flag", flag);
		}
		
		mv.addObject("coop", coop);
		mv.addObject("dealFlag", dealFlag);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		mv.addObject("loginUser", loginUser);
		return mv;
	}
	
	/**
	 * @description: 处理协作申请或者查看协作详情
	 * @param coopId
	 * @param flag
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/deal_or_check_coop")
	public ModelAndView dealOrCheckCoop(String coopId, String flag){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopHandle/coop_deal_or_check");
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		
		Cooperation coop = new Cooperation();
		ActionPlan ap = new ActionPlan();
		//跳级处理人的跳级信息
		SkipInfo skip = new SkipInfo();  
		//未处理人的跳级信息
		SkipInfo lastSkip = new SkipInfo();
		
		if(coopId != null && !"".equals(coopId)){
			coop = coopService.selectByPrimaryKey(Integer.parseInt(coopId));
			ap = apService.findActionPlanByPlanId(coop.getApId());
			List<SkipInfo> skipInfos = skipService.findSkipInfosByCoopId(coop.getCoopId());
			if(skipInfos.size() > 0){
				coop.setSkipInfos(skipInfos);
				//获取最新的跳级信息
				SkipInfo newSkip = skipInfos.get(skipInfos.size() - 1);
				if(newSkip.getHandlerId().equals(loginUser.getUserId())){
					coop.setFlag("skipIn");
					if(skipInfos.size() > 1){
						lastSkip = skipInfos.get(skipInfos.size() - 2);
						lastSkip.setSkipTime(newSkip.getSkipTime());
						mv.addObject("lastSkip", lastSkip);
					}else{
						lastSkip.setSkipHandler(coop.getCollaborator());
						lastSkip.setHandlerBelong(coop.getCollaboratorCompany()+"-"+coop.getCollaboratorOffice());
						lastSkip.setSkipPost(coop.getCollaboratorPost());
						lastSkip.setSkipTime(newSkip.getSkipTime());
						mv.addObject("lastSkip", lastSkip);
					}
				}else{
					coop.setFlag("skipOut");
					if(skipInfos.size() == 1){
						skip = skipInfos.get(0);
						mv.addObject("skip", skip);
					}else if(skipInfos.size() == 2){
						if(loginUser.getUserId().equals(coop.getCollaboratorId())){
							skip = skipInfos.get(0);
							mv.addObject("skip", skip);
						}else{
							skip = skipInfos.get(1);
							lastSkip.setSkipHandler(coop.getCollaborator());
							lastSkip.setHandlerBelong(coop.getCollaboratorCompany()+"-"+coop.getCollaboratorOffice());
							lastSkip.setSkipPost(coop.getCollaboratorPost());
							lastSkip.setSkipTime(skipInfos.get(0).getSkipTime());
							mv.addObject("skip", skip);
							mv.addObject("lastSkip", lastSkip);
						}
					}else{
						for (int i = 0; i < skipInfos.size(); i++) {
							if(loginUser.getUserId().equals(skipInfos.get(i).getHandlerId())){
								skip = skipInfos.get(i+1);
								lastSkip = skipInfos.get(i-1);
								lastSkip.setSkipTime(skipInfos.get(i).getSkipTime());
								mv.addObject("skip", skip);
								mv.addObject("lastSkip", lastSkip);
							}
						}
					}
				}
			}
		}
		
		mv.addObject("coop", coop);
		mv.addObject("ap", ap);
		mv.addObject("flag", flag);
		return mv;
	}
	
	/**
	 * @description: 拒绝协作请求
	 * @param coopId
	 * @param rejectReason
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/reject_coop")
	public String rejectCoop(String coopId, String rejectReason){
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		Date rejectTime = new Date();
		if(coopId != null && !"".equals(coopId)){
			Cooperation coop = coopService.selectByPrimaryKey(Integer.parseInt(coopId));
			coop.setRejectId(loginUser.getUserId());
			coop.setRejectPerson(loginUser.getName());
			coop.setRejectReason(rejectReason);
			coop.setRejectTime(rejectTime);
			coop.setStatus(3);
			coopService.updateByPrimaryKeySelective(coop);
		}
		return "redirect:/cooperation_handle_0_10_0?flag=reject";
	}
	
	/**
	 * @description: 接受协作请求
	 * @param coop
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/accept_coop")
	public String acceptCoop(Cooperation coop){
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		coop.setAssignorId(loginUser.getUserId());
		coop.setAssignor(loginUser.getName());
		coop.setStatus(4);
		coopService.updateByPrimaryKeySelective(coop);
		return "redirect:/cooperation_handle_0_10_0?flag=accept";
	}
	
	/**
	 * @description: 查询协作执行列表
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param coop
	 * @param startTime
	 * @param endTime
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cooperation_execute_{pageCurrent}_{pageSize}_{pageCount}")
	public ModelAndView cooperationExecute(@PathVariable Integer pageCurrent, @PathVariable Integer pageSize,@PathVariable Integer pageCount,
			Cooperation coop, String startTime, String endTime, String flag){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopExecute/coop_execute_list");
		User loginUser = (User) CacheManager.getCacheInfo("user").getValue();
		Map<String,Object> criteria = new HashMap<String, Object>();
		criteria.put("executorId", loginUser.getUserId());
		if(coop.getTheme() != null && !"".equals(coop.getTheme()))
			criteria.put("theme", "%"+coop.getTheme()+"%");
		if(coop.getApplicant() != null && !"".equals(coop.getApplicant()))
			criteria.put("applicant", "%"+coop.getApplicant()+"%");
		if(coop.getAssignor() != null && !"".equals(coop.getAssignor()))
			criteria.put("assignor", "%"+coop.getAssignor()+"%");
		criteria.put("status", coop.getStatus());
		criteria.put("startTime", startTime);
		criteria.put("endTime", endTime);
		
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = coopService.countExecuteList(criteria);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		
		//条件查询所有协作处理列表
		criteria.put("start", (pageCurrent - 1)*pageSize);
		criteria.put("end", pageSize);
		List<Cooperation> coopList = coopService.findExecuteList(criteria);
		
		if(flag != null && "complete".equals(flag)){
			flag = "完成成功！";
			mv.addObject("flag", flag);
		}
		if(coopList != null && coopList.size() > 0)
			mv.addObject("coopList", coopList);
		mv.addObject("coop", coop);
		mv.addObject("startTime", startTime);
		mv.addObject("endTime", endTime);
		mv.addObject("pageCurrent",pageCurrent);
		mv.addObject("pageSize",pageSize);
		mv.addObject("pageCount",pageCount);
		mv.addObject("rows",rows);
		return mv;
	}
	
	/**
	 * @description: 完成协作或者查看协作详情页面跳转
	 * @param coopId
	 * @param flag
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/complete_or_check_cooperation")
	public ModelAndView completeOrCheckCooperation(String coopId, String flag){
		ModelAndView mv = new ModelAndView("pages/planCooperation/coopExecute/coop_execute_or_check");
		Cooperation coop = new Cooperation();
		ActionPlan ap = new ActionPlan();
		if(coopId != null && !"".equals(coopId)){
			coop = coopService.selectByPrimaryKey(Integer.parseInt(coopId));
			ap = apService.findActionPlanByPlanId(coop.getApId());
		}
		Accessory acce = new Accessory();
		acce.setModel("cooperation");
		acce.setModelId(Integer.parseInt(coopId));
		acce.setStatus(0);
		List<Accessory> acceList = acceService.selectByAccessorySelective(acce);
		
		if(acceList.size() > 0)
			mv.addObject("acceList", acceList);
		mv.addObject("coop", coop);
		mv.addObject("ap", ap);
		mv.addObject("flag", flag);
		return mv;
	}
	
	/**
	 * @description: 完成协作
	 * @param coopId
	 * @param remark
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/complete_cooperation")
	public String completeCooperation(String coopId, String remark){
		Date actualFinishTime = new Date();
		Cooperation coop = new Cooperation();
		
		coop.setCoopId(Integer.parseInt(coopId));
		coop.setActualFinishTime(actualFinishTime);
		coop.setRemark(remark);
		coop.setStatus(6);
		coopService.updateByPrimaryKeySelective(coop);
		return "redirect:/cooperation_execute_0_10_0?flag=complete";
	}
	
	/**
	 * @description: 取消完成，删除已上传的附件
	 * @param coopId
	 * @param
	 * @return 
	 * @throws
	 */
	@RequestMapping("/cancel_complete")
	public String cancelComplete(String coopId){
		Accessory acce = new Accessory();
		acce.setModel("cooperation");
		acce.setModelId(Integer.parseInt(coopId));
		acceService.deleteByModelAndId(acce);
		return "redirect:/cooperation_execute_0_10_0.action";
	}
	
	/**
	 * @description: 上传计划协作附件
	 * @param request
	 * @param coopId
	 * @param remark
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping(value = "/upload/cooperationAccessory", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject completeCooperation(HttpServletRequest request, MultipartFile file, String coopId){
		JSONObject json = new JSONObject();
		String uploadDir = "upload/cooperationAccessory/";  
		
		//上传文件，转存到服务器
		boolean flag = FileUploadUtil.upload(request, file, uploadDir);
		if(flag == true){
			Date upTime = new Date();
			String fileName = file.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  
			String size = FileUploadUtil.FormetFileSize(file.getSize());
			
			Accessory acce = new Accessory();
			acce.setModel("cooperation");
			acce.setModelId(Integer.parseInt(coopId));
			acce.setName(fileName);
			acce.setSize(size);
			acce.setSuffix("."+suffix);
			acce.setUpTime(upTime);
			acce.setPath(uploadDir);
			acceService.insertSelective(acce);
			
			json.put("code", 0);
			json.put("msg", "上传成功！");
		}else{
			json.put("code", 1);
			json.put("msg", "上传失败！");
		}
		return json;
	}
	
	/**
	 * @description: 下载附件文件
	 * @param request
	 * @param response
	 * @param id
	 * @return 
	 * @throws
	 */
	@RequestMapping("/download_accessory")
	public void downloadAccessory(HttpServletRequest request, HttpServletResponse response, String id){
		Accessory acce = acceService.selectByPrimaryKey(Integer.parseInt(id));
		String filePath = acce.getPath();     
		String fileName = acce.getName();
		
		FileUploadUtil.download(request, response, fileName, filePath);
	}
}
