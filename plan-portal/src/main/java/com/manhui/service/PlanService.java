package com.manhui.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Plan;

/**
 * @ClassName: PlanService
 * @description: 计划管理 Service 接口
 * @author:	HeJiayan
 * @date Create in 下午6:03:07 2017年10月6日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface PlanService {

	List<Plan> findSubPlans(Integer parentId);

	void savePlan(Plan plan);

	void updatePlan(Plan plan);

	Plan findByPlan(Plan plan);
	
	Plan findById(Integer planId);

	@SuppressWarnings("rawtypes")
	List<Plan> findPlansByOrderAndParentId(Map criteria);
	
	int findMaxPlanId();
	
	void deletePlanById(Integer planId);
	
	//条件查询分解节点计划
	List<Plan> findPlan(Plan plan);
	
	//条件查询已过审主计划
	List<Plan> findApprovedPlan(Plan plan);
	
	//条件查询已过审主计划(除开临时类计划)
	List<Plan> findApprovedPlanExceptInterimPlan(Plan plan);
	
	//条件查询分解节点计划数量
	Integer count(Plan plan);
	
	//根据一定条件查询出子计划
	List<Plan> findPlansByPlan(Plan plan);
	
    //条件查询审批计划
	List<Plan> findPlanApproval(Plan plan);
	
	//条件查询审批节点计划
	List<Plan> findPlanNodeApproval(Plan plan);
	
	//查询出审批计划数量
	int planApprovalCount(Plan plan);
	
	//查询审批节点计划数量
	int planNodeApprovalCount(Plan plan);
	
	//查询主计划
	List<Plan> findMainPlans(Map criteria);
	
	//查询董事长-分子公司负责人-中心负责人的公司计划
	List<Plan> findCoreMainPlan(Plan plan);
	
	//查询中心计划
	List<Plan> findCorePlan(Plan plan);
	
	int getDecomPlancount(Plan plan);

	List<Plan> getDecomPlans(Plan plan);

	int getDraftsCount(Plan plan);

	List<Plan> findDrafts(Plan plan);

	int countApprovedPlan(Plan plan);
	
	//查询所有能被调整的计划
	List<Plan> findAdjustPlan(String blamePerson);

	List<Plan> findAllPlan();
	
	List<Plan> findDCoreMainPlan(Plan plan);
}
