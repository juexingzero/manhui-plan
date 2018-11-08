package com.manhui.model;

/**
 * @ClassName: Reject
 * @description: 驳回表
 * @author:	RanMaoKun
 * @date Create in 2017年11月22日09:56:38
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Reject extends BaseObject{
	
    private Integer rtId;		//驳回id

    private Integer planId;			//关联计划id

    private String rejectReason;	//驳回原因

    private String rejectPerson;	//驳回人

    private String rejectDate;		//驳回时间
    
    private Integer status;			//状态   0为被驳回   1为撤回原因   3节点驳回   4,计划调整被驳回
    
    public int getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


    public Integer getRtId() {
		return rtId;
	}

	public void setRtId(Integer rtId) {
		this.rtId = rtId;
	}

	public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason == null ? null : rejectReason.trim();
    }

    public String getRejectPerson() {
        return rejectPerson;
    }

    public void setRejectPerson(String rejectPerson) {
        this.rejectPerson = rejectPerson == null ? null : rejectPerson.trim();
    }

    public String getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(String rejectDate) {
        this.rejectDate = rejectDate == null ? null : rejectDate.trim();
    }
}