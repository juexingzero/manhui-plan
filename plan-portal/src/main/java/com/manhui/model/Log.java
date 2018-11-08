package com.manhui.model;

import java.sql.Timestamp;


/**
 * @ClassName: Log
 * @description: 日志类
 * @author:	RanMaoKun
 * @date Create in 2017年10月19日 18:02:01
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Log extends BaseObject{
	
	private Integer logId;   		//日志id
	
	private Integer actionPlanId;			//关联计划id
	
	private Timestamp createDate;	//创建日志时间
	
	private String  content;		//日志内容
	
	private String createPerson;	//创建人
	
	

	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public Integer getActionPlanId() {
		return actionPlanId;
	}

	public void setActionPlanId(Integer actionPlanId) {
		this.actionPlanId = actionPlanId;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
