package com.manhui.model;

/**
 * @ClassName: SeeReport
 * @description: 查看计划日志的条件
 * @author:	WangSheng
 * @date Create in 2017年10月31日17:26:40
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class SeeReport extends BaseObject{

	private String reportTime;
	
	private String reportType;
	
	private String planName;
	
	private String mainPlanName;
	
	private String reportPerson;
	
	private String reportObject;
	
	private String blameOrgminzation;
	
	private String blameSection;

	public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getMainPlanName() {
		return mainPlanName;
	}

	public void setMainPlanName(String mainPlanName) {
		this.mainPlanName = mainPlanName;
	}

	public String getReportPerson() {
		return reportPerson;
	}

	public void setReportPerson(String reportPerson) {
		this.reportPerson = reportPerson;
	}

	public String getReportObject() {
		return reportObject;
	}

	public void setReportObject(String reportObject) {
		this.reportObject = reportObject;
	}

	public String getBlameOrgminzation() {
		return blameOrgminzation;
	}

	public void setBlameOrgminzation(String blameOrgminzation) {
		this.blameOrgminzation = blameOrgminzation;
	}

	public String getBlameSection() {
		return blameSection;
	}

	public void setBlameSection(String blameSection) {
		this.blameSection = blameSection;
	}
	
	
	
}
