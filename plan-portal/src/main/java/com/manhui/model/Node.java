package com.manhui.model;

/**
 * @ClassName: Node
 * @description: 节点表
 * @author:	WangSheng
 * @date Create in 2017年11月20日10:52:31
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Node {
    private Integer nodeId;                 //节点编号

    private String nodeName;                //节点名称

    private String nodeDescribe;            //节点描述

    private String startTime;               //开始时间

    private String endTime;                 //结束时间

    private String remark;                  //备注

    private Integer status;                 //节点状态		1:待审批（草稿）	2：未开始	3：进行中  4：已结束  

    private Integer planId;                 //计划id
    
    private Integer nodeOrder;				//序号
    
    private Integer timeLimit;				//工期
    
    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName == null ? null : nodeName.trim();
    }

    public String getNodeDescribe() {
        return nodeDescribe;
    }

    public void setNodeDescribe(String nodeDescribe) {
        this.nodeDescribe = nodeDescribe == null ? null : nodeDescribe.trim();
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

	public Integer getNodeOrder() {
		return nodeOrder;
	}

	public void setNodeOrder(Integer nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}
}