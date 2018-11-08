package com.manhui.model;

import java.util.List;

/**
 * @ClassName: OrganizationSupervisor
 * @description: 机构公司及对应监察人信息实体类
 * @author:	HeJiayan
 * @date Create in 下午21:11:43 2017年12月11日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class OrganizationSupervisor {
    private String id;                        //机构编码

    private String parentId;                  //父级机构编码

    private String name;                      //机构名称

    private String fullName;                  //机构全称

    private String supervisorId;              //监察负责人ID

    private String supervisor;                //监察负责人

    private String supervisorCompany;         //监察负责人公司

    private String supervisorOffice;          //监察负责人部门
    
    private List<OrganizationSupervisor> subos; //子机构

    private Integer status;                   //状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId == null ? null : parentId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
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

	public List<OrganizationSupervisor> getSubos() {
		return subos;
	}

	public void setSubos(List<OrganizationSupervisor> subos) {
		this.subos = subos;
	}

	public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}