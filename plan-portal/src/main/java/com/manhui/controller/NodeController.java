package com.manhui.controller;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.manhui.cacheManage.CacheManager;
import com.manhui.model.Node;
import com.manhui.model.Plan;
import com.manhui.model.Reject;
import com.manhui.model.User;
import com.manhui.service.NodeService;
import com.manhui.service.PlanService;
import com.manhui.service.RejectService;

/**
 * @ClassName: NodeController
 * @Description: 节点控制器 
 * @author WangSheng
 * @date: 2017年11月20日11:43:02
 */

@Controller
public class NodeController {

	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private PlanService planService;
	
	@Autowired
	private RejectService rejectService;
	
	
	/**
	 * 查看我的节点页面跳转
	 * @param planId
	 * @param model
	 * @return
	 */
	@GetMapping("/my_node_{pageCurrent}_{pageSize}_{pageCount}")
	public String toMyNode(Model model,Plan plan,String planId,@PathVariable Integer pageCurrent,@PathVariable Integer pageSize,
			@PathVariable Integer pageCount){
		
		System.out.println(plan.getBlamePerson()+"当前用户《《《《《《《《《《《");
		
		//获取当前登录用户
//		User user = (User) CacheManager.getCacheInfo("user").getValue();
//		plan.setBlamePerson(user.getName());

		 if(plan.getPlanName()!=null){
			 plan.setPlanName("%"+plan.getPlanName()+"%");
		 }
				//分页判断
				if(pageSize == 0) pageSize = 5;
				if(pageCurrent == 0) pageCurrent = 1;
				int rows = planService.getDecomPlancount(plan);
				if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
				plan.setStart((pageCurrent - 1)*pageSize);
				plan.setEnd(pageSize);
				 
				List<Plan> planList=planService.getDecomPlans(plan);
				Integer i = null;
				for (Plan pl : planList) {
					i=pl.getPlanId();
					break;
				}
				if(planList != null && planList.size() > 0)
				model.addAttribute("planList", planList);
				model.addAttribute("planType", plan.getPlanType());
				model.addAttribute("pageCurrent",pageCurrent);
				model.addAttribute("pageSize",pageSize);
				model.addAttribute("pageCount",pageCount);
				model.addAttribute("rows",rows);
		
		
		
		if(planId!=null){
			List<Node> nodeList=nodeService.findNodeByPlanId(Integer.parseInt(planId));
			model.addAttribute("nodeList", nodeList);
		}
		
		
		model.addAttribute("planList", planList);
		model.addAttribute("ts", i);
	
		
		return "pages/myNode/plan_decompose";
		
	}
	
	
	/**
	 * 查看我的节点
	 * @param planId
	 * @param model
	 * @return
	 */
	
	@RequestMapping("/myNode")
	public String myNode(Model model,String planId){
		System.out.println("planId"+planId);
		List<Node> nodeList=nodeService.findNodeByPlanId(Integer.parseInt(planId));
		model.addAttribute("nodeList", nodeList);
		
		return "pages/myNode/plan_decompose_list";
	}
	
	

