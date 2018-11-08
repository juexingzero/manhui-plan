package com.manhui.model;

import java.util.List;

/**
 * 
 * @author Administrator
 *
 */
public class PlanAssessList {
	
	private List<PlanAssess> planAssesses;	//用来接收页面传过来的多个考核对象

	public List<PlanAssess> getPlanAssesses() {
		return planAssesses;
	}

	public void setPlanAssesses(List<PlanAssess> planAssesses) {
		this.planAssesses = planAssesses;
	}
	
	
}
