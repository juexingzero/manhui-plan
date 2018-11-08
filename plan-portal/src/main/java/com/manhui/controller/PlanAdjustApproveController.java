package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.manhui.model.Plan;
import com.manhui.model.PlanAdjust;
import com.manhui.model.Reject;
import com.manhui.service.PlanAdjustService;
import com.manhui.service.PlanService;
import com.manhui.service.RejectService;
import com.manhui.util.DateUtil;

/**
 * @ClassName: PlanAdjustApproveController
 * @Description: 调整审批控制器 
 * @author WangSheng
 * @date: 2017年12月11日9:20:02
 */
@Controller
public class PlanAdjustApproveController {
	
	@Autowired
	private PlanAdjustService planAdjustService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private RejectService rejectService;
	
	/**
     * @description: 调整审批列表页面跳转
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/adjust_approval")
    public String toPlanAdjustApprove(){
		return "pages/planAdjustApproval/adjust_approval";
    }
    
    
    /**
	 * 查看待分解的计划
	 * @param model
	 * @param plan
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * 
	 * @return
	 */
	 @RequestMapping("adjust_approval_{pageCurrent}_{pageSize}_{pageCount}")
	public String toNodeDecompose(Model model,PlanAdjust planAdjust,@PathVariable Integer pageCurrent,@PathVariable Integer pageSize,
											@PathVariable Integer pageCount){
		 
		 if(planAdjust.getPlanName()!=null){
			 planAdjust.setPlanName("%"+planAdjust.getPlanName()+"%");
		 }
		 
//			User user = (User) CacheManager.getCacheInfo("user").getValue();
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planAdjustService.count(planAdjust);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		planAdjust.setStart((pageCurrent - 1)*pageSize);
		planAdjust.setEnd(pageSize);
		 
		List<PlanAdjust> planAdjustList=planAdjustService.findPlanAdjustByPage(planAdjust);
		for (PlanAdjust pl : planAdjustList) {
			System.out.println(pl.getPlanAdjustId()+">>>>>>>>>>>>>.");
		}
		
		if(planAdjustList != null && planAdjustList.size() > 0)
		model.addAttribute("planAdjustList", planAdjustList);
//		model.addAttribute("planType", plan.getPlanType());
		model.addAttribute("pageCurrent",pageCurrent);
		model.addAttribute("pageSize",pageSize);
		model.addAttribute("pageCount",pageCount);
		model.addAttribute("rows",rows);
		
		
		return "pages/planAdjustApproval/adjust_approval_list";
	}
	 
	 
	 /**
		 * 调整审批详情(除了变更类计划的其他所有)
		 * @param model
		 * @param planAdjustId
		 * 
		 * @return
		 */
	 @RequestMapping("adjustApprovalDetail")
	 public String adjustApprovalDetail(Model model,Integer planAdjustId){
		
		 PlanAdjust planAdjust=planAdjustService.findPlanAdjustByPlanAdjustId(planAdjustId);
		 model.addAttribute("planAdjust",planAdjust);
		 return "pages/planAdjustApproval/adjust_approval_detail";
	 }

	 
	 /**
		 * 调整审批详情(变更类计划)
		 * @param model
		 * @param planAdjustId
		 * 
		 * @return
		 */
	 @RequestMapping("adjustApprovalDetai")
	 public String adjustApprovalDetai(Model model,Integer planAdjustId){
		 
		 PlanAdjust planAdjust=planAdjustService.findPlanAdjustByPlanAdjustId(planAdjustId);
		 System.out.println(planAdjust.getAdjustContent()+">>>>>>>>>>>.");
		 //申请调整范围内的计划
		 List<Plan> gListPlan=new ArrayList<>();
		
		 //不在申请调整范围内的计划
		 List<Plan> nListPlan=planService.findSubPlans(planAdjust.getPlanId());
		 
		 String str=planAdjust.getAdjustContent();
		 String hou="";
		 
		 int j = str.length()-str.replaceAll(";", "").length();
		 for(int i=0;i<j;i++){
		 String zong=str.substring(0,str.indexOf(";"));
		 if(i==0){
		 if(zong.indexOf("暂停计划")>-1){
			 String planName = str.substring(str.indexOf("暂停计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setParentId(planAdjust.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 gListPlan.add(pla);
		      List<Plan> templist = new ArrayList<>();  
		      for (Plan s : nListPlan) {  
		          if(s.getPlanName().equals(planName)) {  
		              templist.add(s);  
		          }  
		      }  
		      nListPlan.removeAll(templist);  
			 
			  hou=str.substring(str.indexOf(";")+1);
		 }else if(zong.indexOf("延期计划")>-1){
			 String planName = str.substring(str.indexOf("延期计划")+4,str.indexOf(";"));
			 System.out.println("名字："+planName);
			 Plan plan=new Plan();
			 plan.setParentId(planAdjust.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 gListPlan.add(pla);
		        //新建一个临时列表，暂存要删除的元素，最后一起删除  
		      List<Plan> templist = new ArrayList<>();  
		      for (Plan s : nListPlan) {  
		          if(s.getPlanName().equals(planName)) {  
		              templist.add(s);  
		          }  
		      }  
		      //删除
		      nListPlan.removeAll(templist);  
			  hou=str.substring(str.indexOf(";")+1);
			 
		 }else if(zong.indexOf("中止计划")>-1){
			 
			 String planName = str.substring(str.indexOf("中止计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setParentId(planAdjust.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 gListPlan.add(pla);
			 List<Plan> templist = new ArrayList<>();  
		      for (Plan s : nListPlan) {  
		          if(s.getPlanName().equals(planName)) {  
		              templist.add(s);  
		          }  
		      }  
		      nListPlan.removeAll(templist); 
			 
			  hou=str.substring(str.indexOf(";")+1);
		 }else if(zong.indexOf("删除计划")>-1){
			 String planName = str.substring(str.indexOf("删除计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setParentId(planAdjust.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 gListPlan.add(pla);
			 List<Plan> templist = new ArrayList<>();  
		      for (Plan s : nListPlan) {  
		          if(s.getPlanName().equals(planName)) {  
		              templist.add(s);  
		          }  
		      }  
		      nListPlan.removeAll(templist); 
			 
			  hou=str.substring(str.indexOf(";")+1);
		 }else{
			 	if(hou.indexOf("暂停计划")>-1){
				 String planName = str.substring(str.indexOf("暂停计划")+4,str.indexOf(";"));
				 Plan plan=new Plan();
				 plan.setParentId(planAdjust.getPlanId());
				 plan.setPlanName(planName);
				 Plan pla=planService.findByPlan(plan);
				 gListPlan.add(pla);
				 List<Plan> templist = new ArrayList<>();  
			      for (Plan s : nListPlan) {  
			          if(s.getPlanName().equals(planName)) {  
			              templist.add(s);  
			          }  
			      }  
			      nListPlan.removeAll(templist); 
				 hou=hou.substring(hou.indexOf(";")+1);
			 	}else if(zong.indexOf("延期计划")>-1){
					 String planName = str.substring(str.indexOf("延期计划")+4,str.indexOf(";"));
					 Plan plan=new Plan();
					 plan.setParentId(planAdjust.getPlanId());
					 plan.setPlanName(planName);
					 Plan pla=planService.findByPlan(plan);
					 gListPlan.add(pla);
					 List<Plan> templist = new ArrayList<>();  
				      for (Plan s : nListPlan) {  
				          if(s.getPlanName().equals(planName)) {  
				              templist.add(s);  
				          }  
				      }  
				      nListPlan.removeAll(templist); 
					 
					 hou=hou.substring(hou.indexOf(";")+1);
					 
				 }else if(zong.indexOf("中止计划")>-1){
					 
					 String planName = str.substring(str.indexOf("中止计划")+4,str.indexOf(";"));
					 Plan plan=new Plan();
					 plan.setParentId(planAdjust.getPlanId());
					 plan.setPlanName(planName);
					 Plan pla=planService.findByPlan(plan);
					 gListPlan.add(pla);
					 List<Plan> templist = new ArrayList<>();  
				      for (Plan s : nListPlan) {  
				          if(s.getPlanName().equals(planName)) {  
				              templist.add(s);  
				          }  
				      }  
				      nListPlan.removeAll(templist); 
					 hou=hou.substring(hou.indexOf(";")+1);
				 }else if(zong.indexOf("删除计划")>-1){
					 String planName = str.substring(str.indexOf("删除计划")+4,str.indexOf(";"));
					 Plan plan=new Plan();
					 plan.setParentId(planAdjust.getPlanId());
					 plan.setPlanName(planName);
					 Plan pla=planService.findByPlan(plan);
					 gListPlan.add(pla);
					 List<Plan> templist = new ArrayList<>();  
				      for (Plan s : nListPlan) {  
				          if(s.getPlanName().equals(planName)) {  
				              templist.add(s);  
				          }  
				      }  
				      nListPlan.removeAll(templist); 
					 hou=hou.substring(hou.indexOf(";")+1);
				 }
		 }
		 }
		 }
		 
		 System.out.println("不调整："+nListPlan.size());
		 System.out.println("申请调整："+gListPlan.size());
		 //总经理
		 if(gListPlan.size()>0){
		 for (Plan plano : gListPlan) {
			
			 //中心负责人
			 List<Plan> planListo=planService.findSubPlans(plano.getPlanId());
			 if(planListo.size()>0){
				 plano.setSubPlans(planListo);
				 for (Plan plant : planListo) {
					 //部门负责人
					 List<Plan> planListt=planService.findSubPlans(plant.getPlanId());
					if(planListt.size()>0){
						plant.setSubPlans(planListt);
						 for (Plan plans : planListt) {
							 //员工
							 List<Plan> planLists=planService.findSubPlans(plans.getPlanId());
							 if(planLists.size()>0){
								 plans.setSubPlans(planLists);
							 }
						}
					}
				}
			 }
			 
			}
		 }
		 for (Plan plan : nListPlan) {
			 //中心负责人
			 List<Plan> planListo=planService.findSubPlans(plan.getPlanId());
			 if(planListo.size()>0){
				 plan.setSubPlans(planListo);
				 for (Plan plant : planListo) {
					 //部门负责人
					 List<Plan> planListt=planService.findSubPlans(plant.getPlanId());
					if(planListt.size()>0){
						plant.setSubPlans(planListt);
						 for (Plan plans : planListt) {
							 //员工
							 List<Plan> planLists=planService.findSubPlans(plans.getPlanId());
							 if(planLists.size()>0){
								 plans.setSubPlans(planLists);
							 }
						}
					}
				}
			 }
			 
			}
		 System.out.println("这里进入没");
		 
		 //结束
		 Plan plan=planService.findById(planAdjust.getPlanId());
		 model.addAttribute("planAdjust", planAdjust);
		 model.addAttribute("plan", plan);
		 model.addAttribute("gListPlan", gListPlan);
		 model.addAttribute("nListPlan", nListPlan);
		 
		 return "pages/planAdjustApproval/adjust_approval_de";
	 }
	 
	 
	 
	 /**
		 * 通过该计划(除了变更的其他所有)
		 * @param model
		 * @param planAdjustId
		 * @return
	 * @throws ParseException 
		 */
	 @RequestMapping("throughAdjustApproval")
	 @ResponseBody
	 public String throughAdjustApproval(Integer planAdjustId) throws ParseException{
		 
		 PlanAdjust planAdjust=planAdjustService.findPlanAdjustByPlanAdjustId(planAdjustId);
		 //1:计划延期 2：计划暂停	3计划中止
		 if(planAdjust.getStatus()==1){
			
			 PlanAdjust p=new PlanAdjust();
			 p.setPlanAdjustId(planAdjustId);
			 p.setAdjustType(3); 
			 planAdjustService.updatePlanAdjustId(p);
			 Plan plan=new Plan();
			 plan.setPlanId(planAdjust.getPlanId());
			 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			 plan.setEndTime(sdf.format(planAdjust.getPostponeTime()));
			 
			 //查询计划原本开始时间
			 Plan pl=planService.findById(planAdjust.getPlanId());
			 
			//计算新工期
			String startTime = pl.getStartTime();
			String endTime = sdf.format(planAdjust.getPostponeTime());
			if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
				Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
				plan.setTimeLimit(timeLimit);
			}
			 
			 planService.updatePlan(plan);
			 
		 }else if(planAdjust.getStatus()==2){
			 
			 List<Plan> planListo=planService.findSubPlans(planAdjust.getPlanId());
			 if(planListo.size()>0){
				 for (Plan plan : planListo) {
					 List<Plan> planListt=planService.findSubPlans(plan.getPlanId());
					 if(planListt.size()>0){
						 
						 for (Plan plant : planListt) {
							 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
							 if(planLists.size()>0){
								for (Plan plans : planLists) {
									List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
									if(planListf.size()>0){
										for (Plan planf : planListf) {
											Plan pl=new Plan();
											 pl.setPlanId(planf.getPlanId());
											 pl.setStatus(7);
											 planService.updatePlan(pl);
										}
										PlanAdjust p=new PlanAdjust();
										 planAdjust.setPlanAdjustId(planAdjustId);
										 planAdjust.setAdjustType(3);
										 planAdjustService.updatePlanAdjustId(p);
									}else{
										 PlanAdjust p=new PlanAdjust();
										 planAdjust.setPlanAdjustId(planAdjustId);
										 planAdjust.setAdjustType(3);
										 planAdjustService.updatePlanAdjustId(p);
										 Plan planf=new Plan();
										 planf.setPlanId(plans.getPlanId());
										 planf.setStatus(7);
										 planService.updatePlan(planf);
									}
								}
							 }else{
								 PlanAdjust p=new PlanAdjust();
								 planAdjust.setPlanAdjustId(planAdjustId);
								 planAdjust.setAdjustType(3);
								 planAdjustService.updatePlanAdjustId(p);
								 Plan plans=new Plan();
								 plans.setPlanId(plant.getPlanId());
								 plans.setStatus(7);
								 planService.updatePlan(plans);
							 }
							 
						}
						 
					 }else{
						 PlanAdjust p=new PlanAdjust();
						 planAdjust.setPlanAdjustId(planAdjustId);
						 planAdjust.setAdjustType(3);
						 planAdjustService.updatePlanAdjustId(p);
						 Plan plans=new Plan();
						 plans.setPlanId(plan.getPlanId());
						 plans.setStatus(7);
						 planService.updatePlan(plans);
					 }
				}
			 }else{
				 PlanAdjust p=new PlanAdjust();
				 planAdjust.setPlanAdjustId(planAdjustId);
				 planAdjust.setAdjustType(3);
				 planAdjustService.updatePlanAdjustId(p);
				 Plan plan=new Plan();
				 plan.setPlanId(planAdjust.getPlanId());
				 plan.setStatus(7);
				 planService.updatePlan(plan);
			 }
			 
		 }else if(planAdjust.getStatus()==3){
			 List<Plan> planListo=planService.findSubPlans(planAdjust.getPlanId());
			 if(planListo.size()>0){
				 for (Plan plan : planListo) {
					 List<Plan> planListt=planService.findSubPlans(plan.getPlanId());
					 if(planListt.size()>0){
						 
						 for (Plan plant : planListt) {
							 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
							 if(planLists.size()>0){
								for (Plan plans : planLists) {
									List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
									if(planListf.size()>0){
										for (Plan planf : planListf) {
											Plan pl=new Plan();
											 pl.setPlanId(planf.getPlanId());
											 pl.setStatus(6);
											 planService.updatePlan(pl);
										}
										PlanAdjust p=new PlanAdjust();
										 planAdjust.setPlanAdjustId(planAdjustId);
										 planAdjust.setAdjustType(3);
										 planAdjustService.updatePlanAdjustId(p);
									}else{
										 PlanAdjust p=new PlanAdjust();
										 planAdjust.setPlanAdjustId(planAdjustId);
										 planAdjust.setAdjustType(3);
										 planAdjustService.updatePlanAdjustId(p);
										 Plan planf=new Plan();
										 planf.setPlanId(plans.getPlanId());
										 planf.setStatus(6);
										 planService.updatePlan(planf);
									}
								}
							 }else{
								 PlanAdjust p=new PlanAdjust();
								 planAdjust.setPlanAdjustId(planAdjustId);
								 planAdjust.setAdjustType(3);
								 planAdjustService.updatePlanAdjustId(p);
								 Plan plans=new Plan();
								 plans.setPlanId(plant.getPlanId());
								 plans.setStatus(6);
								 planService.updatePlan(plans);
							 }
							 
						}
						 
					 }else{
						 PlanAdjust p=new PlanAdjust();
						 planAdjust.setPlanAdjustId(planAdjustId);
						 planAdjust.setAdjustType(3);
						 planAdjustService.updatePlanAdjustId(p);
						 Plan plans=new Plan();
						 plans.setPlanId(plan.getPlanId());
						 plans.setStatus(6);
						 planService.updatePlan(plans);
					 }
				}
			 }else{
				 PlanAdjust p=new PlanAdjust();
				 planAdjust.setPlanAdjustId(planAdjustId);
				 planAdjust.setAdjustType(3);
				 planAdjustService.updatePlanAdjustId(p);
				 Plan plan=new Plan();
				 plan.setPlanId(planAdjust.getPlanId());
				 plan.setStatus(6);
				 planService.updatePlan(plan);
			 } 
		 }
		 
		 return "success";
	 }
	 
	 
	 /**
		 * 通过该计划（计划变更）
		 * @param model
		 * @param planAdjustId
		 * @return
	 * @throws ParseException 
		 */
	 @RequestMapping("throughAdjustApprova")
	 @ResponseBody
	 public String throughAdjustApprova(@RequestParam("planAdjustId") Integer planAdjustId) throws ParseException{
		 
		 
		 PlanAdjust planAdjus=planAdjustService.findPlanAdjustByPlanAdjustId(planAdjustId);
		 String str=planAdjus.getAdjustContent();
		 String hou="";
		 
		 int j = str.length()-str.replaceAll(";", "").length();
		 for(int i=0;i<j;i++){
		 String zong=str.substring(0,str.indexOf(";"));
		 if(i==0){
		 if(zong.indexOf("暂停计划")>-1){
			 
			 String planName = str.substring(str.indexOf("暂停计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setPlanId(planAdjus.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 if(pla!=null){
				 List<Plan> planListo=planService.findSubPlans(pla.getPlanId());
				 if(planListo.size()>0){
					 for (Plan plano : planListo) {
						 Plan planf=new Plan();
						 planf.setPlanId(plano.getPlanId());
						 planf.setStatus(7);
						 planService.updatePlan(planf);
						 
						 List<Plan> planListt=planService.findSubPlans(plano.getPlanId());
						 if(planListt.size()>0){
							 
							 for (Plan plant : planListt) {
								 Plan plantt=new Plan();
								 plantt.setPlanId(plant.getPlanId());
								 plantt.setStatus(7);
								 planService.updatePlan(plantt);
								 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
								 
								 if(planLists.size()>0){
									for (Plan plans : planLists) {
										 Plan planff=new Plan();
										 planff.setPlanId(plans.getPlanId());
										 planff.setStatus(7);
										 planService.updatePlan(planff);
										List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
										if(planListf.size()>0){
											for (Plan planfo : planListf) {
												Plan pl=new Plan();
												 pl.setPlanId(planfo.getPlanId());
												 pl.setStatus(7);
												 planService.updatePlan(pl);
											}
										}else{
											 Plan plana=new Plan();
											 plana.setPlanId(plans.getPlanId());
											 plana.setStatus(7);
											 planService.updatePlan(plana);
										}
									}
								 }else{
									 Plan plans=new Plan();
									 plans.setPlanId(plant.getPlanId());
									 plans.setStatus(7);
									 planService.updatePlan(plans);
								 }
								 
							}
							 
						 }else{
							 Plan plans=new Plan();
							 plans.setPlanId(plano.getPlanId());
							 plans.setStatus(7);
							 planService.updatePlan(plans);
						 }
					}
				 }else{
					 Plan p=new Plan();
					 p.setPlanId(pla.getPlanId());
					 p.setStatus(7);
					 planService.updatePlan(p);
				 }
				 
			 }else{
				 return "fail";
			 }
			 
			 
			 
			  hou=str.substring(str.indexOf(";")+1);
		 }else if(zong.indexOf("延期计划")>-1){
			 String planName = str.substring(str.indexOf("延期计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setPlanId(planAdjus.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 if(pla!=null){
				 Plan xp=new Plan();
				 hou=str.substring(str.indexOf(";")+1);
				 String shij = hou.substring(hou.indexOf("延期至")+3,hou.indexOf(";"));
				 
				//计算新工期
				String startTime = pla.getStartTime();
				String endTime = shij;
				if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
					Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
					xp.setTimeLimit(timeLimit);
				}
				
				xp.setEndTime(shij);
				xp.setPlanId(pla.getPlanId());
				planService.updatePlan(xp);
				hou=str.substring(hou.indexOf(";")+1);
			 }else{
				 return "fail";
			 }
			  hou=str.substring(str.indexOf(";")+1);
		 }else if(zong.indexOf("中止计划")>-1){
			 
			 String planName = str.substring(str.indexOf("中止计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setPlanId(planAdjus.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 if(pla!=null){
				 List<Plan> planListo=planService.findSubPlans(pla.getPlanId());
				 if(planListo.size()>0){
					 for (Plan plano : planListo) {
						 Plan planf=new Plan();
						 planf.setPlanId(plano.getPlanId());
						 planf.setStatus(6);
						 planService.updatePlan(planf);
						 
						 List<Plan> planListt=planService.findSubPlans(plano.getPlanId());
						 if(planListt.size()>0){
							 
							 for (Plan plant : planListt) {
								 Plan plantt=new Plan();
								 plantt.setPlanId(plant.getPlanId());
								 plantt.setStatus(6);
								 planService.updatePlan(plantt);
								 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
								 
								 if(planLists.size()>0){
									for (Plan plans : planLists) {
										 Plan planff=new Plan();
										 planff.setPlanId(plans.getPlanId());
										 planff.setStatus(6);
										 planService.updatePlan(planff);
										List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
										if(planListf.size()>0){
											for (Plan planfo : planListf) {
												Plan pl=new Plan();
												 pl.setPlanId(planfo.getPlanId());
												 pl.setStatus(6);
												 planService.updatePlan(pl);
											}
										}else{
											 Plan plana=new Plan();
											 plana.setPlanId(plans.getPlanId());
											 plana.setStatus(6);
											 planService.updatePlan(plana);
										}
									}
								 }else{
									 Plan plans=new Plan();
									 plans.setPlanId(plant.getPlanId());
									 plans.setStatus(6);
									 planService.updatePlan(plans);
								 }
								 
							}
							 
						 }else{
							 Plan plans=new Plan();
							 plans.setPlanId(plano.getPlanId());
							 plans.setStatus(6);
							 planService.updatePlan(plans);
						 }
					}
				 }else{
					 Plan p=new Plan();
					 p.setPlanId(pla.getPlanId());
					 p.setStatus(6);
					 planService.updatePlan(p);
				 }
				 
			 }else{
				 return "fail";
			 }
			 
			  hou=str.substring(str.indexOf(";")+1);
		 }else if(zong.indexOf("删除计划")>-1){
			 String planName = str.substring(str.indexOf("删除计划")+4,str.indexOf(";"));
			 Plan plan=new Plan();
			 plan.setParentId(planAdjus.getPlanId());
			 plan.setPlanName(planName);
			 Plan pla=planService.findByPlan(plan);
			 
			 if(pla!=null){
				 List<Plan> planListo=planService.findSubPlans(pla.getPlanId());
				 planService.deletePlanById(pla.getPlanId());
				 if(planListo.size()>0){
					 for (Plan plano : planListo) {
						 planService.deletePlanById(plano.getPlanId());
						 
						 List<Plan> planListt=planService.findSubPlans(plano.getPlanId());
						 if(planListt.size()>0){
							 for (Plan plant : planListt) {
								 planService.deletePlanById(plant.getPlanId());
								 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
								 
								 if(planLists.size()>0){
									for (Plan plans : planLists) {
										planService.deletePlanById(plans.getPlanId());
										
										List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
										if(planListf.size()>0){
											for (Plan planfo : planListf) {
												planService.deletePlanById(planfo.getPlanId());
											}
										}
									}
								 }
								 
							}
							 
						 }
					}
				 }
				 
			 }else{
				 return "fail";
			 }
			 hou=str.substring(str.indexOf(";")+1);
		 }
		 }else{
			 if(hou.indexOf("暂停计划")>-1){
				 
				 String planName = str.substring(str.indexOf("暂停计划")+4,str.indexOf(";"));
				 Plan plan=new Plan();
				 plan.setPlanId(planAdjus.getPlanId());
				 plan.setPlanName(planName);
				 Plan pla=planService.findByPlan(plan);
				 if(pla!=null){
					 List<Plan> planListo=planService.findSubPlans(pla.getPlanId());
					 if(planListo.size()>0){
						 for (Plan plano : planListo) {
							 Plan planf=new Plan();
							 planf.setPlanId(plano.getPlanId());
							 planf.setStatus(7);
							 planService.updatePlan(planf);
							 
							 List<Plan> planListt=planService.findSubPlans(plano.getPlanId());
							 if(planListt.size()>0){
								 
								 for (Plan plant : planListt) {
									 Plan plantt=new Plan();
									 plantt.setPlanId(plant.getPlanId());
									 plantt.setStatus(7);
									 planService.updatePlan(plantt);
									 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
									 
									 if(planLists.size()>0){
										for (Plan plans : planLists) {
											 Plan planff=new Plan();
											 planff.setPlanId(plans.getPlanId());
											 planff.setStatus(7);
											 planService.updatePlan(planff);
											List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
											if(planListf.size()>0){
												for (Plan planfo : planListf) {
													Plan pl=new Plan();
													 pl.setPlanId(planfo.getPlanId());
													 pl.setStatus(7);
													 planService.updatePlan(pl);
												}
											}else{
												 Plan plana=new Plan();
												 plana.setPlanId(plans.getPlanId());
												 plana.setStatus(7);
												 planService.updatePlan(plana);
											}
										}
									 }else{
										 Plan plans=new Plan();
										 plans.setPlanId(plant.getPlanId());
										 plans.setStatus(7);
										 planService.updatePlan(plans);
									 }
									 
								}
								 
							 }else{
								 Plan plans=new Plan();
								 plans.setPlanId(plano.getPlanId());
								 plans.setStatus(7);
								 planService.updatePlan(plans);
							 }
						}
					 }else{
						 Plan p=new Plan();
						 p.setPlanId(pla.getPlanId());
						 p.setStatus(7);
						 planService.updatePlan(p);
					 }
					 
				 }else{
					 return "fail";
				 }
				 
				 
				  hou=hou.substring(hou.indexOf(";")+1);
			 }else if(hou.indexOf("延期计划")>-1){
				 String planName = str.substring(str.indexOf("延期计划")+4,str.indexOf(";"));
				 Plan plan=new Plan();
				 plan.setPlanId(planAdjus.getPlanId());
				 plan.setPlanName(planName);
				 Plan pla=planService.findByPlan(plan);
				 if(pla!=null){
					 Plan xp=new Plan();
					 hou=hou.substring(hou.indexOf(";")+1);
					 String shij = hou.substring(hou.indexOf("延期至")+3,hou.indexOf(";"));
					 
					//计算新工期
					String startTime = pla.getStartTime();
					String endTime = shij;
					if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
						Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
						xp.setTimeLimit(timeLimit);
					}
					
					xp.setEndTime(shij);
					xp.setPlanId(pla.getPlanId());
					planService.updatePlan(xp);
					hou=str.substring(hou.indexOf(";")+1);
					 
				 }else{
					 return "fail";
				 }
				 
				 
				  hou=hou.substring(hou.indexOf(";")+1);
			 }else if(hou.indexOf("中止计划")>-1){
				 
				 String planName = str.substring(str.indexOf("中止计划")+2,str.indexOf(";"));
				 Plan plan=new Plan();
				 plan.setPlanId(planAdjus.getPlanId());
				 plan.setPlanName(planName);
				 Plan pla=planService.findByPlan(plan);
				 if(pla!=null){
					 List<Plan> planListo=planService.findSubPlans(pla.getPlanId());
					 if(planListo.size()>0){
						 for (Plan plano : planListo) {
							 Plan planf=new Plan();
							 planf.setPlanId(plano.getPlanId());
							 planf.setStatus(6);
							 planService.updatePlan(planf);
							 
							 List<Plan> planListt=planService.findSubPlans(plano.getPlanId());
							 if(planListt.size()>0){
								 
								 for (Plan plant : planListt) {
									 Plan plantt=new Plan();
									 plantt.setPlanId(plant.getPlanId());
									 plantt.setStatus(6);
									 planService.updatePlan(plantt);
									 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
									 
									 if(planLists.size()>0){
										for (Plan plans : planLists) {
											 Plan planff=new Plan();
											 planff.setPlanId(plans.getPlanId());
											 planff.setStatus(6);
											 planService.updatePlan(planff);
											List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
											if(planListf.size()>0){
												for (Plan planfo : planListf) {
													Plan pl=new Plan();
													 pl.setPlanId(planfo.getPlanId());
													 pl.setStatus(6);
													 planService.updatePlan(pl);
												}
											}else{
												 Plan plana=new Plan();
												 plana.setPlanId(plans.getPlanId());
												 plana.setStatus(6);
												 planService.updatePlan(plana);
											}
										}
									 }else{
										 Plan plans=new Plan();
										 plans.setPlanId(plant.getPlanId());
										 plans.setStatus(6);
										 planService.updatePlan(plans);
									 }
									 
								}
								 
							 }else{
								 Plan plans=new Plan();
								 plans.setPlanId(plano.getPlanId());
								 plans.setStatus(6);
								 planService.updatePlan(plans);
							 }
						}
					 }else{
						 Plan p=new Plan();
						 p.setPlanId(pla.getPlanId());
						 p.setStatus(6);
						 planService.updatePlan(p);
					 }
					 
				 }else{
					 return "fail";
				 }
				 
				  hou=hou.substring(hou.indexOf(";")+1);
			 }else if(zong.indexOf("删除计划")>-1){
				 
				 String planName = str.substring(str.indexOf("删除计划")+4,str.indexOf(";"));
				 Plan plan=new Plan();
				 plan.setParentId(planAdjus.getPlanId());
				 plan.setPlanName(planName);
				 Plan pla=planService.findByPlan(plan);
				 
				 if(pla!=null){
					 List<Plan> planListo=planService.findSubPlans(pla.getPlanId());
					 planService.deletePlanById(pla.getPlanId());
					 if(planListo.size()>0){
						 for (Plan plano : planListo) {
							 planService.deletePlanById(plano.getPlanId());
							 
							 List<Plan> planListt=planService.findSubPlans(plano.getPlanId());
							 if(planListt.size()>0){
								 for (Plan plant : planListt) {
									 planService.deletePlanById(plant.getPlanId());
									 List<Plan> planLists=planService.findSubPlans(plant.getPlanId());
									 
									 if(planLists.size()>0){
										for (Plan plans : planLists) {
											planService.deletePlanById(plans.getPlanId());
											
											List<Plan> planListf=planService.findSubPlans(plant.getPlanId());
											if(planListf.size()>0){
												for (Plan planfo : planListf) {
													planService.deletePlanById(planfo.getPlanId());
												}
											}
										}
									 }
									 
								}
							 }
						}
					 }
					 
				 }else{
					 return "fail";
				 }
				 hou=str.substring(str.indexOf(";")+1);
			 }
		 }
		 }
		 
		 return "success";
	 }
	 
	 
	 /**
		 * 驳回该计划
		 * @param model
		 * @param planAdjustId
		 * @return
		 */
	 @RequestMapping("rejectAdjustApproval")
	 @ResponseBody
	 public String rejectAdjustApproval(Integer planAdjustId,String remark){
	       	
	       	//查询该计划是否被驳回过
	       	Map<String, Object> map=new HashMap<>();
	       	map.put("planId", planAdjustId);
	       	map.put("status", 4);
	       	Reject reject=rejectService.selectRejectByPlanId(map);
	       	if(reject==null){
	       		//第一次被驳回
	       		Reject rt=new Reject();
	       		rt.setRejectReason(remark);
	       		rt.setPlanId(planAdjustId);
	       		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	       		rt.setRejectDate(sdf.format(new Date()));
	       		rt.setStatus(4);
	       		rejectService.saveReject(rt);
	       		
	       		PlanAdjust p=new PlanAdjust();
				 p.setPlanAdjustId(planAdjustId);
				 p.setAdjustType(2); 
				 planAdjustService.updatePlanAdjustId(p);
	       		
	       	}else{
	       		//被驳回过
	       		Reject rt=new Reject();
	       		rt.setRejectReason(remark);
	       		rt.setPlanId(planAdjustId);
	       		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	       		rt.setRejectDate(sdf.format(new Date()));
	       		rt.setRtId(reject.getRtId());
	       		rt.setStatus(4);
	       		rejectService.updateByRejectId(rt);
	       		PlanAdjust p=new PlanAdjust();
				 p.setPlanAdjustId(planAdjustId);
				 p.setAdjustType(2); 
				 planAdjustService.updatePlanAdjustId(p);
	       	}
	       	
	       	return "success";
	 }
}
