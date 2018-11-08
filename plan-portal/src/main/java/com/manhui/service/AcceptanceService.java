package com.manhui.service;


import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Acceptance;


/**
 * @ClassName: AcceptanceService
 * @description: 验收表的接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日21:14:36
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface AcceptanceService {
   
	int saveAcceptance(Acceptance acceptance);
	
	//根据计划id查询验收信息
	Acceptance findAcceptanceByPlanId(Integer planId);
	
	//修改验收信息
	int updateAcceptance(Acceptance acceptance);
	
}