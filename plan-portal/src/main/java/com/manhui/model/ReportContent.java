package com.manhui.model;


/**
 * @ClassName: ReportContent
 * @description: 汇报内容表
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日20:40:40
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class ReportContent extends BaseObject{
	
	private Integer	reportContentId;		//汇报内容id
	
	private Integer	reportId;				//汇报id
	
	private String	reportContentName;		//内容名字
	
	private String	reportContentTime;		//汇报内容时间（由读取的子计划的开始时间和结束时间组成）
	
	private Integer	reportContentStatus;	//汇报内容状态	根据计划本身的状态来确定
	
	private Integer	finishDegree;			//完成度
	
	private String	reportContent;			//汇报内容

	public Integer getReportContentId() {
		return reportContentId;
	}

	public void setReportContentId(Integer reportContentId) {
		this.reportContentId = reportContentId;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getReportContentName() {
		return reportContentName;
	}

	public void setReportContentName(String reportContentName) {
		this.reportContentName = reportContentName;
	}

	public String getReportContentTime() {
		return reportContentTime;
	}

	public void setReportContentTime(String reportContentTime) {
		this.reportContentTime = reportContentTime;
	}

	public Integer getReportContentStatus() {
		return reportContentStatus;
	}

	public void setReportContentStatus(Integer reportContentStatus) {
		this.reportContentStatus = reportContentStatus;
	}

	public Integer getFinishDegree() {
		return finishDegree;
	}

	public void setFinishDegree(Integer finishDegree) {
		this.finishDegree = finishDegree;
	}

	public String getReportContent() {
		return reportContent;
	}

	public void setReportContent(String reportContent) {
		this.reportContent = reportContent;
	}
	
}
