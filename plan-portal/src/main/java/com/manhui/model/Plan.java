package com.manhui.model;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: Plan
 * @description: 计划信息实体类
 * @author:	HeJiayan
 * @date Create in 下午4:30:13 2017年10月6日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */

public class Plan extends BaseObject{
    private Integer planId;                  //计划编号

    private String planName;                 //计划名称

    private String planDescribe;             //计划描述

    private Integer planType;                //计划类型         1、项目类    2、日常类     3、临时类

    private String blameOrganization;        //负责机构
    
    private String blameSection;             //负责部门

    private String blamePerson;              //负责人
    
    private Integer specialType;             //特殊类型         1、合同类计划   2、房地产交房类计划
    
    private Date dateRequire;                //时间要求 （合同到期时间/预定交房时间）       

    private String startTime;                //计划开始时间       (日常类：开始时间的小时数/周数/月数)
    
    private String startTimeDaily;           //计划开始时间       (日常类：开始时间的分钟数/周几/第几天)

    private String endTime;                  //计划结束时间        (日常类：结束时间的小时数/周数/月数)
    
    private String endTimeDaily;             //计划结束时间        (日常类：结束时间的分钟数/周几/第几天)

    private Integer importance;              //重要程度            1、重要     2、一般    3、微型
    
    private Date pdDeadline;                 //计划分解截止时间
    
    private Date papDeadline;                //计划审批截止时间
    
    private Date ndDeadline;                 //节点分解截止时间
    
    private Date napDeadline;                //节点审批截止时间

    private String customer;                 //客户名称

    private String auditProcess;             //审核流程（name）

    private String allowHolidays;            //允许假期            多个假期用英文","分隔

    private String accreditPerson;           //授权查看人 （id）         多个查看人用英文","分隔

    private Integer loopMode;                //循环方式          1、按天      2、按周        3、按月

    private Integer loopRate;                //循环频率

    private Integer parentId;                //父计划编号

    private Integer planOrder;               //计划执行排序

    private String finishStandard;           //完成标准

    private String checkItem;                //考核项                  1、未按时完成     2、未按标准完成

    private String checkManner;              //考核方式             1、算入绩效          2、单次考核          3、每天考核          4、算入绩效+单次考核         5、算入绩效+每天考核

    private Integer penaltyAmount;           //罚款金额
    
    private Integer timeLimit;               //工期（天）
    
    private Integer timeOffset;              //时间偏移量（只有生成模板后，模板计划才会有时间偏移量，普通计划没有时间偏移量）
    
    private String createPerson;             //创建人

    private Date createTime;                 //创建时间
    
    private Integer status;                  //状态          0.草稿、1.被驳回、2.待审批、3.审批通过/未启动、4.执行中、5.已完成、6.已终止、7.已暂停、8.调整待审批、9.待验收、10.验证未通过 11、待调整 12.子计划审批通过单主计划没有审批通过的状态(如有修改请上传git)
    
    private Integer decomStatus;             //计划分解状态           1.草稿（部分分解）     2.本级已分解，待下级分解       3.待分解        
    
    private String errorMsg;                 //模板导入子计划时用于存储错误信息 （不会存入数据库）
    
    private List<Plan> subPlans;             //子计划
    
    private Integer subSum;                  //子计划数量
    
    private Reject reject;					 //关联的驳回对象
    
    private Integer nodeStatus;				//节点状态		1:带分解	2:草稿   3：待审批  4：已驳回  5：通过
    
    private String supervisorId;            //监察人编号
    
    private String supervisor;              //监察人
    
    private String supervisorCompany;       //监察人公司
    
    private String supervisorOffice;        //监察人部门
    
    private Integer assessNum;              //考核次数
    
    private Integer adjustStatus;		    //调整类型	1:计划延期    2：计划暂停	3计划中止    4：计划变更	5：计划已延期	
    
    private List<Node> subNodes;			//该计划下的节点
    
    private List<PlanAssess> pass;	        //该计划的考核信息
    
    private String validateMode;            //验证方式            1.上传凭证     2.系统提取
    
