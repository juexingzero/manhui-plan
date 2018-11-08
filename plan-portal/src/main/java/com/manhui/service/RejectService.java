package com.manhui.service;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Reject;

/**
 * @ClassName: RejectService
 * @description: 驳回的接口
 * @author:	RanMaoKun
 * @date Create in 2017年11月22日10:20:02
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface RejectService {
	
	//根据驳回id删除
    int deleteByRejectId(Integer rejectId);
    
    //新增驳回
    int saveReject(Reject reject);
    
    //根据计划id查询驳回
    Reject selectRejectByPlanId(@SuppressWarnings("rawtypes") Map reject);
    
    //修改驳回
    int updateByRejectId(Reject reject);
    
  //根据计划id查询计划驳回原因
    Reject selectRByPlanId(Integer planId);
}