	/**
	 * 修改我的节点状态
	 * @param node
	 * @return
	 */
	@RequestMapping("/test")
	@ResponseBody
	public String NodeStatusUpdate(@RequestParam("nodeId")String nodeId,@RequestParam("status")String status,
									@RequestParam("remark")String remark){
		System.out.println("结束节点流程");
		System.out.println("状态："+status);
		System.out.println("id没传进来？"+nodeId);
		Node node=new Node();
		if(Integer.parseInt(status)==2){
			node.setNodeId(Integer.parseInt(nodeId));
			node.setStatus(3);
			nodeService.updateNodeStatus(node);
		}else if(Integer.parseInt(status)==3){
			System.out.println("进入2");
			node.setNodeId(Integer.parseInt(nodeId));
			node.setStatus(4);
			node.setRemark(remark);
			nodeService.updateNodeStatus(node);
		}
		
	
		return "success";
	}
	
	
	/**
	 * 查看计划详情
	 * @param model
	 * @param planId
	 * @return
	 */
	@RequestMapping("seePlanDetail")
	public String seePlanDetail(Model model,Integer planId){
		
		//查询该计划
		Plan plano=planService.findById(planId);
		
		//查询该计划上层是否有计划(层1)
		if(plano.getParentId()!=null&&!plano.getParentId().equals("")){
			//该计划不是主计划
			Plan plant=planService.findById(plano.getParentId());
			
			//查询该计划上层是否有计划（层2）
			if(plant.getParentId()!=null&&!plant.getParentId().equals("")){
				
				Plan plans=planService.findById(plant.getParentId());
				
				//查询该计划上层是否有计划（层3）
				if(plans.getParentId()!=null&&!plans.getParentId().equals("")){
					System.out.println("该计划为第三层子计划");
					//如果这是主计划
					Plan planf=planService.findById(plans.getParentId());
					
					//查询该计划上层是否有计划（层4）
					if(planf.getParentId()!=null&&!planf.getParentId().equals("")){
						System.out.println("该计划为第4层计划");
					List<Plan> planLiso=planService.findSubPlans(planf.getPlanId());
					planf.setSubPlans(planLiso);
					if(planLiso.size()>0){
					for (Plan plan : planLiso) {
						List<Plan> planList=planService.findSubPlans(plan.getPlanId());
						plan.setSubPlans(planList);
						if(planList.size()>0){
						for (Plan pla : planList) {
							List<Plan> planLiss=planService.findSubPlans(pla.getPlanId());
							pla.setSubPlans(planLiss);
							if(planLiss.size()>0){
								for (Plan pl : planLiss) {
									List<Plan> listLisf=planService.findSubPlans(pl.getPlanId());
									pl.setSubPlans(listLisf);
									model.addAttribute("plano", planf);
								}
							}else{
							model.addAttribute("plano", planf);
							}
						}}else{
							model.addAttribute("plano", planf);
						}
					}}else{
						model.addAttribute("plano", planf);
					}}else{
						model.addAttribute("plano", planf);
					}
					
				}else{
					System.out.println("该计划为第二层子计划");
					//该计划为主计划
					List<Plan> planLiso=planService.findSubPlans(plans.getPlanId());
					plans.setSubPlans(planLiso);
					if(planLiso.size()>0){
						for (Plan plan : planLiso) {
							List<Plan> planList=planService.findSubPlans(plan.getPlanId());
							plan.setSubPlans(planList);
							if(planList.size()>0){
								for (Plan pla : planList) {
									List<Plan> planLiss=planService.findSubPlans(pla.getPlanId());
									pla.setSubPlans(planLiss);
									if(planLiss.size()>0){
										for (Plan pl : planLiss) {
											List<Plan> planLisf=planService.findSubPlans(pl.getPlanId());
											pl.setSubPlans(planLisf);
										}
									}else{
									model.addAttribute("plano", plans);
									}
								}
							}else{
								model.addAttribute("plano", plans);
							}
						}
					}else{
						model.addAttribute("plano", plans);
					}
				}
			}else{
				System.out.println("该计划为第一层子计划");
				//该计划为主计划
				List<Plan> planLiso=planService.findSubPlans(plant.getPlanId());
				plant.setSubPlans(planLiso);
				if(planLiso.size()>0){
					for (Plan plan : planLiso) {
						List<Plan> planList=planService.findSubPlans(plan.getPlanId());
						plan.setSubPlans(planList);
						if(planList.size()>0){
							for (Plan pla : planList) {
								List<Plan> planLiss=planService.findSubPlans(pla.getPlanId());
								pla.setSubPlans(planLiss);
								if(planLiss.size()>0){
									for (Plan pl : planLiss) {
										List<Plan> planLisf=planService.findSubPlans(pl.getPlanId());
										pl.setSubPlans(planLisf);
									}
								}else{
								model.addAttribute("plano", plant);
								}
							}
						}else{
							model.addAttribute("plano", plant);
						}
					}
				}else{
					model.addAttribute("plano", plant);
				}
			}
		}else{
			System.out.println("该计划为主计划");
			//该计划就是主计划
			//子计划（第一层）
			List<Plan> planLiso=planService.findSubPlans(plano.getPlanId());
			
			if(planLiso.size()>0){
				
				plano.setSubPlans(planLiso);
				
			for (Plan plan : planLiso) {
				//子计划（第二层）
				List<Plan> planList=planService.findSubPlans(plan.getPlanId());
				//判断该计划有无子计划
				if(planList.size()>0){
					plan.setSubPlans(planList);
					for (Plan pla : planList) {
						//子计划（最后一层）
						List<Plan> planLiss=planService.findSubPlans(pla.getPlanId());
						if(planLiss.size()>0){
							pla.setSubPlans(planLiss);
							if(planLiss.size()>0){
								for (Plan pl : planLiss) {
									List<Plan> planLisf=planService.findSubPlans(pl.getPlanId());
									pl.setSubPlans(planLisf);
								}
							}else{
							model.addAttribute("plano", plano);
							}
						}else{
							model.addAttribute("plano",plano);
						}
					}
					
				}else{
					model.addAttribute("plano",plano);
				}
				
			}
			}else{
				model.addAttribute("plano",plano);
			}
			
		}
		
		return "pages/myNode/plandetail";
	}
	
	
	/**
     * @description: 节点分解列表页面跳转
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/node_decompose")
    public String planDecompose(Model model,Plan plan){
    	User user = (User) CacheManager.getCacheInfo("user").getValue();
		plan.setBlamePerson(user.getName());
		
		
		
		plan.setStart(0);
		plan.setEnd(10);
		List<Plan> planList=planService.getDecomPlans(plan);
		int rows = planService.getDecomPlancount(plan);
		System.out.println(planList.size()+":::::::::::::::::::;;");
		if(planList != null && planList.size() > 0)
		model.addAttribute("planList", planList);
		model.addAttribute("rows",rows);
		return "pages/nodeDecompose/plan_decompose";
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
	 @RequestMapping("node_decompose_detail_{pageCurrent}_{pageSize}_{pageCount}")
	public String toNodeDecompose(Model model,Plan plan,@PathVariable Integer pageCurrent,@PathVariable Integer pageSize,
											@PathVariable Integer pageCount){
		 System.out.println("进了没");
		 
		 if(plan.getPlanName()!=null){
			 plan.setPlanName("%"+plan.getPlanName()+"%");
		 }
		 
			User user = (User) CacheManager.getCacheInfo("user").getValue();
			plan.setBlamePerson(user.getName());
		//分页判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows = planService.getDecomPlancount(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		System.out.println(plan.getNodeStatus()+"jin");
		List<Plan> planList=planService.getDecomPlans(plan);
		System.out.println(planList.size()+":::::::::::::::::::;;");
		if(planList != null && planList.size() > 0)
		model.addAttribute("planList", planList);
		model.addAttribute("planType", plan.getPlanType());
		model.addAttribute("pageCurrent",pageCurrent);
		model.addAttribute("pageSize",pageSize);
		model.addAttribute("pageCount",pageCount);
		model.addAttribute("rows",rows);
		
		
		return "pages/nodeDecompose/plan_decompose";
	}
	 
	 
	 /**
		 * 分解节点
		 * @param model
		 * @param planId
		 * @return
		 */
	 
