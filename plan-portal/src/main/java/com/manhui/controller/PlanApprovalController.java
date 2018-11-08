package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Plan;
import com.manhui.model.PlanAssess;
import com.manhui.model.PlanTemplateModel;
import com.manhui.model.Reject;
import com.manhui.model.User;
import com.manhui.service.PlanAssessService;
import com.manhui.service.PlanService;
import com.manhui.service.RejectService;
import com.manhui.util.DateUtil;

/**
 * @ClassName: planApprovalController
 * @description: 计划审批控制器
 * @author:	RanMaoKun
 * @date Create in 下午3:45:25 2017年10月14日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class PlanApprovalController {
	
	//引入计划分类类型
	@Autowired
    private PlanService planService;
	
	//引入驳回
	@Autowired
	private RejectService rejectService;
	
	@Autowired
	private PlanAssessService planAssessService;
	
	/**
     * @description: 计划审批列表页面跳转
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/plan_approval")
    public ModelAndView planDecompose(){
    	ModelAndView mv = new ModelAndView("pages/planApproval/planApprovals");
		return mv;
    }
	
	/**
	 * 分页显示审批计划
	 * @param model
	 * @param plan
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @return
	 */
	@GetMapping("/plan_approval_{pageCurrent}_{pageSize}_{pageCount}")
	public String planApproval(Model model,Plan plan,@PathVariable Integer pageCurrent,
	@PathVariable Integer pageSize,@PathVariable Integer pageCount){
		//转模糊查询
		String planName=plan.getPlanName();
		if(null!=planName&&planName!=""){
			planName="%"+planName+"%";
			plan.setPlanName(planName);
		}
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		System.out.println("user.getName()==="+user.getName());
		if("董事长".equals(user.getOffice())){
			List<Plan> pList=this.planService.findDCoreMainPlan(plan);
			model.addAttribute("pList",pList);
			return "pages/planApproval/planDApprovals_list";
		}
		plan.setBlamePerson(user.getName());
		plan.setBlamePerson("张三");
		//判断
		if(pageSize == 0) pageSize = 100;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows=this.planService.planApprovalCount(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> pList=this.planService.findPlanApproval(plan);	//查询登录用户需要负责的计划
		for(int i=0;i<pList.size();i++){
			for(int j=1;j<pList.size();j++){
				if(pList.get(i).getBlamePerson().equals(pList.get(j).getBlamePerson())&&pList.get(i).getPlanId()!=pList.get(j).getPlanId()){
					int k=pList.get(i).getPlanId();
					int q=pList.get(j).getPlanId();
					Integer h=pList.get(i).getParentId();
					Integer u=pList.get(j).getParentId();
					if(h!=null&&u!=null){
						if(k==u){
							pList.remove(j);
						}
						if(q==h){
							pList.remove(i);
						}
					}
				}
			}
		}
		for(Plan p:pList){
			if(p.getStatus()==1){	//表示被驳回
				Map<String, Object> map=new HashMap<>();
				map.put("planId", p.getPlanId());
				map.put("status", 0);
				Reject reject=this.rejectService.selectRejectByPlanId(map);
				if(null!=reject){
					p.setReject(reject);
				}
			}
			List<Plan> subPlans=this.planService.findSubPlans(p.getPlanId());
			if(null!=subPlans&&subPlans.size()>0){
				p.setSubPlans(subPlans);
				if(p.getStatus()!=1&&p.getStatus()!=0&&p.getStatus()!=2&&p.getStatus()!=3){
					for(Plan pp:subPlans){
						if(pp.getStatus()==12&&pp.getBlamePerson()!=p.getBlamePerson()){
							p.setStatus(5); //待提交
						}else if(pp.getStatus()!=12&&pp.getBlamePerson()!=p.getBlamePerson()){
							p.setStatus(6); //待分解
						}
					}
				}
			}
		}
		model.addAttribute("pList",pList);
		model.addAttribute("pageCurrent", pageCurrent);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("rows", rows);
		model.addAttribute("pageCount", pageCount);
		return "pages/planApproval/planApprovals_list";
		
	}
	
	/**
	 * @description: 选择子计划新增位置（之前已有子计划）
	 * @param  request
	 * @param  planId
	 * @param 
	 * @return 
	 * @throws
	 */
	@RequestMapping("/chose_radd_location")
	public ModelAndView choseAddLocation(HttpServletRequest request, String planId){
		ModelAndView mv = new ModelAndView("pages/planApproval/chose_radd_location");
		Plan plan = planService.findById(Integer.parseInt(planId));
		mv.addObject("plan", plan);
		return mv;
	}
	
	
	/**
	 * 编辑页面
	 * @param model
	 * @param planId
	 * @return
	 */
	@GetMapping("/planRedact")
	public String planRedact(Model model,Integer planId,String erroFlag){
		Plan plan=this.planService.findById(planId);
		Integer parentPlan = plan.getPlanId();
		String type="redact";
		//查询所有子计划
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		findAllSubPlan(subPlans);
		plan.setSubPlans(subPlans);
		model.addAttribute("plan",plan);
		model.addAttribute("type",type);
		model.addAttribute("erroFlag",erroFlag);
		return "/pages/planApproval/planredact";
	}

	/**
	 * 查询所属所有子计划
	 * @param subPlans
	 */
	private void findAllSubPlan(List<Plan> subPlans) {
		if(subPlans.size()>0&&subPlans!=null){
			for (Plan p : subPlans) {
			List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
			if(subSubPlans.size() > 0&&subSubPlans!=null)
				p.setSubPlans(subSubPlans);
			for (Plan pp : subSubPlans) {
				List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
				if(subSubSubPlans.size() > 0&&subSubSubPlans!=null)
					pp.setSubPlans(subSubSubPlans);
				for(Plan ppp:subSubSubPlans){
					List<Plan> subSubSubSubPlans=this.planService.findSubPlans(ppp.getPlanId());
					if(subSubSubSubPlans.size()>0&&subSubSubSubPlans!=null){
						ppp.setSubPlans(subSubSubSubPlans);
					}
					
				}
			}
		}
		}
	}
	
	/**
	 * 删除计划
	 * @param id
	 * @return
	 */
	@GetMapping("/planDel")
	public @ResponseBody String planDel(Integer id){
		List<Plan> subPlanList = planService.findSubPlans(id);
		Plan plan=this.planService.findById(id);
		List<Plan> list = planService.findSubPlans(plan.getParentId());
		String msg="true";
		if(subPlanList != null && subPlanList.size()>0){
			msg = "false";
			return msg;
		}else {
			for(Plan p:list){
				if(p.getPlanOrder()>plan.getPlanOrder()){
					p.setPlanOrder(p.getPlanOrder()-1);
					this.planService.updatePlan(p);
				}
			}
			planService.deletePlanById(id);
			return msg;
		}
	}
	
	/**
	 * 重新申请页面
	 * @param model
	 * @param planId
	 * @return
	 */
	@GetMapping("/planApply")
	public String planApply(Model model,Integer planId,String erroFlag){
		Plan plan=this.planService.findById(planId);
		String type="apply";
		Integer parentPlan = plan.getPlanId();
		//查询所有子计划
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		findAllSubPlan(subPlans);
		plan.setSubPlans(subPlans);
		model.addAttribute("type",type);
		model.addAttribute("plan",plan);
		model.addAttribute("erroFlag",erroFlag);
		return "/pages/planApproval/planApply";
	}
	
	
	/**
	 * 审批验证
	 * @return
	 */
	@GetMapping("/applyVerify")
	public @ResponseBody String applyVerify(Integer id){
		Plan plan = this.planService.findById(id);
		String msg="true";
		if(plan.getStatus()!=2){ //表示不为待审批状态 
			Map<String, Object> re=new HashMap<>();
			re.put("planId", id);
			re.put("status", 1);
			Reject reject=this.rejectService.selectRejectByPlanId(re);
			msg=reject.getRejectReason();
		}
		return msg;
	}
	
	/**
	 * 撤回验证
	 * @return
	 */
	@GetMapping("/recall")
	public @ResponseBody String recall(Integer id){
		Plan plan = this.planService.findById(id);
		String msg="true";
		if(plan.getStatus()!=2){ //表示不为可撤回状态 
			msg="false";
		}
		return msg;
	}
	
	
	/**
	 * 查看计划信息
	 * @param model
	 * @param planId
	 * @return
	 */
	@GetMapping("/planShow")
	public String planShow(Model model,Integer planId,String type){
		Plan plan=this.planService.findById(planId);
		Integer parentPlan = plan.getPlanId();
		//查询所有子计划
		List<Plan> subPlans = planService.findSubPlans(parentPlan);
		if(subPlans.size()>0&&subPlans!=null){
			plan.setSubPlans(subPlans);
			plan.setNodeStatus(subPlans.size());
			for (Plan p : subPlans) {
			List<Plan> subSubPlans = planService.findSubPlans(p.getPlanId());
			if(subSubPlans.size() > 0&&subSubPlans!=null)
				p.setSubPlans(subSubPlans);
				p.setNodeStatus(subSubPlans.size());
				for (Plan pp : subSubPlans) {
					List<Plan> subSubSubPlans = planService.findSubPlans(pp.getPlanId());
					if(subSubSubPlans.size() > 0&&subSubSubPlans!=null)
						pp.setSubPlans(subSubSubPlans);
						pp.setNodeStatus(subSubSubPlans.size());
					for(Plan ppp:subSubSubPlans){
						List<Plan> subSubSubSubPlans=this.planService.findSubPlans(ppp.getPlanId());
						if(subSubSubSubPlans.size()>0&&subSubSubSubPlans!=null){
							ppp.setSubPlans(subSubSubSubPlans);
							ppp.setNodeStatus(subSubSubSubPlans.size());
						}
					}
				}
			}
		}
		if(type.equals("4")){
			Map<String, Object> re=new HashMap<>();
			re.put("planId", planId);
			re.put("status", 0);
			Reject reject=this.rejectService.selectRejectByPlanId(re);
			model.addAttribute("reject",reject);
		}
		model.addAttribute("plan",plan);
		model.addAttribute("type",type);	//根据传进来的类型判断 1.查看 2.审批 3.追加分解  4.重新分解（被驳回的）
		if(type.equals("1")||type.equals("2")){
			return "pages/planApproval/detailplan";
		}
		return "pages/planApproval/againresolve";
	}
	
	/**
	 * 发布任务
	 * @param planId
	 * @return
	 */
	@GetMapping("/publishPlan")
	@ResponseBody
	public String publishPlan(Integer planId){
		List<Plan> plans=this.planService.findSubPlans(planId);
		for(Plan plan:plans){
			plan.setDecomStatus(3);
			this.planService.updatePlan(plan);
		}
		return "sucess";
	}
	
	/**
	 * 取消重新分解和追加
	 * @param planId
	 * @return
	 */
	@GetMapping("/cancels")
	public String cancels(Integer planId){
		List<Plan> plans=this.planService.findSubPlans(planId);
		System.out.println(plans.size()>0);
		if(plans!=null&&plans.size()>0){
			for(Plan plan:plans){
				if(plan.getDecomStatus()==null||plan.getDecomStatus()==0){
					this.planService.deletePlanById(plan.getPlanId());
				}
			}
		}
		return "redirect:plan_approval";
	}
	
	/**
	 * 分解计划页面
	 * @param planId
	 * @param planName
	 * @param model
	 * @return
	 */
	@GetMapping("/addPlan")
	public String addPlan(String planId,String planLocation,Model model,String type){
		Plan subplan=this.planService.findById(Integer.parseInt(planId));
		Plan plan=this.planService.findById(subplan.getParentId());
		model.addAttribute("planLocation", planLocation);
		model.addAttribute("planId", planId);
		model.addAttribute("type", type);
		model.addAttribute("subplan", subplan);
		if(null!=plan){
			model.addAttribute("planName", plan.getPlanName());
		}else{
			model.addAttribute("planName", subplan.getPlanName());
		}
		return "pages/planApproval/addPlan";
	}
	
	/**
	 * 保存分解计划
	 * @param mplanId
	 * @param subPlan
	 * @param choose
	 * @return
	 * @throws ParseException 
	 */
	@GetMapping("/saveplan")
	public String savePlan(Integer mplanId,Plan subPlan,String choose,String planLocation,String type,PlanAssess planAssess, PlanTemplateModel planTemplateModel) throws ParseException{
		Plan plan=this.planService.findById(mplanId);
		List<Plan> list=this.planService.findSubPlans(plan.getParentId());
		Date createDate = new Date();
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		Integer newPlanId =this.planService.findMaxPlanId()+1;
		
		subPlan.setPlanId(newPlanId);
		subPlan.setCreateTime(createDate);
		subPlan.setStatus(0);
		subPlan.setCreatePerson(user.getName());
		if(plan.getPlanType()!=2){	//不为日常计划才有工期
			Integer timeLimit = DateUtil.daysBetween(subPlan.getStartTime(), subPlan.getEndTime());
			subPlan.setTimeLimit(timeLimit);
		}
		subPlan.setPapDeadline(plan.getPapDeadline());
		subPlan.setPdDeadline(plan.getPdDeadline());
		subPlan.setNapDeadline(plan.getNapDeadline());
		subPlan.setNdDeadline(plan.getNdDeadline());
		subPlan.setPlanType(plan.getPlanType());
		subPlan.setParentId(plan.getPlanId());
		subPlan.setPlanOrder(list.size()+1);
		//保存计划考核信息
		saveAndUpdatePlanAssess(planAssess,planTemplateModel,subPlan.getPlanId());
		if(subPlan.getPlanType() == 2){
			if(subPlan.getValidateMode() != null && !"".equals(subPlan.getValidateMode())){
				if(subPlan.getExtractItem() != null && !"".equals(subPlan.getExtractItem())){
					subPlan.setValidateMode("1,2");
					}
			}else{
				if(subPlan.getExtractItem() != null && !"".equals(subPlan.getExtractItem()))
					subPlan.setValidateMode("2");
			}
		}
		if("".equals(planLocation)||null==planLocation){		//说明是删除所有子计划再新增的
			planService.savePlan(subPlan);
			return "redirect:planShow.action?type="+type+"&&planId="+mplanId;
		}
		Plan mplan=this.planService.findById(plan.getParentId());
		Map<String,Object> criteria = new HashMap<String, Object>();
		if("1".equals(planLocation)){				//之前
			subPlan.setPlanOrder(plan.getPlanOrder());
			criteria.put("planOrder", plan.getPlanOrder());
			criteria.put("parentId", plan.getParentId());
			List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
			if(plans.size() > 0){
				for (Plan p : plans) {
					p.setPlanOrder(p.getPlanOrder() + 1);
					planService.updatePlan(p);
				}
			}
		}
		if("2".equals(planLocation)){		//之后
			subPlan.setPlanOrder(plan.getPlanOrder() + 1);
			criteria.put("planOrder", plan.getPlanOrder() + 1);
			criteria.put("parentId", plan.getParentId());
			List<Plan> plans = planService.findPlansByOrderAndParentId(criteria);
			if(plans.size() > 0){
				for (Plan p : plans) {
					p.setPlanOrder(p.getPlanOrder() + 1);
					planService.updatePlan(p);
				}
			}
		}
		subPlan.setParentId(mplan.getPlanId());
		planService.savePlan(subPlan);
		return "redirect:planShow.action?type="+type+"&&planId="+mplan.getPlanId();
	}
	
	
	
	
	
	/**
	 * @description: 驳回填写
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@GetMapping("/rejectreason")
	public String rejectreason(String planId,  Model model){
		Plan plan = planService.findById(Integer.parseInt(planId));
		model.addAttribute("plan", plan);
		model.addAttribute("statu", 0);
		return "pages/planApproval/planreject";
	}
	
	/**
	 * @description: 撤回填写
	 * @param @param request
	 * @param @param planId
	 * @param @return
	 * @return 
	 * @throws
	 */
	@GetMapping("/recallreason")
	public String recallreason(String planId,  Model model){
		Plan plan = planService.findById(Integer.parseInt(planId));
		model.addAttribute("plan", plan);
		model.addAttribute("statu", 1);
		return "pages/planApproval/planreject";
	}
	
	/**
	 * 驳回和撤回保存
	 * @param rejectreason
	 * @param planId
	 * @return
	 */
	@GetMapping("/reject")
	@ResponseBody
	public String reject(@RequestParam("rejectreason")String rejectreason,@RequestParam("planId")Integer planId,@RequestParam("statu")Integer statu){
		Map<String, Object> re=new HashMap<>();
		Map<String, Object> rc=new HashMap<>();
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		Plan plan=this.planService.findById(planId);
		if(statu==0){	//表示驳回
			plan.setStatus(1);
			this.planService.updatePlan(plan);
		}else{			//表示撤回
			plan.setStatus(0);
			this.planService.updatePlan(plan);
		}
		re.put("planId", planId);
		re.put("status", 0);
		rc.put("planId", planId);
		rc.put("status", 1);
		Reject rj=this.rejectService.selectRejectByPlanId(re);
		Reject rcc=this.rejectService.selectRejectByPlanId(rc);
		Date date=new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
	    String rejectDate = format.format(date);
	    if(statu==0){		//表示为驳回
	    	if(rj!=null){	//有驳回记录
				rj.setRejectPerson(user.getName());
				rj.setRejectReason(rejectreason);
				rj.setRejectDate(rejectDate);
				rj.setStatus(statu);
				this.rejectService.updateByRejectId(rj);
				return "success";
			}
	    }
	    if(rcc!=null){		//为撤回且有记录
	    	rcc.setRejectPerson(user.getName());
	    	rcc.setRejectReason(rejectreason);
	    	rcc.setRejectDate(rejectDate);
	    	rcc.setStatus(statu);
			this.rejectService.updateByRejectId(rcc);
			return "success";
		}
		Reject reject=new Reject();
		reject.setRejectPerson(user.getName());
		reject.setRejectReason(rejectreason);
		reject.setPlanId(planId);
		reject.setRejectDate(rejectDate);
		reject.setStatus(statu);
		this.rejectService.saveReject(reject);
		return "success";
	}
	/**
	 * 保存编辑分解计划
	 * @param model
	 * @param subPlan
	 * @param httpSession
	 * @param templateId
	 * @param planId
	 * @param currentHandlePlan
	 * @param planLocation
	 * @return
	 * @throws ParseException 
	 */
	@PostMapping("/subPlanRedact")
	public String subPlanEdit(Model model,Plan subPlan,HttpSession httpSession,String type,PlanAssess planAssess, PlanTemplateModel planTemplateModel) throws ParseException{
		//编辑子计划
		//保存计划考核信息
		this.planAssessService.deleteByPlanId(subPlan.getPlanId());
		saveAndUpdatePlanAssess(planAssess,planTemplateModel,subPlan.getPlanId());
		if(2 == subPlan.getPlanType()){
			if(subPlan.getValidateMode() != null && !"".equals(subPlan.getValidateMode())){
				if(subPlan.getExtractItem() != null && !"".equals(subPlan.getExtractItem())){
					subPlan.setValidateMode("1,2");
					}
			}else{
				if(subPlan.getExtractItem() != null && !"".equals(subPlan.getExtractItem()))
					subPlan.setValidateMode("2");
			}
		}		
		if(subPlan.getPlanType()!=2){	//不为日常计划才有工期
			Integer timeLimit = DateUtil.daysBetween(subPlan.getStartTime(), subPlan.getEndTime());
			subPlan.setTimeLimit(timeLimit);
		}
		planService.updatePlan(subPlan);
		return "redirect:planShow?planId="+subPlan.getParentId()+"&type="+type;
	}
	
	
	/**
	 * 跳转到子计划编辑页面
	 * @param model
	 * @param subId
	 * @param planLocation
	 * @return
	 */
	@GetMapping("/redactSubPlan")
	public String editSubPlan(Model model,String planId,String type){
		//编辑模式，查询子计划信息并返回
		Plan subPlan = planService.findById(Integer.parseInt(planId));
		Plan plan=this.planService.findById(subPlan.getParentId());
		List<PlanAssess> pass=this.planAssessService.selectByPlanId(Integer.parseInt(planId));
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
		model.addAttribute("mPlanName", plan.getPlanName());
		model.addAttribute("type", type);
		model.addAttribute("subPlan", subPlan);
		return "pages/planApproval/redactsubplan";
	}
	
	
	/**
	 * 提交申请
	 * @param id
	 * @return
	 */
	@GetMapping("applyAPlan")
	public @ResponseBody String applyPlan(Integer planId){
		Plan subPlan = planService.findById(planId);
		List<Plan> plans=this.planService.findSubPlans(planId);
		for(Plan plan:plans){
			if(plan.getStatus()==12){ //表示有新增但未发布的任务
				return "false";
			}
		}
		subPlan.setStatus(2);
		this.planService.updatePlan(subPlan);
		return "true";
	}
	
	/**
	 * 审批通过
	 * @param id
	 * @return
	 */
	@GetMapping("applyPlans")
	public @ResponseBody String applyPlans(Integer planId){
		Plan subPlan = planService.findById(planId);
		if(subPlan.getParentId()!=null){
			subPlan.setStatus(12);
			this.planService.updatePlan(subPlan);
		}else{	//董事长、计划创建人审批  
			subPlan.setStatus(3);
			this.planService.updatePlan(subPlan);
			List<Plan> plans=this.planService.findSubPlans(planId);
			for(Plan p:plans){
				p.setStatus(3);
				this.planService.updatePlan(p);
				List<Plan> planss=this.planService.findSubPlans(p.getPlanId());
				if(null!=planss&&planss.size()>0){
					for(Plan pp:planss){
						pp.setStatus(3);
						this.planService.updatePlan(pp);
						List<Plan> plansss=this.planService.findSubPlans(pp.getPlanId());
						if(null!=plansss&&plansss.size()>0){
							for(Plan ppp:planss){
								ppp.setStatus(3);
								this.planService.updatePlan(ppp);
								List<Plan> planssss=this.planService.findSubPlans(ppp.getPlanId());
								if(null!=planssss&&planssss.size()>0){
									for(Plan pppp:planss){
										pp.setStatus(3);
										this.planService.updatePlan(pppp);
									}
								}
							}
						}
					}
				}
			}
		}
		return "提交成功";
		
	}
	
	
	/**
	 *计划调序页面
	 * @param model
	 * @param PlanId
	 * @param templateId
	 * @return
	 */
	@GetMapping("/mearder")
	public String changeOrder(Model model,String planId,String type){
		//获取总计划以及下属子计划
		Plan plan = planService.findById(Integer.parseInt(planId));
		Plan mplan=this.planService.findById(plan.getParentId());
		List<Plan> subplans=this.planService.findSubPlans(mplan.getPlanId());
		mplan.setSubPlans(subplans);
		model.addAttribute("mplan", mplan);
		model.addAttribute("type", type);
		model.addAttribute("planId", plan.getPlanId());
		return "pages/planApproval/meaningOrder";
	}
	
	/**
	 * 预览页面
	 * @param model
	 * @param subCheck
	 * @param location
	 * @param planId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/preview")
	@ResponseBody
	public Plan preview(Integer check,String location,Integer planId){
		Plan plan=this.planService.findById(planId);
		Plan checkPlan=this.planService.findById(check);
		int planOrder=plan.getPlanOrder();
		int checkPlanOrder=checkPlan.getPlanOrder();
		Plan mPlan=this.planService.findById(plan.getParentId());
		List<Plan> plans=this.planService.findSubPlans(mPlan.getPlanId());
		if("befor".equals(location)){		//之前
			if(planOrder==(checkPlanOrder-1)){
				return null;
			}
			if(planOrder<checkPlanOrder){	//选择计划在 需要 调序计划之后
				for(Plan p:plans){
					if(p.getPlanOrder()<(checkPlanOrder)&&p.getPlanOrder()>planOrder){
						p.setPlanOrder(p.getPlanOrder()-1);
					}else if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder-1);
					}
				}
			}else{
				for(Plan p:plans){			//选择计划在 需要 调序计划之前
					if(p.getPlanOrder()>(checkPlanOrder-1)&&p.getPlanOrder()<planOrder){
						p.setPlanOrder(p.getPlanOrder()+1);
					}else if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder);
					}
				}
			}
		}
		if("after".equals(location)){		//之后
			if(planOrder==(checkPlanOrder+1)){
				return null;
			}
			if(planOrder<checkPlanOrder){	//选择计划在 需要 调序计划之后
				
				for(Plan p:plans){
					if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder);
					}else if(p.getPlanOrder()<(checkPlanOrder+1)&&p.getPlanOrder()>planOrder){
						p.setPlanOrder(p.getPlanOrder()-1);
					}
				}
			}
			if(planOrder>checkPlanOrder){	//选择计划在 需要 调序计划之前
				for(Plan p:plans){
					if(p.getPlanOrder()<checkPlanOrder&&p.getPlanOrder()>(planOrder-1)){
						p.setPlanOrder(p.getPlanOrder()-1);
					}else 	if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder);
					}
				}
			}
		}
		//根据序号排序
		comparatorstr c=new comparatorstr();
		Collections.sort(plans,c);
		mPlan.setSubPlans(plans);
		return mPlan;
	}
	
	
	//比较器
	 @SuppressWarnings("rawtypes")
	public class comparatorstr implements Comparator{
	 	public int compare(Object o1,Object o2){
	 		Plan u1=(Plan)o1;
	 		Plan u2=(Plan)o2;	
	 		int bijiao=u1.getPlanOrder().compareTo(u2.getPlanOrder());
	 		return bijiao;
	 	}
	 }
	
	/**
	 * 对调序进行保存
	 * @param check
	 * @param location
	 * @param planId
	 * @return
	 */
	@GetMapping("/savepreview")
	@ResponseBody
	public String savepreview(Integer check,String location,Integer planId){
		Plan plan=this.planService.findById(planId);
		Plan checkPlan=this.planService.findById(check);
		Plan mPlan=this.planService.findById(plan.getParentId());
		int planOrder=plan.getPlanOrder();
		int checkPlanOrder=checkPlan.getPlanOrder();
		List<Plan> plans=this.planService.findSubPlans(mPlan.getPlanId());
		if("befor".equals(location)){		//之前
			if(planOrder==(checkPlanOrder-1)){
				return null;
			}
			if(planOrder<checkPlanOrder){	//选择计划在 需要 调序计划之后
				for(Plan p:plans){
					if(p.getPlanOrder()<(checkPlanOrder)&&p.getPlanOrder()>planOrder){
						p.setPlanOrder(p.getPlanOrder()-1);
						this.planService.updatePlan(p);
					}else if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder-1);
						this.planService.updatePlan(p);
					}
				}
			}else{
				for(Plan p:plans){			//选择计划在 需要 调序计划之前
					if(p.getPlanOrder()>(checkPlanOrder-1)&&p.getPlanOrder()<planOrder){
						p.setPlanOrder(p.getPlanOrder()+1);
						this.planService.updatePlan(p);
					}else if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder);
						this.planService.updatePlan(p);
					}
				}
			}
		}
		if("after".equals(location)){		//之后
			if(planOrder==(checkPlanOrder+1)){
				return null;
			}
			if(planOrder<checkPlanOrder){	//选择计划在 需要 调序计划之后
				for(Plan p:plans){
					if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder);
						this.planService.updatePlan(p);
					}else if(p.getPlanOrder()<(checkPlanOrder+1)&&p.getPlanOrder()>planOrder){
						p.setPlanOrder(p.getPlanOrder()-1);
						this.planService.updatePlan(p);
					}
					
				}
			}
			if(planOrder>checkPlanOrder){	//选择计划在 需要 调序计划之前
				for(Plan p:plans){
					if(p.getPlanOrder()<checkPlanOrder&&p.getPlanOrder()>(planOrder-1)){
						p.setPlanOrder(p.getPlanOrder()-1);
						this.planService.updatePlan(p);
					}else 	if(p.getPlanOrder()==planOrder){
						p.setPlanOrder(checkPlanOrder);
						this.planService.updatePlan(p);
					}
				}
			}
		}
		return "sucess";
	}
	
	
	/**
	 * 修改计划时间
	 * @param request
	 * @param planId
	 * @param startTime
	 * @param endTime
	 * @param mainPlanId
	 * @return
	 */
	@RequestMapping("/updatesubtime")
	@ResponseBody
	public String updatesubtime(HttpServletRequest request, String planId, 
			String startTime, String endTime,String mainPlanId){
		try {
			Plan upPlan = new Plan();
			if(planId != null && !"".equals(planId))
				upPlan.setPlanId(Integer.parseInt(planId));
			if(startTime != null && !"".equals(startTime))
				upPlan.setStartTime(startTime);
			if(endTime != null && !"".equals(endTime))
				upPlan.setEndTime(endTime);
			if(endTime != null && !"".equals(endTime)&&startTime != null && !"".equals(startTime)){
				Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
				upPlan.setTimeLimit(timeLimit);
			}
			planService.updatePlan(upPlan);
			return "sucess";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "false";
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
