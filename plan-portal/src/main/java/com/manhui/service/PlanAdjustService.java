package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Plan;
import com.manhui.model.PlanAdjust;

/**
 * @ClassName: PlanAdjustService
 * @description: 授权查看的计划的接口
 * @author:	WangSheng
 * @date Create in 2017年10月26日20:32:08
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface PlanAdjustService {

	//根据执行计划id查询调整申请记录表信息
	PlanAdjust findPlanAdjustByActionPlanId(Integer actionPlanId);
	
	//根据计划id查询调整申请记录表信息
	List<PlanAdjust> findPlanAdjustByPlanId(Integer planId);
	
	List<PlanAdjust> findPlanAdjustByPage(PlanAdjust planAdjust);
	
	//根据调整申请记录id查询信息
	PlanAdjust findPlanAdjustByPlanAdjustId(Integer planAdjustId);
	
	//新增调整记录
	int savePlanAdjustId(PlanAdjust planAdjust);
	
	//修改调整记录
	int updatePlanAdjustId(PlanAdjust planAdjust);
	
	int count(PlanAdjust plan);
	
	//查询所有的可调整的计划
	List<PlanAdjust> findAllPlanAdjust(Integer planType);
}