	 @RequestMapping("nodeDecompose")
	 public String NodeDecompose(Model model,String planId,String plansuch){
		 Plan plan=planService.findById(Integer.parseInt(planId));
		 System.out.println(plansuch+">>>>>>>>>>>>>>>>>>>>>>>>>>");
		 //判断该计划是否是被驳回计划
		 if(plan.getStatus()==3){
			 //查询驳回原因
			Reject reject=rejectService.selectRByPlanId(Integer.parseInt(planId));
			 model.addAttribute("reject", reject);
		 }
		 
		 model.addAttribute("plan", plan);
		 
		 List<Node> nodeList=nodeService.findNodeByPlanId(Integer.parseInt(planId));
		 if(nodeList.size()>0&&nodeList!=null){
			 model.addAttribute("nodeList", nodeList);	 
		 }
		 
		 if(plansuch!=null){
			 model.addAttribute("plansuch", "1");
		 }
		 
		 
		 return "pages/nodeDecompose/titlenode";
	 }
	 
	 
	 /**
		 * 进入添加节点页面
		 * @return
		 */
	 @GetMapping("toAddNode")
	 public String toAddNode(Model model,String planId,String nodeName,String nodeId){
		 List<Node> nodeList=nodeService.findNodeByPlanId(Integer.parseInt(planId));
		 System.out.println("节点有"+nodeList.size()+"个");
		 if(nodeList.size()>0){
			 model.addAttribute("nodesum","1");
			 model.addAttribute("nodeName",nodeName);
			 model.addAttribute("nodeId", nodeId);
		 }else{
			
		 model.addAttribute("nodesum","0");
		 }
		 model.addAttribute("planId", planId);
		 return "pages/nodeDecompose/addnode";
	 }
	 
	 
	 /**
		 * 添加节点
		 * @param node
		 * @return
		 */
	 @RequestMapping("addNode")
	 @ResponseBody
	 public String addNode(Node node,String qh,String oldId){
		 node.setStatus(1);
			  if(oldId==null){
				  node.setNodeOrder(1);
				  nodeService.addNode(node);
			  }else{
				  //查询总的节点
				  List<Node> nodeList=nodeService.findNodeByPlanId(node.getPlanId());
				  Node oldnode=nodeService.findNbyNodeId(Integer.parseInt(oldId));
				  if(Integer.parseInt(qh)==1){
					  //添加到节点之前
					  for (Node nod : nodeList) {
							if(nod.getNodeOrder()>=oldnode.getNodeOrder()){
								nod.setNodeOrder(nod.getNodeOrder()+1);
								nodeService.updateNode(nod);
							}
						}					 
					  //添加新的节点顶替带原节点位置
					  node.setNodeOrder(oldnode.getNodeOrder());
					  nodeService.addNode(node);

					 }else{
					  //添加到节点之后
						  for (Node nod : nodeList) {
								if(nod.getNodeOrder()>oldnode.getNodeOrder()){
									nod.setNodeOrder(nod.getNodeOrder()+1);
									nodeService.updateNode(nod);
								}
							}					 
						  //添加新的节点顶替带原节点位置
						  node.setNodeOrder(oldnode.getNodeOrder()+1);
						  nodeService.addNode(node);
					 }
				  
			  }
			  return "success";
		
		
	 }
	 
	 
	 
