package com.manhui.model;

import java.util.Date;

/**
 * @ClassName: PlanAdjust
 * @description: 调整申请记录实体类
 * @author:	WangSheng
 * @date Create in 下午20:04:13 2017年10月26日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class PlanAdjust extends BaseObject{
	
	private Integer planAdjustId;		//调整记录id
	
	private Integer planId;				//原计划id
	
	private Integer actionPlanId;		//执行计划id
	
	private Date postponeTime;		//延期时间
	
	private String adjustReason;		//调整原因
	
	private String adjustContent;		//调整内容
	
	private Integer postponeTimeLimit;		//延期工期
	
	private String planName;			//计划名称
	
	private Integer parentId;			//申请计划主计划id
	
	private String parentPlanName;		//申请计划主计划名
	
	private Integer auditProcess;		//审核流程
	
	private Integer status;				//调整类型	1:计划延期 2：计划暂停	3计划中止  4：计划变更	5：计划已延期	
	
	private String createTime;			//申请时间
	
	private Integer adjustType;			//申请状态   1:待审批 2：被驳回 3：审批通过 
	
	private Integer planType;			//计划类型    项目类，临时类，日常类
	
	private ActionPlan actionPlan;		//执行计划
	
	private String blameOrganization;        //负责机构(页面显示用)
    
    private String blameSection;             //负责部门(页面显示用)

    private String blamePerson;              //负责人(页面显示用)
    
    private String startTime;                //计划开始时间       (日常类：开始时间的小时数/周数/月数)
    
    private String startTimeDaily;           //计划开始时间       (日常类：开始时间的分钟数/周几/第几天)(不一定用)

    private String endTime;                  //计划结束时间        (日常类：结束时间的小时数/周数/月数)
    
    private String endTimeDaily;             //计划结束时间        (日常类：结束时间的分钟数/周几/第几天)(不一定用)
    
    private Integer planStatus;				//计划的状态(显示用)

	
	public String getAdjustContent() {
		return adjustContent;
	}

	public void setAdjustContent(String adjustContent) {
		this.adjustContent = adjustContent;
	}

	public Integer getAdjustType() {
		return adjustType;
	}

	public void setAdjustType(Integer adjustType) {
		this.adjustType = adjustType;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Integer getActionPlanId() {
		return actionPlanId;
	}

	public void setActionPlanId(Integer actionPlanId) {
		this.actionPlanId = actionPlanId;
	}

	public Date getPostponeTime() {
		return postponeTime;
	}

	public void setPostponeTime(Date postponeTime) {
		this.postponeTime = postponeTime;
	}

	public String getAdjustReason() {
		return adjustReason;
	}

	public void setAdjustReason(String adjustReason) {
		this.adjustReason = adjustReason;
	}

	public Integer getPostponeTimeLimit() {
		return postponeTimeLimit;
	}

	public void setPostponeTimeLimit(Integer postponeTimeLimit) {
		this.postponeTimeLimit = postponeTimeLimit;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getParentPlanName() {
		return parentPlanName;
	}

	public void setParentPlanName(String parentPlanName) {
		this.parentPlanName = parentPlanName;
	}

	public Integer getAuditProcess() {
		return auditProcess;
	}

	public void setAuditProcess(Integer auditProcess) {
		this.auditProcess = auditProcess;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPlanAdjustId() {
		return planAdjustId;
	}

	public void setPlanAdjustId(Integer planAdjustId) {
		this.planAdjustId = planAdjustId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public ActionPlan getActionPlan() {
		return actionPlan;
	}

	public void setActionPlan(ActionPlan actionPlan) {
		this.actionPlan = actionPlan;
	}

	public String getBlameOrganization() {
		return blameOrganization;
	}

	public void setBlameOrganization(String blameOrganization) {
		this.blameOrganization = blameOrganization;
	}

	public String getBlameSection() {
		return blameSection;
	}

	public void setBlameSection(String blameSection) {
		this.blameSection = blameSection;
	}

	public String getBlamePerson() {
		return blamePerson;
	}

	public void setBlamePerson(String blamePerson) {
		this.blamePerson = blamePerson;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTimeDaily() {
		return startTimeDaily;
	}

	public void setStartTimeDaily(String startTimeDaily) {
		this.startTimeDaily = startTimeDaily;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getEndTimeDaily() {
		return endTimeDaily;
	}

	public void setEndTimeDaily(String endTimeDaily) {
		this.endTimeDaily = endTimeDaily;
	}

	public Integer getPlanType() {
		return planType;
	}

	public void setPlanType(Integer planType) {
		this.planType = planType;
	}

	public Integer getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(Integer planStatus) {
		this.planStatus = planStatus;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
}
