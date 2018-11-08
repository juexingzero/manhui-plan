package com.manhui.model;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: Report
 * @description: 汇报表
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日20:32:00
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Report extends BaseObject{
	
	private Integer	reportId;			//汇报id
	
	private Integer	planId;				//关联执行计划id
	
	private Integer	finishDegree;		//完成度
	
	private String	mainPlanName;		//主计划名称
	
	private String	planName;			//计划名称
	
	private String	reportPerson;		//汇报人	就是计划的负责人  
	
	private String	reportObject;		//汇报对象  
	
	private Integer	status;				//汇报状态  1、 未汇报   2、已超时	3、已汇报  
	
	private Date shouldReportTime;		//应该汇报日期
	
	private Date realityReportTime;		//实际汇报日期
	
	private String remark;  			//备注  员工为日报
	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Integer getFinishDegree() {
		return finishDegree;
	}

	public void setFinishDegree(Integer finishDegree) {
		this.finishDegree = finishDegree;
	}

	public String getMainPlanName() {
		return mainPlanName;
	}

	public void setMainPlanName(String mainPlanName) {
		this.mainPlanName = mainPlanName;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getShouldReportTime() {
		return shouldReportTime;
	}

	public void setShouldReportTime(Date shouldReportTime) {
		this.shouldReportTime = shouldReportTime;
	}

	public Date getRealityReportTime() {
		return realityReportTime;
	}

	public void setRealityReportTime(Date realityReportTime) {
		this.realityReportTime = realityReportTime;
	}

}
