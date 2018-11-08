package com.manhui.service;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.ReportSet;

/**
 * @ClassName: ReportSetService
 * @description: 计划汇报设置表的接口
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日21:14:36
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface ReportSetService {
	
	//新增汇报
    int saveReportSet(ReportSet reportSet);
    
    //修改汇报
    int updateReportSet(ReportSet reportSet);
    
    //根据planid查询汇报
    ReportSet findByPlanId(Integer planId);
}