    private String extractItem;             //提取项名称
    
    public Integer getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(Integer nodeStatus) {
		this.nodeStatus = nodeStatus;
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

    public String getPlanDescribe() {
        return planDescribe;
    }

    public void setPlanDescribe(String planDescribe) {
        this.planDescribe = planDescribe == null ? null : planDescribe.trim();
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }
    
	public String getBlameOrganization() {
		return blameOrganization;
	}

	public void setBlameOrganization(String blameOrganization) {
		this.blameOrganization = blameOrganization == null ? null : blameOrganization.trim();
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
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

	public String getStartTimeDaily() {
		return startTimeDaily;
	}

	public void setStartTimeDaily(String startTimeDaily) {
		this.startTimeDaily = startTimeDaily;
	}

	public String getEndTimeDaily() {
		return endTimeDaily;
	}

	public void setEndTimeDaily(String endTimeDaily) {
		this.endTimeDaily = endTimeDaily;
	}

	public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
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

	public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer == null ? null : customer.trim();
    }

    public String getAuditProcess() {
        return auditProcess;
    }

    public void setAuditProcess(String auditProcess) {
        this.auditProcess = auditProcess == null ? null : auditProcess.trim();
    }

    public String getAllowHolidays() {
        return allowHolidays;
    }

    public void setAllowHolidays(String allowHolidays) {
        this.allowHolidays = allowHolidays == null ? null : allowHolidays.trim();
    }

    public String getAccreditPerson() {
        return accreditPerson;
    }

    public void setAccreditPerson(String accreditPerson) {
        this.accreditPerson = accreditPerson == null ? null : accreditPerson.trim();
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
        this.finishStandard = finishStandard == null ? null : finishStandard.trim();
    }

    public String getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(String checkItem) {
        this.checkItem = checkItem == null ? null : checkItem.trim();
    }

    public String getCheckManner() {
        return checkManner;
    }

    public void setCheckManner(String checkManner) {
        this.checkManner = checkManner == null ? null : checkManner.trim();
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
        this.createPerson = createPerson == null ? null : createPerson.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public List<Plan> getSubPlans() {
		return subPlans;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg == null ? null : errorMsg.trim();
	}

	public void setSubPlans(List<Plan> subPlans) {
		this.subPlans = subPlans;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Integer getTimeOffset() {
		return timeOffset;
	}

	public void setTimeOffset(Integer timeOffset) {
		this.timeOffset = timeOffset;
	}

	public Integer getDecomStatus() {
		return decomStatus;
	}

	public void setDecomStatus(Integer decomStatus) {
		this.decomStatus = decomStatus;
	}

	public Integer getSubSum() {
		return subSum;
	}

	public void setSubSum(Integer subSum) {
		this.subSum = subSum;
	}

	public Reject getReject() {
		return reject;
	}

	public void setReject(Reject reject) {
		this.reject = reject;
	}

	public String getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId == null ? null : supervisorId.trim();
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor == null ? null : supervisor.trim();
	}

	public String getSupervisorCompany() {
		return supervisorCompany;
	}

	public void setSupervisorCompany(String supervisorCompany) {
		this.supervisorCompany = supervisorCompany == null ? null : supervisorCompany.trim();
	}

	public String getSupervisorOffice() {
		return supervisorOffice;
	}

	public void setSupervisorOffice(String supervisorOffice) {
		this.supervisorOffice = supervisorOffice == null ? null : supervisorOffice.trim();
	}

	public Integer getAssessNum() {
		return assessNum;
	}

	public void setAssessNum(Integer assessNum) {
		this.assessNum = assessNum;
	}
	
	public Integer getAdjustStatus() {
		return adjustStatus;
	}
	
	public void setAdjustStatus(Integer adjustStatus) {
		this.adjustStatus = adjustStatus;
	}

	public List<Node> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<Node> subNodes) {
		this.subNodes = subNodes;
	}

	public List<PlanAssess> getPass() {
		return pass;
	}

	public void setPass(List<PlanAssess> pass) {
		this.pass = pass;
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