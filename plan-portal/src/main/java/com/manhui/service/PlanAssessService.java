package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.PlanAssess;

@Mapper
public interface PlanAssessService {
	
	//删除
    int deleteByPlanAssessId(Integer id);
    
    //根据计划删除
    int deleteByPlanId(Integer planId);
    
    //新增
    int insertPlanAssess(PlanAssess record);
    
    //根据考核id查询考核项
    PlanAssess selectByPlanAssessId(Integer id);
    
    //修改
    int updateByPlanAssessId(PlanAssess id);
    
    //通过计划id查询考核项
    List<PlanAssess> selectByPlanId(Integer id);
    
    //取得最大id
    int findMaxId();

}