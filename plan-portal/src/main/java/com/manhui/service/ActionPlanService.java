package com.manhui.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.ActionPlan;

/**
 * @ClassName: ActionPlanService
 * @description: 执行的计划的接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月19日19:41:08
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface ActionPlanService {

	//保存执行计划
	int saveActionPlan(ActionPlan actionPlan);
	
	//更新执行计划
	int updateActionPlan(ActionPlan actionPlan);
	
	//通过执行计划的ID查找执行计划数据
	ActionPlan findActionPlanByPlanId(Integer actionPlanId);
	
	//查询出执行计划的总数量(分页用)
	int implementPlanCount(ActionPlan actionPlan);
	
	//查询执行计划的数据(分页用)
	List<ActionPlan> findImplementPlanLoadPage(ActionPlan actionPlan);
	
	//查询执行计划的子计划
	List<ActionPlan> findSubActionPlan(Integer parentId);

	//查出最大id
	int findMaxActionPlan();
	
	List<ActionPlan> findPlanLoadPage(ActionPlan actionPlan);
	
	int planCount(ActionPlan actionPlan);
	
	//计划验收查询所有待验收的子计划
	List<ActionPlan> findAcceptanceSubPlan(ActionPlan plan);
	
	//根据planId查询对应的执行计划
	ActionPlan findByPlanId(Integer planId);
	
	//根据id删除计划
	int deleteActionPlan(Integer actionPlanId);
	
	//查出所有需要汇报的主计划
	List<ActionPlan> findPlanSchedule();
	
	//我关注的计划查询子计划
	List<ActionPlan> findFollowNodeSubPlan(ActionPlan plan);
	
	//查询所有主计划
	List<ActionPlan> findMainActionPlan();

	//查询执行中计划的总条数
	@SuppressWarnings("rawtypes")
	int countExecutingPlans(Map criteria);
	
	//查询执行中计划
	@SuppressWarnings("rawtypes")
	List<ActionPlan> findExecutingPlans(Map criteria);

}
