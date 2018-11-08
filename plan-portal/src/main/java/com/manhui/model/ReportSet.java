package com.manhui.model;


/**
 * @ClassName: ReportSet
 * @description: 计划汇报设置表
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日20:25:22
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class ReportSet extends BaseObject{
	
	private Integer	reportSetId;		//计划汇报设置id
	
	private Integer	planId;				//关联原计划id
	
	private Integer	actionPlanId;		//关联执行计划id
	
	private String	subReportType;		//子计划汇报方式  1  日报   2 周报  3月报  	多个用英文逗号隔开
	
	private String	reportType;			//主计划汇报方式	1  日报   2 周报  3月报	多个用英文逗号隔开
	
	private Integer	subReportDate;		//子计划汇报日期		记录为周几
	
	private Integer	reportDate;			//主计划汇报日期		记录为周几
	
	private String	reportPerson;		//主计划汇报给计划以外的人员,多个用英文，隔开

	public Integer getReportSetId() {
		return reportSetId;
	}

	public void setReportSetId(Integer reportSetId) {
		this.reportSetId = reportSetId;
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

	public String getSubReportType() {
		return subReportType;
	}

	public void setSubReportType(String subReportType) {
		this.subReportType = subReportType;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public Integer getSubReportDate() {
		return subReportDate;
	}

	public void setSubReportDate(Integer subReportDate) {
		this.subReportDate = subReportDate;
	}

	public Integer getReportDate() {
		return reportDate;
	}

	public void setReportDate(Integer reportDate) {
		this.reportDate = reportDate;
	}

	public String getReportPerson() {
		return reportPerson;
	}

	public void setReportPerson(String reportPerson) {
		this.reportPerson = reportPerson;
	}

	@Override
	public String toString() {
		return "ReportSet [reportSetId=" + reportSetId + ", planId=" + planId + ", actionPlanId=" + actionPlanId
				+ ", subReportType=" + subReportType + ", reportType=" + reportType + ", subReportDate=" + subReportDate
				+ ", reportDate=" + reportDate + ", reportPerson=" + reportPerson + "]";
	}
	
	
}
