package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Acceptance;
import com.manhui.model.ActionPlan;
import com.manhui.model.Node;
import com.manhui.model.Plan;
import com.manhui.model.PlanAdjust;
import com.manhui.model.PlanTemplate;
import com.manhui.model.Reject;
import com.manhui.model.ReportSet;
import com.manhui.model.TemplateType;
import com.manhui.model.User;
import com.manhui.service.AcceptanceService;
import com.manhui.service.ActionPlanService;
import com.manhui.service.NodeService;
import com.manhui.service.PlanAdjustService;
import com.manhui.service.PlanService;
import com.manhui.service.PlanTemplateService;
import com.manhui.service.RejectService;
import com.manhui.service.ReportSetService;
import com.manhui.service.TemplateTypeService;
import com.manhui.util.DateUtil;
import com.manhui.util.PageUtil;

/**
 * @ClassName: MyResponsiblePlanController
 * @description: 我的计划控制层
 * @author:	RanMaoKun
 * @date Create in 2017年10月19日20:10:11
 * @version: v1.0.0
 * @modify By:WangSheng
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class MyResponsiblePlanController {
	
		//引入计划分类类型
		@Autowired
		private PlanService planService;
		
		@Autowired
		private NodeService NodeService;
		
		@Autowired
		private RejectService rejectService;
		
		@Autowired
		private AcceptanceService acceptanceService;
		
		
		
		/**
		 * 我的计划跳转页面
		 * @param model
		 * @return
		 */
		@RequestMapping("toFindMyResPlan")
		public String toFindMyResPlan(){
			return "pages/myResPlan/myresleplan";
		}
		
		
		/**
		 * 对我的计划进行分页显示
		 * @param model
		 * @param plan
		 * @param pageCurrent
		 * @param pageSize
		 * @param pageCount
		 * @return
		 */
		@GetMapping("/toFindMyResPlan_{pageCurrent}_{pageSize}_{pageCount}")
		public String FindMainPlan(Model model,Plan plan,@PathVariable Integer pageCurrent,
		@PathVariable Integer pageSize,@PathVariable Integer pageCount){
			//这里还需要调用用户接口 得到用户的 姓名  组成负责人传入查询
			//转模糊查询
			String planName=plan.getPlanName();
			if(planName!=null){
				planName="%"+planName+"%";
				plan.setPlanName(planName);
				
			}
			
			//判断
			if(pageSize == 0) pageSize = 3;
			if(pageCurrent == 0) pageCurrent = 1;
			int rows=this.planService.count(plan);
			if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
			//查询所有计划模板
			plan.setStart((pageCurrent - 1)*pageSize);
			plan.setEnd(pageSize);
			List<Plan> pList=this.planService.findPlan(plan);
			//查询该计划下是否有分解的计划
			for (Plan pl : pList) {
				List<Plan> planL=planService.findSubPlans(pl.getPlanId());
				if(planL.size()>0){
					pl.setSubPlans(planL);
				}
			}
			
			model.addAttribute("pList",pList);
			model.addAttribute("pageCurrent",pageCurrent);
			model.addAttribute("pageSize",pageSize);
			model.addAttribute("pageCount",pageCount);
			model.addAttribute("rows",rows);
			model.addAttribute("planType", plan.getPlanType());
			return "pages/myResPlan/myresleplan_list";
			
		}
		
		
		/**
		 * 显示我的计划详情
		 * @param model
		 * @param planId
		 * @return
		 * @throws ParseException 
		 */
		@RequestMapping("/toPlanDetai")
		public String toPlanDetail(Model model,Integer planId) throws ParseException{
			//查询该计划详情
	    	Plan plan=this.planService.findById(planId);
	    	
	    	//第二层
	    	List<Plan> listPlano=planService.findSubPlans(planId);
	    	if(listPlano.size()>0){
	    		plan.setSubPlans(listPlano);
	    		for (Plan plano : listPlano) {
					//第三层
					List<Plan> listPlant=planService.findSubPlans(plano.getPlanId());
					if(listPlant.size()>0){
						plano.setSubPlans(listPlant);
						for (Plan plant : listPlant) {
							//第四层
							List<Plan> listPlanf=planService.findSubPlans(plant.getPlanId());
							if(listPlanf.size()>0){
								plant.setSubPlans(listPlanf);
								for (Plan planf : listPlanf) {
									//第五层
									List<Plan> listPlanfi=planService.findSubPlans(planf.getPlanId());
									if(listPlanfi.size()>0){
										planf.setSubPlans(listPlanfi);
									}
								}
							}
						}
					}
					
				}
	    	}
			
			model.addAttribute("plan", plan);
			return "pages/myResPlan/titlenode";
		}
		
		/**
		 * 显示我的节点详情
		 * @param model
		 * @param planId
		 * @return
		 */
		@RequestMapping("toNodeDetail")
		public String toNodeDetail(Model model,Integer planId){
			//原计划
			Plan plan=planService.findById(planId);
			//查询该计划的节点
			List<Node> nodeL=NodeService.findNodeByPlanId(planId);
			
			model.addAttribute("plan", plan);
			model.addAttribute("nodeL", nodeL);
			
			return "pages/myResPlan/titlenode";
		}
		
		
		
		/**
		 * 显示我的节点详情
		 * @param model
		 * @param planId
		 * @return
		 */
		@RequestMapping("beginPlan")
		@ResponseBody
		public String beginPlan(Integer planId){
			Plan plan=new Plan();
			plan.setPlanId(planId);
			plan.setStatus(4);
			planService.updatePlan(plan);
			return "success";
		}
		
		
		/**
		 * 查看驳回原因
		 * @param model
		 * @param planId
		 * @return
		 */
		@RequestMapping("torejec")
		public String selectRByPlanId(Model model,Integer planId){
			
			Reject reject=rejectService.selectRByPlanId(planId);
			model.addAttribute("reject", reject);
			return "pages/myResPlan/rejectdetail";
			
		}
		
		
		/**
		 * 提交验收跳转页面
		 * @param model
		 * @param planId
		 * @return
		 */
		@RequestMapping("toAcceptanc")
		public String toAcceptance(Model model,Integer planId){
			System.out.println("进来了嘛");
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			
			model.addAttribute("date", sdf.format(new Date()));
			model.addAttribute("planId", planId);
			return "pages/myResPlan/acceptance";
		}
		
		
		
		/**
		 * 提交验收
		 * @param acceptance
		 * @return
		 */
		@RequestMapping("acceptancePlan")
		@ResponseBody
		public String acceptancePlan(Acceptance acceptance){
			
			Acceptance at=acceptanceService.findAcceptanceByPlanId(acceptance.getActionPlanId());
			if(at!=null){
				acceptanceService.updateAcceptance(acceptance);
				//计划状态更改为待验收
				Plan plan=new Plan();
				plan.setPlanId(acceptance.getActionPlanId());
				plan.setStatus(9);
				planService.updatePlan(plan);
			}else{
				acceptanceService.saveAcceptance(acceptance);
				//计划状态更改为待验收
				Plan plan=new Plan();
				plan.setPlanId(acceptance.getActionPlanId());
				plan.setStatus(9);
				planService.updatePlan(plan);
			}
			return "success";
		}
		
		
		
		/**
		 * 进入提交验收页面
		 * @param model
		 * @param planId
		 * @return
		 */
		@RequestMapping("toInspection")
		public String toInspection(Model model,Integer planId){
			
			Plan plan=planService.findById(planId);
			
			Acceptance acceptance=acceptanceService.findAcceptanceByPlanId(planId);
			
			model.addAttribute("plan",plan);
			model.addAttribute("acceptance", acceptance);
			
			return "pages/myResPlan/planInspection";
		}
		
		
		
		/**
		 * 提交验收
		 * @param acceptance
		 * @return
		 */
		@RequestMapping("inspection")
		@ResponseBody
		public String inspection(Integer planId){
			
			Plan plan=new Plan();
			plan.setPlanId(planId);
			plan.setStatus(5);
			planService.updatePlan(plan);
			
			return "success";
		}
		
		
		
		/**
		 * 驳回 
		 * @param acceptance
		 * @return
		 */
		@RequestMapping("rejectPlan")
		@ResponseBody
		public String rejectPlan(Integer planId,String val){
			System.out.println("驳回原因"+val);
			Reject reject=new Reject();
			reject.setPlanId(planId);
			reject.setRejectReason(val);
			User user = (User) CacheManager.getCacheInfo("user").getValue();
			reject.setRejectPerson(user.getName());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			reject.setRejectDate(sdf.format(new Date()));
			reject.setStatus(2);
			
			Reject rj= rejectService.selectRByPlanId(planId);
			if(rj==null){
				rejectService.saveReject(reject);	
			}else{
				reject.setRtId(rj.getRtId());
				rejectService.updateByRejectId(reject);
			}
			
			
			//计划状态更改验收未通过
			Plan plan=new Plan();
			plan.setPlanId(planId);
			plan.setStatus(10);
			planService.updatePlan(plan);
			
			return "success";
		}
}
