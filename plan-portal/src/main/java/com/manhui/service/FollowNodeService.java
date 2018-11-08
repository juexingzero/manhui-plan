package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.FollowNode;

/**
 * @ClassName: FollowNodeService
 * @description: 关注计划节点的接口
 * @author:	jiangxiaosong
 * @date Create in 2017年10月28日16:20:08
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface FollowNodeService {

	List<FollowNode> findFollowNodeByFollowPerson(String followPerson);
	
	int deleteFollowNodes(Integer followNodeId);
	
	FollowNode findFollowNode(FollowNode followNode);
	
	int saveFollowNode(FollowNode followNode);
}
