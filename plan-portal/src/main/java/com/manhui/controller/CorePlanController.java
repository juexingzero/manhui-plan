package com.manhui.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Plan;
import com.manhui.model.User;
import com.manhui.service.PlanService;

/**
 * @ClassName: CorePlanController
 * @description: 中心计划控制器
 * @author:	RanMaoKun	
 * @date Create in 2017年12月1日14:36:28
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class CorePlanController {
	
		//引入计划分类类型
		@Autowired
	    private PlanService planService;
		
		
		/**
		 * 中心计划跳转
		 * @return
		 */
		@RequestMapping("/core_plan")
		public ModelAndView corePlan(){
			ModelAndView mv = new ModelAndView("pages/corePlan/core_plan");//company_plan     section_plan
			return mv;
		}
		/**
		 * 公司计划跳转
		 * @return
		 */
		@RequestMapping("/company_plan")
		public ModelAndView companyPlan(){
			ModelAndView mv = new ModelAndView("pages/corePlan/company_plan");//company_plan     section_plan
			return mv;
		}
		/**
		 * 部门计划跳转
		 * @return
		 */
		@RequestMapping("/section_plan")
		public ModelAndView sectionPlan(){
			ModelAndView mv = new ModelAndView("pages/corePlan/section_plan");//company_plan     section_plan
			return mv;
		}
		
		
		/**
		 * 公司计划
		 * @param model
		 * @param plan
		 * @return
		 */
		@GetMapping("/company_plan_list")
		public String findCompanyPlan(Model model,Plan plan){
			//转模糊查询
			String planName=plan.getPlanName();
			if(null!=planName&&planName!=""){
				planName="%"+planName+"%";
				plan.setPlanName(planName);
			}
			List<Plan> pList=this.planService.findCoreMainPlan(plan); //查询所有主计划 
			
			model.addAttribute("pList", pList);
			return "pages/corePlan/company_plan_list";
		}
		
		
		/**
		 * 部门计划
		 * @param model
		 * @param plan
		 * @return
		 */
		@GetMapping("/section_plan_list")
		public String findSectionPlan(Model model,Plan plan){
			//转模糊查询
			String planName=plan.getPlanName();
			if(null!=planName&&planName!=""){
				planName="%"+planName+"%";
				plan.setPlanName(planName);
			}
			//如果登录用户是员工 则可查看所有部门计划   需要得到所有部门负责人名字
    		//遍历所有部门负责人 加入负责人进行查询
    		List<String> blperson=new ArrayList<>();  //用来装所有的部门负责人
    		List<Plan> coreplans=new ArrayList<>();		//用来装登录用户的中心计划
    		for(String str:blperson){
    			plan.setBlamePerson(str);
    			List<Plan> blplans=this.planService.findCorePlan(plan);
    			for(Plan p:blplans){
    				if(p.getParentId()!=null&&p.getParentId()!=0){
    					Plan pp=this.planService.findById(p.getParentId()); 
        				if(!pp.getBlamePerson().equals(str)){
        					coreplans.add(p);
        				}
    				}else{
    					coreplans.add(p);
    				}
    			}
    		}
    		for(Plan p:coreplans){
    			Plan mplan=new Plan();
    			if(p.getPlanType()==2){
    				if(p.getParentId()==null||p.getParentId()==0){
    					mplan=this.planService.findById(plan.getParentId());
    					if(mplan.getParentId()==null||mplan.getParentId()==0){
    						mplan=this.planService.findById(mplan.getParentId());
    						if(mplan.getParentId()==null||mplan.getParentId()==0){
    							mplan=this.planService.findById(mplan.getParentId());
    							if(mplan.getParentId()==null||mplan.getParentId()==0){
    								mplan=this.planService.findById(mplan.getParentId());
    							}
    						}
    					}
    					p.setLoopMode(mplan.getLoopMode());
    					p.setLoopRate(mplan.getLoopRate());
    				}
    			}
    		}
			
			model.addAttribute("pList", coreplans);
			return "pages/corePlan/section_plan_list";
		}
		
		/**
		 * 中心计划
		 * @param model
		 * @param plan
		 * @return
		 */
		@GetMapping("/core_plan_list")
		public String findCorePlan(Model model,Plan plan){
			//转模糊查询
			String planName=plan.getPlanName();
			if(null!=planName&&planName!=""){
				planName="%"+planName+"%";
				plan.setPlanName(planName);
			}
			User user = (User) CacheManager.getCacheInfo("user").getValue();
    		
    		//如果是部门负责人  则查询中心负责人的负责的计划   需要将中心负责人的名字传入plan对象进行查询
			plan.setBlamePerson("中心负责人");
    		List<Plan> pList=this.planService.findCorePlan(plan); //查询中心负责人负责的计划的
    		List<Plan> plans=new ArrayList<>();
    		for(Plan p:pList){
    			if(p.getParentId()!=null&&p.getParentId()!=0){
    				Plan pp=this.planService.findById(p.getParentId()); 
    				if(!pp.getBlamePerson().equals("中心负责人")){
    					plans.add(p);
    				}
    			}else{
    				plans.add(p);
    			}
    		}
    		for(Plan p:plans){
    			Plan mplan=new Plan();
    			if(p.getPlanType()==2){
    				if(p.getParentId()==null||p.getParentId()==0){
    					mplan=this.planService.findById(plan.getParentId());
    					if(mplan.getParentId()==null||mplan.getParentId()==0){
    						mplan=this.planService.findById(mplan.getParentId());
    						if(mplan.getParentId()==null||mplan.getParentId()==0){
    							mplan=this.planService.findById(mplan.getParentId());
    							if(mplan.getParentId()==null||mplan.getParentId()==0){
    								mplan=this.planService.findById(mplan.getParentId());
    							}
    						}
    					}
    					p.setLoopMode(mplan.getLoopMode());
    					p.setLoopRate(mplan.getLoopRate());
    				}
    			}
    		}
    		
    		model.addAttribute("pList", plans);
			return "pages/corePlan/core_plan_list";
    		
    		
		}
		
		
		/**
		 * 计划详情
		 * @param planId
		 * @param model
		 * @return
		 */
		@GetMapping("/core_plan_detail")
		public String corePlanDetail(Integer planId,Model model){
			Plan plan=this.planService.findById(planId);
			Plan mplan=new Plan();
			if(plan.getPlanType()==2){
				if(plan.getParentId()==null||plan.getParentId()==0){
					mplan=this.planService.findById(plan.getParentId());
					if(mplan.getParentId()==null||mplan.getParentId()==0){
						mplan=this.planService.findById(mplan.getParentId());
						if(mplan.getParentId()==null||mplan.getParentId()==0){
							mplan=this.planService.findById(mplan.getParentId());
							if(mplan.getParentId()==null||mplan.getParentId()==0){
								mplan=this.planService.findById(mplan.getParentId());
							}
						}
					}
					plan.setLoopMode(mplan.getLoopMode());
					plan.setLoopRate(mplan.getLoopRate());
				}
			}
			List<Plan> subPlans=this.planService.findSubPlans(planId);
			if(null!=subPlans&&subPlans.size()>0){
				for(Plan p:subPlans){
					p.setNodeStatus(0);
					List<Plan> subSubPlans=this.planService.findSubPlans(p.getPlanId());
					if(null!=subSubPlans&&subSubPlans.size()>0){
						for(Plan pp:subSubPlans){
							pp.setNodeStatus(0);
							List<Plan> subSubSubPlans=this.planService.findSubPlans(pp.getPlanId());
							if(null!=subSubSubPlans&&subSubSubPlans.size()>0){
								for(Plan ppp:subSubSubPlans){
									ppp.setNodeStatus(0);
									List<Plan> subSubSubSubPlans=this.planService.findSubPlans(ppp.getPlanId());
									if(null!=subSubSubSubPlans&&subSubSubSubPlans.size()>0){
										ppp.setNodeStatus(subSubSubSubPlans.size());
										ppp.setSubPlans(subSubSubSubPlans);
									}
								}
								pp.setNodeStatus(subSubSubPlans.size());
								pp.setSubPlans(subSubSubPlans);
							}
						}
						p.setNodeStatus(subSubPlans.size());
						p.setSubPlans(subSubPlans);
					}
				}
				
				plan.setNodeStatus(subPlans.size());
				plan.setSubPlans(subPlans);
			}
			model.addAttribute("plan",plan);
			return "pages/corePlan/core_plan_detail";
		}
		
}
