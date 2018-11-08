package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Node;


/**
 * @ClassName: NodeService
 * @description: 节点的接口
 * @author:	WangSheng
 * @date Create in 2017年11月20日11:32:08
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface NodeService {
    
	//查询计划 对应节点
	List<Node> findNodeByPlanId(Integer planId);
	
	//查询计划 对应节点审批通过数量
	Integer findNodeByStatus(Integer planId);
	
	//修改节点状态
	int updateNodeStatus(Node node); 
	
	
	//添加
	int addNode(Node node);
	
	//修改
	int updateNode(Node node);

	//删除
	int deleteNode(Integer nodeId);
	
	//根据节点id查询节点
	Node findNbyNodeId(Integer nodeId);
	
	//查询节点最大排序号
	String findorderbyPlanId(Integer planId);
	
	//根据计划id修改节点状态
	int updateStatusByPlanId(Node node);
}