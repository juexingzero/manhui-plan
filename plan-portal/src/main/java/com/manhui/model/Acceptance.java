package com.manhui.model;

/**
 * @ClassName: Acceptance
 * @description: 验收表
 * @author:	RanMaoKun
 * @date Create in 2017年10月26日20:55:23
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Acceptance extends BaseObject{
	
	private	Integer	acceptanceId;			//验收id
	
	private	Integer	actionPlanId;			//关联执行计划id
	
	private	Integer	acceptanceResult;		//验收结果 1按标准完成   2未按标准完成
	
	private	String	remark;					//备注
	
	private String finishTime;				//完成时间
	
	private String resultDescribe;			//成果描述

	public Integer getAcceptanceId() {
		return acceptanceId;
	}

	public void setAcceptanceId(Integer acceptanceId) {
		this.acceptanceId = acceptanceId;
	}

	public Integer getActionPlanId() {
		return actionPlanId;
	}

	public void setActionPlanId(Integer actionPlanId) {
		this.actionPlanId = actionPlanId;
	}

	public Integer getAcceptanceResult() {
		return acceptanceResult;
	}

	public void setAcceptanceResult(Integer acceptanceResult) {
		this.acceptanceResult = acceptanceResult;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String getResultDescribe() {
		return resultDescribe;
	}

	public void setResultDescribe(String resultDescribe) {
		this.resultDescribe = resultDescribe;
	}
	
}
