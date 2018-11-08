package com.manhui.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import com.manhui.model.ActionPlan;
import com.manhui.model.Log;
import com.manhui.model.Reminders;
import com.manhui.model.User;
import com.manhui.service.ActionPlanService;
import com.manhui.service.LogService;
import com.manhui.service.RemindersService;
import com.manhui.util.PageUtil;


/**
 * @ClassName: ImplementPlanController
 * @description: 我执行的计划控制器
 * @author:	jiangxiaosong	
 * @date Create in 上午12:04:25 2017年10月21日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class ImplementPlanController {
	
	
	@Autowired
    private ActionPlanService actionPlanService;
	
	@Autowired
    private LogService logService;
	
	@Autowired
    private RemindersService remindersService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	 }
	
	
	
	/**
	 * @description: 查询我执行的计划
	 * @param model
	 * @param implementPlan
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @return
	 * @throws ParseException 
	 */
	@GetMapping("/findImplementPlan_{pageCurrent}_{pageSize}_{pageCount}")
	public String findImplementPlan(Model model,ActionPlan plan,@PathVariable Integer pageCurrent,HttpSession httpSession,
						             @PathVariable Integer pageSize,@PathVariable Integer pageCount) throws ParseException{
		
		//取得当前登录人名称
		User user = (User)httpSession.getAttribute("user");
		
		//转换子计划模糊查询
		String planName = plan.getActionPlanName();
		if(planName != null){
			planName = "%"+planName+"%";
			plan.setActionPlanName(planName);
		}
		
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = actionPlanService.implementPlanCount(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有负责的子计划
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<ActionPlan> actionPlans = actionPlanService.findImplementPlanLoadPage(plan);
		
		//解析子计划重新装入显示实体类中
		ActionPlan implementPlanView = new ActionPlan();
		List<ActionPlan> implementPlanViewList = new ArrayList<>();
		//将所有子计划的主计划信息装入implementPlanView中
		for (ActionPlan plans : actionPlans) {
			Integer parentId = plans.getActionParentId();
			if (parentId == null) {
				// 父计划即主计划
				ActionPlan parentPlan = actionPlanService.findActionPlanByPlanId(parentId);
				implementPlanView.setActionPlanId(parentPlan.getActionPlanId());
				implementPlanView.setActionPlanName(parentPlan.getActionPlanName());
				implementPlanViewList.add(implementPlanView);
			}
		}
		
		//去重复操作
		for (int i = 0; i < implementPlanViewList.size(); i++) {
			for (int j = i + 1; j < implementPlanViewList.size(); j++) {
				if (implementPlanViewList.get(i).getActionPlanId() == implementPlanViewList.get(j).getActionPlanId()) {
					implementPlanViewList.remove(j);
				}
			}
		}
		
		//将主计划下面子计划查询出来放入implementPlanViewList中
		//TODO 子计划只查询了一级，后面依照测试逻辑再追加子计划级数
		if(user != null){
			List<ActionPlan> temporaryList = new ArrayList<>();
			ActionPlan temporary = new ActionPlan();
			for(ActionPlan ip : implementPlanViewList){
				//ip.setSubPlanList(subPlanList);
				//获取每个主计划ID
				Integer planId = ip.getActionPlanId();
				//取主计划的子计划，然后判断哪些子计划负责人是当前登录用户的
				List<ActionPlan> subPlans =  actionPlanService.findSubActionPlan(planId);
				if(subPlans != null && subPlans.size() > 0){
					for(ActionPlan subPlan : subPlans){
						if(subPlan.getBlamePerson().equals(user.getUserName())){
							temporary = new ActionPlan();
							temporary.setActionPlanId(subPlan.getActionPlanId());
							temporary.setActionPlanName(subPlan.getActionPlanName());
							temporary.setActionPlanType(subPlan.getActionPlanType());
							temporary.setActionParentId(subPlan.getActionParentId());
							temporary.setStartTime(subPlan.getStartTime());
							temporary.setEndTime(subPlan.getEndTime());
							List<ActionPlan> subPlansList = actionPlanService.findSubActionPlan(temporary.getActionPlanId());
							temporary.setSubSum(subPlansList.size());
							temporary.setComplateTime(subPlan.getComplateTime());
							temporary.setState(subPlan.getState());
							temporary.setIsAdvance(temporary.getAdjustType() == 6 ? 1 : 0);//isAdvance 1为显示提前
							temporary.setIsDelay(temporary.getAdjustType() == 7 ? 1 : 0);//isDelay 1为显示延期
							temporary.setIsAllComplate(temporary.getAdjustType() == 4 ? 1 : 0); //isAllComplate 1为显示全部完成
							temporary.setIsReminders(temporary.getAdjustType() == 5 ? 1 : 0);//isReminders 1为显示催办
							Reminders reminders = remindersService.findRemindersByActionPlanId(subPlan.getActionPlanId());
							temporary.setIsReminders(reminders != null? 1 : 0);
							temporaryList.add(temporary);
						}
					}
				}
				ip.setSubPlanList(temporaryList);
			}
		}
		
		model.addAttribute("viewList", implementPlanViewList);
		String pageHTML = PageUtil.getPageContent("findImplementPlan_{pageCurrent}_{pageSize}_{pageCount}?"
				+ "actionPlanName="+plan.getActionPlanName()+"&actionPlanType="+plan.getActionPlanType()+"&state="+plan.getState()
				+ "&blamePerson="+plan.getBlamePerson()+"&startTime="+plan.getStartTime()+"&endTime="+plan.getEndTime(),
				pageCurrent, pageSize, pageCount);
		model.addAttribute("pageHTML",pageHTML);
		
		//return TODO 页面未出来
		return "";
	}
	
	
	/**
	 * 计划完成
	 * @param model
	 * @param subPlanId
	 * @param file
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	/*@PostMapping("/planComplate")
	public String planComplate(Model model,String subPlanId,String complateDescribe,List<PlanFile> planFiles,
			                        HttpServletRequest request,HttpSession httpSession) throws IllegalStateException, IOException{
		
		//取得当前登录人名称
		User user = (User)httpSession.getAttribute("user");
		
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//将计划完成信息保存到数据库
		ActionPlan ap = actionPlanService.findActionPlanByPlanId(Integer.parseInt(subPlanId));
		ap.setResultDescribe(complateDescribe);
		ap.setCreateTime(time);
		if(user != null){
			ap.setCreatePerson(user.getUserName());
		}
		ap.setComplateTime(sdf.format(time));
		ap.setState(8);//设置状态为8-待验收
		actionPlanService.updateActionPlan(ap);
		
		//保存文件信息到数据库
		for(PlanFile pfs:planFiles){
			PlanFile pf = new PlanFile();
			pf.setModel("plan_complate");
			pf.setModelId(ap.getActionPlanId());
			pf.setCreateTime(time);
			pf.setFileName(pfs.getFileName());
			pf.setFilePath(pfs.getFilePath());
			pf.setFileSuffix(pfs.getFileSuffix());
			pf.setState(1);
			planFileService.insertPlanFile(pf);
		}
		
		//return TODO 页面未出来
		return "redirect:findImplementPlan_0_0_0";
	}*/
	
	
	
	/**
	 * @description: 选择申请调整类型
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@GetMapping("/choseAdjustmentType")
	public String choseAdjustmentType(String planId, Model model){
		ActionPlan plan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
		model.addAttribute("plan", plan);
		//return TODO 页面未出来
		return "";
	}
	
	
	
	/**
	 * @description: 跳转到保存调整计划申请
	 * @param model
	 * @param planId
	 * @param session
	 * @return
	 */
	@GetMapping("/toSavePlanApply")
	public String toSavePlanApply(Model model,String planId,HttpSession session){

		//查询当前选中的子计划
		ActionPlan subPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
		
		//TODO 审核流程接口 - 得到流程ID
		String auditProcess = subPlan.getAuditProcess();
		
		//查询当前选中子计划得最高级计划
		Integer parentId = subPlan.getActionParentId();
		ActionPlan parentPlan = actionPlanService.findActionPlanByPlanId(parentId);
		model.addAttribute("subPlan", subPlan);
		model.addAttribute("parentPlan", parentPlan);
		
		User user = (User)session.getAttribute("user");
		model.addAttribute("userName", user.getUserName());
		
		//return TODO 页面未出来
		return "";
	}
	
	
	
	/**
	 * @description: 保存申请调整
	 * @param model
	 * @param planId
	 * @param flowId
	 * @param startTime
	 * @param endTime
	 * @param reason
	 * @param adjustType 1-延期 2-暂停 3-计划中止
	 * @return
	 *//*
	@PostMapping("/savePlanApply")
	public String savePlanApply(Model model,String actionPlanId,String auditProcess,String postponeTime,
									String postponeTimeLimit,String adjustReason,String adjustType){

		//获取执行计划
		ActionPlan subPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(actionPlanId));
		//更改当前选择子计划状态，状态更改为4.调整待审批
		subPlan.setState(4);
		
		// 保存申请调整信息
		Integer status = 0;
		if ("1".equals(adjustType)) {
			// 计划延期过来的
			status = 0;
			subPlan.setPostponeTime(postponeTime);
			subPlan.setPostponeTimeLimit(postponeTimeLimit);
		} else if ("2".equals(adjustType)) {
			// 计划暂停过来的
			status = 1;
		} else if ("3".equals(adjustType)) {
			// 计划中止过来的
			status = 2;
		}
		subPlan.setAdjustType(status);
		subPlan.setAdjustReason(adjustReason);
		subPlan.setAuditProcess(Integer.parseInt(auditProcess));
		
		actionPlanService.updateActionPlan(subPlan);
		
		//TODO 可能会走流程
		Integer actionAuditProcess = Integer.parseInt(auditProcess);
		
		//return TODO 页面未出来
		return "redirect:findImplementPlan_0_0_0";
	}*/
	
	
	//写日志跳转
	/**
	 * 日志跳转
	 * @param model
	 * @param planId
	 * @param blamePerson
	 * @return
	 */
	@GetMapping("/toWriteLog_{pageCurrent}_{pageSize}_{pageCount}")
	public String toWriteLog(Model model,String planId,String blamePerson,@PathVariable Integer pageCurrent,
			               @PathVariable Integer pageSize,@PathVariable Integer pageCount){
		
		//读取子计划详情
		ActionPlan subPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
		model.addAttribute("subPlan", subPlan);
		
		//读取日志历史
		Log logs = new Log();
		logs.setActionPlanId(Integer.parseInt(planId));
		logs.setCreatePerson(blamePerson);
		
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = logService.findLogCountByPlanIdAndCreatePerson(logs);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		logs.setStart((pageCurrent - 1)*pageSize);
		logs.setEnd(pageSize);
		
		List<Log> logHistory = logService.findLogByPlanIdAndCreatePerson(logs);
		model.addAttribute("logList", logHistory);
		String pageHTML = PageUtil.getPageContent("toWriteLog_{pageCurrent}_{pageSize}_{pageCount}?"
				+ "planId="+logs.getActionPlanId()+"&blamePerson="+logs.getCreatePerson(),
				pageCurrent, pageSize, pageCount);
		model.addAttribute("pageHTML",pageHTML);
		
		//return TODO 页面未出来
		return "";
	}
	
	
	/**
	 * 保存日志
	 * @param model
	 * @param log
	 * @param fileName
	 * @param filePath
	 * @param fileSuffix
	 * @return
	 */
	/*@PostMapping("/saveLog")
	public String saveLog(Model model,String actionPlanId,String Context,List<PlanFile> planFiles,HttpSession session){
		
		User user = (User)session.getAttribute("user");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//将日志信息录入数据库
		Log log = new Log();
		log.setLogId(logService.findMaxLogId()+1);
		log.setActionPlanId(Integer.parseInt(actionPlanId));
		log.setCreateDate(Timestamp.valueOf(sdf.format(date)));
		log.setCreatePerson(user.getUserName());
		log.setContent(Context);
		logService.saveLog(log);
		
		//存入对应的日志文件
		for(PlanFile pf : planFiles){
			PlanFile logFile = new PlanFile();
			logFile.setModel("log");
			logFile.setModelId(log.getLogId());
			logFile.setCreateTime(date);
			logFile.setFileName(pf.getFileName());
			logFile.setFilePath(pf.getFilePath());
			logFile.setFileSuffix(pf.getFileSuffix());
			logFile.setState(1);
			planFileService.insertPlanFile(logFile);
		}
		
		return "redirect:findImplementPlan_0_0_0";
	}*/
	
	
	//TODO 子计划启动(添加启动时间，变更状态为执行中)
	@PostMapping("/subPlanStratUp")
	public String subPlanStratUp(Model model,String actionPlanId){
		
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//变更执行计划的状态以及启动时间
		ActionPlan actionPlan = actionPlanService.findActionPlanByPlanId(Integer.parseInt(actionPlanId));
		actionPlan.setStartUpTime(sdf.format(time));
		actionPlan.setState(2);
		actionPlanService.updateActionPlan(actionPlan);
		return "redirect:findImplementPlan_0_0_0";
	}
	
	
	/**
	 * 查看详细
	 * @param model
	 * @param planId
	 * @param blamePerson
	 * @return
	 */
	/*@GetMapping("/toPlanDetail")
	public String toPlanDetail(Model model,String planId,String blamePerson){
		
		//查询当前项目的信息
		ActionPlan planDetail = actionPlanService.findActionPlanByPlanId(Integer.parseInt(planId));
		
		//查询下属子计划
		List<ActionPlan> subPlans = actionPlanService.findSubActionPlan(Integer.parseInt(planId));
		for(ActionPlan aps : subPlans){
			Integer type = aps.getAdjustType();
			if(type == 6){
				//提前完成
				aps.setIsAdvance(1);
			}else if(type == 7){
				//计划延期
				aps.setIsDelay(1);
			}
		}
		planDetail.setSubPlanList(subPlans);
		model.addAttribute("planDetail", planDetail);
		
		//查询完成信息的文件
		if(planDetail.getComplateTime() != null && !"".equals(planDetail.getComplateTime())){
			PlanFile file = new PlanFile();
			file.setModel("plan_complate");
			file.setModelId(planDetail.getActionPlanId());
			//查询完成信息附件
			List<PlanFile> pflist = planFileService.findPlanFileByModelId(file);
			model.addAttribute("pflist", pflist);
		}
		
		
		//查询验收结果（判断验收表是否有记录）
		
		
		
		//查询日志表
		Log log = new Log();
		log.setActionPlanId(planDetail.getActionPlanId());
		log.setCreatePerson(planDetail.getBlamePerson());
		List<Log> logList = logService.findLogByPlanIdAndCreatePerson(log); 
		model.addAttribute("logList", logList);
		if(logList != null && logList.size() > 0){
			//读取对应日志的ID的日志文件
			Integer logId = 0;
			for(Log logs : logList){
				logId = logs.getLogId();
			}
			
			PlanFile pf = new PlanFile();
			pf.setModel("log");
			pf.setModelId(logId);
			List<PlanFile> planFile = planFileService.findPlanFileByModelId(pf);
			model.addAttribute("logFiles", planFile);
		}
		
		
		//return TODO 页面未出来
		return "";
	}*/
	
}
