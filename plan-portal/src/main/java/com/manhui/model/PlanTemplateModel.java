package com.manhui.model;

import java.math.BigDecimal;
import java.util.Date;

public class PlanTemplateModel {
	private Integer templateId;    //模板编码

    private String templateName;   //模板名称

    private Integer typeId;        //模板类型
    
    private Integer planType;	   //计划类型

    private String createName;     //创建人名称

    private Date createDate;       //创建时间
 
    private Integer state;         //状态
    
    private String templateExplain; //模板说明
    
    private Integer planId;        //计划id
    
    private String planName;                 //计划名称

    private String planDescribe;             //计划描述
    
    private String blameOrganization;        //负责机构

    private String blameSection;             //负责部门

    private String blamePerson;              //负责人
    
    private Integer specialType;             //特殊类型         1、合同类计划   2、房地产交房类计划
    
    private Date dateRequire;              	 //时间要求 （合同到期时间/预定交房时间）       

    private String startTime;                //计划开始时间

    private String endTime;                  //计划结束时间

    private Integer importance;               //重要程度

    private String customer;                 //客户名称

    private String auditProcess;            //审核流程（name）

    private String allowHolidays;            //允许假期            多个假期用英文","分隔

    private String accreditPerson;           //授权查看人 （id）         多个查看人用英文","分隔
    
    private Date pdDeadline;                 //计划分解截止时间
    
    private Date papDeadline;                //计划审批截止时间
    
    private Date ndDeadline;                 //节点分解截止时间
    
    private Date napDeadline;                //节点审批截止时间

	private Integer loopMode;                //循环方式

    private Integer loopRate;                //循环频率

    private Integer parentId;                //父计划编号

    private Integer planOrder;               //计划执行排序

    private String finishStandard;           //完成标准

    private String checkItem;                //考核项

    private Integer checkManner;             //考核方式

    private Integer penaltyAmount;           //罚款金额

    private String createPerson;             //创建人

    private Date createTime;                 //创建时间
    
    private Integer timeLimit;				 //工作工期

    private Integer status;                  //状态

    private BigDecimal cumulativeDays;		//按天累加天数

    private BigDecimal cumulativeAmount;	//按天累加考核金额
    
    private Integer checkItems;				//考核类型			1.计划未按期开始 2. 计划未按期完成

    private Integer checkManners;			//考核方法			1.单次		2.按天累加	

    private BigDecimal singleAmount;		//单次考核金额
    
    private BigDecimal singleAmounts;		//单次考核金额

    private BigDecimal cumulativeDayss;		//按天累加天数

    private BigDecimal cumulativeAmounts;	//按天累加考核金额
    
    private String validateMode;            //验证方式            1.上传凭证     2.系统提取
    
    private String extractItem;             //提取项名称


	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getPlanType() {
		return planType;
	}

	public void setPlanType(Integer planType) {
		this.planType = planType;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getTemplateExplain() {
		return templateExplain;
	}

	public void setTemplateExplain(String templateExplain) {
		this.templateExplain = templateExplain;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanDescribe() {
		return planDescribe;
	}

	public void setPlanDescribe(String planDescribe) {
		this.planDescribe = planDescribe;
	}
	

	public String getBlameOrganization() {
		return blameOrganization;
	}

	public void setBlameOrganization(String blameOrganization) {
		this.blameOrganization = blameOrganization;
	}

	public String getBlameSection() {
		return blameSection;
	}

	public void setBlameSection(String blameSection) {
		this.blameSection = blameSection;
	}

	public String getBlamePerson() {
		return blamePerson;
	}

	public void setBlamePerson(String blamePerson) {
		this.blamePerson = blamePerson;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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

	public Integer getCheckManner() {
		return checkManner;
	}

	public void setCheckManner(Integer checkManner) {
		this.checkManner = checkManner;
	}

	public Integer getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Integer penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
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

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getPdDeadline() {
		return pdDeadline;
	}

	public void setPdDeadline(Date pdDeadline) {
		this.pdDeadline = pdDeadline;
	}

	public Date getPapDeadline() {
		return papDeadline;
	}

	public void setPapDeadline(Date papDeadline) {
		this.papDeadline = papDeadline;
	}

	public Date getNdDeadline() {
		return ndDeadline;
	}

	public void setNdDeadline(Date ndDeadline) {
		this.ndDeadline = ndDeadline;
	}

	public Date getNapDeadline() {
		return napDeadline;
	}

	public void setNapDeadline(Date napDeadline) {
		this.napDeadline = napDeadline;
	}

	public BigDecimal getCumulativeDays() {
		return cumulativeDays;
	}

	public void setCumulativeDays(BigDecimal cumulativeDays) {
		this.cumulativeDays = cumulativeDays;
	}

	public BigDecimal getCumulativeAmount() {
		return cumulativeAmount;
	}

	public void setCumulativeAmount(BigDecimal cumulativeAmount) {
		this.cumulativeAmount = cumulativeAmount;
	}

	public Integer getCheckItems() {
		return checkItems;
	}

	public void setCheckItems(Integer checkItems) {
		this.checkItems = checkItems;
	}

	public Integer getCheckManners() {
		return checkManners;
	}

	public void setCheckManners(Integer checkManners) {
		this.checkManners = checkManners;
	}

	public BigDecimal getSingleAmount() {
		return singleAmount;
	}

	public void setSingleAmount(BigDecimal singleAmount) {
		this.singleAmount = singleAmount;
	}

	public BigDecimal getSingleAmounts() {
		return singleAmounts;
	}

	public void setSingleAmounts(BigDecimal singleAmounts) {
		this.singleAmounts = singleAmounts;
	}

	public BigDecimal getCumulativeDayss() {
		return cumulativeDayss;
	}

	public void setCumulativeDayss(BigDecimal cumulativeDayss) {
		this.cumulativeDayss = cumulativeDayss;
	}

	public BigDecimal getCumulativeAmounts() {
		return cumulativeAmounts;
	}

	public void setCumulativeAmounts(BigDecimal cumulativeAmounts) {
		this.cumulativeAmounts = cumulativeAmounts;
	}

	public String getValidateMode() {
		return validateMode;
	}

	public void setValidateMode(String validateMode) {
		this.validateMode = validateMode;
	}

	public String getExtractItem() {
		return extractItem;
	}

	public void setExtractItem(String extractItem) {
		this.extractItem = extractItem;
	}
    
}
