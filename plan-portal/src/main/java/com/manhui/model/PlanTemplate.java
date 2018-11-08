package com.manhui.model;

import java.util.Date;

public class PlanTemplate extends BaseObject{
    private Integer templateId;    //模板编码

    private String templateName;   //模板名称

    private Integer typeId;        //模板类型
    
    private Integer planType;	   //计划类型

	private Integer planId;        //计划id

    private String createName;     //创建人名称

    private Date createDate;       //创建时间
 
    private Integer state;         //状态
    
    private String templateExplain; //模板说明
    
    private String typeName;       //新增类型名称
    
    private String planTypeName;   //新增计划名称

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
        this.templateName = templateName == null ? null : templateName.trim();
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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
        this.createName = createName == null ? null : createName.trim();
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

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	

	public String getPlanTypeName() {
		return planTypeName;
	}

	public void setPlanTypeName(String planTypeName) {
		this.planTypeName = planTypeName;
	}

	public String getTemplateExplain() {
		return templateExplain;
	}

	public void setTemplateExplain(String templateExplain) {
		this.templateExplain = templateExplain;
	}
    
    public Integer getPlanType() {
		return planType;
	}

	public void setPlanType(Integer planType) {
		this.planType = planType;
	}
}