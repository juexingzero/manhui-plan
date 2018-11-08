package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Plan;

/**
 * @ClassName: MyResponsiblePlanService
 * @description: 我负责的计划的接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月19日19:41:08
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface MyResponsiblePlanService {
	
	/**
	 * 查询所有登陆用户负责的主计划的分页条件查询方法
	 * @param plan 传入的条件
	 * @return
	 */
	List<Plan> findPlanByBlamePerson(Plan plan);
	
	/**
	 * 查询所有登陆用户负责的主计划的数量的条件查询方法
	 * @param plan 传入的条件
	 * @return
	 */
	int count(Plan plan);
}
