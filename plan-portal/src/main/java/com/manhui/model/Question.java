 package com.manhui.model;

/**
 * @ClassName: Question
 * @description: 待处理问题表
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日20:45:51
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Question extends BaseObject{
	
	private Integer	questionId;			//问题表id
	
	private Integer	reportId;				//汇报id
	
	private String	questionContent;	//问题内容
	
	private String	solution;			//解决方案
	
	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

}
