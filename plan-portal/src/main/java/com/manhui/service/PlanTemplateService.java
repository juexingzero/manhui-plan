package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.PlanTemplate;


/**
* @ClassName: PlanTemplateService
* @description: 计划模板Service 接口
* @author: Jiangxiaosong	
* @date Create in 14:29 2017/9/29
* @version: v1.0
* @modify By:
* @Copyright: 版权由满惠科技拥有
*/
@Mapper
public interface PlanTemplateService {
	
	List<PlanTemplate> findAllPlanTemplate(PlanTemplate pt);
	
	
	int count(PlanTemplate pt);
	
	
	PlanTemplate findPlanTemplateById(PlanTemplate pt);
	
	
	int update(PlanTemplate pt);
	
	
	int delete(Integer templateId);
	
	//新增模板
	int insetTemplate(PlanTemplate pt);
	
	//不带条件查询全部模板
	List<PlanTemplate> findPlanTemplate();
	
	//取ID最大数
	int maxTemplateId();
}