	 /**
		 * 进入编辑节点页面
		 * @return
		 */
	 @GetMapping("toupdateNode")
	 public String toUpdateNode(Model model,Node node){
		 System.out.println("进来");
		 model.addAttribute("node", node);
		 return "pages/nodeDecompose/updatenode";
	 }
	 
	 
	 /**
		 * 编辑节点
		 * @param node
		 * @return
		 */
	 @RequestMapping("updateNode")
	 @ResponseBody
	 public String updateNode(Node node){
		 int i=nodeService.updateNode(node);
		 if(i==1){
			 return "success";
		 }else{
			 return "fail";
		 }
		 
	 }
	 
	 /**
		 * 删除节点
		 * @param nodeId
		 * @return
		 */
	 @RequestMapping("deleteNodes")
	 @ResponseBody
	 public String deleteNode(String nodeId){
		 Node node=nodeService.findNbyNodeId(Integer.parseInt(nodeId));
		 List<Node> nodeList=nodeService.findNodeByPlanId(node.getPlanId());
		 for (Node nod : nodeList) {
			if(nod.getNodeOrder()>node.getNodeOrder()){
				nod.setNodeOrder(nod.getNodeOrder()-1);
				nodeService.updateNode(nod);
			}
		}
		 int i=nodeService.deleteNode(Integer.parseInt(nodeId));
		 if(i==1){
			 return "success";
		 }else{
		 return "fail";}
			 
	 }
	 
	 
	 /**
		 * (进入)调序--->查看调序前的节点
		 * @param model
		 * @param planId
		 * @return
		 */
	 
