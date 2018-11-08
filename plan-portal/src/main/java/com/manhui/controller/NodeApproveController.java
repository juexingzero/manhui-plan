package com.manhui.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Node;
import com.manhui.model.Plan;
import com.manhui.model.Reject;
import com.manhui.model.User;
import com.manhui.service.NodeService;
import com.manhui.service.PlanService;
import com.manhui.service.RejectService;
import com.manhui.util.DateUtil;


/**
 * @ClassName: NodeApproveController
 * @Description: 节点控制器 
 * @author WangSheng
 * @date: 2017年11月27日14:03:02
 */

@Controller
public class NodeApproveController {

	@Autowired
	private PlanService planService;
	
	@Autowired
	private RejectService rejectService;
	
	@Autowired
	private NodeService nodeservice;
	
	/**
     * @description: 节点审批列表页面跳转
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/node_approval")
    public ModelAndView planDecompose(){
    	ModelAndView mv = new ModelAndView("pages/nodeApproval/nodeApprovals");
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
    @RequestMapping("/node_approval_list")
	public String findnodeApprove(Model model,Plan plan){
    	
    			//_{pageCurrent}_{pageSize}_{pageCount}
    			//,@PathVariable Integer pageCurrent,
				//@PathVariable Integer pageSize,@PathVariable Integer pageCount
    			//转模糊查询
    			String planName=plan.getPlanName();
    			if(null!=planName&&planName!=""){
    				planName="%"+planName+"%";
    				plan.setPlanName(planName);
    			}
    			
    			//获取当前登录用户
    			User user = (User) CacheManager.getCacheInfo("user").getValue();
    			plan.setBlamePerson(user.getName());
    			//判断
//    			if(pageSize == 0) pageSize = 2;
//    			if(pageCurrent == 0) pageCurrent = 1;
//    			int rows=this.planService.planNodeApprovalCount(plan);
//    			if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
//    			//查询所有计划模板
//    			plan.setStart((pageCurrent - 1)*pageSize);
//    			plan.setEnd(pageSize);
    		
    			List<Plan> pList=this.planService.findPlanNodeApproval(plan);	//查询登录用户需要负责的计划
    			
    			//移除分配给自己的计划
    			System.out.println("你猜："+pList.size());
    			if(pList.size()>0){
    			//待分解（其中有一个不是已批准），待提交（全部已批准），待审批（住计划提交状态），已驳回（主计划驳回状态）
    			for(Plan p:pList){
    				Map<String, Object> map=new HashMap<>();
    				map.put("planId", p.getPlanId());
    				map.put("status", 1);
    				Reject reject=this.rejectService.selectRejectByPlanId(map);
    				if(null!=reject){
    					p.setReject(reject);
    				}
    				List<Plan> subPlans=this.planService.findSubPlans(p.getPlanId());
    				if(subPlans.size()>0){
    					for (Plan pl : subPlans) {
    						System.out.println(pl.getPlanId()+"asdf");
    						if(pl.getNodeStatus()!=5||pl.getNodeStatus().equals("")){
    							p.setNodeStatus(1);
    							break;
    						}else{
    							p.setNodeStatus(2);
    						}
    						
    					}
    					p.setSubPlans(subPlans);
    					
    					
    				}
    			}
    			
    			
    			for (Iterator it = pList.iterator(); it.hasNext();) {
    			    Plan pla = (Plan) it.next();
    			    List<Plan> subPlans=this.planService.findSubPlans(pla.getPlanId());
    			    if(subPlans.size()==0){
    			    	it.remove();
    			    }
    			}
    			}
    			model.addAttribute("pList",pList);
//    			model.addAttribute("pageCurrent", pageCurrent);
//    			model.addAttribute("pageSize", pageSize);
//    			model.addAttribute("rows", rows);
//    			model.addAttribute("pageCount", pageCount);
    			
    			return "pages/nodeApproval/nodeApprovals_list";		
		
	}
    
    
    
    /**
	 * 查看计划信息(包含节点)
	 * @param model
	 * @param planId
	 * @return
     * @throws ParseException 
	 */
    @GetMapping("nodeShow")
    public String seeNodeDetail(Model model,Integer planId,String type) throws ParseException{
    	
    	//查询该计划详情
    	Plan plan=this.planService.findById(planId);
    	
    	//第二层
    	List<Plan> listPlano=planService.findSubPlans(planId);
    	if(listPlano.size()>0){
    		plan.setSubPlans(listPlano);
    		for (Plan plano : listPlano) {
    			//节点部分
				List<Node> listNodeo=nodeservice.findNodeByPlanId(plano.getPlanId());
				if(listNodeo.size()>0){
					plano.setSubNodes(listNodeo);
					for (Node nodeo : listNodeo) {
						//计算工期
			    		String startTime = nodeo.getStartTime();
			    		String endTime = nodeo.getEndTime();
			    		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
			    			Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
			    			nodeo.setTimeLimit(timeLimit);
			    		}
					}
				}
				//第三层
				List<Plan> listPlant=planService.findSubPlans(plano.getPlanId());
				if(listPlant.size()>0){
					plano.setSubPlans(listPlant);
					for (Plan plant : listPlant) {
						//节点部分
						List<Node> listNodet=nodeservice.findNodeByPlanId(plant.getPlanId());
						if(listNodet.size()>0){
							plant.setSubNodes(listNodet);
							for (Node nodet : listNodet) {
								//计算工期
					    		String startTime = nodet.getStartTime();
					    		String endTime = nodet.getEndTime();
					    		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
					    			Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
					    			nodet.setTimeLimit(timeLimit);
					    		}
							}
						}
						//第四层
						List<Plan> listPlanf=planService.findSubPlans(plant.getPlanId());
						if(listPlanf.size()>0){
							plant.setSubPlans(listPlanf);
							for (Plan planf : listPlanf) {
								//节点部分
								List<Node> listNodes=nodeservice.findNodeByPlanId(planf.getPlanId());
								if(listNodes.size()>0){
									planf.setSubNodes(listNodes);
									for (Node nodes : listNodes) {
										//计算工期
							    		String startTime = nodes.getStartTime();
							    		String endTime = nodes.getEndTime();
							    		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
							    			Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
							    			nodes.setTimeLimit(timeLimit);
							    		}
									}
								}
								//第五层
								List<Plan> listPlanfi=planService.findSubPlans(planf.getPlanId());
								if(listPlanfi.size()>0){
									planf.setSubPlans(listPlanfi);
									for (Plan planfi : listPlanfi) {
										//节点部分
										List<Node> listNodef=nodeservice.findNodeByPlanId(planfi.getPlanId());
										if(listNodef.size()>0){
											planfi.setSubNodes(listNodef);
											for (Node nodef : listNodef) {
												//计算工期
									    		String startTime = nodef.getStartTime();
									    		String endTime = nodef.getEndTime();
									    		if(startTime != null && !"".equals(startTime) && endTime != null && !"".equals(endTime)){
									    			Integer timeLimit = DateUtil.daysBetween(startTime, endTime);
									    			nodef.setTimeLimit(timeLimit);
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
    	
    	model.addAttribute("plan", plan);
    	model.addAttribute("type",type);
    	
    	return "pages/nodeApproval/detailplan";
    }
	
    
    
    /**
	 * 批准通过节点
	 * @param model
	 * @param planId
	 * @return
     * @throws ParseException 
	 */
    
    @RequestMapping("approveNode")
    @ResponseBody
    public String approveNode(String id){
    	
    	Plan p=new Plan();
    	p.setPlanId(Integer.parseInt(id));
    	p.setNodeStatus(5);
    	//计划状态更改
    	planService.updatePlan(p);
    	//节点状态更改
    	Node node=new Node();
    	node.setStatus(2);
    	node.setPlanId(Integer.parseInt(id));
    	nodeservice.updateStatusByPlanId(node);
    	
    	return "success";
    }
    
    
    /**
   	 * 驳回节点
   	 * @param model
   	 * @param planId
   	 * @return
        * @throws ParseException 
   	 */
       
       @RequestMapping("rejectNode")
       @ResponseBody
       public String rejectNode(String planId,String remark){
       	Plan p=new Plan();
       	p.setPlanId(Integer.parseInt(planId));
       	p.setNodeStatus(4);
       	//计划状态更改
       	planService.updatePlan(p);
       	
       	//查询该计划是否被驳回过
       	Map<String, Object> map=new HashMap<>();
       	map.put("planId", Integer.parseInt(planId));
       	map.put("nodeStatus", 4);
       	Reject reject=rejectService.selectRejectByPlanId(map);
       	if(reject==null){
       		//第一次被驳回
       		Reject rt=new Reject();
       		rt.setRejectReason(remark);
       		rt.setPlanId(Integer.parseInt(planId));
       		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
       		rt.setRejectDate(sdf.format(new Date()));
       		rt.setStatus(3);
       		rejectService.saveReject(rt);
       		
       	}else{
       		//被驳回过
       		Reject rt=new Reject();
       		rt.setRejectReason(remark);
       		rt.setPlanId(Integer.parseInt(planId));
       		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
       		rt.setRejectDate(sdf.format(new Date()));
       		rt.setRtId(reject.getRtId());
       		rt.setStatus(3);
       		rejectService.updateByRejectId(rt);
       	}
       	
       	return "success";
       }
       
       
       /**
		 * 提交审批
		 * @param model
		 * @param plan
		 * @return
		 */
	 @RequestMapping("submitPlanNode")
	 @ResponseBody
	 public String submitPlanNode(Plan plan){
		planService.updatePlan(plan);
		
		 return "success";
	 }
       
}
