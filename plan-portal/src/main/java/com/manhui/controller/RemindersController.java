package com.manhui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.manhui.model.ActionPlan;
import com.manhui.model.PlanAdjust;
import com.manhui.model.Reminders;
import com.manhui.service.ActionPlanService;
import com.manhui.service.PlanAdjustService;
import com.manhui.service.RemindersService;
import com.manhui.util.PageUtil;

/**
 * @ClassName: RemindersController
 * @Description: 被催办的计划控制器 
 * @author WangSheng
 * @date: 2017年10月26日13:59:02
 */
@Controller
public class RemindersController {

	@Autowired
	private RemindersService remindersService;
	
	@Autowired
	private ActionPlanService actionPlanService;
	
	@Autowired
	private PlanAdjustService planAdjustService;
	
	
	/**
	 * 对被催办的计划进行分页显示
	 * @param reminders
	 * @param pageCurrent
	 * @param pageSize
	 * @param pageCount
	 * @param model
	 * @return
	 */
	@GetMapping("/toFindReminders_{pageCurrent}_{pageSize}_{pageCount}")
	public String toFindReminders(Model model,Reminders reminders,@PathVariable Integer pageCurrent,
									@PathVariable Integer pageSize,@PathVariable Integer pageCount){
		
		//转模糊查询(催办原因)
				String reminderReason=reminders.getReminderReason();
				if(reminderReason!=null){
					reminderReason="%"+reminderReason+"%";
					reminders.setReminderReason(reminderReason);
					
				}
		
		if(pageSize == 0) pageSize = 10;
		if(pageCurrent == 0) pageCurrent = 1;
		int rows=this.remindersService.count(reminders);
		if(pageCount == 0) pageCount = rows%pageSize == 0 ? (rows/pageSize) : (rows/pageSize) + 1;
		reminders.setStart((pageCurrent - 1)*pageSize);
		reminders.setEnd(pageSize);
		List<Reminders> remindersList=remindersService.findRemindersLoadPage(reminders);
		
		for (Reminders rs : remindersList) {
		
			//根据actionplanid查询主计划信息
			//第一层
			
			ActionPlan actionplan=actionPlanService.findActionPlanByPlanId(rs.getActionPlanId());
			rs.setActionPlan(actionplan);
			if(actionplan.getActionParentId()==null){
				rs.setPlanName("无");
			}else{
			ActionPlan actionplanpo=actionPlanService.findActionPlanByPlanId(actionplan.getActionParentId());
			if(actionplanpo.getActionParentId()==null){
				rs.setPlanName(actionplanpo.getActionPlanName());
			}else{
				//第二层
				ActionPlan actionplanpt=actionPlanService.findActionPlanByPlanId(actionplanpo.getActionParentId());
				
				if(actionplanpt.getActionParentId()==null){
					rs.setPlanName(actionplanpt.getActionPlanName());
				}else{
					//第三层
					ActionPlan actionplanps=actionPlanService.findActionPlanByPlanId(actionplanpt.getActionParentId());	
					if(actionplanps.getActionParentId()==null){
						rs.setPlanName(actionplanps.getActionPlanName());
					}else{
						//第四层
						ActionPlan actionplanpf=actionPlanService.findActionPlanByPlanId(actionplanps.getActionParentId());
						rs.setPlanName(actionplanpf.getActionPlanName());
					}
				}
			}
				
		}}

		
		model.addAttribute("remindersList", remindersList);
		
		String pageHTML=PageUtil.getPageContent("toFindReminders_{pageCurrent}_{pageSize}_{pageCount}?"
				+ "planName="+reminders.getId(),
				pageCurrent, pageSize, pageCount);
		model.addAttribute("pageHTML",pageHTML);
		//返回网页
		return "pages/planReminders/titleplan";
	}
	
	
	/**
	 * 查看被催办的计划详情
	 * @param id
	 * @param model
	 * @return
	 */
	
	@RequestMapping("/handreminders")
	public String handReminders(String id,Model model){
		
		Reminders rs=remindersService.findRemindersById(Integer.parseInt(id));
		
		//根据actionplanid查询被催办计划信息
		ActionPlan actionplan=actionPlanService.findActionPlanByPlanId(rs.getActionPlanId());
		rs.setActionPlan(actionplan);
		PlanAdjust planadjust=planAdjustService.findPlanAdjustByActionPlanId(actionplan.getActionParentId());
		if(planadjust!=null){
			rs.setPostponeTime(planadjust.getPostponeTime().toString());	
		}else{
			rs.setPostponeTime("无");
		}
		
		model.addAttribute("rs", rs);
		
		return "pages/planReminders/handreminders";
	}
	
	
	/**
	 * 设置处理结果
	 * @param reminders
	 * @return
	 */
	@GetMapping("/savafeedbackContext")
	public @ResponseBody String savafeedbackContext(Reminders reminders,String feedbackContext){
		
		System.out.println("id："+reminders.getId());
		System.out.println("内容："+reminders.getFeedbackContext());
		System.out.println("内容："+feedbackContext);
		
		String msg="保存成功";
		
		try {
			this.remindersService.savafeedbackContext(reminders);
			
		} catch (Exception e) {
			 msg="保存失败";
			e.printStackTrace();
			return msg;
		}
		
		return msg;
	}
}
