package com.manhui.model;


/**
 * @ClassName: FollowNode
 * @description: 关注节点信息实体类
 * @author:	jiangxiaosong
 * @date Create in 下午4:15:13 2017年10月28日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class FollowNode {
    private Integer id;

    private Integer actionPlanId;      //执行计划ID(暂时废弃)

    private Integer planId;            //计划ID

    private String planName;           //关注计划名称
    
    private String followPerson;      //关注人

    private String startTime;          //关注计划开始时间

    private String endTime;            //关注计划结束时间

    private Integer state;             //状态 默认为1

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActionPlanId() {
        return actionPlanId;
    }

    public void setActionPlanId(Integer actionPlanId) {
        this.actionPlanId = actionPlanId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName == null ? null : planName.trim();
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

	public String getFollowPerson() {
		return followPerson;
	}

	public void setFollowPerson(String followPerson) {
		this.followPerson = followPerson;
	}
}