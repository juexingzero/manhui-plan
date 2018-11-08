package com.manhui.model;

import java.util.List;

/**
 * @ClassName: Offices
 * @description: TODO
 * @author:	HeJiayan
 * @date Create in 下午4:02:20 2017年11月2日
 * @version: v1.0.0
 * @modify By:
 * @Copyright: 版权由满惠科技拥有
 */
public class Office {
	private String id;                  //部门编码

    private String name;                //部门名称

    private String grade;               //部门级数

    private String parentId;            //父级部门编码

    private String parentIds;           //所有父级部门编码
    
    private List<Office> subOffices;    //子部门
    
    private List<User> empList;         //该部门下的员工

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade == null ? null : grade.trim();
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId == null ? null : parentId.trim();
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds == null ? null : parentIds.trim();
	}

	public List<Office> getSubOffices() {
		return subOffices;
	}

	public void setSubOffices(List<Office> subOffices) {
		this.subOffices = subOffices;
	}

	public List<User> getEmpList() {
		return empList;
	}

	public void setEmpList(List<User> empList) {
		this.empList = empList;
	}
}
