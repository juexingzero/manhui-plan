package com.manhui.model;

import java.util.Date;

public class TemplateType extends BaseObject{
    private Integer ttId;              //类型编号

    private String typeName;           //类型名称

    private String typeDescription;    //类型描述

    private String createName;         //创建人名称

    private Date createDate;           //创建时间

    private Integer state;             //状态

    public Integer getTtId() {
        return ttId;
    }

    public void setTtId(Integer ttId) {
        this.ttId = ttId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription == null ? null : typeDescription.trim();
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
}