package com.manhui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.manhui.model.User;
import com.manhui.service.NodeService;
import com.manhui.service.PlanService;

/**
 * @ClassName: NodeAddController
 * @description: 任务补充控制器
 * @author:	RanMaoKun
 * @date Create in 2017年11月25日10:22:45
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Controller
public class NodeAddController {
	
	//任务
	@Autowired
	private NodeService nodeService;
	
	//计划
	@Autowired
	private PlanService planService;
	
	/**
     * @description: 任务补充列表页面跳转
     * @param 
     * @return 
     * @throws
     */
    @RequestMapping("/node_add")
    public ModelAndView nodeAdd(){
    	ModelAndView mv = new ModelAndView("pages/nodeAdd/node_add");
		return mv;
    }
    
    /**
     * 登录用户补充分解的计划
     * @param model
     * @param plan
     * @param pageCurrent
     * @param pageSize
     * @param pageCount
     * @return
     */
    @GetMapping("/plan_nodeAdd_{pageCurrent}_{pageSize}_{pageCount}")
    public String planNodeAdd(Model model,Plan plan,@PathVariable Integer pageCurrent,
    		@PathVariable Integer pageSize,@PathVariable Integer pageCount){
    	//转模糊查询
		String planName=plan.getPlanName();
		if(null!=planName&&planName!=""){
			planName="%"+planName+"%";
			plan.setPlanName(planName);
		}
		User user = (User) CacheManager.getCacheInfo("user").getValue();
		plan.setBlamePerson(user.getName());
		//判断
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows=this.planService.count(plan);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		//查询所有计划模板
		plan.setStart((pageCurrent - 1)*pageSize);
		plan.setEnd(pageSize);
		List<Plan> pList=this.planService.findPlan(plan);	//查询登录用户补充分解的计划
		List<Plan> plans=new ArrayList<>();
		for(Plan p:pList){
    		List<Node> nodes=this.nodeService.findNodeByPlanId(p.getPlanId());
    		Integer nd=this.nodeService.findNodeByStatus(p.getPlanId());	//查询对应计划下面审批过任务数
    		if(nd!=null&&nd!=0&&nodes.size()!=nd){
    			//说明补充分解过
    				for(Node node:nodes){
            			if("".equals(node.getNodeName())||node.getNodeName()==null||node.getNodeName()==""){
            				p.setDecomStatus(nodes.size()-1); //用来暂时存放任务数
            			}else{
            				p.setDecomStatus(nodes.size());
            			}
            		}
        		plans.add(p);
    		}
    	}
		model.addAttribute("pList",plans);
		model.addAttribute("pageCurrent", pageCurrent);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("rows", rows);
		model.addAttribute("pageCount", pageCount);
		return "pages/nodeAdd/node_add_planList";	
    }
    
    
    /**
     * 补充分解选择计划窗口
     * @return
     */
    @RequestMapping("/findNodePlan")
    public String findNodePlan(Model model){
    	Plan plan=new Plan();
    	User user = (User) CacheManager.getCacheInfo("user").getValue();
		plan.setBlamePerson(user.getName());	//得到登录用户名加进去
		plan.setStart(0);
		plan.setEnd(100);
    	List<Plan> pList=this.planService.findPlan(plan);	//查询登录用户补充分解的计划
		for(Plan p:pList){
			List<Node> nodes=this.nodeService.findNodeByPlanId(p.getPlanId());
			Integer nd=this.nodeService.findNodeByStatus(p.getPlanId());
			if(null!=nodes&&nodes.size()>0){
				if(nd==0){	//说明有任务但没有审批过
					pList.remove(p);
				}
			}else{
				pList.remove(p);		//说明没分解过
			}
		}
		model.addAttribute("pList",pList);
		return "pages/nodeAdd/find_node_plan";
    }
   
    /**
     * 任务补充页面
     * @param planId
     * @param model
     * @return
     */
    @GetMapping("/addNodePage")
    public String addNodePage(Integer planId,Model model){
    	Plan plan=this.planService.findById(planId);
    	List<Node> nodes=this.nodeService.findNodeByPlanId(planId);
    	int nd=this.nodeService.findNodeByStatus(planId);
    	if(nodes.size()==nd){
    		Node node=new Node();
    		node.setStatus(1);
    		nodes.add(node);
    	}
    	model.addAttribute("plan",plan);
    	model.addAttribute("nodes",nodes);
    	return "pages/nodeAdd/add_node_page";
    }
    
    /**
     * 新增任务页面窗口
     * @param planId
     * @param model
     * @return
     */
    @GetMapping("/addPlanNode")
    public String addNode(String planId,Integer ndId,Model model){
    	Node node=this.nodeService.findNbyNodeId(ndId);
    	String size="";
    	if(node==null||"".equals(node.getNodeName())||null==node.getNodeName()){
    		size="0";
    	}else{
    		size="1";
    	}
    	model.addAttribute("size", size);
    	model.addAttribute("planId", planId);
    	if(ndId!=null){
    		model.addAttribute("ndId", ndId);
    		model.addAttribute("nodeName", node.getNodeName());
    	}
		return "pages/nodeAdd/add_plan_node";
    }
    
    /**
     * 保存新增任务
     * @param node
     * @param planId
     * @param location
     * @param size
     * @param ndId
     * @return
     */
    @GetMapping("/saveNode")
    @ResponseBody
    public String saveNode(Node node,Integer planId,String nodeNam,String ahead,Integer ndId){
    	List<Node> list=this.nodeService.findNodeByPlanId(planId);
    	int nd=this.nodeService.findNodeByStatus(planId);
    	Node ndNode=this.nodeService.findNbyNodeId(ndId);
    	String msg="true";
    	node.setNodeName(nodeNam);
    	node.setPlanId(planId);
    	node.setStatus(1);
    	if("".equals(ahead)||ahead==null){	//说明是补充第一个任务
    		if(list.size()>nd){
    			for(Node n:list){
    				if("".equals(n.getNodeName())||null==n.getNodeName()){
    					n.setNodeDescribe(node.getNodeDescribe());
    					n.setStartTime(node.getStartTime());
    					n.setEndTime(node.getEndTime());
    					n.setNodeName(node.getNodeName());
    					int i=this.nodeService.updateNode(n);
    					if(i!=1){
    						msg="false";
    					}
    				}
    			}
    		}else{
    			node.setNodeOrder(nd+1);
    			int i=this.nodeService.addNode(node);
    			if(i!=1){
					msg="false";
				}
    		}
    	}else{
    		int ndOrder=ndNode.getNodeOrder();
    		if("0".equals(ahead)){	//加在任务前面
    			node.setNodeOrder(ndOrder);
    			for(Node n:list){
    				if(n.getNodeOrder()>(ndOrder-1)){
    					n.setNodeOrder(n.getNodeOrder()+1);
    					int i=this.nodeService.updateNode(n);
    					if(i!=1){
    						msg="false";
    					}
    				}
    			}
    			this.nodeService.addNode(node);
        	}
    		if("1".equals(ahead)){	//加在任务后面
    			node.setNodeOrder(ndOrder+1);
    			for(Node n:list){
    				if(n.getNodeOrder()>(ndOrder)){
    					n.setNodeOrder(n.getNodeOrder()+1);
    					int i=this.nodeService.updateNode(n);
    					if(i!=1){
    						msg="false";
    					}
    				}
    			}
    			this.nodeService.addNode(node);
        	}
    	}
		return msg;
    }
    
    /**
     * 任务编辑页面
     * @param nodeId
     * @param model
     * @return
     */
    @GetMapping("/nodeUpdate")
    public String nodeUpdate(Integer nodeId,Model model){
    	Node node=this.nodeService.findNbyNodeId(nodeId);
    	model.addAttribute("node", node);
		return "pages/nodeAdd/node_update";
    }
    
    
    /**
     * 保存编辑
     * @param node
     * @param nodeNam
     * @return
     */
    @GetMapping("/saveUpdateNode")
    @ResponseBody
    public String saveUpdateNode(Node node,String nodeNam){
    	node.setNodeName(nodeNam);
    	int i=this.nodeService.updateNode(node);
    	if(i!=1){
    		return "2";
    	}
		return "1";
    }
    
    /**
     * 删除任务
     * 
     * @param nodeId
     * @return
     */
    @GetMapping("/deleteNode")
    @ResponseBody
    public String deleteNode(Integer nodeId){
    	String msg="true";
    	Node node=this.nodeService.findNbyNodeId(nodeId);
    	int nodeOrder=node.getNodeOrder();
    	List<Node> nodes=this.nodeService.findNodeByPlanId(node.getPlanId());
    	int i=this.nodeService.deleteNode(nodeId);
    	if(i!=1){
    		msg="false";
    	}else{
    		for(Node n:nodes){
    			if(n.getNodeOrder()>nodeOrder){
    				n.setNodeOrder(n.getNodeOrder()-1);
    				this.nodeService.updateNode(node);
    			}
    		}
    	}
		return msg;
    }
    
    /**
     * 删除所有补充任务
     * @param planId
     * @return
     */
    @GetMapping("/deleteAll")
    @ResponseBody
    public String deleteAll(Integer planId){
    	List<Node> nodes=this.nodeService.findNodeByPlanId(planId);
    	for(Node n:nodes){
    		if(n.getStatus()==1){
    			int i= this.nodeService.deleteNode(n.getNodeId());
    			if(i!=1){
    				return "2";
    			}
    		}
    	}
    	return "1";
    }
    
    
    /**
     * 列表点击查看
     * @param planId
     * @param model
     * @return
     */
    @GetMapping("/detailPlanNode")
    public String detailPlanNode(Integer planId,Model model){
    	Plan plan=this.planService.findById(planId);
    	List<Node> nodes=this.nodeService.findNodeByPlanId(planId);
    	model.addAttribute("plan", plan);
    	model.addAttribute("nodes", nodes);
		return "pages/nodeAdd/detail_plan_node";
    }
    
    /**
     * 任务编辑和查看的方法
     * @param planId
     * @param model
     * @return
     */
    @GetMapping("/plan_node_detail")
    public String planNodeDetail(Integer planId,Model model){
    	Plan plan=this.planService.findById(planId);
    	List<Node> nodes=this.nodeService.findNodeByPlanId(planId);
    	model.addAttribute("plan", plan);
    	model.addAttribute("nodes", nodes);
		return "pages/nodeAdd/plan_node_detail";
    }
    
    /**
     * 保存草稿  
     * @param planId
     * @return
     */
    @GetMapping("/saveInitial")
    @ResponseBody
    public String saveInitial(Integer planId){
    	String msg="1";
    	List<Node> nodes=this.nodeService.findNodeByPlanId(planId);
    	int nd=this.nodeService.findNodeByStatus(planId);
    	if(nodes.size()==nd){  	//如果没有补充分解直接保存草稿 新增一条空数据
    		Node node=new Node();
    		node.setStatus(1);
    		node.setNodeOrder(nd+1);
    		node.setPlanId(planId);
    		int i=this.nodeService.addNode(node);
    		if(i!=1){
    			msg="2";
    		}
    	}
		return msg;
    }
    
    /**
     * 提交审批
     * @param planId
     * @return
     */
    @GetMapping("/putApprove")
    @ResponseBody
    public String putApprove(Integer planId){
    	String msg="1";
    	Plan plan=this.planService.findById(planId);
    	plan.setNodeStatus(2);
    	this.planService.updatePlan(plan);
		return msg;
    }
    
    /**
	 * (进入)调序--->查看调序前的节点
	 * @param model
	 * @param planId
	 * @return
	 */
	 @RequestMapping("toseeNodeAdd")
	 @ResponseBody
	 public String toseeNode(String planId){
		 List<Node> nodeList= nodeService.findNodeByPlanId(Integer.parseInt(planId));
		 List<Node> nodes=new ArrayList<>();
		 for(Node node:nodeList){
			 if(node.getStatus()==1&&(null!=node.getNodeName()||""!=node.getNodeName())){
				 nodes.add(node);
			 }
		 }
		 if(nodes.size()>1){
			 return "success";
		 }else{
			 System.out.println("fail");
			 return "fail";
		 }
	 }
	 
	 /**
		 * 调序--->查看调序前的节点
		 * @param model
		 * @param planId
		 * @return
		 */
	 
	 @RequestMapping("seeNodeAdd")
	 public String seeNode(Model model,String planId,String nodeId){
		 List<Node> nodeList= nodeService.findNodeByPlanId(Integer.parseInt(planId));
		 List<Node> nodes=new ArrayList<>();
		 for(Node node:nodeList){
			 if(node.getStatus()==1&&(null!=node.getNodeName()||""!=node.getNodeName())){
				 nodes.add(node);
			 }
		 }
		Node node=nodeService.findNbyNodeId(Integer.parseInt(nodeId));
		model.addAttribute("nodeList", nodes);
		model.addAttribute("node", node);
		return "pages/nodeAdd/sequencingnodeadd";
	 }
	 
	 
	 /**
		 * 调序--->预览调序后的节点
		 * @param model
		 * @param planId
		 * @param newnodeId
		 * @param bj
		 * @return
		 */
	 
	 @SuppressWarnings("unchecked")
	 @RequestMapping("previewNodeAdd")
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
	 @RequestMapping("updateNodeAddsequencing")
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
	 //比较器
		@SuppressWarnings("rawtypes")
		public class comparatorstr implements Comparator{
		 	public int compare(Object o1,Object o2){
		 		Node u1=(Node)o1;
		 		Node u2=(Node)o2;	
		 		int bijiao=u1.getNodeOrder().compareTo(u2.getNodeOrder());
		 		return bijiao;
		 	}
		 }
}
