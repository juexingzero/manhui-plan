package com.manhui.model;

import java.util.Date;


/**
 * @ClassName: Reminders
 * @description: 执行计划催办实体类
 * @author:	HeJiayan
 * @date Create in 下午8:44:13 2017年10月24日
 * @version: v1.0.0
 * @modify By: WangSheng
 * @Copyright: 版权由满惠科技拥有
 */
public class Reminders extends BaseObject {
    private Integer id;                   //催办ID
 
    private Integer actionPlanId;         //执行计划ID

    private String reminderReason;        //催办原因

    private String reminderDescribe;      //催办描述

    private String reminderPerson;        //催办人

    private String reimnderTime;            //催办时间
    
    private String feedbackContext;       //反馈内容

    private Integer state;                //状态
    
    private ActionPlan actionPlan;		  //执行的计划
    
    private String planName;			  //主计划名称
    
    private String postponeTime;			//延期时间至
    
    
	public String getPostponeTime() {
		return postponeTime;
	}

	public void setPostponeTime(String postponeTime) {
		this.postponeTime = postponeTime;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public ActionPlan getActionPlan() {
		return actionPlan;
	}

	public void setActionPlan(ActionPlan actionPlan) {
		this.actionPlan = actionPlan;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionPlanId() {
        return actionPlanId;
    }

    public void setActionPlanId(Integer actionPlanId) {
        this.actionPlanId = actionPlanId;
    }

    public String getReminderReason() {
        return reminderReason;
    }

    public void setReminderReason(String reminderReason) {
        this.reminderReason = reminderReason == null ? null : reminderReason.trim();
    }

    public String getReminderDescribe() {
        return reminderDescribe;
    }

    public void setReminderDescribe(String reminderDescribe) {
        this.reminderDescribe = reminderDescribe == null ? null : reminderDescribe.trim();
    }

    public String getReminderPerson() {
        return reminderPerson;
    }

    public void setReminderPerson(String reminderPerson) {
        this.reminderPerson = reminderPerson == null ? null : reminderPerson.trim();
    }


    public String getReimnderTime() {
		return reimnderTime;
	}

	public void setReimnderTime(String reimnderTime) {
		this.reimnderTime = reimnderTime;
	}

	public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

	public String getFeedbackContext() {
		return feedbackContext;
	}

	public void setFeedbackContext(String feedbackContext) {
		this.feedbackContext = feedbackContext;
	}
}