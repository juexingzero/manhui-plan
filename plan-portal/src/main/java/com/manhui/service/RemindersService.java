package com.manhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.manhui.model.Reminders;

/**
 * @ClassName: RemindersService
 * @description: 计划催办 Service 接口
 * @author:	jiangxiaosong
 * @date Create in 下午8:28:07 2017年10月24日
 * @version: v1.0.0
 * @modify By: WangSheng
 * @Copyright: 版权由满惠科技拥有
 */
@Mapper
public interface RemindersService {
	
	List<Reminders> findRemindersLoadPage(Reminders reminders);
	
	Reminders findRemindersByActionPlanId(Integer ActionPlanId);
	
	Reminders findRemindersById(Integer id);
	
	int count(Reminders reminders);
	
	//保存处理结果
	void savafeedbackContext(Reminders reminders);
}