	 @RequestMapping("toseeNode")
	 @ResponseBody
	 public String toseeNode(String planId){
		 List<Node> nodeList= nodeService.findNodeByPlanId(Integer.parseInt(planId));
		 if(nodeList.size()>1){
			 return "success";
		 }else{
			 return "fail";
		 }
	 }
	 
	 /**
		 * 调序--->查看调序前的节点
		 * @param model
		 * @param planId
		 * @return
		 */
	 
	 @RequestMapping("seeNode")
	 public String seeNode(Model model,String planId,String nodeId){
		 List<Node> nodeList= nodeService.findNodeByPlanId(Integer.parseInt(planId));

			 Node node=nodeService.findNbyNodeId(Integer.parseInt(nodeId));
			 model.addAttribute("nodeList", nodeList);
			 model.addAttribute("node", node);
			 
			 return "pages/nodeDecompose/sequencingnode";
	 }
	 
	 
	 /**
		 * 调序--->预览调序后的节点
		 * @param model
		 * @param planId
		 * @param newnodeId
		 * @param bj
		 * @return
		 */
	 
	 @RequestMapping("previewNode")
	 @ResponseBody
	 public List<Node> previewNode(String nodeId,String newnodeId,String bj){
		//原来的
		Node oldnode=nodeService.findNbyNodeId(Integer.parseInt(nodeId));
		//新的
		Node newnode=nodeService.findNbyNodeId(Integer.parseInt(newnodeId));
		
		List<Node> nodel=nodeService.findNodeByPlanId(newnode.getPlanId());
		if(Integer.parseInt(bj)==1){
		//移动到所选位置之后
		//新位置小于原位置
		if(newnode.getNodeOrder()<oldnode.getNodeOrder()){
			
			//小于
			for (Node node : nodel) {
				if(node.getNodeOrder()>newnode.getNodeOrder()&&node.getNodeOrder()<oldnode.getNodeOrder()){
					
					node.setNodeOrder(node.getNodeOrder()+1);
					
				}else if(node.getNodeOrder()==oldnode.getNodeOrder()){
					node.setNodeOrder(newnode.getNodeOrder()+1);
				}
			}
			
			
		}else{
			//大于
			for (Node node : nodel) {
				if(node.getNodeOrder()==oldnode.getNodeOrder()){
					node.setNodeOrder(newnode.getNodeOrder());
					
				}else if(node.getNodeOrder()>oldnode.getNodeOrder()&&node.getNodeOrder()<=newnode.getNodeOrder()){
					node.setNodeOrder(node.getNodeOrder()-1);
				}
			}
			
			
		}
		}else{
			//移动到所选位置之前
			//新位置小于原位置
			if(newnode.getNodeOrder()<oldnode.getNodeOrder()){
				for (Node node : nodel) {
				if(node.getNodeOrder()>=newnode.getNodeOrder()&&node.getNodeOrder()<oldnode.getNodeOrder()){
					node.setNodeOrder(node.getNodeOrder()+1);
				}else if(node.getNodeOrder()==oldnode.getNodeOrder()){
					node.setNodeOrder(newnode.getNodeOrder()-1);
				}
				}
				
			}else{
				//大于
				for (Node node : nodel) {
					
					if(node.getNodeOrder()==oldnode.getNodeOrder()){
						node.setNodeOrder(newnode.getNodeOrder()-1);
					}else if(node.getNodeOrder()>oldnode.getNodeOrder()&&node.getNodeOrder()<newnode.getNodeOrder()){
						node.setNodeOrder(node.getNodeOrder()-1);
					}
					
				}
				
			}
			
			
		}
		
		//根据序号排序
		comparatorstr c=new comparatorstr();
		Collections.sort(nodel,c);
			 return nodel;
	 }
	 
	 
	 
