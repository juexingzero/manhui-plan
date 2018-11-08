package com.manhui.model.planCooperation;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: Cooperation
 * @description: 协作实体类
 * @author:	HeJiayan
 * @date Create in 下午2:12:43 2017年11月28日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Cooperation {
    private Integer coopId;            //协作编号
    
    private Integer apId;              //协作执行计划编号

    //协作信息
    private String theme;              //协作主题

    private String coopReason;         //协作原因

    private String criterion;          //协作完成标准

    private String applicantId;        //申请人编号
    
    private String applicant;          //申请人
    
    private String applicantCompany;   //申请人公司（机构）
    
    private String applicantOffice;    //申请人部门
    
    private Date applyTime;            //申请时间

    private String collaboratorId;     //协作人编号
    
    private String collaborator;       //协作人

    private String collaboratorCompany;//协作人公司/机构

    private String collaboratorOffice; //协作人部门
    
    private String collaboratorPost;   //协作人岗位

    private Date propFinishTime;       //建议完成时间

    //拒绝信息
    private String rejectId;           //拒绝人编号
    
    private String rejectPerson;       //拒绝人

    private Date rejectTime;           //拒绝时间

    private String rejectReason;       //拒绝原因

    //跳级信息
    private List<SkipInfo> skipInfos;  //跳级信息
    
    //跳级标识
    private String flag;               //相对于当前登录用户的跳级标识（判别是跳进还是跳出）       不做入库操作

    //指派信息
    private String assignorId;         //指派人编号
    
    private String assignor;           //指派人

    private Date finishTime;           //指派完成时间

    private String executorId;         //执行人编号
    
    private String executor;           //执行人

    private String executorCompany;    //执行人公司/机构

    private String executorOffice;     //执行人部门
    
    private Date actualFinishTime;     //实际完成时间

    //协作反馈信息
    private String remark;             //执行反馈备注

    private Integer status;            //状态             1.待处理      2.待处理（已跳级）     3.已拒绝(已驳回)   4.已接受         5.已超时         6.已完成
    
    public Integer getCoopId() {
        return coopId;
    }

    public void setCoopId(Integer coopId) {
        this.coopId = coopId;
    }

	public Integer getApId() {
		return apId;
	}

	public void setApId(Integer apId) {
		this.apId = apId;
	}

	public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme == null ? null : theme.trim();
    }

    public String getCoopReason() {
        return coopReason;
    }

    public void setCoopReason(String coopReason) {
        this.coopReason = coopReason == null ? null : coopReason.trim();
    }

    public String getCriterion() {
        return criterion;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion == null ? null : criterion.trim();
    }

	public String getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId == null ? null : applicantId.trim();
	}

	public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant == null ? null : applicant.trim();
    }

	public String getApplicantCompany() {
		return applicantCompany;
	}

	public void setApplicantCompany(String applicantCompany) {
		this.applicantCompany = applicantCompany == null ? null : applicantCompany.trim();
	}

	public String getApplicantOffice() {
		return applicantOffice;
	}

	public void setApplicantOffice(String applicantOffice) {
		this.applicantOffice = applicantOffice == null ? null : applicantOffice.trim();
	}

	public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

	public String getCollaboratorId() {
		return collaboratorId;
	}

	public void setCollaboratorId(String collaboratorId) {
		this.collaboratorId = collaboratorId == null ? null : collaboratorId.trim();
	}

	public String getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(String collaborator) {
        this.collaborator = collaborator == null ? null : collaborator.trim();
    }

    public String getCollaboratorCompany() {
        return collaboratorCompany;
    }

    public void setCollaboratorCompany(String collaboratorCompany) {
        this.collaboratorCompany = collaboratorCompany == null ? null : collaboratorCompany.trim();
    }

    public String getCollaboratorOffice() {
        return collaboratorOffice;
    }

    public void setCollaboratorOffice(String collaboratorOffice) {
        this.collaboratorOffice = collaboratorOffice == null ? null : collaboratorOffice.trim();
    }

    public String getCollaboratorPost() {
        return collaboratorPost;
    }

    public void setCollaboratorPost(String collaboratorPost) {
        this.collaboratorPost = collaboratorPost == null ? null : collaboratorPost.trim();
    }

    public Date getPropFinishTime() {
        return propFinishTime;
    }

    public void setPropFinishTime(Date propFinishTime) {
        this.propFinishTime = propFinishTime;
    }

	public String getRejectId() {
		return rejectId;
	}

	public void setRejectId(String rejectId) {
		this.rejectId = rejectId == null ? null : rejectId.trim();
	}

	public String getRejectPerson() {
        return rejectPerson;
    }

    public void setRejectPerson(String rejectPerson) {
        this.rejectPerson = rejectPerson == null ? null : rejectPerson.trim();
    }

    public Date getRejectTime() {
        return rejectTime;
    }

    public void setRejectTime(Date rejectTime) {
        this.rejectTime = rejectTime;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason == null ? null : rejectReason.trim();
    }

	public List<SkipInfo> getSkipInfos() {
		return skipInfos;
	}

	public void setSkipInfos(List<SkipInfo> skipInfos) {
		this.skipInfos = skipInfos;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag == null ? null : flag.trim();
	}

	public String getAssignorId() {
		return assignorId;
	}

	public void setAssignorId(String assignorId) {
		this.assignorId = assignorId == null ? null : assignorId.trim();
	}

	public String getAssignor() {
        return assignor;
    }

    public void setAssignor(String assignor) {
        this.assignor = assignor == null ? null : assignor.trim();
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

	public String getExecutorId() {
		return executorId;
	}

	public void setExecutorId(String executorId) {
		this.executorId = executorId == null ? null : executorId.trim();
	}

	public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor == null ? null : executor.trim();
    }

    public String getExecutorCompany() {
        return executorCompany;
    }

    public void setExecutorCompany(String executorCompany) {
        this.executorCompany = executorCompany == null ? null : executorCompany.trim();
    }

    public String getExecutorOffice() {
        return executorOffice;
    }

    public void setExecutorOffice(String executorOffice) {
        this.executorOffice = executorOffice == null ? null : executorOffice.trim();
    }

	public Date getActualFinishTime() {
		return actualFinishTime;
	}

	public void setActualFinishTime(Date actualFinishTime) {
		this.actualFinishTime = actualFinishTime;
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
}