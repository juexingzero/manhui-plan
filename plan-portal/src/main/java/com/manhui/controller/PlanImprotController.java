package com.manhui.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.manhui.cacheManage.Cache;
import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Plan;
import com.manhui.model.User;
import com.manhui.service.PlanService;
import com.manhui.util.DateUtil;
import com.manhui.util.ExcelUtil;

/**
 * @ClassName: PlanImprotController
 * @description: 计划导入控制器
 * @author:	HeJiayan
 * @date Create in 上午11:53:52 2017年10月20日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanImprotController {
	public static Logger logger = Logger.getLogger(PlanImprotController.class);
	
	@Autowired
    private PlanService planService;
	
	/**
	 * @description: 下载计划模板
	 * @param @param request
	 * @param @param response
	 * @param @param planId
	 * @param @param session
	 * @return 
	 * @throws
	 */
	@RequestMapping("/download_template")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String planId, HttpSession session){
		String filePath = "";
		String fileName = "";
		Plan plan = planService.findById(Integer.parseInt(planId));
		if(plan.getPlanType() == 1){
			fileName = "项目类工作计划模板.xlsx";
		}else if(plan.getPlanType() == 3){
			fileName = "临时类工作计划模板.xlsx";
		}else if(plan.getPlanType() == 2){
			if(plan.getLoopMode() == 1){
				fileName = "日常类工作计划-按天循环模板.xlsx";
			}else if(plan.getLoopMode() == 2){
				fileName = "日常类工作计划-按周循环模板.xlsx";
			}else if(plan.getLoopMode() == 3){
				fileName = "日常类工作计划-按月循环模板.xlsx";
			}
		}
		
		ServletContext contex = session.getServletContext();
		filePath = contex.getRealPath("subPlanTemplate/");
		File file = null;
        BufferedInputStream input = null;
        BufferedOutputStream out = null;
        OutputStream os = null;
		try {
			response.reset();
			response.setHeader("conent-type", "application/octet-stream");  
            response.setContentType("application/octet-stream");
            response.setContentType("application/msexcel"); 
            response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName, "UTF-8"));
	        
	        file = new File(filePath+fileName);
			input = new BufferedInputStream(new FileInputStream(file));
			os = response.getOutputStream();
			out = new BufferedOutputStream(os);
			byte[] buffer = new byte[2048];
			int len;
			while((len = input.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			out.flush();
        	input.close();
        	os.close();
        	logger.info("下载成功！");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * @description: 模板数据导入
	 * @param @param excelFile
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws  
	 */
	@RequestMapping(value="/ecxel_improt",method=RequestMethod.POST)
	public void ecxelImprot(@RequestParam(value="excelFile", required=false)MultipartFile excelFile, @RequestParam(value="planId", required=true)String planId, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Plan parentPlan = planService.findById(Integer.parseInt(planId));
		List<Plan> planList = ExcelUtil.readExcelFile(excelFile,parentPlan.getPlanType());
		System.out.println(planList.size());
		
		if(planList.size() > 0 && planList != null){
			List<Plan> importList = new ArrayList<Plan>();
			for (Plan plan : planList) {
				Plan newPlan = new Plan();
				newPlan.setPlanId(plan.getPlanId());
				newPlan.setPlanName(plan.getPlanName());
				newPlan.setPlanType(plan.getPlanType());
				newPlan.setPlanDescribe(plan.getPlanDescribe());
				newPlan.setParentId(plan.getParentId());
				newPlan.setBlameOrganization(plan.getBlameOrganization());
				newPlan.setBlameSection(plan.getBlameSection());
				newPlan.setBlamePerson(plan.getBlamePerson());
				newPlan.setStartTime(plan.getStartTime());
				newPlan.setStartTimeDaily(plan.getStartTimeDaily());
				newPlan.setEndTime(plan.getEndTime());
				newPlan.setEndTimeDaily(plan.getEndTimeDaily());
				newPlan.setFinishStandard(plan.getFinishStandard());
				newPlan.setCheckItem(plan.getCheckItem());
				newPlan.setCheckManner(plan.getCheckManner());
				newPlan.setPenaltyAmount(plan.getPenaltyAmount());
				importList.add(newPlan);
			}
			
			//如果是日常类计划，处理每一条模板数据
			if(parentPlan.getPlanType() == 2){
				planList = dealWithModeData(planList,parentPlan);
			}
			
			//校验每一条模板数据
			planList = checkModeData(planList,parentPlan);
			
			//判断有错误信息的计划是否有子计划，如果有，其子计划也会提示有问题
			planList = judgeErrorMsg(planList);
			
			//如果是日常类按月循环的计划，判断每一条模板数据的计划时间是否是当月最后一天
			/*if(parentPlan.getPlanType() == 2 && parentPlan.getLoopMode() == 3){
				planList = judgeModeTime(planList,parentPlan);
			}*/
			
			//存入缓存，以便后来在错误信息查看页面使用
			Map<String, Object> importMap = new HashMap<String, Object>();
			//统计导入成功条数和失败条数
			Integer success = 0;
			for (Plan plan : planList) {
				if("".equals(plan.getErrorMsg())){ 
					success++; 
				}else{
					for (Plan importPlan : importList) {
						if(importPlan.getPlanId() == plan.getPlanId() && importPlan.getPlanName().equals(plan.getPlanName()) 
								&& importPlan.getPlanDescribe().equals(plan.getPlanDescribe()))
							importPlan.setErrorMsg(plan.getErrorMsg());
					}
				}
			}
			
			Integer failure = planList.size() - success;
			importMap.put("importList_"+parentPlan.getPlanId(), importList);
			importMap.put("total", planList.size());
			importMap.put("success", success);
			importMap.put("failure", failure);
			putIntoCache(importMap);
			
			for (Plan plan : planList) {
				System.out.println("--- "+plan.getPlanType()+"--- "+plan.getPlanId()+"--- "+plan.getPlanName()+"--- "+plan.getPlanDescribe()+"--- "+plan.getParentId()+"--- "+plan.getBlameOrganization()+"--- "+plan.getBlameSection()+"--- "+plan.getBlamePerson()+"--- "+plan.getStartTime()+"--- "+plan.getStartTimeDaily()+"--- "+plan.getEndTime()+"--- "+plan.getEndTimeDaily()+"--- "+plan.getFinishStandard()+"--- "+plan.getCheckItem()+"--- "+plan.getCheckManner()+"--- "+plan.getPenaltyAmount()+"--- "+plan.getErrorMsg());
			}
			
			//如果是项目类和临时类，计算导入计划的工期
			if(parentPlan.getPlanType() != 2){
				planList = getTimeLimit(planList);
			}
			
			//判断模板数据有无错误信息，再入库
			Date createTime = new Date();
			User user = (User) session.getAttribute("user");
			//保存没有错误信息的所有数据
			for (Plan plan : planList) {
				//没有错误，则判断父级编号是否为空
				if("".equals(plan.getErrorMsg())){
					//父级编号为空，则直接保存
					if(plan.getParentId() == null || "".equals(plan.getParentId())){
						Integer oldId = plan.getPlanId();
						plan.setPlanId(null);
						plan.setParentId(parentPlan.getPlanId());
						plan.setCreateTime(createTime);
						if(user != null){ plan.setCreatePerson(user.getUserName()); }
						planService.savePlan(plan);
						Plan newPlan = planService.findByPlan(plan);
						//找到子计划及保存
						for (Plan subPlan : planList) {
							if("".equals(subPlan.getErrorMsg()) && subPlan.getParentId() == oldId){
								Integer oldSubId = subPlan.getPlanId();
								subPlan.setPlanId(null);
								subPlan.setParentId(newPlan.getPlanId());
								subPlan.setCreateTime(createTime);
								if(user != null){ subPlan.setCreatePerson(user.getUserName()); }
								planService.savePlan(subPlan);
								Plan newSubPlan = planService.findByPlan(subPlan);
								for (Plan subSubPlan : planList) {
									if("".equals(subSubPlan.getErrorMsg()) && subSubPlan.getParentId() == oldSubId){
										Integer oldSubSubId = subSubPlan.getPlanId();
										subSubPlan.setPlanId(null);
										subSubPlan.setParentId(newSubPlan.getPlanId());
										subSubPlan.setCreateTime(createTime);
										if(user != null){ subSubPlan.setCreatePerson(user.getUserName()); }
										planService.savePlan(subSubPlan);
										Plan newSubSubPlan = planService.findByPlan(subSubPlan);
										for (Plan subSubSubPlan : planList) {
											if("".equals(subSubSubPlan.getErrorMsg()) && subSubSubPlan.getParentId() == oldSubSubId){
												subSubSubPlan.setPlanId(null);
												subSubSubPlan.setParentId(newSubSubPlan.getPlanId());
												subSubSubPlan.setCreateTime(createTime);
												if(user != null){ subSubSubPlan.setCreatePerson(user.getUserName()); }
												planService.savePlan(subSubSubPlan);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @description: 如果是项目类和临时类，计算导入计划的工期
	 * @param @param planList
	 * @param @return
	 * @return 
	 * @throws 
	 */
	private List<Plan> getTimeLimit(List<Plan> planList) {
		try {
			for (Plan plan : planList) {
				if("".equals(plan.getErrorMsg())){
					Integer timeLimit = DateUtil.daysBetween(plan.getStartTime(), plan.getEndTime());
					plan.setTimeLimit(timeLimit);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return planList;
	}

	/**
	 * @description: 判断有错误信息的计划是否有子计划，如果有，其子计划也会错误
	 * @param planList
	 * @return planList
	 * @throws 
	 */
	private List<Plan> judgeErrorMsg(List<Plan> planList) {
		for (Plan plan : planList) {
			//得到有错误的的数据
			if(plan.getErrorMsg() != null && !"".equals(plan.getErrorMsg())){
				Integer planId = plan.getPlanId();
				if(planId != null && !"".equals(planId)){
					for (Plan subPlan : planList) {
						if(subPlan.getParentId() == planId && "".equals(subPlan.getErrorMsg())){
							subPlan.setErrorMsg(subPlan.getErrorMsg()+";上级计划存在问题");
							Integer subPlanId = subPlan.getPlanId();
							if(subPlanId != null && !"".equals(subPlanId)){
								for (Plan subSubPlan : planList) {
									if(subSubPlan.getParentId() == subPlanId && "".equals(subSubPlan.getErrorMsg())){
										subSubPlan.setErrorMsg(subSubPlan.getErrorMsg()+";上级计划存在问题");
										Integer subSubPlanId = subSubPlan.getPlanId();
										if(subSubPlanId != null && !"".equals(subSubPlanId)){
											for (Plan subSubSubPlan : planList) {
												if(subSubSubPlan.getParentId() == subSubPlanId && "".equals(subSubSubPlan.getErrorMsg())){
													subSubSubPlan.setErrorMsg(subSubSubPlan.getErrorMsg()+";上级计划存在问题");
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return planList;
	}

	/**
	 * @description: 如果是日常类按月循环的计划，判断每一条模板数据的计划时间是否是当月最后一天
	 * @param @param planList
	 * @param @param parentPlan
	 * @param @return
	 * @return 
	 * @throws 
	 */
	private List<Plan> judgeModeTime(List<Plan> planList, Plan parentPlan) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		String pStartTime = parentPlan.getStartTime();
		
		try {
			calendar.setTime(sdf.parse(pStartTime));
			for (Plan plan : planList) {
				String startTimeDaily = plan.getStartTimeDaily();
				Integer std = Integer.parseInt(startTimeDaily);
				if(std >= 28){
					//得到开始时间月数
					Integer st = Integer.parseInt(plan.getStartTime());
					//得到开始时间是哪一个月
					calendar.add(Calendar.MONTH, st - 1);
					Integer month = calendar.get(Calendar.MONTH);
					if(month == 2){
						Integer yesr = calendar.get(Calendar.YEAR);
						if((yesr%4 == 0 && yesr%100 != 0) || yesr%400 == 0){
							//闰年
							if(std >= 29){ plan.setStartTimeDaily("最后一天"); }
						}else{
							//不是闰年
							if(std >= 28){ plan.setStartTimeDaily("最后一天"); }
						}
					}else if(month == 4 || month == 6 || month == 9 || month == 11){
						//小月
						if(std >= 30){ plan.setStartTimeDaily("最后一天"); }
					}else{
						//大月
						if(std == 31){ plan.setStartTimeDaily("最后一天"); }
					}
				}
				
				String ednTimeDaily = plan.getEndTimeDaily();
				Integer etd = Integer.parseInt(ednTimeDaily);
				if(etd >= 28){
					//得到结束时间月数
					Integer ed = Integer.parseInt(plan.getEndTime());
					//得到结束时间是哪一个月
					calendar.add(Calendar.MONTH, ed - 1);
					Integer month = calendar.get(Calendar.MONTH);
					if(month == 2){
						Integer yesr = calendar.get(Calendar.YEAR);
						if((yesr%4 == 0 && yesr%100 != 0) || yesr%400 == 0){
							//闰年
							if(etd >= 29){ plan.setEndTimeDaily("最后一天"); }
						}else{
							//不是闰年
							if(etd >= 28){ plan.setEndTimeDaily("最后一天"); }
						}
					}else if(month == 4 || month == 6 || month == 9 || month == 11){
						//小月
						if(etd >= 30){ plan.setEndTimeDaily("最后一天"); }
					}else{
						//大月
						if(etd == 31){ plan.setEndTimeDaily("最后一天"); }
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return planList;
	}

	/**
	 * @description: 校验每一条模板数据
	 * @param planList
	 * @param parentPlan
	 * @return 
	 * @throws 
	 */
	private List<Plan> checkModeData(List<Plan> planList, Plan parentPlan) {
		//校验计划编号
		List<Integer> list = new ArrayList<Integer>();
		for (Plan p : planList) {
			Integer pId = p.getPlanId();
			if(pId != null && !"".equals(pId)){ 
				list.add(pId); 
				p.setErrorMsg("");
			}else{
				p.setErrorMsg(";计划编号不允许为空");
			}
		}
		Integer temp = null;
		List<Integer> echolist = new ArrayList<Integer>();
		for (int i = 0; i < list.size() - 1; i++){
			temp = list.get(i);
            for (int j = i + 1; j < list.size(); j++){
            	if (temp.equals(list.get(j))){
            		Integer listSize = echolist.size();
            		if(listSize > 0){
                    	for (int k = 0; k < listSize; k++) {
    						if(echolist.get(k) != temp)
    							echolist.add(temp);
    					}
                    }else{
                    	echolist.add(temp);
                    }
                }
            }
        }
		
        for (Integer echo : echolist) {
        	for (Plan plan : planList) {
        		if(plan.getPlanId() == echo || echo.equals(plan.getPlanId())){
					plan.setErrorMsg(plan.getErrorMsg()+";不允许有重复计划编号");
				}
			}
		}
        
        //校验计划名称
        for (Plan plan : planList) {
			String planName = plan.getPlanName();
			if(planName == null || "".equals(planName)){
				plan.setErrorMsg(plan.getErrorMsg()+";计划名称不能为空");
			}else if(planName.length() > 50){
				plan.setErrorMsg(plan.getErrorMsg()+";计划名称不能超过50个字符");
			}
		}
        
        //校验计划描述
        for (Plan plan : planList) {
			String planDescribe = plan.getPlanDescribe();
			if(planDescribe != null && planDescribe.length() > 200){
				plan.setErrorMsg(plan.getErrorMsg()+";计划描述不能超过200个字符");
			}
		}
        
        //校验父级计划编号
        for (Plan plan : planList) {
			Integer parentId = plan.getParentId();
			if(parentId != null && !"".equals(parentId)){
				if(parentId == plan.getPlanId() || parentId.equals(plan.getPlanId())){
					plan.setErrorMsg(plan.getErrorMsg()+";父级编号不允许是自己的编号");
				}else if(!list.contains(parentId)){
					plan.setErrorMsg(plan.getErrorMsg()+";父级编号在该列表中不存在");
				}
			}
		}
        
        //校验机构、部门、负责人以及负责人工号
        planList = checkCharge(planList);
        
        //校验完成标准
        for (Plan plan : planList) {
			String finishStandard = plan.getFinishStandard();
			if(finishStandard != null && !"".equals(finishStandard)){
				if(finishStandard.length() > 500)
					plan.setErrorMsg(plan.getErrorMsg()+";完成标准不能超过500个字符");
			}else{
				plan.setErrorMsg(plan.getErrorMsg()+";完成标准不允许为空");
			}
		}
        
        //校验考核项、考核方式、考核金额
        for (Plan plan : planList) {
			String checkItem = plan.getCheckItem();
			String checkManner = plan.getCheckManner();
			Integer penaltyAmount = plan.getPenaltyAmount();
			if(checkItem != null && !"".equals(checkItem)){
				if(checkManner != null && !"".equals(checkManner)){
					if(!"1".equals(checkManner)){
						if(penaltyAmount == null || "".equals(penaltyAmount))
							plan.setErrorMsg(plan.getErrorMsg()+";考核方式包含了单次或者每天考核，则考核金额不能为空");
					}else{
						if(penaltyAmount != null && !"".equals(penaltyAmount))
							plan.setErrorMsg(plan.getErrorMsg()+";所选考核方式没有考核金额");
					}
				}else{
					plan.setErrorMsg(plan.getErrorMsg()+";存在考核项，则考核方式不能为空");
				}
			}else{
				if(checkManner != null && !"".equals(checkManner)){
					if(penaltyAmount != null && !"".equals(penaltyAmount)){
						plan.setErrorMsg(plan.getErrorMsg()+";请先选择考核项和考核方式，再填写考核金额");
					}else{
						plan.setErrorMsg(plan.getErrorMsg()+";请先选择考核项，再选择考核方式");
					}
				}else{
					if(penaltyAmount != null && !"".equals(penaltyAmount))
						plan.setErrorMsg(plan.getErrorMsg()+";请先选择考核项和考核方式，再填写考核金额");
				}
			}
		}

        //校验导入计划的时间
        planList = checkPlanTime(planList,parentPlan);
        
		return planList;
	}

	/**
	 * @description: 校验导入计划的时间
	 * @param planList
	 * @param parentPlan 
	 * @return 
	 * @throws 
	 */
	private List<Plan> checkPlanTime(List<Plan> planList, Plan parentPlan) {
		try {
			for (Plan plan : planList) {
				if(parentPlan.getPlanType() != 2){
					//项目类和临时类
					String startTime = plan.getStartTime();
					String endTime = plan.getEndTime();
					String pStartTime = parentPlan.getStartTime();
					String pEndTime = parentPlan.getEndTime();
					if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
						if(DateUtil.daysBetween(pStartTime, startTime) < 0){
							plan.setErrorMsg(plan.getErrorMsg()+";开始时间不能早于主计划的开始时间");
						}else{
							if(DateUtil.daysBetween(pEndTime, endTime) > 0){
								plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能晚于主计划的结束时间");
							}else{
								if(DateUtil.daysBetween(startTime, endTime) < 0){
									plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
								}
							}
						}
					}else{
						plan.setErrorMsg(plan.getErrorMsg()+";开始时间和结束时间不允许为空");
					}
				}else if(parentPlan.getPlanType() == 2 && parentPlan.getLoopMode() == 1){
					//日常类按天循环
					String startTime = plan.getStartTime();
					String startTimeDaily = plan.getStartTimeDaily();
					String endTime = plan.getEndTime();
					String endTimeDaily = plan.getEndTimeDaily();
					if(startTime != null && !"".equals(startTime) && startTimeDaily != null && !"".equals(startTimeDaily)
							&& endTime != null && !"".equals(endTime) && endTimeDaily != null && !"".equals(endTimeDaily)){
						Integer st = Integer.parseInt(startTime);
						Integer std = Integer.parseInt(startTimeDaily);
						Integer et = Integer.parseInt(endTime);
						Integer etd = Integer.parseInt(endTimeDaily);
						if(st.compareTo(et) == 1){
							plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
						}else if(st.compareTo(et) == 0){
							if(std.compareTo(etd) == 1)
								plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
						}
					}else{
						plan.setErrorMsg(plan.getErrorMsg()+";开始时间和结束时间不允许为空");
					}
				}else if(parentPlan.getPlanType() == 2 && parentPlan.getLoopMode() == 2){
					//日常类按周循环
					String startTime = plan.getStartTime();
					String startTimeDaily = plan.getStartTimeDaily();
					String endTime = plan.getEndTime();
					String endTimeDaily = plan.getEndTimeDaily();
					if(startTime != null && !"".equals(startTime) && startTimeDaily != null && !"".equals(startTimeDaily)
							&& endTime != null && !"".equals(endTime) && endTimeDaily != null && !"".equals(endTimeDaily)){
						if(startTimeDaily.equals("一")){ startTimeDaily = "1"; }
						else if(startTimeDaily.equals("二")){ startTimeDaily = "2"; }
						else if(startTimeDaily.equals("三")){ startTimeDaily = "3"; }
						else if(startTimeDaily.equals("四")){ startTimeDaily = "4"; }
						else if(startTimeDaily.equals("五")){ startTimeDaily = "5"; }
						else if(startTimeDaily.equals("六")){ startTimeDaily = "6"; }
						else if(startTimeDaily.equals("日")){ startTimeDaily = "7"; }
						
						if(endTimeDaily.equals("一")){ endTimeDaily = "1"; }
						else if(endTimeDaily.equals("二")){ endTimeDaily = "2"; }
						else if(endTimeDaily.equals("三")){ endTimeDaily = "3"; }
						else if(endTimeDaily.equals("四")){ endTimeDaily = "4"; }
						else if(endTimeDaily.equals("五")){ endTimeDaily = "5"; }
						else if(endTimeDaily.equals("六")){ endTimeDaily = "6"; }
						else if(endTimeDaily.equals("日")){ endTimeDaily = "7"; }
						
						Integer st = Integer.parseInt(startTime);
						Integer std = Integer.parseInt(startTimeDaily);
						Integer et = Integer.parseInt(endTime);
						Integer etd = Integer.parseInt(endTimeDaily);
						Integer loopRate = parentPlan.getLoopRate();
						if(st <= loopRate && et <= loopRate){
							if(st.compareTo(et) == 1){
								plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
							}else if(st.compareTo(et) == 0){
								if(std.compareTo(etd) == 1)
									plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
							}
						}else{
							plan.setErrorMsg(plan.getErrorMsg()+";的周数必须小于主计划的循环频率");
						}
					}else{
						plan.setErrorMsg(plan.getErrorMsg()+";开始时间和结束时间不允许为空");
					}
				}else if(parentPlan.getPlanType() == 2 && parentPlan.getLoopMode() == 3){
					//日常类按月循环
					String startTime = plan.getStartTime();
					String startTimeDaily = plan.getStartTimeDaily();
					String endTime = plan.getEndTime();
					String endTimeDaily = plan.getEndTimeDaily();
					if(startTime != null && !"".equals(startTime) && startTimeDaily != null && !"".equals(startTimeDaily)
							&& endTime != null && !"".equals(endTime) && endTimeDaily != null && !"".equals(endTimeDaily)){
						Integer st = Integer.parseInt(startTime);
						Integer et = Integer.parseInt(endTime);
						Integer loopRate = parentPlan.getLoopRate();
						if(st <= loopRate && et <= loopRate){
							if(st.compareTo(et) == 1){
								plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
							}else if(st.compareTo(et) == 0){
								if(!endTimeDaily.equals("最后一天")){
									Integer etd = Integer.parseInt(endTimeDaily);
									if(!startTimeDaily.equals("最后一天")){
										Integer std = Integer.parseInt(startTimeDaily);
										if(std.compareTo(etd) == 1)
											plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
									}else{
										plan.setErrorMsg(plan.getErrorMsg()+";结束时间不能早于本计划的开始时间");
									}
								}
							}
						}else{
							plan.setErrorMsg(plan.getErrorMsg()+";开始时间和结束时间的月数必须小于主计划的循环频率");
						}
					}else{
						plan.setErrorMsg(plan.getErrorMsg()+";开始时间和结束时间不允许为空");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return planList;
	}

	/**
	 * @description: 校验机构、部门、负责人以及负责人工号
	 * @param @param planList
	 * @param @return
	 * @return 
	 * @throws 
	 */
	private List<Plan> checkCharge(List<Plan> planList) {
		//校验机构
		for (Plan plan : planList) {
			String blameOrganization = plan.getBlameOrganization();
			if(blameOrganization != null && !"".equals(blameOrganization)){
				// TODO 调用查询机构接口，通过机构名判断该机构是否存在，返回结果为 Integer
				
				Integer blameOrganizationCount = 1;
				if(blameOrganizationCount == 0){
					plan.setErrorMsg(plan.getErrorMsg()+";负责机构不存在");
				}
			}else{
				plan.setErrorMsg(plan.getErrorMsg()+";负责机构不允许为空");
			}
		}
		
		//校验部门
		for (Plan plan : planList) {
			String blameSection = plan.getBlameSection();
			if(blameSection != null && !"".equals(blameSection)){
				if(!plan.getErrorMsg().contains("负责机构")){
					// TODO 调用查询部门接口，通过部门名判断该部门是否存在，返回结果为 Integer
					
					Integer blameSectionCount = 1;
					if(blameSectionCount == 0){
						plan.setErrorMsg(plan.getErrorMsg()+";负责部门不存在");
					}else{
						// TODO 调用接口，通过部门名称查询该部门的所属机构，返回一个机构对象
						
						String blameOrganization = "";
						/*if(!blameOrganization.equals(plan.getBlameOrganization())){
							plan.setErrorMsg(plan.getErrorMsg()+";所填机构下查询不到该负责部门");
						}*/
					}
				}
			}else{
				plan.setErrorMsg(plan.getErrorMsg()+";负责部门不允许为空");
			}
		}
		
		//校验负责人
		for (Plan plan : planList) {
			String blamePerson = null;
			String personAndNumber = plan.getBlamePerson();
			if(personAndNumber != null && !"".equals(personAndNumber)){
				if(!personAndNumber.contains(",")){
					blamePerson = personAndNumber;
				}else{
					String[] arry = personAndNumber.split(",");
					blamePerson = arry[0];
				}
			}
			
			if(blamePerson != null && !"".equals(blamePerson)){
				if(!plan.getErrorMsg().contains("部门")){
					// TODO 调用查询员工接口，通过负责人名称查询该负责人对应的员工信息 ，返回一个list
					
					Integer blamePersonCount = 1;  //list.size();
					if(blamePersonCount == 0){
						plan.setErrorMsg(plan.getErrorMsg()+";负责人不存在");
					}else{
						// TODO 新建一个部门list，循环上面得到的员工list，调用接口，用员工信息查询该员工的所属部门，返回一个部门对象，将该部门对象添加到新建的部门list中
						    
						//判断该计划所填部门是否存在于新建的部门list中
						String blameSection = plan.getBlameSection();
						// 如果不存在 ，则： plan.setErrorMsg(plan.getErrorMsg()+";所填部门下查询不到该负责人");
						
					}
				}
			}else{
				plan.setErrorMsg(plan.getErrorMsg()+";负责人不允许为空");
			}
		}
		
		//校验工号
		for (Plan plan : planList) {
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
			
			if(number != null && !"".equals(number)){
				if(!plan.getErrorMsg().contains("负责人")){
					// TODO 调用查询员工接口，通过工号判断该工号对应员工是否存在，返回结果为 Integer
					
					Integer numberCount = 1;
					if(numberCount == 0){
						plan.setErrorMsg(plan.getErrorMsg()+";工号不存在");
					}else{
						// TODO 调用接口，通过工号查询该工号对应员工信息，返回一个员工对象
						String returnblamePerson = "";
						if(!blamePerson.equals(returnblamePerson)){
							//plan.setErrorMsg(plan.getErrorMsg()+";工号与负责人不匹配");
						}else{
							//如果匹配，用返回的这个员工对象去查询其所属部门，返回一个部门对象
							String blameSection = "";
							if(!blameSection.equals(plan.getBlameSection())){
								plan.setErrorMsg(plan.getErrorMsg()+";工号所匹配的人员不在所填部门下");
							}else{
								plan.setBlamePerson(blamePerson);
							}
						}
					}
				}
			}else{
				plan.setErrorMsg(plan.getErrorMsg()+";工号不允许为空");
			}
		}
		return planList;
	}

	/**
	 * @description: 如果是日常类计划，处理所抓取的每一条模板数据
	 * @param planList
	 * @param parentPlan 
	 * @return 
	 * @throws 
	 */
	private List<Plan> dealWithModeData(List<Plan> planList, Plan parentPlan) {
		Integer loopMode = parentPlan.getLoopMode();
		for (int i = 0; i < planList.size(); i++) {
			Plan plan = planList.get(i);
			//处理每一条数据的时间信息
			if(loopMode == 1){
				//按天循环
				String startTime = plan.getStartTime();
				if(startTime != null && !"".equals(startTime)){
					plan.setStartTime(startTime.substring(0, 2));
					plan.setStartTimeDaily(startTime.substring(3, 5));
				}
				
				String endTime = plan.getEndTime();
				if(endTime != null && !"".equals(endTime)){
					plan.setEndTime(endTime.substring(0, 2));
					plan.setEndTimeDaily(endTime.substring(3, 5));
				}
			}else if(loopMode != 1){
				//按周或者按月循环
				String startTime = plan.getStartTime();
				if(startTime != null && !"".equals(startTime)){
					plan.setStartTime(startTime.substring(1, startTime.length() - 1));
				}
				
				String startTimeDaily = plan.getStartTimeDaily();
				if(startTimeDaily != null && !"".equals(startTimeDaily) && !"最后一天".equals(startTimeDaily)){
					if(loopMode == 2){
						//按周循环
						plan.setStartTimeDaily(startTimeDaily.substring(1, startTimeDaily.length()));
					}else if(loopMode == 3){
						//按月循环
						plan.setStartTimeDaily(startTimeDaily.substring(1, startTimeDaily.length() - 1));
					}
				}
				
				String endTime = plan.getEndTime();
				if(endTime != null && !"".equals(endTime)){
					plan.setEndTime(endTime.substring(1, endTime.length() - 1));
				}
				
				String endTimeDaily = plan.getEndTimeDaily();
				if(endTimeDaily != null && !"".equals(endTimeDaily) && !"最后一天".equals(endTimeDaily)){
					if(loopMode == 2){
						//按周循环
						plan.setEndTimeDaily(endTimeDaily.substring(1, endTimeDaily.length()));
					}else if(loopMode == 3){
						//按月循环
						plan.setEndTimeDaily(endTimeDaily.substring(1, endTimeDaily.length() - 1));
					}
				}
			}
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
				}
			}
		}
		List<Plan> subPlans = planService.findSubPlans(plan.getPlanId());
		if(subPlans.size() > 0){
			plan.setSubPlans(subPlans);
			for (Plan p : subPlans) {
				List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
				if(subSubPlans.size() > 0)
					p.setSubPlans(subSubPlans);
				for (Plan pp : subSubPlans) {
					List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
					if(subSubSubPlans.size() > 0)
						pp.setSubPlans(subSubSubPlans);
					for (Plan ppp : subSubSubPlans) {
						List<Plan> subSubSubSubPlans = planService.findSubPlans(ppp.getPlanId());
						if(subSubSubSubPlans.size() > 0)
							ppp.setSubPlans(subSubSubSubPlans);
					}
				}
			}
		}
		return plan;
	}
	
	/**
	 * @description: 将校验后的临时子计划列表存入缓存
	 * @param importMap
	 * @return 
	 * @throws 
	 */
	private void putIntoCache(Map<String, Object> importMap) {
		String key = "importMap";
		Cache cache = CacheManager.getCacheInfo(key);
		if(cache == null){
			cache = new Cache();
			cache.setKey(key);
			cache.setValue(importMap);
			CacheManager.putCache(key, cache);
		}else{
			CacheManager.clearOnly(key);
			cache = new Cache();
			cache.setKey(key);
			cache.setValue(importMap);
			CacheManager.putCache(key, cache);
		}
	}
}