	 /**
		 * 保存排序后的节点
		 * @param model
		 * @param planId
		 * @return
		 */
	 @RequestMapping("updateNodesequencing")
	 @ResponseBody
	 public String updateNodesequencing(String nodeId,String newnodeId,String bj){
			//原来的
			Node oldnode=nodeService.findNbyNodeId(Integer.parseInt(nodeId));
			//新的
			Node newnode=nodeService.findNbyNodeId(Integer.parseInt(newnodeId));
			
			List<Node> nodel=nodeService.findNodeByPlanId(newnode.getPlanId());
			if(Integer.parseInt(bj)==1){
			//移动到所选位置之后
			//新位置小于原位置
			if(newnode.getNodeOrder()<oldnode.getNodeOrder()){
				
				//小于
				for (Node node : nodel) {
					if(node.getNodeOrder()>newnode.getNodeOrder()&&node.getNodeOrder()<oldnode.getNodeOrder()){
						node.setNodeOrder(node.getNodeOrder()+1);
						nodeService.updateNode(node);
						
						
					}else if(node.getNodeOrder()==oldnode.getNodeOrder()){
						node.setNodeOrder(newnode.getNodeOrder()+1);
						nodeService.updateNode(node);
					}
				}
				
				
			}else{
				//大于
				for (Node node : nodel) {
					if(node.getNodeOrder()==oldnode.getNodeOrder()){
						node.setNodeOrder(newnode.getNodeOrder());
						nodeService.updateNode(node);
					}else if(node.getNodeOrder()>oldnode.getNodeOrder()&&node.getNodeOrder()<=newnode.getNodeOrder()){
						node.setNodeOrder(node.getNodeOrder()-1);
						nodeService.updateNode(node);
					}
				}
				
				
			}
			}else if(Integer.parseInt(bj)==2){
				//移动到所选位置之前
				//新位置小于原位置
				if(newnode.getNodeOrder()<oldnode.getNodeOrder()){
					for (Node node : nodel) {
					if(node.getNodeOrder()>=newnode.getNodeOrder()&&node.getNodeOrder()<oldnode.getNodeOrder()){
						node.setNodeOrder(node.getNodeOrder()+1);
						nodeService.updateNode(node);
					}else if(node.getNodeOrder()==oldnode.getNodeOrder()){
						node.setNodeOrder(newnode.getNodeOrder()-1);
						nodeService.updateNode(node);
					}
					}
					
				}else{
					//大于
					for (Node node : nodel) {
						
						if(node.getNodeOrder()==oldnode.getNodeOrder()){
							node.setNodeOrder(newnode.getNodeOrder()-1);
							nodeService.updateNode(node);
						}else if(node.getNodeOrder()>oldnode.getNodeOrder()&&node.getNodeOrder()<newnode.getNodeOrder()){
							node.setNodeOrder(node.getNodeOrder()-1);
							nodeService.updateNode(node);
						}
						
					}
					
				}
				
				
			}else{
				return "fail";
			}
			
		 return "success";
	 }
	 
	 
	 /**
		 * 提交审批
		 * @param model
		 * @param plan
		 * @return
		 */
	 @RequestMapping("submitNode")
	 @ResponseBody
	 public String submitNode(Plan plan){
		 
		planService.updatePlan(plan);
		
		 return "success";
	 }
	 
	 
	//比较器
	 public class comparatorstr implements Comparator{
	 	public int compare(Object o1,Object o2){
	 		Node u1=(Node)o1;
	 		Node u2=(Node)o2;	
	 		
	 		int bijiao=u1.getNodeOrder().compareTo(u2.getNodeOrder());
	 		return bijiao;

	 	}

	 }
}
