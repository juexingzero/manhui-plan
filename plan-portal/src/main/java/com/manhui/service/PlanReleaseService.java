/**
 * 
 */
package com.manhui.service;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Plan;

/**
 * @ClassName: PlanReleaseService
 * @description: 计划申请 Service 接口
 * @author:	RanMaoKun
 * @date Create in 上午09:33:11 2017年10月16日 
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface PlanReleaseService {

	List<Plan> findAllMainPlan(Plan plan);
	
	int count(Plan plan);
}
