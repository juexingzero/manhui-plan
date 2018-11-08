package com.manhui.model.assessment;

import java.util.Date;

/**
 * @ClassName: Assessment
 * @description: 考核信息实体类
 * @author:	HeJiayan
 * @date Create in 下午11:26:43 2017年12月09日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Assessment {
    private Integer assId;                 //考核编号

    private Integer coopId;                //关联协作编号

    private Integer planId;                //关联计划编号

    private Integer assType;               //考核类型           1.协作未处理          2.计划未按期汇报          3.计划未按期开始      4.计划未按期结束

    private Date assTime;                  //考核时间
    
    private String assContent;             //考核关联内容（计划名称/协作主题）

    private String personId;               //被考核人编号

    private String assedPerson;            //被考核人

    private String personCompany;          //被考核人公司

    private String personOffice;           //被考核人部门

    private Double assAmount;              //考核金额

    private String complainant;            //申诉人（被考核人）

    private Date complainTime;             //申诉时间

    private String complainReason;         //申诉原因
    
    private String rebutPerson;            //驳回人
    
    private Date rebutTime;                //驳回时间
    
    private String rebutReason;            //驳回原因
    
    private String revokePerson;           //撤销人
    
    private Date revokeTime;               //撤销时间
    
    private String revokeReason;           //撤销原因

    private Date createTime;               //考核生成时间

    private Integer status;                //状态                  1.申诉待处理        2.申诉已驳回       3.未计入薪酬          4.已计入薪酬           5.考核已撤销

    public Integer getAssId() {
        return assId;
    }

    public void setAssId(Integer assId) {
        this.assId = assId;
    }

    public Integer getCoopId() {
        return coopId;
    }

    public void setCoopId(Integer coopId) {
        this.coopId = coopId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getAssType() {
        return assType;
    }

    public void setAssType(Integer assType) {
        this.assType = assType;
    }

    public Date getAssTime() {
        return assTime;
    }

    public void setAssTime(Date assTime) {
        this.assTime = assTime;
    }

	public String getAssContent() {
		return assContent;
	}

	public void setAssContent(String assContent) {
		this.assContent = assContent == null ? null : assContent.trim();
	}

	public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId == null ? null : personId.trim();
    }

    public String getAssedPerson() {
        return assedPerson;
    }

    public void setAssedPerson(String assedPerson) {
        this.assedPerson = assedPerson == null ? null : assedPerson.trim();
    }

    public String getPersonCompany() {
        return personCompany;
    }

    public void setPersonCompany(String personCompany) {
        this.personCompany = personCompany == null ? null : personCompany.trim();
    }

    public String getPersonOffice() {
        return personOffice;
    }

    public void setPersonOffice(String personOffice) {
        this.personOffice = personOffice == null ? null : personOffice.trim();
    }

    public Double getAssAmount() {
        return assAmount;
    }

    public void setAssAmount(Double assAmount) {
        this.assAmount = assAmount;
    }

    public String getComplainant() {
        return complainant;
    }

    public void setComplainant(String complainant) {
        this.complainant = complainant == null ? null : complainant.trim();
    }

    public Date getComplainTime() {
        return complainTime;
    }

    public void setComplainTime(Date complainTime) {
        this.complainTime = complainTime;
    }

    public String getComplainReason() {
        return complainReason;
    }

    public void setComplainReason(String complainReason) {
        this.complainReason = complainReason == null ? null : complainReason.trim();
    }

	public String getRebutPerson() {
		return rebutPerson;
	}

	public void setRebutPerson(String rebutPerson) {
		this.rebutPerson = rebutPerson == null ? null : rebutPerson.trim();
	}

	public Date getRebutTime() {
		return rebutTime;
	}

	public void setRebutTime(Date rebutTime) {
		this.rebutTime = rebutTime;
	}

	public String getRebutReason() {
		return rebutReason;
	}

	public void setRebutReason(String rebutReason) {
		this.rebutReason = rebutReason == null ? null : rebutReason.trim();
	}

	public String getRevokePerson() {
		return revokePerson;
	}

	public void setRevokePerson(String revokePerson) {
		this.revokePerson = revokePerson == null ? null : revokePerson.trim();
	}

	public Date getRevokeTime() {
		return revokeTime;
	}

	public void setRevokeTime(Date revokeTime) {
		this.revokeTime = revokeTime;
	}

	public String getRevokeReason() {
		return revokeReason;
	}

	public void setRevokeReason(String revokeReason) {
		this.revokeReason = revokeReason == null ? null : revokeReason.trim();
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
}