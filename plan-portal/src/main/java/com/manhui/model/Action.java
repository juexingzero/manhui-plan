package com.manhui.model;

public class Action extends BaseObject{
    private Integer actionId;         //权限编码

    private String actionName;        //权限名称

    private String actionRemark;      //权限备注

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName == null ? null : actionName.trim();
    }

    public String getActionRemark() {
        return actionRemark;
    }

    public void setActionRemark(String actionRemark) {
        this.actionRemark = actionRemark == null ? null : actionRemark.trim();
    }
}