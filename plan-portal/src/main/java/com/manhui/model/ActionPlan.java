package com.manhui.model;

import java.util.Date;
import java.util.List;


/**
 * @ClassName: ActionPlan
 * @description: 执行计划信息实体类
 * @author:	HeJiayan
 * @date Create in 下午8:40:13 2017年10月24日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class ActionPlan extends BaseObject{
    private Integer actionPlanId;            //执行计划ID
    
    private Integer planId;					//生成执行计划的编制计划的id			
 
    private String actionPlanName;           //执行计划名称

    private Integer actionPlanType;          //执行计划类型    1-项目类计划 2-日常类计划 3-临时类计划
    
    private String planDescribe;			 //计划描述

    private String blameOrgminzation;        //负责机构

    private String blameSection;             //负责部门

    private String blamePerson;              //负责人

    private String startTime;                //计划既定开始时间

    private String endTime;                  //计划既定结束时间

    private String startUpTime;              //实际启动时间

    private String complateTime;             //实际完成时间
    
    private Integer specialType;             //特殊类型         1、合同类计划   2、房地产交房类计划
    
    private Date dateRequire;                //时间要求 （合同到期时间/预定交房时间）       
    
    private Integer importance;              //重要程度            1、重要     2、一般    3、微型

    private String customer;                 //客户名称

    private String auditProcess;            //审核流程（name）

    private String allowHolidays;            //允许假期            多个假期用英文","分隔

    private String accreditPerson;           //授权查看人 （id）         多个查看人用英文","分隔

    private Integer loopMode;                //循环方式

    private Integer loopRate;                //循环频率

    private Integer actionParentId;          //执行计划父计划ID
    
    private Integer planOrder;               //计划执行排序

    private String finishStandard;           //完成标准

    private String checkItem;                //考核项

    private String checkManner;             //考核方式

    private Integer penaltyAmount;           //罚款金额
    
    private Integer timeLimit;               //工期（天）
    
    private String createPerson;             //创建人

    private Date createTime;                 //创建时间

    private String resultDescribe;           //结果描述

    private Integer adjustType;              //调整类型  1-计划延期 2-计划暂停 3-计划终止 4-添加子计划 5-主计划变更 6-子计划变更

    private String adjustReason;             //调整原因

    private String postponeTime;             //延期时间

    private String postponeTimeLimit;        //延期时间工期
    
    private Integer state;                   //状态 1-未启动 2-执行中 3-已完成  4-调整待审批  5-已暂停 6-已中止 7-已延期 8-待验收 9-验收未通过 
    
	private Integer isAdvance;        //是否提前完成
	
	private Integer isDelay;          //是否延期
	
	private Integer isAllComplate;    //是否全部完成
	
	private Integer isReminders;      //是否被催办
    
    private Integer subSum;           //子计划数量
    
    private List<ActionPlan> subPlanList;   //子计划
    
    
    
    
    public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public String getPlanDescribe() {
		return planDescribe;
	}

	public void setPlanDescribe(String planDescribe) {
		this.planDescribe = planDescribe;
	}

    public Integer getActionPlanId() {
        return actionPlanId;
    }

    public void setActionPlanId(Integer actionPlanId) {
        this.actionPlanId = actionPlanId;
    }

    public String getActionPlanName() {
        return actionPlanName;
    }

    public void setActionPlanName(String actionPlanName) {
        this.actionPlanName = actionPlanName == null ? null : actionPlanName.trim();
    }

    public Integer getActionPlanType() {
        return actionPlanType;
    }

    public void setActionPlanType(Integer actionPlanType) {
        this.actionPlanType = actionPlanType;
    }

    public String getBlameOrgminzation() {
        return blameOrgminzation;
    }

    public void setBlameOrgminzation(String blameOrgminzation) {
        this.blameOrgminzation = blameOrgminzation == null ? null : blameOrgminzation.trim();
    }

    public String getBlameSection() {
        return blameSection;
    }

    public void setBlameSection(String blameSection) {
        this.blameSection = blameSection == null ? null : blameSection.trim();
    }

    public String getBlamePerson() {
        return blamePerson;
    }

    public void setBlamePerson(String blamePerson) {
        this.blamePerson = blamePerson == null ? null : blamePerson.trim();
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

    public String getStartUpTime() {
        return startUpTime;
    }

    public void setStartUpTime(String startUpTime) {
        this.startUpTime = startUpTime == null ? null : startUpTime.trim();
    }

    public String getComplateTime() {
        return complateTime;
    }

    public void setComplateTime(String complateTime) {
        this.complateTime = complateTime == null ? null : complateTime.trim();
    }

    public Integer getActionParentId() {
        return actionParentId;
    }

    public void setActionParentId(Integer actionParentId) {
        this.actionParentId = actionParentId;
    }

    public String getResultDescribe() {
        return resultDescribe;
    }

    public void setResultDescribe(String resultDescribe) {
        this.resultDescribe = resultDescribe == null ? null : resultDescribe.trim();
    }

    public Integer getAdjustType() {
        return adjustType;
    }

    public void setAdjustType(Integer adjustType) {
        this.adjustType = adjustType;
    }

    public String getAdjustReason() {
        return adjustReason;
    }

    public void setAdjustReason(String adjustReason) {
        this.adjustReason = adjustReason == null ? null : adjustReason.trim();
    }

    public String getPostponeTime() {
        return postponeTime;
    }

    public void setPostponeTime(String postponeTime) {
        this.postponeTime = postponeTime == null ? null : postponeTime.trim();
    }

    public String getPostponeTimeLimit() {
        return postponeTimeLimit;
    }

    public void setPostponeTimeLimit(String postponeTimeLimit) {
        this.postponeTimeLimit = postponeTimeLimit == null ? null : postponeTimeLimit.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

	public Integer getImportance() {
		return importance;
	}

	public void setImportance(Integer importance) {
		this.importance = importance;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getAuditProcess() {
		return auditProcess;
	}

	public void setAuditProcess(String auditProcess) {
		this.auditProcess = auditProcess;
	}

	public String getAllowHolidays() {
		return allowHolidays;
	}

	public void setAllowHolidays(String allowHolidays) {
		this.allowHolidays = allowHolidays;
	}

	public String getAccreditPerson() {
		return accreditPerson;
	}

	public void setAccreditPerson(String accreditPerson) {
		this.accreditPerson = accreditPerson;
	}

	public Integer getLoopMode() {
		return loopMode;
	}

	public void setLoopMode(Integer loopMode) {
		this.loopMode = loopMode;
	}

	public Integer getLoopRate() {
		return loopRate;
	}

	public void setLoopRate(Integer loopRate) {
		this.loopRate = loopRate;
	}

	public Integer getPlanOrder() {
		return planOrder;
	}

	public void setPlanOrder(Integer planOrder) {
		this.planOrder = planOrder;
	}

	public String getFinishStandard() {
		return finishStandard;
	}

	public void setFinishStandard(String finishStandard) {
		this.finishStandard = finishStandard;
	}

	public String getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	public String getCheckManner() {
		return checkManner;
	}

	public void setCheckManner(String checkManner) {
		this.checkManner = checkManner;
	}

	public Integer getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Integer penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getIsAdvance() {
		return isAdvance;
	}

	public void setIsAdvance(Integer isAdvance) {
		this.isAdvance = isAdvance;
	}

	public Integer getIsDelay() {
		return isDelay;
	}

	public void setIsDelay(Integer isDelay) {
		this.isDelay = isDelay;
	}

	public Integer getIsAllComplate() {
		return isAllComplate;
	}

	public void setIsAllComplate(Integer isAllComplate) {
		this.isAllComplate = isAllComplate;
	}

	public Integer getSubSum() {
		return subSum;
	}

	public void setSubSum(Integer subSum) {
		this.subSum = subSum;
	}

	public List<ActionPlan> getSubPlanList() {
		return subPlanList;
	}

	public void setSubPlanList(List<ActionPlan> subPlanList) {
		this.subPlanList = subPlanList;
	}

	public Integer getSpecialType() {
		return specialType;
	}

	public void setSpecialType(Integer specialType) {
		this.specialType = specialType;
	}

	public Date getDateRequire() {
		return dateRequire;
	}

	public void setDateRequire(Date dateRequire) {
		this.dateRequire = dateRequire;
	}

	public Integer getIsReminders() {
		return isReminders;
	}

	public void setIsReminders(Integer isReminders) {
		this.isReminders = isReminders;
	}
}